package br.com.intuitive.care.service.teste2;

import br.com.intuitive.care.model.teste2.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AggregationService {

    public List<DespesaAgregada> agregar(List<DespesaEnriquecida> lista) {

        Map<String, List<DespesaEnriquecida>> agrupado =
                lista.stream().collect(Collectors.groupingBy(
                        d -> d.getRazaoSocial() + ";" + d.getUf()
                ));

        List<DespesaAgregada> resultado = new ArrayList<>();

        agrupado.forEach((key, despesas) -> {

            String[] partes = key.split(";", 2);

            String razao = partes[0];
            String uf = partes.length > 1 ? partes[1] : "SEM_UF";

            double total = despesas.stream()
                    .mapToDouble(DespesaEnriquecida::getValor)
                    .sum();

            double media = total / despesas.size();

            double variancia = despesas.stream()
                    .mapToDouble(d -> Math.pow(d.getValor() - media, 2))
                    .sum() / despesas.size();

            double desvioPadrao = Math.sqrt(variancia);

            resultado.add(
                    new DespesaAgregada(
                            razao,
                            uf,
                            total,
                            media,
                            desvioPadrao
                    )
            );
        });

        return resultado;
    }
}