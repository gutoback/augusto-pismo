package com.pismo.augusto.account.controller;

import com.pismo.augusto.account.dto.AccountDto;
import com.pismo.augusto.account.dto.CreateAccountRequestDto;
import com.pismo.augusto.account.entity.Account;
import com.pismo.augusto.account.service.AccountService;
import com.pismo.augusto.common.exception.NotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Validated
public class AccountController {
    private final AccountService accountService;
    private final ModelMapper modelMapper;

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccount(@PathVariable UUID id) {
        Optional<Account> account = accountService.getAccount(id);
        return account.map(value -> ResponseEntity.ok(modelMapper.map(value, AccountDto.class)))
                .orElseThrow(() -> new NotFoundException(id));
    }

    @PostMapping
    public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody CreateAccountRequestDto createAccountRequestDto) {
        Account account = accountService.createAccount(createAccountRequestDto.getDocumentNumber());
        return ResponseEntity.ok(modelMapper.map(account, AccountDto.class));
    }
}
