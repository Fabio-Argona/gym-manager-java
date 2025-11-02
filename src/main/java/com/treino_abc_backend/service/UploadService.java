package com.treino_abc_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

@Service
public class UploadService {

    private static final String UPLOAD_DIR = "uploads/";

    public String salvarImagem(MultipartFile file, String alunoId) throws IOException {
        String tipo = file.getContentType();

        if (tipo == null || (!tipo.startsWith("image/") && !tipo.equals("application/octet-stream"))) {
            throw new IOException("Tipo de arquivo não suportado: " + tipo);
        }

        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String extensao = getExtensao(Objects.requireNonNull(file.getOriginalFilename()));
        String nomeArquivo = alunoId + "." + extensao;
        Path destino = uploadPath.resolve(nomeArquivo);

        Files.copy(file.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);

        return nomeArquivo;
    }

    private String getExtensao(String nomeArquivo) {
        int ponto = nomeArquivo.lastIndexOf('.');
        return (ponto > 0) ? nomeArquivo.substring(ponto + 1) : "jpg";
    }
}
