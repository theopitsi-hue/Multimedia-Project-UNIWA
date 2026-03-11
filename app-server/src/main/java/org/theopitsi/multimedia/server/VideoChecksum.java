package org.theopitsi.multimedia.server;

import org.theopitsi.multimedia.server.transmission.VideoFormatType;
import org.theopitsi.multimedia.server.transmission.VideoQualityType;

import java.util.List;

public class VideoChecksum {
    //represents the available formats and qualities of video
    private VideoData[][] videoFormatQualityMap = new VideoData[VideoFormatType.values().length][VideoQualityType.values().length];

    void format(){
        for (int f = 0; f < VideoFormatType.values().length; f++) {
            for (int q = 0; q < VideoQualityType.values().length; q++) {
                //videoFormatQualityMap[f][q] = new VideoData();
            }
        }
    }
}
