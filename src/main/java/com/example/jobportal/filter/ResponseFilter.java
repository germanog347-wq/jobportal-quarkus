package com.example.jobportal.filter;

import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.NewCookie;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;

public class ResponseFilter {

    // Header em que o BO devolve o token gerado no login
    public static final String HEADER_JWT = "jwt-token";
    // Deve coincidir com mp.jwt.token.cookie em application.properties
    public static final String COOKIE_JWT = "jwt-token";
    // Mesma validade do token (UsuarioBO.gerarJWT)
    public static final int VALIDADE_SEGUNDOS = 3600;

    /** Move o token JWT enviado no header "jwt-token" (gerado no login) para um cookie HttpOnly. */
    @ServerResponseFilter
    public void responseFilter(ContainerResponseContext responseContext) {
        String token = responseContext.getHeaderString(HEADER_JWT);
        // O token não deve ficar exposto no header da resposta, apenas no cookie
        responseContext.getHeaders().remove(HEADER_JWT);

        if (token == null || token.isBlank()) {
            return;
        }

        NewCookie cookie = new NewCookie.Builder(COOKIE_JWT)
                .value(token)
                .path("/")
                .maxAge(VALIDADE_SEGUNDOS)
                .secure(false)
                .httpOnly(true)
                .sameSite(NewCookie.SameSite.LAX)
                .build();

        responseContext.getHeaders().add("Set-Cookie", cookie);
    }
}
