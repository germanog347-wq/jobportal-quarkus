package com.example.jobportal.model;

import io.quarkus.qute.TemplateEnum;

@TemplateEnum
public enum StatusCandidatura {
    ENVIADA("Enviada"),
    EM_ANALISE("Em análise"),
    APROVADA("Aprovada"),
    REJEITADA("Rejeitada");

    private final String descricao;

    StatusCandidatura(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
