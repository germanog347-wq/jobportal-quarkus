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
import java.net.URI;
import java.util.List;

/** Gestão de candidaturas (somente admin). */
@Path("/candidaturas")
@RolesAllowed({ "ADMIN" })
public class AdminCandidaturaController {

    @Inject
    CandidaturaBO candidaturaBO;
    @Inject
    SessaoService sessaoService;

    @CheckedTemplate(basePath = "")
    public static class Templates {
        public static native TemplateInstance candidaturaGestao(List<Candidatura> candidaturas, Usuario usuario, String msg, String erro);
    }

    @GET
    @Path("/gestao")
    @Produces(MediaType.TEXT_HTML)
    public Response candidaturas(@QueryParam("msg") String msg, @QueryParam("erro") String erro) {
        Usuario u = sessaoService.usuarioObrigatorio();
        return Response.ok(Templates.candidaturaGestao(candidaturaBO.listarTodas(), u, msg, erro))
                .type(MediaType.TEXT_HTML).build();
    }

    @POST
    @Path("/status/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response status(@PathParam("id") Integer id, @FormParam("status") String status) {
        Usuario u = sessaoService.usuarioObrigatorio();
        try {
            candidaturaBO.atualizarStatus(id, status, u);
            return Response.seeOther(URI.create("/candidaturas/gestao?msg=" + enc("Status atualizado"))).build();
        } catch (Exception e) {
            return Response.seeOther(URI.create("/candidaturas/gestao?erro=" + enc(e.getMessage()))).build();
        }
    }

    private static String enc(String v) {
        return v == null ? "" : java.net.URLEncoder.encode(v, java.nio.charset.StandardCharsets.UTF_8);
    }
}
