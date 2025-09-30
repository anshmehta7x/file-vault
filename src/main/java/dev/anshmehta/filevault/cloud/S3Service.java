package dev.anshmehta.filevault.cloud;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class S3Service {

    private final S3AsyncClient s3AsyncClient;

    @Value("${aws.s3.bucketname}")
    private String bucketName;

    public S3Service(S3AsyncClient s3AsyncClient) {
        this.s3AsyncClient = s3AsyncClient;
    }

    public CompletableFuture<String> uploadFile(MultipartFile file) throws IOException {
        String key = "uploads/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

        PutObjectRequest putObjectRequest = PutObjectRequest
                .builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        byte[] fileBytes;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            throw e;
        }

        CompletableFuture<PutObjectResponse> response = s3AsyncClient.putObject(
                putObjectRequest,
                AsyncRequestBody.fromBytes(fileBytes)
        );

        return response.handle((resp, ex) -> {
            if(ex != null) {
                throw new RuntimeException("Error uploading file to S3", ex);
            }

            String url = "https://" + bucketName + ".s3.amazonaws.com/" + key;

            return url;
        });
    }

    public CompletableFuture<Boolean> deleteFile(String url) {
        String keyPrefix = "https://" + bucketName + ".s3.amazonaws.com/";

        if (!url.contains(keyPrefix)) {
            return CompletableFuture.failedFuture(
                    new IllegalArgumentException("Invalid S3 URL: " + url)
            );
        }

        String extractedKey = url.substring(url.lastIndexOf(keyPrefix) + keyPrefix.length());

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest
                .builder()
                .bucket(bucketName)
                .key(extractedKey)
                .build();

        CompletableFuture<DeleteObjectResponse> response = s3AsyncClient.deleteObject(deleteObjectRequest);

        return response.handle((resp, ex) -> {
            if(ex != null) {
                throw new RuntimeException("Error deleting file from S3", ex);
            }

            return true;
        });
    }
}