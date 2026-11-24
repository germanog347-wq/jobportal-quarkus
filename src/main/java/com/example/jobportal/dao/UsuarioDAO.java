package com.example.jobportal.dao;

import com.example.jobportal.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;

@ApplicationScoped
public class UsuarioDAO {

    @Inject
    EntityManager em;

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACOES = 120_000;
    private static final int TAMANHO_SALT = 16;
    private static final int TAMANHO_HASH = 256;

    /** Retorna o usuário com o id informado, ou null se não existir. */
    public Usuario buscarPorId(Integer id) {
        return em.find(Usuario.class, id);
    }

    /** Retorna o usuário com o e-mail informado, ou null se não existir. */
    public Usuario buscarPorEmail(String email) {
        try {
            return em.createQuery("SELECT u FROM Usuario u WHERE u.email = :email", Usuario.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /** Retorna o usuário ativo se o e-mail existir e a senha conferir com o hash salvo; senão, null. */
    public Usuario autenticar(String email, String senha) {
        if (email == null || senha == null) {
            return null;
        }
        Usuario usuario = buscarPorEmail(email);
        if (usuario == null || !usuario.isAtivo() || !senhaConfere(senha, usuario.getSenha())) {
            return null;
        }
        return usuario;
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

    /**
     * Salva o usuário armazenando a senha criptografada (PBKDF2 + salt), nunca em texto puro.
     * Se a senha já estiver criptografada (ex.: ao promover/rebaixar), ela é mantida.
     */
    @Transactional
    public Usuario salvar(Usuario u) {
        if (!estaCriptografada(u.getSenha())) {
            u.setSenha(criptografar(u.getSenha()));
        }
        if (u.getId() == null) {
            em.persist(u);
            return u;
        }
        return em.merge(u);
    }

    // Formato armazenado: iteracoes:salt(base64):hash(base64)
    private String criptografar(String senha) {
        byte[] salt = new byte[TAMANHO_SALT];
        new SecureRandom().nextBytes(salt);
        byte[] hash = derivar(senha, salt, ITERACOES);
        return ITERACOES + ":" + Base64.getEncoder().encodeToString(salt)
                + ":" + Base64.getEncoder().encodeToString(hash);
    }

    private boolean senhaConfere(String senha, String armazenada) {
        if (armazenada == null) {
            return false;
        }
        String[] partes = armazenada.split(":");
        if (partes.length != 3) {
            return false;
        }
        try {
            byte[] salt = Base64.getDecoder().decode(partes[1]);
            byte[] esperado = Base64.getDecoder().decode(partes[2]);
            byte[] calculado = derivar(senha, salt, Integer.parseInt(partes[0]));
            return MessageDigest.isEqual(esperado, calculado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /** true se o valor já está no formato iteracoes:salt:hash gerado por criptografar(). */
    public boolean estaCriptografada(String valor) {
        if (valor == null) {
            return false;
        }
        String[] partes = valor.split(":");
        if (partes.length != 3) {
            return false;
        }
        try {
            Integer.parseInt(partes[0]);
            return Base64.getDecoder().decode(partes[1]).length == TAMANHO_SALT
                    && Base64.getDecoder().decode(partes[2]).length == TAMANHO_HASH / 8;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private byte[] derivar(String senha, byte[] salt, int iteracoes) {
        try {
            PBEKeySpec spec = new PBEKeySpec(senha.toCharArray(), salt, iteracoes, TAMANHO_HASH);
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao criptografar a senha", e);
        }
    }
}
