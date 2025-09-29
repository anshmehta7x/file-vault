package dev.anshmehta.filevault.model;
import dev.anshmehta.filevault.enums.FileAccess;
import jakarta.persistence.*;


@Entity
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String fileId;

    private String url;
    private String filename;
    private String fileHash;
    private Long fileSize;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId")
    private User owner;

    @Enumerated(EnumType.STRING)
    private FileAccess fileAccess = FileAccess.PRIVATE;

    public File(String testURL, String originalFilename, String fileHash,Long fileSize ,User owner) {
        this.url = testURL;
        this.filename = originalFilename;
        this.fileSize = fileSize;
        this.owner = owner;
        this.fileHash = fileHash;
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
