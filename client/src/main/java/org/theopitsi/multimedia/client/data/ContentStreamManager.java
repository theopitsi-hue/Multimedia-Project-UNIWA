package org.theopitsi.multimedia.client.data;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.theopitsi.multimedia.client.MMClient;
import org.theopitsi.multimedia.client.gui.MediaController;
import org.theopitsi.multimedia.common.data.VideoData;
import org.theopitsi.multimedia.common.packet.StreamStopPacket;
import org.theopitsi.multimedia.common.packet.VideoSelectPacket;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;

public class ContentStreamManager {
    private static final String videoDir = Paths.get(
            System.getProperty("user.home"),
            "Documents",
            "VideoPlayer",
            "server"
    ).toString()+"/";


    public VideoData watching = null;

    public void startReceivingStream() {
        File tempFile = new File(videoDir+"cache/stream.mp4");
        tempFile.getParentFile().mkdirs();

        new Thread(() -> {
            try {

                Socket socket = new Socket("localhost", 5001);

                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());

                long size = in.readLong();

                FileOutputStream fos = new FileOutputStream(tempFile);

                byte[] buffer = new byte[8192];
                long received = 0;

                while (received < size) {

                    int read = in.read(buffer);
                    if (read == -1) break;

                    fos.write(buffer, 0, read);
                    received += read;

                    MMClient.logger.info("received:" + received);

                    // trigger playback when buffer is ready
                    if (received > size*0.05) { //5% buffer
                        Platform.runLater(this::startPlayback);
                    }
                }

                fos.close();
                socket.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startPlayback() {
        File tempFile = new File(videoDir+"cache/stream.mp4");

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/ui/mediaplayer.fxml")
            );

            Scene scene = new Scene(loader.load());

            MediaController mediaController = loader.getController();

            // Pass the file path
            mediaController.playMedia(String.valueOf(tempFile));

            Stage mediaStage = new Stage();

            mediaStage.setOnCloseRequest(event ->
                    mediaController.onWindowClosed()
            );

            mediaStage.setTitle("Mediaplayer");
            mediaStage.setScene(scene);
            mediaStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setVideo(VideoData videoData) {
        watching = videoData;

        if (watching != null) {
            MMClient.main.send(new VideoSelectPacket.Request(watching));
        }else{
            MMClient.main.send(new StreamStopPacket.Request());
        }
    }
}
