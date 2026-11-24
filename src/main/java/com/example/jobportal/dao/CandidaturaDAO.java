package com.example.jobportal.dao;

import com.example.jobportal.model.Candidatura;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class CandidaturaDAO {

    @Inject
    EntityManager em;

    /** Retorna a candidatura com o id informado, ou null se não existir. */
    public Candidatura buscarPorId(Integer id) {
        return em.find(Candidatura.class, id);
    }

    public List<Candidatura> listarPorUsuario(Integer usuarioId) {
        return em.createQuery(
                "SELECT c FROM Candidatura c WHERE c.usuario.id = :uid ORDER BY c.criadoEm DESC",
                Candidatura.class)
                .setParameter("uid", usuarioId).getResultList();
    }

    public List<Candidatura> listarTodas() {
        return em.createQuery("SELECT c FROM Candidatura c ORDER BY c.criadoEm DESC", Candidatura.class)
                .getResultList();
    }

    public boolean existe(Integer usuarioId, Integer vagaId) {
        Long count = em.createQuery(
                "SELECT COUNT(c) FROM Candidatura c WHERE c.usuario.id = :uid AND c.vaga.id = :vid",
                Long.class)
                .setParameter("uid", usuarioId)
                .setParameter("vid", vagaId)
                .getSingleResult();
        return count > 0;
    }

    public long contarPorVaga(Integer vagaId) {
        return em.createQuery(
                "SELECT COUNT(c) FROM Candidatura c WHERE c.vaga.id = :vid", Long.class)
                .setParameter("vid", vagaId)
                .getSingleResult();
    }

    @Transactional
    public Candidatura salvar(Candidatura c) {
        if (c.getId() == null) {
            em.persist(c);
            return c;
        }
        return em.merge(c);
    }
}
