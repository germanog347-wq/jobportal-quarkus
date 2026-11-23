package com.example.jobportal.bo.bo;

import com.example.jobportal.dao.AuditoriaDAO;
import com.example.jobportal.model.Auditoria;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class AuditoriaBO {

    @Inject
    AuditoriaDAO auditoriaDAO;

    public void registrar(String acao, String detalhe, String usuarioEmail) {
        auditoriaDAO.salvar(new Auditoria(acao, detalhe, usuarioEmail != null ? usuarioEmail : ""));
    }

    public List<Auditoria> listarTodas() {
        return auditoriaDAO.listarTodas();
    }
}
