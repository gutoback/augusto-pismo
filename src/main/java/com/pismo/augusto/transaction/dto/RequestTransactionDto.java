package com.pismo.augusto.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestTransactionDto {
    private UUID accountId;
    private UUID operationTypeId;
    private BigDecimal amount;
}
