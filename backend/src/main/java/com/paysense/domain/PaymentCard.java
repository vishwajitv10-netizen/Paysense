package com.paysense.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="payment_cards")
public class PaymentCard {
    @Id private UUID id=UUID.randomUUID();
    @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="wallet_id",nullable=false) private Wallet wallet;
    @Column(nullable=false,length=4) private String last4;
    @Column(nullable=false,length=20) private String brand="VISA";
    @Column(nullable=false,length=16) private String status="ACTIVE";
    @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
    protected PaymentCard(){}
    public PaymentCard(Wallet wallet,String last4){this.wallet=wallet;this.last4=last4;}
    public UUID getId(){return id;} public String getLast4(){return last4;} public String getBrand(){return brand;} public String getStatus(){return status;} public Instant getCreatedAt(){return createdAt;}
    public void freeze(){if(status.equals("CANCELLED"))throw new IllegalArgumentException("Cancelled cards cannot be changed.");status="FROZEN";}
    public void unfreeze(){if(status.equals("CANCELLED"))throw new IllegalArgumentException("Cancelled cards cannot be changed.");status="ACTIVE";}
}
