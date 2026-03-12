package au.ca.ac.rw.repository;

import au.ca.ac.rw.entity.Barber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

@Repository
public interface BarberRepository extends JpaRepository<Barber, Long> {

    @Query("SELECT b FROM Barber b JOIN FETCH b.user WHERE b.user.id = :userId")
    Optional<Barber> findByUserId(@Param("userId") Long userId);

    @Query("SELECT b FROM Barber b JOIN FETCH b.user WHERE b.isAvailable = true")
    List<Barber> findByIsAvailableTrue();

    @Query("SELECT b FROM Barber b JOIN FETCH b.user WHERE LOWER(b.specialization) LIKE LOWER(CONCAT('%', :specialization, '%'))")
    List<Barber> findBySpecializationContainingIgnoreCase(@Param("specialization") String specialization);

    boolean existsByLicenseNumber(String licenseNumber);
    
    boolean existsByUserId(Long userId);

    @Query("SELECT b FROM Barber b JOIN FETCH b.user ORDER BY b.rating DESC")
    Page<Barber> findAllWithUserOrderByRatingDesc(Pageable pageable);

    @Query("SELECT b FROM Barber b JOIN FETCH b.user ORDER BY b.yearsOfExperience DESC")
    List<Barber> findAllWithUserOrderByYearsOfExperienceDesc();

    @Query("SELECT b FROM Barber b JOIN FETCH b.user JOIN FETCH b.user.village WHERE b.user.village.parent.parent.parent.parent.id = :provinceId AND b.user.village.type = 'VILLAGE'")
    List<Barber> findAvailableBarbersByProvinceId(@Param("provinceId") Long provinceId);

    @Query("SELECT b FROM Barber b JOIN FETCH b.user JOIN FETCH b.user.village WHERE b.user.village.id = :villageId AND b.isAvailable = true AND b.user.village.type = 'VILLAGE'")
    List<Barber> findAvailableBarbersByVillageId(@Param("villageId") Long villageId);

    @Query("SELECT b FROM Barber b JOIN FETCH b.user WHERE b.id = :id")
    Optional<Barber> findByIdWithUser(@Param("id") Long id);

    @Query("SELECT b FROM Barber b JOIN FETCH b.user")
    List<Barber> findAllWithUser();

    @Query("SELECT DISTINCT b FROM Barber b LEFT JOIN FETCH b.services WHERE b.id = :id")
    Optional<Barber> findByIdWithServices(@Param("id") Long id);

    @Query("SELECT DISTINCT b FROM Barber b LEFT JOIN FETCH b.services")
    List<Barber> findAllWithServices();

    @Query("SELECT DISTINCT b FROM Barber b LEFT JOIN FETCH b.services WHERE b.isAvailable = true")
    List<Barber> findAvailableWithServices();

    @Query("SELECT DISTINCT b FROM Barber b " +
           "LEFT JOIN FETCH b.user " +
           "LEFT JOIN FETCH b.user.village " +
           "LEFT JOIN FETCH b.services " +
           "WHERE b.id = :id")
    Optional<Barber> findByIdWithDetails(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Barber b JOIN FETCH b.user WHERE b.id = :id")
    Optional<Barber> findByIdWithUserForUpdate(@Param("id") Long id);

    @Query("SELECT b FROM Barber b JOIN FETCH b.user")
    Page<Barber> findAllWithUser(Pageable pageable);

    @Query("SELECT b FROM Barber b JOIN FETCH b.user JOIN FETCH b.user.village WHERE b.isAvailable = true")
    List<Barber> findAvailableBarbersWithDetails();
}