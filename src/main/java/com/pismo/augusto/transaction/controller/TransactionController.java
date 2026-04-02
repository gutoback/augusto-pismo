package com.pismo.augusto.transaction.controller;

import com.pismo.augusto.transaction.dto.OperationTypeDto;
import com.pismo.augusto.transaction.dto.RequestTransactionDto;
import com.pismo.augusto.transaction.dto.TransactionDto;
import com.pismo.augusto.transaction.model.RequestTransaction;
import com.pismo.augusto.transaction.service.OperationTypeService;
import com.pismo.augusto.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final OperationTypeService operationTypeService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@RequestBody RequestTransactionDto transactionDto) {
        RequestTransaction requestTransaction = modelMapper.map(transactionDto, RequestTransaction.class);
        TransactionDto response = modelMapper.map(transactionService.createTransaction(requestTransaction), TransactionDto.class);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/operation-types")
    public ResponseEntity<List<OperationTypeDto>> listOperationTypes() {
        return ResponseEntity.ok(operationTypeService.getOperationTypes().stream().map(type ->  modelMapper.map(type, OperationTypeDto.class)).toList());
    }
}
