package com.pismo.augusto.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pismo.augusto.account.dto.CreateAccountRequestDto;
import com.pismo.augusto.account.entity.Account;
import com.pismo.augusto.account.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Tag("test-integration")
class AccountControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountRepository accountRepository;

    private ObjectMapper objectMapper;

    private CreateAccountRequestDto createAccountRequestDto;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        createAccountRequestDto = new CreateAccountRequestDto();
        createAccountRequestDto.setDocumentNumber("12345678901");
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateAccountSuccess() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAccountRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.document_number").value("12345678901"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertFalse(responseBody.isEmpty());
    }

    @Test
    void testGetAccountSuccess() throws Exception {
        Account account = new Account();
        account.setDocumentNumber("12345678901");
        Account savedAccount = accountRepository.save(account);

        mockMvc.perform(get("/api/v1/accounts/{id}", savedAccount.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedAccount.getId().toString()))
                .andExpect(jsonPath("$.document_number").value("12345678901"));
    }

    @Test
    void testGetAccountNotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/v1/accounts/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testCreateAccountWithDifferentDocumentNumber() throws Exception {
        createAccountRequestDto.setDocumentNumber("98765432109");

        mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAccountRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.document_number").value("98765432109"));
    }

    @Test
    void testCreateMultipleAccounts() throws Exception {
        for (int i = 0; i < 3; i++) {
            CreateAccountRequestDto dto = new CreateAccountRequestDto();
            dto.setDocumentNumber("1234567890" + i);

            mockMvc.perform(post("/api/v1/accounts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk());
        }

        assertEquals(3, accountRepository.count());
    }

    @Test
    void testCreateAccountWithInvalidDocumentNumber() throws Exception {
        createAccountRequestDto.setDocumentNumber("");

        mockMvc.perform(post("/api/v1/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAccountRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAccountReturnsCorrectFields() throws Exception {
        Account account = new Account();
        account.setDocumentNumber("55555555555");
        Account savedAccount = accountRepository.save(account);

        mockMvc.perform(get("/api/v1/accounts/{id}", savedAccount.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.document_number").value("55555555555"));
    }
}
