package au.ca.ac.rw.enums;

public enum UserRole {
    CUSTOMER("Customer", "Regular customer who books appointments"),
    BARBER("Barber", "Service provider who performs haircuts and services"),
    ADMIN("Administrator", "System administrator with full access");

    private final String displayName;
    private final String description;

    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    // Check if role has admin privileges
    public boolean isAdmin() {
        return this == ADMIN;
    }

    // Check if role is a service provider
    public boolean isServiceProvider() {
        return this == BARBER;
    }

    // Check if role is a customer
    public boolean isCustomer() {
        return this == CUSTOMER;
    }

    // Convert from string with error handling
    public static UserRole fromString(String role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        try {
            return UserRole.valueOf(role.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid user role: " + role + 
                ". Valid values are: " + java.util.Arrays.toString(UserRole.values()));
        }
    }

    // Get all valid roles as string array
    public static String[] getValidRoles() {
        UserRole[] values = values();
        String[] result = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = values[i].name();
        }
        return result;
    }
}