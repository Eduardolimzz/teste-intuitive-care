# Teste 4 – API e Interface Web (Intuitive Care)

Este documento descreve a implementação do **Teste 4**, que consiste no
desenvolvimento de uma **API em Python (FastAPI)** integrada a um **frontend em Vue 3**,
utilizando os dados consolidados e agregados dos testes anteriores.

O objetivo deste README é permitir que qualquer pessoa consiga:
- Entender a arquitetura do projeto
- Executar o backend e o frontend localmente
- Testar todas as rotas da API
- Compreender as decisões técnicas adotadas

---

## Objetivo do Teste 4

O Teste 4 tem como objetivo:

- Disponibilizar uma **API REST** para consulta de operadoras de saúde
- Expor dados consolidados e estatísticas de despesas
- Implementar uma **interface web** para consumo da API
- Demonstrar organização de código, clareza e documentação

---

## Tecnologias Utilizadas

### Backend
- Python 3
- FastAPI
- SQLAlchemy
- MySQL
- Uvicorn

### Frontend
- Vue 3
- Vite
- Vue Router
- Axios
- Chart.js


---

## Backend – Funcionamento Geral

O backend foi desenvolvido utilizando **FastAPI**, com acesso a banco de dados MySQL
via **SQLAlchemy**.

Ele é responsável por:
- Listar operadoras com paginação e filtro
- Retornar detalhes de uma operadora
- Exibir histórico de despesas
- Calcular estatísticas agregadas
- Servir dados para gráficos no frontend

---

## Rotas Disponíveis na API

### Health Check
```bash
GET /api/health
```
Verifica se a API e o banco de dados estão funcionando corretamente.

---

### Listar Operadoras (com paginação e busca)
```bash
GET /api/operadoras?page=1&limit=10&busca=UNIMED
```

Parâmetros:
- `page`: página atual
- `limit`: quantidade de registros por página
- `busca`: filtro por CNPJ ou Razão Social (opcional)

---

### Detalhes da Operadora
```bash
GET /api/operadoras/{cnpj}
```


---

### Histórico de Despesas da Operadora
```bash
GET /api/operadoras/{cnpj}/despesas
```


---

### Estatísticas Gerais
```bash
GET /api/estatisticas
```


Retorna:
- Total de despesas
- Média de despesas
- Total de operadoras
- Top 5 operadoras por volume de despesas

Esta rota utiliza **cache em memória** com TTL de 5 minutos para reduzir custo de consultas.

---

### Despesas por UF
```bash
GET /api/despesas-por-uf
```


Utilizada no frontend para geração de gráficos.

---

## Importação de Dados

Antes de executar a API, é necessário importar os dados para o banco.

O script `import_data.py` realiza:
- Importação das operadoras
- Importação das despesas consolidadas
- Normalização de dados inconsistentes
- Criação automática das tabelas

---

## Como Executar o Backend

### 1. Criar e ativar o ambiente virtual

No Windows:

```bash
cd backend
python -m venv venv
.\venv\Scripts\Activate
```
---
### 2. Instalar dependências do backend
```bash
pip install -r requirements.txt
```

---

### 3. Configurar o arquivo .env

Exemplo:
```bash
DB_USER=root
DB_PASSWORD=senha
DB_HOST=localhost
DB_PORT=3306
DB_NAME=intuitive_care
```

---

### 4. Importar os dados
```bash
python -m app.scripts.import_data
```

---

### 5. Executar a API
```bash
uvicorn app.main:app --reload
```

A API ficará disponível em:
```bash
http://localhost:8000
```

---

## Frontend – Funcionamento Geral

**O frontend foi desenvolvido em Vue 3, utilizando Vite.**

Ele consome diretamente a API para:

- Exibir lista paginada de operadoras
- Visualizar detalhes de uma operadora
- Mostrar estatísticas gerais
- Renderizar gráficos de despesas por UF

---
## Como Executar o Frontend
### 1. Instalar dependências
```bash
cd frontend
npm install
```

---

### 2. Executar o servidor de desenvolvimento
```bash
npm run dev
```

O frontend ficará disponível em:
```bash
http://localhost:5173
```

---
## Comunicação Frontend ↔ Backend

### O frontend utiliza Axios, configurado em:
```bash
frontend/src/services/api.js
```

Base URL:
```bash
http://localhost:8000/api
```

---
## Coleção Postman
**A coleção do Postman contendo todas as rotas da API está disponível em:**
```bash
docs/Postman_Collection.json
```
Ela inclui exemplos de requisições e respostas para todas as rotas implementadas.