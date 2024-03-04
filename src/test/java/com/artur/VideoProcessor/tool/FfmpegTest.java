package com.artur.VideoProcessor.tool;

import com.artur.VideoProcessor.MediaProcessorApplicationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FfmpegTest extends MediaProcessorApplicationTest {
    @Autowired
    Ffmpeg ffmpeg;

    @Test
    void convertVideoToHls() throws Exception {
        File video = new File(TEST_VIDEO_FILE);
        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("tmp-ffmpeg");
            Path index = Path.of(tempDir + "/" + "index.mp4");
            Files.write(index, Files.readAllBytes(video.toPath()));
            ffmpeg.convertVideoToHls(index.toFile());
            int amount = tempDir.toFile().listFiles().length;
            assertTrue(amount > 1);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            FileSystemUtils.deleteRecursively(tempDir);
        }
    }

}