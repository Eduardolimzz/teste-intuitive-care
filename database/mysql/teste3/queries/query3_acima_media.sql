-- =====================================================
-- QUERY PRINCIPAL
-- =====================================================

WITH media_por_trimestre AS (
    -- Calcular média geral de despesas por trimestre
    SELECT
        ano,
        trimestre,
        AVG(valor_despesas) AS media_geral
    FROM despesas_consolidadas
    GROUP BY ano, trimestre
),
despesas_com_comparacao AS (
    -- Comparar cada registro com a média do seu trimestre
    SELECT
        dc.cnpj,
        dc.ano,
        dc.trimestre,
        dc.valor_despesas,
        m.media_geral,
        CASE
            WHEN dc.valor_despesas > m.media_geral THEN 1
            ELSE 0
        END AS acima_da_media
    FROM despesas_consolidadas dc
    INNER JOIN media_por_trimestre m
        ON dc.ano = m.ano
        AND dc.trimestre = m.trimestre
),
operadoras_qualificadas AS (
    -- Contar em quantos trimestres cada operadora ficou acima da média
    SELECT
        cnpj,
        SUM(acima_da_media) AS trimestres_acima_media,
        COUNT(DISTINCT CONCAT(ano, '-', trimestre)) AS trimestres_participados
    FROM despesas_com_comparacao
    GROUP BY cnpj
    HAVING SUM(acima_da_media) >= 2
)
-- Resultado final: Contagem de operadoras qualificadas
SELECT
    COUNT(*) AS QuantidadeOperadoras
FROM operadoras_qualificadas;