package dev.anshmehta.filevault.controller;

import dev.anshmehta.filevault.dto.FileListResponse;
import dev.anshmehta.filevault.dto.FileUploadResponse;
import dev.anshmehta.filevault.model.User;
import dev.anshmehta.filevault.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public CompletableFuture<ResponseEntity<FileUploadResponse>> uploadFile(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal User user) {
        try {
            return fileService.createFile(file, user)
                    .thenApply(uploadURL -> uploadURL.map(url -> ResponseEntity.ok(new FileUploadResponse(file.getOriginalFilename(), Optional.of(url))))
                            .orElse(ResponseEntity.badRequest().body(new FileUploadResponse("Error uploading file"))))
                    .exceptionally(ex -> {
                        FileUploadResponse fileUploadResponse = new FileUploadResponse(ex.getMessage());
                        return ResponseEntity.badRequest().body(fileUploadResponse);
                    });
        } catch (Exception e) {
            FileUploadResponse fileUploadResponse = new FileUploadResponse(e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(fileUploadResponse));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileListResponse>> listFiles(@AuthenticationPrincipal User user) {
        try {
            List<FileListResponse> files = fileService.getAllFilesForUser(user);
            if (files.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok().body(files);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{fileId}")
    public CompletableFuture<ResponseEntity<String>> deleteFile(@PathVariable String fileId, @AuthenticationPrincipal User user) {
        try {
            return fileService.deleteFile(fileId, user)
                    .thenApply(success -> {
                        if (success) {
                            return ResponseEntity.ok().body("File deleted successfully");
                        } else {
                            return ResponseEntity.badRequest().body("Error deleting file");
                        }
                    })
                    .exceptionally(ex -> ResponseEntity.badRequest().body(ex.getMessage()));
        } catch (Exception e) {
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(e.getMessage()));
        }
    }

}
