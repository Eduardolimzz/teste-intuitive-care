package br.com.intuitive.care.service.teste1;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;

@Service
public class AnsDataService {

    private final ZipExtractionService zipExtractionService;

    private static final Path RAW_BASE =
            Paths.get("data/raw/demonstracoes_contabeis/2025");

    private static final Path PROCESSED_BASE =
            Paths.get("data/processed/demonstracoes_contabeis/2025");

    public AnsDataService(ZipExtractionService zipExtractionService) {
        this.zipExtractionService = zipExtractionService;
    }

    public void extrairArquivosZip() {

        if (!Files.exists(RAW_BASE)) {
            throw new RuntimeException("Diretório RAW não encontrado: " + RAW_BASE);
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(RAW_BASE, "*.zip")) {

            for (Path zipFile : stream) {
                String nomeZip = zipFile.getFileName().toString();
                String trimestre = nomeZip.replace(".zip", "");

                Path destino = PROCESSED_BASE.resolve(trimestre);
                zipExtractionService.extractZip(zipFile, destino);
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao extrair ZIPs da ANS", e);
        }
    }
}
