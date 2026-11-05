package com.paysense.api;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class ApiModels {
 private ApiModels(){}
 public record RegisterRequest(@NotBlank @Size(min=2,max=80) String name,@NotBlank @Email @Size(max=254) String email,@NotBlank @Size(min=10,max=72) String password){}
 public record LoginRequest(@NotBlank @Email String email,@NotBlank String password){}
 public record AuthResponse(String accessToken,String tokenType,Instant expiresAt,UserResponse user){}
 public record UserResponse(UUID id,String name,String email,String phone,Instant createdAt){}
 public record TransferRequest(@NotBlank @Size(min=2,max=80) String recipient,@NotNull @DecimalMin(value="0.01") @Digits(integer=12,fraction=2) BigDecimal amount){}
 public record TransactionResponse(UUID id,String name,String category,String type,BigDecimal amount,String currency,String status,String reference,Instant createdAt){}
 public record DashboardResponse(BigDecimal balance,String currency,BigDecimal monthlyIncome,BigDecimal monthlySpending,long transactionCount){}
 public record TransferResponse(TransactionResponse transaction,BigDecimal balance,String currency){}
 public record PageResponse<T>(java.util.List<T> items,int page,int size,long totalElements,int totalPages){}
 public record CardResponse(UUID id,String last4,String brand,String status,Instant createdAt){}
 public record ErrorResponse(String error,String message,Instant timestamp,String path){}
}
