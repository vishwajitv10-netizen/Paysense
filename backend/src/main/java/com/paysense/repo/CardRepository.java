package com.paysense.repo;
import com.paysense.domain.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
public interface CardRepository extends JpaRepository<PaymentCard,UUID>{List<PaymentCard> findByWalletId(UUID walletId);Optional<PaymentCard> findByIdAndWalletId(UUID id,UUID walletId);}
