package com.example.jobportal.controller;

import com.example.jobportal.bo.AuditoriaBO;
import com.example.jobportal.model.Log;
import com.example.jobportal.model.TipoUsuario;
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

@Path("/logs")
public class AuditoriaController {

    @Inject
    AuditoriaBO auditoriaBO;
    @Inject
    SessaoService sessaoService;

    @CheckedTemplate(basePath = "")
    public static class Templates {
        public static native TemplateInstance logVisao(List<Log> logs, Usuario usuario);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response pagina(@CookieParam("usuarioId") String uid) {
        Usuario u = sessaoService.usuarioLogado(uid).orElse(null);
        if (u == null) return Response.seeOther(URI.create("/auth/login?erro=Fa%C3%A7a+login")).build();
        if (u.getTipo() != TipoUsuario.ADMIN) return Response.seeOther(URI.create("/homepage")).build();
        return Response.ok(Templates.logVisao(auditoriaBO.listarTodas(), u))
                .type(MediaType.TEXT_HTML).build();
    }
}
