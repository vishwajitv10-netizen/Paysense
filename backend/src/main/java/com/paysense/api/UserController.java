package com.paysense.api;

import com.paysense.api.ApiModels.UserResponse;
import com.paysense.domain.Customer;
import com.paysense.repo.CustomerRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController @RequestMapping("/api/users")
public class UserController {
 public record UpdateProfile(@NotBlank @Size(min=2,max=80)String name,@Pattern(regexp="^$|^\\+?[6-9][0-9]{9}$",message="Enter a valid Indian mobile number.")String phone){}
 private final CustomerRepository customers;public UserController(CustomerRepository customers){this.customers=customers;}
 @GetMapping("/me") public UserResponse me(@AuthenticationPrincipal String id){return view(find(id));}
 @PutMapping("/me") public UserResponse update(@AuthenticationPrincipal String id,@Valid @RequestBody UpdateProfile request){Customer c=find(id);c.updateProfile(request.name().trim(),request.phone()==null||request.phone().isBlank()?null:request.phone().trim());return view(c);}
 private Customer find(String id){return customers.findById(UUID.fromString(id)).orElseThrow(()->new IllegalArgumentException("Account not found."));}
 private UserResponse view(Customer c){return new UserResponse(c.getId(),c.getName(),c.getEmail(),c.getPhone(),c.getCreatedAt());}
}
