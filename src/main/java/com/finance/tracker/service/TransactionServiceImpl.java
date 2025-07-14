package com.finance.tracker.service;

import com.finance.tracker.entity.Transaction;
import com.finance.tracker.entity.User;
import com.finance.tracker.mapper.TransactionMapper;
import com.finance.tracker.repository.TransactionRepository;
import com.finance.tracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TransactionServiceImpl implements TransactionService{

    private final TransactionRepository transactionRepo;

    private final UserRepository userRepo;

    private final TransactionMapper transactionMapper;


    public TransactionServiceImpl(TransactionRepository transactionRepo, UserRepository userRepo, TransactionMapper transactionMapper) {
        this.transactionRepo = transactionRepo;
        this.userRepo = userRepo;
        this.transactionMapper = transactionMapper;
    }

    @Override
    public Transaction addTransaction(User user, Transaction transaction) {

        transaction.setUser(user);
        return transactionRepo.save(transaction);
    }

    @Override
    public List<Transaction> getUserTransactions(UUID userId) {

        return transactionRepo.findByUserId(userId);
    }

    @Override
    public List<Transaction> getUserTransactions(UUID user, LocalDate from, LocalDate to) {
        return transactionRepo.findByUserIdAndDateBetween(user, from, to);
    }

    @Override
    public void deleteTransaction(UUID txId, String email) {
        Transaction transaction = transactionRepo.findById(txId).orElseThrow(
                () -> new RuntimeException(" Transaction not found.")
        );

        if(!transaction.getUser().getEmail().equals(email)){
            throw new RuntimeException("Not auth to delete the tranx");
        }
        transactionRepo.delete(transaction);
    }

    @Override
    public Map<String, Double> getTransactionSummary(User user) {
        List<Transaction> txs = transactionRepo.findByUserId(user.getId());
        double income = txs.stream().
                filter(tx -> tx.getType().equalsIgnoreCase("income"))
                .mapToDouble(Transaction::getAmount).sum();
        double expense = txs.stream().
                filter(tx -> tx.getType().equalsIgnoreCase("expense"))
                .mapToDouble(Transaction::getAmount).sum();
        Map<String, Double> summary = new HashMap<>();
        summary.put("expense", expense);
        summary.put("income", income);
        summary.put("balance", income - expense);

        return summary;
    }


}
