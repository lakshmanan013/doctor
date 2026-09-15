package doctor.backend.service;

import doctor.backend.entity.Owner;
import doctor.backend.dto.owner.OwnerRequest;
import doctor.backend.dto.owner.OwnerResponse;
import doctor.backend.exception.ResourceNotFoundException;
import doctor.backend.repository.OwnerRepository;
import doctor.backend.security.CurrentUserProvider;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnerService {

    private final OwnerRepository ownerRepository;
    private final CurrentUserProvider currentUserProvider;

    public OwnerService(OwnerRepository ownerRepository, CurrentUserProvider currentUserProvider) {
        this.ownerRepository = ownerRepository;
        this.currentUserProvider = currentUserProvider;
    }

    // Create Owner
    public OwnerResponse createOwner(OwnerRequest request) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();
        Owner owner = new Owner();

        owner.setFullName(request.getFullName());
        owner.setPhone(request.getPhone());
        owner.setEmail(request.getEmail());
        owner.setAddress(request.getAddress());
        owner.setCity(request.getCity());
        owner.setState(request.getState());
        owner.setPincode(request.getPincode());
        owner.setEmergencyContact(request.getEmergencyContact());
        owner.setNotes(request.getNotes());
        owner.setDoctorId(doctorId);

        Owner savedOwner = ownerRepository.save(owner);

        return mapToResponse(savedOwner);
    }

    // Get all Owners for logged in doctor
    public List<OwnerResponse> getAllOwners() {

        Long doctorId = currentUserProvider.getCurrentDoctorId();
        return ownerRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get Owner by ID
    public OwnerResponse getOwnerById(Long id) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();
        Owner owner = ownerRepository.findByIdAndDoctorId(id, doctorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Owner not found with id: " + id
                        )
                );

        return mapToResponse(owner);
    }

    // Update Owner
    public OwnerResponse updateOwner(
            Long id,
            OwnerRequest request) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();
        Owner owner = ownerRepository.findByIdAndDoctorId(id, doctorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Owner not found with id: " + id
                        )
                );

        owner.setFullName(request.getFullName());
        owner.setPhone(request.getPhone());
        owner.setEmail(request.getEmail());
        owner.setAddress(request.getAddress());
        owner.setCity(request.getCity());
        owner.setState(request.getState());
        owner.setPincode(request.getPincode());
        owner.setEmergencyContact(request.getEmergencyContact());
        owner.setNotes(request.getNotes());

        Owner updatedOwner = ownerRepository.save(owner);

        return mapToResponse(updatedOwner);
    }

    // Delete Owner
    public void deleteOwner(Long id) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();
        Owner owner = ownerRepository.findByIdAndDoctorId(id, doctorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Owner not found with id: " + id
                        )
                );

        ownerRepository.delete(owner);
    }

    // Convert Entity → Response DTO
    private OwnerResponse mapToResponse(Owner owner) {

        OwnerResponse response = new OwnerResponse();

        response.setId(owner.getId());
        response.setFullName(owner.getFullName());
        response.setPhone(owner.getPhone());
        response.setEmail(owner.getEmail());
        response.setAddress(owner.getAddress());
        response.setCity(owner.getCity());
        response.setState(owner.getState());
        response.setPincode(owner.getPincode());
        response.setEmergencyContact(owner.getEmergencyContact());
        response.setNotes(owner.getNotes());
        response.setDoctorId(owner.getDoctorId());
        response.setCreatedAt(owner.getCreatedAt());
        response.setUpdatedAt(owner.getUpdatedAt());

        return response;
    }
}