-- =====================================================
-- 1. TABELA DE OPERADORAS (Dados Cadastrais)
-- =====================================================

CREATE TABLE IF NOT EXISTS operadoras (
    cnpj VARCHAR(14) PRIMARY KEY,
    razao_social VARCHAR(255) NOT NULL,
    registro_ans VARCHAR(50),
    modalidade VARCHAR(100),
    uf CHAR(2),
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_cnpj_length CHECK (LENGTH(cnpj) = 14),
    CONSTRAINT chk_uf_format CHECK (uf IS NULL OR LENGTH(uf) = 2)
);

-- =====================================================
-- 2. TABELA DE DESPESAS CONSOLIDADAS
-- =====================================================

-- Trade-off para valores monetários: DECIMAL(15,2)
-- Justificativa:
-- - DECIMAL garante precisão exata (essencial para valores financeiros)
-- - FLOAT causaria erros de arredondamento
-- - INTEGER (centavos) economizaria espaço mas dificultaria queries
-- - DECIMAL(15,2) suporta até 999 trilhões com 2 casas decimais

CREATE TABLE IF NOT EXISTS despesas_consolidadas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cnpj VARCHAR(14) NOT NULL,
    razao_social VARCHAR(255) NOT NULL,
    trimestre INT NOT NULL,
    ano INT NOT NULL,
    valor_despesas DECIMAL(15,2) NOT NULL,
    data_importacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_despesas_operadora FOREIGN KEY (cnpj)
        REFERENCES operadoras(cnpj) ON DELETE CASCADE,

    CONSTRAINT chk_trimestre CHECK (trimestre BETWEEN 1 AND 4),
    CONSTRAINT chk_ano CHECK (ano BETWEEN 2000 AND 2100),
    CONSTRAINT chk_valor_positivo CHECK (valor_despesas >= 0),

    UNIQUE KEY uk_despesa_periodo (cnpj, trimestre, ano)
);

-- =====================================================
-- 3. TABELA DE DESPESAS AGREGADAS
-- =====================================================

CREATE TABLE IF NOT EXISTS despesas_agregadas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    razao_social VARCHAR(255) NOT NULL,
    uf CHAR(2) NOT NULL,
    total_despesas DECIMAL(15,2) NOT NULL,
    media_despesas DECIMAL(15,2) NOT NULL,
    desvio_padrao DECIMAL(15,2) NOT NULL,
    data_agregacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_total_positivo CHECK (total_despesas >= 0),
    CONSTRAINT chk_media_positiva CHECK (media_despesas >= 0),
    CONSTRAINT chk_desvio_positivo CHECK (desvio_padrao >= 0),

    UNIQUE KEY uk_agregacao (razao_social, uf)
);