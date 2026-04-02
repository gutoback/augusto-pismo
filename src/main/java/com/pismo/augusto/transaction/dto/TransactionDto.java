package com.pismo.augusto.transaction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private UUID id;
    private UUID accountId;
    private OperationTypeDto type;
    private BigDecimal amount;
    @JsonProperty(value = "event_date")
    private Date date;
}
