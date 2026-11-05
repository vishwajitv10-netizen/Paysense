package com.paysense.repo;
import com.paysense.domain.PaymentTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
public interface TransactionRepository extends JpaRepository<PaymentTransaction,UUID>{
 @Query("select t from PaymentTransaction t where t.wallet.id=:walletId and (:query='' or lower(t.name) like lower(concat('%',:query,'%')) or lower(t.category) like lower(concat('%',:query,'%')) or lower(t.type) like lower(concat('%',:query,'%')))") Page<PaymentTransaction> search(@Param("walletId") UUID walletId,@Param("query") String query,Pageable pageable);
 Optional<PaymentTransaction> findByWalletIdAndIdempotencyKey(UUID walletId,String key);
 Optional<PaymentTransaction> findByIdAndWalletId(UUID id,UUID walletId);
 long countByWalletId(UUID walletId);
 @Query("select coalesce(sum(t.amount),0) from PaymentTransaction t where t.wallet.id=:walletId and t.amount>0 and t.createdAt>=:since") java.math.BigDecimal incomeSince(@Param("walletId") UUID walletId,@Param("since") Instant since);
 @Query("select coalesce(sum(t.amount),0) from PaymentTransaction t where t.wallet.id=:walletId and t.amount<0 and t.createdAt>=:since") java.math.BigDecimal spendingSince(@Param("walletId") UUID walletId,@Param("since") Instant since);
}
