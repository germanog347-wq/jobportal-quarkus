package com.example.jobportal.controller;

import com.example.jobportal.bo.VagaBO;
import com.example.jobportal.dto.VagaDTO;
import com.example.jobportal.model.Usuario;
import com.example.jobportal.model.Vaga;
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

/** Gestão de vagas (somente admin). */
@Path("/vagas")
@RolesAllowed({ "ADMIN" })
public class AdminVagaController {

    @Inject
    VagaBO vagaBO;
    @Inject
    SessaoService sessaoService;

    @CheckedTemplate(basePath = "")
    public static class Templates {
        public static native TemplateInstance vagaGestao(List<Vaga> vagas, Usuario usuario, String msg, String erro);
        public static native TemplateInstance vagaCadastro(Vaga vaga, String acao, Usuario usuario, String erro);
    }

    @GET
    @Path("/gestao")
    @Produces(MediaType.TEXT_HTML)
    public Response painel(@QueryParam("msg") String msg, @QueryParam("erro") String erro) {
        Usuario u = sessaoService.usuarioObrigatorio();
        return Response.ok(Templates.vagaGestao(vagaBO.listarTodas(), u, msg, erro)).type(MediaType.TEXT_HTML).build();
    }

    @GET
    @Path("/cadastro")
    @Produces(MediaType.TEXT_HTML)
    public Response nova() {
        Usuario u = sessaoService.usuarioObrigatorio();
        Vaga v = new Vaga();
        v.setAtiva(true);
        return Response.ok(Templates.vagaCadastro(v, "/vagas/salvar", u, null)).type(MediaType.TEXT_HTML).build();
    }

    @GET
    @Path("/editar/{id}")
    @Produces(MediaType.TEXT_HTML)
    public Response editar(@PathParam("id") Integer id) {
        Usuario u = sessaoService.usuarioObrigatorio();
        try {
            Vaga v = vagaBO.buscar(id);
            return Response.ok(Templates.vagaCadastro(v, "/vagas/atualizar/" + id, u, null))
                    .type(MediaType.TEXT_HTML).build();
        } catch (IllegalArgumentException e) {
            return Response.seeOther(URI.create("/vagas/gestao?erro=" + enc(e.getMessage()))).build();
        }
    }

    @POST
    @Path("/salvar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response salvar(@BeanParam VagaDTO dto) {
        Usuario u = sessaoService.usuarioObrigatorio();
        try {
            vagaBO.criar(dto, u);
            return Response.seeOther(URI.create("/vagas/gestao?msg=" + enc("Vaga criada"))).build();
        } catch (IllegalArgumentException e) {
            Vaga v = new Vaga(dto);
            return Response.ok(Templates.vagaCadastro(v, "/vagas/salvar", u, e.getMessage()))
                    .type(MediaType.TEXT_HTML).build();
        }
    }

    @POST
    @Path("/atualizar/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response atualizar(@PathParam("id") Integer id, @BeanParam VagaDTO dto) {
        Usuario u = sessaoService.usuarioObrigatorio();
        try {
            vagaBO.atualizar(id, dto, u);
            return Response.seeOther(URI.create("/vagas/gestao?msg=" + enc("Vaga atualizada"))).build();
        } catch (IllegalArgumentException e) {
            Vaga v = vagaBO.buscar(id);
            return Response.ok(Templates.vagaCadastro(v, "/vagas/atualizar/" + id, u, e.getMessage()))
                    .type(MediaType.TEXT_HTML).build();
        }
    }

    @POST
    @Path("/excluir/{id}")
    public Response excluir(@PathParam("id") Integer id) {
        Usuario u = sessaoService.usuarioObrigatorio();
        try {
            vagaBO.excluir(id, u);
            return Response.seeOther(URI.create("/vagas/gestao?msg=" + enc("Vaga excluída"))).build();
        } catch (IllegalArgumentException e) {
            return Response.seeOther(URI.create("/vagas/gestao?erro=" + enc(e.getMessage()))).build();
        }
    }

    private static String enc(String v) {
        return v == null ? "" : java.net.URLEncoder.encode(v, java.nio.charset.StandardCharsets.UTF_8);
    }
}
