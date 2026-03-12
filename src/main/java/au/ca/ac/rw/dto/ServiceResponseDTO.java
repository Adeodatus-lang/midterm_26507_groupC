package au.ca.ac.rw.dto;

public class ServiceResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer durationMinutes;
    private Boolean isActive;

    public ServiceResponseDTO(Long id, String name, String description, Double price, Integer durationMinutes, Boolean isActive) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.isActive = isActive;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Double getPrice() { return price; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public Boolean getIsActive() { return isActive; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(Double price) { this.price = price; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}