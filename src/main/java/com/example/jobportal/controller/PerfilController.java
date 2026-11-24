package com.example.jobportal.controller;

import com.example.jobportal.bo.UsuarioBO;
import com.example.jobportal.dto.PerfilUpdateDTO;
import com.example.jobportal.model.Usuario;
import com.example.jobportal.service.SessaoService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/usuario")
public class PerfilController {

    @Inject
    UsuarioBO usuarioBO;
    @Inject
    SessaoService sessaoService;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance perfil(Usuario usuario, String erro, String msg);
    }

    @GET
    @Path("/perfil")
    @Produces(MediaType.TEXT_HTML)
    public Response pagina(@QueryParam("erro") String erro,
                           @QueryParam("msg") String msg,
                           @CookieParam("usuarioId") String uid) {
        Usuario u = sessaoService.usuarioLogado(uid).orElse(null);
        if (u == null) {
            return Response.seeOther(URI.create("/auth/login?erro=" + enc("Faça login"))).build();
        }
        // Recarrega do banco para ter dados atualizados
        u = usuarioBO.buscarPorId(u.getId()).orElse(u);
        return Response.ok(Templates.perfil(u, erro, msg)).type(MediaType.TEXT_HTML).build();
    }

    @POST
    @Path("/atualizar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response atualizar(@BeanParam PerfilUpdateDTO dto,
                              @CookieParam("usuarioId") String uid) {
        Usuario u = sessaoService.usuarioLogado(uid).orElse(null);
        if (u == null) {
            return Response.seeOther(URI.create("/auth/login")).build();
        }
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
