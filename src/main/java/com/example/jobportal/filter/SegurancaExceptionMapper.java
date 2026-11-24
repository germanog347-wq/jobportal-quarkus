package com.example.jobportal.filter;

import com.example.jobportal.service.SessaoService;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.ForbiddenException;
import io.quarkus.security.UnauthorizedException;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Converte as falhas de segurança do @RolesAllowed em redirecionamentos,
 * já que as páginas são HTML (o padrão seria responder 401/403).
 */
public class SegurancaExceptionMapper {

    /** Sem token (não logado) ou usuário do token não existe mais. */
    @ServerExceptionMapper
    public Response naoAutenticado(UnauthorizedException e) {
        return paraLogin("Faça login para continuar");
    }

    /** Token inválido ou vencido no cookie. */
    @ServerExceptionMapper
    public Response tokenInvalido(AuthenticationFailedException e) {
        return paraLogin("Sua sessão expirou. Entre novamente");
    }

    /** Logado, mas sem o perfil exigido (ex.: candidato acessando área de admin). */
    @ServerExceptionMapper
    public Response semPermissao(ForbiddenException e) {
        return Response.seeOther(URI.create("/homepage?erro=" + enc("Acesso negado"))).build();
    }

    private Response paraLogin(String mensagem) {
        return Response.seeOther(URI.create("/auth/login?erro=" + enc(mensagem)))
                .cookie(SessaoService.cookieLogout())
                .build();
    }

    private static String enc(String v) {
        return URLEncoder.encode(v, StandardCharsets.UTF_8);
    }
}
