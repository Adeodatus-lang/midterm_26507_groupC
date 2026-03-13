package au.ca.ac.rw.controller;

import au.ca.ac.rw.dto.CellDTO;
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
@RequestMapping("/api/cells")
@CrossOrigin(origins = "*")
public class CellController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationMapperService mapperService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCells() {
        return ResponseEntity.ok(Map.of(
                "content", locationService.findAllCellsDTO(),
                "totalItems", 0));
    }

    @PostMapping
    public ResponseEntity<CellDTO> createCell(@RequestBody LocationRequest request) {
        Location location = new Location();
        location.setType(LocationType.CELL);
        location.setName(request.getName());
        location.setCode(request.getCode());

        if (request.getParentId() != null) {
            Location parent = new Location();
            parent.setId(request.getParentId());
            location.setParent(parent);
        }

        Location saved = locationService.createLocation(location);
        return ResponseEntity.ok(locationService.getCellByIdDTO(saved.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CellDTO> getCellById(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getCellByIdDTO(id));
    }

    @GetMapping("/sector/{sectorId}")
    public ResponseEntity<Map<String, Object>> getCellsBySector(@PathVariable Long sectorId) {
        List<CellDTO> dtos = locationService.findCellsBySectorDTO(sectorId);
        return ResponseEntity.ok(Map.of(
                "content", dtos,
                "totalItems", dtos.size()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CellDTO> updateCell(@PathVariable Long id,
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
        return ResponseEntity.ok(locationService.getCellByIdDTO(updated.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCell(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
