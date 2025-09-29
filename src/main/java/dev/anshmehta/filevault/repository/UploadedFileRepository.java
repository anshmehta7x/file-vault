package dev.anshmehta.filevault.repository;

import dev.anshmehta.filevault.model.UploadedFile;
import dev.anshmehta.filevault.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, String> {
    Optional<UploadedFile> findByIndividualFile_FileHash(String fileHash);
    List<UploadedFile> findByOwner(User owner);
}