package com.treino_abc_backend.controller;

import com.treino_abc_backend.service.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImagem(@RequestParam("foto") MultipartFile file) {
        try {
            String caminho = uploadService.salvarImagem(file);
            String url = "http://18.222.56.92:8080" + caminho;
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erro ao salvar imagem: " + e.getMessage());
        }
    }
}
