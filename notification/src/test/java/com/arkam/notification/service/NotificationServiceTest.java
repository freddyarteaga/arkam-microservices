package com.arkam.notification.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private AwsSesService awsSesService;

    @Mock
    private AwsS3Service awsS3Service;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void sendEmail_shouldCallAwsSesService() {
        when(awsSesService.sendEmail(anyString(), anyString(), anyString())).thenReturn("messageId");

        String result = notificationService.sendEmail("to@example.com", "subject", "body");

        verify(awsSesService).sendEmail("to@example.com", "subject", "body");
        assertEquals("messageId", result);
    }

    @Test
    void uploadTemplate_shouldCallAwsS3Service() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(awsS3Service.uploadTemplate(anyString(), any(MultipartFile.class))).thenReturn("key");

        String result = notificationService.uploadTemplate("fileName", file);

        verify(awsS3Service).uploadTemplate("fileName", file);
        assertEquals("key", result);
    }

    @Test
    void getTemplates_shouldCallAwsS3Service() {
        when(awsS3Service.listTemplates()).thenReturn(List.of("template1", "template2"));

        List<String> result = notificationService.getTemplates();

        verify(awsS3Service).listTemplates();
        assertEquals(2, result.size());
    }

    @Test
    void deleteTemplate_shouldCallAwsS3Service() {
        doNothing().when(awsS3Service).deleteTemplate(anyString());

        notificationService.deleteTemplate("id");

        verify(awsS3Service).deleteTemplate("id");
    }
}