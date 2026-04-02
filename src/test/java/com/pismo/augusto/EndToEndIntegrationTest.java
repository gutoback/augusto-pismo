package com.pismo.augusto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pismo.augusto.account.dto.AccountDto;
import com.pismo.augusto.account.dto.CreateAccountRequestDto;
import com.pismo.augusto.account.repository.AccountRepository;
import com.pismo.augusto.transaction.dto.RequestTransactionDto;
import com.pismo.augusto.transaction.dto.TransactionDto;
import com.pismo.augusto.transaction.entity.OperationType;
import com.pismo.augusto.transaction.repository.OperationTypeRepository;
import com.pismo.augusto.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Execution(ExecutionMode.SAME_THREAD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@Tag("test-integration")
class EndToEndIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private OperationTypeRepository operationTypeRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @Order(1)
    void testCompleteWorkflowCreateAccountAndTransaction() throws Exception {
        CreateAccountRequestDto createAccountRequestDto = new CreateAccountRequestDto();
        createAccountRequestDto.setDocumentNumber("12345678901");

        MvcResult createAccountResult = mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAccountRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        String accountResponse = createAccountResult.getResponse().getContentAsString();
        String accountId = objectMapper.readTree(accountResponse).get("id").asText();

        mockMvc.perform(get("/api/v1/accounts/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.document_number").value("12345678901"));

        OperationType debitOperationType = new OperationType();
        debitOperationType.setDescription("DEBIT2");
        debitOperationType.setShouldNegateAmount(true);
        debitOperationType = operationTypeRepository.save(debitOperationType);

        RequestTransactionDto requestTransactionDto = new RequestTransactionDto();
        requestTransactionDto.setAccountId(UUID.fromString(accountId));
        requestTransactionDto.setOperationTypeId(debitOperationType.getId());
        requestTransactionDto.setAmount(new BigDecimal("500.00"));

        MvcResult createTransactionResult = mockMvc.perform(post("/api/v1/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestTransactionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(-500.00))
                .andReturn();

        TransactionDto transactionDtoResponse = objectMapper.readValue(createTransactionResult.getResponse().getContentAsString(), TransactionDto.class);
        assertEquals(-500, transactionDtoResponse.getAmount().doubleValue());

        assertEquals(1, transactionRepository.count());
        assertEquals(1, accountRepository.count());
        transactionRepository.deleteById(transactionDtoResponse.getId());
        accountRepository.deleteById(UUID.fromString(accountId));
        operationTypeRepository.deleteById(debitOperationType.getId());

    }

    @Test
    @Order(2)
    void testMultipleTransactionsForSingleAccount() throws Exception {
        CreateAccountRequestDto createAccountRequestDto = new CreateAccountRequestDto();
        createAccountRequestDto.setDocumentNumber("98765432109");

        MvcResult createAccountResult = mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAccountRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        String accountResponse = createAccountResult.getResponse().getContentAsString();
        String accountId = objectMapper.readTree(accountResponse).get("id").asText();

        OperationType debitOperationType = new OperationType();
        debitOperationType.setDescription("DEBIT1");
        debitOperationType.setShouldNegateAmount(true);
        debitOperationType = operationTypeRepository.save(debitOperationType);

        OperationType creditOperationType = new OperationType();
        creditOperationType.setDescription("CREDIT1");
        creditOperationType.setShouldNegateAmount(false);
        creditOperationType = operationTypeRepository.save(creditOperationType);

        RequestTransactionDto debitTransaction = new RequestTransactionDto();
        debitTransaction.setAccountId(UUID.fromString(accountId));
        debitTransaction.setOperationTypeId(debitOperationType.getId());
        debitTransaction.setAmount(new BigDecimal("100.00"));

        MvcResult createDebitTransactionResult = mockMvc.perform(post("/api/v1/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(debitTransaction)))
                .andExpect(status().isOk())
                .andReturn();

        TransactionDto debitTransactionResponse = objectMapper.readValue(createDebitTransactionResult.getResponse().getContentAsString(), TransactionDto.class);

        RequestTransactionDto creditTransaction = new RequestTransactionDto();
        creditTransaction.setAccountId(UUID.fromString(accountId));
        creditTransaction.setOperationTypeId(creditOperationType.getId());
        creditTransaction.setAmount(new BigDecimal("50.00"));

        MvcResult createCreditTransactionResult =  mockMvc.perform(post("/api/v1/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creditTransaction)))
                .andExpect(status().isOk())
                .andReturn();

        TransactionDto creditTransactionResponse = objectMapper.readValue(createCreditTransactionResult.getResponse().getContentAsString(), TransactionDto.class);

        assertEquals(2, transactionRepository.count());

        assertEquals(new BigDecimal("-100.00"), debitTransactionResponse.getAmount());
        assertEquals(new BigDecimal("50.00"), creditTransactionResponse.getAmount());

        transactionRepository.deleteById(debitTransactionResponse.getId());
        transactionRepository.deleteById(creditTransactionResponse.getId());
        accountRepository.deleteById(UUID.fromString(accountId));
        operationTypeRepository.deleteById(debitOperationType.getId());
        operationTypeRepository.deleteById(creditOperationType.getId());
    }

    @Test
    @Order(3)
    void testAccountRetrievalAfterCreation() throws Exception {
        CreateAccountRequestDto createAccountRequestDto = new CreateAccountRequestDto();
        createAccountRequestDto.setDocumentNumber("55555555555");

        MvcResult createResult = mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAccountRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        String response = createResult.getResponse().getContentAsString();
        String accountId = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(get("/api/v1/accounts/{id}", accountId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(accountId))
                .andExpect(jsonPath("$.document_number").value("55555555555"));
        accountRepository.deleteById(UUID.fromString(accountId));
    }

    @Test
    @Order(4)
    void testTransactionWithoutNegation() throws Exception {
        CreateAccountRequestDto createAccountRequestDto = new CreateAccountRequestDto();
        createAccountRequestDto.setDocumentNumber("11111111111");

        MvcResult accountResult = mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAccountRequestDto)))
                .andExpect(status().isOk())
                .andReturn();

        AccountDto accountDto =  objectMapper.readValue(accountResult.getResponse().getContentAsString(), AccountDto.class);

        OperationType creditOperationType = new OperationType();
        creditOperationType.setDescription("CREDIT3");
        creditOperationType.setShouldNegateAmount(false);
        creditOperationType = operationTypeRepository.save(creditOperationType);

        RequestTransactionDto creditTransaction = new RequestTransactionDto();
        creditTransaction.setAccountId(accountDto.getId());
        creditTransaction.setOperationTypeId(creditOperationType.getId());
        creditTransaction.setAmount(new BigDecimal("250.00"));

        MvcResult transaction = mockMvc.perform(post("/api/v1/transaction")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(creditTransaction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(250.00))
                .andReturn();

        TransactionDto transactionDto = objectMapper.readValue(transaction.getResponse().getContentAsString(), TransactionDto.class);
        assertEquals(new BigDecimal("250.00"), transactionDto.getAmount());

        transactionRepository.deleteById(transactionDto.getId());
        accountRepository.deleteById(accountDto.getId());
        operationTypeRepository.deleteById(creditOperationType.getId());
    }
}
