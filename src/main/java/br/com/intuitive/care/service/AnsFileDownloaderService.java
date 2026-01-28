package br.com.intuitive.care.service;

import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class AnsFileDownloaderService {

    private static final String BASE_URL =
            "https://dadosabertos.ans.gov.br/FTP/PDA/demonstracoes_contabeis/2025/";

    private static final Path BASE_DIR =
            Path.of("data/raw/demonstracoes_contabeis/2025");

    public void downloadUltimosTrimestres() {
        try {
            Files.createDirectories(BASE_DIR);

            download("1T2025.zip");
            download("2T2025.zip");
            download("3T2025.zip");

        } catch (Exception e) {
            throw new RuntimeException("Erro ao baixar arquivos da ANS", e);
        }
    }

    private void download(String fileName) throws Exception {
        URL url = new URL(BASE_URL + fileName);
        Path target = BASE_DIR.resolve(fileName);

        if (Files.exists(target)) {
            System.out.println(fileName + " já existe, pulando download.");
            return;
        }

        try (InputStream in = url.openStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Baixado: " + fileName);
        }
    }
}
