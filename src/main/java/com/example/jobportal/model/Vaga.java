package com.example.jobportal.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vagas")
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 150)
    public String titulo;

    @Column(nullable = false, length = 2000)
    public String descricao;

    @Column(nullable = false, length = 100)
    public String empresa;

    @Column(nullable = false, length = 100)
    public String localizacao;

    @Column(name = "faixa_salarial", length = 50)
    public String faixaSalarial;

    @Column(name = "data_expiracao")
    public LocalDate dataExpiracao;

    @Column(nullable = false)
    public boolean ativa = true;

    @Column(name = "criado_em")
    public LocalDateTime criadoEm = LocalDateTime.now();

    public Vaga() {}

    public Vaga(String titulo, String descricao, String empresa, String localizacao,
                String faixaSalarial, LocalDate dataExpiracao) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.empresa = empresa;
        this.localizacao = localizacao;
        this.faixaSalarial = faixaSalarial;
        this.dataExpiracao = dataExpiracao;
    }
}
