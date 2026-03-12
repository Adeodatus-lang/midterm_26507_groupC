package au.ca.ac.rw.controller;

import au.ca.ac.rw.dto.UserProfileRequest;
import au.ca.ac.rw.dto.UserProfileResponseDTO;
import au.ca.ac.rw.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user-profiles")
@CrossOrigin(origins = "*")
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    @PostMapping
    public ResponseEntity<UserProfileResponseDTO> createUserProfile(@RequestBody UserProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userProfileService.createUserProfile(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileResponseDTO> getUserProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(userProfileService.getUserProfileById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserProfileResponseDTO> updateUserProfile(@PathVariable Long id,
            @RequestBody UserProfileRequest request) {
        return ResponseEntity.ok(userProfileService.updateUserProfile(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserProfile(@PathVariable Long id) {
        userProfileService.deleteUserProfile(id);
        return ResponseEntity.noContent().build();
    }
}
