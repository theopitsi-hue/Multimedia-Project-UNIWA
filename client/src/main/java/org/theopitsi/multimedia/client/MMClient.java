package org.theopitsi.multimedia.client;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.theopitsi.multimedia.client.data.Client;
import org.theopitsi.multimedia.client.data.ContentStreamManager;
import org.theopitsi.multimedia.client.gui.ClientController;
import org.theopitsi.multimedia.common.data.ClientState;
import org.theopitsi.multimedia.common.packet.BandwidthPacket;

import java.io.IOException;
import java.util.logging.Logger;

public class MMClient extends Application {
    public static Logger logger = Logger.getLogger("MM-CLIENT");
    private static final int PORT = 5000;
    private static final int STREAM_PORT = 5001;

    public static ClientState state = ClientState.IDLE;

    public static SpeedTestSocket speedTestSocket = new SpeedTestSocket();
    public static double lastTransferRateBit;
    public static Client main;

    public  static ContentStreamManager contentManager;

    public static ClientController clientController;

    public static void main(String[] args) throws IOException {
        //makes logger more pretty.
        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "[CLIENT][%1$tT/%4$s]: %5$s%n"
        );

        contentManager = new ContentStreamManager();

        // add a listener to wait for speedtest completion and progress
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {
            @Override
            public void onCompletion(SpeedTestReport report) {
                double mbps = report.getTransferRateBit().doubleValue() / 1_000_000.0;
                System.out.println("[COMPLETED] rate in Mbps: " + mbps);
                lastTransferRateBit = mbps;
                main.send(new BandwidthPacket.Response(mbps));

                Platform.runLater(() -> {
                    MMClient.clientController.updateDownloadRate(mbps);
                });

            }

            @Override
            public void onError(SpeedTestError speedTestError, String errorMessage) {
                System.out.println("[BANDWIDTH CALC ERROR]: "+speedTestError+" " + errorMessage);

            }

            @Override
            public void onProgress(float percent, SpeedTestReport report) {
            }
        });

        new Thread(() -> {
            main = new Client("Star");
            try {
                main.connect("localhost", PORT,STREAM_PORT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
       launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ui/client.fxml")
        );

        Scene scene = new Scene(loader.load());
        stage.setTitle("Media Client");
        stage.setScene(scene);
        stage.show();

        clientController = loader.getController();
    }




}
