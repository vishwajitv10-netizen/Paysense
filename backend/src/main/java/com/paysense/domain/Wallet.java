package com.paysense.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="wallets",uniqueConstraints=@UniqueConstraint(columnNames="customer_id"))
public class Wallet {
    @Id private UUID id=UUID.randomUUID();
    @OneToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="customer_id",nullable=false,unique=true) private Customer customer;
    @Column(nullable=false,length=3) private String currency="USD";
    @Column(nullable=false,precision=19,scale=2) private BigDecimal balance=BigDecimal.ZERO;
    @Column(nullable=false) private Instant createdAt=Instant.now();
    @Version private long version;
    protected Wallet() {}
    public Wallet(Customer customer){this.customer=customer;}
    public UUID getId(){return id;} public Customer getCustomer(){return customer;} public String getCurrency(){return currency;} public BigDecimal getBalance(){return balance;} public Instant getCreatedAt(){return createdAt;}
    public void debit(BigDecimal amount){if(balance.compareTo(amount)<0)throw new InsufficientBalanceException("Insufficient available balance.");balance=balance.subtract(amount);}
    public void credit(BigDecimal amount){balance=balance.add(amount);}
}
