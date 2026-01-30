-- =====================================================
-- QUERY PRINCIPAL
-- =====================================================

WITH trimestres_disponiveis AS (
    -- Identificar o range de trimestres analisados
    SELECT
        MIN(ano * 10 + trimestre) AS primeiro_periodo,
        MAX(ano * 10 + trimestre) AS ultimo_periodo
    FROM despesas_consolidadas
),
primeiro_trimestre AS (
    -- Despesas no primeiro trimestre por operadora
    SELECT
        dc.cnpj,
        o.razao_social,
        SUM(dc.valor_despesas) AS despesa_inicial,
        dc.ano AS ano_inicial,
        dc.trimestre AS trimestre_inicial
    FROM despesas_consolidadas dc
    INNER JOIN operadoras o ON dc.cnpj = o.cnpj
    CROSS JOIN trimestres_disponiveis td
    WHERE (dc.ano * 10 + dc.trimestre) = td.primeiro_periodo
    GROUP BY dc.cnpj, o.razao_social, dc.ano, dc.trimestre
    HAVING despesa_inicial > 0  -- Evitar divisão por zero
),
ultimo_trimestre AS (
    -- Despesas no último trimestre por operadora
    SELECT
        dc.cnpj,
        SUM(dc.valor_despesas) AS despesa_final,
        dc.ano AS ano_final,
        dc.trimestre AS trimestre_final
    FROM despesas_consolidadas dc
    CROSS JOIN trimestres_disponiveis td
    WHERE (dc.ano * 10 + dc.trimestre) = td.ultimo_periodo
    GROUP BY dc.cnpj, dc.ano, dc.trimestre
),
crescimento_calculado AS (
    -- Calcular crescimento percentual
    SELECT
        pt.cnpj,
        pt.razao_social,
        pt.despesa_inicial,
        pt.ano_inicial,
        pt.trimestre_inicial,
        ut.despesa_final,
        ut.ano_final,
        ut.trimestre_final,
        ROUND(
            ((ut.despesa_final - pt.despesa_inicial) / pt.despesa_inicial) * 100,
            2
        ) AS crescimento_percentual,
        ut.despesa_final - pt.despesa_inicial AS variacao_absoluta
    FROM primeiro_trimestre pt
    INNER JOIN ultimo_trimestre ut ON pt.cnpj = ut.cnpj
)
-- Resultado final: Top 5 com maior crescimento
SELECT
    cnpj AS CNPJ,
    razao_social AS RazaoSocial,
    CONCAT(ano_inicial, '-Q', trimestre_inicial) AS PrimeiroPeriodo,
    FORMAT(despesa_inicial, 2, 'pt_BR') AS DespesaInicial,
    CONCAT(ano_final, '-Q', trimestre_final) AS UltimoPeriodo,
    FORMAT(despesa_final, 2, 'pt_BR') AS DespesaFinal,
    CONCAT(crescimento_percentual, '%') AS CrescimentoPercentual,
    FORMAT(variacao_absoluta, 2, 'pt_BR') AS VariacaoAbsoluta
FROM crescimento_calculado
ORDER BY crescimento_percentual DESC
LIMIT 5;