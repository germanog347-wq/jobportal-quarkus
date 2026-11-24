package com.example.jobportal.controller;

import com.example.jobportal.bo.UsuarioBO;
import com.example.jobportal.model.Usuario;
import com.example.jobportal.service.SessaoService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

/** Gestão de usuários (somente admin). */
@Path("/usuario")
@RolesAllowed({ "ADMIN" })
public class AdminUsuarioController {

    @Inject
    UsuarioBO usuarioBO;
    @Inject
    SessaoService sessaoService;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance usuarioVisaoGeral(List<Usuario> usuarios, Usuario usuario, String msg, String erro);
    }

    @GET
    @Path("/visao-geral")
    @Produces(MediaType.TEXT_HTML)
    public Response usuarios(@QueryParam("msg") String msg, @QueryParam("erro") String erro) {
        Usuario u = sessaoService.usuarioObrigatorio();
        return Response.ok(Templates.usuarioVisaoGeral(usuarioBO.listarTodos(), u, msg, erro))
                .type(MediaType.TEXT_HTML).build();
    }

    @POST
    @Path("/promover/{id}")
    public Response promover(@PathParam("id") Integer id) {
        Usuario u = sessaoService.usuarioObrigatorio();
        try {
            usuarioBO.promover(id, u.getEmail());
            return Response.seeOther(URI.create("/usuario/visao-geral?msg=" + enc("Promovido"))).build();
        } catch (Exception e) {
            return Response.seeOther(URI.create("/usuario/visao-geral?erro=" + enc(e.getMessage()))).build();
        }
    }

    @POST
    @Path("/rebaixar/{id}")
    public Response rebaixar(@PathParam("id") Integer id) {
        Usuario u = sessaoService.usuarioObrigatorio();
        try {
            usuarioBO.rebaixar(id, u.getEmail());
            return Response.seeOther(URI.create("/usuario/visao-geral?msg=" + enc("Rebaixado"))).build();
        } catch (Exception e) {
            return Response.seeOther(URI.create("/usuario/visao-geral?erro=" + enc(e.getMessage()))).build();
        }
    }

    private static String enc(String v) {
        return v == null ? "" : java.net.URLEncoder.encode(v, java.nio.charset.StandardCharsets.UTF_8);
    }
}
