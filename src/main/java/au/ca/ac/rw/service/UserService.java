package au.ca.ac.rw.service;

import au.ca.ac.rw.dto.UserRequest;
import au.ca.ac.rw.dto.UserResponseDTO;
import au.ca.ac.rw.entity.Location;
import au.ca.ac.rw.entity.User;
import au.ca.ac.rw.enums.UserRole;
import au.ca.ac.rw.repository.LocationRepository;
import au.ca.ac.rw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Transactional
    public UserResponseDTO createUser(UserRequest userReq) {
        User user = new User();
        updateUserFields(user, userReq);

        User savedUser = userRepository.save(user);
        // Fetch with village to ensure DTO mapping has location loaded
        User fetched = userRepository.findByIdWithVillageOptional(savedUser.getId())
                .orElse(savedUser);
        return new UserResponseDTO(fetched);
    }

    public Page<UserResponseDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAllWithVillage(pageable).map(UserResponseDTO::new);
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findByIdWithVillageOptional(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
        return new UserResponseDTO(user);
    }

    public UserResponseDTO getUserByEmail(String email) {
        User user = userRepository.findByEmailWithVillage(email)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with email: " + email));
        return new UserResponseDTO(user);
    }

    public Page<UserResponseDTO> getUsersByRole(String role, Pageable pageable) {
        validateRole(role);
        return userRepository.findByRoleWithVillage(role.toUpperCase(), pageable).map(UserResponseDTO::new);
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequest userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));

        updateUserFields(user, userDetails);

        User updatedUser = userRepository.save(user);
        User fetched = userRepository.findByIdWithVillageOptional(updatedUser.getId())
                .orElse(updatedUser);
        return new UserResponseDTO(fetched);
    }

    @Transactional
    public UserResponseDTO assignVillageToUser(Long userId, Long villageId) {
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId));

        Location location = locationRepository.findById(villageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Location not found with id: " + villageId));

        if (location.getType() != Location.LocationType.VILLAGE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Location with id: " + villageId + " is not a village (type: " + location.getType() + ")");
        }

        user.setVillage(location);
        User updatedUser = userRepository.save(user);
        User fetched = userRepository.findByIdWithVillageOptional(updatedUser.getId())
                .orElse(updatedUser);
        return new UserResponseDTO(fetched);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private void updateUserFields(User user, UserRequest userReq) {
        if (userReq.getEmail() != null)
            user.setEmail(userReq.getEmail());
        if (userReq.getPassword() != null)
            user.setPassword(userReq.getPassword());
        if (userReq.getFullName() != null)
            user.setFullName(userReq.getFullName());
        if (userReq.getPhoneNumber() != null)
            user.setPhoneNumber(userReq.getPhoneNumber());

        if (userReq.getRole() != null) {
            validateRole(userReq.getRole());
            user.setRole(UserRole.valueOf(userReq.getRole().toUpperCase()));
        }

        if (userReq.getVillageId() != null) {
            Location location = locationRepository.findById(userReq.getVillageId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Village location not found with id: " + userReq.getVillageId()));

            if (location.getType() != Location.LocationType.VILLAGE) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Location is not a village (type: " + location.getType() + ")");
            }
            user.setVillage(location);
        }
    }

    private void validateRole(String role) {
        try {
            UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role: " + role);
        }
    }
}
