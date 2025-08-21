package com.challenge.rental_cars_spring_api.access;

import com.challenge.rental_cars_spring_api.core.queries.ListarAlugueisQuery;
import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarAlugueisResponse;
import com.challenge.rental_cars_spring_api.core.usecases.ProcessarAluguelRtnService;
import com.challenge.rental_cars_spring_api.core.usecases.ProcessarAluguelRtnService.ProcessamentoResultado;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/alugueis")
@RequiredArgsConstructor
@Tag(name = "Aluguéis", description = "Endpoints para gerenciamento de aluguéis de carros")
public class AluguelRestController {

    private final ProcessarAluguelRtnService processarAluguelRtnService; // Serviço para processar arquivos .rtn
    private final ListarAlugueisQuery listarAlugueisQuery; // Serviço para listar alugueis

    @PostMapping("/processar") // Endpoint para processar o arquivo .rtn
    @Operation(summary = "Processar arquivo .rtn de aluguéis", 
               description = "Processa um arquivo .rtn contendo dados de aluguéis e popula a tabela ALUGUEL")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Processamento concluído com sucesso", 
                        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, 
                                         schema = @Schema(implementation = ProcessamentoResultado.class))),
            @ApiResponse(responseCode = "400", description = "Erro no processamento do arquivo", 
                        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", 
                        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<ProcessamentoResultado> processarArquivoRtn(
            @RequestParam(value = "nomeArquivo", defaultValue = "RentReport.rtn") String nomeArquivo) {
        
        ProcessamentoResultado resultado = processarAluguelRtnService.processarArquivoRtn(nomeArquivo); // Chama o serviço para processar o arquivo .rtn
        
        if (resultado.isSucesso()) { // Verifica se o processamento foi bem-sucedido
            return ResponseEntity.ok(resultado);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
        }
    }

    @GetMapping("/listar") // Endpoint para listar todos os alugueis
    @Operation(summary = "Listar todos os aluguéis", 
               description = "Retorna uma lista com todos os aluguéis cadastrados, incluindo detalhes do carro e cliente, e o valor total não pago")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de aluguéis retornada com sucesso", 
                        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, 
                                         schema = @Schema(implementation = ListarAlugueisResponse.class))),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", 
                        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    })
    public ResponseEntity<ListarAlugueisResponse> listarAlugueis() {
        ListarAlugueisResponse response = listarAlugueisQuery.executar(); // Chama o serviço para listar os alugueis
        return ResponseEntity.ok(response);
    }
}
