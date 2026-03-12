package au.ca.ac.rw.repository;

import au.ca.ac.rw.entity.Location;
import au.ca.ac.rw.entity.Location.LocationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByType(LocationType type);

    Page<Location> findByType(LocationType type, Pageable pageable);

    List<Location> findByParent_Id(Long parentId);

    Page<Location> findByParent_Id(Long parentId, Pageable pageable);

    default List<Location> findByParentId(Long parentId) {
        return findByParent_Id(parentId);
    }

    List<Location> findByTypeAndParent_Id(LocationType type, Long parentId);

    Page<Location> findByTypeAndParent_Id(LocationType type, Long parentId, Pageable pageable);

    default List<Location> findByTypeAndParentId(LocationType type, Long parentId) {
        return findByTypeAndParent_Id(type, parentId);
    }

    Location findFirstByTypeAndCode(LocationType type, String code);

    Location findFirstByCode(String code);

    @Query("SELECT l FROM Location l WHERE l.code = :code AND l.type = :type")
    Optional<Location> findByCodeAndType(@Param("code") String code, @Param("type") LocationType type);

    @Query("SELECT COUNT(l) > 0 FROM Location l WHERE l.code = :code AND l.type = :type")
    boolean existsByCodeAndType(@Param("code") String code, @Param("type") LocationType type);

    @Query("SELECT l FROM Location l WHERE l.code IN :codes")
    List<Location> findByCodes(@Param("codes") List<String> codes);

    @Query("SELECT l FROM Location l WHERE l.type = :type AND l.code IN :codes")
    List<Location> findByTypeAndCodes(@Param("type") LocationType type, @Param("codes") List<String> codes);

    @Query("SELECT l FROM Location l WHERE l.parent.id = :parentId")
    List<Location> findChildren(@Param("parentId") Long parentId);

    @Query("SELECT l FROM Location l WHERE l.type = :type AND l.parent.id = :parentId")
    List<Location> findChildrenByType(@Param("type") LocationType type, @Param("parentId") Long parentId);

    @Query("SELECT DISTINCT l FROM Location l LEFT JOIN FETCH l.children WHERE l.type = :type")
    List<Location> findByTypeWithChildren(@Param("type") LocationType type);

    @Query("SELECT l FROM Location l LEFT JOIN FETCH l.children WHERE l.id = :id")
    Optional<Location> findByIdWithChildren(@Param("id") Long id);

    List<Location> findByTypeAndParentIsNull(LocationType type);

    @Query("SELECT COUNT(l) > 0 FROM Location l WHERE l.parent.id = :parentId")
    boolean hasChildren(@Param("parentId") Long parentId);

    List<Location> findByTypeIn(List<LocationType> types);

    List<Location> findByNameContainingIgnoreCase(String name);

    List<Location> findByNameContainingIgnoreCaseAndType(String name, LocationType type);

    @Query("SELECT COUNT(l) = 0 FROM Location l WHERE l.code = :code AND l.type = :type AND l.id <> :excludeId")
    boolean isCodeAndTypeUnique(@Param("code") String code, @Param("type") LocationType type,
            @Param("excludeId") Long excludeId);

    @Query("SELECT l FROM Location l WHERE l.type = 'PROVINCE' AND l.id IN (" +
            "SELECT d.parent.id FROM Location d WHERE d.type = 'DISTRICT' AND d.id IN (" +
            "SELECT s.parent.id FROM Location s WHERE s.type = 'SECTOR' AND s.id IN (" +
            "SELECT c.parent.id FROM Location c WHERE c.type = 'CELL' AND c.id IN (" +
            "SELECT v.parent.id FROM Location v WHERE v.type = 'VILLAGE' AND v.id = :villageId))))")
    Optional<Location> findProvinceByVillageId(@Param("villageId") Long villageId);
}