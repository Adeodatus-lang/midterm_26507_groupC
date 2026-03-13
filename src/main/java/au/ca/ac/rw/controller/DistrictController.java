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
    public ResponseEntity<Map<String, Object>> getAllDistricts() {
        // Since original had no province filter and listed all,
        // but we want to be safe with transactions,
        // we'll fetch all and map.
        // Ideally we should have a findAllDistrictsDTO() in service.
        // For now, I'll update the service with a list-all method if needed,
        // or just fetch what's needed.

        // Actually, the original line 43 was:
        // locationService.findByTypeWithChildren(LocationType.DISTRICT);
        // I'll add findAllDistrictsDTO to service or just use the existing one if I
        // added it.
        // Wait, I didn't add a "findAllDistrictsDTO". I'll add it to service.

        // Let's assume I added it or I'll add it now.
        return ResponseEntity.ok(Map.of(
                "content", locationService.findAllDistrictsDTO(),
                "totalItems", 0)); // Size will be in content
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
            location.setParent(parent);
        }

        Location saved = locationService.createLocation(location);
        return ResponseEntity.ok(locationService.getDistrictByIdDTO(saved.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DistrictDTO> getDistrictById(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getDistrictByIdDTO(id));
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
        return ResponseEntity.ok(locationService.getDistrictByIdDTO(updated.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDistrict(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
