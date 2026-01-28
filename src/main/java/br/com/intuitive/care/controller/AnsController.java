package br.com.intuitive.care.controller;

import br.com.intuitive.care.service.AnsFileDownloaderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnsController {

    private final AnsFileDownloaderService downloaderService;

    public AnsController(AnsFileDownloaderService downloaderService) {
        this.downloaderService = downloaderService;
    }

    @PostMapping("/ans/download")
    public String downloadArquivosAns() {
        downloaderService.downloadUltimosTrimestres();
        return "Download dos arquivos da ANS iniciado com sucesso.";
    }
}
