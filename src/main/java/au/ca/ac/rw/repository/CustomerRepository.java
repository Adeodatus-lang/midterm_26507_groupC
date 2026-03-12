package au.ca.ac.rw.repository;

import au.ca.ac.rw.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Find customer by user ID
    @Query("SELECT c FROM Customer c WHERE c.user.id = :userId")
    Optional<Customer> findByUserId(@Param("userId") Long userId);

    // Find customers with loyalty points greater than or equal to specified points
    List<Customer> findByLoyaltyPointsGreaterThanEqual(Integer points);

    // Paginated version
    Page<Customer> findByLoyaltyPointsGreaterThanEqual(Integer points, Pageable pageable);

    // Optional: You can also use derived query method without @Query
    // Optional<Customer> findByUser_Id(Long userId);
}
