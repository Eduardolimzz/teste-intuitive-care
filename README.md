# Teste IntuitiveCare 2026 - Estágio

**Candidato:** Eduardo Lima dos Santos  
**Data:** Janeiro 2026  
**Linguagem:** Java 17  
**Framework:** Spring Boot 3.2.1

---

## 📋 Sumário

- [Sobre o Projeto](#-sobre-o-projeto)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Pré-requisitos](#-pré-requisitos)
- [Instalação](#-instalação)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Autor](#-autor)
- [Documentação](#documentação)
- [Trade-offs Técnicos](#trade-offs-técnicos)
- [Resultados](#resultados)

---

## 📖 Sobre o Projeto

O projeto está em **fase inicial**, com foco na configuração correta do ambiente, estrutura base do backend em Java e preparação para as próximas etapas do teste, que envolvem:

1. **Integração com APIs** - Consumo da API de Dados Abertos da ANS
2. **Processamento de Dados** - ETL, validação e transformação de arquivos
3. **Banco de Dados** - Modelagem, queries SQL e análises
4. **Desenvolvimento Web** - API REST e interface Vue.js

---

## 📌 Status dos Testes

- [x] **Teste 1 – Integração com API Pública**
- [ ] **Teste 2 – Transformação e Validação de Dados**
- [ ] **Teste 3 – Banco de Dados e Análise SQL**
- [ ] **Teste 4 – API e Interface Web**

---

## ✅ Teste 1 – Integração com API Pública

### 📎 Objetivo

Realizar a integração com a **API de Dados Abertos da ANS**, processar os arquivos de demonstrações contábeis e gerar um arquivo consolidado contendo as **despesas com eventos/sinistros** dos **últimos 3 trimestres disponíveis**.

---

### ⚙️ O que foi implementado

O Teste 1 contempla as seguintes etapas:

#### 1. Acesso à API da ANS
- Consumo da API pública da ANS:
```bash
https://dadosabertos.ans.gov.br/FTP/PDA/
```

- Identificação automática dos **últimos 3 trimestres disponíveis**
- Download dos arquivos compactados (ZIP), considerando variações de estrutura entre diretórios

#### 2. Processamento dos Arquivos
- Download automático dos arquivos ZIP
- Extração dos arquivos compactados
- Identificação e leitura apenas dos arquivos relacionados a **Despesas com Eventos/Sinistros**
- Normalização dos dados relevantes para um modelo único

#### 3. Consolidação dos Dados
- Consolidação das informações dos 3 trimestres em memória
- Geração de um arquivo CSV consolidado com as colunas:
- `CNPJ`
- `RazaoSocial`
- `Ano`
- `Trimestre`
- `ValorDespesas`

#### 4. Tratamento de Inconsistências
Durante o processamento, foram tratados os seguintes cenários:
- CNPJs duplicados com razões sociais diferentes
- Valores zerados ou negativos
- Variações no formato de trimestre/ano

As decisões de tratamento estão documentadas na seção de **Trade-offs Técnicos**.

#### 5. Resultado Final
- Geração do arquivo:
```bash
consolidado_despesas.csv
```

- Compactação do resultado final em:
```bash
consolidado_despesas.zip
```

---

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 17**
- **Spring Boot 3.2.1**
- **Maven**
- **Spring Web**
- **Spring Data JPA**
- **MySQL 8.0**
- **Lombok**
- **Springdoc OpenAPI (Swagger)**

### Ferramentas
- **IntelliJ IDEA**
- **Git / GitHub**
- **Postman** (para testes futuros de API)

---

## ✅ Pré-requisitos

Antes de executar o projeto, certifique-se de ter instalado:

- [x] **JDK 17 ou superior** - [Download](https://www.oracle.com/java/technologies/downloads/)
- [x] **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi)
- [x] **MySQL 8.0** - [Download](https://dev.mysql.com/downloads/)
- [x] **Node.js 18+ e npm** - [Download](https://nodejs.org/)
- [x] **Git** - [Download](https://git-scm.com/downloads)

### Verificar Instalação

```bash
java -version   
mvn -version    
mysql --version
git --version    
```

---

## 📁 Estrutura do Projeto

```
teste-intuitive-care/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/com/intuitive/care/
│   │   │       ├── config/
│   │   │       ├── controller/
│   │   │       ├── model/
│   │   │       └── service/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── data/
├── logs/
├── pom.xml
└── README.md

```
---
## 🧠 Trade-offs Técnicos

### Processamento dos arquivos

- **Estratégia escolhida:** processamento incremental
- **Justificativa:** evita alto consumo de memória considerando o volume de dados e possíveis variações na estrutura dos arquivos disponibilizados pela ANS.

### Tratamento de inconsistências

- **Valores zerados ou negativos:** mantidos no dataset para análise, sem descarte automático, preservando a integridade dos dados originais.
- **CNPJs duplicados:** consolidação realizada por trimestre, mantendo a rastreabilidade das informações.
- **Variações de formato:** normalização aplicada durante o parsing dos dados para garantir consistência no arquivo final.

As escolhas priorizaram **simplicidade, legibilidade e resiliência**, seguindo o princípio **KISS (Keep It Simple)**.

---
## 📄 Observações

Este projeto faz parte de um processo seletivo e está sendo desenvolvido de forma incremental, priorizando boas práticas, clareza de código e organização.

---

## Autor

<div align="center">
  <img src="https://github.com/Eduardolimzz.png" width="100px" style="border-radius: 50%">

**Eduardo Lima dos Santos**

[![GitHub](https://img.shields.io/badge/-GitHub-181717?style=flat&logo=github)](https://github.com/Eduardolimzz)
[![LinkedIn](https://img.shields.io/badge/-LinkedIn-0A66C2?style=flat&logo=linkedin)](https://www.linkedin.com/in/eduardo-lima-3b1092316/)

**Contato**: eduardoaluno1800@gmail.com
---

**Desenvolvido com ☕ e 💙 para IntuitiveCare**
