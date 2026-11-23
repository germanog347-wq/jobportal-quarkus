package com.example.jobportal.dao;

import com.example.jobportal.model.Auditoria;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class AuditoriaDAO {

    @Inject
    EntityManager em;

    public List<Auditoria> listarTodas() {
        return em.createQuery("SELECT a FROM Auditoria a ORDER BY a.dataHora DESC", Auditoria.class)
                .getResultList();
    }

    @Transactional
    public void salvar(Auditoria a) {
        em.persist(a);
    }
}
