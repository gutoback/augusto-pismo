package com.pismo.augusto.transaction.service;

import com.pismo.augusto.common.exception.NotFoundException;
import com.pismo.augusto.transaction.entity.OperationType;
import com.pismo.augusto.transaction.repository.OperationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OperationTypeServiceImpl implements OperationTypeService {

    private final OperationTypeRepository operationTypeRepository;

    @Override
    public List<OperationType> getOperationTypes() {
        return Streamable.of(operationTypeRepository.findAll()).toList();
    }

    @Override
    public boolean shouldNegateAmount(UUID id) {
        return getOperationType(id).getShouldNegateAmount();
    }

    @Override
    public OperationType getOperationType(UUID id) {
        return operationTypeRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
    }
}
