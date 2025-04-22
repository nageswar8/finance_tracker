package com.finance.tracker.service;

import com.finance.tracker.entity.Transaction;
import com.finance.tracker.entity.User;
import com.finance.tracker.repository.TransactionRepository;
import com.finance.tracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepository transactionRepo;

    private final UserRepository userRepo;


    public TransactionServiceImpl(TransactionRepository transactionRepo, UserRepository userRepo) {
        this.transactionRepo = transactionRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Transaction addTransaction(UUID userId, Transaction transaction) {
        User user = userRepo.findById(userId).orElseThrow();
        transaction.setUser(user);
        return  transactionRepo.save(transaction);
    }

    @Override
    public List<Transaction> getUserTransactions(UUID userId) {

        return transactionRepo.findByUserId(userId);
    }
}
