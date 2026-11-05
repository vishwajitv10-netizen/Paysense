package com.paysense.repo;
import com.paysense.domain.LedgerEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface LedgerRepository extends JpaRepository<LedgerEntry,UUID>{}
