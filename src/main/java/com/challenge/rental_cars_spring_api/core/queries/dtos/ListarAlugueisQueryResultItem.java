package com.challenge.rental_cars_spring_api.core.queries.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
// DTO para representar um item de resultado da consulta de listagem de alugueis
public class ListarAlugueisQueryResultItem { 
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataAluguel;
    
    private String modeloCarro;
    
    private Long kmCarro;
    
    private String nomeCliente;
    
    private String telefoneCliente;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate dataDevolucao;
    
    private BigDecimal valor;
    
    private String pago;
}
