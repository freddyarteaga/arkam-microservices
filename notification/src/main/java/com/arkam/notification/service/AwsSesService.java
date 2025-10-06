package com.arkam.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AwsSesService {

    private final SesClient sesClient;

    @Value("${app.aws.ses.from-email}")
    private String fromEmail;

    public String sendEmail(String to, String subject, String body) {
        log.info("Enviando correo electrónico a: {}", to);

        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .source(fromEmail)
                .destination(Destination.builder().toAddresses(to).build())
                .message(Message.builder()
                        .subject(Content.builder().data(subject).build())
                        .body(Body.builder().text(Content.builder().data(body).build()).build())
                        .build())
                .build();

        SendEmailResponse response = sesClient.sendEmail(sendEmailRequest);

        log.info("Correo electrónico enviado exitosamente con ID de mensaje: {}", response.messageId());
        return response.messageId();
    }

    // Optional: method for HTML body or attachments, but for simplicity, basic text
}