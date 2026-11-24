package com.example.jobportal.bo;

import com.example.jobportal.dao.CandidaturaDAO;
import com.example.jobportal.dao.VagaDAO;
import com.example.jobportal.dto.VagaDTO;
import com.example.jobportal.model.TipoUsuario;
import com.example.jobportal.model.Usuario;
import com.example.jobportal.model.Vaga;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class VagaBO {

    @Inject
    VagaDAO vagaDAO;
    @Inject
    CandidaturaDAO candidaturaDAO;
    @Inject
    AuditoriaBO auditoriaBO;

    public List<Vaga> listarAtivas() { return vagaDAO.listarAtivas(); }

    public List<Vaga> buscar(String termo) { return vagaDAO.buscar(termo); }
    public List<Vaga> listarTodas() { return vagaDAO.listarTodas(); }

    public Vaga buscar(Integer id) {
        Vaga vaga = vagaDAO.buscarPorId(id);
        if (vaga == null) {
            throw new IllegalArgumentException("Vaga não encontrada");
        }
        return vaga;
    }

    public Vaga criar(VagaDTO dto, Usuario admin) {
        exigirAdmin(admin);
        validar(dto);
        Vaga v = new Vaga(dto);
        v.setAtiva(dto.isAtiva() || dto.ativa == null);
        vagaDAO.salvar(v);
        auditoriaBO.registrar("CRIAR_VAGA", "Criou: " + v.getTitulo(), admin.getEmail());
        return v;
    }

    public Vaga atualizar(Integer id, VagaDTO dto, Usuario admin) {
        exigirAdmin(admin);
        validar(dto);
        Vaga v = buscar(id);
        v.setTitulo(dto.titulo.trim());
        v.setDescricao(dto.descricao.trim());
        v.setEmpresa(dto.empresa.trim());
        v.setLocalizacao(dto.localizacao.trim());
        v.setFaixaSalarial(dto.faixaSalarial);
        v.setDataExpiracao(dto.getDataExpiracaoAsDate());
        v.setAtiva(dto.isAtiva());
        vagaDAO.salvar(v);
        auditoriaBO.registrar("ATUALIZAR_VAGA", "Atualizou id=" + id, admin.getEmail());
        return v;
    }

    public void excluir(Integer id, Usuario admin) {
        exigirAdmin(admin);
        Vaga v = buscar(id);
        long candidaturas = candidaturaDAO.contarPorVaga(id);
        if (candidaturas > 0) {
            // Excluir apagaria o histórico dos candidatos; o caminho é desativar a vaga
            throw new IllegalArgumentException("A vaga \"" + v.getTitulo() + "\" possui " + candidaturas
                    + (candidaturas == 1 ? " candidatura" : " candidaturas")
                    + " e não pode ser excluída. Desmarque \"Ativa\" para encerrá-la.");
        }
        String t = v.getTitulo();
        vagaDAO.remover(id);
        auditoriaBO.registrar("EXCLUIR_VAGA", "Excluiu: " + t, admin.getEmail());
    }

    private void validar(VagaDTO dto) {
        if (dto.titulo == null || dto.titulo.isBlank()) throw new IllegalArgumentException("Título obrigatório");
        if (dto.descricao == null || dto.descricao.isBlank()) throw new IllegalArgumentException("Descrição obrigatória");
        if (dto.empresa == null || dto.empresa.isBlank()) throw new IllegalArgumentException("Empresa obrigatória");
        if (dto.localizacao == null || dto.localizacao.isBlank()) throw new IllegalArgumentException("Localização obrigatória");
    }

    private void exigirAdmin(Usuario u) {
        if (u == null || u.getTipo() != TipoUsuario.ADMIN) throw new IllegalArgumentException("Acesso restrito a administradores");
    }
}
