package au.ca.ac.rw.dto;

public class UserLocationDTO {
    private Long id;
    private String email;
    private String role;
    private String phoneNumber;
    private String fullName;
    private String villageName;
    private String villageCode;
    private String cellName;
    private String cellCode;
    private String sectorName;
    private String sectorCode;
    private String districtName;
    private String districtCode;
    private String provinceName;
    private String provinceCode;

    // Constructors
    public UserLocationDTO() {}

    public UserLocationDTO(Long id, String email, String role, String phoneNumber) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }
    
    public UserLocationDTO(Long id, String email, String role, String phoneNumber, String fullName) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getVillageName() { return villageName; }
    public void setVillageName(String villageName) { this.villageName = villageName; }

    public String getVillageCode() { return villageCode; }
    public void setVillageCode(String villageCode) { this.villageCode = villageCode; }

    public String getCellName() { return cellName; }
    public void setCellName(String cellName) { this.cellName = cellName; }

    public String getCellCode() { return cellCode; }
    public void setCellCode(String cellCode) { this.cellCode = cellCode; }

    public String getSectorName() { return sectorName; }
    public void setSectorName(String sectorName) { this.sectorName = sectorName; }

    public String getSectorCode() { return sectorCode; }
    public void setSectorCode(String sectorCode) { this.sectorCode = sectorCode; }

    public String getDistrictName() { return districtName; }
    public void setDistrictName(String districtName) { this.districtName = districtName; }

    public String getDistrictCode() { return districtCode; }
    public void setDistrictCode(String districtCode) { this.districtCode = districtCode; }

    public String getProvinceName() { return provinceName; }
    public void setProvinceName(String provinceName) { this.provinceName = provinceName; }

    public String getProvinceCode() { return provinceCode; }
    public void setProvinceCode(String provinceCode) { this.provinceCode = provinceCode; }
}