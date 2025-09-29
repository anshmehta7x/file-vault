package dev.anshmehta.filevault.service;

import dev.anshmehta.filevault.cloud.S3Service;
import dev.anshmehta.filevault.config.HashUtil;
import dev.anshmehta.filevault.model.File;
import dev.anshmehta.filevault.model.User;
import dev.anshmehta.filevault.repository.FileRepository;
//import dev.anshmehta.filevault.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
public class FileService {

    private final FileRepository fileRepository;
//    private final UserRepository userRepository;
    private final S3Service s3Service;

    FileService(FileRepository fileRepository, S3Service s3Service){
        this.fileRepository = fileRepository;
//        this.userRepository = userRepository;
        this.s3Service = s3Service;
    }

    private static Long convertFileSizeToLong(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return 0L;
        }
        return file.getSize(); // size in bytes
    }


    public Optional<String> createFile(MultipartFile file, User user) throws Exception {
        String fileHash = HashUtil.getFileHash(file);
        Long fileSize = convertFileSizeToLong(file);
        if(fileSize <= 0L){
            throw new Exception("File is Empty");
        }
        // check file hash if present, then dedupe

        Optional<File> existingFile = fileRepository.findByFileHash(fileHash);
        String uploadURL = "";
        if(existingFile.isPresent()){
            uploadURL = existingFile.get().getUrl();
        }
        else{
            try{
                uploadURL = s3Service.uploadFile(file);
            }
            catch(Exception e){
                throw new Exception("Error uploading file to S3: " + e.getMessage());
            }
        }
        File fileToSave = new File(uploadURL, file.getOriginalFilename(),fileHash,fileSize, user);
        fileRepository.save(fileToSave);
        return Optional.of(uploadURL);
    }




}
