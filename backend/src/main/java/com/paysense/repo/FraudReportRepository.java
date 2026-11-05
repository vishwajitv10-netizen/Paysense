package com.paysense.repo;
import com.paysense.domain.FraudReport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface FraudReportRepository extends JpaRepository<FraudReport,UUID>{List<FraudReport> findTop100ByReporterIdOrderByCreatedAtDesc(UUID reporterId);long countByStatus(String status);}
