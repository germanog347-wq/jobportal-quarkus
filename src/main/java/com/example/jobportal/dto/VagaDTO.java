package com.example.jobportal.dto;

import jakarta.ws.rs.FormParam;
import java.time.LocalDate;

public class VagaDTO {
    public Long id;
    @FormParam("titulo")
    public String titulo;
    @FormParam("descricao")
    public String descricao;
    @FormParam("empresa")
    public String empresa;
    @FormParam("localizacao")
    public String localizacao;
    @FormParam("faixaSalarial")
    public String faixaSalarial;
    @FormParam("dataExpiracao")
    public String dataExpiracao;
    @FormParam("ativa")
    public String ativa;

    public LocalDate getDataExpiracaoAsDate() {
        if (dataExpiracao == null || dataExpiracao.isBlank()) return null;
        return LocalDate.parse(dataExpiracao);
    }

    public boolean isAtiva() {
        return "on".equals(ativa) || "true".equals(ativa);
    }
}
