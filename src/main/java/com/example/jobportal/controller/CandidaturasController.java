package com.example.jobportal.controller;

import com.example.jobportal.bo.CandidaturaBO;
import com.example.jobportal.model.Candidatura;
import com.example.jobportal.model.Usuario;
import com.example.jobportal.service.SessaoService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/candidaturas")
public class CandidaturasController {

    @Inject
    CandidaturaBO candidaturaBO;
    @Inject
    SessaoService sessaoService;

    @CheckedTemplate(basePath = "")
    public static class Templates {
        public static native TemplateInstance candidaturaMinhas(List<Candidatura> candidaturas, Usuario usuario);
    }

    @GET
    @Path("/minhas")
    @Produces(MediaType.TEXT_HTML)
    @RolesAllowed({ "USUARIO", "ADMIN" })
    public Response pagina() {
        Usuario u = sessaoService.usuarioObrigatorio();
        return Response.ok(Templates.candidaturaMinhas(candidaturaBO.listarMinhas(u), u))
                .type(MediaType.TEXT_HTML).build();
    }
}
