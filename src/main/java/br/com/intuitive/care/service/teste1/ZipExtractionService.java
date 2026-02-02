package br.com.intuitive.care.service.teste1;

import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ZipExtractionService {

    public void extractZip(Path zipFilePath, Path destinationDir) {
        try {
            Files.createDirectories(destinationDir);

            try (ZipInputStream zis = new ZipInputStream(
                    new BufferedInputStream(Files.newInputStream(zipFilePath)))) {

                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {

                    Path novoArquivo = destinationDir.resolve(entry.getName());

                    if (entry.isDirectory()) {
                        Files.createDirectories(novoArquivo);
                    } else {
                        Files.createDirectories(novoArquivo.getParent());
                        try (OutputStream os = Files.newOutputStream(novoArquivo)) {
                            zis.transferTo(os);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao extrair ZIP: " + zipFilePath, e);
        }
    }

    public Path extractZipAndReturnCsv(Path zipFilePath, Path destinationDir) {
        extractZip(zipFilePath, destinationDir);

        try {
            Optional<Path> csvEncontrado =
                    Files.walk(destinationDir)
                            .filter(p -> p.toString().toLowerCase().endsWith(".csv"))
                            .findFirst();

            return csvEncontrado.orElseThrow(() ->
                    new RuntimeException("Nenhum CSV encontrado após extração do ZIP"));

        } catch (IOException e) {
            throw new RuntimeException("Erro ao localizar CSV extraído", e);
        }
    }
}
