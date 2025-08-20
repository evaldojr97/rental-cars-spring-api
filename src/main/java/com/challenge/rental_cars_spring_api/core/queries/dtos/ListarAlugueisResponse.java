package com.challenge.rental_cars_spring_api.core.queries.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@AllArgsConstructor
public class ListarAlugueisResponse {
    private List<ListarAlugueisQueryResultItem> alugueis; // Lista de itens de aluguel
    private BigDecimal valorTotalNaoPago; // Valor total dos alugueis não pagos
}
