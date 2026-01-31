# Teste 2 – Validação, Enriquecimento e Agregação de Dados

Este documento descreve em detalhes a implementação do **Teste 2**, responsável por
validar, enriquecer e agregar os dados consolidados no Teste 1.

O Teste 2 representa a **etapa de tratamento e qualificação dos dados**, garantindo
que apenas informações consistentes avancem para análises estatísticas e persistência
em banco de dados nas etapas seguintes.

---

## Objetivo do Teste 2

O Teste 2 tem como principais objetivos:

- Validar os dados consolidados gerados no Teste 1
- Garantir integridade e consistência das informações
- Enriquecer os registros com dados cadastrais das operadoras
- Gerar métricas agregadas para análise estatística
- Produzir um novo arquivo CSV com dados prontos para análise

Este teste atua como um **pipeline de transformação e qualidade de dados**.

---

## Entrada de Dados

O Teste 2 utiliza como entrada o arquivo gerado no Teste 1:

```text
data/output/consolidado_despesas.csv
```

Este arquivo contém os dados brutos consolidados, que ainda podem apresentar
inconsistências e precisam passar por validação antes de qualquer análise.

---
##  Arquitetura e Organização

O código do Teste 2 está organizado por responsabilidade, seguindo o mesmo padrão
adotado no Teste 1.
---

## Localização do código
```
src/main/java/br/com/intuitive/care/
├── controller/teste2
├── model/teste2
└── service/teste2
```
---
## Componentes do Teste 2
### 1. Pipeline de Execução
#### `DataProcessingPipelineService`

Classe responsável por orquestrar todo o fluxo do Teste 2.

**Responsabilidades:**

- Coordenar a ordem de execução das etapas
- Garantir que validação, enriquecimento e agregação ocorram corretamente
- Centralizar o fluxo principal do pipeline
- Este serviço funciona como o ponto de entrada lógico do Teste 2.

---
### 2. Validação dos Dados
#### `ValidationService`

Responsável por validar os dados consolidados antes do enriquecimento.

**Validações implementadas:**

- CNPJ válido (formato e dígitos verificadores)
- Razão social não nula e não vazia
- Valores de despesas positivos

**Comportamento adotado:**

- Registros inválidos são identificados e tratados conforme regra definida
- As validações são isoladas para facilitar manutenção e testes
- Este componente garante a qualidade mínima dos dados antes de qualquer cálculo.

---

### 3. Download e Leitura de Dados Cadastrais
#### `OperadoraDownloaderService`

Responsável por obter os dados cadastrais das operadoras diretamente da ANS.

**Responsabilidades:**

- Download automático do arquivo ```Relatorio_cadop.csv```
- Armazenamento local para processamento
- Leitura e indexação dos dados por CNPJ
- Essa etapa fornece os dados necessários para o enriquecimento das despesas.

---

### 4. Enriquecimento dos Dados
#### `EnrichmentService`

Responsável por enriquecer os registros de despesas com informações cadastrais.

**Dados adicionados:**

- Registro ANS
- Modalidade da operadora
- UF

**Cenários tratados:**

- CNPJ presente no consolidado, mas ausente no cadastro
- Múltiplos registros cadastrais para o mesmo CNPJ
- As decisões adotadas priorizam consistência e rastreabilidade dos dados.

---

### 5. Agregação Estatística
#### `AggregationService`

Responsável por gerar métricas agregadas a partir dos dados enriquecidos.

**Agregações realizadas:**

- Total de despesas por operadora e UF
- Média de despesas por trimestre
- Desvio padrão das despesas
- Essas métricas permitem identificar:
- Operadoras com maior volume de despesas
- Variações significativas ao longo do tempo

---

## Testes Unitários

O Teste 2 conta com testes unitários automatizados, focados nas regras críticas
de validação de dados.

**Testes cobertos**
- Validação de CNPJ válido
- Rejeição de CNPJ inválido ou nulo
- Validação de razão social válida
- Rejeição de razão social vazia ou nula
- Validação de valores positivos
- Rejeição de valores zero ou negativos

Os testes foram implementados utilizando JUnit 5 e estão localizados em:
```bash
src/test/java/br/com/intuitive/care/teste2/ValidationServiceTest.java
```
Esses testes garantem confiabilidade e reduzem risco de propagação de dados inválidos.

---
## Resultado Gerado

Ao final da execução do Teste 2, é gerado o arquivo:
```text
data/output/despesas_agregadas.csv
```

Este arquivo contém os dados validados, enriquecidos e agregados, prontos para:

- Persistência em banco de dados (Teste 3)
- Consumo por API ou interface web (Teste 4)
---
## Observação Final

O Teste 2 é uma etapa crítica do projeto, pois garante que os dados utilizados nas
análises posteriores sejam consistentes, confiáveis e bem estruturados.