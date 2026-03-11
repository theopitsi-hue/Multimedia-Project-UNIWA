package org.theopitsi.multimedia.server.transmission;

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
}
