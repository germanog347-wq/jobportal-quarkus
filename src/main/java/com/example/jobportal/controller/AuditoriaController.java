package com.example.jobportal.controller;

import com.example.jobportal.bo.AuditoriaBO;
import com.example.jobportal.model.Auditoria;
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

@Path("/admin/auditoria")
public class AuditoriaController {

    @Inject
    AuditoriaBO auditoriaBO;
    @Inject
    SessaoService sessaoService;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance auditoria(List<Auditoria> logs, Usuario usuario);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response pagina(@CookieParam("usuarioId") String uid) {
        Usuario u = sessaoService.usuarioLogado(uid).orElse(null);
        if (u == null) return Response.seeOther(URI.create("/login?erro=Faça%20login")).build();
        if (!u.isAdmin) return Response.seeOther(URI.create("/")).build();
        return Response.ok(Templates.auditoria(auditoriaBO.listarTodas(), u))
                .type(MediaType.TEXT_HTML).build();
    }
}
