package com.paysense.events;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
public record TransactionEvent(UUID eventId,String eventType,Instant occurredAt,UUID transactionId,UUID walletId,BigDecimal amount,String currency){}
