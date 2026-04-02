package com.pismo.augusto.transaction.service;

import com.pismo.augusto.transaction.entity.OperationType;

import java.util.List;
import java.util.UUID;

public interface OperationTypeService {

    List<OperationType> getOperationTypes();

    boolean shouldNegateAmount(UUID id);

    OperationType getOperationType(UUID id);
}
