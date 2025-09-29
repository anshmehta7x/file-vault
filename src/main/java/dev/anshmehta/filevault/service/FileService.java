package dev.anshmehta.filevault.service;

import dev.anshmehta.filevault.cloud.S3Service;
import dev.anshmehta.filevault.config.HashUtil;
import dev.anshmehta.filevault.dto.FileListResponse;
import dev.anshmehta.filevault.model.IndividualFile;
import dev.anshmehta.filevault.model.UploadedFile;
import dev.anshmehta.filevault.model.User;
import dev.anshmehta.filevault.repository.IndividualFileRepository;
import dev.anshmehta.filevault.repository.UploadedFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class FileService {

    private final UploadedFileRepository uploadedFileRepository;
    private final IndividualFileRepository individualFileRepository;
    private final S3Service s3Service;

    FileService(UploadedFileRepository uploadedFileRepository, IndividualFileRepository individualFileRepository, S3Service s3Service) {
        this.uploadedFileRepository = uploadedFileRepository;
        this.individualFileRepository = individualFileRepository;
        this.s3Service = s3Service;
    }

    private static Long convertFileSizeToLong(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return 0L;
        }
        return file.getSize(); // size in bytes
    }

    public List<FileListResponse> getAllFilesForUser(User owner) {
        List<UploadedFile> uploadedFiles = uploadedFileRepository.findByOwner(owner);
        return uploadedFiles.stream().map(
                file -> new FileListResponse(
                        file.getFileId(),
                        file.getFilename(),
                        file.getFileAccess(),
                        file.getIndividualFile().getFileSize(),
                        file.getIndividualFile().getUrl()
                )
        ).collect(Collectors.toList());
    }

    public CompletableFuture<Optional<String>> createFile(MultipartFile file, User user) throws Exception {
        String fileHash = HashUtil.getFileHash(file);
        Long fileSize = convertFileSizeToLong(file);
        if (fileSize <= 0L) {
            throw new Exception("File is Empty");
        }

        Optional<IndividualFile> existingIndividualFile = individualFileRepository.findById(fileHash);

        if (existingIndividualFile.isPresent()) {
            // File already exists, no need to upload
            IndividualFile individualFile = existingIndividualFile.get();
            individualFile.incrementUploadCount();
            individualFileRepository.save(individualFile);

            UploadedFile uploadedFileToSave = new UploadedFile(file.getOriginalFilename(), user, individualFile);
            uploadedFileRepository.save(uploadedFileToSave);

            return CompletableFuture.completedFuture(Optional.of(individualFile.getUrl()));
        } else {
            // New file, need to upload to S3
            try {
                return s3Service.uploadFile(file)
                        .thenApply(uploadURL -> {
                            IndividualFile individualFile = new IndividualFile(fileHash, uploadURL, fileSize);
                            individualFileRepository.save(individualFile);

                            UploadedFile uploadedFileToSave = new UploadedFile(file.getOriginalFilename(), user, individualFile);
                            uploadedFileRepository.save(uploadedFileToSave);

                            return Optional.of(individualFile.getUrl());
                        })
                        .exceptionally(ex -> {
                            throw new RuntimeException("Error uploading file to S3: " + ex.getMessage(), ex);
                        });
            } catch (IOException e) {
                throw new Exception("Error reading file: " + e.getMessage());
            }
        }
    }

    public CompletableFuture<Boolean> deleteFile(String fileId) throws Exception {
        Optional<UploadedFile> uploadedFile = uploadedFileRepository.findById(fileId);

        if (uploadedFile.isEmpty()) {
            throw new Exception("File does not exist");
        }

        UploadedFile fileToDelete = uploadedFile.get();
        IndividualFile individualFile = fileToDelete.getIndividualFile();

        if (individualFile.getUploadCount() == 1) {
            // Last reference - delete from S3 and database
            return s3Service.deleteFile(individualFile.getUrl())
                    .thenApply(success -> {
                        if (success) {
                            uploadedFileRepository.delete(fileToDelete);
                            individualFileRepository.delete(individualFile);
                            return true;
                        } else {
                            throw new RuntimeException("Error deleting file from S3");
                        }
                    })
                    .exceptionally(ex -> {
                        throw new RuntimeException("Error deleting file: " + ex.getMessage(), ex);
                    });
        } else {
            // Still other references - just decrement count
            individualFile.decrementUploadCount();
            individualFileRepository.save(individualFile);
            uploadedFileRepository.delete(fileToDelete);
            return CompletableFuture.completedFuture(true);
        }
    }
}