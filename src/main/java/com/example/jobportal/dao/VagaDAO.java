package com.example.jobportal.dao;

import com.example.jobportal.model.Vaga;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class VagaDAO {

    @Inject
    EntityManager em;

    /** Retorna a vaga com o id informado, ou null se não existir. */
    public Vaga buscarPorId(Integer id) {
        return em.find(Vaga.class, id);
    }

    // Vaga aberta = ativa e com prazo não vencido (o dia da expiração ainda conta)
    private static final String VAGA_ABERTA =
            "v.ativa = true AND (v.dataExpiracao IS NULL OR v.dataExpiracao >= CURRENT_DATE)";

    public List<Vaga> listarAtivas() {
        return em.createQuery(
                "SELECT v FROM Vaga v WHERE " + VAGA_ABERTA + " ORDER BY v.criadoEm DESC", Vaga.class)
                .getResultList();
    }

    public List<Vaga> listarTodas() {
        return em.createQuery("SELECT v FROM Vaga v ORDER BY v.criadoEm DESC", Vaga.class)
                .getResultList();
    }

    /** Busca por título, empresa, localização ou descrição (apenas vagas abertas). */
    public List<Vaga> buscar(String termo) {
        if (termo == null || termo.isBlank()) {
            return listarAtivas();
        }
        String t = "%" + termo.trim().toLowerCase() + "%";
        return em.createQuery(
                "SELECT v FROM Vaga v WHERE " + VAGA_ABERTA + " AND (" +
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
        if (v.getId() == null) {
            em.persist(v);
            return v;
        }
        return em.merge(v);
    }

    @Transactional
    public void remover(Integer id) {
        Vaga v = em.find(Vaga.class, id);
        if (v != null) em.remove(v);
    }
}
