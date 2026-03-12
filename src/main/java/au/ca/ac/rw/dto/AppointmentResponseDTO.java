package au.ca.ac.rw.dto;

import au.ca.ac.rw.enums.AppointmentStatus;

import java.util.Date;

public class AppointmentResponseDTO {
    private Long id;
    private Long barberId;
    private Long customerId;
    private Long serviceId;
    private String serviceName;
    private Date appointmentDate;
    private Date endDate;
    private String appointmentTime;
    private AppointmentStatus status;
    private String notes;
    private Double price;

    public AppointmentResponseDTO() {}

    public AppointmentResponseDTO(Long id, Long barberId, Long customerId, Long serviceId,
            String serviceName, Date appointmentDate, Date endDate, String appointmentTime,
            AppointmentStatus status, String notes, Double price) {
        this.id = id;
        this.barberId = barberId;
        this.customerId = customerId;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.appointmentDate = appointmentDate;
        this.endDate = endDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.notes = notes;
        this.price = price;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBarberId() {
        return barberId;
    }

    public void setBarberId(Long barberId) {
        this.barberId = barberId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
