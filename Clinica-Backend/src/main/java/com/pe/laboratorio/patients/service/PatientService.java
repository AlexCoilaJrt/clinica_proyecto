package com.pe.laboratorio.patients.service;

import com.pe.laboratorio.patients.dto.PatientDTO;
import com.pe.laboratorio.patients.entity.Patient;
import com.pe.laboratorio.patients.repository.PatientRepository;
import com.pe.laboratorio.exception.ResourceNotFoundException;
import com.pe.laboratorio.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    private PatientDTO mapToDTO(Patient patient) {
        return PatientDTO.builder()
                .id(patient.getId())
                .firstName(patient.getFirstName())
                .lastName(patient.getLastName())
                .dni(patient.getDni())
                .email(patient.getEmail())
                .dateOfBirth(patient.getDateOfBirth())
                .gender(patient.getGender())
                .phone(patient.getPhone())
                .address(patient.getAddress())
                .build();
    }

    private Patient mapToEntity(PatientDTO dto) {
        return Patient.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .dni(dto.getDni())
                .email(dto.getEmail())
                .dateOfBirth(dto.getDateOfBirth())
                .gender(dto.getGender())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .build();
    }

    public PatientDTO createPatient(PatientDTO dto) {
        if (patientRepository.findByDni(dto.getDni()).isPresent()) {
            throw new ValidationException("Ya existe un paciente con el DNI: " + dto.getDni());
        }
        Patient newPatient = mapToEntity(dto);
        Patient savedPatient = patientRepository.save(newPatient);
        return mapToDTO(savedPatient);
    }

    public List<PatientDTO> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public PatientDTO getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));
        return mapToDTO(patient);
    }

    public PatientDTO updatePatient(Long id, PatientDTO dto) {
        Patient existingPatient = patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + id));

        if (!existingPatient.getDni().equals(dto.getDni()) && patientRepository.findByDni(dto.getDni()).isPresent()) {
            throw new ValidationException("Ya existe otro paciente con el DNI: " + dto.getDni());
        }

        existingPatient.setFirstName(dto.getFirstName());
        existingPatient.setLastName(dto.getLastName());
        existingPatient.setDni(dto.getDni());
        existingPatient.setEmail(dto.getEmail());
        existingPatient.setDateOfBirth(dto.getDateOfBirth());
        existingPatient.setGender(dto.getGender());
        existingPatient.setPhone(dto.getPhone());
        existingPatient.setAddress(dto.getAddress());

        Patient updatedPatient = patientRepository.save(existingPatient);
        return mapToDTO(updatedPatient);
    }

    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Paciente no encontrado con ID: " + id);
        }
        patientRepository.deleteById(id);
    }
}