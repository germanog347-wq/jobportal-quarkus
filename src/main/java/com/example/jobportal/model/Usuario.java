package com.example.jobportal.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 100)
    public String nome;

    @Column(nullable = false, unique = true, length = 150)
    public String email;

    @Column(nullable = false, length = 100)
    public String senha;

    @Column(length = 14)
    public String cpf;

    @Column(name = "data_nascimento")
    public LocalDate dataNascimento;

    @Column(length = 20)
    public String telefone;

    @Column(length = 100)
    public String cidade;

    @Column(nullable = false)
    public boolean isAdmin = false;

    @Column(nullable = false)
    public boolean ativo = true;

    @Column(name = "criado_em")
    public LocalDateTime criadoEm = LocalDateTime.now();

    public Usuario() {}

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }
}
