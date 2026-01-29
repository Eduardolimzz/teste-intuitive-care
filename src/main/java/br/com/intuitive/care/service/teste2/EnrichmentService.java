package br.com.intuitive.care.service.teste2;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class EnrichmentService {

    public Map<String, String[]> carregarCadastro(String path) throws IOException {
        Map<String, String[]> map = new HashMap<>();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(path), StandardCharsets.UTF_8))) {

            br.readLine(); // header
            String linha;

            while ((linha = br.readLine()) != null) {
                String[] c = linha.split(";");
                String cnpj = c[0].replaceAll("\\D", "");

                map.put(cnpj, new String[]{
                        c[1], // RegistroANS
                        c[2], // Modalidade
                        c[3]  // UF
                });
            }
        }
        return map;
    }
}