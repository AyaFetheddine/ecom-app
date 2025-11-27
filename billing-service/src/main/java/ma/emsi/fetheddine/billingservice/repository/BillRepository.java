package ma.emsi.fetheddine.billingservice.repository;

import ma.emsi.fetheddine.billingservice.entities.Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BillRepository extends JpaRepository<Bill, Long> {
}
