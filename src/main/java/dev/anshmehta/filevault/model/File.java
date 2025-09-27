package dev.anshmehta.filevault.model;
import jakarta.persistence.*;

@Entity
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String fileId;

    private String url;
    private String filename;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User owner;

    public File(String testURL, String originalFilename, User owner) {
        this.url = testURL;
        this.filename = originalFilename;
        this.owner = owner;
    }

    public File() {

    }

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

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

}
