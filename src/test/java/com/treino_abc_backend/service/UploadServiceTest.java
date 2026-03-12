package com.treino_abc_backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class UploadServiceTest {
    private UploadService uploadService;

    @BeforeEach
    void setUp() {
        uploadService = new UploadService();
    }

    @Test
    void salvarImagem_sucesso() throws IOException {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getContentType()).thenReturn("image/png");
        Mockito.when(file.getOriginalFilename()).thenReturn("foto.png");
        Mockito.when(file.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(new byte[]{1,2,3}));

        String nome = uploadService.salvarImagem(file, "123");
        assertTrue(nome.startsWith("123."));
    }

    @Test
    void salvarImagem_tipoNaoSuportado() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getContentType()).thenReturn("text/plain");
        Mockito.when(file.getOriginalFilename()).thenReturn("arquivo.txt");
        assertThrows(IOException.class, () -> uploadService.salvarImagem(file, "123"));
    }

    @Test
    void salvarImagem_semTipo() {
        MultipartFile file = Mockito.mock(MultipartFile.class);
        Mockito.when(file.getContentType()).thenReturn(null);
        Mockito.when(file.getOriginalFilename()).thenReturn("arquivo.txt");
        assertThrows(IOException.class, () -> uploadService.salvarImagem(file, "123"));
    }
}
