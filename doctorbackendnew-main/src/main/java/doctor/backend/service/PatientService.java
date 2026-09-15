package doctor.backend.service;

import doctor.backend.dto.patient.PatientRequest;
import doctor.backend.dto.patient.PatientResponse;
import doctor.backend.entity.Owner;
import doctor.backend.entity.Patient;
import doctor.backend.exception.ResourceNotFoundException;
import doctor.backend.repository.OwnerRepository;
import doctor.backend.repository.PatientRepository;
import doctor.backend.security.CurrentUserProvider;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final OwnerRepository ownerRepository;
    private final CurrentUserProvider currentUserProvider;

    public PatientService(
            PatientRepository patientRepository,
            OwnerRepository ownerRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.patientRepository = patientRepository;
        this.ownerRepository = ownerRepository;
        this.currentUserProvider = currentUserProvider;
    }

    // =====================================================
    // CREATE PATIENT
    // =====================================================

    public PatientResponse createPatient(
            PatientRequest request
    ) {

        validatePatientRequest(request);

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        // -------------------------------------------------
        // FIND OWNER (belonging to current doctor)
        // -------------------------------------------------

        Owner owner = ownerRepository
                .findByIdAndDoctorId(request.getOwnerId(), doctorId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Owner not found with id: "
                                        + request.getOwnerId()
                        )
                );

        // -------------------------------------------------
        // CREATE PATIENT
        // -------------------------------------------------

        Patient patient = new Patient();

        mapRequestToPatient(
                request,
                patient
        );

        patient.setOwner(owner);
        patient.setDoctorId(doctorId);

        // =================================================
        // GENERATE PET ID BEFORE SAVE
        // =================================================

        String petId = generatePetId(
                patient.getSpecies()
        );

        patient.setPetId(petId);

        // =================================================
        // SAVE ONLY ONCE
        // =================================================

        Patient savedPatient =
                patientRepository.save(patient);

        return mapToResponse(savedPatient);
    }

    // =====================================================
    // SPECIES → PET ID PREFIX
    // =====================================================

    private String getSpeciesPrefix(
            String species
    ) {

        if (species == null ||
                species.isBlank()) {

            return "PET";
        }

        return switch (
                species.trim().toLowerCase()
                ) {

            case "dog" ->
                    "DOG";

            case "cat" ->
                    "CAT";

            case "buffalo" ->
                    "BUF";

            case "goat" ->
                    "GOA";

            case "cattle" ->
                    "CTL";

            case "rabbit" ->
                    "RAB";

            case "parrot" ->
                    "PAR";

            default ->
                    "PET";
        };
    }

    // =====================================================
    // GENERATE SPECIES-SPECIFIC PET ID
    // =====================================================

    private String generatePetId(
            String species
    ) {

        String prefix =
                getSpeciesPrefix(species);

        // -------------------------------------------------
        // Find latest Pet ID for this species
        // -------------------------------------------------

        Optional<Patient> latestPatient =
                patientRepository
                        .findTopBySpeciesIgnoreCaseAndPetIdIsNotNullOrderByPetIdDesc(
                                species
                        );

        int nextNumber = 1;

        // -------------------------------------------------
        // Calculate next number
        // -------------------------------------------------

        if (latestPatient.isPresent()) {

            String previousPetId =
                    latestPatient
                            .get()
                            .getPetId();

            if (previousPetId != null &&
                    !previousPetId.isBlank()) {

                String expectedPrefix =
                        prefix + "-";

                if (previousPetId.startsWith(
                        expectedPrefix
                )) {

                    String numberPart =
                            previousPetId.substring(
                                    expectedPrefix.length()
                            );

                    try {

                        int previousNumber =
                                Integer.parseInt(
                                        numberPart
                                );

                        nextNumber =
                                previousNumber + 1;

                    } catch (
                            NumberFormatException exception
                    ) {

                        nextNumber = 1;
                    }
                }
            }
        }

        // -------------------------------------------------
        // Create Pet ID
        // -------------------------------------------------

        String petId = String.format(
                "%s-%04d",
                prefix,
                nextNumber
        );

        // -------------------------------------------------
        // Extra safety check
        // -------------------------------------------------

        while (
                patientRepository.existsByPetId(
                        petId
                )
        ) {

            nextNumber++;

            petId = String.format(
                    "%s-%04d",
                    prefix,
                    nextNumber
            );
        }

        return petId;
    }

    // =====================================================
    // GET ALL PATIENTS
    // =====================================================

    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatients() {

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return patientRepository
                .findByDoctorId(doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET PATIENT BY DATABASE ID
    // =====================================================

    @Transactional(readOnly = true)
    public PatientResponse getPatientById(
            Long id
    ) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        Patient patient =
                patientRepository
                        .findByIdAndDoctorId(id, doctorId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Patient not found with id: "
                                                + id
                                )
                        );

        return mapToResponse(patient);
    }

    // =====================================================
    // GET PATIENT BY PET ID
    // =====================================================

    @Transactional(readOnly = true)
    public PatientResponse getPatientByPetId(
            String petId
    ) {

        if (petId == null ||
                petId.isBlank()) {

            throw new RuntimeException(
                    "Pet ID is required"
            );
        }

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        Patient patient =
                patientRepository
                        .findByPetIdAndDoctorId(
                                petId.trim(),
                                doctorId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Patient not found with Pet ID: "
                                                + petId
                                )
                        );

        return mapToResponse(patient);
    }

    // =====================================================
    // GET PATIENTS BY OWNER
    // =====================================================

    @Transactional(readOnly = true)
    public List<PatientResponse> getPatientsByOwner(
            Long ownerId
    ) {

        if (ownerId == null) {

            throw new RuntimeException(
                    "Owner ID is required"
            );
        }

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        if (!ownerRepository.existsByIdAndDoctorId(ownerId, doctorId)) {

            throw new ResourceNotFoundException(
                    "Owner not found with id: "
                            + ownerId
            );
        }

        return patientRepository
                .findByOwnerIdAndDoctorId(ownerId, doctorId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // SEARCH PATIENTS
    // =====================================================

    @Transactional(readOnly = true)
    public List<PatientResponse> searchPatients(
            String name
    ) {

        if (name == null ||
                name.isBlank()) {

            return getAllPatients();
        }

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return patientRepository
                .findByNameContainingIgnoreCaseAndDoctorId(
                        name.trim(),
                        doctorId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY SPECIES
    // =====================================================

    @Transactional(readOnly = true)
    public List<PatientResponse> getPatientsBySpecies(
            String species
    ) {

        if (species == null ||
                species.isBlank()) {

            return getAllPatients();
        }

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return patientRepository
                .findBySpeciesAndDoctorId(
                        species.trim(),
                        doctorId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // GET BY STATUS
    // =====================================================

    @Transactional(readOnly = true)
    public List<PatientResponse> getPatientsByStatus(
            String status
    ) {

        if (status == null ||
                status.isBlank()) {

            return getAllPatients();
        }

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        return patientRepository
                .findByStatusAndDoctorId(
                        status.trim(),
                        doctorId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // =====================================================
    // UPDATE PATIENT
    // =====================================================

    public PatientResponse updatePatient(
            Long id,
            PatientRequest request
    ) {

        validatePatientRequest(request);

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        Patient patient =
                patientRepository
                        .findByIdAndDoctorId(id, doctorId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Patient not found with id: "
                                                + id
                                )
                        );

        Owner owner =
                ownerRepository
                        .findByIdAndDoctorId(
                                request.getOwnerId(),
                                doctorId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Owner not found with id: "
                                                + request.getOwnerId()
                                )
                        );

        // -------------------------------------------------
        // IMPORTANT
        // -------------------------------------------------
        // Pet ID is NEVER regenerated during update.
        //
        // DOG-0001 remains DOG-0001
        // CAT-0001 remains CAT-0001
        // -------------------------------------------------

        mapRequestToPatient(
                request,
                patient
        );

        patient.setOwner(owner);

        Patient updatedPatient =
                patientRepository.save(patient);

        return mapToResponse(updatedPatient);
    }

    // =====================================================
    // DELETE PATIENT
    // =====================================================

    public void deletePatient(
            Long id
    ) {

        Long doctorId = currentUserProvider.getCurrentDoctorId();

        Patient patient =
                patientRepository
                        .findByIdAndDoctorId(id, doctorId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Patient not found with id: "
                                                + id
                                )
                        );

        patientRepository.delete(patient);
    }

    // =====================================================
    // VALIDATE
    // =====================================================

    private void validatePatientRequest(
            PatientRequest request
    ) {

        if (request == null) {

            throw new RuntimeException(
                    "Patient request cannot be null"
            );
        }

        if (request.getName() == null ||
                request.getName().isBlank()) {

            throw new RuntimeException(
                    "Patient name is required"
            );
        }

        if (request.getSpecies() == null ||
                request.getSpecies().isBlank()) {

            throw new RuntimeException(
                    "Patient species is required"
            );
        }

        if (request.getOwnerId() == null) {

            throw new RuntimeException(
                    "Owner ID is required"
            );
        }

        if (request.getWeight() != null &&
                request.getWeight() < 0) {

            throw new RuntimeException(
                    "Patient weight cannot be negative"
            );
        }
    }

    // =====================================================
    // REQUEST → ENTITY
    // =====================================================

    private void mapRequestToPatient(
            PatientRequest request,
            Patient patient
    ) {

        patient.setName(
                clean(request.getName())
        );

        patient.setSpecies(
                clean(request.getSpecies())
        );

        patient.setBreed(
                clean(request.getBreed())
        );

        patient.setGender(
                clean(request.getGender())
        );

        patient.setDateOfBirth(
                request.getDateOfBirth()
        );

        patient.setWeight(
                request.getWeight()
        );

        patient.setColor(
                clean(request.getColor())
        );

        patient.setMicrochipNumber(
                clean(request.getMicrochipNumber())
        );

        patient.setMedicalAlerts(
                clean(request.getMedicalAlerts())
        );

        patient.setStatus(
                request.getStatus() == null ||
                        request.getStatus().isBlank()
                        ? "ACTIVE"
                        : request.getStatus().trim()
        );

        patient.setNotes(
                clean(request.getNotes())
        );

        patient.setIcon(
                request.getIcon() == null ||
                        request.getIcon().isBlank()
                        ? getDefaultIcon(
                        request.getSpecies()
                )
                        : request.getIcon()
        );
    }

    // =====================================================
    // ENTITY → RESPONSE
    // =====================================================

    private PatientResponse mapToResponse(
            Patient patient
    ) {

        PatientResponse response =
                new PatientResponse();

        response.setId(
                patient.getId()
        );

        // IMPORTANT:
        // Send generated Pet ID to frontend
        response.setPetId(
                patient.getPetId()
        );

        response.setName(
                patient.getName()
        );

        response.setSpecies(
                patient.getSpecies()
        );

        response.setBreed(
                patient.getBreed()
        );

        response.setGender(
                patient.getGender()
        );

        response.setDateOfBirth(
                patient.getDateOfBirth()
        );

        response.setWeight(
                patient.getWeight()
        );

        response.setColor(
                patient.getColor()
        );

        response.setMicrochipNumber(
                patient.getMicrochipNumber()
        );

        response.setMedicalAlerts(
                patient.getMedicalAlerts()
        );

        response.setStatus(
                patient.getStatus()
        );

        response.setNotes(
                patient.getNotes()
        );

        response.setIcon(
                patient.getIcon()
        );

        response.setDoctorId(
                patient.getDoctorId()
        );

        // -------------------------------------------------
        // OWNER
        // -------------------------------------------------

        if (patient.getOwner() != null) {

            response.setOwnerId(
                    patient.getOwner().getId()
            );

            response.setOwnerName(
                    patient.getOwner().getFullName()
            );

            response.setOwnerPhone(
                    patient.getOwner().getPhone()
            );

            response.setOwnerEmail(
                    patient.getOwner().getEmail()
            );

            response.setOwnerAddress(
                    patient.getOwner().getAddress()
            );

            response.setOwnerCity(
                    patient.getOwner().getCity()
            );

            response.setOwnerState(
                    patient.getOwner().getState()
            );

            response.setOwnerPincode(
                    patient.getOwner().getPincode()
            );
        }

        // -------------------------------------------------
        // TIMESTAMPS
        // -------------------------------------------------

        response.setCreatedAt(
                patient.getCreatedAt()
        );

        response.setUpdatedAt(
                patient.getUpdatedAt()
        );

        return response;
    }

    // =====================================================
    // CLEAN STRING
    // =====================================================

    private String clean(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String cleaned =
                value.trim();

        return cleaned.isEmpty()
                ? null
                : cleaned;
    }

    // =====================================================
    // DEFAULT ICON
    // =====================================================

    private String getDefaultIcon(
            String species
    ) {

        if (species == null ||
                species.isBlank()) {

            return "🐾";
        }

        return switch (
                species.trim().toLowerCase()
                ) {

            case "dog" ->
                    "🐶";

            case "cat" ->
                    "🐱";

            case "buffalo" ->
                    "🐃";

            case "goat" ->
                    "🐐";

            case "cattle" ->
                    "🐄";

            case "rabbit" ->
                    "🐰";

            case "parrot" ->
                    "🦜";

            default ->
                    "🐾";
        };
    }
}