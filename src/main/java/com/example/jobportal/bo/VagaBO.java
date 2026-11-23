package com.example.jobportal.bo;

import com.example.jobportal.dao.VagaDAO;
import com.example.jobportal.dto.VagaDTO;
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
    AuditoriaBO auditoriaBO;

    public List<Vaga> listarAtivas() { return vagaDAO.listarAtivas(); }

    public List<Vaga> buscar(String termo) { return vagaDAO.buscar(termo); }
    public List<Vaga> listarTodas() { return vagaDAO.listarTodas(); }

    public Vaga buscar(Long id) {
        return vagaDAO.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Vaga não encontrada"));
    }

    public Vaga criar(VagaDTO dto, Usuario admin) {
        exigirAdmin(admin);
        validar(dto);
        Vaga v = new Vaga(dto.titulo.trim(), dto.descricao.trim(), dto.empresa.trim(),
                dto.localizacao.trim(), dto.faixaSalarial, dto.getDataExpiracaoAsDate());
        v.ativa = dto.isAtiva() || dto.ativa == null;
        vagaDAO.salvar(v);
        auditoriaBO.registrar("CRIAR_VAGA", "Criou: " + v.titulo, admin.email);
        return v;
    }

    public Vaga atualizar(Long id, VagaDTO dto, Usuario admin) {
        exigirAdmin(admin);
        validar(dto);
        Vaga v = buscar(id);
        v.titulo = dto.titulo.trim();
        v.descricao = dto.descricao.trim();
        v.empresa = dto.empresa.trim();
        v.localizacao = dto.localizacao.trim();
        v.faixaSalarial = dto.faixaSalarial;
        v.dataExpiracao = dto.getDataExpiracaoAsDate();
        v.ativa = dto.isAtiva();
        vagaDAO.salvar(v);
        auditoriaBO.registrar("ATUALIZAR_VAGA", "Atualizou id=" + id, admin.email);
        return v;
    }

    public void excluir(Long id, Usuario admin) {
        exigirAdmin(admin);
        Vaga v = buscar(id);
        String t = v.titulo;
        vagaDAO.remover(id);
        auditoriaBO.registrar("EXCLUIR_VAGA", "Excluiu: " + t, admin.email);
    }

    private void validar(VagaDTO dto) {
        if (dto.titulo == null || dto.titulo.isBlank()) throw new IllegalArgumentException("Título obrigatório");
        if (dto.descricao == null || dto.descricao.isBlank()) throw new IllegalArgumentException("Descrição obrigatória");
        if (dto.empresa == null || dto.empresa.isBlank()) throw new IllegalArgumentException("Empresa obrigatória");
        if (dto.localizacao == null || dto.localizacao.isBlank()) throw new IllegalArgumentException("Localização obrigatória");
    }

    private void exigirAdmin(Usuario u) {
        if (u == null || !u.isAdmin) throw new IllegalArgumentException("Acesso restrito a administradores");
    }
}
