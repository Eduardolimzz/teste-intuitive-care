package br.com.intuitive.care.model.teste2;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DespesaEnriquecida extends DespesaConsolidada {

    private String registroAns;
    private String modalidade;
    private String uf;

    public DespesaEnriquecida(
            DespesaConsolidada base,
            String registroAns,
            String modalidade,
            String uf) {

        super(
                base.getCnpj(),
                base.getRazaoSocial(),
                base.getAno(),
                base.getTrimestre(),
                base.getValor()
        );

        this.registroAns = registroAns;
        this.modalidade = modalidade;
        this.uf = uf;
    }
}
