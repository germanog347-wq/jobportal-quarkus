package com.example.jobportal.controller;

import com.example.jobportal.bo.CandidaturaBO;
import com.example.jobportal.model.Candidatura;
import com.example.jobportal.model.Usuario;
import com.example.jobportal.service.SessaoService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/minhas-candidaturas")
public class CandidaturasController {

    @Inject
    CandidaturaBO candidaturaBO;
    @Inject
    SessaoService sessaoService;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance minhas(List<Candidatura> candidaturas, Usuario usuario);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response pagina(@CookieParam("usuarioId") String uid) {
        Usuario u = sessaoService.usuarioLogado(uid).orElse(null);
        if (u == null) {
            return Response.seeOther(URI.create("/login?erro=" + enc("Faça login"))).build();
        }
        return Response.ok(Templates.minhas(candidaturaBO.listarMinhas(u), u))
                .type(MediaType.TEXT_HTML).build();
    }

    private static String enc(String v) {
        return v == null ? "" : java.net.URLEncoder.encode(v, java.nio.charset.StandardCharsets.UTF_8);
    }
}
