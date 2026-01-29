package br.com.intuitive.care.model.teste2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DespesaConsolidada {

    private String cnpj;
    private String razaoSocial;
    private int ano;
    private int trimestre;
    private double valor;
}
