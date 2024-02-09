package com.wallet.security.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class JwtAuthenticationDTO {

    @NotBlank(message = "Informe um email")
    private String email;
    @NotBlank(message = "Informe uma senha")
    private String password;
}
