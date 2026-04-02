package com.pismo.augusto.transaction.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class RequestTransaction {

    @NotNull(message = "Account should be provided")
    private UUID accountId;
    @NotNull(message = "Amount should be provided")
    private BigDecimal amount;
    @NotNull(message = "Type should be provided")
    private UUID operationTypeId;

}
