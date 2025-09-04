package org.example.moduleB.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction Status", description = "APIs for transaction status of payments")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    @GetMapping("/status/{transactionId}")
    @Operation(summary = "Get transaction status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction found"),
            @ApiResponse(responseCode = "400", description = "Bad Request - invalid transaction ID"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing/invalid token"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<TransactionResponse> getTransactionStatus(@PathVariable String transactionId) {

        // 400: Null or empty check
        if (transactionId == null || transactionId.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new TransactionResponse(null, "Transaction ID is required"));
        }

        // 400: Format validation
        if (!transactionId.matches("^TXN\\d{6}$")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new TransactionResponse(null, "Invalid transaction ID format. Example: TXN123456"));
        }

        // 404: Simulate transaction lookup
        boolean transactionExists = true; // replace with real lookup
        if (!transactionExists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new TransactionResponse(null, "Transaction not found"));
        }

        // 200: Success
        String generatedId = UUID.randomUUID().toString();
        return ResponseEntity.ok(new TransactionResponse(generatedId,
                "Transaction " + transactionId + " is SUCCESSFUL"));
    }

    public static class TransactionResponse {
        private String id;
        private String message;
        private Instant timestamp;

        public TransactionResponse(String id, String message) {
            this.id = id;
            this.message = message;
            this.timestamp = Instant.now();
        }

        // Getters
        public String getId() { return id; }
        public String getMessage() { return message; }
        public Instant getTimestamp() { return timestamp; }
    }
}
