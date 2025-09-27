package dev.anshmehta.filevault.service;

import dev.anshmehta.filevault.model.File;
import dev.anshmehta.filevault.model.User;
import dev.anshmehta.filevault.repository.FileRepository;
import dev.anshmehta.filevault.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;

    FileService(FileRepository fileRepository, UserRepository userRepository) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
    }

    public Optional<String> createFile(MultipartFile file, User user) throws Exception {
        // check file hash if present, then dedupe


        String testURL = "www.anshmehta.dev"; // change to s3 upload url
        fileRepository.save(new File(testURL, file.getOriginalFilename(), user));

        return Optional.of(testURL);
    }




}
