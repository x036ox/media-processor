package com.artur.VideoProcessor.utils;

public class AppConstants {

    public static final float USER_PICTURE_QUALITY = 0.6f;
    public static final float VIDEO_THUMBNAIL_QUALITY = 0.6f;


    //kafka related constants
    public static final String VIDEO_INPUT_TOPIC = "video_processor.video.input";
    public static final String VIDEO_OUTPUT_TOPIC = "video_processor.video.output";
    public static final String THUMBNAIL_INPUT_TOPIC = "video-processor.thumbnail.input";
    public static final String THUMBNAIL_OUTPUT_TOPIC = "video-processor.thumbnail.output";
    public static final String USER_PICTURE_INPUT_TOPIC = "video-processor.user-picture.input";
    public static final String USER_PICTURE_OUTPUT_TOPIC = "video-processor.user-picture.output";


}
