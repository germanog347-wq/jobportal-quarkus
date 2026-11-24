package com.example.jobportal.controller;

import com.example.jobportal.bo.AuditoriaBO;
import com.example.jobportal.model.Log;
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

@Path("/logs")
@RolesAllowed({ "ADMIN" })
public class AuditoriaController {

    @Inject
    AuditoriaBO auditoriaBO;
    @Inject
    SessaoService sessaoService;

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance logVisao(List<Log> logs, Usuario usuario);
    }

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response pagina() {
        Usuario u = sessaoService.usuarioObrigatorio();
        return Response.ok(Templates.logVisao(auditoriaBO.listarTodas(), u))
                .type(MediaType.TEXT_HTML).build();
    }
}
