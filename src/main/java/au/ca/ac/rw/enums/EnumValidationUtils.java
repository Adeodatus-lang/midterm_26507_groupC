package au.ca.ac.rw.enums;

public class EnumValidationUtils {
    
    // Validate appointment status transitions
    public static boolean isValidStatusTransition(AppointmentStatus from, AppointmentStatus to) {
        if (from == null || to == null) {
            return false;
        }
        
        // Once in final state, cannot change
        if (from.isFinal()) {
            return false;
        }
        
        // Use the enhanced enum's canBeModified check
        if (!from.canBeModified()) {
            return false;
        }
        
        // Specific business rules for valid transitions using switch expression
        return switch (from) {
            case PENDING -> to == AppointmentStatus.SCHEDULED || to == AppointmentStatus.CANCELLED;
            case SCHEDULED -> to == AppointmentStatus.CONFIRMED || to == AppointmentStatus.CANCELLED;
            case CONFIRMED -> to == AppointmentStatus.IN_PROGRESS || to == AppointmentStatus.CANCELLED;
            case IN_PROGRESS -> to == AppointmentStatus.COMPLETED || to == AppointmentStatus.CANCELLED;
            default -> false;
        };
    }
    
    // Check if user can perform action based on role
    public static boolean canUserPerformAction(UserRole userRole, String action) {
        if (userRole == null || action == null) {
            return false;
        }
        
        return switch (action) {
            case "CREATE_APPOINTMENT" -> userRole.isCustomer() || userRole.isAdmin();
            case "MANAGE_APPOINTMENTS" -> userRole.isServiceProvider() || userRole.isAdmin();
            case "MANAGE_USERS" -> userRole.isAdmin();
            case "VIEW_REPORTS" -> userRole.isServiceProvider() || userRole.isAdmin();
            case "UPDATE_SERVICES" -> userRole.isServiceProvider() || userRole.isAdmin();
            case "MANAGE_BUSINESS_HOURS" -> userRole.isAdmin();
            default -> false;
        };
    }
    
    // Get valid next statuses for a given current status
    public static AppointmentStatus[] getValidNextStatuses(AppointmentStatus currentStatus) {
        if (currentStatus == null) {
            return new AppointmentStatus[0];
        }
        
        return switch (currentStatus) {
            case PENDING -> new AppointmentStatus[]{AppointmentStatus.SCHEDULED, AppointmentStatus.CANCELLED};
            case SCHEDULED -> new AppointmentStatus[]{AppointmentStatus.CONFIRMED, AppointmentStatus.CANCELLED};
            case CONFIRMED -> new AppointmentStatus[]{AppointmentStatus.IN_PROGRESS, AppointmentStatus.CANCELLED};
            case IN_PROGRESS -> new AppointmentStatus[]{AppointmentStatus.COMPLETED, AppointmentStatus.CANCELLED};
            case COMPLETED, CANCELLED, NO_SHOW -> new AppointmentStatus[0]; // No valid transitions from final states
            default -> new AppointmentStatus[0];
        };
    }
    
    // Validate if a string is a valid appointment status
    public static boolean isValidAppointmentStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return false;
        }
        try {
            AppointmentStatus.fromString(status);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    // Validate if a string is a valid user role
    public static boolean isValidUserRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return false;
        }
        try {
            UserRole.fromString(role);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}