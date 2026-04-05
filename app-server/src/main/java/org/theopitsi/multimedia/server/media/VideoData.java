package org.theopitsi.multimedia.server.media;

import java.util.Objects;

public class VideoData {
    private final String filename;
    private final VideoFormatType format;
    private final VideoQualityType quality;

    public VideoData(String filename, VideoFormatType format, VideoQualityType quality) {
        this.filename = filename;
        this.format = format;
        this.quality = quality;
    }

    @Override
    public String toString() {
        return filename+"_("+quality+")"+"["+format+"]";
    }

    public String toFileName(){
        return filename+"-"+quality+"."+format;
    }

    @Override
    public boolean equals(Object o) {
        //sets the requirements for similiarity. Two data objects are equal only if all their fields are equal

        if (o == null || getClass() != o.getClass()) return false;
        VideoData videoData = (VideoData) o;
        return Objects.equals(filename, videoData.filename) && format == videoData.format && quality == videoData.quality;
    }

    @Override
    public int hashCode() {
        //used for identifying each object as unique within a hashmap
        return Objects.hash(filename, format, quality);
    }

    public String getFilename() {
        return filename;
    }

    public VideoFormatType getFormat() {
        return format;
    }

    public VideoQualityType getQuality() {
        return quality;
    }
}
