package com.pismo.augusto.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pismo.augusto.account.entity.Account;
import com.pismo.augusto.account.repository.AccountRepository;
import com.pismo.augusto.transaction.dto.RequestTransactionDto;
import com.pismo.augusto.transaction.entity.OperationType;
import com.pismo.augusto.transaction.entity.Transaction;
import com.pismo.augusto.transaction.repository.OperationTypeRepository;
import com.pismo.augusto.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Tag("test-integration")
class TransactionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private OperationTypeRepository operationTypeRepository;

    private ObjectMapper objectMapper;

    private OperationType creditOperationType;
    private RequestTransactionDto requestTransactionDto;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        operationTypeRepository.deleteAll();

        Account account = new Account();
        account.setDocumentNumber("12345678901");
        account = accountRepository.save(account);

        OperationType debitOperationType = new OperationType();
        debitOperationType.setDescription("DEBIT");
        debitOperationType.setShouldNegateAmount(true);
        debitOperationType = operationTypeRepository.save(debitOperationType);

        creditOperationType = new OperationType();
        creditOperationType.setDescription("CREDIT");
        creditOperationType.setShouldNegateAmount(false);
        creditOperationType = operationTypeRepository.save(creditOperationType);

        requestTransactionDto = new RequestTransactionDto();
        requestTransactionDto.setAccountId(account.getId());
        requestTransactionDto.setOperationTypeId(debitOperationType.getId());
        requestTransactionDto.setAmount(new BigDecimal("100.00"));
    }

    @Test
    void testCreateTransactionSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestTransactionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.amount").value(-100.00));

        assertEquals(1, transactionRepository.count());
    }

    @Test
    void testCreateTransactionWithCreditType() throws Exception {
        requestTransactionDto.setOperationTypeId(creditOperationType.getId());

        mockMvc.perform(post("/api/v1/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestTransactionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(100.00));
    }

    @Test
    void testCreateTransactionWithInvalidOperationType() throws Exception {
        requestTransactionDto.setOperationTypeId(java.util.UUID.randomUUID());

        mockMvc.perform(post("/api/v1/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestTransactionDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTransactionWithLargeAmount() throws Exception {
        requestTransactionDto.setAmount(new BigDecimal("999999.99"));

        mockMvc.perform(post("/api/v1/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestTransactionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(-999999.99));
    }

    @Test
    void testCreateTransactionWithZeroAmount() throws Exception {
        requestTransactionDto.setAmount(new BigDecimal("0.00"));

        mockMvc.perform(post("/api/v1/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestTransactionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(0.00));
    }

    @Test
    void testCreateMultipleTransactionsForSameAccount() throws Exception {
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/v1/transaction")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestTransactionDto)))
                    .andExpect(status().isOk());
        }

        assertEquals(3, transactionRepository.count());
    }

    @Test
    void testTransactionSetsEventDateAutomatically() throws Exception {
        mockMvc.perform(post("/api/v1/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestTransactionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.event_date").exists());

        Transaction transaction = transactionRepository.findAll().iterator().next();
        assertNotNull(transaction.getDate());
    }
}
