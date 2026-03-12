package au.ca.ac.rw.controller;

import au.ca.ac.rw.dto.ServiceRequest;
import au.ca.ac.rw.dto.ServiceResponseDTO;
import au.ca.ac.rw.dto.ServiceSummaryDTO;
import au.ca.ac.rw.service.ServiceItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*")
public class ServiceController {

    @Autowired
    private ServiceItemService serviceItemService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> listServices(
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<ServiceResponseDTO> servicePage = serviceItemService.getAllServices(active, pageable);

        return ResponseEntity.ok(Map.of(
                "content", servicePage.getContent(),
                "currentPage", servicePage.getNumber(),
                "totalItems", servicePage.getTotalElements(),
                "totalPages", servicePage.getTotalPages()));
    }

    @GetMapping("/summaries")
    public ResponseEntity<List<ServiceSummaryDTO>> getServiceSummaries(
            @RequestParam(value = "active", required = false) Boolean active) {
        return ResponseEntity.ok(serviceItemService.getAllServiceSummaries(active));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponseDTO> getService(@PathVariable Long id) {
        return ResponseEntity.ok(serviceItemService.getServiceById(id));
    }

    @PostMapping
    public ResponseEntity<ServiceResponseDTO> createService(@RequestBody ServiceRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviceItemService.createService(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServiceResponseDTO> updateService(@PathVariable Long id, @RequestBody ServiceRequest req) {
        return ResponseEntity.ok(serviceItemService.updateService(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        serviceItemService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
