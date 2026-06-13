package org.theopitsi.multimedia.server.stream;

import org.theopitsi.multimedia.common.data.VideoData;
import org.theopitsi.multimedia.server.MMServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FFmpegStreamManager {

    private static class ProcessHandle {
        final Process process;
        final Thread ioThread;

        ProcessHandle(Process process, Thread ioThread) {
            this.process = process;
            this.ioThread = ioThread;
        }
    }

    private static final Map<Integer, ProcessHandle> activeClientProcesses = new ConcurrentHashMap<>();

    private final String host;
    private final int port;

    public FFmpegStreamManager(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void streamForClient(int clientId, VideoData selected, String protocol) {
        stopClient(clientId);

        String input = MMServer.contentManager.getVideoFile(selected).getAbsolutePath();

        try {
            ProcessBuilder pb = new ProcessBuilder(buildCommand(input, protocol));
            pb.redirectErrorStream(true);

            Process process = pb.start();

            Thread ioThread = createLoggingThread(clientId, process);
            ioThread.start();

            activeClientProcesses.put(clientId, new ProcessHandle(process, ioThread));

        } catch (IOException e) {
            throw new RuntimeException("Failed to start stream for client " + clientId, e);
        }
    }

    private Thread createLoggingThread(int clientId, Process process) {
        Thread t = new Thread(() -> {
            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(process.getInputStream()))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[ffmpeg-client-" + clientId + "] " + line);
                }

            } catch (IOException ignored) {
            }
        });

        t.setName("ffmpeg-client-" + clientId + "-io");
        t.setDaemon(true);
        return t;
    }

    private String[] buildCommand(String input, String protocol) throws IOException {

        String target;

        return switch (protocol.toLowerCase()) {

            case "tcp" -> {
                target = "tcp://" + host + ":" + port+ "?listen=1";
                yield baseFFmpeg(input, target, "mpegts");
            }

            case "udp" -> {
                target = "udp://" + host + ":" + port + "?pkt_size=1316";
                yield baseFFmpeg(input, target, "mpegts");
            }

            case "rtp" -> {
                String dir = Paths.get(
                        System.getProperty("user.home"),
                        "Documents",
                        "VideoPlayer",
                        "sdp"
                ).toString();

                Files.createDirectories(Paths.get(dir));

                String sdpPath = Paths.get(dir, "stream.sdp").toString();
                target = "rtp://" + host + ":" + port;

                yield new String[]{
                        "ffmpeg",
                        "-re",
                        "-stream_loop", "-1",
                        "-i", input,

                        "-c:v", "libx264",
                        "-preset", "veryfast",
                        "-tune", "zerolatency",

                        "-an",
                        "-g", "30",
                        "-keyint_min", "30",
                        "-sc_threshold", "0",

                        "-f", "rtp",
                        "-sdp_file", sdpPath,
                        target
                };
            }

            default -> throw new IllegalArgumentException("Unsupported protocol: " + protocol);
        };
    }

    private String[] baseFFmpeg(String input, String target, String format) {
        return new String[]{
                "ffmpeg",

                "-re",
                "-stream_loop", "-1",
                "-i", input,

                "-fflags", "nobuffer",
                "-flags", "low_delay",

                "-c:v", "libx264",
                "-preset", "veryfast",
                "-tune", "zerolatency",
                "-g", "30",
                "-keyint_min", "30",
                "-sc_threshold", "0",

                "-f", format,
                target
        };
    }

    public void stopClient(int clientId) {
        ProcessHandle handle = activeClientProcesses.remove(clientId);

        if (handle == null) return;

        Process p = handle.process;

        if (p.isAlive()) {
            p.destroyForcibly();
        }

        try {
            handle.ioThread.join(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stopAll() {
        for (Integer clientId : activeClientProcesses.keySet()) {
            stopClient(clientId);
        }
    }
}