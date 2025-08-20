package com.challenge.rental_cars_spring_api.core.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "aluguel")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Aluguel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carro_id", nullable = false)
    private Carro carro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "data_aluguel", nullable = false)
    private LocalDate dataAluguel;

    @Column(name = "data_devolucao", nullable = false)
    private LocalDate dataDevolucao;

    @Column(name = "valor", precision = 7, scale = 2, nullable = false)
    private BigDecimal valor;

    @Column(name = "pago", nullable = false)
    private Boolean pago;
}
