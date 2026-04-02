package com.pismo.augusto.account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountRequestDto {
    @JsonProperty("document_number")
    @Length(min = 1, message = "Document Number is mandatory")
    @NotNull(message = "Document Number is mandatory")
    private String documentNumber;
}
