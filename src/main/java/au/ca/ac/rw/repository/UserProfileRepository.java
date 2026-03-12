package au.ca.ac.rw.repository;

import au.ca.ac.rw.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    // Find by user ID
    Optional<UserProfile> findByUserId(Long userId);

    // Find by name patterns
    List<UserProfile> findByFirstNameContainingIgnoreCase(String firstName);

    List<UserProfile> findByLastNameContainingIgnoreCase(String lastName);

    List<UserProfile> findByFirstNameAndLastName(String firstName, String lastName);

    // Find by gender
    List<UserProfile> findByGender(String gender);

    // EXISTS BY queries
    boolean existsByUserId(Long userId);

    // Sorting and Pagination
    Page<UserProfile> findAllByOrderByFirstNameAsc(Pageable pageable);

    Page<UserProfile> findAllByOrderByLastNameAsc(Pageable pageable);

    List<UserProfile> findAllByOrderByDateOfBirthDesc();

    // Complex queries with JOIN
    @Query("SELECT up FROM UserProfile up WHERE up.user.email = :email")
    Optional<UserProfile> findByUserEmail(@Param("email") String email);

    @Query("SELECT up FROM UserProfile up WHERE up.user.role = :role")
    List<UserProfile> findByUserRole(@Param("role") String role);

    // Find all user profiles by user's village id (now Location type=VILLAGE)
    @Query("SELECT up FROM UserProfile up WHERE up.user.village.id = :villageId AND up.user.village.type = 'VILLAGE'")
    List<UserProfile> findByVillageId(@Param("villageId") Long villageId);

    // Search by bio content
    @Query("SELECT up FROM UserProfile up WHERE LOWER(up.bio) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<UserProfile> findByBioContaining(@Param("keyword") String keyword);

    // Find profiles with profile pictures
    List<UserProfile> findByProfilePictureIsNotNull();

    // Count queries
    long countByGender(String gender);

    @Query("SELECT COUNT(up) FROM UserProfile up WHERE up.user.role = :role")
    long countByUserRole(@Param("role") String role);
}
