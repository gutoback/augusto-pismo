package com.pismo.augusto.transaction.service;

import com.pismo.augusto.common.exception.NotFoundException;
import com.pismo.augusto.transaction.entity.OperationType;
import com.pismo.augusto.transaction.entity.Transaction;
import com.pismo.augusto.transaction.model.RequestTransaction;
import com.pismo.augusto.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("test-unit")
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private OperationTypeService operationTypeService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private RequestTransaction requestTransaction;
    private OperationType operationType;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        UUID accountId = UUID.randomUUID();
        UUID operationTypeId = UUID.randomUUID();
        UUID transactionId = UUID.randomUUID();

        requestTransaction = new RequestTransaction();
        requestTransaction.setAccountId(accountId);
        requestTransaction.setOperationTypeId(operationTypeId);
        requestTransaction.setAmount(new BigDecimal("100.00"));

        transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setAccountId(accountId);
        transaction.setType(operationType);
        transaction.setAmount(new BigDecimal("-100.00"));
        transaction.setDate(new Date());
    }

    @Test
    void testCreateTransactionWithNegationSuccess() {
        when(operationTypeService.shouldNegateAmount(any())).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = transactionService.createTransaction(requestTransaction);

        assertNotNull(result);
        assertEquals(new BigDecimal("-100.00"), result.getAmount());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testCreateTransactionWithoutNegationSuccess() {
        transaction.setAmount(new BigDecimal("100.00"));
        when(operationTypeService.shouldNegateAmount(any())).thenReturn(false);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = transactionService.createTransaction(requestTransaction);

        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void testCreateTransactionThrowsNotFoundWhenOperationTypeNotFound() {
        when(operationTypeService.shouldNegateAmount(any())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> transactionService.createTransaction(requestTransaction));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testCreateTransactionWithZeroAmount() {
        requestTransaction.setAmount(new BigDecimal("0.00"));
        transaction.setAmount(new BigDecimal("0.00"));
        when(operationTypeService.shouldNegateAmount(any())).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = transactionService.createTransaction(requestTransaction);

        assertNotNull(result);
        assertEquals(new BigDecimal("0.00"), result.getAmount());
    }

    @Test
    void testCreateTransactionWithLargeAmount() {
        requestTransaction.setAmount(new BigDecimal("999999.99"));
        transaction.setAmount(new BigDecimal("-999999.99"));
        when(operationTypeService.shouldNegateAmount(any())).thenReturn(true);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        Transaction result = transactionService.createTransaction(requestTransaction);

        assertEquals(new BigDecimal("-999999.99"), result.getAmount());
    }
}
