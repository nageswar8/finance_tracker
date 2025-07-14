package com.finance.tracker.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class TransactionResponse {
    // TransactionResponse.java

    private UUID id;
    private String type;
    private String category;
    private String description;
    private Double amount;
    private LocalDate date;

}
