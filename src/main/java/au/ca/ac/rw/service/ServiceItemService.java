package au.ca.ac.rw.service;

import au.ca.ac.rw.dto.ServiceRequest;
import au.ca.ac.rw.dto.ServiceResponseDTO;
import au.ca.ac.rw.dto.ServiceSummaryDTO;
import au.ca.ac.rw.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceItemService {

    @Autowired
    private ServiceRepository serviceRepository;

    public Page<ServiceResponseDTO> getAllServices(Boolean activeOnly, Pageable pageable) {
        Page<au.ca.ac.rw.entity.Service> servicePage;
        if (activeOnly != null && activeOnly) {
            servicePage = serviceRepository.findByIsActiveTrue(pageable);
        } else {
            servicePage = serviceRepository.findAll(pageable);
        }
        return servicePage.map(this::convertToDTO);
    }

    public List<ServiceSummaryDTO> getAllServiceSummaries(Boolean activeOnly) {
        List<au.ca.ac.rw.entity.Service> services;
        if (activeOnly != null && activeOnly) {
            services = serviceRepository.findByIsActiveTrue(Pageable.unpaged()).getContent();
        } else {
            services = serviceRepository.findAll();
        }
        return services.stream()
                .map(s -> new ServiceSummaryDTO(s.getId(), s.getName()))
                .collect(Collectors.toList());
    }

    public ServiceResponseDTO getServiceById(Long id) {
        return serviceRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Service not found with id: " + id));
    }

    @Transactional
    public ServiceResponseDTO createService(ServiceRequest req) {
        validateRequest(req);

        au.ca.ac.rw.entity.Service service = new au.ca.ac.rw.entity.Service();
        service.setName(req.getName().trim());
        service.setDescription(req.getDescription() != null ? req.getDescription().trim() : null);
        service.setPrice(req.getPrice());
        service.setDurationMinutes(req.getDurationMinutes());
        service.setIsActive(true);

        return convertToDTO(serviceRepository.save(service));
    }

    @Transactional
    public ServiceResponseDTO updateService(Long id, ServiceRequest req) {
        au.ca.ac.rw.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Service not found with id: " + id));

        if (req.getName() != null) {
            if (req.getName().trim().isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name cannot be empty");
            }
            service.setName(req.getName().trim());
        }

        if (req.getDescription() != null) {
            service.setDescription(req.getDescription().trim());
        }

        if (req.getPrice() != null) {
            if (req.getPrice() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price cannot be negative");
            }
            service.setPrice(req.getPrice());
        }

        if (req.getDurationMinutes() != null) {
            if (req.getDurationMinutes() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duration must be greater than 0");
            }
            service.setDurationMinutes(req.getDurationMinutes());
        }

        if (req.getIsActive() != null) {
            service.setIsActive(req.getIsActive());
        }

        return convertToDTO(serviceRepository.save(service));
    }

    @Transactional
    public void deleteService(Long id) {
        au.ca.ac.rw.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Service not found with id: " + id));

        service.setIsActive(false);
        serviceRepository.save(service);
    }

    private void validateRequest(ServiceRequest req) {
        if (req.getName() == null || req.getName().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }
        if (req.getPrice() == null || req.getPrice() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid price");
        }
        if (req.getDurationMinutes() == null || req.getDurationMinutes() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid duration");
        }
    }

    private ServiceResponseDTO convertToDTO(au.ca.ac.rw.entity.Service service) {
        if (service == null)
            return null;
        return new ServiceResponseDTO(
                service.getId(),
                service.getName(),
                service.getDescription(),
                service.getPrice(),
                service.getDurationMinutes(),
                service.getIsActive());
    }
}
