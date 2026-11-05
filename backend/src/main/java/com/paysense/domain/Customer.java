package com.paysense.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="customers", uniqueConstraints=@UniqueConstraint(columnNames="email"))
public class Customer {
    @Id private UUID id = UUID.randomUUID();
    @Column(nullable=false,length=80) private String name;
    @Column(nullable=false,length=254) private String email;
    @Column(length=13) private String phone;
    @Column(nullable=false) private String passwordHash;
    @Column(nullable=false,updatable=false) private Instant createdAt = Instant.now();
    protected Customer() {}
    public Customer(String name,String email,String passwordHash){this.name=name;this.email=email.toLowerCase().trim();this.passwordHash=passwordHash;}
    public UUID getId(){return id;} public String getName(){return name;} public String getEmail(){return email;}
    public String getPasswordHash(){return passwordHash;} public Instant getCreatedAt(){return createdAt;} public String getPhone(){return phone;} public void updateProfile(String name,String phone){this.name=name;this.phone=phone;}
}
