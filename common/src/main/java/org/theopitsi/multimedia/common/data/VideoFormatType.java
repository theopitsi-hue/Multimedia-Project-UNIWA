package org.theopitsi.multimedia.common.data;

public enum VideoFormatType {
    AVI,
    MP4,
    MKV;

    public static VideoFormatType parse(String format) {
        if (format.endsWith(".avi"))return AVI;
        if (format.endsWith(".mp4"))return MP4;
        if (format.endsWith(".mkv"))return MKV;

        if (format.equalsIgnoreCase("avi"))return AVI;
        if (format.equalsIgnoreCase("mp4"))return MP4;
        if (format.equalsIgnoreCase("mkv"))return MKV;
        return null;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
