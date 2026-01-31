# Teste IntuitiveCare 2026 - Estágio

Projeto técnico desenvolvido para o processo seletivo da **IntuitiveCare**, com foco em integração de dados, processamento ETL, modelagem de banco de dados e boas práticas de desenvolvimento backend em Java.

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-green)
![License](https://img.shields.io/badge/License-MIT-green)

---


## Sobre o Projeto


Este projeto implementa, de forma incremental, as etapas propostas no teste técnico da IntuitiveCare, priorizando organização, clareza de responsabilidades e boas práticas de desenvolvimento backend.


1. **Integração com APIs** - Consumo da API de Dados Abertos da ANS
2. **Processamento de Dados** - ETL, validação e transformação de arquivos
3. **Banco de Dados** - Modelagem, queries SQL e análises
4. **Desenvolvimento Web** - API REST e interface Vue.js

---

## Testes Implementados

### Teste 1 – Integração com API Pública
**Objetivo:**  
Consumir a API de Dados Abertos da ANS, identificar automaticamente os últimos
trimestres disponíveis, processar os arquivos de despesas e consolidar os dados
em um único arquivo CSV.

 Código-fonte:  
`src/main/java/br/com/intuitive/care/**/teste1`

 Documentação detalhada:  
`src/main/java/br/com/intuitive/care/TESTE_1.md`

---

### Teste 2 – Validação, Enriquecimento e Agregação
**Objetivo:**  
Validar os dados consolidados no Teste 1, enriquecer com informações cadastrais
das operadoras e gerar métricas agregadas para análise estatística.

 Código-fonte:  
`src/main/java/br/com/intuitive/care/**/teste2`

 Documentação detalhada:  
`src/main/java/br/com/intuitive/care/TESTE_2.md`

---

### Teste 3 – Banco de Dados e Análise SQL
**Objetivo:**  
Modelar a estrutura do banco de dados, definir tipos adequados, índices e
desenvolver queries analíticas conforme solicitado no teste técnico.

 Scripts e documentação:  
`database/mysql/teste3/`

 Documentação detalhada:  
`database/mysql/teste3/README.md`

---

### Teste 4 – API e Interface Web
**Objetivo:**  
Desenvolver uma API REST e uma interface web para consulta e visualização dos
dados processados.

 Status: **não iniciado** (escopo documentado e planejado conforme especificação do teste)

---

## Testes Unitários

O projeto conta com **testes unitários automatizados**, focados principalmente nas
regras críticas de validação implementadas no Teste 2.

- Validação de CNPJ
- Validação de razão social
- Validação de valores positivos

Os testes foram desenvolvidos utilizando **JUnit 5**, garantindo maior confiabilidade
no pipeline de processamento de dados.


Para executar todos os testes unitários do projeto, utilize o comando abaixo:
```bash
mvn test
```
---

## Tecnologias Utilizadas

### Backend
- **Java 17**
- **Spring Boot 3**
- **Maven**
- **MySQL 8.0**
- **JUnit 5**

### Ferramentas
- **IntelliJ IDEA**
- **Git / GitHub**
- **Postman** (para testes futuros de API)

---

## Pré-requisitos

Antes de executar o projeto, certifique-se de ter instalado:

- [x] **JDK 17 ou superior** - [Download](https://www.oracle.com/java/technologies/downloads/)
- [x] **Maven 3.8+** - [Download](https://maven.apache.org/download.cgi)
- [x] **MySQL 8.0** - [Download](https://dev.mysql.com/downloads/)
- [x] **Git** - [Download](https://git-scm.com/downloads)

### Verificar Instalação

```bash
java -version   
mvn -version    
mysql --version
git --version    
```
---
## Como Executar o Projeto

1. Clone o repositório:
```bash
git clone https://github.com/Eduardolimzz/teste-intuitive-care.git
cd teste-intuitive-care
```

2. Execute os testes:
```bash
mvn test
```

3. Execute a aplicação (quando aplicável):
```bash
mvn spring-boot:run
```

---

## Estrutura do Projeto

```
teste-intuitive-care/
├── src/main/java/br/com/intuitive/care/
│   ├── config/
│   ├── controller/
│   ├── model/
│   ├── service/
│   ├── TESTE_1.md
│   └── TESTE_2.md
│
├── database/mysql/teste3/
│   ├── ddl/
│   ├── dml/
│   ├── queries/
│   └── README.md
│
├── data/
│   ├── raw/
│   ├── processed/
│   └── output/
│
├── src/test/
│   └── java/
│
└── README.md
```
---
## Trade-offs Técnicos

### Processamento dos arquivos

- **Estratégia escolhida:** processamento incremental
- **Justificativa:** evita alto consumo de memória considerando o volume de dados e possíveis variações na estrutura dos arquivos disponibilizados pela ANS.

### Tratamento de inconsistências

- **Valores zerados ou negativos:** mantidos no dataset para análise, sem descarte automático, preservando a integridade dos dados originais.
- **CNPJs duplicados:** consolidação realizada por trimestre, mantendo a rastreabilidade das informações.
- **Variações de formato:** normalização aplicada durante o parsing dos dados para garantir consistência no arquivo final.

As escolhas priorizaram **simplicidade, legibilidade e resiliência**, seguindo o princípio **KISS (Keep It Simple)**.

---
## Contato

Em caso de dúvidas ou interesse em discutir o projeto:

-  Email: **eduardoaluno1800@gmail.com**
-  LinkedIn: https://www.linkedin.com/in/eduardo-lima-dos-santos-3b1092316/

---

## License

[MIT](LICENSE) © Eduardo Lima
