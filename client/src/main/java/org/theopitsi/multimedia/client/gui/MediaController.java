package org.theopitsi.multimedia.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;

import java.io.File;

public class MediaController {

    @FXML
    private MediaView mediaView;

    @FXML
    private Button mediaPlay;

    @FXML
    private Slider mediaVolumeSlider;

    @FXML
    private ChoiceBox<String> mediaQualityChoice;

    @FXML
    private Text mediaDebugText;

    private MediaPlayer mediaPlayer;

    @FXML
    public void initialize() {
        mediaVolumeSlider.setMin(0);
        mediaVolumeSlider.setMax(1);
        mediaVolumeSlider.setValue(0.5);

        mediaPlay.setOnAction(e -> togglePlay());
    }

    /**
     * Load and play a media file
     */
    public void playMedia(String filePath) {
        try {
            File file = new File(filePath);
            //MMServer.logger.info(filePath);
            Media media = new Media(file.toURI().toString());

            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);

            // Bind volume
            mediaPlayer.volumeProperty().bind(mediaVolumeSlider.valueProperty());

            mediaDebugText.setText("Now Watching: " + file.getName());

            mediaPlayer.play();

        } catch (Exception e) {
            e.printStackTrace();
            mediaDebugText.setText("Error loading media");
        }
    }

    /**
     * Play / Pause toggle
     */
    private void togglePlay() {
        if (mediaPlayer == null) return;

        switch (mediaPlayer.getStatus()) {
            case MediaPlayer.Status.PLAYING:
                mediaPlayer.pause();
                mediaPlay.setText("Play");
                break;
            case MediaPlayer.Status.PAUSED:
            case MediaPlayer.Status.READY:
            case MediaPlayer.Status.STOPPED:
                mediaPlayer.play();
                mediaPlay.setText("Pause");
                break;
        }
    }
}
