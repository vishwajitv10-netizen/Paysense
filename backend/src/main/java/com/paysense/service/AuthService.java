package com.paysense.service;

import com.paysense.api.ApiModels.*;
import com.paysense.domain.Customer;
import com.paysense.domain.Wallet;
import com.paysense.repo.CustomerRepository;
import com.paysense.repo.WalletRepository;
import com.paysense.repo.TransactionRepository;
import com.paysense.repo.LedgerRepository;
import com.paysense.domain.PaymentTransaction;
import com.paysense.domain.LedgerEntry;
import com.paysense.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Locale;

@Service
public class AuthService {
 private final CustomerRepository customers;private final WalletRepository wallets;private final PasswordEncoder encoder;private final JwtService jwt;private final long ttl;private final java.math.BigDecimal demoBalance;private final TransactionRepository transactions;private final LedgerRepository ledger;
 public AuthService(CustomerRepository customers,WalletRepository wallets,PasswordEncoder encoder,JwtService jwt,@Value("${app.jwt.expiration-seconds:3600}")long ttl,@Value("${app.demo.initial-balance:12840.50}")java.math.BigDecimal demoBalance,TransactionRepository transactions,LedgerRepository ledger){this.customers=customers;this.wallets=wallets;this.encoder=encoder;this.jwt=jwt;this.ttl=ttl;this.demoBalance=demoBalance;this.transactions=transactions;this.ledger=ledger;}
 @Transactional public AuthResponse register(RegisterRequest r){String email=r.email().trim().toLowerCase(Locale.ROOT);if(customers.existsByEmailIgnoreCase(email))throw new IllegalArgumentException("An account with this email already exists.");Customer c=customers.save(new Customer(r.name().trim(),email,encoder.encode(r.password())));Wallet wallet=wallets.save(new Wallet(c));if(demoBalance.signum()>0){wallet.credit(demoBalance);PaymentTransaction opening=transactions.save(new PaymentTransaction(wallet,"Demo opening balance","Income","Opening balance",demoBalance,"opening:"+wallet.getId()));ledger.save(new LedgerEntry(opening,"wallet:"+wallet.getId(),demoBalance));ledger.save(new LedgerEntry(opening,"platform:demo-funding",demoBalance.negate()));}return response(c);}
 @Transactional(readOnly=true) public AuthResponse login(LoginRequest r){Customer c=customers.findByEmailIgnoreCase(r.email().trim()).orElse(null);if(c==null||!encoder.matches(r.password(),c.getPasswordHash()))throw new UnauthorizedException("Email or password is incorrect.");return response(c);}
 private AuthResponse response(Customer c){return new AuthResponse(jwt.issue(c.getId(),c.getEmail()),"Bearer",Instant.now().plusSeconds(ttl),new UserResponse(c.getId(),c.getName(),c.getEmail(),c.getPhone(),c.getCreatedAt()));}
 public static class UnauthorizedException extends RuntimeException{public UnauthorizedException(String message){super(message);}}
}
