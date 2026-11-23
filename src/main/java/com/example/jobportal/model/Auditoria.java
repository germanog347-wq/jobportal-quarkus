package com.example.jobportal.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 80)
    public String acao;

    @Column(length = 255)
    public String detalhe;

    @Column(length = 150)
    public String usuarioEmail;

    @Column(nullable = false)
    public LocalDateTime dataHora = LocalDateTime.now();

    public Auditoria() {}

    public Auditoria(String acao, String detalhe, String usuarioEmail) {
        this.acao = acao;
        this.detalhe = detalhe;
        this.usuarioEmail = usuarioEmail;
    }
}
