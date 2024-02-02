package com.wallet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wallet.enums.TypeEnum;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WalletItemDTO {

    private Long id;
    @NotNull(message = "Informe o ID da carteira")
    private Long wallet;
    @NotNull(message = "Informe uma data")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy", locale = "pt-BR", timezone = "Brazil/East")
    private Date date;
    @NotNull(message = "Informe um tipo")
    @Pattern(regexp = "^(ENTRADA|SAÍDA)$", message = "Para o tipo somente são aceitos os valores ENTRADA ou SAÍDA")
    private String type;
    @NotNull(message = "Informe uma descrição")
    @Length(min = 5, message = "A descrição deve ter no mínimo 5 caracteres")
    private String description;
    @NotNull(message = "Informe um valor")
    private BigDecimal value;

}
