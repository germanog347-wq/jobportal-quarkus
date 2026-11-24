package com.example.jobportal.service;

import com.example.jobportal.dao.UsuarioDAO;
import com.example.jobportal.model.TipoUsuario;
import com.example.jobportal.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.NewCookie;
import java.util.Optional;

@ApplicationScoped
public class SessaoService {

    public static final String COOKIE_NAME = "usuarioId";
    private static final int MAX_AGE = 8 * 60 * 60; // 8 horas

    @Inject
    UsuarioDAO usuarioDAO;

    public Optional<Usuario> usuarioLogado(String usuarioIdStr) {
        if (usuarioIdStr == null || usuarioIdStr.isBlank()) {
            return Optional.empty();
        }
        try {
            Integer id = Integer.parseInt(usuarioIdStr.trim());
            return usuarioDAO.buscarPorId(id).filter(Usuario::isAtivo);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /** Cookie HttpOnly + SameSite=Lax — não acessível via JS (mais seguro). */
    public NewCookie cookieLogin(Integer usuarioId) {
        return new NewCookie.Builder(COOKIE_NAME)
                .value(String.valueOf(usuarioId))
                .path("/")
                .maxAge(MAX_AGE)
                .httpOnly(true)
                .sameSite(NewCookie.SameSite.LAX)
                .build();
    }

    public NewCookie cookieLogout() {
        return new NewCookie.Builder(COOKIE_NAME)
                .value("")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .sameSite(NewCookie.SameSite.LAX)
                .build();
    }

    public boolean isAdmin(String usuarioIdStr) {
        return usuarioLogado(usuarioIdStr).map(u -> u.getTipo() == TipoUsuario.ADMIN).orElse(false);
    }
}
