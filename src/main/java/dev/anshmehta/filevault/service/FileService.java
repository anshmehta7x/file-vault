package dev.anshmehta.filevault.service;

import dev.anshmehta.filevault.cloud.S3Service;
import dev.anshmehta.filevault.config.HashUtil;
import dev.anshmehta.filevault.dto.FileListResponse;
import dev.anshmehta.filevault.enums.FileAccess;
import dev.anshmehta.filevault.model.IndividualFile;
import dev.anshmehta.filevault.model.UploadedFile;
import dev.anshmehta.filevault.model.User;
import dev.anshmehta.filevault.repository.IndividualFileRepository;
import dev.anshmehta.filevault.repository.UploadedFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class FileService {

    private final UploadedFileRepository uploadedFileRepository;
    private final IndividualFileRepository individualFileRepository;
    private final S3Service s3Service;

    FileService(UploadedFileRepository uploadedFileRepository,
                IndividualFileRepository individualFileRepository,
                S3Service s3Service) {
        this.uploadedFileRepository = uploadedFileRepository;
        this.individualFileRepository = individualFileRepository;
        this.s3Service = s3Service;
    }

    private static Long convertFileSizeToLong(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return 0L;
        }
        return file.getSize();
    }

    public FileListResponse getFileById(String fileId, User user) throws Exception {
        Optional<UploadedFile> uploadedFile = uploadedFileRepository.findById(fileId);
        if (uploadedFile.isEmpty()) {
            throw new Exception("File does not exist");
        }
        if(!uploadedFile.get().getFileAccess().equals(FileAccess.PUBLIC) && !uploadedFile.get().getOwner().getUserId().equals(user.getUserId())) {
            throw new Exception("Unauthorized to access this file");
        }

        UploadedFile file = uploadedFile.get();
        return new FileListResponse(
                file.getFileId(),
                file.getFilename(),
                file.getFileAccess(),
                file.getIndividualFile().getFileSize(),
                file.getIndividualFile().getUrl()
        );
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
        String fileHash;
        try {
            fileHash = HashUtil.getFileHash(file);
        } catch (Exception e) {
            throw new Exception("Error computing file hash: " + e.getMessage());
        }

        Long fileSize = convertFileSizeToLong(file);

        if (fileSize <= 0L) {
            throw new Exception("File is Empty");
        }

        Optional<IndividualFile> existingIndividualFile = individualFileRepository.findById(fileHash);

        if (existingIndividualFile.isPresent()) {
            IndividualFile individualFile = existingIndividualFile.get();

            individualFile.incrementUploadCount();
            individualFileRepository.save(individualFile);

            UploadedFile uploadedFileToSave = new UploadedFile(file.getOriginalFilename(), user, individualFile);
            uploadedFileRepository.save(uploadedFileToSave);

            return CompletableFuture.completedFuture(Optional.of(individualFile.getUrl()));
        } else {
            try {
                return s3Service.uploadFile(file)
                        .handle((uploadURL, ex) -> {
                            if (ex != null) {
                                throw new RuntimeException("Error uploading file to S3: " + ex.getMessage(), ex);
                            }

                            try {
                                IndividualFile individualFile = new IndividualFile(fileHash, uploadURL, fileSize);
                                individualFileRepository.save(individualFile);

                                UploadedFile uploadedFileToSave = new UploadedFile(
                                        file.getOriginalFilename(), user, individualFile
                                );
                                uploadedFileRepository.save(uploadedFileToSave);

                                return Optional.of(individualFile.getUrl());
                            } catch (Exception dbEx) {
                                throw new RuntimeException("Error saving file to database: " + dbEx.getMessage(), dbEx);
                            }
                        });
            } catch (IOException e) {
                throw new Exception("Error reading file: " + e.getMessage());
            }
        }
    }

    public CompletableFuture<Boolean> deleteFromS3(IndividualFile individualFile, UploadedFile fileToDelete) {
        return s3Service.deleteFile(individualFile.getUrl())
                .handle((success, ex) -> {
                    if (ex != null) {
                        throw new RuntimeException("Error deleting file from S3: " + ex.getMessage(), ex);
                    }

                    if (success) {
                        try {
                            uploadedFileRepository.delete(fileToDelete);

                            individualFileRepository.delete(individualFile);

                            return true;
                        } catch (Exception dbEx) {
                            throw new RuntimeException("Error deleting from database: " + dbEx.getMessage(), dbEx);
                        }
                    } else {
                        throw new RuntimeException("Error deleting file from S3");
                    }
                });
    }

    public CompletableFuture<Boolean> deleteFile(String fileId, User user) throws Exception {
        Optional<UploadedFile> uploadedFile = uploadedFileRepository.findById(fileId);
        if (uploadedFile.isEmpty()) {
            throw new Exception("File does not exist");
        }
        UploadedFile fileToDelete = uploadedFile.get();
        IndividualFile individualFile = fileToDelete.getIndividualFile();
        if(!fileToDelete.getOwner().getUserId().equals(user.getUserId())) {
            throw new Exception("Unauthorized to delete this file");
        }
        if (individualFile.getUploadCount() == 1) {
            return deleteFromS3(individualFile, fileToDelete);
        } else {
            individualFile.decrementUploadCount();
            individualFileRepository.save(individualFile);

            uploadedFileRepository.delete(fileToDelete);

            return CompletableFuture.completedFuture(true);
        }
    }

    public CompletableFuture<Boolean> deleteAllFilesForUser(User user){
        List<UploadedFile> uploadedFiles = uploadedFileRepository.findByOwner(user);

        if (uploadedFiles.isEmpty()) {
            return CompletableFuture.completedFuture(true);
        }
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        for(UploadedFile uploadedFile : uploadedFiles){
            try {
                futures.add(deleteFile(uploadedFile.getFileId(), user));
            }
            catch (Exception e) {
                return CompletableFuture.completedFuture(false);
            }
        }
        CompletableFuture<?>[] futuresArray = futures.toArray(new CompletableFuture[0]);
        CompletableFuture<Void> allDeletesFuture = CompletableFuture.allOf(futuresArray);
        return allDeletesFuture.thenApply(v -> true)
                .exceptionally(ex -> false);
    }

    public boolean renameFile(String newName, String fileId, User user) throws Exception {
        Optional<UploadedFile> uploadedFile = uploadedFileRepository.findById(fileId);
        if (uploadedFile.isEmpty()) {
            throw new Exception("File does not exist");
        }
        UploadedFile fileToRename = uploadedFile.get();
        if(!fileToRename.getOwner().getUserId().equals(user.getUserId())) {
            throw new Exception("Unauthorized to rename this file");
        }
        fileToRename.setFilename(newName);
        uploadedFileRepository.save(fileToRename);
        return true;
    }


}