package com.treino_abc_backend.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void enviar(String para, String assunto, String corpo) {
        System.out.println("Enviando para: " + para);
        System.out.println("Assunto: " + assunto);
        System.out.println("Corpo: " + corpo);
    }
}
