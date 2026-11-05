package com.paysense.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="payment_transactions",indexes=@Index(columnList="wallet_id,created_at"),uniqueConstraints=@UniqueConstraint(columnNames={"wallet_id","idempotency_key"}))
public class PaymentTransaction {
    @Id private UUID id=UUID.randomUUID();
    @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="wallet_id",nullable=false) private Wallet wallet;
    @Column(nullable=false,length=120) private String name;
    @Column(nullable=false,length=60) private String category;
    @Column(nullable=false,length=40) private String type;
    @Column(nullable=false,precision=19,scale=2) private BigDecimal amount;
    @Column(nullable=false,length=20) private String status="COMPLETED";
    @Column(nullable=false,unique=true,length=32) private String reference;
    @Column(name="idempotency_key",length=100) private String idempotencyKey;
    @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
    protected PaymentTransaction() {}
    public PaymentTransaction(Wallet wallet,String name,String category,String type,BigDecimal amount,String idempotencyKey){this.wallet=wallet;this.name=name;this.category=category;this.type=type;this.amount=amount;this.idempotencyKey=idempotencyKey;this.reference="PS-"+UUID.randomUUID().toString().replace("-","").substring(0,12).toUpperCase();}
    public UUID getId(){return id;} public Wallet getWallet(){return wallet;} public String getName(){return name;} public String getCategory(){return category;} public String getType(){return type;} public BigDecimal getAmount(){return amount;} public String getStatus(){return status;} public String getReference(){return reference;} public Instant getCreatedAt(){return createdAt;}
    public void markRefunded(){status="REFUNDED";}
}
