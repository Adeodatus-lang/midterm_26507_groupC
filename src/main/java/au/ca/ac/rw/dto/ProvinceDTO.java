package au.ca.ac.rw.dto;

import java.util.List;
import java.util.ArrayList;

public class ProvinceDTO {
    private Long id;
    private String name;
    private String code;
    /** Children using new Location model (districts) */
    private List<DistrictDTO> districts = new ArrayList<>();

    public ProvinceDTO() {
    }

    public ProvinceDTO(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public ProvinceDTO(Long id, String name, String code, List<DistrictDTO> districts) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.districts = districts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<DistrictDTO> getDistricts() {
        return districts;
    }

    public void setDistricts(List<DistrictDTO> districts) {
        this.districts = districts;
    }
}