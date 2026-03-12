package au.ca.ac.rw.controller;

import au.ca.ac.rw.entity.Location;
import au.ca.ac.rw.entity.User;
import au.ca.ac.rw.repository.LocationRepository;
import au.ca.ac.rw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/location-migration")
public class LocationMigrationController {
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/run")
    @Transactional
    public ResponseEntity<String> migrateUserLocations() {
        List<User> users = userRepository.findAll();
        int migrated = 0;

        for (User user : users) {
            if (user.getVillage() != null && !(user.getVillage() instanceof Location)) {
                try {
                    // Try to get the code from the old village object
                    String villageCode = (String) user.getVillage().getClass().getMethod("getCode")
                            .invoke(user.getVillage());
                    Location newVillage = locationRepository.findFirstByCode(villageCode);
                    if (newVillage != null
                            && newVillage.getType() == Location.LocationType.VILLAGE) {
                        user.setVillage(newVillage);
                        userRepository.save(user);
                        migrated++;
                    }
                } catch (NoSuchMethodException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
                    // Skip if we can't get the code or invoke the legacy getter
                }
            }
        }

        return ResponseEntity.ok("User location migration completed. Users updated: " + migrated);
    }

    @PostMapping("/migrate-users-village")
    @Transactional
    public ResponseEntity<String> migrateUsersVillageToLocation() {
        // Current `User.village` is already of type `Location` in the domain model.
        // No migration is required at runtime. Keep this endpoint as a no-op for safety
        // or for manual invocation in case legacy data needs special handling.
        return ResponseEntity.ok("No migration performed: `User.village` is already a `Location`.");
    }
}
