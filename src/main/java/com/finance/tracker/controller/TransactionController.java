package com.finance.tracker.controller;

import com.finance.tracker.entity.Transaction;
import com.finance.tracker.entity.User;
import com.finance.tracker.repository.UserRepository;
import com.finance.tracker.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

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

    public TransactionController(TransactionService transactionService,
                                 UserRepository userRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    private User getUserFromPrincipal(OAuth2User principal) {
        String email = principal.getAttribute("email");
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Operation(summary = "Add transaction", description = "Create a new transaction for the logged-in user.")
    @PostMapping
    public ResponseEntity<Transaction> addTransaction(
            @RequestBody Transaction tx,
            @AuthenticationPrincipal OAuth2User principal) {

        User user = getUserFromPrincipal(principal);
        return ResponseEntity.ok(transactionService.addTransaction(user, tx));
    }

    @Operation(summary = "Get transactions", description = "Fetch list of transactions for the logged-in user.")
    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions(
            @AuthenticationPrincipal OAuth2User principal) {

        User user = getUserFromPrincipal(principal);
        return ResponseEntity.ok(transactionService.getUserTransactions(user.getId()));
    }

    @Operation(summary = "Delete transaction", description = "Delete a transaction by its ID.")
    @DeleteMapping("/{txId}")
    public ResponseEntity<String> deleteTransaction(
            @PathVariable UUID txId,
            @AuthenticationPrincipal OAuth2User principal) {

        User user = getUserFromPrincipal(principal);
        transactionService.deleteTransaction(txId, user.getEmail());

        return ResponseEntity.ok("Transaction deleted");
    }

    @Operation(summary = "Get transaction summary", description = "Returns summary (total income/expense) for the user.")
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Double>> getSummary(
            @AuthenticationPrincipal OAuth2User principal) {

        User user = getUserFromPrincipal(principal);
        return ResponseEntity.ok(transactionService.getTransactionSummary(user));
    }

    @Operation(summary = "Filter transactions", description = "Returns transactions between a date range.")
    @GetMapping("/filter")
    public ResponseEntity<List<Transaction>> getFiltered(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @AuthenticationPrincipal OAuth2User principal) {

        User user = getUserFromPrincipal(principal);
        return ResponseEntity.ok(transactionService.getUserTransactions(user.getId(), from, to));
    }

    @Operation(summary = "Export transactions as CSV", description = "Exports user transactions in CSV format.")
    @GetMapping("/export")
    public void exportCSV(
            HttpServletResponse response,
            @AuthenticationPrincipal OAuth2User principal) throws IOException {

        User user = getUserFromPrincipal(principal);
        List<Transaction> txs = transactionService.getUserTransactions(user.getId());

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"transactions.csv\"");

        PrintWriter writer = response.getWriter();
        writer.println("Date,Type,Category,Description,Amount");

        for (Transaction tx : txs) {
            writer.printf("%s,%s,%s,%s,%.2f\n",
                    tx.getDate(), tx.getType(), tx.getCategory(), tx.getDescription(), tx.getAmount());
        }

        writer.flush();
    }
}
