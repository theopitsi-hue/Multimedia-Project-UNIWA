package org.theopitsi.multimedia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.theopitsi.multimedia.server.connection.ConnectionManager;
import org.theopitsi.multimedia.server.media.ContentManager;

import java.util.logging.Logger;

public class MMServer extends Application {
    public static Logger logger = Logger.getLogger("MM-SERVER");
    public static ConnectionManager connectionManager;
    public static ContentManager contentManager;


    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ui/server.fxml"));
        stage.setTitle("MMServer");
        stage.setScene(new Scene(root));
        stage.show();


        contentManager = new ContentManager();
        contentManager.collectMedia();

        //manage connections without hanging graphics
        new Thread(()->{
            connectionManager = new ConnectionManager(4);
            connectionManager.beginListening();
        }).start();
    }

    public static void main(String[] args) {
        //makes logger more pretty.
        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "[%1$tT/%4$s]: %5$s%n"
        );

        launch(args);
    }
}
