package br.com.intuitive.care.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.*;

@Service
public class AnsFileDownloaderService {

    private static final String BASE_URL =
            "https://dadosabertos.ans.gov.br/FTP/PDA/demonstracoes_contabeis/2025/";

    private static final Path DESTINO =
            Paths.get("data/raw/demonstracoes_contabeis/2025");

    private final RestTemplate restTemplate;

    public AnsFileDownloaderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void downloadUltimosTrimestres() {

        try {
            Files.createDirectories(DESTINO);

            String[] arquivos = {
                    "1T2025.zip",
                    "2T2025.zip",
                    "3T2025.zip"
            };

            for (String nomeArquivo : arquivos) {
                byte[] conteudo = restTemplate.getForObject(
                        BASE_URL + nomeArquivo,
                        byte[].class
                );

                if (conteudo != null) {
                    Files.write(DESTINO.resolve(nomeArquivo), conteudo);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao baixar arquivos da ANS", e);
        }
    }
}
