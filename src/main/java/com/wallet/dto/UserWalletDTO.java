package com.wallet.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserWalletDTO {

    private Long id;
    @NotNull(message = "Informe o ID do usuário")
    private Long users;
    @NotNull(message = "Informe o ID da carteira")
    private Long wallet;

}
