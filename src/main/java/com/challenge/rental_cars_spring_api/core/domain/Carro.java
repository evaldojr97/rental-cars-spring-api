package com.challenge.rental_cars_spring_api.core.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "carro")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Carro implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "modelo", length = 50, nullable = false)
    private String modelo;

    @Column(name = "ano", length = 4, nullable = false)
    private String ano;

    @Column(name = "qtd_passageiros", nullable = false)
    private Long qtdPassageiros;

    @Column(name = "km", nullable = false)
    private Long km;

    @Column(name = "fabricante", length = 50, nullable = false)
    private String fabricante;

    @Column(name = "vlr_diaria", precision = 7, scale = 2, nullable = false)
    private BigDecimal vlrDiaria;
}
