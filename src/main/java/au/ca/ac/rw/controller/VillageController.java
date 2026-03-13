package au.ca.ac.rw.controller;

import au.ca.ac.rw.dto.VillageDTO;
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
@RequestMapping("/api/villages")
@CrossOrigin(origins = "*")
public class VillageController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationMapperService mapperService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllVillages() {
        return ResponseEntity.ok(Map.of(
                "content", locationService.findAllVillagesDTO(),
                "totalItems", 0));
    }

    @PostMapping
    public ResponseEntity<VillageDTO> createVillage(@RequestBody LocationRequest request) {
        Location location = new Location();
        location.setType(LocationType.VILLAGE);
        location.setName(request.getName());
        location.setCode(request.getCode());

        if (request.getParentId() != null) {
            Location parent = new Location();
            parent.setId(request.getParentId());
            location.setParent(parent);
        }

        Location saved = locationService.createLocation(location);
        return ResponseEntity.ok(locationService.getVillageByIdDTO(saved.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VillageDTO> getVillageById(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getVillageByIdDTO(id));
    }

    @GetMapping("/cell/{cellId}")
    public ResponseEntity<Map<String, Object>> getVillagesByCell(@PathVariable Long cellId) {
        List<VillageDTO> dtos = locationService.findVillagesByCellDTO(cellId);
        return ResponseEntity.ok(Map.of(
                "content", dtos,
                "totalItems", dtos.size()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VillageDTO> updateVillage(@PathVariable Long id,
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
        return ResponseEntity.ok(locationService.getVillageByIdDTO(updated.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVillage(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
