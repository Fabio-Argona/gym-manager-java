package com.treino_abc_backend.controller;

import com.treino_abc_backend.dto.IaMensagemRequestDTO;
import com.treino_abc_backend.dto.IaRespostaDTO;
import com.treino_abc_backend.security.JwtUtil;
import com.treino_abc_backend.service.IaCoachService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IaCoachControllerTest {

    @Mock
    private IaCoachService iaCoachService;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private IaCoachController controller;

    private UUID alunoId;
    private IaMensagemRequestDTO mensagemRequest;
    private IaRespostaDTO respostaDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        alunoId = UUID.randomUUID();
        mensagemRequest = new IaMensagemRequestDTO();
        mensagemRequest.setAlunoId(alunoId);
        mensagemRequest.setMensagem("Olá IA Coach!");
        respostaDTO = new IaRespostaDTO();
    }

    @Test
    void iniciarConversa_tokenValido_semTreinos() {
        String token = "Bearer valid-token";
        when(iaCoachService.verificarInicioAluno(alunoId)).thenReturn(respostaDTO);
        ResponseEntity<?> response = controller.iniciarConversa(token, alunoId);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(respostaDTO, response.getBody());
        verify(iaCoachService).verificarInicioAluno(alunoId);
    }

    @Test
    void iniciarConversa_tokenValido_comTreinos() {
        String token = "Bearer valid-token";
        when(iaCoachService.verificarInicioAluno(alunoId)).thenReturn(null);
        when(iaCoachService.gerarAnalise(alunoId)).thenReturn(respostaDTO);
        ResponseEntity<?> response = controller.iniciarConversa(token, alunoId);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(respostaDTO, response.getBody());
        verify(iaCoachService).verificarInicioAluno(alunoId);
        verify(iaCoachService).gerarAnalise(alunoId);
    }

    @Test
    void iniciarConversa_tokenInvalido() {
        String token = "invalid-token";
        ResponseEntity<?> response = controller.iniciarConversa(token, alunoId);
        assertEquals(401, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("erro"));
    }

    @Test
    void iniciarConversa_exception() {
        String token = "Bearer valid-token";
        when(iaCoachService.verificarInicioAluno(alunoId)).thenThrow(new RuntimeException("Erro"));
        ResponseEntity<?> response = controller.iniciarConversa(token, alunoId);
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("erro"));
    }

    @Test
    void chat_tokenValido_mensagemValida() {
        String token = "Bearer valid-token";
        when(iaCoachService.processarMensagem(alunoId, "Olá IA Coach!")).thenReturn(respostaDTO);
        ResponseEntity<?> response = controller.chat(token, mensagemRequest);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(respostaDTO, response.getBody());
        verify(iaCoachService).processarMensagem(alunoId, "Olá IA Coach!");
    }

    @Test
    void chat_tokenInvalido() {
        String token = "invalid-token";
        ResponseEntity<?> response = controller.chat(token, mensagemRequest);
        assertEquals(401, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("erro"));
    }

    @Test
    void chat_alunoIdNulo() {
        String token = "Bearer valid-token";
        mensagemRequest.setAlunoId(null);
        ResponseEntity<?> response = controller.chat(token, mensagemRequest);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("erro"));
    }

    @Test
    void chat_mensagemVazia() {
        String token = "Bearer valid-token";
        mensagemRequest.setMensagem("");
        ResponseEntity<?> response = controller.chat(token, mensagemRequest);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("erro"));
    }

    @Test
    void chat_exception() {
        String token = "Bearer valid-token";
        when(iaCoachService.processarMensagem(alunoId, "Olá IA Coach!")).thenThrow(new RuntimeException("Erro"));
        ResponseEntity<?> response = controller.chat(token, mensagemRequest);
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("erro"));
    }

    @Test
    void gerarAnalise_tokenValido() {
        String token = "Bearer valid-token";
        when(iaCoachService.gerarAnalise(alunoId)).thenReturn(respostaDTO);
        ResponseEntity<?> response = controller.gerarAnalise(token, alunoId);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(respostaDTO, response.getBody());
        verify(iaCoachService).gerarAnalise(alunoId);
    }

    @Test
    void gerarAnalise_tokenInvalido() {
        String token = "invalid-token";
        ResponseEntity<?> response = controller.gerarAnalise(token, alunoId);
        assertEquals(401, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("erro"));
    }

    @Test
    void gerarAnalise_exception() {
        String token = "Bearer valid-token";
        when(iaCoachService.gerarAnalise(alunoId)).thenThrow(new RuntimeException("Erro"));
        ResponseEntity<?> response = controller.gerarAnalise(token, alunoId);
        assertEquals(500, response.getStatusCodeValue());
        assertTrue(((Map<?, ?>) response.getBody()).containsKey("erro"));
    }

    @Test
    void status_deveRetornarOnline() {
        ResponseEntity<Map<String, String>> response = controller.status();
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("IA Coach online", response.getBody().get("status"));
    }
}
