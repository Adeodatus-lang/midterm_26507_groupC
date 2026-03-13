package au.ca.ac.rw.service;

import au.ca.ac.rw.dto.*;
import au.ca.ac.rw.entity.Location;
import au.ca.ac.rw.entity.Location.LocationType;
import au.ca.ac.rw.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapperService mapperService;

    public LocationService(LocationRepository locationRepository, LocationMapperService mapperService) {
        this.locationRepository = locationRepository;
        this.mapperService = mapperService;
    }

    @Transactional
    public List<Location> createBatch(List<Location> locations) {
        validateBatchLocations(locations);
        return locationRepository.saveAll(locations);
    }

    @Transactional(readOnly = true)
    public Map<LocationType, List<Location>> getLocationHierarchy(Long parentId) {
        List<Location> children = parentId != null ? locationRepository.findByParentId(parentId)
                : locationRepository.findByType(LocationType.PROVINCE);

        return children.stream().collect(Collectors.groupingBy(Location::getType));
    }

    @Transactional(readOnly = true)
    public List<Location> getChildrenByType(Long parentId, LocationType type) {
        if (parentId == null && type != LocationType.PROVINCE) {
            throw new IllegalArgumentException("Parent ID is required for non-province locations");
        }
        return locationRepository.findByTypeAndParentId(type, parentId);
    }

    @Transactional(readOnly = true)
    public Optional<Location> findByCodeAndType(String code, LocationType type) {
        return locationRepository.findByCodeAndType(code, type);
    }

    @Transactional(readOnly = true)
    public Page<Location> findByType(LocationType type, Pageable pageable) {
        return locationRepository.findByType(type, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Location> findByTypeAndParentId(LocationType type, Long parentId, Pageable pageable) {
        return locationRepository.findByTypeAndParent_Id(type, parentId, pageable);
    }

    @Transactional(readOnly = true)
    public boolean existsByCodeAndType(String code, LocationType type) {
        return locationRepository.existsByCodeAndType(code, type);
    }

    @Transactional
    public Location createLocation(Location location) {
        validateSingleLocation(location);
        return locationRepository.save(location);
    }

    @Transactional
    public Location updateLocation(Long id, Location locationDetails) {
        Location existingLocation = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + id));

        // Update fields
        if (locationDetails.getName() != null) {
            existingLocation.setName(locationDetails.getName());
        }
        if (locationDetails.getCode() != null) {
            existingLocation.setCode(locationDetails.getCode());
        }
        if (locationDetails.getParent() != null) {
            existingLocation.setParent(locationDetails.getParent());
        }

        validateSingleLocation(existingLocation);
        return locationRepository.save(existingLocation);
    }

    @Transactional
    public void deleteLocation(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + id));

        // Check if location has children
        List<Location> children = locationRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new IllegalStateException("Cannot delete location with children. Delete children first.");
        }

        locationRepository.delete(location);
    }

    @Transactional(readOnly = true)
    public Optional<Location> findByIdWithChildren(Long id) {
        return locationRepository.findByIdWithChildren(id);
    }

    @Transactional(readOnly = true)
    public List<Location> findByTypeWithChildren(LocationType type) {
        return locationRepository.findByTypeWithChildren(type);
    }

    @Transactional(readOnly = true)
    public boolean hasChildren(Long parentId) {
        return locationRepository.hasChildren(parentId);
    }

    @Transactional(readOnly = true)
    public boolean isCodeAndTypeUnique(String code, LocationType type, Long excludeId) {
        return locationRepository.isCodeAndTypeUnique(code, type, excludeId);
    }

    private void validateBatchLocations(List<Location> locations) {
        Map<String, Location> codeToLocation = locations.stream()
                .collect(Collectors.toMap(Location::getCode, l -> l));

        for (Location location : locations) {
            validateLocation(location, codeToLocation);
        }
    }

    private void validateSingleLocation(Location location) {
        validateLocation(location, Collections.emptyMap());
    }

    private void validateLocation(Location location, Map<String, Location> codeToLocation) {
        location.validateHierarchy();

        if (location.getParent() != null) {
            String parentCode = location.getParent().getCode();
            Location parent = codeToLocation.get(parentCode);

            if (parent == null) {
                parent = locationRepository.findFirstByCode(parentCode);
                if (parent == null) {
                    throw new IllegalStateException("Parent location with code " + parentCode
                            + " not found for " + location.getCode());
                }
            }

            validateParentChildTypes(parent.getType(), location.getType());
            location.setParent(parent);
        }
    }

    private void validateParentChildTypes(LocationType parentType, LocationType childType) {
        boolean valid = switch (childType) {
            case PROVINCE -> parentType == null;
            case DISTRICT -> parentType == LocationType.PROVINCE;
            case SECTOR -> parentType == LocationType.DISTRICT;
            case CELL -> parentType == LocationType.SECTOR;
            case VILLAGE -> parentType == LocationType.CELL;
        };

        if (!valid) {
            throw new IllegalStateException(
                    "Invalid parent-child relationship: " + parentType + " -> " + childType);
        }
    }

    @Transactional(readOnly = true)
    public Location getFullHierarchy(Long id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Location not found with id: " + id));

        loadChildrenRecursively(location);
        return location;
    }

    private void loadChildrenRecursively(Location location) {
        List<Location> children = locationRepository.findByParentId(location.getId());
        location.setChildren(children);
        for (Location child : children) {
            loadChildrenRecursively(child);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Location> findProvinceByVillageId(Long villageId) {
        return locationRepository.findProvinceByVillageId(villageId);
    }

    @Transactional(readOnly = true)
    public List<Location> searchByName(String name) {
        return locationRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional(readOnly = true)
    public List<Location> searchByNameAndType(String name, LocationType type) {
        return locationRepository.findByNameContainingIgnoreCaseAndType(name, type);
    }

    @Transactional(readOnly = true)
    public List<ProvinceDTO> findAllProvincesDTO() {
        return locationRepository.findByTypeAndParentIsNull(LocationType.PROVINCE).stream()
                .map(mapperService::mapToProvinceDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProvinceDTO getProvinceWithHierarchyDTO(Long id) {
        Location province = getFullHierarchy(id);
        if (province.getType() != LocationType.PROVINCE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location is not a province");
        }
        return mapperService.mapToProvinceDTO(province);
    }

    @Transactional(readOnly = true)
    public ProvinceDTO getProvinceByIdDTO(Long id) {
        Location province = locationRepository.findByIdWithChildren(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Province not found with id: " + id));

        if (province.getType() != LocationType.PROVINCE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location is not a province");
        }
        return mapperService.mapToProvinceDTO(province);
    }

    @Transactional(readOnly = true)
    public List<DistrictDTO> findAllDistrictsDTO() {
        return locationRepository.findByType(LocationType.DISTRICT).stream()
                .map(mapperService::mapToDistrictDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SectorDTO> findAllSectorsDTO() {
        return locationRepository.findByType(LocationType.SECTOR).stream()
                .map(mapperService::mapToSectorDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CellDTO> findAllCellsDTO() {
        return locationRepository.findByType(LocationType.CELL).stream()
                .map(mapperService::mapToCellDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VillageDTO> findAllVillagesDTO() {
        return locationRepository.findByType(LocationType.VILLAGE).stream()
                .map(mapperService::mapToVillageDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DistrictDTO> findDistrictsByProvinceDTO(Long provinceId) {
        return locationRepository.findByTypeAndParent_Id(LocationType.DISTRICT, provinceId).stream()
                .map(mapperService::mapToDistrictDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DistrictDTO getDistrictByIdDTO(Long id) {
        Location district = locationRepository.findByIdWithChildren(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "District not found with id: " + id));
        return mapperService.mapToDistrictDTO(district);
    }

    @Transactional(readOnly = true)
    public List<SectorDTO> findSectorsByDistrictDTO(Long districtId) {
        return locationRepository.findByTypeAndParent_Id(LocationType.SECTOR, districtId).stream()
                .map(mapperService::mapToSectorDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SectorDTO getSectorByIdDTO(Long id) {
        Location sector = locationRepository.findByIdWithChildren(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sector not found with id: " + id));
        return mapperService.mapToSectorDTO(sector);
    }

    @Transactional(readOnly = true)
    public List<CellDTO> findCellsBySectorDTO(Long sectorId) {
        return locationRepository.findByTypeAndParent_Id(LocationType.CELL, sectorId).stream()
                .map(mapperService::mapToCellDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CellDTO getCellByIdDTO(Long id) {
        Location cell = locationRepository.findByIdWithChildren(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cell not found with id: " + id));
        return mapperService.mapToCellDTO(cell);
    }

    @Transactional(readOnly = true)
    public List<VillageDTO> findVillagesByCellDTO(Long cellId) {
        return locationRepository.findByTypeAndParent_Id(LocationType.VILLAGE, cellId).stream()
                .map(mapperService::mapToVillageDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VillageDTO getVillageByIdDTO(Long id) {
        Location village = locationRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Village not found with id: " + id));
        return mapperService.mapToVillageDTO(village);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getLocationStatistics() {
        Map<String, Object> stats = new HashMap<>();

        for (LocationType type : LocationType.values()) {
            long count = locationRepository.findByType(type).size();
            stats.put(type.name().toLowerCase() + "Count", count);
        }

        return stats;
    }
}