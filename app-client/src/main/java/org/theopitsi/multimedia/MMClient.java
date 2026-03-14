package org.theopitsi.multimedia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.theopitsi.multimedia.client.Client;

import java.util.logging.Logger;

public class MMClient extends Application {
    public static Logger logger = Logger.getLogger("MM-CLIENT");
    private static final int PORT = 5000;

    public static void main(String[] args) {
        //makes logger more pretty.
        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "[%1$tT/%4$s]: %5$s%n"
        );

       Client main = new Client("Star");
       main.connect("localhost", PORT);
       launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ui/client.fxml"));
        stage.setTitle("MMClient");
        stage.setScene(new Scene(root));
        stage.show();
    }
}
