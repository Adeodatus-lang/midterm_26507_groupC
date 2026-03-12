package au.ca.ac.rw.service;

import au.ca.ac.rw.dto.BarberRequest;
import au.ca.ac.rw.dto.BarberResponseDTO;
import au.ca.ac.rw.dto.ServiceSummaryDTO;
import au.ca.ac.rw.entity.Barber;
import au.ca.ac.rw.entity.User;
import au.ca.ac.rw.repository.BarberRepository;
import au.ca.ac.rw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BarberService {

    @Autowired
    private BarberRepository barberRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public BarberResponseDTO createBarber(BarberRequest request) {
        if (request.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with ID: " + request.getUserId()));

        if (barberRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Barber profile already exists for user with ID: " + request.getUserId());
        }

        Barber barber = new Barber();
        updateBarberFields(barber, request);
        barber.setUser(user);

        Barber savedBarber = barberRepository.save(barber);
        Barber fetched = barberRepository.findByIdWithServices(savedBarber.getId()).orElse(savedBarber);
        return convertToDTO(fetched);
    }

    public Page<BarberResponseDTO> getAllBarbers(Pageable pageable) {
        return barberRepository.findAll(pageable).map(this::convertToDTO);
    }

    public BarberResponseDTO getBarberById(Long id) {
        Barber barber = barberRepository.findByIdWithServices(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Barber not found with ID: " + id));
        return convertToDTO(barber);
    }

    public List<BarberResponseDTO> getAvailableBarbers() {
        return barberRepository.findAvailableWithServices().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public BarberResponseDTO updateBarber(Long id, BarberRequest request) {
        Barber barber = barberRepository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Barber not found with ID: " + id));

        updateBarberFields(barber, request);

        Barber updatedBarber = barberRepository.save(barber);
        Barber fetched = barberRepository.findByIdWithServices(updatedBarber.getId()).orElse(updatedBarber);
        return convertToDTO(fetched);
    }

    @Transactional
    public void deleteBarber(Long id) {
        if (!barberRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Barber not found with ID: " + id);
        }
        barberRepository.deleteById(id);
    }

    private void updateBarberFields(Barber barber, BarberRequest request) {
        if (request.getLicenseNumber() != null) {
            if (request.getLicenseNumber().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "License number cannot be empty");
            }
            barber.setLicenseNumber(request.getLicenseNumber());
        } else if (barber.getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "License number is required");
        }

        if (request.getSpecialization() != null) {
            barber.setSpecialization(request.getSpecialization());
        }

        if (request.getYearsOfExperience() != null) {
            if (request.getYearsOfExperience() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Years of experience cannot be negative");
            }
            barber.setYearsOfExperience(request.getYearsOfExperience());
        }

        if (request.getRating() != null) {
            if (request.getRating() < 0 || request.getRating() > 5) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 0 and 5");
            }
            barber.setRating(request.getRating());
        }

        if (request.getIsAvailable() != null) {
            barber.setIsAvailable(request.getIsAvailable());
        }
    }

    private BarberResponseDTO convertToDTO(Barber b) {
        if (b == null)
            return null;
        Long userId = b.getUser() != null ? b.getUser().getId() : null;
        String userEmail = b.getUser() != null ? b.getUser().getEmail() : null;
        String userFullName = b.getUser() != null ? b.getUser().getFullName() : null;
        List<ServiceSummaryDTO> services = b.getServices() == null ? java.util.Collections.emptyList()
                : b.getServices().stream().map(s -> new ServiceSummaryDTO(s.getId(), s.getName()))
                        .collect(Collectors.toList());

        return new BarberResponseDTO(b.getId(), b.getSpecialization(), b.getRating(),
                b.getYearsOfExperience(), b.getIsAvailable(), b.getLicenseNumber(), userId,
                userEmail, userFullName, services);
    }
}
