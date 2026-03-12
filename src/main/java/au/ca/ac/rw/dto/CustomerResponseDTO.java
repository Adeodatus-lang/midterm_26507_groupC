package au.ca.ac.rw.dto;

import au.ca.ac.rw.entity.Customer;
import java.time.LocalDateTime;
import java.util.Date;

public class CustomerResponseDTO {
    private Long id;
    private String preferences;
    private Integer loyaltyPoints;
    private String preferredPaymentMethod;
    private Long userId;
    private String userEmail;
    private String userFullName;
    private String userPhoneNumber;
    private VillageDTO village;
    private Date createdAt; // Changed to Date
    private Date updatedAt; // Changed to Date
    private Integer totalAppointments;
    private Integer completedAppointments;
    private Double totalSpent;

    // Default constructor
    public CustomerResponseDTO() {}

    // Constructor from Customer entity with safe access
    public CustomerResponseDTO(Customer customer) {
        if (customer != null) {
            this.id = customer.getId();
            this.preferences = customer.getPreferences();
            this.loyaltyPoints = customer.getLoyaltyPoints();
            this.preferredPaymentMethod = customer.getPreferredPaymentMethod();

            // Safe access to user data to avoid lazy loading issues
            if (customer.getUser() != null) {
                this.userId = customer.getUser().getId();
                this.userEmail = customer.getUser().getEmail();
                this.userFullName = customer.getUser().getFullName();
                this.userPhoneNumber = customer.getUser().getPhoneNumber();
                
                // Get timestamps from User entity - these are Date objects
                this.createdAt = customer.getUser().getCreatedAt();
                this.updatedAt = customer.getUser().getUpdatedAt();
            }
        }
    }

    // Basic parameterized constructor
    public CustomerResponseDTO(Long id, String preferences, Integer loyaltyPoints,
            String preferredPaymentMethod, Long userId, String userEmail, String userFullName) {
        this.id = id;
        this.preferences = preferences;
        this.loyaltyPoints = loyaltyPoints;
        this.preferredPaymentMethod = preferredPaymentMethod;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userFullName = userFullName;
    }

    // Constructor with phone number
    public CustomerResponseDTO(Long id, String preferences, Integer loyaltyPoints,
            String preferredPaymentMethod, Long userId, String userEmail, String userFullName,
            String userPhoneNumber) {
        this(id, preferences, loyaltyPoints, preferredPaymentMethod, userId, userEmail, userFullName);
        this.userPhoneNumber = userPhoneNumber;
    }

