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

public class ContentStreamReceiver {
    private static final String videoDir = Paths.get(
            System.getProperty("user.home"),
            "Documents",
            "VideoPlayer",
            "client"
    ).toString()+"/";

    // STREAM SOCKET
    private Socket streamSocket;
    private DataOutputStream streamOut;
    private DataInputStream streamIn;
    private volatile int cId;

    public static VideoData watching = null;
    public static String protocol = null;
    public volatile static boolean mediaPlayerOpen = false;

    private void startPlayback() {
        if (mediaPlayerOpen) return;

        mediaPlayerOpen = true;
        File tempFile = new File(videoDir+"cache/"+watching.toFileName());

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
                    onMediaplayerClosed(mediaController)
            );

            mediaStage.setTitle("Mediaplayer");
            mediaStage.setScene(scene);
            mediaStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setVideoWatching(VideoData videoData, String protool) {
        watching = videoData;
        protocol = protool;

        if (watching != null) {
            MMClient.main.sendToServer(new VideoSelectPacket.Request(watching,protool));
            //uh, start ffmpeg reading here?
        }else{
            MMClient.main.sendToServer(new StreamStopPacket.Request());
        }
    }

    //WORKS, just needs some fixes with cache
    public void receiveStream(DataInputStream in, DataOutputStream out) {
        if (watching == null || protocol.equals("ProjectAuto")) return;

        File tempFile = new File(videoDir + "cache/"+ watching.toFileName());
        tempFile.getParentFile().mkdirs();

        try {
            if (tempFile.exists()) {
                return;
            }

            long size = in.readLong();
            MMClient.logger.warning("INCOMING file length: " + size + " bytes");

            boolean playbackStarted = false;
            long received = 0;

            try (FileOutputStream fos = new FileOutputStream(tempFile)) {

                while (true) {
                    int chunkSize = in.readInt();

                    if (chunkSize == -1) {
                        break;
                    }

                    byte[] chunk = new byte[chunkSize];

                    // Read exactly chunkSize bytes
                    in.readFully(chunk);

                    fos.write(chunk);

                    received += chunkSize;

                    MMClient.logger.info("received: " + received);

                    if (!playbackStarted && received > size * 0.05) {
                        playbackStarted = true;
                        Platform.runLater(this::startPlayback);
                    }
                }

                fos.flush();
            }

            MMClient.logger.info("Video stream complete");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //creates a socket for the client
    public void connect(int id, String addr, int controlPort) throws IOException{
        cId = id;
        //waits for connection
        streamSocket = new Socket(addr, controlPort);

        streamOut = new DataOutputStream(streamSocket.getOutputStream());
        streamIn = new DataInputStream(streamSocket.getInputStream());

        MMClient.logger.info("Connected streaming socket.");

        streamOut.writeInt(cId);
        streamOut.flush();

        //receive incoming stream data here
        while (!streamSocket.isClosed()){

                //read stream
                receiveStream(streamIn,streamOut);

        }
    }

    private void onMediaplayerClosed(MediaController mediaController) {
        mediaController.onWindowClosed();
        mediaPlayerOpen = false;

        MMClient.contentManager.setVideoWatching(null, null);

        clearCacheFolder();
    }

    public static void clearCacheFolder() {
        File cacheDir = new File(videoDir + "cache");

        if (!cacheDir.exists() || !cacheDir.isDirectory()) {
            return;
        }

        File[] files = cacheDir.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {
            deleteRecursively(file);
        }
    }

    private static void deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();

            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }

        file.delete();
    }

    public void disconnect() {
        try {
            if (streamIn != null) streamIn.close();
            if (streamOut != null) streamOut.close();
            if (streamSocket != null) streamSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
