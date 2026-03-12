package au.ca.ac.rw.dto;

public class LocationRequest {
    private String name;
    private String code;
    private Long parentId;

    public LocationRequest() {}

    public LocationRequest(String name, String code, Long parentId) {
        this.name = name;
        this.code = code;
        this.parentId = parentId;
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }
}
