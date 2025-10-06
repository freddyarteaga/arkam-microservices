package com.arkam.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsS3Service {

    private final S3Client s3Client;

    @Value("${app.aws.s3.bucket}")
    private String bucketName;

    public String uploadTemplate(String key, MultipartFile file) throws IOException {
        log.info("Subiendo plantilla a S3: {}", key);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        log.info("Plantilla subida exitosamente: {}", key);
        return key;
    }

    public List<String> listTemplates() {
        log.info("Listando plantillas del bucket S3: {}", bucketName);

        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);

        List<String> keys = listObjectsResponse.contents().stream()
                .map(S3Object::key)
                .collect(Collectors.toList());

        log.info("Encontradas {} plantillas", keys.size());
        return keys;
    }

    public void deleteTemplate(String key) {
        log.info("Eliminando plantilla de S3: {}", key);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);

        log.info("Plantilla eliminada exitosamente: {}", key);
    }

    public byte[] downloadTemplate(String key) {
        log.info("Descargando plantilla de S3: {}", key);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        return s3Client.getObjectAsBytes(getObjectRequest).asByteArray();
    }
}