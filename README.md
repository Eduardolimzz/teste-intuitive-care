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

## 🚀 Instalação

### 1. Clonar o Repositório

```bash
git clone https://github.com/Eduardolimzz/teste-intuitive-care.git
cd teste-intuitive-care
```

### 2. Configurar o Banco de Dados

```sql
CREATE DATABASE intuitive_care;

CREATE USER 'intuitive_user'@'localhost'
IDENTIFIED BY 'intuitive123';

GRANT ALL PRIVILEGES ON intuitive_care.*
TO 'intuitive_user'@'localhost';

FLUSH PRIVILEGES;
```

### 3. Configurar application.properties

Editar `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/intuitive_care?useSSL=false&serverTimezone=UTC
spring.datasource.username=intuitive_user
spring.datasource.password=intuitive123
```

### 4. Instalar Dependências Maven

```bash
mvn clean install
mvn spring-boot:run
```
### A aplicação ficará disponível em:

```bash
http://localhost:8080
```

### Swagger (quando houver controllers):

```bash
http://localhost:8080/swagger-ui.html
```

---

## 📁 Estrutura do Projeto

```
teste-intuitive-care/
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/
│
├── .gitignore
├── pom.xml
└── README.md
```

---

## 👤 Autor

**Eduardo Lima dos Santos**
- GitHub: [@Eduardolimzz](https://github.com/Eduardolimzz)
- LinkedIn: [Eduardo Lima dos Santos](https://www.linkedin.com/in/eduardo-lima-dos-santos-3b1092316/)
- Email: eduardoaluno1800@gmail.com

---

## 📄 Observações

Este projeto faz parte de um processo seletivo e está sendo desenvolvido de forma incremental, priorizando boas práticas, clareza de código e organização.

---


**Desenvolvido com ☕ e 💙 para IntuitiveCare**
