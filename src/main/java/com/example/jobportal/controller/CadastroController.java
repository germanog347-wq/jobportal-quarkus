package com.example.jobportal.controller;

import com.example.jobportal.bo.UsuarioBO;
import com.example.jobportal.dto.CadastroDTO;
import com.example.jobportal.filter.ResponseFilter;
import com.example.jobportal.model.Usuario;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Path("/usuario")
@PermitAll
public class CadastroController {

    @Inject
    UsuarioBO usuarioBO;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance usuarioCadastro(Usuario usuario, String erro, CadastroDTO dto);
    }

    @GET
    @Path("/cadastro")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance pagina(@QueryParam("erro") String erro) {
        return Templates.usuarioCadastro(null, erro, new CadastroDTO());
    }

    @POST
    @Path("/salvar")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response cadastrar(@BeanParam CadastroDTO dto) {
        UsuarioBO.Resultado r = usuarioBO.cadastrar(dto);
        if (!r.sucesso) {
            // Mostra o formulário de novo com o que foi digitado (exceto a senha)
            dto.senha = null;
            return Response.ok(Templates.usuarioCadastro(null, r.mensagem, dto))
                    .type(MediaType.TEXT_HTML).build();
        }
        // Já entra logado: o ResponseFilter converte o header em cookie
        return Response.seeOther(URI.create("/homepage"))
                .header(ResponseFilter.HEADER_JWT, usuarioBO.gerarJWT(r.usuario))
                .build();
    }
}
