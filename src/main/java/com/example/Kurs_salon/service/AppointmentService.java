package com.example.Kurs_salon.service;

import com.example.Kurs_salon.dto.AppointmentCreateDTO;
import com.example.Kurs_salon.model.Appointment;
import com.example.Kurs_salon.model.Master;
import com.example.Kurs_salon.model.Servicee;
import com.example.Kurs_salon.model.User;
import com.example.Kurs_salon.repository.AppointmentRepository;
import com.example.Kurs_salon.repository.MasterRepository;
import com.example.Kurs_salon.repository.ServiceRepository;
import com.example.Kurs_salon.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final MasterRepository masterRepository;
    private final ServiceRepository serviceRepository;

    public Appointment createAppointment(AppointmentCreateDTO dto, User currentUser) {
        Master master = masterRepository.findById(dto.getMasterId())
                .orElseThrow(() -> new EntityNotFoundException("Мастер не найден"));

        Servicee service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new EntityNotFoundException("Услуга не найдена"));

        Appointment appointment = new Appointment();
        appointment.setUser(currentUser);
        appointment.setMaster(master);
        appointment.setServicee(service);
        appointment.setAppointmentDate(dto.getAppointmentDate());
        appointment.setStatus("В обработке");

        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getMasterSchedule(Long masterId, LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByMasterIdAndAppointmentDateBetween(masterId, start, end);
    }

    public List<Appointment> getUserAppointments(Long userId) {
        return appointmentRepository.findByUserId(userId);
    }
    @Transactional
    public Appointment cancelAppointment(Long appointmentId, String username) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));

        if (appointment.getStatus().equals("COMPLETED")) {
            throw new IllegalStateException("Cannot cancel a completed appointment");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!appointment.getUser().equals(user) && !appointment.getMaster().equals(user)) {
            throw new AccessDeniedException("You don't have permission to cancel this appointment");
        }

        appointment.setStatus("CANCELED");
        return appointmentRepository.save(appointment);
    }
}