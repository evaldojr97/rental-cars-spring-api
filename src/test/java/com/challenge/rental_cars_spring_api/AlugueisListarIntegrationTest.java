package com.challenge.rental_cars_spring_api;

import com.challenge.rental_cars_spring_api.infrastructure.repositories.AluguelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.anyOf;

@SpringBootTest
@AutoConfigureMockMvc
class AlugueisListarIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AluguelRepository aluguelRepository;

    @BeforeEach
    void setup() {
        aluguelRepository.deleteAll();
        // Insere um aluguel manualmente para validar listagem
        var aluguel = new com.challenge.rental_cars_spring_api.core.domain.Aluguel(
            null,
            new com.challenge.rental_cars_spring_api.core.domain.Carro(1L, "ModelX", "2024", 4L, 5000L, "Fab", BigDecimal.valueOf(100)),
            new com.challenge.rental_cars_spring_api.core.domain.Cliente(1L, "Cliente Teste", "12345678900", "98765432100", "11987654321"),
            LocalDate.of(2025, 1, 1),
            LocalDate.of(2025, 1, 5),
            BigDecimal.valueOf(400),
            false
        );
        aluguelRepository.save(aluguel);
    }

    @Test
    void deveListarAlugueisComCamposFormatados() throws Exception {
        mockMvc.perform(get("/alugueis/listar"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.alugueis", hasSize(1)))
            .andExpect(jsonPath("$.alugueis[0].modeloCarro").value("GOL"))
            .andExpect(jsonPath("$.alugueis[0].kmCarro").value(154748))
            .andExpect(jsonPath("$.alugueis[0].nomeCliente").value("Lucas Joaquim Almada"))
            .andExpect(jsonPath("$.alugueis[0].telefoneCliente").value("65991842240"))
            .andExpect(jsonPath("$.alugueis[0].pago").value("NAO"))
            .andExpect(jsonPath("$.valorTotalNaoPago").value(400.00));
    }

}
