package au.ca.ac.rw.controller;

import au.ca.ac.rw.entity.Appointment;
import au.ca.ac.rw.entity.Barber;
import au.ca.ac.rw.entity.Customer;
import au.ca.ac.rw.entity.Service;
import au.ca.ac.rw.enums.AppointmentStatus;
import au.ca.ac.rw.enums.EnumValidationUtils;
import au.ca.ac.rw.repository.AppointmentRepository;
import au.ca.ac.rw.repository.BarberRepository;
import au.ca.ac.rw.repository.CustomerRepository;
import au.ca.ac.rw.repository.ServiceRepository;
import au.ca.ac.rw.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Date;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import au.ca.ac.rw.dto.AppointmentResponseDTO;
import au.ca.ac.rw.dto.AppointmentRequest;
import au.ca.ac.rw.dto.UpdateStatusRequest;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentRequest request) {
        try {
            AppointmentResponseDTO dto = appointmentService.createAppointment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating appointment: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                    : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<AppointmentResponseDTO> appointmentPage = appointmentService.getAllAppointments(pageable);

            return ResponseEntity.ok()
                    .body(Map.of(
                            "content", appointmentPage.getContent(),
                            "currentPage", appointmentPage.getNumber(),
                            "totalItems", appointmentPage.getTotalElements(),
                            "totalPages", appointmentPage.getTotalPages()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving appointments: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long id) {
        try {
            AppointmentResponseDTO dto = appointmentService.getAppointmentById(id);
            return ResponseEntity.ok(dto);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving appointment: " + e.getMessage());
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<?> getAppointmentsByCustomer(@PathVariable Long customerId) {
        try {
            List<AppointmentResponseDTO> dtos = appointmentService.getAppointmentsByCustomer(customerId);
            return ResponseEntity.ok(Map.of(
                    "appointments", dtos,
                    "count", dtos.size()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving customer appointments: " + e.getMessage());
        }
    }

    @GetMapping("/barber/{barberId}")
    public ResponseEntity<?> getAppointmentsByBarber(@PathVariable Long barberId) {
        try {
            List<AppointmentResponseDTO> dtos = appointmentService.getAppointmentsByBarber(barberId);
            return ResponseEntity.ok(Map.of(
                    "appointments", dtos,
                    "count", dtos.size()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving barber appointments: " + e.getMessage());
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getAppointmentsByStatus(@PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<AppointmentResponseDTO> appointmentPage = appointmentService.getAppointmentsByStatus(status, pageable);

            return ResponseEntity.ok()
                    .body(Map.of(
                            "content", appointmentPage.getContent(),
                            "currentPage", appointmentPage.getNumber(),
                            "totalItems", appointmentPage.getTotalElements(),
                            "totalPages", appointmentPage.getTotalPages()));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving appointments by status: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateAppointmentStatus(@PathVariable Long id,
            @RequestBody UpdateStatusRequest request) {
        try {
            AppointmentResponseDTO dto = appointmentService.updateAppointmentStatus(id, request);
            return ResponseEntity.ok(dto);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating appointment status: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long id,
            @RequestBody AppointmentRequest request) {
        try {
            AppointmentResponseDTO dto = appointmentService.updateAppointment(id, request);
            return ResponseEntity.ok(dto);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating appointment: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        try {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting appointment: " + e.getMessage());
        }
    }
}