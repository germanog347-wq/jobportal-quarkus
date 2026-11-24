package com.example.jobportal.service;

import com.example.jobportal.dao.UsuarioDAO;
import com.example.jobportal.filter.ResponseFilter;
import com.example.jobportal.model.Usuario;
import io.quarkus.security.UnauthorizedException;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.NewCookie;
import org.eclipse.microprofile.jwt.JsonWebToken;

/**
 * Identifica o usuário logado a partir do JWT lido do cookie "jwt-token".
 */
@RequestScoped
public class SessaoService {

    @Inject
    JsonWebToken jwt;

    @Inject
    UsuarioDAO usuarioDAO;

    /**
     * Usuário do token, se houver token válido e o usuário ainda existir e estiver ativo.
     * Retorna null para visitante (sem login, token inválido ou usuário inativo).
     */
    public Usuario usuarioLogado() {
        try {
            if (jwt == null || jwt.getRawToken() == null) {
                return null;
            }
            Object id = jwt.getClaim("id");
            if (id == null) {
                return null;
            }
            Usuario usuario = usuarioDAO.buscarPorId(Integer.parseInt(id.toString()));
            if (usuario == null || !usuario.isAtivo()) {
                return null;
            }
            return usuario;
        } catch (RuntimeException e) {
            // Token vencido/inválido: em páginas públicas tratamos como visitante
            return null;
        }
    }

    /** Usuário do token; usado em endpoints protegidos por @RolesAllowed. */
    public Usuario usuarioObrigatorio() {
        Usuario usuario = usuarioLogado();
        if (usuario == null) {
            throw new UnauthorizedException();
        }
        return usuario;
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
