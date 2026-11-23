package com.example.jobportal.bo.bo;

import com.example.jobportal.dao.UsuarioDAO;
import com.example.jobportal.dto.CadastroDTO;
import com.example.jobportal.dto.LoginDTO;
import com.example.jobportal.dto.PerfilUpdateDTO;
import com.example.jobportal.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UsuarioBO {

    @Inject
    UsuarioDAO usuarioDAO;
    @Inject
    AuditoriaBO auditoriaBO;

    public static class Resultado {
        public boolean sucesso;
        public String mensagem;
        public Usuario usuario;

        public static Resultado ok(Usuario u) {
            Resultado r = new Resultado();
            r.sucesso = true;
            r.usuario = u;
            return r;
        }

        public static Resultado erro(String msg) {
            Resultado r = new Resultado();
            r.sucesso = false;
            r.mensagem = msg;
            return r;
        }
    }

    public Resultado autenticar(LoginDTO dto) {
        if (dto.email == null || dto.email.isBlank() || dto.senha == null || dto.senha.isBlank()) {
            return Resultado.erro("E-mail e senha são obrigatórios");
        }
        Optional<Usuario> opt = usuarioDAO.buscarPorEmailESenha(
                dto.email.trim().toLowerCase(), dto.senha);
        if (opt.isEmpty()) {
            auditoriaBO.registrar("LOGIN_FALHA", "Tentativa inválida", dto.email);
            return Resultado.erro("Credenciais inválidas");
        }
        Usuario u = opt.get();
        auditoriaBO.registrar("LOGIN", "Login realizado", u.email);
        return Resultado.ok(u);
    }

    public Resultado cadastrar(CadastroDTO dto) {
        if (dto.nome == null || dto.nome.isBlank()) {
            return Resultado.erro("Nome é obrigatório");
        }
        if (dto.email == null || dto.email.isBlank()) {
            return Resultado.erro("E-mail é obrigatório");
        }
        if (dto.senha == null || dto.senha.length() < 4) {
            return Resultado.erro("Senha mínimo 4 caracteres");
        }
        if (dto.cpf == null || dto.cpf.isBlank()) {
            return Resultado.erro("CPF é obrigatório");
        }
        if (dto.telefone == null || dto.telefone.isBlank()) {
            return Resultado.erro("Telefone é obrigatório");
        }
        if (dto.cidade == null || dto.cidade.isBlank()) {
            return Resultado.erro("Cidade é obrigatória");
        }

        String email = dto.email.trim().toLowerCase();
        if (usuarioDAO.contarPorEmail(email) > 0) {
            return Resultado.erro("E-mail já cadastrado");
        }

        Usuario u = new Usuario(dto.nome.trim(), email, dto.senha);
        u.cpf = limparCpf(dto.cpf);
        u.telefone = dto.telefone.trim();
        u.cidade = dto.cidade.trim();
        u.dataNascimento = parseData(dto.dataNascimento);
        u.isAdmin = false;
        u.ativo = true;
        usuarioDAO.salvar(u);
        auditoriaBO.registrar("CADASTRO", "Novo usuário", u.email);
        return Resultado.ok(u);
    }

    public void atualizarPerfil(Long id, PerfilUpdateDTO dto) {
        Usuario u = usuarioDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (dto.nome != null && !dto.nome.isBlank()) {
            u.nome = dto.nome.trim();
        }
        if (dto.email != null && !dto.email.isBlank()) {
            String novo = dto.email.trim().toLowerCase();
            if (!novo.equals(u.email) && usuarioDAO.contarPorEmail(novo) > 0) {
                throw new IllegalArgumentException("E-mail já em uso");
            }
            u.email = novo;
        }
        if (dto.senha != null && !dto.senha.isBlank()) {
            if (dto.senha.length() < 4) {
                throw new IllegalArgumentException("Senha mínimo 4 caracteres");
            }
            u.senha = dto.senha;
        }
        if (dto.cpf != null && !dto.cpf.isBlank()) {
            u.cpf = limparCpf(dto.cpf);
        }
        if (dto.telefone != null && !dto.telefone.isBlank()) {
            u.telefone = dto.telefone.trim();
        }
        if (dto.cidade != null && !dto.cidade.isBlank()) {
            u.cidade = dto.cidade.trim();
        }
        if (dto.dataNascimento != null && !dto.dataNascimento.isBlank()) {
            u.dataNascimento = parseData(dto.dataNascimento);
        }

        usuarioDAO.salvar(u);
        auditoriaBO.registrar("ATUALIZAR_PERFIL", "Atualizou perfil", u.email);
    }

    public List<Usuario> listarTodos() {
        return usuarioDAO.listarTodos();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioDAO.buscarPorId(id);
    }

    public void promover(Long id, String adminEmail) {
        Usuario u = usuarioDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Não encontrado"));
        u.isAdmin = true;
        usuarioDAO.salvar(u);
        auditoriaBO.registrar("PROMOVER", "Promoveu " + u.email, adminEmail);
    }

    public void rebaixar(Long id, String adminEmail) {
        Usuario u = usuarioDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Não encontrado"));
        u.isAdmin = false;
        usuarioDAO.salvar(u);
        auditoriaBO.registrar("REBAIXAR", "Rebaixou " + u.email, adminEmail);
    }

    private static String limparCpf(String cpf) {
        return cpf == null ? null : cpf.replaceAll("[^0-9]", "");
    }

    private static LocalDate parseData(String s) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Data de nascimento inválida");
        }
    }
}
