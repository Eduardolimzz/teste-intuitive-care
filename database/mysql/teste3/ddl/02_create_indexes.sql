-- =====================================================
-- ÍNDICES PARA TABELA: operadoras
-- =====================================================

-- Índice para buscas por razão social (LIKE queries)
CREATE INDEX idx_operadoras_razao_social
    ON operadoras(razao_social);

-- Índice para filtros por UF
CREATE INDEX idx_operadoras_uf
    ON operadoras(uf);

-- Índice para filtros por modalidade
CREATE INDEX idx_operadoras_modalidade
    ON operadoras(modalidade);

-- =====================================================
-- ÍNDICES PARA TABELA: despesas_consolidadas
-- =====================================================

-- Índice composto para queries por período
-- Cobre queries como: WHERE ano = X AND trimestre = Y
CREATE INDEX idx_despesas_periodo
    ON despesas_consolidadas(ano, trimestre);

-- Índice para JOIN com operadoras (já existe FK, mas reforçamos)
CREATE INDEX idx_despesas_cnpj
    ON despesas_consolidadas(cnpj);

-- Índice para ordenação por valor
CREATE INDEX idx_despesas_valor
    ON despesas_consolidadas(valor_despesas DESC);

-- Índice composto para análises temporais por operadora
CREATE INDEX idx_despesas_cnpj_periodo
    ON despesas_consolidadas(cnpj, ano, trimestre);

-- =====================================================
-- ÍNDICES PARA TABELA: despesas_agregadas
-- =====================================================

-- Índice para filtros por UF
CREATE INDEX idx_agregadas_uf
    ON despesas_agregadas(uf);

-- Índice para ordenação por total
CREATE INDEX idx_agregadas_total
    ON despesas_agregadas(total_despesas DESC);

-- Índice composto para queries analíticas comuns
CREATE INDEX idx_agregadas_uf_total
    ON despesas_agregadas(uf, total_despesas DESC);