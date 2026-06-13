package org.theopitsi.multimedia.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.theopitsi.multimedia.common.packet.Packet;
import org.theopitsi.multimedia.server.connection.ConnectionManager;
import org.theopitsi.multimedia.server.connection.ContentStreamOutput;
import org.theopitsi.multimedia.server.connection.client.ClientHandler;
import org.theopitsi.multimedia.server.gui.ServerController;
import org.theopitsi.multimedia.server.media.ContentManager;

import java.util.logging.Logger;

public class MMServer extends Application {
    public static Logger logger = Logger.getLogger("MM-SERVER");
    public static ConnectionManager connectionManager;
    public static ContentStreamOutput connectionStreamOutput;
    public static ContentManager contentManager;
    public static ServerController serverController;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ui/server.fxml")
        );

        Scene scene = new Scene(loader.load());
        stage.setTitle("Media Server");
        stage.setScene(scene);
        stage.show();

        contentManager = new ContentManager();
        contentManager.collectMedia();

        //manage connections without hanging graphics
        new Thread(()->{
            connectionManager = new ConnectionManager();
            connectionStreamOutput = new ContentStreamOutput();

            connectionManager.beginListening();
            connectionStreamOutput.beginListening();
        }).start();

        serverController = loader.getController();
        serverController.setVideos(contentManager.getVideos());
    }

    public static void main(String[] args) {
        //makes logger more pretty.
        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "[SERVER][%1$tT/%4$s]: %5$s%n"
        );

        launch(args);
    }


}
