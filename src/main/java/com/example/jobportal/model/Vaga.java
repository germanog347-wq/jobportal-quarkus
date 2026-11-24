package com.example.jobportal.model;

import com.example.jobportal.dto.VagaDTO;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vaga")
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 150)
    private String titulo;
    @Column(nullable = false, length = 2000)
    private String descricao;
    @Column(nullable = false, length = 100)
    private String empresa;
    @Column(nullable = false, length = 100)
    private String localizacao;
    @Column(name = "faixa_salarial", length = 50)
    private String faixaSalarial;
    @Column(name = "data_expiracao")
    private LocalDate dataExpiracao;
    @Column(nullable = false)
    private boolean ativa = true;
    @Column(name = "criado_em")
    private LocalDateTime criadoEm = LocalDateTime.now();

    //Construtores
    public Vaga() {
    }

    public Vaga(VagaDTO dto) {
        this.titulo = limpar(dto.titulo);
        this.descricao = limpar(dto.descricao);
        this.empresa = limpar(dto.empresa);
        this.localizacao = limpar(dto.localizacao);
        this.faixaSalarial = dto.faixaSalarial;
        this.dataExpiracao = dto.getDataExpiracaoAsDate();
        this.ativa = dto.isAtiva();
    }

    private static String limpar(String valor) {
        return valor == null ? null : valor.trim();
    }

    //Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getFaixaSalarial() {
        return faixaSalarial;
    }

    public void setFaixaSalarial(String faixaSalarial) {
        this.faixaSalarial = faixaSalarial;
    }

    public LocalDate getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(LocalDate dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}
