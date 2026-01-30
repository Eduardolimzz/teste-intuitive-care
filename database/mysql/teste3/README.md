# Teste 3 – Banco de Dados e Análise SQL

Este documento descreve a implementação do **Teste 3**, responsável pela
modelagem do banco de dados, carga dos dados provenientes dos testes anteriores
e desenvolvimento de **queries analíticas** conforme solicitado no teste técnico.

O objetivo deste README é permitir que o leitor compreenda:
- A estrutura do banco de dados
- O papel de cada script SQL
- As decisões técnicas adotadas
- Como as queries analíticas foram construídas

---

## 🎯 Objetivo do Teste 3

O Teste 3 tem como objetivos principais:

- Estruturar o banco de dados relacional
- Importar os dados gerados nos Testes 1 e 2
- Garantir consistência e integridade dos dados
- Criar queries analíticas para extração de informações relevantes
- Preparar os dados para consumo por API e interface web

Este teste representa a **etapa de persistência e análise de dados** do projeto.

---

##  Fontes de Dados Utilizadas

Os dados utilizados neste teste são provenientes dos testes anteriores:

- `consolidado_despesas.csv` (Teste 1)
- `despesas_agregadas.csv` (Teste 2)
- Cadastro de operadoras da ANS (`Relatorio_cadop.csv`)

Esses arquivos são importados para o banco de dados por meio de scripts SQL
dedicados, garantindo reprodutibilidade do processo.

---

## 📂 Estrutura de Diretórios

```text
database/mysql/teste3/
├── ddl/
│   ├── 01_create_tables.sql
│   └── 02_create_indexes.sql
│
├── dml/
│   ├── 01_import_consolidado.sql
│   ├── 02_import_cadastro.sql
│   └── 03_import_agregados.sql
│
├── queries/
│   ├── query1_crescimento_operadoras.sql
│   ├── query2_distribuicao_uf.sql
│   └── query3_acima_media.sql
│
└── README.md
```
A separação entre DDL, DML e queries facilita leitura, manutenção e execução
do projeto em ambientes diferentes.

---

## 🔧 Modelagem do Banco de Dados (DDL)
#### `01_create_tables.sql`

Responsável pela criação das tabelas principais do projeto.

**Características da modelagem:**

- Separação entre dados consolidados, cadastrais e agregados
- Uso de chaves primárias para identificação única
- Tipos de dados definidos visando precisão e consistência
- As tabelas refletem diretamente os arquivos CSV utilizados como entrada.

---

#### `02_create_indexes.sql`

Responsável pela criação de índices para otimização das consultas.

**Objetivos dos índices:**

- Melhorar performance das queries analíticas
- Otimizar filtros por CNPJ, UF e período
- Reduzir custo de agregações e ordenações
- Os índices foram definidos com foco em leitura analítica, não em escrita frequente.

---

##  Importação de Dados (DML)
#### `01_import_consolidado.sql`

Importa os dados do arquivo ```consolidado_despesas.csv```

**Cuidados adotados:**

- Tratamento de encoding
- Conversão de tipos quando necessário
- Garantia de integridade mínima dos registros

---

#### `02_import_cadastro.sql`

**Responsável pela importação do cadastro de operadoras da ANS.**

Este script permite o enriquecimento relacional entre despesas e dados cadastrais,
utilizando o CNPJ como chave lógica.

---

#### `03_import_agregados.sql`

Importa os dados agregados gerados no Teste 2.

**Esses dados são utilizados principalmente para:**

- Análises estatísticas
- Queries de ranking e distribuição
- Consumo por API futuramente

---

## Queries Analíticas
### 📄 Query 1 – Crescimento de Despesas por Operadora

#### `query1_crescimento_operadoras.sql`

**Objetivo:**
Identificar as 5 operadoras com maior crescimento percentual de despesas
entre o primeiro e o último trimestre analisado.

**Considerações:**

- Operadoras sem dados em todos os trimestres são tratadas conforme regra definida
- O crescimento é calculado de forma percentual
- A query prioriza clareza e legibilidade

---

### 📄 Query 2 – Distribuição de Despesas por UF

#### `query2_distribuicao_uf.sql`

**Objetivo:**
Analisar a distribuição das despesas por UF e listar os 5 estados com maiores
despesas totais.

**Cálculos adicionais:**

- Total de despesas por UF
- Média de despesas por operadora em cada UF

---

### 📄 Query 3 – Operadoras Acima da Média

#### `query3_acima_media.sql`

**Objetivo:**
Identificar quantas operadoras apresentaram despesas acima da média geral
em pelo menos 2 dos 3 trimestres analisados.

**Abordagem adotada:**

- Uso de agregações e subqueries
- Foco em legibilidade e facilidade de manutenção
- Estrutura pensada para facilitar ajustes futuros

---
## 📌 Observação Final

O Teste 3 consolida todo o trabalho realizado nos testes anteriores,
transformando arquivos CSV em dados estruturados e analisáveis.

Essa etapa é fundamental para permitir análises consistentes,
dashboards e consumo via API nas próximas fases do projeto.