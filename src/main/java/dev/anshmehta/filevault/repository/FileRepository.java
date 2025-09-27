package dev.anshmehta.filevault.repository;

import dev.anshmehta.filevault.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<File, String> {
}
