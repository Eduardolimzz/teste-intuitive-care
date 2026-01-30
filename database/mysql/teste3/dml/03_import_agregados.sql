-- =====================================================
-- PREPARAÇÃO: Criar tabela temporária para staging
-- =====================================================

CREATE TEMPORARY TABLE IF NOT EXISTS staging_agregado (
    razao_social VARCHAR(255),
    uf CHAR(2),
    total VARCHAR(50),
    media VARCHAR(50),
    desvio_padrao VARCHAR(50),
    linha_original INT AUTO_INCREMENT PRIMARY KEY
) ENGINE=MEMORY;

-- =====================================================
-- IMPORTAÇÃO: Carregar CSV para staging
-- =====================================================

LOAD DATA LOCAL INFILE '../data/output/despesas_agregadas.csv'
INTO TABLE staging_agregado
CHARACTER SET utf8mb4
FIELDS TERMINATED BY ';'
ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(razao_social, uf, total, media, desvio_padrao);

-- =====================================================
-- VALIDAÇÃO E LIMPEZA
-- =====================================================

-- 1. Limpar espaços em branco
UPDATE staging_agregado
SET razao_social = TRIM(razao_social),
    uf = TRIM(uf),
    total = TRIM(total),
    media = TRIM(media),
    desvio_padrao = TRIM(desvio_padrao);

-- 2. Normalizar formato de decimal (vírgula para ponto)
UPDATE staging_agregado
SET total = REPLACE(total, ',', '.'),
    media = REPLACE(media, ',', '.'),
    desvio_padrao = REPLACE(desvio_padrao, ',', '.');

-- 3. Remover registros inválidos
DELETE FROM staging_agregado
WHERE razao_social IS NULL
   OR razao_social = ''
   OR uf IS NULL
   OR uf = ''
   OR total NOT REGEXP '^[0-9]+\.?[0-9]*$'
   OR media NOT REGEXP '^[0-9]+\.?[0-9]*$'
   OR desvio_padrao NOT REGEXP '^[0-9]+\.?[0-9]*$';

-- =====================================================
-- INSERÇÃO: Mover dados para tabela final
-- =====================================================

INSERT INTO despesas_agregadas
    (razao_social, uf, total_despesas, media_despesas, desvio_padrao)
SELECT
    razao_social,
    uf,
    CAST(total AS DECIMAL(15,2)),
    CAST(media AS DECIMAL(15,2)),
    CAST(desvio_padrao AS DECIMAL(15,2))
FROM staging_agregado
ON DUPLICATE KEY UPDATE
    total_despesas = VALUES(total_despesas),
    media_despesas = VALUES(media_despesas),
    desvio_padrao = VALUES(desvio_padrao),
    data_agregacao = CURRENT_TIMESTAMP;

-- =====================================================
-- VERIFICAÇÃO: Estatísticas
-- =====================================================

SELECT
    'Registros agregados importados' AS metrica,
    COUNT(*) AS total
FROM despesas_agregadas;

-- =====================================================
-- LIMPEZA
-- =====================================================

DROP TEMPORARY TABLE IF EXISTS staging_agregado;
