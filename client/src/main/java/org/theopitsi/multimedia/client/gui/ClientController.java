package org.theopitsi.multimedia.client.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.theopitsi.multimedia.client.MMClient;
import org.theopitsi.multimedia.common.data.VideoData;
import org.theopitsi.multimedia.common.data.VideoFormatType;
import org.theopitsi.multimedia.common.data.VideoQualityType;

import java.util.List;

public class ClientController {

    @FXML
    private Button connectionButton;

    @FXML
    private Label downloadRateDisplay;

    @FXML
    private TableView<VideoData> VideoDataList;

    @FXML
    private Button playMovieButton;

    @FXML
    private ChoiceBox<String> protocolChoice;

    private boolean connected = true;

    @FXML
    private TableColumn<VideoData, String> nameColumn;

    @FXML
    private TableColumn<VideoData, String> resolutionColumn;

    @FXML
    private TableColumn<VideoData, String> formatColumn;

    @FXML
    public void initialize() {

        nameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFilename())
        );

        resolutionColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getQuality().toString())
        );

        formatColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFormat().toString())
        );

        //sample data
        VideoDataList.setItems(FXCollections.observableArrayList((
                new VideoData("Interstellar", VideoFormatType.AVI, VideoQualityType.p720)
        )));

        //protocol choices
        protocolChoice.setItems(FXCollections.observableArrayList(
                "TCP",
                "UDP",
                "RTP",
                "Auto"
        ));

        protocolChoice.getSelectionModel().selectFirst();

        //enable play button when a VideoData is selected
        VideoDataList.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) ->
                        playMovieButton.setDisable(newSelection == null)
        );

        connectionButton.setOnAction(event -> toggleConnection());

        playMovieButton.setOnAction(event -> playSelectedVideoData());
    }

    private void toggleConnection() {
        connected = !connected;

        if (connected) {
            connectionButton.setText("Disconnect");
        } else {
            connectionButton.setText("Connect");
            playMovieButton.setDisable(true);
        }
    }

    @FXML
    private void playSelectedVideoData() {

        VideoData VideoData = VideoDataList.getSelectionModel().getSelectedItem();

        if (VideoData == null) {
            return;
        }

        //send play packet
        MMClient.contentManager.setVideoWatching(VideoData);
    }

    public void updateDownloadRate(double mbps) {
        downloadRateDisplay.setText(
                String.format("Download Rate: %.2f mb/s", mbps)
        );
    }

    public void setVideoList(List<VideoData> videos){
        VideoDataList.setItems(FXCollections.observableArrayList(videos));

    }
}