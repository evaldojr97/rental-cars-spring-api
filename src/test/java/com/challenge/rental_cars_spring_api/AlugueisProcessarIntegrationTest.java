package com.challenge.rental_cars_spring_api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
@AutoConfigureMockMvc
class AlugueisProcessarIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private MockMultipartFile file;

    @BeforeEach
    void setup() {
        // Gera um arquivo .rtn com dois registros válidos
        String conteudo = "01 12025010120250105\n02 23025020120250110";
        file = new MockMultipartFile(
            "file",
            "RentReport.rtn",
            "text/plain",
            conteudo.getBytes(StandardCharsets.UTF_8)
        );
    }

    @Test
    void deveProcessarArquivoRtnERetornarEstatisticas() throws Exception {
        mockMvc.perform(multipart("/alugueis/processar")
                .file(file))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.processados").value(9))       // 9 aluguéis salvos com sucesso
            .andExpect(jsonPath("$.sucesso").value(true))        // flag de sucesso
            .andExpect(jsonPath("$.alertas", hasSize(3)))        // 3 alerts for missing entities
            .andExpect(jsonPath("$.erros", hasSize(0)));         // quantidade de erros esperada
    }
}
