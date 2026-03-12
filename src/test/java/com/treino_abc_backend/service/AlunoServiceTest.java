package com.treino_abc_backend.service;

import com.treino_abc_backend.dto.AlunoDTO;
import com.treino_abc_backend.dto.AlunoUpdateDTO;
import com.treino_abc_backend.entity.Aluno;
import com.treino_abc_backend.repository.AlunoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AlunoServiceTest {
    @Mock
    private AlunoRepository alunoRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AlunoService alunoService;

    private UUID id;
    private Aluno aluno;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        id = UUID.randomUUID();
        aluno = new Aluno();
        aluno.setId(id);
        aluno.setNome("Teste");
        aluno.setLogin("login");
        aluno.setPassword("senha");
        aluno.setDataNascimento(LocalDate.of(2000, 1, 1));
    }

    @Test
    void buscarPorId_sucesso() {
        when(alunoRepository.findById(id)).thenReturn(Optional.of(aluno));
        AlunoDTO dto = alunoService.buscarPorId(id);
        assertEquals(aluno.getId(), dto.getId());
        assertEquals(aluno.getNome(), dto.getNome());
        verify(alunoRepository).findById(id);
    }

    @Test
    void buscarPorId_erro() {
        when(alunoRepository.findById(id)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> alunoService.buscarPorId(id));
        assertTrue(ex.getMessage().contains("Aluno não encontrado"));
        verify(alunoRepository).findById(id);
    }

    @Test
    void atualizarCompleto_sucesso() {
        AlunoUpdateDTO updateDTO = new AlunoUpdateDTO();
        updateDTO.setNome("Novo Nome");
        updateDTO.setLogin("novoLogin");
        updateDTO.setPassword("novaSenha");
        updateDTO.setDataNascimento(LocalDate.of(1999, 12, 31));
        when(alunoRepository.findById(id)).thenReturn(Optional.of(aluno));
        when(passwordEncoder.encode("novaSenha")).thenReturn("senhaCodificada");
        when(alunoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        AlunoDTO dto = alunoService.atualizarCompleto(id, updateDTO);
        assertEquals("Novo Nome", dto.getNome());
        assertEquals("novoLogin", dto.getLogin());
        assertEquals(LocalDate.of(1999, 12, 31), dto.getDataNascimento());
        verify(alunoRepository).findById(id);
        verify(passwordEncoder).encode("novaSenha");
        verify(alunoRepository).save(any());
    }

    @Test
    void atualizarCompleto_erro() {
        AlunoUpdateDTO updateDTO = new AlunoUpdateDTO();
        when(alunoRepository.findById(id)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> alunoService.atualizarCompleto(id, updateDTO));
        assertTrue(ex.getMessage().contains("Aluno não encontrado"));
        verify(alunoRepository).findById(id);
    }

    @Test
    void atualizarFisico_sucesso() {
        Map<String, Object> body = Map.of("sexo", "Masculino", "altura", 1.80);
        when(alunoRepository.findById(id)).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        AlunoDTO dto = alunoService.atualizarFisico(id, body);
        assertEquals("Masculino", dto.getSexo());
        assertEquals(1.80, dto.getAltura());
        verify(alunoRepository).findById(id);
        verify(alunoRepository).save(any());
    }

    @Test
    void atualizarFisico_erro() {
        Map<String, Object> body = Map.of("sexo", "Masculino", "altura", 1.80);
        when(alunoRepository.findById(id)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> alunoService.atualizarFisico(id, body));
        assertTrue(ex.getMessage().contains("Aluno não encontrado"));
        verify(alunoRepository).findById(id);
    }

    @Test
    void atualizarObjetivo_sucesso() {
        when(alunoRepository.findById(id)).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        AlunoDTO dto = alunoService.atualizarObjetivo(id, "Emagrecer", "Avançado");
        assertEquals("Emagrecer", dto.getObjetivo());
        assertEquals("Avançado", dto.getNivelTreinamento());
        verify(alunoRepository).findById(id);
        verify(alunoRepository).save(any());
    }

    @Test
    void atualizarObjetivo_erro() {
        when(alunoRepository.findById(id)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () -> alunoService.atualizarObjetivo(id, "Emagrecer", "Avançado"));
        assertTrue(ex.getMessage().contains("Aluno não encontrado"));
        verify(alunoRepository).findById(id);
    }

    @Test
    void toDTO_deveConverterAluno() {
        AlunoDTO dto = alunoService.toDTO(aluno);
        assertEquals(aluno.getId(), dto.getId());
        assertEquals(aluno.getNome(), dto.getNome());
        assertEquals(aluno.getLogin(), dto.getLogin());
        assertEquals(aluno.getDataNascimento(), dto.getDataNascimento());
    }
}
