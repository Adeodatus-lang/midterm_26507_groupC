package au.ca.ac.rw.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "location", uniqueConstraints = @UniqueConstraint(name = "uk_location_code_type", columnNames = { "code",
        "type" }))
public class Location {

    @PrePersist
    @PreUpdate
    public void validateHierarchy() {
        if (type == null) {
            throw new IllegalStateException("Location type cannot be null");
        }

        if (type == LocationType.PROVINCE && parent != null) {
            throw new IllegalStateException("Province cannot have a parent location");
        }

        if (parent != null) {
            switch (type) {
                case DISTRICT -> {
                    if (parent.getType() != LocationType.PROVINCE) {
                        throw new IllegalStateException("District must have a Province as parent");
                    }
                }
                case SECTOR -> {
                    if (parent.getType() != LocationType.DISTRICT) {
                        throw new IllegalStateException("Sector must have a District as parent");
                    }
                }
                case CELL -> {
                    if (parent.getType() != LocationType.SECTOR) {
                        throw new IllegalStateException("Cell must have a Sector as parent");
                    }
                }
                case VILLAGE -> {
                    if (parent.getType() != LocationType.CELL) {
                        throw new IllegalStateException("Village must have a Cell as parent");
                    }
                }
                default -> {
                }
            }
        } else if (type != LocationType.PROVINCE) {
            throw new IllegalStateException(type + " must have a parent location");
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Location parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Location> children;

    private LocalDateTime createdAt = LocalDateTime.now();

    public enum LocationType {
        PROVINCE, DISTRICT, SECTOR, CELL, VILLAGE
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

    public LocationType getType() {
        return type;
    }

    public void setType(LocationType type) {
        this.type = type;
    }

    public Location getParent() {
        return parent;
    }

    public void setParent(Location parent) {
        this.parent = parent;
    }

    public List<Location> getChildren() {
        return children;
    }

    public void setChildren(List<Location> children) {
        this.children = children;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Location{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", type=" + type +
                ", parent=" + (parent != null ? parent.getName() : "null") +
                ", createdAt=" + createdAt +
                '}';
    }
}