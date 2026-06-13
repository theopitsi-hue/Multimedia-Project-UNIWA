package org.theopitsi.multimedia.client.stream;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

public class FfmpegStreamReceiver {

    private final String host;
    private final int port;

    private Process process;

    public FfmpegStreamReceiver(String host, int port) {
        this.host = host;
        this.port = port;
    }

    //WHY does this only work when i close the client?!
    //is it hanging? do i need to thread it?
    //wtf
    public void beginCapturingStream(String protocol) {
        stop();

        try {
            ProcessBuilder pb = new ProcessBuilder(buildCommand(protocol.toLowerCase()));
            pb.redirectErrorStream(true);

            process = pb.start();

            new Thread(() -> {
                try (var reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException ignored) {}
            }).start();

        } catch (IOException e) {
            throw new RuntimeException("Failed to start stream receiver", e);
        }
    }

    private String[] buildCommand(String protocol) {

        String url;

        switch (protocol.toLowerCase()) {

            case "tcp":
                url = "tcp://" + host + ":" + port;
                return new String[]{
                        "ffplay",
                        "-fflags", "nobuffer",
                        "-flags", "low_delay",
                        url
                };

            case "udp":
                url = "udp://" + host + ":" + port;
                return new String[]{
                        "ffplay",
                        "-fflags", "nobuffer",
                        "-flags", "low_delay",
                        url
                };

            case "rtp":
                url = "rtp://" + host + ":" + port;
                return new String[]{
                        "ffplay",
                        "-protocol_whitelist", "file,udp,rtp",
                        Paths.get(
                                System.getProperty("user.home"),
                                "Documents",
                                "VideoPlayer",
                                "sdp"
                        ).toString()+"\\"+"stream.sdp"
                };

            default:
                throw new IllegalArgumentException("Unsupported protocol: " + protocol);
        }
    }

    public void stop() {
        if (process != null) {
            process.destroy();

            try {
                if (!process.waitFor(2, java.util.concurrent.TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                }
            } catch (InterruptedException ignored) {
                process.destroyForcibly();
                Thread.currentThread().interrupt();
            }

            process = null;
        }
    }
}
