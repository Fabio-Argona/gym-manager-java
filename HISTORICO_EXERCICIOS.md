# Backend - Histórico de Exercícios Realizados

## ✅ O que foi implementado

Seu backend agora está **completamente preparado** para registrar e consultar histórico de exercícios realizados por dia, com suporte para gráficos futuros.

### 1. **DTOs Criados**

#### `ExercicioRealizadoDTO.java`
- Representa um exercício que foi realizado
- Campos: id, exercicioId, nomeExercicio, seriesRealizadas, repeticoesRealizadas, pesoUtilizado, observacoes, dataSessao, criadoEm

#### `RegistrarExercicioDTO.java`
- DTO para o frontend enviar dados ao registrar um exercício
- Recebe: treino_realizado_id, exercicio_id, series_realizadas, repeticoes_realizadas, peso_utilizado, observacoes, data_sessao

### 2. **Repository Criado**

#### `ExercicioRealizadoRepository.java`
Métodos disponíveis:
- `findByAlunoIdAndData()` - Busca exercícios de um aluno em uma data específica
- `findByAlunoIdAndDataRange()` - Busca exercícios em um período (últimos 30 dias, etc)
- `findByAlunoIdAndExercicioId()` - Histórico completo de um exercício
- `findProgressao()` - Dados ordenados para gráficos de evolução (peso, séries, reps)
- `findByTreinoRealizadoId()` - Todos os exercícios de uma sessão

### 3. **Service Criado**

#### `ExercicioRealizadoService.java`
Métodos disponíveis:
- `registrarExercicio()` - Registra um exercício realizado
- `buscarPorData()` - Todos os exercícios de um aluno em um dia
- `buscarPorPeriodo()` - Exercícios em um intervalo de datas
- `buscarProgressaoExercicio()` - Dados para gráficos de um exercício
- `buscarHistoricoExercicio()` - Histórico completo de um exercício
- `buscarPorSessao()` - Exercícios de uma sessão de treino
- `atualizar()` - Edita um exercício realizado
- `deletar()` - Remove um exercício realizado

### 4. **Controller Criado**

#### `ExercicioRealizadoController.java`

**Endpoints disponíveis:**

```
POST /exercicios-realizados
Body: {
  "treino_realizado_id": "UUID",
  "exercicio_id": "UUID",
  "series_realizadas": 3,
  "repeticoes_realizadas": 10,
  "peso_utilizado": 20.5,
  "observacoes": "Sentia bem",
  "data_sessao": "2025-02-04"
}
Response: ExercicioRealizadoDTO
```

```
GET /exercicios-realizados?aluno-id=UUID&data=2025-02-04
Response: List<ExercicioRealizadoDTO>
```

```
GET /exercicios-realizados/periodo?aluno-id=UUID&data-inicio=2025-02-01&data-fim=2025-02-28
Response: List<ExercicioRealizadoDTO> (para gráficos)
```

```
GET /exercicios-realizados/progressao?aluno-id=UUID&exercicio-id=UUID&data-inicio=2025-02-01&data-fim=2025-02-28
Response: List<ExercicioRealizadoDTO> (ordenado por data para gráficos)
```

```
GET /exercicios-realizados/historico?aluno-id=UUID&exercicio-id=UUID
Response: List<ExercicioRealizadoDTO> (histórico completo do exercício)
```

```
GET /exercicios-realizados/sessao/{treino-realizado-id}
Response: List<ExercicioRealizadoDTO>
```

```
PUT /exercicios-realizados/{id}
Body: RegistrarExercicioDTO
Response: ExercicioRealizadoDTO
```

```
DELETE /exercicios-realizados/{id}
Response: 204 No Content
```

### 5. **Melhorias no TreinoRealizadoService**

- Agora evita duplicação de sessões no mesmo dia
- Novos métodos: `obterSessao()`, `buscarSessoesPorAluno()`

---

## 🔄 Fluxo de Uso (Frontend -> Backend)

### 1️⃣ **Ativar um Treino (Criar Sessão)**
```
POST /treinos/realizado/{treinoId}?data=2025-02-04
Response: TreinoRealizado { id: "uuid-sessao", treino: {...}, data: "2025-02-04" }
```

### 2️⃣ **Clicar em um Exercício e Registrar**
```
POST /exercicios-realizados
{
  "treino_realizado_id": "uuid-sessao",
  "exercicio_id": "uuid-exercicio",
  "series_realizadas": 3,
  "repeticoes_realizadas": 10,
  "peso_utilizado": 20.5,
  "observacoes": "Consegui fazer com facilidade",
  "data_sessao": "2025-02-04"
}
```

### 3️⃣ **Consultar Histórico (Para Gráficos)**
```
GET /exercicios-realizados/progressao?aluno-id=UUID&exercicio-id=UUID&data-inicio=2025-01-01&data-fim=2025-02-28
```
Retorna: Lista ordenada por data com peso, séries e reps de cada sessão

---

## 📊 Exemplos de Dados para Gráficos

Com o endpoint `/progressao`, você tem dados assim:
```json
[
  {
    "id": "uuid1",
    "nomeExercicio": "Supino Reto",
    "dataSessao": "2025-01-15",
    "seriesRealizadas": 3,
    "repeticoesRealizadas": 8,
    "pesoUtilizado": 40.0
  },
  {
    "id": "uuid2",
    "nomeExercicio": "Supino Reto",
    "dataSessao": "2025-01-22",
    "seriesRealizadas": 3,
    "repeticoesRealizadas": 10,
    "pesoUtilizado": 42.5
  },
  {
    "id": "uuid3",
    "nomeExercicio": "Supino Reto",
    "dataSessao": "2025-02-01",
    "seriesRealizadas": 3,
    "repeticoesRealizadas": 12,
    "pesoUtilizado": 45.0
  }
]
```

Perfeito para criar gráficos de **Peso vs Data**, **Reps vs Data**, **Séries vs Data**

---

## 🎯 Próximos Passos Opcionais

1. **Adicionar validações** no Controller (verificar se peso é positivo, etc)
2. **Filtros avançados** no repository (por exercício, por intervalo de peso, etc)
3. **Melhorias de segurança** (verificar se o aluno tem acesso àquele treino)
4. **Endpoints de estatísticas** (melhor série, peso máximo, média de reps, etc)

---

## ⚠️ Importante

Certifique-se de que o banco de dados tem a tabela `exercicio_realizado` criada com todos os campos. Se não tiver migrations, execute as queries ou use Liquibase/Flyway.
