package com.treino_abc_backend.controller;

import com.treino_abc_backend.service.UploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api")
public class UploadController {

    private final UploadService uploadService;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    // 📤 Upload da imagem do aluno
    @PostMapping("/upload/{id}")
    public ResponseEntity<String> uploadImagem(@RequestParam("foto") MultipartFile file, @PathVariable("id") String id) {
        try {
            String nomeArquivo = uploadService.salvarImagem(file, id);
            String url = baseUrl + "/api/uploads/" + nomeArquivo;
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao salvar imagem: " + e.getMessage());
        }
    }

    // 🖼️ Retorna a imagem para exibição no avatar
    @GetMapping("/uploads/{filename}")
    public ResponseEntity<Resource> getImagem(@PathVariable("filename") String filename) {
        try {
            Path caminho = Paths.get("uploads").resolve(filename);
            Resource imagem = new UrlResource(caminho.toUri());

            if (imagem.exists() && imagem.isReadable()) {
                // Detecta extensão para definir tipo MIME corretamente
                MediaType tipoImagem = filename.toLowerCase().endsWith(".png")
                        ? MediaType.IMAGE_PNG
                        : MediaType.IMAGE_JPEG;

                return ResponseEntity.ok()
                        .contentType(tipoImagem)
                        .body(imagem);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
