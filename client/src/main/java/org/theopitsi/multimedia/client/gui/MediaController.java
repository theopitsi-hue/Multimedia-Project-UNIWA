package org.theopitsi.multimedia.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

import org.theopitsi.multimedia.client.MMClient;

import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.io.File;

public class MediaController {

    @FXML
    private ImageView videoView;

    @FXML
    private Button mediaPlay;

    @FXML
    private Slider mediaVolumeSlider;

    @FXML
    private ChoiceBox<String> mediaQualityChoice;

    @FXML
    private Text mediaDebugText;

    private MediaPlayerFactory factory;
    private EmbeddedMediaPlayer mediaPlayer;

    @FXML
    public void initialize() {

        mediaVolumeSlider.setMin(0);
        mediaVolumeSlider.setMax(100);
        mediaVolumeSlider.setValue(50);

        factory = new MediaPlayerFactory();
        mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer();

        mediaPlayer.videoSurface().set(
                new ImageViewVideoSurface(videoView)
        );

        mediaVolumeSlider.valueProperty().addListener(
                (obs, oldVal, newVal) ->
                        mediaPlayer.audio().setVolume(newVal.intValue())
        );

        mediaPlay.setOnAction(e -> togglePlay());
    }

    public void playMedia(String filePath) {

        try {

            File file = new File(filePath);

            mediaDebugText.setText(
                    "Now Watching: " + file.getName()
            );

            mediaPlayer.media().play(file.getAbsolutePath());

            mediaPlay.setText("Pause");

        } catch (Exception e) {

            e.printStackTrace();
            mediaDebugText.setText("Error loading media");
        }
    }

    private void togglePlay() {

        if (mediaPlayer.status().isPlaying()) {

            mediaPlayer.controls().pause();
            mediaPlay.setText("Play");

        } else {

            mediaPlayer.controls().play();
            mediaPlay.setText("Pause");
        }
    }

    public void onWindowClosed() {

        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        if (factory != null) {
            factory.release();
        }

        MMClient.logger.info("Media window closed");
    }
}