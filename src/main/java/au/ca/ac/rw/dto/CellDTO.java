package au.ca.ac.rw.dto;

import java.util.List;
import java.util.ArrayList;

public class CellDTO {
    private Long id;
    private String name;
    private String code;
    /** Children using new Location model (villages) */
    private List<VillageDTO> villages = new ArrayList<>();

    public CellDTO() {
    }

    public CellDTO(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public CellDTO(Long id, String name, String code, List<VillageDTO> villages) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.villages = villages;
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

    public List<VillageDTO> getVillages() {
        return villages;
    }

    public void setVillages(List<VillageDTO> villages) {
        this.villages = villages;
    }
}