package com.paysense.api;

import com.paysense.domain.Customer;
import com.paysense.domain.FraudReport;
import com.paysense.repo.CustomerRepository;
import com.paysense.repo.FraudReportRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController @RequestMapping("/api/fraud-reports")
public class FraudReportController {
 public record CreateReport(@NotBlank @Pattern(regexp="^[A-Za-z0-9._-]{2,64}@[A-Za-z]{2,30}$")String upiId,@NotBlank @Size(max=40)String scamType,@NotBlank @Size(min=10,max=500)String description){}
 public record ReportView(UUID id,String upiId,String scamType,String description,String status,Instant createdAt){}
 private final FraudReportRepository reports;private final CustomerRepository customers;
 public FraudReportController(FraudReportRepository reports,CustomerRepository customers){this.reports=reports;this.customers=customers;}
 @PostMapping @org.springframework.web.bind.annotation.ResponseStatus(org.springframework.http.HttpStatus.CREATED) public ReportView create(@AuthenticationPrincipal String subject,@Valid @RequestBody CreateReport request){Customer customer=customers.findById(UUID.fromString(subject)).orElseThrow(()->new IllegalArgumentException("Account not found."));return view(reports.save(new FraudReport(customer,request.upiId().toLowerCase(),request.scamType().trim(),request.description().trim())));}
 @GetMapping public List<ReportView> mine(@AuthenticationPrincipal String subject){return reports.findTop100ByReporterIdOrderByCreatedAtDesc(UUID.fromString(subject)).stream().map(FraudReportController::view).toList();}
 private static ReportView view(FraudReport r){return new ReportView(r.getId(),r.getUpiId(),r.getScamType(),r.getDescription(),r.getStatus(),r.getCreatedAt());}
}
