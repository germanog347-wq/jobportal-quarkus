package com.example.jobportal.dao;

import com.example.jobportal.model.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class AuditoriaDAO {

    @Inject
    EntityManager em;

    public List<Log> listarTodas() {
        return em.createQuery(
                "SELECT l FROM Log l LEFT JOIN FETCH l.usuario ORDER BY l.dataHora DESC", Log.class)
                .getResultList();
    }

    @Transactional
    public void salvar(Log log) {
        em.persist(log);
    }
}
