package ma.emsi.fetheddine.billingservice.repository;

import ma.emsi.fetheddine.billingservice.entities.ProductItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductItemRepository extends JpaRepository<ProductItem, String> {
}
