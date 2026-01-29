package br.com.intuitive.care.controller.teste1;

import br.com.intuitive.care.service.teste1.AnsFileDownloaderService;
import br.com.intuitive.care.service.teste1.AnsService;
import br.com.intuitive.care.service.teste1.ConsolidacaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ans")
public class AnsController {

    private static final Logger logger = LoggerFactory.getLogger(AnsController.class);

    private final AnsFileDownloaderService downloaderService;
    private final AnsService ansService;
    private final ConsolidacaoService consolidacaoService;

    public AnsController(AnsFileDownloaderService downloaderService,
                         AnsService ansService,
                         ConsolidacaoService consolidacaoService) {
        this.downloaderService = downloaderService;
        this.ansService = ansService;
        this.consolidacaoService = consolidacaoService;
    }

    @PostMapping("/processar-completo")
    public ResponseEntity<Map<String, Object>> processarCompleto() {
        Map<String, Object> response = new HashMap<>();
        try {
            logger.info("Iniciando processamento completo");
            downloaderService.downloadUltimosTrimestres();
            response.put("download", "OK");
            ansService.extrairArquivosZip();
            response.put("extracao", "OK");
            String arquivoFinal = consolidacaoService.consolidarDespesas();
            response.put("consolidacao", "OK");
            response.put("arquivo", arquivoFinal);
            response.put("status", "SUCCESS");
            logger.info("Processamento finalizado");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Erro no processamento: {}", e.getMessage());
            response.put("status", "ERROR");
            response.put("erro", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/download")
    public ResponseEntity<Map<String, String>> downloadArquivos() {
        try {
            downloaderService.downloadUltimosTrimestres();
            return ResponseEntity.ok(Map.of("status", "SUCCESS"));
        } catch (Exception e) {
            logger.error("Erro no download: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("status", "ERROR", "erro", e.getMessage()));
        }
    }

    @PostMapping("/extrair")
    public ResponseEntity<Map<String, String>> extrairArquivos() {
        try {
            ansService.extrairArquivosZip();
            return ResponseEntity.ok(Map.of("status", "SUCCESS"));
        } catch (Exception e) {
            logger.error("Erro na extração: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("status", "ERROR", "erro", e.getMessage()));
        }
    }

    @PostMapping("/consolidar")
    public ResponseEntity<Map<String, Object>> consolidarDespesas() {
        try {
            String arquivo = consolidacaoService.consolidarDespesas();
            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "arquivo", arquivo
            ));
        } catch (Exception e) {
            logger.error("Erro na consolidação: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("status", "ERROR", "erro", e.getMessage()));
        }
    }
}