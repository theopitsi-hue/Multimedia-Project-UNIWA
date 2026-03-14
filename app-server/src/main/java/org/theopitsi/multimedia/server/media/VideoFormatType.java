package org.theopitsi.multimedia.server.media;

public enum VideoFormatType {
    AVI,
    MP4,
    MKV;

    public static VideoFormatType parse(String format) {
        if (format.endsWith(".avi"))return AVI;
        if (format.endsWith(".mp4"))return MP4;
        if (format.endsWith(".mkv"))return MKV;
        return null;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
