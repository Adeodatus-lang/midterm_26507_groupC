package au.ca.ac.rw.service;

import au.ca.ac.rw.dto.AppointmentRequest;
import au.ca.ac.rw.dto.AppointmentResponseDTO;
import au.ca.ac.rw.dto.UpdateStatusRequest;
import au.ca.ac.rw.entity.Appointment;
import au.ca.ac.rw.entity.Barber;
import au.ca.ac.rw.entity.Customer;
import au.ca.ac.rw.enums.AppointmentStatus;
import au.ca.ac.rw.enums.EnumValidationUtils;
import au.ca.ac.rw.repository.AppointmentRepository;
import au.ca.ac.rw.repository.BarberRepository;
import au.ca.ac.rw.repository.CustomerRepository;
import au.ca.ac.rw.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private BarberRepository barberRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    private static final LocalTime BUSINESS_START = LocalTime.of(9, 0);
    private static final LocalTime BUSINESS_END = LocalTime.of(18, 0);
    private static final int MAX_DURATION_MINUTES = 240;

    @Transactional
    public AppointmentResponseDTO createAppointment(AppointmentRequest request) {
        Barber barber = barberRepository.findByIdWithUserForUpdate(request.getBarberId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Barber not found with ID: " + request.getBarberId()));

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer not found with ID: " + request.getCustomerId()));

        au.ca.ac.rw.entity.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Service not found with ID: " + request.getServiceId()));

        Date appointmentStartDate = request.getAppointmentDate();
        if (appointmentStartDate == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment date is required");
        }

        if (appointmentStartDate.before(new Date())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot schedule appointments in the past");
        }

        Integer serviceDuration = service.getDurationMinutes();
        if (serviceDuration == null || serviceDuration <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid service duration");
        }

        if (serviceDuration > MAX_DURATION_MINUTES) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Service duration cannot exceed " + MAX_DURATION_MINUTES + " minutes");
        }

        LocalTime appointmentTime = appointmentStartDate.toInstant()
                .atZone(ZoneId.systemDefault()).toLocalTime();

        if (appointmentTime.isBefore(BUSINESS_START)
                || appointmentTime.isBefore(LocalTime.MIN.plus(1, ChronoUnit.MILLIS))) {
        }

        if (appointmentTime.isBefore(BUSINESS_START) || appointmentTime.isAfter(BUSINESS_END)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Appointments must be scheduled within business hours (between " + BUSINESS_START + " and "
                            + BUSINESS_END + ")");
        }

        LocalTime appointmentEndTime = appointmentTime.plus(serviceDuration, ChronoUnit.MINUTES);
        if (appointmentEndTime.isAfter(BUSINESS_END)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Appointment duration exceeds business hours. Latest start time for this service is "
                            + BUSINESS_END.minusMinutes(serviceDuration));
        }

        Date appointmentEndDate = Date.from(appointmentStartDate.toInstant().plus(Duration.ofMinutes(serviceDuration)));

        List<Appointment> conflictingAppointments = appointmentRepository
                .findOverlappingAppointments(request.getBarberId(), appointmentStartDate, appointmentEndDate);
        if (!conflictingAppointments.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "This time slot overlaps with existing appointments");
        }

        if (barber.getIsAvailable() != null && !barber.getIsAvailable()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Barber is not available for appointments");
        }

        Appointment appointment = new Appointment();
        appointment.setBarber(barber);
        appointment.setCustomer(customer);
        appointment.setAppointmentDate(appointmentStartDate);
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setService(service);
        appointment.setNotes(request.getNotes());
        appointment.setEndDate(appointmentEndDate);
        appointment.setPrice(service.getPrice());

        AppointmentStatus status = AppointmentStatus.PENDING;
        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            try {
                status = AppointmentStatus.fromString(request.getStatus());
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + e.getMessage());
            }
        }
        appointment.setStatus(status);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return convertToDTO(savedAppointment);
    }

    public Page<AppointmentResponseDTO> getAllAppointments(Pageable pageable) {
        return appointmentRepository.findAll(pageable).map(this::convertToDTO);
    }

    public AppointmentResponseDTO getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Appointment not found with ID: " + id));
    }

    public List<AppointmentResponseDTO> getAppointmentsByCustomer(Long customerId) {
        return appointmentRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponseDTO> getAppointmentsByBarber(Long barberId) {
        return appointmentRepository.findByBarberId(barberId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<AppointmentResponseDTO> getAppointmentsByStatus(String statusStr, Pageable pageable) {
        if (!EnumValidationUtils.isValidAppointmentStatus(statusStr)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid status. Valid values: " + String.join(", ", AppointmentStatus.getValidStatuses()));
        }

        AppointmentStatus status = AppointmentStatus.fromString(statusStr);
        List<Appointment> allWithStatus = appointmentRepository.findByStatus(status);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allWithStatus.size());

        if (start > allWithStatus.size()) {
            return new PageImpl<>(List.of(), pageable, allWithStatus.size());
        }

        List<AppointmentResponseDTO> content = allWithStatus.subList(start, end).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, allWithStatus.size());
    }

    @Transactional
    public AppointmentResponseDTO updateAppointmentStatus(Long id, UpdateStatusRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Appointment not found with ID: " + id));

        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status is required");
        }

        AppointmentStatus newStatus;
        try {
            newStatus = AppointmentStatus.fromString(request.getStatus());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status: " + e.getMessage());
        }

        if (!EnumValidationUtils.isValidStatusTransition(appointment.getStatus(), newStatus)) {
            AppointmentStatus[] validNextStatuses = EnumValidationUtils.getValidNextStatuses(appointment.getStatus());
            String validStatuses = validNextStatuses.length > 0
                    ? java.util.Arrays.stream(validNextStatuses).map(Enum::name).collect(Collectors.joining(", "))
                    : "No valid status transitions available";

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid status transition from "
                    + appointment.getStatus() + " to " + newStatus + ". Valid next statuses: " + validStatuses);
        }

        appointment.setStatus(newStatus);
        return convertToDTO(appointmentRepository.save(appointment));
    }

    @Transactional
    public AppointmentResponseDTO updateAppointment(Long id, AppointmentRequest request) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Appointment not found with ID: " + id));

        if (!appointment.getStatus().canBeModified()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot update appointment with status: " + appointment.getStatus());
        }

        if (request.getAppointmentDate() != null) {
            appointment.setAppointmentDate(request.getAppointmentDate());
        }
        if (request.getAppointmentTime() != null) {
            appointment.setAppointmentTime(request.getAppointmentTime());
        }
        if (request.getNotes() != null) {
            appointment.setNotes(request.getNotes());
        }

        return convertToDTO(appointmentRepository.save(appointment));
    }

    @Transactional
    public void deleteAppointment(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Appointment not found with ID: " + id));

        if (!appointment.getStatus().canBeModified()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot delete appointment with status: " + appointment.getStatus());
        }

        appointmentRepository.delete(appointment);
    }

    private AppointmentResponseDTO convertToDTO(Appointment a) {
        if (a == null)
            return null;
        Long barberId = a.getBarber() != null ? a.getBarber().getId() : null;
        Long customerId = a.getCustomer() != null ? a.getCustomer().getId() : null;
        Long serviceId = a.getService() != null ? a.getService().getId() : null;
        String serviceName = a.getService() != null ? a.getService().getName() : null;

        return new AppointmentResponseDTO(a.getId(), barberId, customerId, serviceId, serviceName,
                a.getAppointmentDate(), a.getEndDate(), a.getAppointmentTime(), a.getStatus(),
                a.getNotes(), a.getPrice());
    }
}
