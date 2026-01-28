package br.com.intuitive.care.controller;

import br.com.intuitive.care.service.AnsFileDownloaderService;
import br.com.intuitive.care.service.AnsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnsController {

    private final AnsFileDownloaderService downloaderService;
    private final AnsService ansService;

    public AnsController(
            AnsFileDownloaderService downloaderService,
            AnsService ansService
    ) {
        this.downloaderService = downloaderService;
        this.ansService = ansService;
    }

    @PostMapping("/ans/download")
    public String downloadArquivosAns() {
        downloaderService.downloadUltimosTrimestres();
        return "Download dos arquivos da ANS iniciado com sucesso.";
    }

    @PostMapping("/ans/extract")
    public String extrairArquivosAns() {
        ansService.extrairArquivosZip();
        return "Extração dos arquivos ZIP concluída com sucesso.";
    }
}
