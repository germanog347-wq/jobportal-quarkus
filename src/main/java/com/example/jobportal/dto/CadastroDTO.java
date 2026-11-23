package com.example.jobportal.dto;

import jakarta.ws.rs.FormParam;

public class CadastroDTO {
    @FormParam("nome")
    public String nome;
    @FormParam("email")
    public String email;
    @FormParam("senha")
    public String senha;
    @FormParam("cpf")
    public String cpf;
    @FormParam("dataNascimento")
    public String dataNascimento;
    @FormParam("telefone")
    public String telefone;
    @FormParam("cidade")
    public String cidade;
}
