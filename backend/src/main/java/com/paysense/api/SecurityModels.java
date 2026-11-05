package com.paysense.api;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public final class SecurityModels {
 private SecurityModels(){}
 public record AnalyzeRequest(@NotBlank @Size(max=100)String recipient,@NotBlank @Pattern(regexp="^[A-Za-z0-9._-]{2,64}@[A-Za-z]{2,30}$",message="Enter a valid UPI ID (name@bank).")String upiId,@NotNull @DecimalMin("1.00") @Digits(integer=10,fraction=2)BigDecimal amount,@Size(max=300)String note){}
 public record LayerResult(int layer,String name,String status,String detail){}
 public record AnalyzeResponse(int riskScore,String riskLevel,boolean requiresCaution,String summary,List<String>safetyTips,List<LayerResult>layerResults){}
 public record ScamEducation(String type,String title,String description,List<String>warningSigns,List<String>whatToDo){}
}
