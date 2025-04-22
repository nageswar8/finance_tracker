package com.finance.tracker.repository;

import com.finance.tracker.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByUserId(UUID user_id);
    List<Transaction> findByUserIdAndDateBetween(UUID user_id, LocalDate start, LocalDate end);
}
