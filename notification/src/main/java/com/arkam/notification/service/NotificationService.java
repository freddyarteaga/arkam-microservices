package com.arkam.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final AwsSesService awsSesService;
    private final AwsS3Service awsS3Service;

    public String sendEmail(String to, String subject, String body) {
        log.info("Enviando correo electrónico a través del servicio");
        return awsSesService.sendEmail(to, subject, body);
    }

    public String uploadTemplate(String fileName, MultipartFile file) throws IOException {
        log.info("Subiendo plantilla: {}", fileName);
        return awsS3Service.uploadTemplate(fileName, file);
    }

    public List<String> getTemplates() {
        log.info("Recuperando lista de plantillas");
        return awsS3Service.listTemplates();
    }

    public void deleteTemplate(String id) {
        log.info("Eliminando plantilla: {}", id);
        awsS3Service.deleteTemplate(id);
    }
}