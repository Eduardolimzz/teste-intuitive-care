package br.com.intuitive.care.controller.teste2;

import br.com.intuitive.care.service.teste2.DataProcessingPipelineService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/teste-2")
public class Teste2Controller {

    private final DataProcessingPipelineService pipelineService;

    public Teste2Controller(DataProcessingPipelineService pipelineService) {
        this.pipelineService = pipelineService;
    }

    @GetMapping("/executar")
    public String executar() throws Exception {
        pipelineService.executar();
        return "Teste 2 executado com sucesso.";
    }
}