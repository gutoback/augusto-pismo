package com.pismo.augusto.account.service;

import com.pismo.augusto.account.dto.CreateAccountRequestDto;
import com.pismo.augusto.account.entity.Account;
import com.pismo.augusto.account.repository.AccountRepository;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("test-unit")
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountServiceImpl accountService;

    private CreateAccountRequestDto createAccountRequestDto;
    private Account accountEntity;
    private UUID accountId;

    @BeforeEach
    void setUp() {
        accountId = UUID.randomUUID();
        createAccountRequestDto = new CreateAccountRequestDto();
        createAccountRequestDto.setDocumentNumber("12345678901");

        accountEntity = new Account();
        accountEntity.setId(accountId);
        accountEntity.setDocumentNumber("12345678901");
    }

    @Test
    void testCreateAccountSuccess() {
        when(accountRepository.save(any(Account.class))).thenReturn(accountEntity);

        Account result = accountService.createAccount(createAccountRequestDto.getDocumentNumber());

        assertNotNull(result);
        assertEquals("12345678901", result.getDocumentNumber());
        assertEquals(accountId, result.getId());
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testCreateAccountThrowsExceptionWhenSaveFails() {
        when(accountRepository.save(any(Account.class))).thenThrow(new HibernateException("Database error"));

        assertThrows(HibernateException.class, () -> accountService.createAccount(createAccountRequestDto.getDocumentNumber()));
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void testGetAccountSuccess() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.of(accountEntity));

        Optional<Account> result = accountService.getAccount(accountId);

        assertNotNull(result);
        assertEquals(accountId, result.get().getId());
        assertEquals("12345678901", result.get().getDocumentNumber());
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void testGetAccountReturnEmptyWhenAccountDoesNotExist() {
        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), accountService.getAccount(accountId));
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void testGetAccountWithInvalidUUID() {
        UUID invalidId = UUID.randomUUID();
        when(accountRepository.findById(invalidId)).thenReturn(Optional.empty());
        assertEquals(Optional.empty(), accountService.getAccount(invalidId));
    }

    @Test
    void testCreateAccountWithDifferentDocumentNumber() {
        CreateAccountRequestDto dto = new CreateAccountRequestDto();
        dto.setDocumentNumber("98765432109");

        Account expected = new Account();
        expected.setId(UUID.randomUUID());
        expected.setDocumentNumber("98765432109");

        when(accountRepository.save(any(Account.class))).thenReturn(expected);

        Account result = accountService.createAccount(dto.getDocumentNumber());

        assertEquals("98765432109", result.getDocumentNumber());
        verify(accountRepository, times(1)).save(any(Account.class));
    }
}
