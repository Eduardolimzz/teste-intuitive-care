package br.com.intuitive.care.service.teste2;

import br.com.intuitive.care.model.teste2.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class Teste2PipelineService {

    private final ValidationService validation;
    private final EnrichmentService enrichment;
    private final AggregationService aggregation;
    private final OperadoraDownloaderService downloader;

    public Teste2PipelineService(
            ValidationService validation,
            EnrichmentService enrichment,
            AggregationService aggregation,
            OperadoraDownloaderService downloader) {

        this.validation = validation;
        this.enrichment = enrichment;
        this.aggregation = aggregation;
        this.downloader = downloader;
    }

    public void executar() throws Exception {


        String basePath = System.getProperty("user.dir");

        Path input = Paths.get(
                basePath, "data", "output", "consolidado_despesas.csv"
        );

        Path cadastroCsv = Path.of(downloader.baixarCsv());

        Path output = Paths.get(
                basePath, "data", "output", "despesas_agregadas.csv"
        );

        Path cadastroPath = downloader.downloadCadastroOperadoras();

        Map<String, String[]> cadastroMap =
                enrichment.carregarCadastro(cadastroCsv.toString());


        List<DespesaEnriquecida> enriquecidas = new ArrayList<>();

        int linhasLidas = 0;
        int linhasValidas = 0;
        int linhasIgnoradas = 0;

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(input.toFile()),
                        StandardCharsets.UTF_8))) {

            String header = br.readLine();

            String linha;
            while ((linha = br.readLine()) != null) {

                String[] partes = linha.split(",");

                if (partes.length < 5) continue;

                String cnpj = partes[0].replaceAll("\\D", "");
                String razao = partes[1].trim();

                int trimestre;
                int ano;
                double valor;

                try {
                    trimestre = Integer.parseInt(partes[2]);
                    ano = Integer.parseInt(partes[3]);
                    valor = Double.parseDouble(partes[4].replace(",", "."));
                } catch (Exception e) {
                    continue;
                }

                if (!validation.razaoSocialValida(razao)) continue;
                if (!validation.valorValido(valor)) continue;

                String[] cad = cadastroMap.get(cnpj);

                String reg = "SEM_CADASTRO";
                String mod = "SEM_CADASTRO";
                String uf = "SEM_CADASTRO";

                if (cad != null) {
                    if (cad.length > 0) reg = cad[0];
                    if (cad.length > 1) mod = cad[1];
                    if (cad.length > 2) uf = cad[2];
                }

                DespesaConsolidada base =
                        new DespesaConsolidada(cnpj, razao, ano, trimestre, valor);

                enriquecidas.add(new DespesaEnriquecida(base, reg, mod, uf));
            }
        }


        if (!enriquecidas.isEmpty()) {
            DespesaEnriquecida exemplo = enriquecidas.get(0);
        }

        List<DespesaAgregada> agregadas =
                aggregation.agregar(enriquecidas);


        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(output.toFile()),
                        StandardCharsets.UTF_8))) {

            bw.write("RazaoSocial;UF;Total;Media;DesvioPadrao\n");

            for (DespesaAgregada d : agregadas) {
                bw.write(String.format(
                        "%s;%s;%.2f;%.2f;%.2f%n",
                        d.getRazaoSocial(),
                        d.getUf(),
                        d.getTotal(),
                        d.getMedia(),
                        d.getDesvioPadrao()
                ));
            }
        }

    }
}