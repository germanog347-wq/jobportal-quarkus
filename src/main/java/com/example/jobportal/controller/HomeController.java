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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;

@Path("/")
@PermitAll
public class HomeController {

    @Inject
    SessaoService sessaoService;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance homepage(Usuario usuario, String erro);
    }

    @GET
    public Response raiz() {
        return Response.seeOther(URI.create("/homepage")).build();
    }

    @GET
    @Path("/homepage")
    @Produces(MediaType.TEXT_HTML)
    public Response home(@QueryParam("erro") String erro) {
        Usuario usuario = sessaoService.usuarioLogado().orElse(null);
        return Response.ok(Templates.homepage(usuario, erro)).type(MediaType.TEXT_HTML).build();
    }
}
