package com.treino_abc_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class UploadService {

    private static final String UPLOAD_DIR = "uploads/";

    public String salvarImagem(MultipartFile file) throws IOException {
        // Valida tipo de arquivo
        String tipo = file.getContentType();
        if (tipo == null || !tipo.startsWith("image/")) {
            throw new IOException("Tipo de arquivo não suportado: " + tipo);
        }

        // Cria diretório se não existir
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String extensao = getExtensao(file.getOriginalFilename());
        String nomeUnico = UUID.randomUUID().toString() + "." + extensao;
        Path destino = uploadPath.resolve(nomeUnico);

        Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        return "/uploads/" + nomeUnico;
    }

    private String getExtensao(String nomeArquivo) {
        int ponto = nomeArquivo.lastIndexOf('.');
        return (ponto > 0) ? nomeArquivo.substring(ponto + 1) : "jpg";
    }
}
