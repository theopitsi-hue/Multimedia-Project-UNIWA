package org.theopitsi.multimedia.server.stream;

import org.theopitsi.multimedia.common.data.VideoData;
import org.theopitsi.multimedia.server.MMServer;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FFmpegStreamManager {

    private static final Map<Integer, Process> activeClientProcesses = new ConcurrentHashMap<>();

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
            activeClientProcesses.put(clientId, process);

        } catch (IOException e) {
            throw new RuntimeException("Failed to start stream for client " + clientId, e);
        }
    }

    private String[] buildCommand(String input, String protocol) {

        String target;

        return switch (protocol.toLowerCase()) {

            case "tcp" -> {
                target = "tcp://" + host + ":" + port;
                yield baseFFmpeg(input, target, "mpegts");
            }

            case "udp" -> {
                target = "udp://" + host + ":" + port + "?pkt_size=1316";
                yield baseFFmpeg(input, target, "mpegts");
            }

            case "rtp" -> {
                target = "rtp://" + host + ":" + port;
                yield new String[]{
                        "ffmpeg",
                        "-re",
                        "-stream_loop", "-1",
                        "-i", input,

                        // FIX: stabilize encoding + decoder entry points
                        "-c:v", "libx264",
                        "-preset", "veryfast",
                        "-tune", "zerolatency",

                        "-an",
                        "-g", "30",
                        "-keyint_min", "30",
                        "-sc_threshold", "0",

                        "-f", "rtp",
                        target
                };
            }

            default -> throw new IllegalArgumentException("Unsupported protocol: " + protocol);
        };
    }

    /**
     * FIX: shared stable encoding profile for TCP/UDP
     */
    private String[] baseFFmpeg(String input, String target, String format) {
        return new String[]{
                "ffmpeg",

                // FIX: ensures real-time pacing without buffering spikes
                "-re",
                "-stream_loop", "-1",
                "-i", input,

                // FIX: prevents decode stalls & startup freeze
                "-fflags", "nobuffer",
                "-flags", "low_delay",

                // FIX: consistent GOP structure (critical for FFplay startup issues)
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
        Process p = activeClientProcesses.remove(clientId);

        if (p != null && p.isAlive()) {
            // FIX: force termination (destroy() alone often leaves FFmpeg hanging)
            p.destroyForcibly();
        }
    }

    public void stopAll() {
        for (Map.Entry<Integer, Process> entry : activeClientProcesses.entrySet()) {
            Process p = entry.getValue();

            if (p != null && p.isAlive()) {
                p.destroyForcibly();
            }
        }
        activeClientProcesses.clear();
    }
}