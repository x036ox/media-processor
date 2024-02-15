package com.artur.VideoProcessor.service;

import io.minio.GetObjectResponse;
import io.minio.messages.Item;

import java.io.File;
import java.util.List;

public interface MinioService {

    void putObject(byte[] objectBytes, String objectName) throws Exception;

    void uploadObject(File object, String pathname) throws Exception;

    void putFolder(String folderName) throws Exception;

    List<Item> listFiles(String prefix) throws Exception;

    GetObjectResponse getObject(String objectName) throws Exception;

    File download(String objectName) throws Exception;

    void removeObject(String objectName) throws Exception;

    void removeFolder(String prefix) throws Exception;
}
