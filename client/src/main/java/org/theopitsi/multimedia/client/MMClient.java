package org.theopitsi.multimedia.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.theopitsi.multimedia.client.data.Client;
import org.theopitsi.multimedia.client.gui.MediaController;

import java.util.logging.Logger;

public class MMClient extends Application {
    public static Logger logger = Logger.getLogger("MM-CLIENT");
    private static final int PORT = 5000;

    public static void main(String[] args) {
        //makes logger more pretty.
        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "[CLIENT][%1$tT/%4$s]: %5$s%n"
        );

       Client main = new Client("Star");
       main.connect("localhost", PORT);
       launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
//        Parent root = FXMLLoader.load(getClass().getResource("/ui/client.fxml"));
//        stage.setTitle("MMClient");
//        stage.setScene(new Scene(root));
//        stage.show();
//

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/ui/mediaplayer.fxml")
        );

        Scene scene = new Scene(loader.load());
        stage.setTitle("Media Player");
        stage.setScene(scene);
        stage.show();
        MediaController controller = loader.getController();
       // controller.playMedia(contentManager.getVideoFile(new VideoData("surfing", VideoFormatType.MP4, VideoQualityType.p720)).getAbsolutePath());
    }
}
