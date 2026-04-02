package com.pismo.augusto.transaction.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class OperationType {

    private UUID id;
    private String description;
    private Boolean shouldNegateAmount;
}
