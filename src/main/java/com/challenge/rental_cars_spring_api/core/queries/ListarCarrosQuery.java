package com.challenge.rental_cars_spring_api.core.queries;

import com.challenge.rental_cars_spring_api.core.queries.dtos.ListarCarrosQueryResultItem;
import com.challenge.rental_cars_spring_api.infrastructure.repositories.CarroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListarCarrosQuery {

    private final CarroRepository carroRepository;

    public List<ListarCarrosQueryResultItem> execute() {
        return carroRepository.findAll()
                .stream() // Converte a lista em um fluxo para processamento
                .map(ListarCarrosQueryResultItem::from) // Mapeia cada carro para o DTO correspondente
                .collect(Collectors.toList()); // Coleta os resultados em uma lista
    }
}
