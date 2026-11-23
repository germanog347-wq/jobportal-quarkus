package com.example.jobportal;

import com.example.jobportal.dao.UsuarioDAO;
import com.example.jobportal.dao.VagaDAO;
import com.example.jobportal.model.Usuario;
import com.example.jobportal.model.Vaga;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.LocalDate;

@ApplicationScoped
public class DataSeeder {

    @Inject
    UsuarioDAO usuarioDAO;
    @Inject
    VagaDAO vagaDAO;

    @Transactional
    void onStart(@Observes StartupEvent event) {
        if (usuarioDAO.contar() == 0) {
            Usuario admin = new Usuario("Administrador", "admin@portal.com", "admin123");
            admin.isAdmin = true;
            admin.ativo = true;
            admin.cpf = "00000000000";
            admin.telefone = "(62) 99999-0000";
            admin.cidade = "Goiânia";
            admin.dataNascimento = LocalDate.of(1990, 1, 1);
            usuarioDAO.salvar(admin);

            Usuario aluno = new Usuario("Aluno Teste", "aluno@email.com", "123456");
            aluno.isAdmin = false;
            aluno.ativo = true;
            aluno.cpf = "12345678900";
            aluno.telefone = "(62) 98888-1111";
            aluno.cidade = "Luziânia";
            aluno.dataNascimento = LocalDate.of(2000, 5, 15);
            usuarioDAO.salvar(aluno);
        }

        if (vagaDAO.contar() == 0) {
            salvarVaga("Desenvolvedor Java Junior",
                    "Vaga para desenvolvedor Java com conhecimento em Spring ou Quarkus. Trabalho em equipe ágil.",
                    "TechBrasil LTDA", "São Paulo - SP", "R$ 4.000 - R$ 6.000", LocalDate.of(2026, 12, 31));
            salvarVaga("Analista de Sistemas",
                    "Atuação em levantamento de requisitos, modelagem de software e apoio à equipe.",
                    "Sistemas Goiás SA", "Goiânia - GO", "R$ 5.500 - R$ 7.500", LocalDate.of(2026, 11, 30));
            salvarVaga("Desenvolvedor Front-end",
                    "Experiência com HTML, CSS, JavaScript e frameworks modernos (React ou Vue).",
                    "WebDev Solutions", "Remoto", "R$ 4.500 - R$ 7.000", LocalDate.of(2026, 10, 31));
            salvarVaga("Estágio em TI",
                    "Estágio para estudantes de Sistemas de Informação ou áreas afins. Carga horária de 6h.",
                    "IFG Parceiros", "Luziânia - GO", "Bolsa + auxílio transporte", LocalDate.of(2026, 9, 30));
            salvarVaga("DevOps Junior",
                    "Conhecimentos básicos em Linux, Docker e CI/CD. Interesse em cloud (AWS/Azure).",
                    "Cloud Nine Tech", "Brasília - DF", "R$ 5.000 - R$ 7.000", LocalDate.of(2026, 12, 15));
            salvarVaga("Analista de Suporte",
                    "Atendimento a usuários, abertura de chamados e resolução de problemas.",
                    "Suporte Total Ltda", "Anápolis - GO", "R$ 2.800 - R$ 3.500", LocalDate.of(2026, 11, 15));
        }
    }

    private void salvarVaga(String titulo, String descricao, String empresa,
                            String localizacao, String faixaSalarial, LocalDate dataExpiracao) {
        Vaga v = new Vaga(titulo, descricao, empresa, localizacao, faixaSalarial, dataExpiracao);
        v.ativa = true;
        vagaDAO.salvar(v);
    }
}
