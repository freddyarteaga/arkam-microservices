package com.arkam.notification.controller;

import com.arkam.notification.dto.request.SendEmailRequest;
import com.arkam.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/send-email")
    @Operation(summary = "Enviar correo electrónico a través de SES")
    public ResponseEntity<Map<String, String>> sendEmail(@Valid @RequestBody SendEmailRequest request) {
        String messageId = notificationService.sendEmail(request.getTo(), request.getSubject(), request.getBody());
        return ResponseEntity.ok(Map.of("messageId", messageId));
    }

    @PostMapping(value = "/upload-template", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir plantilla de correo electrónico a S3")
    public ResponseEntity<Map<String, String>> uploadTemplate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileName") String fileName) throws IOException {
        String key = notificationService.uploadTemplate(fileName, file);
        return ResponseEntity.ok(Map.of("key", key));
    }

    @GetMapping("/templates")
    @Operation(summary = "Listar plantillas de correo electrónico desde S3")
    public ResponseEntity<List<String>> getTemplates() {
        List<String> templates = notificationService.getTemplates();
        return ResponseEntity.ok(templates);
    }

    @DeleteMapping("/templates/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar plantilla de correo electrónico de S3 (solo administradores)")
    public ResponseEntity<Void> deleteTemplate(@PathVariable String id) {
        notificationService.deleteTemplate(id);
        return ResponseEntity.noContent().build();
    }
}