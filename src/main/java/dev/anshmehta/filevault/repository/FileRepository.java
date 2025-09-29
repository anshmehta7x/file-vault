package dev.anshmehta.filevault.repository;

import dev.anshmehta.filevault.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, String> {
    Optional<File> findByFileHash(String fileHash);
}
