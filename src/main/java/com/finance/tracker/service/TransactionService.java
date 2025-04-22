package com.finance.tracker.service;

import com.finance.tracker.entity.Transaction;

import java.util.List;
import java.util.UUID;

public interface TransactionService {

    Transaction addTransaction(UUID userId, Transaction transaction);

    List<Transaction> getUserTransactions(UUID userId);
}
