package org.example.moduleA.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment Services", description = "APIs for processing and managing payments")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

    @PostMapping("/validate")
    @Operation(summary = "Validate a payment request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment request is valid"),
            @ApiResponse(responseCode = "400", description = "Bad Request - invalid payment data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user not allowed to perform this action"),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity - business rule validation failed"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - processing failed")
    })
    public ResponseEntity<PaymentResponse> validatePayment(@Valid @RequestBody PaymentRequest request) {

        // Business-level validation (422)
        if (request.getAmount() > 10000) {
            return ResponseEntity.status(422).body(new PaymentResponse(null, "Amount exceeds daily processing limit"));
        }

        if (request.getAmount() < 0) {
            return ResponseEntity.status(400).body(new PaymentResponse(null, "Amount should be positive"));
        }

        if (!request.getCurrency().equalsIgnoreCase("USD") &&
                !request.getCurrency().equalsIgnoreCase("EUR")) {
            return ResponseEntity.status(422).body(new PaymentResponse(null, "Currency not supported for payment"));
        }

        if (!request.getPaymentMethod().equalsIgnoreCase("CARD") &&
                !request.getPaymentMethod().equalsIgnoreCase("UPI") &&
                !request.getPaymentMethod().equalsIgnoreCase("BANK_TRANSFER")) {
            return ResponseEntity.status(422).body(new PaymentResponse(null, "Payment method not allowed"));
        }

        // Generate an ID and success message
        String generatedId = UUID.randomUUID().toString();
        return ResponseEntity.ok(new PaymentResponse(generatedId, "Payment request is valid"));
    }

    public static class PaymentRequest {

        @Positive(message = "Amount must be positive")
        private double amount;

        @NotBlank(message = "Currency is required")
        private String currency;

        @NotBlank(message = "Payment method is required")
        private String paymentMethod;

        // Getters and setters
        public double getAmount() { return amount; }
        public void setAmount(double amount) { this.amount = amount; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    }

    public static class PaymentResponse {
        private String id;
        private String message;
        private Instant timestamp;

        public PaymentResponse(String id, String message) {
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
