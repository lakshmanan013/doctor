package doctor.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "doctor_profile")
public class DoctorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The doctor (users.id) this profile belongs to. One row per doctor -
    // previously every doctor shared the single row with id=1, which is the
    // bug this column fixes. Not marked NOT NULL for the same reason other
    // evolving columns on this schema aren't (hibernate ddl-auto=update
    // can't add a NOT NULL column to a table that may already have rows);
    // application code always sets it and only ever looks rows up by it.
    @Column(name = "user_id", unique = true)
    private Long userId;

    private String fullName;

    private String qualification;

    private String speciality;

    private String city;

    private Integer pincode;

    private Integer experience;



    private String councilRegistration;

    private String clinicHospital;

    private String phone;

    private String email;

    private String digitalSignatureName;

    private Double consultationFee;

    private Double followUpFee;

    private Integer slotLength;

    // =========================
    // Verification flags
    // Set by the Zenve admin backend when an admin clicks "verify" on the
    // corresponding item (see InternalDoctorStatusController#verify). Never
    // set by the doctor's own profile edits (saveProfile() does not touch these).
    // =========================

    @Column(columnDefinition = "boolean default false")
    private boolean veterinaryRegistrationVerified = false;

    @Column(columnDefinition = "boolean default false")
    private boolean kycVerified = false;

    @Column(columnDefinition = "boolean default false")
    private boolean digitalSignatureVerified = false;

    @Column(columnDefinition = "boolean default false")
    private boolean stateCouncilSyncVerified = false;

    public DoctorProfile() {
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getCouncilRegistration() {
        return councilRegistration;
    }

    public void setCouncilRegistration(String councilRegistration) {
        this.councilRegistration = councilRegistration;
    }

    public String getClinicHospital() {
        return clinicHospital;
    }

    public void setClinicHospital(String clinicHospital) {
        this.clinicHospital = clinicHospital;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDigitalSignatureName() {
        return digitalSignatureName;
    }

    public void setDigitalSignatureName(String digitalSignatureName) {
        this.digitalSignatureName = digitalSignatureName;
    }

    public Double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(Double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public Double getFollowUpFee() {
        return followUpFee;
    }

    public void setFollowUpFee(Double followUpFee) {
        this.followUpFee = followUpFee;
    }

    public Integer getSlotLength() {
        return slotLength;
    }

    public void setSlotLength(Integer slotLength) {
        this.slotLength = slotLength;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getPincode() {
        return pincode;
    }

    public void setPincode(Integer pincode) {
        this.pincode = pincode;
    }



    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public boolean isVeterinaryRegistrationVerified() {
        return veterinaryRegistrationVerified;
    }

    public void setVeterinaryRegistrationVerified(boolean veterinaryRegistrationVerified) {
        this.veterinaryRegistrationVerified = veterinaryRegistrationVerified;
    }

    public boolean isKycVerified() {
        return kycVerified;
    }

    public void setKycVerified(boolean kycVerified) {
        this.kycVerified = kycVerified;
    }

    public boolean isDigitalSignatureVerified() {
        return digitalSignatureVerified;
    }

    public void setDigitalSignatureVerified(boolean digitalSignatureVerified) {
        this.digitalSignatureVerified = digitalSignatureVerified;
    }

    public boolean isStateCouncilSyncVerified() {
        return stateCouncilSyncVerified;
    }

    public void setStateCouncilSyncVerified(boolean stateCouncilSyncVerified) {
        this.stateCouncilSyncVerified = stateCouncilSyncVerified;
    }
}