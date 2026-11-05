package com.paysense.api;

import com.paysense.api.ApiModels.*;
import com.paysense.domain.Customer;
import com.paysense.repo.CustomerRepository;
import com.paysense.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final AuthService auth;private final CustomerRepository customers;
 public AuthController(AuthService auth,CustomerRepository customers){this.auth=auth;this.customers=customers;}
 @PostMapping("/register") public AuthResponse register(@Valid @RequestBody RegisterRequest request){return auth.register(request);}
 @PostMapping("/login") public AuthResponse login(@Valid @RequestBody LoginRequest request){return auth.login(request);}
 @GetMapping("/me") public UserResponse me(@AuthenticationPrincipal String subject){Customer c=customers.findById(UUID.fromString(subject)).orElseThrow(()->new AuthService.UnauthorizedException("Account no longer exists."));return new UserResponse(c.getId(),c.getName(),c.getEmail(),c.getPhone(),c.getCreatedAt());}
}
