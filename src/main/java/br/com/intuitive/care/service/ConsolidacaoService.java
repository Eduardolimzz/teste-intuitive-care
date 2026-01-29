package br.com.intuitive.care.service;

import br.com.intuitive.care.model.DespesaRecord;
import org.apache.commons.csv.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ConsolidacaoService {

    private static final Logger logger = LoggerFactory.getLogger(ConsolidacaoService.class);

    private static final Path PROCESSED_BASE =
            Paths.get("data/processed/demonstracoes_contabeis/2025");

    private static final Path OUTPUT_DIR =
            Paths.get("data/output");

    private static final char[] DELIMITADORES = {';', ','};

    public String consolidarDespesas() throws IOException {

        logger.info("Iniciando consolidação de despesas");

        if (!Files.exists(PROCESSED_BASE)) {
            throw new IOException("Diretório processado não encontrado: " + PROCESSED_BASE);
        }

        Files.createDirectories(OUTPUT_DIR);

        List<DespesaRecord> registros = new ArrayList<>();

        try (DirectoryStream<Path> trimestres = Files.newDirectoryStream(PROCESSED_BASE)) {
            for (Path trimestreDir : trimestres) {
                if (Files.isDirectory(trimestreDir)) {
                    registros.addAll(processarTrimestre(trimestreDir));
                }
            }
        }

        RelatorioInconsistencias relatorio = analisarInconsistencias(registros);

        Path csvFinal = OUTPUT_DIR.resolve("consolidado_despesas.csv");
        salvarCsv(registros, csvFinal);

        Path zipFinal = OUTPUT_DIR.resolve("consolidado_despesas.zip");
        compactarCsv(csvFinal, zipFinal);

        logger.info(relatorio.toString());
        logger.info("Consolidação finalizada");

        return zipFinal.toString();
    }

    private List<DespesaRecord> processarTrimestre(Path trimestreDir) throws IOException {
        List<DespesaRecord> registros = new ArrayList<>();
        String trimestre = trimestreDir.getFileName().toString();

        for (Path csv : encontrarCsvs(trimestreDir)) {
            registros.addAll(lerCsv(csv, trimestre));
        }

        return registros;
    }

    private List<Path> encontrarCsvs(Path diretorio) throws IOException {
        List<Path> arquivos = new ArrayList<>();

        Files.walk(diretorio)
                .filter(Files::isRegularFile)
                .filter(p -> p.getFileName().toString().toLowerCase().endsWith(".csv"))
                .forEach(arquivos::add);

        return arquivos;
    }

    private List<DespesaRecord> lerCsv(Path arquivo, String trimestre) throws IOException {
        List<DespesaRecord> registros = new ArrayList<>();

        String ano = extrairAno(trimestre);
        String trimestreNumero = extrairTrimestre(trimestre);

        for (char delimitador : DELIMITADORES) {
            try (Reader reader = Files.newBufferedReader(arquivo, StandardCharsets.UTF_8)) {

                CSVParser parser = CSVFormat.DEFAULT.builder()
                        .setHeader()
                        .setSkipHeaderRecord(true)
                        .setIgnoreHeaderCase(true)
                        .setTrim(true)
                        .setDelimiter(delimitador)
                        .build()
                        .parse(reader);

                for (CSVRecord record : parser) {
                    DespesaRecord despesa = extrairDespesa(record, trimestreNumero, ano);
                    if (despesa != null) {
                        registros.add(despesa);
                    }
                }

                if (!registros.isEmpty()) {
                    break;
                }

            } catch (Exception ignored) {
            }
        }

        return registros;
    }

    private DespesaRecord extrairDespesa(CSVRecord record, String trimestre, String ano) {

        String identificador = buscarValor(record,
                "reg_ans", "REG_ANS", "cnpj", "CNPJ", "cd_operadora");

        String descricao = buscarValor(record,
                "descricao", "DESCRICAO", "desc_conta", "nm_conta");

        if (identificador == null || descricao == null) {
            return null;
        }

        String descricaoUpper = descricao.toUpperCase();
        if (!descricaoUpper.contains("DESPESA")
                && !descricaoUpper.contains("SINISTRO")
                && !descricaoUpper.contains("CUSTO")
                && !descricaoUpper.contains("RECEITA")) {
            return null;
        }

        String valorStr = buscarValor(record,
                "vl_saldo_inicial", "VL_SALDO_INICIAL",
                "vl_saldo_final", "VL_SALDO_FINAL",
                "valor", "VALOR");

        BigDecimal valor = parseValor(valorStr);
        String cnpj = identificador.replaceAll("\\D", "");

        return new DespesaRecord(cnpj, descricao, trimestre, ano, valor);
    }

    private BigDecimal parseValor(String valor) {
        if (valor == null || valor.isBlank()) {
            return BigDecimal.ZERO;
        }

        try {
            String normalizado = valor.replace(".", "")
                    .replace(",", ".")
                    .replaceAll("[^0-9.-]", "");
            return new BigDecimal(normalizado);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private String buscarValor(CSVRecord record, String... colunas) {
        for (String coluna : colunas) {
            if (record.isMapped(coluna)) {
                String valor = record.get(coluna);
                if (valor != null && !valor.isBlank()) {
                    return valor.trim();
                }
            }
        }
        return null;
    }

    private String extrairAno(String trimestre) {
        return trimestre.replaceAll("\\D", "");
    }

    private String extrairTrimestre(String trimestre) {
        return String.valueOf(trimestre.charAt(0));
    }

    private RelatorioInconsistencias analisarInconsistencias(List<DespesaRecord> registros) {

        RelatorioInconsistencias relatorio = new RelatorioInconsistencias();

        Map<String, Set<String>> cnpjRazoes = new HashMap<>();

        for (DespesaRecord r : registros) {
            cnpjRazoes
                    .computeIfAbsent(r.getCnpj(), k -> new HashSet<>())
                    .add(r.getRazaoSocial());
        }

        for (DespesaRecord r : registros) {
            if (cnpjRazoes.getOrDefault(r.getCnpj(), Set.of()).size() > 1) {
                r.marcarInconsistencia("CNPJ com múltiplas descrições");
                relatorio.cnpjsDuplicados++;
            }

            if (r.getValorDespesas().compareTo(BigDecimal.ZERO) <= 0) {
                r.marcarInconsistencia("Valor zerado ou negativo");
                relatorio.valoresInvalidos++;
            }

            if (r.getCnpj() == null || r.getCnpj().length() < 6) {
                r.marcarInconsistencia("CNPJ inválido");
                relatorio.cnpjsInvalidos++;
            }
        }

        relatorio.totalRegistros = registros.size();
        relatorio.registrosComInconsistencia = (int) registros.stream()
                .filter(DespesaRecord::isPossuiInconsistencia)
                .count();

        return relatorio;
    }

    private void salvarCsv(List<DespesaRecord> registros, Path destino) throws IOException {

        try (BufferedWriter writer = Files.newBufferedWriter(destino, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer,
                     CSVFormat.DEFAULT.withHeader(
                             "CNPJ", "RazaoSocial", "Trimestre", "Ano",
                             "ValorDespesas", "PossuiInconsistencia", "TipoInconsistencia"))) {

            for (DespesaRecord r : registros) {
                printer.printRecord(
                        r.getCnpj(),
                        r.getRazaoSocial(),
                        r.getTrimestre(),
                        r.getAno(),
                        r.getValorDespesas(),
                        r.isPossuiInconsistencia() ? "SIM" : "NAO",
                        r.getTipoInconsistencia()
                );
            }
        }
    }

    private void compactarCsv(Path csv, Path zip) throws IOException {

        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zip))) {
            zos.putNextEntry(new ZipEntry(csv.getFileName().toString()));
            Files.copy(csv, zos);
            zos.closeEntry();
        }
    }

    private static class RelatorioInconsistencias {

        int totalRegistros;
        int cnpjsDuplicados;
        int valoresInvalidos;
        int cnpjsInvalidos;
        int registrosComInconsistencia;

        @Override
        public String toString() {
            return """
                    
                    ===== RELATÓRIO DE INCONSISTÊNCIAS =====
                    Total de registros: %d
                    CNPJs duplicados: %d
                    Valores inválidos: %d
                    CNPJs inválidos: %d
                    Registros com inconsistência: %d
                    =====================================
                    """.formatted(
                    totalRegistros,
                    cnpjsDuplicados,
                    valoresInvalidos,
                    cnpjsInvalidos,
                    registrosComInconsistencia
            );
        }
    }
}
