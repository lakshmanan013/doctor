package doctor.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "owners")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =====================================================
    // OWNER INFORMATION
    // =====================================================

    @Column(nullable = false)
    private String fullName;

    private String phone;

    private String email;

    private String address;

    private String city;

    private String state;

    private String pincode;

    private String emergencyContact;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "doctor_id")
    private Long doctorId;

    // =====================================================
    // TIMESTAMPS
    // =====================================================

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // =====================================================
    // BEFORE INSERT
    // =====================================================

    @PrePersist
    protected void onCreate() {

        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    // =====================================================
    // BEFORE UPDATE
    // =====================================================

    @PreUpdate
    protected void onUpdate() {

        updatedAt =
                LocalDateTime.now();
    }

    // =====================================================
    // GETTERS / SETTERS
    // =====================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(
            String fullName
    ) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(
            String phone
    ) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(
            String email
    ) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(
            String address
    ) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(
            String city
    ) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(
            String state
    ) {
        this.state = state;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(
            String pincode
    ) {
        this.pincode = pincode;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(
            String emergencyContact
    ) {
        this.emergencyContact =
                emergencyContact;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(
            String notes
    ) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }
}