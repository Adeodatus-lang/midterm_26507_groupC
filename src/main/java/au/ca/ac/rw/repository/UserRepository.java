package au.ca.ac.rw.repository;

import au.ca.ac.rw.entity.User;
import au.ca.ac.rw.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByEmail(String email);

        List<User> findByRole(String role);

        @Query("SELECT u FROM User u LEFT JOIN FETCH u.village WHERE u.id = :id")
        User findByIdWithVillage(@Param("id") Long id);

        @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.village WHERE u.id = :id")
        Optional<User> findByIdWithVillageOptional(@Param("id") Long id);

        @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.village")
        List<User> findAllWithVillage();

        @Query(value = "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.village", countQuery = "SELECT COUNT(u) FROM User u")
        org.springframework.data.domain.Page<User> findAllWithVillage(
                        org.springframework.data.domain.Pageable pageable);

        @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.village WHERE u.email = :email")
        Optional<User> findByEmailWithVillage(@Param("email") String email);

        @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.village WHERE u.role = :role")
        List<User> findByRoleWithVillage(@Param("role") String role);

        @Query(value = "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.village WHERE u.role = :role", countQuery = "SELECT COUNT(u) FROM User u WHERE u.role = :role")
        org.springframework.data.domain.Page<User> findByRoleWithVillage(@Param("role") String role,
                        org.springframework.data.domain.Pageable pageable);

        @Query("""
                        SELECT DISTINCT u FROM User u
                        LEFT JOIN FETCH u.village v
                        LEFT JOIN FETCH v.parent c
                        LEFT JOIN FETCH c.parent s
                        LEFT JOIN FETCH s.parent d
                        LEFT JOIN FETCH d.parent p
                        WHERE u.id = :id
                        """)
        User findByIdWithFullLocationHierarchy(@Param("id") Long id);

        @Query("""
                        SELECT DISTINCT u FROM User u
                        JOIN u.village v
                        JOIN v.parent c
                        JOIN c.parent s
                        JOIN s.parent d
                        JOIN d.parent p
                        WHERE p.name = :provinceName
                        AND v.type = au.ca.ac.rw.entity.Location$LocationType.VILLAGE
                        """)
        List<User> findByProvinceName(@Param("provinceName") String provinceName);

        @Query("""
                        SELECT DISTINCT u FROM User u
                        JOIN u.village v
                        JOIN v.parent c
                        JOIN c.parent s
                        JOIN s.parent d
                        JOIN d.parent p
                        WHERE p.code = :provinceCode
                        AND v.type = au.ca.ac.rw.entity.Location$LocationType.VILLAGE
                        """)
        List<User> findByProvinceCode(@Param("provinceCode") String provinceCode);

        @Query("""
                        SELECT DISTINCT u FROM User u
                        JOIN u.village v
                        JOIN v.parent c
                        JOIN c.parent s
                        JOIN s.parent d
                        JOIN d.parent p
                        WHERE (p.name = :query OR p.code = :query)
                        AND v.type = au.ca.ac.rw.entity.Location$LocationType.VILLAGE
                        """)
        List<User> findByProvinceNameOrCode(@Param("query") String query);

        @Query("""
                        SELECT p FROM User u
                        JOIN u.village v
                        JOIN v.parent c
                        JOIN c.parent s
                        JOIN s.parent d
                        JOIN d.parent p
                        WHERE u.id = :userId
                        AND p.type = au.ca.ac.rw.entity.Location$LocationType.PROVINCE
                        """)
        Optional<Location> findProvinceByUserId(@Param("userId") Long userId);
}
