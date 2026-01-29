package br.com.intuitive.care.model.teste2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DespesaAgregada {

    private String razaoSocial;
    private String uf;
    private double total;
    private double media;
    private double desvioPadrao;
}
