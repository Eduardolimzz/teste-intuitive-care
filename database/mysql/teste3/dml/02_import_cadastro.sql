-- =====================================================
-- PREPARAÇÃO: Criar tabela temporária para staging
-- =====================================================

CREATE TEMPORARY TABLE IF NOT EXISTS staging_cadastro (
    registro_ans VARCHAR(50),
    cnpj VARCHAR(14),
    razao_social VARCHAR(255),
    modalidade VARCHAR(100),
    uf CHAR(2),
    linha_original INT AUTO_INCREMENT PRIMARY KEY
);

-- =====================================================
-- IMPORTAÇÃO: Carregar CSV para staging
-- =====================================================

-- Para MySQL:
LOAD DATA LOCAL INFILE '../data/downloads/cadastro_operadoras.csv'
INTO TABLE staging_cadastro
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ';'
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(registro_ans, cnpj, razao_social, modalidade, uf);

-- =====================================================
-- VALIDAÇÃO E LIMPEZA
-- =====================================================

-- 1. Limpar espaços em branco
UPDATE staging_cadastro
SET cnpj = TRIM(cnpj),
    razao_social = TRIM(razao_social),
    registro_ans = TRIM(registro_ans),
    modalidade = TRIM(modalidade),
    uf = TRIM(uf);

-- 2. Remover caracteres não numéricos do CNPJ
UPDATE staging_cadastro
SET cnpj = REGEXP_REPLACE(cnpj, '[^0-9]', '');

-- 3. Remover registros inválidos
DELETE FROM staging_cadastro
WHERE cnpj IS NULL
   OR cnpj = ''
   OR LENGTH(cnpj) != 14
   OR razao_social IS NULL
   OR razao_social = '';

-- 4. Padronizar UF (maiúsculas)
UPDATE staging_cadastro
SET uf = UPPER(uf);

-- =====================================================
-- ENRIQUECIMENTO: Atualizar tabela operadoras
-- =====================================================

-- Análise crítica: Tratamento de CNPJs duplicados com dados diferentes
-- Decisão: Usar o registro mais recente (última linha do CSV)
-- Justificativa: CSVs geralmente são ordenados cronologicamente

-- Atualizar operadoras existentes com dados do cadastro
UPDATE operadoras o
INNER JOIN (
    SELECT
        cnpj,
        registro_ans,
        modalidade,
        uf,
        MAX(linha_original) as ultima_linha
    FROM staging_cadastro
    GROUP BY cnpj, registro_ans, modalidade, uf
) s ON o.cnpj = s.cnpj
SET
    o.registro_ans = s.registro_ans,
    o.modalidade = s.modalidade,
    o.uf = s.uf;

-- Inserir novas operadoras que não existem
INSERT INTO operadoras (cnpj, razao_social, registro_ans, modalidade, uf)
SELECT DISTINCT
    s.cnpj,
    s.razao_social,
    s.registro_ans,
    s.modalidade,
    s.uf
FROM staging_cadastro s
WHERE NOT EXISTS (
    SELECT 1 FROM operadoras o WHERE o.cnpj = s.cnpj
);

-- =====================================================
-- VERIFICAÇÃO: Registros sem match
-- =====================================================

-- Análise: CNPJs no consolidado sem cadastro
SELECT
    'CNPJs sem cadastro' AS metrica,
    COUNT(DISTINCT dc.cnpj) AS total
FROM despesas_consolidadas dc
LEFT JOIN operadoras o ON dc.cnpj = o.cnpj
WHERE o.registro_ans IS NULL;

-- =====================================================
-- LIMPEZA
-- =====================================================

DROP TEMPORARY TABLE IF EXISTS staging_cadastro;