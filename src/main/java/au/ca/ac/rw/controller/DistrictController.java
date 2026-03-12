package au.ca.ac.rw.controller;

import au.ca.ac.rw.dto.DistrictDTO;
import au.ca.ac.rw.dto.LocationRequest;
import au.ca.ac.rw.entity.Location;
import au.ca.ac.rw.entity.Location.LocationType;
import au.ca.ac.rw.service.LocationMapperService;
import au.ca.ac.rw.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/districts")
@CrossOrigin(origins = "*")
public class DistrictController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationMapperService mapperService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDistricts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        List<Location> districts = locationService.findByTypeWithChildren(LocationType.DISTRICT);

        // Manual pagination if service doesn't support it directly returning
        // Page<Entity>
        // But wait, the service could be improved.
        // For now, I'll use the service's findByType which returns a list.

        List<DistrictDTO> dtos = districts.stream()
                .map(mapperService::mapToDistrictDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "content", dtos,
                "totalItems", dtos.size()));
    }

    @PostMapping
    public ResponseEntity<DistrictDTO> createDistrict(@RequestBody LocationRequest request) {
        Location location = new Location();
        location.setType(LocationType.DISTRICT);
        location.setName(request.getName());
        location.setCode(request.getCode());

        if (request.getParentId() != null) {
            Location parent = new Location();
            parent.setId(request.getParentId());
            // Need code for validation in service
            location.setParent(parent);
        }

        Location saved = locationService.createLocation(location);
        return ResponseEntity.ok(mapperService.mapToDistrictDTO(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DistrictDTO> getDistrictById(@PathVariable Long id) {
        Location district = locationService.findByIdWithChildren(id)
                .orElseThrow(() -> new IllegalArgumentException("District not found with id: " + id));
        return ResponseEntity.ok(mapperService.mapToDistrictDTO(district));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DistrictDTO> updateDistrict(@PathVariable Long id,
            @RequestBody LocationRequest locationDetails) {
        Location location = new Location();
        location.setName(locationDetails.getName());
        location.setCode(locationDetails.getCode());

        if (locationDetails.getParentId() != null) {
            Location parent = new Location();
            parent.setId(locationDetails.getParentId());
            location.setParent(parent);
        }

        Location updated = locationService.updateLocation(id, location);
        return ResponseEntity.ok(mapperService.mapToDistrictDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDistrict(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
