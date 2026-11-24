package com.example.jobportal.service;

import com.example.jobportal.dao.UsuarioDAO;
import com.example.jobportal.filter.ResponseFilter;
import com.example.jobportal.model.Usuario;
import io.quarkus.security.UnauthorizedException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.NewCookie;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.util.Optional;

/**
 * Identifica o usuário logado a partir do JWT lido do cookie "jwt-token".
 */
@RequestScoped
public class SessaoService {

    @Inject
    JsonWebToken jwt;

    @Inject
    UsuarioDAO usuarioDAO;

    /** Usuário do token, se houver token válido e o usuário ainda existir e estiver ativo. */
    public Optional<Usuario> usuarioLogado() {
        try {
            if (jwt == null || jwt.getRawToken() == null) {
                return Optional.empty();
            }
            Object id = jwt.getClaim("id");
            if (id == null) {
                return Optional.empty();
            }
            return usuarioDAO.buscarPorId(Integer.parseInt(id.toString()))
                    .filter(Usuario::isAtivo);
        } catch (RuntimeException e) {
            // Token vencido/inválido: em páginas públicas tratamos como visitante
            return Optional.empty();
        }
    }

    /** Usuário do token; usado em endpoints protegidos por @RolesAllowed. */
    public Usuario usuarioObrigatorio() {
        return usuarioLogado().orElseThrow(UnauthorizedException::new);
    }

    /** Cookie que apaga o JWT do navegador (logout). */
    public static NewCookie cookieLogout() {
        return new NewCookie.Builder(ResponseFilter.COOKIE_JWT)
                .value("")
                .path("/")
                .maxAge(0)
                .secure(false)
                .httpOnly(true)
                .sameSite(NewCookie.SameSite.LAX)
                .build();
    }
}
