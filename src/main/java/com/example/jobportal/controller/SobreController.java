package com.example.jobportal.controller;

import com.example.jobportal.model.Usuario;
import com.example.jobportal.service.SessaoService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/sobre")
@PermitAll
public class SobreController {

    @Inject
    SessaoService sessaoService;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance sobre(Usuario usuario);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response pagina() {
        Usuario u = sessaoService.usuarioLogado().orElse(null);
        return Response.ok(Templates.sobre(u)).type(MediaType.TEXT_HTML).build();
    }
}
