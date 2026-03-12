package au.ca.ac.rw.enums;

public enum AppointmentStatus {
    PENDING("Pending", "Appointment is waiting for confirmation"),
    SCHEDULED("Scheduled", "Appointment is confirmed and scheduled"),
    CONFIRMED("Confirmed", "Customer has confirmed the appointment"),
    IN_PROGRESS("In Progress", "Service is currently being performed"),
    COMPLETED("Completed", "Service has been completed successfully"),
    CANCELLED("Cancelled", "Appointment was cancelled"),
    NO_SHOW("No Show", "Customer did not show up for appointment");

    private final String displayName;
    private final String description;

    AppointmentStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    // Helper method to check if status allows modifications
    public boolean canBeModified() {
        return this == PENDING || this == SCHEDULED || this == CONFIRMED;
    }

    // Helper method to check if status is active
    public boolean isActive() {
        return this == PENDING || this == SCHEDULED || this == CONFIRMED || this == IN_PROGRESS;
    }

    // Helper method to check if status is final
    public boolean isFinal() {
        return this == COMPLETED || this == CANCELLED || this == NO_SHOW;
    }

    // Convert from string with error handling
    public static AppointmentStatus fromString(String status) {
        if (status == null) {
            return PENDING; // Default value
        }
        try {
            return AppointmentStatus.valueOf(status.toUpperCase().trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid appointment status: " + status + 
                ". Valid values are: " + java.util.Arrays.toString(AppointmentStatus.values()));
        }
    }

    // Get all valid status values as string array
    public static String[] getValidStatuses() {
        AppointmentStatus[] values = values();
        String[] result = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = values[i].name();
        }
        return result;
    }
}