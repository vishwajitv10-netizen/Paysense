package com.paysense.repo;
import com.paysense.domain.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;
import java.util.UUID;
public interface WalletRepository extends JpaRepository<Wallet,UUID>{Optional<Wallet> findByCustomerId(UUID customerId); @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select w from Wallet w where w.customer.id=:customerId") Optional<Wallet> findLockedByCustomerId(@Param("customerId") UUID customerId);}
