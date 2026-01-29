package br.com.intuitive.care.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Modelo para representar um registro de despesa
 * Usado na consolidação dos dados da ANS
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DespesaRecord {

    private String cnpj;
    private String razaoSocial;
    private String trimestre;
    private String ano;
    private BigDecimal valorDespesas;

    // Flag para marcar registros com inconsistências
    private boolean possuiInconsistencia;
    private String tipoInconsistencia;

    /**
     * Construtor simplificado (sem flags de inconsistência)
     */
    public DespesaRecord(String cnpj, String razaoSocial, String trimestre, String ano, BigDecimal valorDespesas) {
        this.cnpj = cnpj;
        this.razaoSocial = razaoSocial;
        this.trimestre = trimestre;
        this.ano = ano;
        this.valorDespesas = valorDespesas;
        this.possuiInconsistencia = false;
        this.tipoInconsistencia = "";
    }

    /**
     * Marca este registro como tendo inconsistência
     */
    public void marcarInconsistencia(String tipo) {
        this.possuiInconsistencia = true;
        if (this.tipoInconsistencia.isEmpty()) {
            this.tipoInconsistencia = tipo;
        } else {
            this.tipoInconsistencia += "; " + tipo;
        }
    }
}