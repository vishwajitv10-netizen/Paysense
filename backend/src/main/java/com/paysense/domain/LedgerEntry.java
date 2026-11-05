package com.paysense.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="ledger_entries",indexes=@Index(columnList="transaction_id"))
public class LedgerEntry {
    @Id private UUID id=UUID.randomUUID();
    @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="transaction_id",nullable=false) private PaymentTransaction transaction;
    @Column(nullable=false,length=100) private String account;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal amount;
    @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
    protected LedgerEntry(){}
    public LedgerEntry(PaymentTransaction transaction,String account,BigDecimal amount){this.transaction=transaction;this.account=account;this.amount=amount;}
}
