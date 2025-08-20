package com.challenge.rental_cars_spring_api.core.queries;

import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarAlugueisQueryResultItem;
import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarAlugueisResponse;
import com.challenge.rental_cars_spring_api.core.domain.Aluguel;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.AluguelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListarAlugueisQuery { // Classe de serviço para listar alugueis

    private final AluguelRepository aluguelRepository; // Repositório para acessar dados de alugueis

    public ListarAlugueisResponse executar() {
        List<Aluguel> alugueis = aluguelRepository.findAll(); // Busca todos os alugueis no repositório
        
        List<ListarAlugueisQueryResultItem> alugueisDto = alugueis.stream() // Converte cada aluguel para um DTO
            .map(aluguel -> new ListarAlugueisQueryResultItem(
                aluguel.getDataAluguel(),
                aluguel.getCarro().getModelo(),
                aluguel.getCarro().getKm(),
                aluguel.getCliente().getNome(),
                formatarTelefone(aluguel.getCliente().getTelefone()),
                aluguel.getDataDevolucao(),
                aluguel.getValor(),
                aluguel.getPago() ? "SIM" : "NAO"
            ))
            .collect(Collectors.toList());
        
        BigDecimal valorTotalNaoPago = alugueis.stream()
            .filter(aluguel -> !aluguel.getPago())
            .map(Aluguel::getValor)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return new ListarAlugueisResponse(alugueisDto, valorTotalNaoPago); // Retorna a resposta com a lista de DTOs e o valor total não pago
    }
    
    private String formatarTelefone(String telefone) { // Formata o telefone no padrão +XX(XX)XXXXX-XXXX
        if (telefone == null || telefone.length() != 13) { // Verifica se o telefone é nulo ou não tem o tamanho esperado
            return telefone;
        }
        
        // Formato esperado: +XX(XX)XXXXX-XXXX
        String ddi = telefone.substring(0, 2); 
        String ddd = telefone.substring(2, 4);
        String numeroParte1 = telefone.substring(4, 9);
        String numeroParte2 = telefone.substring(9, 13);
        
        return String.format("+%s(%s)%s-%s", ddi, ddd, numeroParte1, numeroParte2); // Retorna o telefone formatado
    }
}
