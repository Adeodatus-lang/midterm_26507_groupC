package au.ca.ac.rw.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import au.ca.ac.rw.entity.Location;
import au.ca.ac.rw.entity.Location.LocationType;
import au.ca.ac.rw.entity.User;
import au.ca.ac.rw.enums.UserRole;
import au.ca.ac.rw.repository.LocationRepository;
import au.ca.ac.rw.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private LocationRepository locationRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        if (locationRepository.count() == 0) {
            initializeLocations();
        }
        if (userRepository.count() == 0) {
            initializeUsers();
        }
    }

    private void initializeLocations() {
        // Create Province
        Location kigali = new Location();
        kigali.setName("Kigali");
        kigali.setCode("KGL");
        kigali.setType(LocationType.PROVINCE);
        kigali = locationRepository.save(kigali);

        // Create District
        Location gasabo = new Location();
        gasabo.setName("Gasabo");
        gasabo.setCode("GSB");
        gasabo.setType(LocationType.DISTRICT);
        gasabo.setParent(kigali);
        gasabo = locationRepository.save(gasabo);

        // Create Sector
        Location kacyiru = new Location();
        kacyiru.setName("Kacyiru");
        kacyiru.setCode("KCY");
        kacyiru.setType(LocationType.SECTOR);
        kacyiru.setParent(gasabo);
        kacyiru = locationRepository.save(kacyiru);

        // Create Cell
        Location kamatamu = new Location();
        kamatamu.setName("Kamatamu");
        kamatamu.setCode("KMT");
        kamatamu.setType(LocationType.CELL);
        kamatamu.setParent(kacyiru);
        kamatamu = locationRepository.save(kamatamu);

        // Create Village
        Location nyarutarama = new Location();
        nyarutarama.setName("Nyarutarama");
        nyarutarama.setCode("NYR");
        nyarutarama.setType(LocationType.VILLAGE);
        nyarutarama.setParent(kamatamu);
        locationRepository.save(nyarutarama);
    }

    private void initializeUsers() {
        Location village = locationRepository.findByType(LocationType.VILLAGE).get(0);

        // Create Barber
        User barber = new User();
        barber.setEmail("john.barber@example.com");
        barber.setPassword("password123");
        barber.setFullName("John Doe");
        barber.setPhoneNumber("+250788123456");
        barber.setRole(UserRole.BARBER);
        barber.setVillage(village);
        userRepository.save(barber);

        // Create Customer
        User customer = new User();
        customer.setEmail("jane.customer@example.com");
        customer.setPassword("password123");
        customer.setFullName("Jane Smith");
        customer.setPhoneNumber("+250788654321");
        customer.setRole(UserRole.CUSTOMER);
        customer.setVillage(village);
        userRepository.save(customer);
    }
}