    // Full constructor with all fields (using Date)
    public CustomerResponseDTO(Long id, String preferences, Integer loyaltyPoints,
            String preferredPaymentMethod, Long userId, String userEmail, String userFullName,
            String userPhoneNumber, VillageDTO village, Date createdAt, 
            Date updatedAt, Integer totalAppointments, Integer completedAppointments,
            Double totalSpent) {
        this.id = id;
        this.preferences = preferences;
        this.loyaltyPoints = loyaltyPoints;
        this.preferredPaymentMethod = preferredPaymentMethod;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userFullName = userFullName;
        this.userPhoneNumber = userPhoneNumber;
        this.village = village;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.totalAppointments = totalAppointments;
        this.completedAppointments = completedAppointments;
        this.totalSpent = totalSpent;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPreferences() {
        return preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public String getPreferredPaymentMethod() {
        return preferredPaymentMethod;
    }

    public void setPreferredPaymentMethod(String preferredPaymentMethod) {
        this.preferredPaymentMethod = preferredPaymentMethod;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

    public String getUserPhoneNumber() {
        return userPhoneNumber;
    }

    public void setUserPhoneNumber(String userPhoneNumber) {
        this.userPhoneNumber = userPhoneNumber;
    }

    public VillageDTO getVillage() {
        return village;
    }

    public void setVillage(VillageDTO village) {
        this.village = village;
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

    public Integer getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(Integer totalAppointments) {
        this.totalAppointments = totalAppointments;
    }

    public Integer getCompletedAppointments() {
        return completedAppointments;
    }

    public void setCompletedAppointments(Integer completedAppointments) {
        this.completedAppointments = completedAppointments;
    }

    public Double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(Double totalSpent) {
        this.totalSpent = totalSpent;
    }

    // Utility methods
    public String getLoyaltyTier() {
        if (loyaltyPoints == null) return "Bronze";
        if (loyaltyPoints < 100) return "Bronze";
        if (loyaltyPoints < 500) return "Silver";
        if (loyaltyPoints < 1000) return "Gold";
        return "Platinum";
    }

    public Double getCompletionRate() {
        if (totalAppointments == null || totalAppointments == 0) return 0.0;
        if (completedAppointments == null) return 0.0;
        return (completedAppointments * 100.0) / totalAppointments;
    }

    public String getCustomerStatus() {
        if (totalAppointments == null || totalAppointments == 0) return "New Customer";
        if (totalAppointments < 5) return "Regular Customer";
        return "VIP Customer";
    }

    public boolean hasPreferences() {
        return preferences != null && !preferences.trim().isEmpty();
    }

    public String getFormattedTotalSpent() {
        return totalSpent != null ? String.format("$%.2f", totalSpent) : "$0.00";
    }

    // Helper method to convert Date to LocalDateTime (optional)
    public LocalDateTime getCreatedAtAsLocalDateTime() {
        return createdAt != null ? 
            createdAt.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : 
            null;
    }

    public LocalDateTime getUpdatedAtAsLocalDateTime() {
        return updatedAt != null ? 
            updatedAt.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : 
            null;
    }

    // Helper method to format date as string
    public String getCreatedAtFormatted() {
        if (createdAt == null) return "N/A";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(createdAt);
    }

    public String getUpdatedAtFormatted() {
        if (updatedAt == null) return "N/A";
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(updatedAt);
    }

    @Override
    public String toString() {
        return "CustomerResponseDTO{" +
                "id=" + id +
                ", preferences='" + preferences + '\'' +
                ", loyaltyPoints=" + loyaltyPoints +
                ", preferredPaymentMethod='" + preferredPaymentMethod + '\'' +
                ", userId=" + userId +
                ", userEmail='" + userEmail + '\'' +
                ", userFullName='" + userFullName + '\'' +
                ", userPhoneNumber='" + userPhoneNumber + '\'' +
                ", village=" + (village != null ? village.getName() : "null") +
                ", createdAt=" + getCreatedAtFormatted() +
                ", updatedAt=" + getUpdatedAtFormatted() +
                ", totalAppointments=" + totalAppointments +
                ", completedAppointments=" + completedAppointments +
                ", totalSpent=" + totalSpent +
                '}';
    }

    // Builder pattern for fluent creation
    public static class Builder {
        private Long id;
        private String preferences;
        private Integer loyaltyPoints;
        private String preferredPaymentMethod;
        private Long userId;
        private String userEmail;
        private String userFullName;
        private String userPhoneNumber;
        private VillageDTO village;
        private Date createdAt;
        private Date updatedAt;
        private Integer totalAppointments;
        private Integer completedAppointments;
        private Double totalSpent;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder preferences(String preferences) {
            this.preferences = preferences;
            return this;
        }

        public Builder loyaltyPoints(Integer loyaltyPoints) {
            this.loyaltyPoints = loyaltyPoints;
            return this;
        }

        public Builder preferredPaymentMethod(String preferredPaymentMethod) {
            this.preferredPaymentMethod = preferredPaymentMethod;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder userEmail(String userEmail) {
            this.userEmail = userEmail;
            return this;
        }

        public Builder userFullName(String userFullName) {
            this.userFullName = userFullName;
            return this;
        }

        public Builder userPhoneNumber(String userPhoneNumber) {
            this.userPhoneNumber = userPhoneNumber;
            return this;
        }

        public Builder village(VillageDTO village) {
            this.village = village;
            return this;
        }

        public Builder createdAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder totalAppointments(Integer totalAppointments) {
            this.totalAppointments = totalAppointments;
            return this;
        }

        public Builder completedAppointments(Integer completedAppointments) {
            this.completedAppointments = completedAppointments;
            return this;
        }

        public Builder totalSpent(Double totalSpent) {
            this.totalSpent = totalSpent;
            return this;
        }

        public CustomerResponseDTO build() {
            return new CustomerResponseDTO(id, preferences, loyaltyPoints, preferredPaymentMethod,
                    userId, userEmail, userFullName, userPhoneNumber, village, createdAt,
                    updatedAt, totalAppointments, completedAppointments, totalSpent);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}