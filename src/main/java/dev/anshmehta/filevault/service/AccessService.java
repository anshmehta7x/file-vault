package dev.anshmehta.filevault.service;

import dev.anshmehta.filevault.enums.FileAccess;
import dev.anshmehta.filevault.model.UploadedFile;
import dev.anshmehta.filevault.model.User;
import dev.anshmehta.filevault.repository.UploadedFileRepository;
import dev.anshmehta.filevault.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class AccessService {

    private final UploadedFileRepository uploadedFileRepository;
    private final UserRepository userRepository;

    AccessService(UploadedFileRepository uploadedFileRepository, UserRepository userRepository) {
        this.uploadedFileRepository = uploadedFileRepository;
        this.userRepository = userRepository;
    }

    private List<User> getUsersByIds(List<String> userIds) {
        return userRepository.findAllById(userIds);
    }

    private UploadedFile accessFile(String fileId, User Owner)throws Exception{
        Optional<UploadedFile> uploadedFile = uploadedFileRepository.findById(fileId);
        if(uploadedFile.isEmpty()) {
            throw new Exception("File does not exist");
        }
        UploadedFile file = uploadedFile.get();
        if(!file.getOwner().getUserId().equals(Owner.getUserId())) {
            throw new Exception("Unauthorized to change access for this file");
        }
        return file;
    }


    public List<User> getFileAccessList(String fileId, User Owner) throws Exception{
        UploadedFile fileAccessed = accessFile(fileId, Owner);
        return fileAccessed.getAccessList().stream().toList();
    }


    public void giveFileAccess(String fileId, User Owner, List<String> addAccessList) throws Exception{
        UploadedFile fileAccessed = accessFile(fileId, Owner);
        Set<User> currentAccessList = fileAccessed.getAccessList();
        currentAccessList.addAll(getUsersByIds(addAccessList));
        if(!currentAccessList.isEmpty() && fileAccessed.getFileAccess() == FileAccess.PRIVATE){
            fileAccessed.setFileAccess(FileAccess.SHARED);
        }
        fileAccessed.setAccessList(currentAccessList);
        uploadedFileRepository.save(fileAccessed);
    }

    public void revokeFileAccess(String fileId, User Owner, List<String> removeAccessList) throws Exception{
        UploadedFile fileAccessed = accessFile(fileId, Owner);
        Set<User> currentAccessList = fileAccessed.getAccessList();
        List<User> removeAccessListUsers = getUsersByIds(removeAccessList);
        removeAccessListUsers.forEach(currentAccessList::remove);
        if(currentAccessList.isEmpty() && fileAccessed.getFileAccess() == FileAccess.SHARED){
            fileAccessed.setFileAccess(FileAccess.PRIVATE);
        }
        fileAccessed.setAccessList(currentAccessList);
        uploadedFileRepository.save(fileAccessed);
    }


}
