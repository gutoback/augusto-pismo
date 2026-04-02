package com.pismo.augusto.transaction.service;

import com.pismo.augusto.transaction.entity.Transaction;
import com.pismo.augusto.transaction.model.RequestTransaction;
import jakarta.validation.Valid;

public interface TransactionService {

    Transaction createTransaction(@Valid RequestTransaction transaction);
}
