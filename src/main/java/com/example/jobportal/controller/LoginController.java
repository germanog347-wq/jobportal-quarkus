package com.example.jobportal.controller;

import com.example.jobportal.bo.UsuarioBO;
import com.example.jobportal.dto.LoginDTO;
import com.example.jobportal.model.TipoUsuario;
import com.example.jobportal.model.Usuario;
import com.example.jobportal.service.SessaoService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/auth")
@PermitAll
public class LoginController {

    @Inject
    UsuarioBO usuarioBO;
    @Inject
    SessaoService sessaoService;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance login(Usuario usuario, String erro, String msg, String email);
    }

    @GET
    @Path("/login")
    @Produces(MediaType.TEXT_HTML)
    public Response login(@QueryParam("erro") String erro, @QueryParam("msg") String msg) {
        Usuario logado = sessaoService.usuarioLogado();
        if (logado != null) {
            return Response.seeOther(URI.create(logado.getTipo() == TipoUsuario.ADMIN ? "/vagas/gestao" : "/homepage")).build();
        }
        return Response.ok(Templates.login(null, erro, msg, null)).type(MediaType.TEXT_HTML).build();
    }

    /**
     * O BO valida as credenciais e devolve o JWT no header; o ResponseFilter grava o cookie.
     * Em caso de erro a tela é exibida de novo mantendo o e-mail digitado.
     */
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response fazerLogin(@BeanParam LoginDTO dto) {
        try {
            return usuarioBO.autenticar(dto);
        } catch (IllegalArgumentException e) {
            return Response.ok(Templates.login(null, e.getMessage(), null, dto.email))
                    .type(MediaType.TEXT_HTML).build();
        }
    }

    @GET
    @Path("/logout")
    public Response logout() {
        return Response.seeOther(URI.create("/homepage"))
                .cookie(SessaoService.cookieLogout())
                .build();
    }
}
