package com.pismo.augusto.transaction.service;

import com.pismo.augusto.transaction.entity.Transaction;
import com.pismo.augusto.transaction.model.RequestTransaction;
import com.pismo.augusto.transaction.repository.TransactionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
@Validated
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final OperationTypeService operationTypeService;

    @Override
    public Transaction createTransaction(@Valid RequestTransaction request) {

        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .accountId(request.getAccountId())
                .type(operationTypeService.getOperationType(request.getOperationTypeId()))
                .build();

        if(operationTypeService.shouldNegateAmount(request.getOperationTypeId()) && request.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            transaction.setAmount(transaction.getAmount().negate());
        }

        return transactionRepository.save(transaction);
    }
}
