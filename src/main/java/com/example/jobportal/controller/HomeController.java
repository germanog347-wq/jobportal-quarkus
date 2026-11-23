package com.example.jobportal.controller;

import com.example.jobportal.model.Usuario;
import com.example.jobportal.service.SessaoService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class HomeController {

    @Inject
    SessaoService sessaoService;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance home(Usuario usuario);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response home(@CookieParam("usuarioId") String usuarioIdStr) {
        Usuario usuario = sessaoService.usuarioLogado(usuarioIdStr).orElse(null);
        return Response.ok(Templates.home(usuario)).type(MediaType.TEXT_HTML).build();
    }
}
