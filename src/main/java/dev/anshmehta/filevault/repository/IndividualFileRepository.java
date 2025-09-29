package dev.anshmehta.filevault.repository;

import dev.anshmehta.filevault.model.IndividualFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndividualFileRepository extends JpaRepository<IndividualFile, String> {
}
