package au.ca.ac.rw.repository;

import au.ca.ac.rw.entity.Appointment;
import au.ca.ac.rw.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

        List<Appointment> findByCustomerId(Long customerId);

        List<Appointment> findByBarberId(Long barberId);

        List<Appointment> findByStatus(AppointmentStatus status);

        boolean existsByBarberIdAndAppointmentDateBetween(Long barberId, Date startDate, Date endDate);

        Page<Appointment> findByCustomerId(Long customerId, Pageable pageable);

        List<Appointment> findByBarberIdOrderByAppointmentDateDesc(Long barberId);

        @Query("SELECT a FROM Appointment a WHERE a.barber.id = :barberId AND a.appointmentDate BETWEEN :startDate AND :endDate")
        List<Appointment> findBarberAppointmentsInDateRange(@Param("barberId") Long barberId,
                        @Param("startDate") Date startDate, @Param("endDate") Date endDate);

        @Query("SELECT a FROM Appointment a WHERE a.barber.id = :barberId AND a.appointmentDate < :newEnd AND (a.endDate IS NULL OR a.endDate > :newStart)")
        List<Appointment> findOverlappingAppointments(@Param("barberId") Long barberId,
                        @Param("newStart") Date newStart, @Param("newEnd") Date newEnd);

        @Query("SELECT a FROM Appointment a WHERE a.customer.user.id = :userId")
        List<Appointment> findByUserId(@Param("userId") Long userId);
}
