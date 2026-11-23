package com.example.jobportal.dto;

import jakarta.ws.rs.FormParam;

public class LoginDTO {
    @FormParam("email")
    public String email;
    @FormParam("senha")
    public String senha;
}
