package au.ca.ac.rw.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import au.ca.ac.rw.dto.BarberRequest;
import au.ca.ac.rw.dto.BarberResponseDTO;
import au.ca.ac.rw.dto.ServiceSummaryDTO;
import au.ca.ac.rw.entity.Barber;
import au.ca.ac.rw.entity.User;
import au.ca.ac.rw.repository.BarberRepository;
import au.ca.ac.rw.repository.UserRepository;
import au.ca.ac.rw.service.BarberService;

@RestController
@RequestMapping("/api/barbers")
public class BarberController {

    @Autowired
    private BarberService barberService;

    @PostMapping
    public ResponseEntity<?> createBarber(@RequestBody BarberRequest request) {
        try {
            BarberResponseDTO dto = barberService.createBarber(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllBarbers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<BarberResponseDTO> barberPage = barberService.getAllBarbers(pageable);

            return ResponseEntity.ok()
                    .body(Map.of(
                            "content", barberPage.getContent(),
                            "currentPage", barberPage.getNumber(),
                            "totalItems", barberPage.getTotalElements(),
                            "totalPages", barberPage.getTotalPages()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving barbers: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBarberById(@PathVariable Long id) {
        try {
            BarberResponseDTO dto = barberService.getBarberById(id);
            return ResponseEntity.ok(dto);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving barber: " + e.getMessage());
        }
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableBarbers() {
        try {
            List<BarberResponseDTO> dtos = barberService.getAvailableBarbers();
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving available barbers: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBarber(@PathVariable Long id,
            @RequestBody BarberRequest request) {
        try {
            BarberResponseDTO dto = barberService.updateBarber(id, request);
            return ResponseEntity.ok(dto);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBarber(@PathVariable Long id) {
        try {
            barberService.deleteBarber(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
