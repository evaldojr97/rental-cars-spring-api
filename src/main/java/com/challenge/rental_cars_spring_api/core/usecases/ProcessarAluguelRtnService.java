package com.challenge.rental_cars_spring_api.core.usecases;

import com.challenge.rental_cars_spring_api.core.domain.Aluguel;
import com.challenge.rental_cars_spring_api.core.domain.Carro;
import com.challenge.rental_cars_spring_api.core.domain.Cliente;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.AluguelRepository;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.CarroRepository;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProcessarAluguelRtnService {

    private final AluguelRepository aluguelRepository;
    private final CarroRepository carroRepository;
    private final ClienteRepository clienteRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd"); // Formato de data esperado no arquivo .rtn

    @Transactional
    public ProcessamentoResultado processarArquivoRtn(String nomeArquivo) { // Método para processar o arquivo .rtn de aluguéis
        log.info("Iniciando processamento do arquivo: {}", nomeArquivo);
        
        ProcessamentoResultado resultado = new ProcessamentoResultado();
        List<Aluguel> alugueisParaSalvar = new ArrayList<>();
        
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(nomeArquivo)) { // Tenta carregar o arquivo do classpath
            if (inputStream == null) { // Verifica se o arquivo foi encontrado
                log.error("Arquivo {} não encontrado no classpath", nomeArquivo);
                resultado.adicionarErro("Arquivo " + nomeArquivo + " não encontrado");
                return resultado;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) { // Lê o arquivo linha por linha
                String linha;
                int numeroLinha = 0;
                
                while ((linha = reader.readLine()) != null) { // Lê cada linha do arquivo
                    numeroLinha++;
                    
                    if (linha.length() != 20) { // Verifica se a linha tem o tamanho correto
                        log.warn("Linha {} ignorada - tamanho incorreto: {}", numeroLinha, linha.length());
                        resultado.adicionarErro("Linha " + numeroLinha + " ignorada - tamanho incorreto");
                        continue;
                    }
                    
                    try { // Tenta processar a linha
                        Aluguel aluguel = processarLinha(linha, numeroLinha, resultado);
                        if (aluguel != null) {
                            alugueisParaSalvar.add(aluguel);
                            resultado.incrementarProcessados();
                        }
                    } catch (Exception e) { // Captura qualquer exceção ao processar a linha
                        log.error("Erro ao processar linha {}: {}", numeroLinha, e.getMessage());
                        resultado.adicionarErro("Erro na linha " + numeroLinha + ": " + e.getMessage());
                    }
                }
                
                // Salvar todos os alugueis válidos em lote
                if (!alugueisParaSalvar.isEmpty()) {
                    aluguelRepository.saveAll(alugueisParaSalvar);
                    log.info("Processamento concluído. {} aluguéis salvos com sucesso", alugueisParaSalvar.size());
                }
                
            }
        } catch (IOException e) { // Captura exceção de I/O ao ler o arquivo
            log.error("Erro ao ler o arquivo {}: {}", nomeArquivo, e.getMessage());
            resultado.adicionarErro("Erro ao ler o arquivo: " + e.getMessage());
        }
        
        return resultado;
    }

    private Aluguel processarLinha(String linha, int numeroLinha, ProcessamentoResultado resultado) { // Método para processar uma linha do arquivo .rtn
        try { 
            // Extrair dados da linha
            Long carroId = Long.parseLong(linha.substring(0, 2).trim());
            Long clienteId = Long.parseLong(linha.substring(2, 4).trim());
            String dataAluguelStr = linha.substring(4, 12);
            String dataDevolucaoStr = linha.substring(12, 20);

            // Validar e converter datas
            LocalDate dataAluguel = LocalDate.parse(dataAluguelStr, DATE_FORMATTER);
            LocalDate dataDevolucao = LocalDate.parse(dataDevolucaoStr, DATE_FORMATTER);

            if (dataDevolucao.isBefore(dataAluguel)) { // Verifica se a data de devolução é anterior à data de aluguel
                log.warn("Data de devolução anterior à data de aluguel na linha {}", numeroLinha);
                resultado.adicionarErro("Data de devolução inválida na linha " + numeroLinha);
                return null;
            }

            // Buscar carro e cliente
            Carro carro = carroRepository.findById(carroId).orElse(null);
            Cliente cliente = clienteRepository.findById(clienteId).orElse(null);

            if (carro == null) {
                log.warn("Carro com ID {} não encontrado na linha {}", carroId, numeroLinha);
                resultado.adicionarAlerta("Carro ID " + carroId + " não encontrado (linha " + numeroLinha + ")");
                return null;
            }

            if (cliente == null) {
                log.warn("Cliente com ID {} não encontrado na linha {}", clienteId, numeroLinha);
                resultado.adicionarAlerta("Cliente ID " + clienteId + " não encontrado (linha " + numeroLinha + ")");
                return null;
            }

            // Calcular valor do aluguel
            long diasAlugados = java.time.temporal.ChronoUnit.DAYS.between(dataAluguel, dataDevolucao) + 1;
            BigDecimal valor = carro.getVlrDiaria().multiply(BigDecimal.valueOf(diasAlugados));

            // Criar objeto Aluguel
            Aluguel aluguel = new Aluguel(
                null, // ID será gerado automaticamente
                carro,
                cliente,
                dataAluguel,
                dataDevolucao,
                valor,
                false // pago
            );

            return aluguel;

        } catch (NumberFormatException e) { // Captura exceção de conversão de número
            log.error("Erro ao converter número na linha {}: {}", numeroLinha, e.getMessage());
            resultado.adicionarErro("Erro de conversão na linha " + numeroLinha);
            return null;
        } catch (Exception e) { // Captura qualquer outra exceção inesperada
            log.error("Erro inesperado ao processar linha {}: {}", numeroLinha, e.getMessage());
            resultado.adicionarErro("Erro inesperado na linha " + numeroLinha);
            return null;
        }
    }

    public static class ProcessamentoResultado { // Classe para encapsular o resultado do processamento do arquivo .rtn
        private int processados = 0;
        private List<String> alertas = new ArrayList<>();
        private List<String> erros = new ArrayList<>();

        public void incrementarProcessados() {
            this.processados++;
        }

        public void adicionarAlerta(String alerta) {
            this.alertas.add(alerta);
        }

        public void adicionarErro(String erro) {
            this.erros.add(erro);
        }

        public int getProcessados() {
            return processados;
        }

        public List<String> getAlertas() {
            return alertas;
        }

        public List<String> getErros() {
            return erros;
        }

        public boolean isSucesso() {
            return erros.isEmpty();
        }
    }
}
