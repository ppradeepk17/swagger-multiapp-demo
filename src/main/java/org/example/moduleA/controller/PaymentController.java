package org.example.moduleA.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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
    @Operation(
            summary = "Validate a payment request",
            description = "Validates payment amount, currency, and payment method. "
                    + "Returns validation errors or a success response with a generated ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment request is valid",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request - invalid payment data",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - missing or invalid token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - user not allowed to perform this action",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "422", description = "Unprocessable Entity - business rule validation failed",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error - processing failed")
    })
    public ResponseEntity<PaymentResponse> validatePayment(@Valid @RequestBody PaymentRequest request) {

        // Business-level validation (422)
        if (request.getAmount() > 10000) {
            return ResponseEntity.status(422)
                    .body(new PaymentResponse(null, "Amount exceeds daily processing limit"));
        }

        if (request.getAmount() < 0) {
            return ResponseEntity.status(400)
                    .body(new PaymentResponse(null, "Amount should be positive"));
        }

        if (request.getCurrency() == null ||
                (!request.getCurrency().equalsIgnoreCase("USD") &&
                        !request.getCurrency().equalsIgnoreCase("EUR"))) {
            return ResponseEntity.status(422)
                    .body(new PaymentResponse(null, "Currency not supported for payment"));
        }

        if (request.getPaymentMethod() == null ||
                (!request.getPaymentMethod().equalsIgnoreCase("CARD") &&
                        !request.getPaymentMethod().equalsIgnoreCase("UPI") &&
                        !request.getPaymentMethod().equalsIgnoreCase("BANK_TRANSFER"))) {
            return ResponseEntity.status(422)
                    .body(new PaymentResponse(null, "Payment method not allowed"));
        }

        // Generate an ID and success message
        String generatedId = UUID.randomUUID().toString();
        return ResponseEntity.ok(new PaymentResponse(generatedId, "Payment request is valid"));
    }

    public static class PaymentRequest {

        @Positive(message = "Amount must be positive")
        @Schema(description = "Payment amount. Must be positive and cannot exceed 10,000 (daily limit).")
        private double amount;

        @NotBlank(message = "Currency is required")
        @Schema(description = "Currency of payment. Supported values: USD, EUR.")
        private String currency;

        @NotBlank(message = "Payment method is required")
        @Schema(description = "Payment method. Allowed values: CARD, UPI, BANK_TRANSFER.")
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

        @Schema(description = "Unique identifier assigned to the payment request (only if valid).")
        private String id;

        @Schema(description = "Validation result message.")
        private String message;

        @Schema(description = "Server timestamp when response was generated.")
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
