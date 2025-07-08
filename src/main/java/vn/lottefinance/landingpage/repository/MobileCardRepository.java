package vn.lottefinance.landingpage.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.lottefinance.landingpage.domain.MobileCard;

@Repository
public interface MobileCardRepository extends JpaRepository<MobileCard, Long> {
    @Query(value = """
            SELECT * FROM (
                SELECT * FROM mobile_card
                WHERE status = :status AND brand = :brand AND price = :price
                ORDER BY id
            )
            WHERE ROWNUM = 1
            """, nativeQuery = true)
    MobileCard findFirstByStatusAndBrandAndPrice(
            @Param("status") String status,
            @Param("brand") String brand,
            @Param("price") String price
    );


}
