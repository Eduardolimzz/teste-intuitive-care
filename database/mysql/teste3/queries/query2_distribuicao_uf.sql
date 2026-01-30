-- =====================================================
-- QUERY PRINCIPAL
-- =====================================================

WITH despesas_por_uf AS (
    -- Totalizar despesas por UF e contar operadoras únicas
    SELECT
        o.uf,
        SUM(dc.valor_despesas) AS total_despesas,
        COUNT(DISTINCT dc.cnpj) AS qtd_operadoras,
        COUNT(dc.id) AS qtd_registros
    FROM despesas_consolidadas dc
    INNER JOIN operadoras o ON dc.cnpj = o.cnpj
    WHERE o.uf IS NOT NULL
      AND o.uf != ''
      AND o.uf != 'SEM_CADASTRO'
    GROUP BY o.uf
),
media_por_operadora_uf AS (
    -- Calcular média de despesas por operadora em cada UF
    -- Primeiro somamos por operadora, depois tiramos a média dessas somas
    SELECT
        o.uf,
        AVG(total_operadora) AS media_por_operadora
    FROM (
        SELECT
            dc.cnpj,
            o.uf,
            SUM(dc.valor_despesas) AS total_operadora
        FROM despesas_consolidadas dc
        INNER JOIN operadoras o ON dc.cnpj = o.cnpj
        WHERE o.uf IS NOT NULL
          AND o.uf != ''
          AND o.uf != 'SEM_CADASTRO'
        GROUP BY dc.cnpj, o.uf
    ) AS totais_por_operadora
    GROUP BY uf
)
-- Resultado final: Top 5 estados + estatísticas
SELECT
    d.uf AS UF,
    FORMAT(d.total_despesas, 2, 'pt_BR') AS TotalDespesas,
    d.qtd_operadoras AS QuantidadeOperadoras,
    FORMAT(m.media_por_operadora, 2, 'pt_BR') AS MediaPorOperadora,
    d.qtd_registros AS TotalRegistros,
    ROUND(
        (d.total_despesas / (SELECT SUM(total_despesas) FROM despesas_por_uf)) * 100,
        2
    ) AS PercentualDoTotal
FROM despesas_por_uf d
INNER JOIN media_por_operadora_uf m ON d.uf = m.uf
ORDER BY d.total_despesas DESC
LIMIT 5;