package org.theopitsi.multimedia.server.media;

import ws.schild.jave.info.VideoSize;

public enum VideoQualityType {
    p240(300,700,400),
    p360(400,1000,750),
    p480(500,2000,1000),
    p720(1500,4000,2500),
    p1080(3000,6000, 3000);

    public final int max;
    public final int min;
    public final int rec;

    /// A video quality enum that takes in the maximum, minimum and recommended kbps
    /// to use for determining the max quality of video a user can see.
    VideoQualityType(int min, int max, int rec){
        this.max = max;
        this.min = min;
        this.rec = rec;
    }

    public static VideoQualityType parse(VideoSize size) {
        var h = size.getHeight();

        //yes, this looks ugly.
        switch (h){
            case 240 -> {
                return p240;
            }
            case 360 -> {
                return p360;
            }
            case 480 -> {
                return p480;
            }
            case 720 -> {
                return p720;
            }
            case 1080 -> {
                return p1080;
            }
            default -> {
                return null;
            }
        }
    }

    /// if le given kbps speed is enough to support this quality. Used to filter vid list
    public boolean meetsSpeedReq(int kbps){
        return kbps >= min && kbps <= max;
    }

    @Override
    public String toString() {
        return switch (this) {
            case p240 -> "240p";
            case p360 -> "360p";
            case p480 -> "480p";
            case p720 -> "720p";
            case p1080 -> "1080p";
        };
    }

    public VideoSize toVideoSize() {
        switch (this){
            case p240 -> {
                return new VideoSize(426,240);
            }
            case p360 -> {
                return new VideoSize(640,360);
            }
            case p480 -> {
                return new VideoSize(854,480);
            }
            case p720 -> {
                return new VideoSize(1280,720);
            }
            default -> {
                return new VideoSize(1920,1080);
            }
        }
    }
}
