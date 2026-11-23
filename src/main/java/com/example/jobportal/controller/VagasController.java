package com.example.jobportal.controller;

import com.example.jobportal.bo.CandidaturaBO;
import com.example.jobportal.bo.VagaBO;
import com.example.jobportal.model.Usuario;
import com.example.jobportal.model.Vaga;
import com.example.jobportal.service.SessaoService;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;

@Path("/")
public class VagasController {

    @Inject
    VagaBO vagaBO;
    @Inject
    CandidaturaBO candidaturaBO;
    @Inject
    SessaoService sessaoService;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance vagas(List<Vaga> vagas, Usuario usuario, String q);
        public static native TemplateInstance detalhe(Vaga vaga, Usuario usuario, String erro, String msg);
    }

    @GET
    @Path("/vagas")
    @Produces(MediaType.TEXT_HTML)
    public Response listar(@QueryParam("q") String q, @CookieParam("usuarioId") String uid) {
        Usuario u = sessaoService.usuarioLogado(uid).orElse(null);
        List<Vaga> lista = vagaBO.buscar(q);
        return Response.ok(Templates.vagas(lista, u, q)).type(MediaType.TEXT_HTML).build();
    }

    @GET
    @Path("/vagas/{id}")
    @Produces(MediaType.TEXT_HTML)
    public Response detalhe(@PathParam("id") Long id,
                            @QueryParam("erro") String erro,
                            @QueryParam("msg") String msg,
                            @CookieParam("usuarioId") String uid) {
        Usuario u = sessaoService.usuarioLogado(uid).orElse(null);
        try {
            Vaga v = vagaBO.buscar(id);
            return Response.ok(Templates.detalhe(v, u, erro, msg)).type(MediaType.TEXT_HTML).build();
        } catch (IllegalArgumentException e) {
            return Response.seeOther(URI.create("/vagas")).build();
        }
    }

    @POST
    @Path("/vagas/{id}/candidatar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response candidatar(@PathParam("id") Long id,
                               @FormParam("mensagem") String mensagem,
                               @CookieParam("usuarioId") String uid) {
        Usuario u = sessaoService.usuarioLogado(uid).orElse(null);
        if (u == null) {
            return Response.seeOther(URI.create("/login?erro=" + enc("Faça login para se candidatar"))).build();
        }
        try {
            candidaturaBO.candidatar(id, mensagem, u);
            return Response.seeOther(URI.create("/vagas/" + id + "?msg=" + enc("Candidatura enviada!"))).build();
        } catch (IllegalArgumentException e) {
            return Response.seeOther(URI.create("/vagas/" + id + "?erro=" + enc(e.getMessage()))).build();
        }
    }

    private static String enc(String v) {
        return v == null ? "" : java.net.URLEncoder.encode(v, java.nio.charset.StandardCharsets.UTF_8);
    }
}
