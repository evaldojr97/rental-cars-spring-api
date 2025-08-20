package com.challenge.rental_cars_spring_api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collection;

@SpringBootTest
@AutoConfigureMockMvc
class CarrosIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveListarTodosCarros() throws Exception {
        mockMvc.perform(get("/carros")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", isA(Collection.class)))
            .andExpect(jsonPath("$.length()", greaterThan(0)));
    }
}
