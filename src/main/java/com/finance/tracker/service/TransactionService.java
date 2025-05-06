package com.finance.tracker.service;

import com.finance.tracker.entity.Transaction;
import com.finance.tracker.entity.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface TransactionService {

    Transaction addTransaction(User user, Transaction transaction);

    List<Transaction> getUserTransactions(UUID userId);

    List<Transaction> getUserTransactions(UUID user, LocalDate from, LocalDate to);

    void deleteTransaction(UUID txId, String email);

    Map<String, Double> getTransactionSummary(User user);
}
