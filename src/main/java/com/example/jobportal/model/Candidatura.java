package com.example.jobportal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "candidaturas")
public class Candidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_id", nullable = false)
    public Usuario usuario;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vaga_id", nullable = false)
    public Vaga vaga;

    @Column(length = 1000)
    public String mensagem;

    @Column(nullable = false, length = 30)
    public String status = "ENVIADA";

    @Column(name = "criado_em")
    public LocalDateTime criadoEm = LocalDateTime.now();

    public Candidatura() {}
}
