CREATE TABLE operadora (
    cnpj CHAR(14) NOT NULL,
    razao_social VARCHAR(255) NOT NULL,
    registro_ans VARCHAR(20),
    modalidade VARCHAR(100),
    uf CHAR(2),
    PRIMARY KEY (cnpj),
    INDEX idx_operadora_uf (uf)
);

CREATE TABLE despesa_consolidada (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cnpj CHAR(14) NOT NULL,
    razao_social VARCHAR(255) NOT NULL,
    ano INT NOT NULL,
    trimestre INT NOT NULL,
    valor_despesas DECIMAL(15,2) NOT NULL,
    INDEX idx_despesa_cnpj (cnpj),
    INDEX idx_despesa_periodo (ano, trimestre),
    CONSTRAINT fk_despesa_operadora
        FOREIGN KEY (cnpj)
        REFERENCES operadora(cnpj)
        ON DELETE RESTRICT
);

CREATE TABLE despesa_agregada (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cnpj CHAR(14) NOT NULL,
    razao_social VARCHAR(255) NOT NULL,
    uf CHAR(2),
    total_despesas DECIMAL(15,2) NOT NULL,
    media_trimestral DECIMAL(15,2),
    desvio_padrao DECIMAL(15,2),
    INDEX idx_agregada_total (total_despesas),
    INDEX idx_agregada_uf (uf),
    CONSTRAINT fk_agregada_operadora
        FOREIGN KEY (cnpj)
        REFERENCES operadora(cnpj)
        ON DELETE RESTRICT
);
