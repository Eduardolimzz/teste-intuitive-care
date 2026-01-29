package br.com.intuitive.care.teste2;

import br.com.intuitive.care.service.teste2.ValidationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários do ValidationService - Teste 2
 */
class ValidationServiceTest {

    private final ValidationService service = new ValidationService();

    // CNPJ

    @Test
    void deveValidarCnpjCorreto() {
        assertTrue(service.cnpjValido("12.345.678/0001-95"));
    }

    @Test
    void deveInvalidarCnpjIncorreto() {
        assertFalse(service.cnpjValido("11.111.111/1111-11"));
    }

    @Test
    void deveInvalidarCnpjNulo() {
        assertFalse(service.cnpjValido(null));
    }

    // Valor

    @Test
    void deveValidarValorPositivo() {
        assertTrue(service.valorValido(10.50));
    }

    @Test
    void deveInvalidarValorZeroOuNegativo() {
        assertFalse(service.valorValido(0));
        assertFalse(service.valorValido(-10));
    }

    // Razão Social

    @Test
    void deveValidarRazaoSocialValida() {
        assertTrue(service.razaoSocialValida("UNIMED BRASIL"));
    }

    @Test
    void deveInvalidarRazaoSocialVaziaOuNula() {
        assertFalse(service.razaoSocialValida(""));
        assertFalse(service.razaoSocialValida("   "));
        assertFalse(service.razaoSocialValida(null));
    }
}
