package org.theopitsi.multimedia.server;

import org.graalvm.collections.Pair;
import org.jspecify.annotations.NonNull;
import org.theopitsi.multimedia.MMServer;
import org.theopitsi.multimedia.server.transmission.VideoFormatType;
import org.theopitsi.multimedia.server.transmission.VideoQualityType;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.info.MultimediaInfo;
import ws.schild.jave.info.VideoSize;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class ContentManager {
    private static final String videoDir = "B:/Code/GitHub/Multimedia-Project-UNIWA/Videos";

    private final HashMap<VideoData,Path> videoDatabase = new HashMap<>();
    private final HashSet<Pair<VideoData,VideoData>> videosToGenerate = new HashSet<>();

    public ContentManager(){
        // Path to the Chocolatey-installed FFmpeg

        // Tell JAVE to use this executable
       //Encoder.addOptionAtIndex(ffmpeg);
    }

    public void collectMedia(){
        //scan folder for videos within the quality range and formats we support
        //look into video file metadata fr
        scanMediaFolder();
        generateMissingMedia();


        //fill in the videosToGenerate depending on what we dont have
//        while (videoDatabase.iterator().hasNext()){
//           // var video = getvideo()
//            var next = videoDatabase.iterator().next();
//            next.getFilename();
//            next.getFormat();
//        }
    }

    private void scanMediaFolder(){
        Path videosDir = Paths.get(videoDir);
        final File folder = new File(videoDir);

        MMServer.logger.info("Scanning video folder: " + folder.getAbsolutePath());

        try {
            if (Files.notExists(videosDir)) {
                var dir = Files.createDirectories(videosDir);
                MMServer.logger.info("Video folder missing! Created video folder:" + dir.toAbsolutePath());
                return; //directory was just made so theres nothing in it
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (final File fileEntry : Objects.requireNonNull(folder.listFiles())) {
            if (!fileEntry.isDirectory()) {
                MMServer.logger.info("Found Video: " + fileEntry.getName());

                MultimediaObject multimediaObject = new MultimediaObject(fileEntry);

                try {
                    MultimediaInfo info = multimediaObject.getInfo();
                    String cleanName = removeExtension(fileEntry.getName()).replaceAll("-.*$", "");

                    var format = VideoFormatType.parse(fileEntry.getName().toLowerCase());
                    if (format == null){
                        MMServer.logger.warning("Unsupported Extension: "+fileEntry.getName());
                        continue;
                    }

                    var quality = VideoQualityType.parse(info.getVideo().getSize());
                    if (quality == null){
                        MMServer.logger.warning("Unsupported Resolution: " + fileEntry.getName() + " ("+info.getVideo().getSize().aspectRatioExpression()+")");
                        continue;
                    }

                    //packages the data into a compact format to send to client
                    var data = new VideoData(cleanName,format,quality);
                    videoDatabase.put(data,fileEntry.toPath().toAbsolutePath());

                    //generate all missing formats, "queue" em up for creation.
                    for (int f = 0; f < VideoFormatType.values().length; f++) {
                        for (int q = 0; q < VideoQualityType.values().length; q++) {

                            //checking for lesser quality
                            if (q < data.getQuality().ordinal()){
                                //generate (formats * qualities) videos
                                videosToGenerate.add(Pair.create(data, new VideoData(
                                        cleanName,
                                        VideoFormatType.values()[f],
                                        VideoQualityType.values()[q]
                                )));

                            }
                            //checking for native quality
                            else if (q == data.getQuality().ordinal()){
                                //generate missing formats for native quality
                                if (f != data.getFormat().ordinal()){
                                    videosToGenerate.add(Pair.create(data, new VideoData(
                                            cleanName,
                                            VideoFormatType.values()[f],
                                            VideoQualityType.values()[q]
                                    )));
                                }
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void generateMissingMedia(){
        //beep boop generate videos
        int totalGenerated = 0;
        for (Pair<VideoData, VideoData> pair : videosToGenerate) {
            var to = pair.getRight();
            var from = pair.getLeft();

            //make sure it doesnt exist already!
            //so we dont generate copies of videos!
            //might beeee slooowwwww
            if (videoDatabase.containsKey(to)) continue;


            MMServer.logger.info("Generating Video: " + to);
            generateVideo(from,to.getFormat(),to.getQuality());
            totalGenerated++;

        }
        MMServer.logger.info("Finished Generating "+totalGenerated+" missing files.");
        //clear todo list
        videosToGenerate.clear();
    }

    private void compileMediaList(){
    }

    private void generateVideo(VideoData data, VideoFormatType format, VideoQualityType quality){
        String inputFormat = data.getFormat().toString();
        String outputFormat = format.toString();

        File source = videoDatabase.get(data).toFile();
        File target = new File(videoDir + "/" + data.getFilename()+"-"+quality+"."+outputFormat);

        AudioAttributes audio = new AudioAttributes();
        VideoAttributes video = new VideoAttributes();
        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setAudioAttributes(audio);
        attrs.setVideoAttributes(video);

        video.setSize(quality.toVideoSize());
        video.setCodec("h264");

        if(format == VideoFormatType.MP4 || format == VideoFormatType.AVI){
            audio.setCodec("aac"); // MP4/AVI
        } else {
            audio.setCodec("libvorbis"); // MKV
        }


        MMServer.logger.info("Generating: " + data.getQuality()+"/"+data.getFormat()+" -> "+ quality+"/"+format);
        attrs.setOutputFormat(outputFormat.equalsIgnoreCase("MKV") ? "matroska" : outputFormat.toLowerCase());

        try {
            Encoder instance = new Encoder();

            instance.encode(new MultimediaObject(source), target, attrs, null);
        } catch (EncoderException e) {
            e.printStackTrace();
        }
    }

    public static String removeExtension(String filename) {
        if (filename == null) return null;
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) return filename; //no extension found
        return filename.substring(0, lastDot);
    }
}
