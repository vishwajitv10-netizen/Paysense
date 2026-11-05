package com.paysense.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="fraud_reports",indexes=@Index(columnList="created_at"))
public class FraudReport {
 @Id private UUID id=UUID.randomUUID();
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="customer_id",nullable=false) private Customer reporter;
 @Column(nullable=false,length=64) private String upiId;
 @Column(nullable=false,length=40) private String scamType;
 @Column(nullable=false,length=500) private String description;
 @Column(nullable=false,length=20) private String status="OPEN";
 @Column(nullable=false,updatable=false) private Instant createdAt=Instant.now();
 protected FraudReport(){}
 public FraudReport(Customer reporter,String upiId,String scamType,String description){this.reporter=reporter;this.upiId=upiId;this.scamType=scamType;this.description=description;}
 public UUID getId(){return id;}public String getUpiId(){return upiId;}public String getScamType(){return scamType;}public String getDescription(){return description;}public String getStatus(){return status;}public Instant getCreatedAt(){return createdAt;}
}
