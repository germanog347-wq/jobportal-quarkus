package com.example.jobportal.controller;

import com.example.jobportal.bo.UsuarioBO;
import com.example.jobportal.dto.LoginDTO;
import com.example.jobportal.model.Usuario;
import com.example.jobportal.service.SessaoService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;

@Path("/")
public class LoginController {

    @Inject
    UsuarioBO usuarioBO;
    @Inject
    SessaoService sessaoService;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance login(String erro, String sucesso);
    }

    @GET
    @Path("/login")
    @Produces(MediaType.TEXT_HTML)
    public Response login(@QueryParam("erro") String erro,
                          @QueryParam("sucesso") String sucesso,
                          @CookieParam("usuarioId") String usuarioIdStr) {
        Optional<Usuario> logado = sessaoService.usuarioLogado(usuarioIdStr);
        if (logado.isPresent()) {
            return Response.seeOther(URI.create(logado.get().isAdmin ? "/admin" : "/")).build();
        }
        return Response.ok(Templates.login(erro, sucesso)).type(MediaType.TEXT_HTML).build();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response fazerLogin(@BeanParam LoginDTO dto) {
        UsuarioBO.Resultado r = usuarioBO.autenticar(dto);
        if (!r.sucesso) {
            return Response.seeOther(URI.create("/login?erro=" + enc(r.mensagem))).build();
        }
        return Response.seeOther(URI.create("/"))
                .cookie(sessaoService.cookieLogin(r.usuario.id))
                .build();
    }

    @GET
    @Path("/logout")
    public Response logout() {
        return Response.seeOther(URI.create("/"))
                .cookie(sessaoService.cookieLogout())
                .build();
    }

    private static String enc(String v) {
        return v == null ? "" : java.net.URLEncoder.encode(v, java.nio.charset.StandardCharsets.UTF_8);
    }
}
