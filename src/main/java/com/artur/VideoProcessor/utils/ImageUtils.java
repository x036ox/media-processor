package com.artur.VideoProcessor.utils;

import net.coobird.thumbnailator.Thumbnails;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class ImageUtils {
    private static final Logger logger = LoggerFactory.getLogger(ImageUtils.class);

    public static byte[] compressUserPicture(@NotNull InputStream inputStream) throws IOException {
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ){
            Thumbnails.of(inputStream)
                    .size(240, 240)
                    .outputQuality(AppConstants.USER_PICTURE_QUALITY)
                    .allowOverwrite(true)
                    .toOutputStream(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }

    public static byte[] compressVideoThumbnail(@NotNull InputStream inputStream) throws IOException {
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ){
            Thumbnails.of(inputStream)
                    .size(240, 320)
                    .outputQuality(AppConstants.VIDEO_THUMBNAIL_QUALITY)
                    .allowOverwrite(true)
                    .toOutputStream(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }

}
