package com.example.jobportal.dao;

import com.example.jobportal.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UsuarioDAO {

    @Inject
    EntityManager em;

    public Optional<Usuario> buscarPorId(Integer id) {
        return Optional.ofNullable(em.find(Usuario.class, id));
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        List<Usuario> list = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                .setParameter("email", email).getResultList();
        return list.stream().findFirst();
    }

    public Optional<Usuario> buscarPorEmailESenha(String email, String senha) {
        List<Usuario> list = em.createQuery(
                "SELECT u FROM Usuario u WHERE u.email = :email AND u.senha = :senha AND u.ativo = true",
                Usuario.class)
                .setParameter("email", email)
                .setParameter("senha", senha)
                .getResultList();
        return list.stream().findFirst();
    }

    public List<Usuario> listarTodos() {
        return em.createQuery("SELECT u FROM Usuario u ORDER BY u.nome", Usuario.class)
                .getResultList();
    }

    public long contar() {
        return em.createQuery("SELECT COUNT(u) FROM Usuario u", Long.class).getSingleResult();
    }

    public long contarPorEmail(String email) {
        return em.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.email = :email", Long.class)
                .setParameter("email", email).getSingleResult();
    }

    @Transactional
    public Usuario salvar(Usuario u) {
        if (u.getId() == null) {
            em.persist(u);
            return u;
        }
        return em.merge(u);
    }
}
