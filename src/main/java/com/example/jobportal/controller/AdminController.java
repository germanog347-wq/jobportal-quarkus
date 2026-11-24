package com.example.jobportal.controller;

import com.example.jobportal.bo.CandidaturaBO;
import com.example.jobportal.bo.UsuarioBO;
import com.example.jobportal.bo.VagaBO;
import com.example.jobportal.dto.VagaDTO;
import com.example.jobportal.model.Candidatura;
import com.example.jobportal.model.TipoUsuario;
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

@Path("/admin")
public class AdminController {

    @Inject
    VagaBO vagaBO;
    @Inject
    UsuarioBO usuarioBO;
    @Inject
    CandidaturaBO candidaturaBO;
    @Inject
    SessaoService sessaoService;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance painel(List<Vaga> vagas, Usuario usuario, String msg, String erro);
        public static native TemplateInstance formVaga(Vaga vaga, String acao, Usuario usuario, String erros);
        public static native TemplateInstance usuarios(List<Usuario> usuarios, Usuario usuario, String msg, String erro);
        public static native TemplateInstance candidaturas(List<Candidatura> candidaturas, Usuario usuario, String msg, String erro);
    }

    private Response exigirAdmin(String uid) {
        Usuario u = sessaoService.usuarioLogado(uid).orElse(null);
        if (u == null) return Response.seeOther(URI.create("/login?erro=" + enc("Faça login"))).build();
        if (u.getTipo() != TipoUsuario.ADMIN) return Response.seeOther(URI.create("/?erro=" + enc("Acesso negado"))).build();
        return null;
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response painel(@QueryParam("msg") String msg, @QueryParam("erro") String erro,
                           @CookieParam("usuarioId") String uid) {
        Response r = exigirAdmin(uid);
        if (r != null) return r;
        Usuario u = sessaoService.usuarioLogado(uid).get();
        return Response.ok(Templates.painel(vagaBO.listarTodas(), u, msg, erro)).type(MediaType.TEXT_HTML).build();
    }

    @GET
    @Path("/vagas/nova")
    @Produces(MediaType.TEXT_HTML)
    public Response nova(@CookieParam("usuarioId") String uid) {
        Response r = exigirAdmin(uid);
        if (r != null) return r;
        Usuario u = sessaoService.usuarioLogado(uid).get();
        Vaga v = new Vaga();
        v.setAtiva(true);
        return Response.ok(Templates.formVaga(v, "/admin/vagas/salvar", u, null)).type(MediaType.TEXT_HTML).build();
    }

    @GET
    @Path("/vagas/editar/{id}")
    @Produces(MediaType.TEXT_HTML)
    public Response editar(@PathParam("id") Integer id, @CookieParam("usuarioId") String uid) {
        Response r = exigirAdmin(uid);
        if (r != null) return r;
        Usuario u = sessaoService.usuarioLogado(uid).get();
        try {
            Vaga v = vagaBO.buscar(id);
            return Response.ok(Templates.formVaga(v, "/admin/vagas/atualizar/" + id, u, null))
                    .type(MediaType.TEXT_HTML).build();
        } catch (IllegalArgumentException e) {
            return Response.seeOther(URI.create("/admin?erro=" + enc(e.getMessage()))).build();
        }
    }

    @POST
    @Path("/vagas/salvar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response salvar(@BeanParam VagaDTO dto, @CookieParam("usuarioId") String uid) {
        Response r = exigirAdmin(uid);
        if (r != null) return r;
        Usuario u = sessaoService.usuarioLogado(uid).get();
        try {
            vagaBO.criar(dto, u);
            return Response.seeOther(URI.create("/admin?msg=" + enc("Vaga criada"))).build();
        } catch (IllegalArgumentException e) {
            Vaga v = new Vaga(dto);
            return Response.ok(Templates.formVaga(v, "/admin/vagas/salvar", u, e.getMessage()))
                    .type(MediaType.TEXT_HTML).build();
        }
    }

    @POST
    @Path("/vagas/atualizar/{id}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response atualizar(@PathParam("id") Integer id, @BeanParam VagaDTO dto,
                              @CookieParam("usuarioId") String uid) {
        Response r = exigirAdmin(uid);
        if (r != null) return r;
        Usuario u = sessaoService.usuarioLogado(uid).get();
        try {
            vagaBO.atualizar(id, dto, u);
            return Response.seeOther(URI.create("/admin?msg=" + enc("Vaga atualizada"))).build();
        } catch (IllegalArgumentException e) {
            Vaga v = vagaBO.buscar(id);
            return Response.ok(Templates.formVaga(v, "/admin/vagas/atualizar/" + id, u, e.getMessage()))
                    .type(MediaType.TEXT_HTML).build();
        }
    }

    @POST
    @Path("/vagas/excluir/{id}")
    public Response excluir(@PathParam("id") Integer id, @CookieParam("usuarioId") String uid) {
        Response r = exigirAdmin(uid);
        if (r != null) return r;
        Usuario u = sessaoService.usuarioLogado(uid).get();
        try {
            vagaBO.excluir(id, u);
            return Response.seeOther(URI.create("/admin?msg=" + enc("Vaga excluída"))).build();
        } catch (IllegalArgumentException e) {
            return Response.seeOther(URI.create("/admin?erro=" + enc(e.getMessage()))).build();
        }
    }

    @GET
    @Path("/usuarios")
    @Produces(MediaType.TEXT_HTML)
    public Response usuarios(@QueryParam("msg") String msg, @QueryParam("erro") String erro,
                             @CookieParam("usuarioId") String uid) {
        Response r = exigirAdmin(uid);
        if (r != null) return r;
        Usuario u = sessaoService.usuarioLogado(uid).get();
        return Response.ok(Templates.usuarios(usuarioBO.listarTodos(), u, msg, erro))
                .type(MediaType.TEXT_HTML).build();
    }

    @POST
    @Path("/usuarios/{id}/promover")
    public Response promover(@PathParam("id") Integer id, @CookieParam("usuarioId") String uid) {
        Response r = exigirAdmin(uid);
        if (r != null) return r;
        Usuario u = sessaoService.usuarioLogado(uid).get();
        try {
            usuarioBO.promover(id, u.getEmail());
            return Response.seeOther(URI.create("/admin/usuarios?msg=" + enc("Promovido"))).build();
        } catch (Exception e) {
            return Response.seeOther(URI.create("/admin/usuarios?erro=" + enc(e.getMessage()))).build();
        }
    }

    @POST
    @Path("/usuarios/{id}/rebaixar")
    public Response rebaixar(@PathParam("id") Integer id, @CookieParam("usuarioId") String uid) {
        Response r = exigirAdmin(uid);
        if (r != null) return r;
        Usuario u = sessaoService.usuarioLogado(uid).get();
        try {
            usuarioBO.rebaixar(id, u.getEmail());
            return Response.seeOther(URI.create("/admin/usuarios?msg=" + enc("Rebaixado"))).build();
        } catch (Exception e) {
            return Response.seeOther(URI.create("/admin/usuarios?erro=" + enc(e.getMessage()))).build();
        }
    }

    @GET
    @Path("/candidaturas")
    @Produces(MediaType.TEXT_HTML)
    public Response candidaturas(@QueryParam("msg") String msg, @QueryParam("erro") String erro,
                                 @CookieParam("usuarioId") String uid) {
        Response r = exigirAdmin(uid);
        if (r != null) return r;
        Usuario u = sessaoService.usuarioLogado(uid).get();
        return Response.ok(Templates.candidaturas(candidaturaBO.listarTodas(), u, msg, erro))
                .type(MediaType.TEXT_HTML).build();
    }

    @POST
    @Path("/candidaturas/{id}/status")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response status(@PathParam("id") Integer id, @FormParam("status") String status,
                           @CookieParam("usuarioId") String uid) {
        Response r = exigirAdmin(uid);
        if (r != null) return r;
        Usuario u = sessaoService.usuarioLogado(uid).get();
        try {
            candidaturaBO.atualizarStatus(id, status, u);
            return Response.seeOther(URI.create("/admin/candidaturas?msg=" + enc("Status atualizado"))).build();
        } catch (Exception e) {
            return Response.seeOther(URI.create("/admin/candidaturas?erro=" + enc(e.getMessage()))).build();
        }
    }

    private static String enc(String v) {
        return v == null ? "" : java.net.URLEncoder.encode(v, java.nio.charset.StandardCharsets.UTF_8);
    }
}
