package com.paysense.api;

import com.paysense.api.ApiModels.*;
import com.paysense.service.WalletService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController @Validated @RequestMapping("/api")
public class WalletController {
 private final WalletService service;public WalletController(WalletService service){this.service=service;}
 @GetMapping("/dashboard") public DashboardResponse dashboard(@AuthenticationPrincipal String id){return service.dashboard(UUID.fromString(id));}
 @GetMapping("/transactions") public PageResponse<TransactionResponse> transactions(@AuthenticationPrincipal String id,@RequestParam(defaultValue="")String q,@RequestParam(defaultValue="0")@Min(0)int page,@RequestParam(defaultValue="20")@Min(1)@Max(100)int size){return service.list(UUID.fromString(id),q,page,size);}
 @GetMapping("/transactions/{transactionId}") public TransactionResponse transaction(@AuthenticationPrincipal String id,@PathVariable UUID transactionId){return service.get(UUID.fromString(id),transactionId);}
 @PostMapping("/transfers") @org.springframework.web.bind.annotation.ResponseStatus(org.springframework.http.HttpStatus.CREATED) public TransferResponse transfer(@AuthenticationPrincipal String id,@Valid @RequestBody TransferRequest request,@RequestHeader(value="Idempotency-Key",required=false)String key){return service.transfer(UUID.fromString(id),request,key);}
 @PostMapping("/transactions/{transactionId}/refund") public TransactionResponse refund(@AuthenticationPrincipal String id,@PathVariable UUID transactionId,@RequestHeader(value="Idempotency-Key",required=false)String key){return service.refund(UUID.fromString(id),transactionId,key);}
 @PostMapping("/payments") public TransactionResponse payment(@AuthenticationPrincipal String id,@Valid @RequestBody PaymentRequest request,@RequestHeader(value="Idempotency-Key",required=false)String key){return service.payment(UUID.fromString(id),request.merchant(),request.recipientUpiId(),request.note(),request.amount(),request.confirmedRisk(),key);}
 public record PaymentRequest(@jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max=100)String merchant,@jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Pattern(regexp="^[A-Za-z0-9._-]{2,64}@[A-Za-z]{2,30}$")String recipientUpiId,@jakarta.validation.constraints.Size(max=300)String note,@jakarta.validation.constraints.NotNull @jakarta.validation.constraints.DecimalMin("0.01") @jakarta.validation.constraints.Digits(integer=12,fraction=2)java.math.BigDecimal amount,boolean confirmedRisk){}
}
