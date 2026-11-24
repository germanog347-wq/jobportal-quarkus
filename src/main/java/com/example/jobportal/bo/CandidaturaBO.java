package com.example.jobportal.bo;

import com.example.jobportal.dao.CandidaturaDAO;
import com.example.jobportal.dao.VagaDAO;
import com.example.jobportal.model.Candidatura;
import com.example.jobportal.model.StatusCandidatura;
import com.example.jobportal.model.TipoUsuario;
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

    public void candidatar(Integer vagaId, String mensagem, Usuario usuario) {
        if (usuario == null) throw new IllegalArgumentException("Faça login");
        Vaga vaga = vagaDAO.buscarPorId(vagaId)
                .orElseThrow(() -> new IllegalArgumentException("Vaga não encontrada"));
        if (!vaga.isAtiva()) throw new IllegalArgumentException("Vaga não está ativa");
        if (candidaturaDAO.existe(usuario.getId(), vagaId))
            throw new IllegalArgumentException("Você já se candidatou a esta vaga");
        Candidatura c = new Candidatura();
        c.setUsuario(usuario);
        c.setVaga(vaga);
        c.setMensagem(mensagem);
        c.setStatus(StatusCandidatura.ENVIADA);
        candidaturaDAO.salvar(c);
        auditoriaBO.registrar("CANDIDATURA", "Candidatou-se à vaga " + vaga.getTitulo(), usuario.getEmail());
    }

    public List<Candidatura> listarMinhas(Usuario usuario) {
        return candidaturaDAO.listarPorUsuario(usuario.getId());
    }

    public List<Candidatura> listarTodas() {
        return candidaturaDAO.listarTodas();
    }

    public void atualizarStatus(Integer id, String status, Usuario admin) {
        if (admin == null || admin.getTipo() != TipoUsuario.ADMIN) throw new IllegalArgumentException("Acesso restrito");
        StatusCandidatura novoStatus = converterStatus(status);
        Candidatura c = candidaturaDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Candidatura não encontrada"));
        c.setStatus(novoStatus);
        candidaturaDAO.salvar(c);
        auditoriaBO.registrar("STATUS_CANDIDATURA", "Status " + novoStatus + " id=" + id, admin.getEmail());
    }

    private static StatusCandidatura converterStatus(String status) {
        if (status == null || status.isBlank()) throw new IllegalArgumentException("Status inválido");
        try {
            return StatusCandidatura.valueOf(status.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido");
        }
    }
}
