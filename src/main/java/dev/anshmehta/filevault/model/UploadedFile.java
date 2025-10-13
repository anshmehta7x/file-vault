package dev.anshmehta.filevault.model;
import dev.anshmehta.filevault.enums.FileAccess;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
public class UploadedFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String fileId;
    private String filename;

    @ManyToOne
    @JoinColumn(name = "file_hash", referencedColumnName = "fileHash")
    private IndividualFile individualFile;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User owner;

    @ManyToMany
    @JoinTable(
            name = "file_access_list",
            joinColumns = @JoinColumn(name = "file_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> accessList = new HashSet<User>() {
    };

    @Enumerated(EnumType.STRING)
    private FileAccess fileAccess = FileAccess.PRIVATE;

    public UploadedFile(String originalFilename, User owner, IndividualFile individualFile) {
        this.filename = originalFilename;
        this.owner = owner;
        this.individualFile = individualFile;
    }

    public UploadedFile() {

    }

    // Getters and setters
    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public IndividualFile getIndividualFile() {
        return individualFile;
    }

    public void setIndividualFile(IndividualFile individualFile) {
        this.individualFile = individualFile;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public FileAccess getFileAccess() {
        return fileAccess;
    }

    public void setFileAccess(FileAccess fileAccess) {
        this.fileAccess = fileAccess;
    }

    public Set<User> getAccessList() {
        return accessList;
    }

    public void setAccessList(Set<User> accessList) {
        this.accessList = accessList;
    }
}