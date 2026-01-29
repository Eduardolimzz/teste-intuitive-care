package br.com.intuitive.care.service.teste2;

import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    public boolean razaoSocialValida(String razao) {
        return razao != null && !razao.trim().isEmpty();
    }

    public boolean valorValido(double valor) {
        return valor > 0;
    }

    public boolean cnpjValido(String cnpj) {
        if (cnpj == null) return false;
        cnpj = cnpj.replaceAll("\\D", "");
        if (cnpj.length() != 14) return false;

        int[] peso1 = {5,4,3,2,9,8,7,6,5,4,3,2};
        int[] peso2 = {6,5,4,3,2,9,8,7,6,5,4,3,2};

        return calc(cnpj, peso1) == Character.getNumericValue(cnpj.charAt(12)) &&
                calc(cnpj, peso2) == Character.getNumericValue(cnpj.charAt(13));
    }

    private int calc(String cnpj, int[] peso) {
        int soma = 0;
        for (int i = 0; i < peso.length; i++) {
            soma += Character.getNumericValue(cnpj.charAt(i)) * peso[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
