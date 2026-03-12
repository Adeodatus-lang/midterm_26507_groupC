package au.ca.ac.rw.dto;

import au.ca.ac.rw.entity.User;
import java.util.Date;

public class UserResponseDTO {
    private Long id;
    private String email;
    private String fullName;
    private String role; // Changed to String to match UserRole enum conversion
    private String phoneNumber;
    private Date createdAt; // Changed to Date to match your User entity
    private Date updatedAt; // Changed to Date to match your User entity
    private VillageDTO village;
    private Long userProfileId;
    private Long barberId;
    private Long customerId;

    // Constructors
    public UserResponseDTO() {}

    // Basic constructor
    public UserResponseDTO(Long id, String email, String role, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }

    // Constructor with village
    public UserResponseDTO(Long id, String email, String role, String phoneNumber,
            VillageDTO village) {
        this(id, email, role, phoneNumber);
        this.village = village;
    }

    // Full constructor
    public UserResponseDTO(Long id, String email, String fullName, String role, String phoneNumber,
            Date createdAt, Date updatedAt, VillageDTO village, Long userProfileId, Long barberId,
            Long customerId) {
        this(id, email, role, phoneNumber);
        this.fullName = fullName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.village = village;
        this.userProfileId = userProfileId;
        this.barberId = barberId;
        this.customerId = customerId;
    }

    // Constructor from User entity
    public UserResponseDTO(User user) {
        if (user != null) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.fullName = user.getFullName();

            // Convert UserRole enum to String
            this.role = user.getRole() != null ? user.getRole().name() : null;

            this.phoneNumber = user.getPhoneNumber();
            this.createdAt = user.getCreatedAt();
            this.updatedAt = user.getUpdatedAt();

            // Convert Village entity to VillageDTO if exists
            if (user.getVillage() != null) {
                this.village = new VillageDTO(user.getVillage().getId(),
                        user.getVillage().getName(), user.getVillage().getCode());
            }

            // Set relationship IDs (not full objects to avoid circular references)
            this.userProfileId =
                    user.getUserProfile() != null ? user.getUserProfile().getId() : null;
            this.barberId = user.getBarber() != null ? user.getBarber().getId() : null;
            this.customerId = user.getCustomer() != null ? user.getCustomer().getId() : null;
        }
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public VillageDTO getVillage() {
        return village;
    }

    public void setVillage(VillageDTO village) {
        this.village = village;
    }

    public Long getUserProfileId() {
        return userProfileId;
    }

    public void setUserProfileId(Long userProfileId) {
        this.userProfileId = userProfileId;
    }

    public Long getBarberId() {
        return barberId;
    }

    public void setBarberId(Long barberId) {
        this.barberId = barberId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    // toString method for debugging
    @Override
    public String toString() {
        return "UserResponseDTO{" + "id=" + id + ", email='" + email + '\'' + ", fullName='"
                + fullName + '\'' + ", role='" + role + '\'' + ", phoneNumber='" + phoneNumber
                + '\'' + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", village="
                + (village != null ? village.toString() : "null") + ", userProfileId="
                + userProfileId + ", barberId=" + barberId + ", customerId=" + customerId + '}';
    }
}
