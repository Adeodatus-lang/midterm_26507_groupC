package au.ca.ac.rw.service;

import au.ca.ac.rw.dto.UserProfileRequest;
import au.ca.ac.rw.dto.UserProfileResponseDTO;
import au.ca.ac.rw.entity.User;
import au.ca.ac.rw.entity.UserProfile;
import au.ca.ac.rw.repository.UserProfileRepository;
import au.ca.ac.rw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserProfileResponseDTO createUserProfile(UserProfileRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with id: " + request.getUserId()));

        if (userProfileRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "User profile already exists for user with id: " + request.getUserId());
        }

        UserProfile userProfile = new UserProfile();
        updateProfileFields(userProfile, request);
        userProfile.setUser(user);

        UserProfile savedProfile = userProfileRepository.save(userProfile);
        return convertToDTO(savedProfile);
    }

    public UserProfileResponseDTO getUserProfileById(Long id) {
        return userProfileRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User profile not found with id: " + id));
    }

    @Transactional
    public UserProfileResponseDTO updateUserProfile(Long id, UserProfileRequest request) {
        UserProfile userProfile = userProfileRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User profile not found with id: " + id));

        updateProfileFields(userProfile, request);

        UserProfile saved = userProfileRepository.save(userProfile);
        return convertToDTO(saved);
    }

    @Transactional
    public void deleteUserProfile(Long id) {
        if (!userProfileRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found with id: " + id);
        }
        userProfileRepository.deleteById(id);
    }

    private void updateProfileFields(UserProfile userProfile, UserProfileRequest request) {
        if (request.getFirstName() != null)
            userProfile.setFirstName(request.getFirstName());
        if (request.getLastName() != null)
            userProfile.setLastName(request.getLastName());
        if (request.getDateOfBirth() != null)
            userProfile.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null)
            userProfile.setGender(request.getGender());
        if (request.getProfilePicture() != null)
            userProfile.setProfilePicture(request.getProfilePicture());
        if (request.getBio() != null)
            userProfile.setBio(request.getBio());
    }

    private UserProfileResponseDTO convertToDTO(UserProfile up) {
        if (up == null)
            return null;
        return new UserProfileResponseDTO(
                up.getId(),
                up.getUser() != null ? up.getUser().getId() : null,
                up.getFirstName(),
                up.getLastName(),
                up.getDateOfBirth(),
                up.getGender(),
                up.getProfilePicture(),
                up.getBio());
    }
}
