package com.pismo.augusto.account.service;

import com.pismo.augusto.account.entity.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountService {

    Account createAccount(String documentNumber);
    Optional<Account> getAccount(UUID id);
}
