package com.example.jobportal.bo;

import com.example.jobportal.dao.AuditoriaDAO;
import com.example.jobportal.dao.UsuarioDAO;
import com.example.jobportal.model.Log;
import com.example.jobportal.model.Usuario;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class AuditoriaBO {

    @Inject
    AuditoriaDAO auditoriaDAO;
    @Inject
    UsuarioDAO usuarioDAO;

    /**
     * Adaptador temporário: mantém a assinatura antiga e grava na entidade Log.
     * No passo 6 será substituído por LogBO.registrarAcao(Usuario, String).
     */
    public void registrar(String acao, String detalhe, String usuarioEmail) {
        Usuario usuario = null;
        if (usuarioEmail != null && !usuarioEmail.isBlank()) {
            usuario = usuarioDAO.buscarPorEmail(usuarioEmail.trim().toLowerCase()).orElse(null);
        }

        String texto = acao + " - " + detalhe;
        if (usuario == null && usuarioEmail != null && !usuarioEmail.isBlank()) {
            texto += " (e-mail: " + usuarioEmail + ")";
        }

        Log log = new Log();
        log.setAcao(texto);
        log.setUsuario(usuario);
        auditoriaDAO.salvar(log);
    }

    public List<Log> listarTodas() {
        return auditoriaDAO.listarTodas();
    }
}
