package com.example.jobportal.config;

import io.quarkus.qute.TemplateExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Métodos disponíveis nos templates Qute.
 * Ex.: {vaga.dataExpiracao.formatado} → 31/12/2026
 */
@TemplateExtension
public class TemplateExtensions {

    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    static String formatado(LocalDate data) {
        return data == null ? "" : data.format(DATA);
    }

    static String formatado(LocalDateTime dataHora) {
        return dataHora == null ? "" : dataHora.format(DATA_HORA);
    }
}
