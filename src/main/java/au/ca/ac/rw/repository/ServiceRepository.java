package au.ca.ac.rw.repository;

import au.ca.ac.rw.entity.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    /**
     * Find all active services with pagination
     * @param pageable pagination information
     * @return page of active services
     */
    Page<Service> findByIsActiveTrue(Pageable pageable);
    
    /**
     * Find all active services without pagination
     * @return list of active services
     */
    List<Service> findByIsActiveTrue();

    List<Service> findByNameContainingIgnoreCase(String name);

    boolean existsByName(String name);

    Page<Service> findAllByOrderByNameAsc(Pageable pageable);

    @Query("SELECT DISTINCT s FROM Service s LEFT JOIN FETCH s.barbers WHERE s.id = :id")
    Optional<Service> findByIdWithBarbers(@Param("id") Long id);

    @Query("SELECT DISTINCT s FROM Service s LEFT JOIN FETCH s.barbers")
    List<Service> findAllWithBarbers();
}