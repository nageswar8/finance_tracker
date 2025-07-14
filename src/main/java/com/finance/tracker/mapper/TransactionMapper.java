package com.finance.tracker.mapper;

import com.finance.tracker.dto.TransactionResponse;
import com.finance.tracker.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public TransactionResponse toDto(Transaction transaction) {
        TransactionResponse dto = new TransactionResponse();
        dto.setId(transaction.getId());
        dto.setType(transaction.getType());
        dto.setCategory(transaction.getCategory());
        dto.setDescription(transaction.getDescription());
        dto.setAmount(transaction.getAmount());
        dto.setDate(transaction.getDate());
        return dto;
    }
}
