package org.theopitsi.multimedia;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.theopitsi.multimedia.server.ConnectionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

public class MMServer extends Application {
    public static Logger logger = Logger.getLogger("MM-SERVER");
    public static ConnectionManager connectionManager;


    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ui/server.fxml"));
        stage.setTitle("MMServer");
        stage.setScene(new Scene(root));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
        connectionManager = new ConnectionManager(4);
        connectionManager.beginListening();

    }
}
