-- =====================================================
-- PREPARAÇÃO: Criar tabela temporária para staging
-- =====================================================

-- Tabela temporária permite validação antes de mover para tabela final
CREATE TEMPORARY TABLE IF NOT EXISTS staging_consolidado (
    cnpj VARCHAR(14),
    razao_social VARCHAR(255),
    trimestre VARCHAR(10),
    ano VARCHAR(10),
    valor_despesas VARCHAR(50),
    linha_original INT AUTO_INCREMENT PRIMARY KEY
);

-- =====================================================
-- IMPORTAÇÃO: Carregar CSV para staging
-- =====================================================

-- Para MySQL:
LOAD DATA LOCAL INFILE '../data/output/consolidado_despesas.csv'
INTO TABLE staging_consolidado
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(cnpj, razao_social, trimestre, ano, valor_despesas);

-- =====================================================
-- VALIDAÇÃO E LIMPEZA: Tratar inconsistências
-- =====================================================

-- Análise crítica: Tratamento de inconsistências encontradas

-- 1. Limpar espaços em branco
UPDATE staging_consolidado
SET cnpj = TRIM(cnpj),
    razao_social = TRIM(razao_social),
    trimestre = TRIM(trimestre),
    ano = TRIM(ano),
    valor_despesas = TRIM(valor_despesas);

-- 2. Remover caracteres não numéricos do CNPJ
UPDATE staging_consolidado
SET cnpj = REGEXP_REPLACE(cnpj, '[^0-9]', '');

-- 3. Tratar valores NULL em campos obrigatórios
-- Decisão: REJEITAR registros com NULL em campos obrigatórios
-- Justificativa: Dados incompletos comprometem análises
DELETE FROM staging_consolidado
WHERE cnpj IS NULL
   OR cnpj = ''
   OR razao_social IS NULL
   OR razao_social = ''
   OR trimestre IS NULL
   OR ano IS NULL
   OR valor_despesas IS NULL;

-- 4. Tratar strings em campos numéricos
-- Decisão: TENTAR CONVERSÃO, rejeitar se falhar
-- Justificativa: Alguns sistemas exportam números como "1.234,56"

-- Normalizar formato de decimal (vírgula para ponto)
UPDATE staging_consolidado
SET valor_despesas = REPLACE(valor_despesas, ',', '.');

-- Remover registros com valores não conversíveis
DELETE FROM staging_consolidado
WHERE trimestre NOT REGEXP '^[0-9]+$'
   OR ano NOT REGEXP '^[0-9]+$'
   OR valor_despesas NOT REGEXP '^-?[0-9]+\.?[0-9]*$';

-- 5. Tratar datas em formatos inconsistentes
-- Decisão: Validar e corrigir trimestres inválidos
DELETE FROM staging_consolidado
WHERE CAST(trimestre AS UNSIGNED) NOT BETWEEN 1 AND 4
   OR CAST(ano AS UNSIGNED) NOT BETWEEN 2000 AND 2100;

-- 6. Tratar valores negativos ou zerados
-- Decisão: MANTER valores zerados, REJEITAR negativos
-- Justificativa: Zero pode ser legítimo (operadora sem despesas),
-- mas valores negativos indicam erro de dados
DELETE FROM staging_consolidado
WHERE CAST(valor_despesas AS DECIMAL(15,2)) < 0;

-- =====================================================
-- LOG: Registrar estatísticas de validação
-- =====================================================

-- Criar tabela de log temporária
CREATE TEMPORARY TABLE IF NOT EXISTS import_stats (
    descricao VARCHAR(255),
    quantidade INT
);

-- Registrar estatísticas
INSERT INTO import_stats VALUES
    ('Total de linhas carregadas', (SELECT COUNT(*) FROM staging_consolidado)),
    ('Linhas válidas após limpeza', (SELECT COUNT(*) FROM staging_consolidado));

-- =====================================================
-- INSERÇÃO: Mover dados validados para tabelas finais
-- =====================================================

-- Primeiro, inserir operadoras únicas
INSERT IGNORE INTO operadoras (cnpj, razao_social)
SELECT DISTINCT
    cnpj,
    razao_social
FROM staging_consolidado
WHERE LENGTH(cnpj) = 14;

-- Depois, inserir despesas consolidadas
INSERT INTO despesas_consolidadas
    (cnpj, razao_social, trimestre, ano, valor_despesas)
SELECT
    cnpj,
    razao_social,
    CAST(trimestre AS UNSIGNED),
    CAST(ano AS UNSIGNED),
    CAST(valor_despesas AS DECIMAL(15,2))
FROM staging_consolidado
WHERE LENGTH(cnpj) = 14
ON DUPLICATE KEY UPDATE
    valor_despesas = VALUES(valor_despesas),
    data_importacao = CURRENT_TIMESTAMP;

-- =====================================================
-- VERIFICAÇÃO: Exibir estatísticas finais
-- =====================================================

SELECT * FROM import_stats;

SELECT
    'Operadoras importadas' AS metrica,
    COUNT(*) AS total
FROM operadoras
UNION ALL
SELECT
    'Despesas importadas' AS metrica,
    COUNT(*) AS total
FROM despesas_consolidadas;

-- =====================================================
-- LIMPEZA: Dropar tabelas temporárias
-- =====================================================

DROP TEMPORARY TABLE IF EXISTS staging_consolidado;
DROP TEMPORARY TABLE IF EXISTS import_stats;