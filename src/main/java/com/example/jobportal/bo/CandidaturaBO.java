package com.example.jobportal.bo;

import com.example.jobportal.dao.CandidaturaDAO;
import com.example.jobportal.dao.VagaDAO;
import com.example.jobportal.model.Candidatura;
import com.example.jobportal.model.Usuario;
import com.example.jobportal.model.Vaga;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class CandidaturaBO {

    @Inject
    CandidaturaDAO candidaturaDAO;
    @Inject
    VagaDAO vagaDAO;
    @Inject
    AuditoriaBO auditoriaBO;

    public void candidatar(Long vagaId, String mensagem, Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("Faça login");
        Vaga vaga = vagaDAO.buscarPorId(vagaId)
                .orElseThrow(() -> new IllegalArgumentException("Vaga não encontrada"));
        if (!vaga.ativa) throw new IllegalArgumentException("Vaga não está ativa");
        if (candidaturaDAO.existe(usuario.id, vagaId))
            throw new IllegalArgumentException("Você já se candidatou a esta vaga");
        Candidatura c = new Candidatura();
        c.usuario = usuario;
        c.vaga = vaga;
        c.mensagem = mensagem;
        c.status = "ENVIADA";
        candidaturaDAO.salvar(c);
        auditoriaBO.registrar("CANDIDATURA", "Candidatou-se à vaga " + vaga.titulo, usuario.email);
    }

    public List<Candidatura> listarMinhas(Usuario usuario) {
        return candidaturaDAO.listarPorUsuario(usuario.id);
    }

    public List<Candidatura> listarTodas() {
        return candidaturaDAO.listarTodas();
    }

    public void atualizarStatus(Long id, String status, Usuario admin) {
        if (admin == null || !admin.isAdmin) throw new IllegalArgumentException("Acesso restrito");
        Candidatura c = candidaturaDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Candidatura não encontrada"));
        c.status = status;
        candidaturaDAO.salvar(c);
        auditoriaBO.registrar("STATUS_CANDIDATURA", "Status " + status + " id=" + id, admin.email);
    }
}
