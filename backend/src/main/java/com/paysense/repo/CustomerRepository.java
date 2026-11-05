package com.paysense.repo;
import com.paysense.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
public interface CustomerRepository extends JpaRepository<Customer,UUID>{Optional<Customer> findByEmailIgnoreCase(String email); boolean existsByEmailIgnoreCase(String email);}
