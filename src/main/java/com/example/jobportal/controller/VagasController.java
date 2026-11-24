package com.example.jobportal.controller;

import com.example.jobportal.bo.CandidaturaBO;
import com.example.jobportal.bo.VagaBO;
import com.example.jobportal.model.Usuario;
import com.example.jobportal.model.Vaga;
import com.example.jobportal.service.SessaoService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/vagas")
public class VagasController {

    @Inject
    VagaBO vagaBO;
    @Inject
    CandidaturaBO candidaturaBO;
    @Inject
    SessaoService sessaoService;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance vagaVisaoGeral(List<Vaga> vagas, Usuario usuario, String q);
        public static native TemplateInstance vagaDetalhe(Vaga vaga, Usuario usuario, String erro, String msg);
    }

    @GET
    @Path("/visao-geral")
    @Produces(MediaType.TEXT_HTML)
    @PermitAll
    public Response listar(@QueryParam("q") String q) {
        Usuario u = sessaoService.usuarioLogado().orElse(null);
        List<Vaga> lista = vagaBO.buscar(q);
        return Response.ok(Templates.vagaVisaoGeral(lista, u, q)).type(MediaType.TEXT_HTML).build();
    }

    @GET
    @Path("/detalhe/{id}")
    @Produces(MediaType.TEXT_HTML)
    @PermitAll
    public Response detalhe(@PathParam("id") Integer id,
                            @QueryParam("erro") String erro,
                            @QueryParam("msg") String msg) {
        Usuario u = sessaoService.usuarioLogado().orElse(null);
        try {
            Vaga v = vagaBO.buscar(id);
            return Response.ok(Templates.vagaDetalhe(v, u, erro, msg)).type(MediaType.TEXT_HTML).build();
        } catch (IllegalArgumentException e) {
            return Response.seeOther(URI.create("/vagas/visao-geral")).build();
        }
    }

    @POST
    @Path("/candidatar/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @RolesAllowed({ "USUARIO", "ADMIN" })
    public Response candidatar(@PathParam("id") Integer id, @FormParam("mensagem") String mensagem) {
        Usuario u = sessaoService.usuarioObrigatorio();
        try {
            candidaturaBO.candidatar(id, mensagem, u);
            return Response.seeOther(URI.create("/vagas/detalhe/" + id + "?msg=" + enc("Candidatura enviada!"))).build();
        } catch (IllegalArgumentException e) {
            return Response.seeOther(URI.create("/vagas/detalhe/" + id + "?erro=" + enc(e.getMessage()))).build();
        }
    }

    private static String enc(String v) {
        return v == null ? "" : java.net.URLEncoder.encode(v, java.nio.charset.StandardCharsets.UTF_8);
    }
}
