package com.arkam.notification.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailRequest {

    @NotBlank(message = "El correo electrónico del destinatario es obligatorio")
    @Email(message = "Formato de correo electrónico inválido")
    private String to;

    @NotBlank(message = "El asunto es obligatorio")
    @Size(max = 100, message = "El asunto no debe exceder 100 caracteres")
    private String subject;

    @NotBlank(message = "El cuerpo es obligatorio")
    private String body;

    private List<String> attachments; // Optional list of attachment file paths or S3 keys
}