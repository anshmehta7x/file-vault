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
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public CompletableFuture<ResponseEntity<FileUploadResponse>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) {

        try {
            return fileService.createFile(file, user)
                    .handle((uploadURL, ex) -> {
                        if (ex != null) {
                            FileUploadResponse errorResponse = new FileUploadResponse(ex.getMessage());
                            return ResponseEntity.badRequest().body(errorResponse);
                        }

                        if (uploadURL.isPresent()) {
                            FileUploadResponse response = new FileUploadResponse(
                                    file.getOriginalFilename(),
                                    uploadURL
                            );
                            return ResponseEntity.ok(response);
                        } else {
                            return ResponseEntity.badRequest()
                                    .body(new FileUploadResponse("Error uploading file"));
                        }
                    });

        } catch (Exception e) {
            FileUploadResponse fileUploadResponse = new FileUploadResponse(e.getMessage());
            return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(fileUploadResponse)
            );
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
    public CompletableFuture<ResponseEntity<String>> deleteFile(
            @PathVariable String fileId,
            @AuthenticationPrincipal User user) {

        try {
            return fileService.deleteFile(fileId, user)
                    .handle((success, ex) -> {
                        if (ex != null) {
                            return ResponseEntity.badRequest().body(ex.getMessage());
                        }

                        if (success) {
                            return ResponseEntity.ok().body("File deleted successfully");
                        } else {
                            return ResponseEntity.badRequest().body("Error deleting file");
                        }
                    });

        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(e.getMessage())
            );
        }
    }

    @DeleteMapping("/delete")
    public CompletableFuture<ResponseEntity<String>> deleteAllFiles(@AuthenticationPrincipal User user) {
        try{
            return fileService.deleteAllFilesForUser(user)
                    .handle((success, ex) -> {
                        if (ex != null) {
                            return ResponseEntity.badRequest().body(ex.getMessage());
                        }
                        if (success) {
                            return ResponseEntity.ok().body("All files deleted successfully");
                        } else {
                            return ResponseEntity.badRequest().body("Error deleting files");
                        }
                    });
        } catch (Exception e) {
            return CompletableFuture.completedFuture(
                    ResponseEntity.badRequest().body(e.getMessage())
            );
        }
    }
}