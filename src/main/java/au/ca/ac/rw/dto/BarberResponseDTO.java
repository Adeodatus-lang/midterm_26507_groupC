package au.ca.ac.rw.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BarberResponseDTO {
    private Long id;
    private String specialization;
    private BigDecimal rating;
    private Integer yearsOfExperience;
    private Boolean isAvailable;
    private String licenseNumber;
    private Long userId;
    private String userEmail;
    private String userFullName;
    private String userPhoneNumber;
    private VillageDTO village;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ServiceSummaryDTO> services;
    private Integer totalAppointments;
    private Integer completedAppointments;

    public BarberResponseDTO() {}

    // Basic constructor
    public BarberResponseDTO(Long id, String specialization, BigDecimal rating,
            Integer yearsOfExperience, Boolean isAvailable, String licenseNumber, Long userId,
            String userEmail, String userFullName) {
        this.id = id;
        this.specialization = specialization;
        this.rating = rating;
        this.yearsOfExperience = yearsOfExperience;
        this.isAvailable = isAvailable;
        this.licenseNumber = licenseNumber;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userFullName = userFullName;
    }

    // Constructor with user phone number
    public BarberResponseDTO(Long id, String specialization, BigDecimal rating,
            Integer yearsOfExperience, Boolean isAvailable, String licenseNumber, Long userId,
            String userEmail, String userFullName, String userPhoneNumber) {
        this(id, specialization, rating, yearsOfExperience, isAvailable, licenseNumber, userId,
                userEmail, userFullName);
        this.userPhoneNumber = userPhoneNumber;
    }

    // NEW: Constructor with Double rating (to match your entity)
    public BarberResponseDTO(Long id, String specialization, Double rating,
            Integer yearsOfExperience, Boolean isAvailable, String licenseNumber, Long userId,
            String userEmail, String userFullName, String userPhoneNumber) {
        this.id = id;
        this.specialization = specialization;
        this.rating = rating != null ? BigDecimal.valueOf(rating) : null;
        this.yearsOfExperience = yearsOfExperience;
        this.isAvailable = isAvailable;
        this.licenseNumber = licenseNumber;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userFullName = userFullName;
        this.userPhoneNumber = userPhoneNumber;
    }

    // Constructor with services
    public BarberResponseDTO(Long id, String specialization, BigDecimal rating,
            Integer yearsOfExperience, Boolean isAvailable, String licenseNumber, Long userId,
            String userEmail, String userFullName, List<ServiceSummaryDTO> services) {
        this(id, specialization, rating, yearsOfExperience, isAvailable, licenseNumber, userId,
                userEmail, userFullName);
        this.services = services;
    }

    // Constructor with Double rating and services
    public BarberResponseDTO(Long id, String specialization, Double rating,
            Integer yearsOfExperience, Boolean isAvailable, String licenseNumber, Long userId,
            String userEmail, String userFullName, List<ServiceSummaryDTO> services) {
        this.id = id;
        this.specialization = specialization;
        this.rating = rating != null ? BigDecimal.valueOf(rating) : null;
        this.yearsOfExperience = yearsOfExperience;
        this.isAvailable = isAvailable;
        this.licenseNumber = licenseNumber;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userFullName = userFullName;
        this.services = services;
    }

    // Full constructor with all fields
    public BarberResponseDTO(Long id, String specialization, BigDecimal rating,
            Integer yearsOfExperience, Boolean isAvailable, String licenseNumber, Long userId,
            String userEmail, String userFullName, String userPhoneNumber, VillageDTO village,
            LocalDateTime createdAt, LocalDateTime updatedAt, List<ServiceSummaryDTO> services) {
        this.id = id;
        this.specialization = specialization;
        this.rating = rating;
        this.yearsOfExperience = yearsOfExperience;
        this.isAvailable = isAvailable;
        this.licenseNumber = licenseNumber;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userFullName = userFullName;
        this.userPhoneNumber = userPhoneNumber;
        this.village = village;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.services = services;
    }

    // Complete constructor with statistics
    public BarberResponseDTO(Long id, String specialization, BigDecimal rating,
            Integer yearsOfExperience, Boolean isAvailable, String licenseNumber, Long userId,
            String userEmail, String userFullName, String userPhoneNumber, VillageDTO village,
            LocalDateTime createdAt, LocalDateTime updatedAt, List<ServiceSummaryDTO> services,
            Integer totalAppointments, Integer completedAppointments) {
        this(id, specialization, rating, yearsOfExperience, isAvailable, licenseNumber, userId,
                userEmail, userFullName, userPhoneNumber, village, createdAt, updatedAt, services);
        this.totalAppointments = totalAppointments;
        this.completedAppointments = completedAppointments;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ServiceSummaryDTO> getServices() {
        return services;
    }

    public void setServices(List<ServiceSummaryDTO> services) {
        this.services = services;
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

    // Utility methods
    public Double getRatingAsDouble() {
        return rating != null ? rating.doubleValue() : null;
    }

    public String getAvailabilityStatus() {
        return Boolean.TRUE.equals(isAvailable) ? "Available" : "Not Available";
    }

    public String getExperienceLevel() {
        if (yearsOfExperience == null) return "Not specified";
        if (yearsOfExperience < 2) return "Beginner";
        if (yearsOfExperience < 5) return "Intermediate";
        return "Expert";
    }

    public Double getSuccessRate() {
        if (totalAppointments == null || totalAppointments == 0) return 0.0;
        if (completedAppointments == null) return 0.0;
        return (completedAppointments * 100.0) / totalAppointments;
    }

    @Override
    public String toString() {
        return "BarberResponseDTO{" +
                "id=" + id +
                ", specialization='" + specialization + '\'' +
                ", rating=" + rating +
                ", yearsOfExperience=" + yearsOfExperience +
                ", isAvailable=" + isAvailable +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", userId=" + userId +
                ", userEmail='" + userEmail + '\'' +
                ", userFullName='" + userFullName + '\'' +
                ", userPhoneNumber='" + userPhoneNumber + '\'' +
                ", village=" + (village != null ? village.getName() : "null") +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", servicesCount=" + (services != null ? services.size() : 0) +
                ", totalAppointments=" + totalAppointments +
                ", completedAppointments=" + completedAppointments +
                '}';
    }
}