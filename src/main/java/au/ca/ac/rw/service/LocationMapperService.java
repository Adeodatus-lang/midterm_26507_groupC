package au.ca.ac.rw.service;

import au.ca.ac.rw.dto.*;
import au.ca.ac.rw.entity.*;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
public class LocationMapperService {

    public ProvinceDTO mapToProvinceDTO(Location location) {
        if (location == null || location.getType() != Location.LocationType.PROVINCE)
            return null;

        ProvinceDTO dto = new ProvinceDTO(location.getId(), location.getName(), location.getCode());
        if (location.getChildren() != null && !location.getChildren().isEmpty()) {
            dto.setDistricts(location.getChildren().stream()
                    .filter(child -> child.getType() == Location.LocationType.DISTRICT)
                    .map(this::mapToDistrictDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public DistrictDTO mapToDistrictDTO(Location location) {
        if (location == null || location.getType() != Location.LocationType.DISTRICT)
            return null;

        DistrictDTO dto = new DistrictDTO(location.getId(), location.getName(), location.getCode());
        if (location.getChildren() != null && !location.getChildren().isEmpty()) {
            dto.setSectors(location.getChildren().stream()
                    .filter(child -> child.getType() == Location.LocationType.SECTOR)
                    .map(this::mapToSectorDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public SectorDTO mapToSectorDTO(Location location) {
        if (location == null || location.getType() != Location.LocationType.SECTOR)
            return null;

        SectorDTO dto = new SectorDTO(location.getId(), location.getName(), location.getCode());
        if (location.getChildren() != null && !location.getChildren().isEmpty()) {
            dto.setCells(location.getChildren().stream()
                    .filter(child -> child.getType() == Location.LocationType.CELL)
                    .map(this::mapToCellDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public CellDTO mapToCellDTO(Location location) {
        if (location == null || location.getType() != Location.LocationType.CELL)
            return null;

        CellDTO dto = new CellDTO(location.getId(), location.getName(), location.getCode());
        if (location.getChildren() != null && !location.getChildren().isEmpty()) {
            dto.setVillages(location.getChildren().stream()
                    .filter(child -> child.getType() == Location.LocationType.VILLAGE)
                    .map(this::mapToVillageDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public VillageDTO mapToVillageDTO(Location location) {
        if (location == null || location.getType() != Location.LocationType.VILLAGE)
            return null;
        return new VillageDTO(location.getId(), location.getName(), location.getCode());
    }

    public UserResponseDTO mapToUserResponseDTO(User user) {
        if (user == null)
            return null;

        Long barberId = null;
        Long customerId = null;

        try {
            if (user.getBarber() != null) {
                barberId = user.getBarber().getId();
            }
        } catch (Exception e) {

            barberId = null;
        }

        try {
            if (user.getCustomer() != null) {
                customerId = user.getCustomer().getId();
            }
        } catch (Exception e) {
            customerId = null;
        }

        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole() != null ? user.getRole().name() : null,
                user.getPhoneNumber(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getVillage() != null ? mapToVillageDTO(user.getVillage()) : null,
                user.getUserProfile() != null ? user.getUserProfile().getId() : null,
                barberId,
                customerId);
    }

    public BarberResponseDTO mapToBarberResponseDTO(Barber barber) {
        if (barber == null)
            return null;

        String barberName = null;
        String email = null;
        String phoneNumber = null;
        Long userId = null;

        try {
            if (barber.getUser() != null) {
                User user = barber.getUser();
                barberName = user.getFullName();
                email = user.getEmail();
                phoneNumber = user.getPhoneNumber();
                userId = user.getId();
            }
        } catch (Exception e) {

        }

        return new BarberResponseDTO(
                barber.getId(),
                barber.getSpecialization(),
                barber.getRating(),
                barber.getYearsOfExperience(),
                barber.getIsAvailable(),
                barber.getLicenseNumber(),
                userId,
                email,
                barberName,
                phoneNumber);
    }

    public CustomerResponseDTO mapToCustomerResponseDTO(Customer customer) {
        if (customer == null)
            return null;

        String customerName = null;
        String email = null;
        String phoneNumber = null;
        Long userId = null;

        try {
            if (customer.getUser() != null) {
                User user = customer.getUser();
                customerName = user.getFullName();
                email = user.getEmail();
                phoneNumber = user.getPhoneNumber();
                userId = user.getId();
            }
        } catch (Exception e) {

        }

        return new CustomerResponseDTO(
                customer.getId(),
                customer.getPreferences(),
                customer.getLoyaltyPoints(),
                customer.getPreferredPaymentMethod(),
                userId,
                email,
                customerName,
                phoneNumber);
    }

    public UserLocationDTO mapToUserLocationDTO(User user) {
        if (user == null)
            return null;

        UserLocationDTO dto = new UserLocationDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole() != null ? user.getRole().name() : null);
        dto.setPhoneNumber(user.getPhoneNumber());

        try {
            if (user.getVillage() != null && user.getVillage().getType() == Location.LocationType.VILLAGE) {
                Location village = user.getVillage();
                dto.setVillageName(village.getName());
                dto.setVillageCode(village.getCode());

                Location cell = village.getParent();
                if (cell != null && cell.getType() == Location.LocationType.CELL) {
                    dto.setCellName(cell.getName());
                    dto.setCellCode(cell.getCode());

                    Location sector = cell.getParent();
                    if (sector != null && sector.getType() == Location.LocationType.SECTOR) {
                        dto.setSectorName(sector.getName());
                        dto.setSectorCode(sector.getCode());

                        Location district = sector.getParent();
                        if (district != null && district.getType() == Location.LocationType.DISTRICT) {
                            dto.setDistrictName(district.getName());
                            dto.setDistrictCode(district.getCode());

                            Location province = district.getParent();
                            if (province != null && province.getType() == Location.LocationType.PROVINCE) {
                                dto.setProvinceName(province.getName());
                                dto.setProvinceCode(province.getCode());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return dto;
    }
}