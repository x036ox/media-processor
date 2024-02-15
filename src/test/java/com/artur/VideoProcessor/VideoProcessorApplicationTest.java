package com.artur.VideoProcessor;

import com.artur.VideoProcessor.utils.ImageUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = VideoProcessorApplication.class)
public class VideoProcessorApplicationTest {
    public static final String TEST_VIDEO_FILE = "src/test/files/Video.mp4";
    public static final String TEST_IMAGE_FILE = "src/test/files/Image.jpg";

    @Test
    public void imageConverterTest() throws IOException {
        File image = new File(TEST_IMAGE_FILE);
        long lengthBefore = image.length();
        try (
                FileInputStream imageInputStream = new FileInputStream(image);
                FileInputStream imageInputStreamSecond = new FileInputStream(image)
        ){
            long lengthAfter = ImageUtils.compressUserPicture(imageInputStream).length;
            assertTrue(lengthBefore > lengthAfter);
            lengthAfter = ImageUtils.compressUserPicture(imageInputStreamSecond).length;
            assertTrue(lengthBefore > lengthAfter);
        }
    }
}