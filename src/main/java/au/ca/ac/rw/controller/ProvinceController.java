package au.ca.ac.rw.controller;

import au.ca.ac.rw.dto.ProvinceDTO;
import au.ca.ac.rw.dto.LocationRequest;
import au.ca.ac.rw.entity.Location;
import au.ca.ac.rw.entity.Location.LocationType;
import au.ca.ac.rw.service.LocationMapperService;
import au.ca.ac.rw.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/provinces")
@CrossOrigin(origins = "*")
public class ProvinceController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationMapperService mapperService;

    @GetMapping
    public ResponseEntity<List<ProvinceDTO>> getAllProvinces() {
        return ResponseEntity.ok(locationService.findAllProvincesDTO());
    }

    @PostMapping
    public ResponseEntity<ProvinceDTO> createProvince(@RequestBody LocationRequest request) {
        Location location = new Location();
        location.setType(LocationType.PROVINCE);
        location.setName(request.getName());
        location.setCode(request.getCode());

        Location saved = locationService.createLocation(location);
        return ResponseEntity.ok(locationService.getProvinceByIdDTO(saved.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProvinceDTO> getProvinceById(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getProvinceByIdDTO(id));
    }

    @GetMapping("/{id}/with-hierarchy")
    public ResponseEntity<ProvinceDTO> getProvinceWithHierarchy(@PathVariable Long id) {
        return ResponseEntity.ok(locationService.getProvinceWithHierarchyDTO(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProvinceDTO> updateProvince(@PathVariable Long id,
            @RequestBody LocationRequest locationDetails) {
        Location location = new Location();
        location.setName(locationDetails.getName());
        location.setCode(locationDetails.getCode());

        Location updated = locationService.updateLocation(id, location);
        return ResponseEntity.ok(locationService.getProvinceByIdDTO(updated.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvince(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
