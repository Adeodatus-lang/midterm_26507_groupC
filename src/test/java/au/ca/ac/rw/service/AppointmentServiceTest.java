package au.ca.ac.rw.service;

import au.ca.ac.rw.dto.AppointmentRequest;
import au.ca.ac.rw.dto.AppointmentResponseDTO;
import au.ca.ac.rw.entity.Appointment;
import au.ca.ac.rw.entity.Barber;
import au.ca.ac.rw.entity.Customer;
import au.ca.ac.rw.entity.Service;
import au.ca.ac.rw.repository.AppointmentRepository;
import au.ca.ac.rw.repository.BarberRepository;
import au.ca.ac.rw.repository.CustomerRepository;
import au.ca.ac.rw.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private BarberRepository barberRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ServiceRepository serviceRepository;

    private AppointmentService appointmentService;

    private AppointmentRequest validRequest;
    private Barber barber;
    private Customer customer;
    private Service service;

    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentService();
        // Manually inject mocks because @InjectMocks can be finicky with field
        // injection in some environments
        ReflectionTestUtils.setField(appointmentService, "appointmentRepository", appointmentRepository);
        ReflectionTestUtils.setField(appointmentService, "barberRepository", barberRepository);
        ReflectionTestUtils.setField(appointmentService, "customerRepository", customerRepository);
        ReflectionTestUtils.setField(appointmentService, "serviceRepository", serviceRepository);

        // Monday, 2026-06-01 at 10:00 AM
        LocalDateTime ldt = LocalDateTime.of(2026, 6, 1, 10, 0);
        Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());

        validRequest = new AppointmentRequest();
        validRequest.setBarberId(1L);
        validRequest.setCustomerId(1L);
        validRequest.setServiceId(1L);
        validRequest.setAppointmentDate(date);
        validRequest.setAppointmentTime("10:00");

        barber = new Barber();
        barber.setId(1L);
        barber.setIsAvailable(true);

        customer = new Customer();
        customer.setId(1L);

        service = new Service();
        service.setId(1L);
        service.setDurationMinutes(30);
        service.setIsActive(true);
        service.setPrice(5000.0);
    }

    @Test
    void createAppointment_Success() {
        // Arrange
        when(barberRepository.findByIdWithUserForUpdate(1L)).thenReturn(Optional.of(barber));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(appointmentRepository.findOverlappingAppointments(any(), any(), any())).thenReturn(java.util.Collections.emptyList());
        when(appointmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AppointmentResponseDTO result = appointmentService.createAppointment(validRequest);

        // Assert
        assertNotNull(result);
        verify(appointmentRepository).save(any());
    }

    @Test
    void createAppointment_BarberNotFound() {
        // Arrange
        when(barberRepository.findByIdWithUserForUpdate(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            appointmentService.createAppointment(validRequest);
        });
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Barber not found"));
    }

    @Test
    void createAppointment_OutsideBusinessHours() {
        // Set time to 7:00 AM (Shop opens at 9:00 AM according to AppointmentService)
        LocalDateTime ldt = LocalDateTime.of(2026, 6, 1, 7, 0);
        Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
        validRequest.setAppointmentDate(date);

        // Arrange
        when(barberRepository.findByIdWithUserForUpdate(1L)).thenReturn(Optional.of(barber));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            appointmentService.createAppointment(validRequest);
        });
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertTrue(exception.getReason().contains("business hours"));
    }

    @Test
    void createAppointment_Overlapping() {
        // Arrange
        when(barberRepository.findByIdWithUserForUpdate(1L)).thenReturn(Optional.of(barber));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(serviceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(appointmentRepository.findOverlappingAppointments(any(), any(), any()))
                .thenReturn(java.util.List.of(new Appointment()));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            appointmentService.createAppointment(validRequest);
        });
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertTrue(exception.getReason().contains("overlaps"));
    }
}
