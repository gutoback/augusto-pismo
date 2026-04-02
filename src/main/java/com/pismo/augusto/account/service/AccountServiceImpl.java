package com.pismo.augusto.account.service;

import com.pismo.augusto.account.entity.Account;
import com.pismo.augusto.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @Override
    public Account createAccount(String documentNumber) {
        return accountRepository.save(Account.builder().documentNumber(documentNumber).build());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Account> getAccount(UUID id) {
        return accountRepository.findById(id);
    }

}
