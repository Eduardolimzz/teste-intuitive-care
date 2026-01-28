package br.com.intuitive.care.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AnsService {

    private static final String BASE_URL =
            "https://dadosabertos.ans.gov.br/FTP/PDA/";

    private final RestTemplate restTemplate;

    public AnsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String buscarConteudoRaiz() {
        return restTemplate.getForObject(BASE_URL, String.class);
    }
}

