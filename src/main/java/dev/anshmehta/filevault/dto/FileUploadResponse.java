package dev.anshmehta.filevault.dto;

import dev.anshmehta.filevault.model.File;

import java.util.Optional;

public class FileUploadResponse {

    public FileUploadResponse(String name, Optional<String> uploadURL) {
        this.fileName = name;
        this.uploadURL = uploadURL.orElse(null);
    }

    public String getUploadURL() {
        return uploadURL;
    }

    public void setUploadURL(String uploadURL) {
        this.uploadURL = uploadURL;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String fileName;
    private String uploadURL;
    private String error;

    public FileUploadResponse(String fileName, String uploadURL) {
        this.fileName = fileName;
        this.uploadURL = uploadURL;
    }

    public FileUploadResponse(String error) {
        this.error = error;
    }



}
