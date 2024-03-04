package com.artur.VideoProcessor;

import com.artur.VideoProcessor.utils.ImageUtils;
import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("dev")
@EmbeddedKafka
@SpringBootTest(classes = MediaProcessorApplication.class)
public class MediaProcessorApplicationTest {
    public static final String TEST_VIDEO_FILE = "src/test/files/Video.mp4";
    public static final String TEST_IMAGE_FILE = "src/test/files/Image.jpg";

    @MockBean
    MinioClient minioClient;

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