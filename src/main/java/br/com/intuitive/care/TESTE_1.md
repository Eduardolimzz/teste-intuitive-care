# Teste 1 – Integração com API Pública (ANS)

Este documento descreve em detalhes a implementação do **Teste 1**, responsável pela
integração com a API de Dados Abertos da ANS, processamento dos arquivos contábeis
e consolidação das despesas com eventos/sinistros.

O objetivo deste README é permitir que qualquer pessoa consiga **entender o fluxo
completo do Teste 1 apenas pela leitura**, e em seguida **navegar pelo código com clareza**.

---

## 🎯 Objetivo do Teste 1

O Teste 1 tem como objetivo:

- Acessar a **API de Dados Abertos da ANS**
- Identificar automaticamente os **últimos 3 trimestres disponíveis**
- Baixar os arquivos de **Demonstrações Contábeis**
- Processar apenas os dados de **Despesas com Eventos/Sinistros**
- Consolidar as informações em um **único arquivo CSV padronizado**

Este teste representa a **etapa de ingestão e consolidação inicial dos dados**, servindo
como base para todas as etapas seguintes do projeto.

---

##  Fonte dos Dados

Os dados são obtidos a partir da API pública da ANS:

```
https://dadosabertos.ans.gov.br/FTP/PDA/
```

Os arquivos estão organizados por **ano** e **trimestre**, podendo conter:
- Estruturas de diretório diferentes
- Múltiplos arquivos ZIP por trimestre
- Arquivos em formatos variados

A implementação foi desenvolvida para ser **resiliente a essas variações**.

---

##  Arquitetura e Responsabilidades

O Teste 1 foi dividido em componentes bem definidos, cada um com uma
responsabilidade clara.

### 📂 Localização do código

```
src/main/java/br/com/intuitive/care/
├── config/teste1
├── controller/teste1
├── model/teste1
└── service/teste1
```


---

## 🔧 Componentes do Teste 1

### 1️⃣ Configuração HTTP

#### `RestTemplateConfig`

Responsável por configurar o `RestTemplate`, utilizado para realizar
requisições HTTP para a API da ANS.

**Responsabilidades:**
- Centralizar a configuração do cliente HTTP
- Facilitar manutenção e reutilização
- Evitar criação direta de instâncias espalhadas pelo código

---

### 2️⃣ Controller

#### `AnsController`

Camada de entrada do Teste 1.

**Responsabilidades:**
- Expor o endpoint que dispara a execução do Teste 1
- Delegar a lógica de negócio para a camada de serviço
- Não conter regras de negócio

O controller atua apenas como **orquestrador da requisição**, seguindo boas práticas
de separação de responsabilidades.

---

### 3️⃣ Serviços de Integração e Download

#### `AnsDataService`

Responsável por coordenar o fluxo principal do Teste 1.

**Responsabilidades:**
- Identificar os últimos trimestres disponíveis
- Orquestrar o download, extração e consolidação dos dados
- Garantir a ordem correta das etapas do pipeline

Este serviço funciona como o **coração do Teste 1**.

---

#### `AnsFileDownloaderService`

Responsável exclusivamente pelo **download dos arquivos ZIP** disponibilizados pela ANS.

**Responsabilidades:**
- Realizar requisições HTTP para download dos arquivos
- Salvar os arquivos localmente na pasta `data/raw`
- Isolar a lógica de download do restante do processamento

Essa separação facilita manutenção e testes futuros.

---

### 4️⃣ Extração de Arquivos

#### `ZipExtractionService`

Responsável pela **extração automática dos arquivos ZIP** baixados.

**Responsabilidades:**
- Extrair os arquivos compactados
- Organizar os arquivos extraídos na pasta `data/processed`
- Garantir que arquivos duplicados ou inválidos não quebrem o fluxo

---

### 5️⃣ Processamento e Consolidação

#### `ConsolidacaoService`

Responsável por:
- Ler os arquivos extraídos
- Identificar os registros relacionados a **Despesas com Eventos/Sinistros**
- Normalizar os dados em um formato comum
- Consolidar os dados dos 3 trimestres

Durante esse processo, são tratados cenários como:
- CNPJs duplicados
- Razões sociais inconsistentes
- Valores zerados ou negativos
- Formatos diferentes de período (ano/trimestre)

---

### 6️⃣ Modelo de Dados

#### `DespesaRecord`

Modelo que representa um registro consolidado de despesa.

**Campos principais:**
- `CNPJ`
- `RazaoSocial`
- `Ano`
- `Trimestre`
- `ValorDespesas`

Este modelo é utilizado como **estrutura intermediária**, garantindo padronização
antes da geração do CSV final.

---

## 🔄 Fluxo Completo do Teste 1

De forma resumida, o fluxo executado é:

1. Requisição ao endpoint do Teste 1
2. Identificação dos últimos 3 trimestres disponíveis
3. Download dos arquivos ZIP da ANS
4. Extração dos arquivos compactados
5. Leitura e filtragem dos dados relevantes
6. Consolidação em memória
7. Geração do arquivo CSV final
8. Compactação do resultado

---

## 📤 Resultado Gerado

Ao final da execução do Teste 1, são gerados:

```text
data/output/consolidado_despesas.csv
data/output/consolidado_despesas.zip
```

O arquivo CSV contém os dados consolidados e padronizados, prontos para serem
utilizados no Teste 2.

---

## 📌 Observação Final

O Teste 1 representa a base de dados do projeto.
A qualidade e organização desta etapa são fundamentais para garantir
confiabilidade nas etapas seguintes de validação, agregação e análise.