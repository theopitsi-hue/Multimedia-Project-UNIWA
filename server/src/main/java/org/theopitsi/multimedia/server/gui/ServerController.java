package org.theopitsi.multimedia.server.gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.theopitsi.multimedia.common.data.VideoData;
import org.theopitsi.multimedia.server.connection.client.ClientHandler;

import java.util.List;

public class ServerController {

    @FXML
    private TableView<ClientHandler> clientList;

    @FXML
    private TableColumn<ClientHandler, String> clientNameColumn;

    @FXML
    private TableColumn<ClientHandler, String> watchingColumn;

    @FXML
    private TableView<VideoData> movieList;

    @FXML
    private TableColumn<VideoData, String> nameColumn;

    @FXML
    private TableColumn<VideoData, String> resolutionColumn;

    @FXML
    private TableColumn<VideoData, String> formatColumn;

    @FXML
    private Label clientCapacityText;

    @FXML
    private Label statusMessageText;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button forceDisconnectClient;

    @FXML
    public void initialize() {

        clientNameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getClientName())
        );

        watchingColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCurrentlyWatching())
        );

        nameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFilename())
        );

        resolutionColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getQuality().toString())
        );

        formatColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFormat().toString())
        );

        forceDisconnectClient.setDisable(true);

        clientList.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, oldValue, newValue) ->
                        forceDisconnectClient.setDisable(newValue == null)
                );

        progressBar.setProgress(0);
        statusMessageText.setText("Status: Idle");
    }

    public void setVideos(List<VideoData> videos) {
        Platform.runLater(() ->
                movieList.setItems(
                        FXCollections.observableArrayList(videos)
                ));
    }

    public void setClients(List<ClientHandler> clients) {
        Platform.runLater(() ->
                clientList.setItems(
                        FXCollections.observableArrayList(clients)
                ));
    }

    public void updateCapacity(int current, int max) {
        Platform.runLater(() ->
                clientCapacityText.setText(
                        "Accepting Clients: " + current + "/" + max
                ));
    }

    public void updateStatus(String status) {
        Platform.runLater(() ->
                statusMessageText.setText(
                        "Status: " + status
                ));
    }

    public void updateProgress(double progress) {
        Platform.runLater(() ->
                progressBar.setProgress(progress)
        );
    }

    @FXML
    private void forceDisconnectSelectedClient() {

        ClientHandler selected =
                clientList.getSelectionModel().getSelectedItem();

        if (selected == null) {
            return;
        }

        System.out.println(
                "Disconnecting client: "
                        + selected.getName()
        );

        // TODO:
        // server.disconnect(selected.getClientId());
    }
}