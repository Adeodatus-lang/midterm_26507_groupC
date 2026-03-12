package au.ca.ac.rw.service;

import au.ca.ac.rw.dto.CustomerRequest;
import au.ca.ac.rw.dto.CustomerResponseDTO;
import au.ca.ac.rw.entity.Customer;
import au.ca.ac.rw.entity.User;
import au.ca.ac.rw.repository.CustomerRepository;
import au.ca.ac.rw.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequest request) {
        if (request.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with id: " + request.getUserId()));

        if (customerRepository.findByUserId(request.getUserId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Customer profile already exists for user with id: " + request.getUserId());
        }

        Customer customer = new Customer();
        customer.setPreferences(request.getPreferences());

        Integer loyaltyPoints = request.getLoyaltyPoints();
        if (loyaltyPoints != null && loyaltyPoints < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loyalty points cannot be negative");
        }
        customer.setLoyaltyPoints(loyaltyPoints != null ? loyaltyPoints : 0);

        customer.setPreferredPaymentMethod(request.getPreferredPaymentMethod());
        customer.setUser(user);

        Customer savedCustomer = customerRepository.save(customer);
        return convertToDTO(savedCustomer);
    }

    public Page<CustomerResponseDTO> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable).map(this::convertToDTO);
    }

    public CustomerResponseDTO getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer not found with id: " + id));
    }

    public Page<CustomerResponseDTO> getCustomersByMinLoyaltyPoints(Integer points, Pageable pageable) {
        if (points == null || points < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid loyalty points");
        }
        return customerRepository.findByLoyaltyPointsGreaterThanEqual(points, pageable).map(this::convertToDTO);
    }

    public CustomerResponseDTO getCustomerByUserId(Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }

        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + userId);
        }

        return customerRepository.findByUserId(userId)
                .map(this::convertToDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer not found for user with id: " + userId));
    }

    @Transactional
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer not found with id: " + id));

        if (request.getLoyaltyPoints() != null) {
            if (request.getLoyaltyPoints() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Loyalty points cannot be negative");
            }
            customer.setLoyaltyPoints(request.getLoyaltyPoints());
        }

        if (request.getPreferences() != null) {
            customer.setPreferences(request.getPreferences());
        }

        if (request.getPreferredPaymentMethod() != null) {
            customer.setPreferredPaymentMethod(request.getPreferredPaymentMethod());
        }

        return convertToDTO(customerRepository.save(customer));
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Customer not found with id: " + id));
        customerRepository.delete(customer);
    }

    private CustomerResponseDTO convertToDTO(Customer customer) {
        if (customer == null)
            return null;

        User user = customer.getUser();
        Long userId = user != null ? user.getId() : null;
        String userEmail = user != null ? user.getEmail() : null;
        String userFullName = user != null ? user.getFullName() : null;

        return new CustomerResponseDTO(
                customer.getId(),
                customer.getPreferences(),
                customer.getLoyaltyPoints() != null ? customer.getLoyaltyPoints() : 0,
                customer.getPreferredPaymentMethod(),
                userId,
                userEmail,
                userFullName);
    }
}
