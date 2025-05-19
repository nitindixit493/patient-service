package com.pm.patientservice.service;

import com.pm.patientservice.dto.PatientRequestDTO;
import com.pm.patientservice.dto.PatientResponseDTO;
import com.pm.patientservice.exception.EmailAlreadyExistException;
import com.pm.patientservice.exception.PatientNotFoundException;
import com.pm.patientservice.mapper.PatientMapper;
import com.pm.patientservice.model.Patient;
import com.pm.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository){
        this.patientRepository=patientRepository;
    }

    public List<PatientResponseDTO> getPatients(){
        List<Patient> allPatients = patientRepository.findAll();
        return allPatients.stream().map(PatientMapper::toDTO).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO){
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())){
         throw new EmailAlreadyExistException("Email Already Exist "+patientRequestDTO.getEmail());
        }
        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));
        return PatientMapper.toDTO(newPatient);
    }

    public PatientResponseDTO updatePatient(UUID patientId,PatientRequestDTO patientRequestDTO){

        Patient patient = patientRepository.findById(patientId).orElseThrow(
                ()->new PatientNotFoundException("Patient Not Found"+patientId));

        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(),patientId)){
            throw new EmailAlreadyExistException("Email Already Exist "+patientRequestDTO.getEmail());
        }
        patient.setEmail(patientRequestDTO.getEmail());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setName(patientRequestDTO.getName());
        Patient newPatient = patientRepository.save(patient);
        return PatientMapper.toDTO(newPatient);
    }

    public void deletePatient(UUID patientId){
        patientRepository.deleteById(patientId);
    }
}
