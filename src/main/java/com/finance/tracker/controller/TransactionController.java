package com.finance.tracker.controller;

import com.finance.tracker.dto.TransactionResponse;
import com.finance.tracker.entity.Transaction;
import com.finance.tracker.entity.User;
import com.finance.tracker.mapper.TransactionMapper;
import com.finance.tracker.repository.UserRepository;
import com.finance.tracker.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "Endpoints for managing transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;

    public TransactionController(TransactionService transactionService,
                                 UserRepository userRepository,
                                 TransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
        this.transactionMapper = transactionMapper;
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    @Operation(summary = "Add transaction", description = "Create a new transaction for the logged-in user.")
    @PostMapping
    public ResponseEntity<TransactionResponse> addTransaction(@RequestBody Transaction tx) {
        User user = getCurrentUser();
        Transaction saved = transactionService.addTransaction(user, tx);
        return ResponseEntity.ok(transactionMapper.toDto(saved));
    }

    @Operation(summary = "Get transactions", description = "Fetch list of transactions for the logged-in user.")
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions() {
        User user = getCurrentUser();
        List<Transaction> transactions = transactionService.getUserTransactions(user.getId());
        List<TransactionResponse> responseList = transactions.stream()
                .map(transactionMapper::toDto)
                .toList();
        return ResponseEntity.ok(responseList);
    }

    @Operation(summary = "Delete transaction", description = "Delete a transaction by its ID.")
    @DeleteMapping("/{txId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable UUID txId) {
        User user = getCurrentUser();
        transactionService.deleteTransaction(txId, user.getEmail());
        return ResponseEntity.noContent().build(); // ✅ 204 No Content
    }

    @Operation(summary = "Get transaction summary", description = "Returns summary (total income/expense) for the user.")
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Double>> getSummary() {
        User user = getCurrentUser();
        return ResponseEntity.ok(transactionService.getTransactionSummary(user));
    }

    @Operation(summary = "Filter transactions", description = "Returns transactions between a date range.")
    @GetMapping("/filter")
    public ResponseEntity<List<TransactionResponse>> getFiltered(
            @RequestParam(value = "from", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,

            @RequestParam(value = "to", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        User user = getCurrentUser();

        LocalDate today = LocalDate.now();

        if (to == null) to = today;
        if (from == null) from = today.minusDays(7);

        List<Transaction> filtered = transactionService.getUserTransactions(user.getId(), from, to);
        List<TransactionResponse> dtos = filtered.stream().map(transactionMapper::toDto).toList();

        return ResponseEntity.ok(dtos);
    }


    @Operation(summary = "Export transactions as CSV", description = "Exports user transactions in CSV format.")
    @GetMapping("/export")
    public void exportCSV(HttpServletResponse response) throws IOException {
        User user = getCurrentUser();
        List<Transaction> txs = transactionService.getUserTransactions(user.getId());

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"transactions.csv\"");

        try (PrintWriter writer = response.getWriter()) {
            writer.println("Date,Type,Category,Description,Amount");

            txs.forEach(tx -> writer.printf("%s,%s,%s,%s,%.2f%n",
                    tx.getDate(), tx.getType(), tx.getCategory(),
                    tx.getDescription(), tx.getAmount()));
        }
    }
}
