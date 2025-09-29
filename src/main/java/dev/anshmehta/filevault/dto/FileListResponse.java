package dev.anshmehta.filevault.dto;

import dev.anshmehta.filevault.enums.FileAccess;

public class FileListResponse {
    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    private String fileId;
    private String fileName;
    private FileAccess accessLevel;
    private Long size;
    private String url;

    public FileListResponse(String fileId,String fileName, FileAccess accessLevel, Long size, String url) {
        this.fileName = fileName;
        this.accessLevel = accessLevel;
        this.size = size;
        this.url = url;
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileAccess getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(FileAccess accessLevel) {
        this.accessLevel = accessLevel;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}