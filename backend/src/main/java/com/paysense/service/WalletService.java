package com.paysense.service;

import com.paysense.api.ApiModels.*;
import com.paysense.domain.*;
import com.paysense.repo.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
public class WalletService {
 private final WalletRepository wallets;private final TransactionRepository transactions;private final LedgerRepository ledger;private final com.paysense.events.DomainEventPublisher eventPublisher;private final SecurityAssessmentService securityAssessment;
 public WalletService(WalletRepository wallets,TransactionRepository transactions,LedgerRepository ledger,com.paysense.events.DomainEventPublisher eventPublisher,SecurityAssessmentService securityAssessment){this.wallets=wallets;this.transactions=transactions;this.ledger=ledger;this.eventPublisher=eventPublisher;this.securityAssessment=securityAssessment;}
 @Transactional(readOnly=true) public DashboardResponse dashboard(UUID user){Wallet w=wallets.findByCustomerId(user).orElseThrow(()->new IllegalArgumentException("Wallet not found."));Instant start=YearMonth.now(ZoneOffset.UTC).atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC);return new DashboardResponse(w.getBalance(),w.getCurrency(),transactions.incomeSince(w.getId(),start),transactions.spendingSince(w.getId(),start).abs(),transactions.countByWalletId(w.getId()));}
 @Transactional(readOnly=true) public PageResponse<TransactionResponse> list(UUID user,String query,int page,int size){Wallet w=wallets.findByCustomerId(user).orElseThrow(()->new IllegalArgumentException("Wallet not found."));var result=transactions.search(w.getId(),query==null?"":query.trim(),PageRequest.of(page,size,Sort.by(Sort.Direction.DESC,"createdAt")));return new PageResponse<>(result.getContent().stream().map(WalletService::view).toList(),page,size,result.getTotalElements(),result.getTotalPages());}
 @Transactional(readOnly=true) public TransactionResponse get(UUID user,UUID id){Wallet w=wallets.findByCustomerId(user).orElseThrow(()->new IllegalArgumentException("Wallet not found."));return transactions.findByIdAndWalletId(id,w.getId()).map(WalletService::view).orElseThrow(()->new IllegalArgumentException("Transaction not found."));}
 @Transactional public TransferResponse transfer(UUID user,TransferRequest req,String idempotency){Wallet w=wallets.findLockedByCustomerId(user).orElseThrow(()->new IllegalArgumentException("Wallet not found."));if(idempotency!=null){var prior=transactions.findByWalletIdAndIdempotencyKey(w.getId(),idempotency);if(prior.isPresent())return new TransferResponse(view(prior.get()),w.getBalance(),w.getCurrency());}BigDecimal amount=money(req.amount());w.debit(amount);var tx=post(w,"Transfer to "+req.recipient().trim(),"Transfer","Transfer",amount.negate(),idempotency);return new TransferResponse(tx,w.getBalance(),w.getCurrency());}
 @Transactional public TransactionResponse payment(UUID user,String merchant,String upiId,String note,BigDecimal raw,boolean confirmedRisk,String idempotency){Wallet w=wallets.findLockedByCustomerId(user).orElseThrow(()->new IllegalArgumentException("Wallet not found."));if(idempotency!=null){var prior=transactions.findByWalletIdAndIdempotencyKey(w.getId(),idempotency);if(prior.isPresent())return view(prior.get());}if(merchant==null||merchant.isBlank()||merchant.trim().length()>100)throw new IllegalArgumentException("A valid merchant is required.");BigDecimal amount=money(raw);var risk=securityAssessment.analyze(new com.paysense.api.SecurityModels.AnalyzeRequest(merchant,upiId,amount,note));if(risk.requiresCaution()&&!confirmedRisk)throw new RiskConfirmationRequiredException("Safety check: "+risk.summary());w.debit(amount);return post(w,merchant.trim(),"Merchant payment","Payment",amount.negate(),idempotency);}
 @Transactional public TransactionResponse refund(UUID user,UUID transactionId,String key){Wallet w=wallets.findLockedByCustomerId(user).orElseThrow(()->new IllegalArgumentException("Wallet not found."));if(key!=null){var prior=transactions.findByWalletIdAndIdempotencyKey(w.getId(),key);if(prior.isPresent())return view(prior.get());}PaymentTransaction original=transactions.findByIdAndWalletId(transactionId,w.getId()).orElseThrow(()->new IllegalArgumentException("Transaction not found."));if(!original.getStatus().equals("COMPLETED")||original.getAmount().compareTo(BigDecimal.ZERO)>=0)throw new IllegalStateException("Only completed debit transactions can be refunded once.");original.markRefunded();BigDecimal amount=original.getAmount().abs();w.credit(amount);return post(w,"Refund: "+original.getName(),"Refund","Refund",amount,key);}
 private TransactionResponse post(Wallet w,String name,String category,String type,BigDecimal signed,String key){PaymentTransaction tx=transactions.save(new PaymentTransaction(w,name,category,type,signed,key));ledger.save(new LedgerEntry(tx,"wallet:"+w.getId(),signed));ledger.save(new LedgerEntry(tx,"platform:clearing",signed.negate()));eventPublisher.transactionCompleted(tx);return view(tx);}
 private BigDecimal money(BigDecimal value){if(value==null||value.scale()>2||value.compareTo(BigDecimal.ZERO)<=0||value.precision()>14)throw new IllegalArgumentException("Amount must be positive and have at most two decimal places.");return value.setScale(2,RoundingMode.UNNECESSARY);}
 public static TransactionResponse view(PaymentTransaction t){return new TransactionResponse(t.getId(),t.getName(),t.getCategory(),t.getType(),t.getAmount(),t.getWallet().getCurrency(),t.getStatus(),t.getReference(),t.getCreatedAt());}
}
