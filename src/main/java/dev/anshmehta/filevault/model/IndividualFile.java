package dev.anshmehta.filevault.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class IndividualFile {

    @Id
    private String fileHash;
    private String url;

    private Long fileSize;

    private Integer uploadCount = 1;

    @OneToMany(mappedBy = "individualFile")
    private List<UploadedFile> uploadedFiles = new ArrayList<>();

    public IndividualFile() {
    }

    public IndividualFile(String fileHash, String url, Long fileSize) {
        this.fileHash = fileHash;
        this.url = url;
        this.fileSize = fileSize;
    }

    // Getters and setters
    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getUploadCount() {
        return uploadCount;
    }

    public void setUploadCount(Integer uploadCount) {
        this.uploadCount = uploadCount;
    }

    public List<UploadedFile> getUploadedFiles() {
        return uploadedFiles;
    }

    public void setUploadedFiles(List<UploadedFile> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    // Helper method to increment upload count
    public void incrementUploadCount() {
        this.uploadCount++;
    }

    public void decrementUploadCount() {
        if (this.uploadCount > 0) {
            this.uploadCount--;
        }
    }
}