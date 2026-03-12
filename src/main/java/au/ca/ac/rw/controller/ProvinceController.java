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
        List<Location> provinces = locationService.findAllProvinces();
        return ResponseEntity.ok(provinces.stream()
                .map(mapperService::mapToProvinceDTO)
                .collect(Collectors.toList()));
    }

    @PostMapping
    public ResponseEntity<ProvinceDTO> createProvince(@RequestBody LocationRequest request) {
        Location location = new Location();
        location.setType(LocationType.PROVINCE);
        location.setName(request.getName());
        location.setCode(request.getCode());

        Location saved = locationService.createLocation(location);
        return ResponseEntity.ok(mapperService.mapToProvinceDTO(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProvinceDTO> getProvinceById(@PathVariable Long id) {
        Location province = locationService.findByIdWithChildren(id)
                .orElseThrow(() -> new IllegalArgumentException("Province not found with id: " + id));

        if (province.getType() != LocationType.PROVINCE) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapperService.mapToProvinceDTO(province));
    }

    @GetMapping("/{id}/with-hierarchy")
    public ResponseEntity<ProvinceDTO> getProvinceWithHierarchy(@PathVariable Long id) {
        Location province = locationService.getFullHierarchy(id);
        if (province.getType() != LocationType.PROVINCE) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(mapperService.mapToProvinceDTO(province));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProvinceDTO> updateProvince(@PathVariable Long id,
            @RequestBody LocationRequest locationDetails) {
        Location location = new Location();
        location.setName(locationDetails.getName());
        location.setCode(locationDetails.getCode());

        Location updated = locationService.updateLocation(id, location);
        return ResponseEntity.ok(mapperService.mapToProvinceDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvince(@PathVariable Long id) {
        locationService.deleteLocation(id);
        return ResponseEntity.noContent().build();
    }
}
