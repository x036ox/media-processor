package com.artur.VideoProcessor.tool;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class Ffmpeg {


    /**Converts video into m3u8 and ts files. All operations writs in log file.
     * @param file video to convert
     * @throws Exception - if convert fails
     */
    public void convertVideoToHls(File file) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(buildFfmpegCommand(file));
        Path ffmpegLog = Path.of("logging/ffmpeg.log");
        if (!Files.exists(ffmpegLog)) {
            Files.createDirectory(Path.of("logging"));
            Files.createFile(ffmpegLog);
        }
        processBuilder.redirectError(ffmpegLog.toFile())
                .redirectOutput(ffmpegLog.toFile());
        processBuilder.start().waitFor();
    }


    private String[] buildFfmpegCommand(File video){
        //result string is going to look like:
        // ffmpeg -i input.mp4 -c copy -hls_time 10 -hls_list_size 0 -hls_segment_filename "output_%03d.ts" output.m3u8

        String pathWithoutExtension = StringUtils.stripFilenameExtension(video.getAbsolutePath());
        return new String[]{
                "ffmpeg", "-i",
                video.getAbsolutePath(),
                "-c", "copy", "-hls_time",
                Integer.toString(10),
                "-hls_list_size", "0", "-hls_segment_filename",
                pathWithoutExtension + "%03d.ts",
                pathWithoutExtension + ".m3u8"
        };
    }
}
