package au.ca.ac.rw.dto;

import java.util.List;
import java.util.ArrayList;

public class SectorDTO {
    private Long id;
    private String name;
    private String code;
    /** Children using new Location model (cells) */
    private List<CellDTO> cells = new ArrayList<>();

    public SectorDTO() {
    }

    public SectorDTO(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public SectorDTO(Long id, String name, String code, List<CellDTO> cells) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.cells = cells;
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

    public List<CellDTO> getCells() {
        return cells;
    }

    public void setCells(List<CellDTO> cells) {
        this.cells = cells;
    }
}