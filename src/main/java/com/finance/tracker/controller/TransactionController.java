package com.finance.tracker.controller;

import com.finance.tracker.entity.Transaction;
import com.finance.tracker.entity.User;
import com.finance.tracker.repository.UserRepository;
import com.finance.tracker.service.TransactionService;
import com.finance.tracker.util.UserUtil;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {


    private final TransactionService transactionService;
    private final UserRepository userRepository;
    private final UserUtil userUtil;

    public TransactionController(TransactionService transactionService,
                                 UserRepository userRepository, UserUtil userUtil) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
        this.userUtil = userUtil;
    }

    @PostMapping
    public ResponseEntity<Transaction> addTransaction(@RequestBody Transaction tx, Principal principal) {
        User user = userUtil.getUser(principal, userRepository);
        return ResponseEntity.ok(transactionService.addTransaction(user, tx));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getTransactions(Principal principal) {
        User user = userUtil.getUser(principal, userRepository);
        return ResponseEntity.ok(transactionService.getUserTransactions(user.getId()));
    }

    @DeleteMapping("/{txId}")
    public ResponseEntity<String> deleteTransaction(@PathVariable UUID txId, Principal principal) {
        String email = principal.getName();
        transactionService.deleteTransaction(txId, email);
        return ResponseEntity.ok("Transaction deleted");
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Double>> getSummary(Principal principal) {
        User user = userUtil.getUser(principal, userRepository);
        return ResponseEntity.ok(transactionService.getTransactionSummary(user));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Transaction>> getFiltered(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Principal principal
            ) {
        User user = userUtil.getUser(principal, userRepository);
        return ResponseEntity.ok(transactionService.getUserTransactions(user.getId(), from, to));
    }

    @GetMapping("/export")
    public void exportCSV(HttpServletResponse response, Principal principal) throws IOException {
        User user = userUtil.getUser(principal, userRepository);
        List<Transaction> txs = transactionService.getUserTransactions(user.getId());
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"transactions.csv\"");
        PrintWriter writer = response.getWriter();

        writer.println("Date,Type,Category,Description,Amount");

        for(Transaction tx: txs) {
            writer.printf("%s,%s,%s,%s,%.2f\n",
                    tx.getDate(), tx.getType(), tx.getCategory(), tx.getDescription(), tx.getAmount());
        }
        writer.flush();
    }


}
