package doctor.backend.service;

import doctor.backend.entity.DoctorProfile;
import doctor.backend.repository.DoctorProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class DoctorProfileService {

    private final DoctorProfileRepository repository;
    private final ZippyCrmSyncService zippyCrmSyncService;

    public DoctorProfileService(
            DoctorProfileRepository repository,
            ZippyCrmSyncService zippyCrmSyncService) {
        this.repository = repository;
        this.zippyCrmSyncService = zippyCrmSyncService;
    }

    /**
     * Returns the profile belonging to the given doctor, creating an empty
     * one on first access. userId must come from the authenticated
     * request (CurrentUserProvider / the internal endpoint's email lookup)
     * - never from client input.
     */
    public DoctorProfile getProfile(Long userId) {

        return repository
                .findByUserId(userId)
                .orElseGet(() -> {
                    DoctorProfile profile = new DoctorProfile();
                    profile.setUserId(userId);

                    return repository.save(profile);
                });
    }

    public DoctorProfile saveProfile(
            Long userId,
            DoctorProfile profile) {

        DoctorProfile existing = repository
                .findByUserId(userId)
                .orElseGet(DoctorProfile::new);

        existing.setUserId(userId);

        existing.setFullName(
                profile.getFullName());

        existing.setQualification(
                profile.getQualification());

        existing.setSpeciality(
                profile.getSpeciality());

        existing.setCouncilRegistration(
                profile.getCouncilRegistration());

        existing.setClinicHospital(
                profile.getClinicHospital());

        existing.setPhone(
                profile.getPhone());

        existing.setEmail(
                profile.getEmail());

        existing.setDigitalSignatureName(
                profile.getDigitalSignatureName());

        existing.setConsultationFee(
                profile.getConsultationFee());

        existing.setFollowUpFee(
                profile.getFollowUpFee());

        existing.setSlotLength(
                profile.getSlotLength());

        existing.setCity(
                profile.getCity());

        existing.setPincode(
                profile.getPincode());

        existing.setExperience(
                profile.getExperience());

        DoctorProfile saved = repository.save(existing);
        zippyCrmSyncService.syncDoctor(saved);
        return saved;
    }

    /**
     * Called by the internal endpoint the Zenve admin backend hits when an
     * admin clicks "verify" on one of the Verification panel items, for the
     * specific doctor (userId) the admin was looking at.
     */
    public DoctorProfile applyVerification(Long userId, String item) {

        DoctorProfile existing = repository
                .findByUserId(userId)
                .orElseGet(() -> {
                    DoctorProfile profile = new DoctorProfile();
                    profile.setUserId(userId);
                    return profile;
                });

        switch (item) {
            case "VETERINARY_REGISTRATION" ->
                existing.setVeterinaryRegistrationVerified(true);
            case "KYC" ->
                existing.setKycVerified(true);
            case "DIGITAL_SIGNATURE" ->
                existing.setDigitalSignatureVerified(true);
            case "STATE_COUNCIL_SYNC" ->
                existing.setStateCouncilSyncVerified(true);
            default ->
                throw new doctor.backend.exception.BadRequestException(
                        "Unknown verification item: " + item);
        }

        DoctorProfile saved = repository.save(existing);
        zippyCrmSyncService.syncDoctor(saved);
        return saved;
    }
}
