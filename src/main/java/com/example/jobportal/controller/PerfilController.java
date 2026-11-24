package com.example.jobportal.controller;

import com.example.jobportal.bo.UsuarioBO;
import com.example.jobportal.dto.PerfilUpdateDTO;
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

@Path("/usuario")
@RolesAllowed({ "USUARIO", "ADMIN" })
public class PerfilController {

    @Inject
    UsuarioBO usuarioBO;
    @Inject
    SessaoService sessaoService;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance usuarioPerfil(Usuario usuario, String erro, String msg);
    }

    @GET
    @Path("/perfil")
    @Produces(MediaType.TEXT_HTML)
    public Response pagina(@QueryParam("erro") String erro, @QueryParam("msg") String msg) {
        Usuario u = sessaoService.usuarioObrigatorio();
        return Response.ok(Templates.usuarioPerfil(u, erro, msg)).type(MediaType.TEXT_HTML).build();
    }

    @POST
    @Path("/atualizar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response atualizar(@BeanParam PerfilUpdateDTO dto) {
        Usuario u = sessaoService.usuarioObrigatorio();
        try {
            usuarioBO.atualizarPerfil(u.getId(), dto);
            return Response.seeOther(URI.create("/usuario/perfil?msg=" + enc("Perfil atualizado com sucesso"))).build();
        } catch (IllegalArgumentException e) {
            return Response.seeOther(URI.create("/usuario/perfil?erro=" + enc(e.getMessage()))).build();
        }
    }

    private static String enc(String v) {
        return v == null ? "" : java.net.URLEncoder.encode(v, java.nio.charset.StandardCharsets.UTF_8);
    }
}
