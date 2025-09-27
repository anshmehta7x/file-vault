package dev.anshmehta.filevault.controller;

import dev.anshmehta.filevault.dto.FileUploadResponse;
import dev.anshmehta.filevault.model.User;
import dev.anshmehta.filevault.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequestMapping("/file")
public class FileController {

    private final FileService fileService;

    FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal User user) {
        try{
            Optional<String> uploadURL = fileService.createFile(file, user);
            if(uploadURL.isEmpty()){
                return ResponseEntity.badRequest().body(new FileUploadResponse("Error uploading file"));
            }
            return ResponseEntity.ok(new FileUploadResponse(file.getName(), uploadURL));
        }catch (Exception e){
            FileUploadResponse fileUploadResponse = new FileUploadResponse(e.getMessage());
            return ResponseEntity.badRequest().body(fileUploadResponse);
        }

    }
}
