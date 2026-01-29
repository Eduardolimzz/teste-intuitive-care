package br.com.intuitive.care.service.teste2;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class OperadoraDownloaderService {

    private static final String URL_CADOP =
            "https://dadosabertos.ans.gov.br/FTP/PDA/operadoras_de_plano_de_saude_ativas/Relatorio_cadop.csv";

    private final RestTemplate restTemplate = new RestTemplate();

    public String baixarCsv() {
        try {
            byte[] conteudo = restTemplate.getForObject(URL_CADOP, byte[].class);

            Path destino = Paths.get(
                    System.getProperty("user.dir"),
                    "data",
                    "raw",
                    "Relatorio_cadop.csv"
            );

            destino.toFile().getParentFile().mkdirs();

            try (FileOutputStream fos = new FileOutputStream(destino.toFile())) {
                fos.write(conteudo);
            }

            return destino.toString();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao baixar Relatorio_cadop.csv da ANS", e);
        }
    }

    public Path downloadCadastroOperadoras() throws IOException {
        String url = "https://dadosabertos.ans.gov.br/FTP/PDA/operadoras_de_plano_de_saude_ativas/Relatorio_cadop.csv";

        Path outputPath = Paths.get("data/output/Relatorio_cadop.csv");
        Files.createDirectories(outputPath.getParent());

        try (InputStream in = new URL(url).openStream()) {
            Files.copy(in, outputPath, StandardCopyOption.REPLACE_EXISTING);
        }

        return outputPath;
    }

}
