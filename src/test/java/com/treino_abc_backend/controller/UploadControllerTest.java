package com.treino_abc_backend.controller;

import com.treino_abc_backend.service.UploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UploadControllerTest {

    @Mock
    private UploadService uploadService;

    @InjectMocks
    private UploadController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadImagem_sucesso() throws Exception {
        MultipartFile file = new MockMultipartFile("foto", "foto.jpg", "image/jpeg", new byte[]{1, 2, 3});
        when(uploadService.salvarImagem(file, "123")).thenReturn("123.jpg");
        ResponseEntity<String> response = controller.uploadImagem(file, "123");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("/api/uploads/123.jpg"));
        verify(uploadService).salvarImagem(file, "123");
    }

    @Test
    void uploadImagem_erro() throws Exception {
        MultipartFile file = new MockMultipartFile("foto", "foto.jpg", "image/jpeg", new byte[]{1, 2, 3});
        when(uploadService.salvarImagem(file, "123")).thenThrow(new IOException("Falha ao salvar"));
        ResponseEntity<String> response = controller.uploadImagem(file, "123");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Erro ao salvar imagem"));
        verify(uploadService).salvarImagem(file, "123");
    }
}
