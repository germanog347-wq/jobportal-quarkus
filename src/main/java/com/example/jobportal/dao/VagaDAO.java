package com.example.jobportal.dao;

import com.example.jobportal.model.Vaga;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class VagaDAO {

    @Inject
    EntityManager em;

    public Optional<Vaga> buscarPorId(Long id) {
        return Optional.ofNullable(em.find(Vaga.class, id));
    }

    public List<Vaga> listarAtivas() {
        return em.createQuery(
                "SELECT v FROM Vaga v WHERE v.ativa = true ORDER BY v.criadoEm DESC", Vaga.class)
                .getResultList();
    }

    public List<Vaga> listarTodas() {
        return em.createQuery("SELECT v FROM Vaga v ORDER BY v.criadoEm DESC", Vaga.class)
                .getResultList();
    }

    /** Busca por título, empresa, localização ou descrição (apenas ativas). */
    public List<Vaga> buscar(String termo) {
        if (termo == null || termo.isBlank()) {
            return listarAtivas();
        }
        String t = "%" + termo.trim().toLowerCase() + "%";
        return em.createQuery(
                "SELECT v FROM Vaga v WHERE v.ativa = true AND (" +
                "LOWER(v.titulo) LIKE :t OR LOWER(v.empresa) LIKE :t OR " +
                "LOWER(v.localizacao) LIKE :t OR LOWER(v.descricao) LIKE :t) " +
                "ORDER BY v.criadoEm DESC", Vaga.class)
                .setParameter("t", t)
                .getResultList();
    }

    public long contar() {
        return em.createQuery("SELECT COUNT(v) FROM Vaga v", Long.class).getSingleResult();
    }

    @Transactional
    public Vaga salvar(Vaga v) {
        if (v.id == null) {
            em.persist(v);
            return v;
        }
        return em.merge(v);
    }

    @Transactional
    public void remover(Long id) {
        Vaga v = em.find(Vaga.class, id);
        if (v != null) em.remove(v);
    }
}
