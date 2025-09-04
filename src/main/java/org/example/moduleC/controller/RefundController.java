package org.example.moduleC.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/refunds")
@Tag(name = "Refund", description = "APIs for refunding payments")
@SecurityRequirement(name = "bearerAuth")
public class RefundController {

    @PostMapping("/request")
    @Operation(summary = "Request a refund for a payment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Refund request accepted"),
            @ApiResponse(responseCode = "400", description = "Bad Request - invalid refund data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing/invalid token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user not allowed to request refund"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity - refund cannot be processed"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    public ResponseEntity<RefundResponse> requestRefund(@Valid @RequestBody RefundRequest request) {

        // 400: Basic validation
        if (request.getTransactionId() == null || request.getTransactionId().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RefundResponse(null, "Transaction ID is required"));
        }

        if (request.getAmount() <= 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new RefundResponse(null, "Amount must be positive"));
        }

        // 422: Business-level validation
        if (request.getAmount() > 5000) { // example limit
            return ResponseEntity.status(422)
                    .body(new RefundResponse(null, "Refund amount exceeds allowed limit"));
        }

        // Simulate success
        String generatedId = UUID.randomUUID().toString();
        return ResponseEntity.ok(new RefundResponse(generatedId,
                "Refund request for transaction " + request.getTransactionId() + " is accepted"));
    }

    public static class RefundRequest {

        @NotBlank(message = "Transaction ID is required")
        private String transactionId;

        @Min(value = 1, message = "Amount must be greater than zero")
        private double amount;

        // Getters and setters
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
    }

    public static class RefundResponse {
        private String id;
        private String message;
        private Instant timestamp;

        public RefundResponse(String id, String message) {
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
