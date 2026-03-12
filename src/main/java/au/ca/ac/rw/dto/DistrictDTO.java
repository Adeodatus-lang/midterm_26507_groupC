package au.ca.ac.rw.dto;

import java.util.List;
import java.util.ArrayList;

public class DistrictDTO {
    private Long id;
    private String name;
    private String code;
    /** Children using new Location model (sectors) */
    private List<SectorDTO> sectors = new ArrayList<>();

    public DistrictDTO() {
    }

    public DistrictDTO(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public DistrictDTO(Long id, String name, String code, List<SectorDTO> sectors) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.sectors = sectors;
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

    public List<SectorDTO> getSectors() {
        return sectors;
    }

    public void setSectors(List<SectorDTO> sectors) {
        this.sectors = sectors;
    }
}