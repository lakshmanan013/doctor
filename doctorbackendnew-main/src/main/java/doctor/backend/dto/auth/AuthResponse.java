package doctor.backend.dto.auth;

public class AuthResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private String token;
    private String tokenType = "Bearer";

    // "PENDING" | "APPROVED" | "REJECTED". Null token + status=PENDING/REJECTED
    // means the frontend must NOT treat this as a logged-in session.
    private String approvalStatus;
    private String message;

    public AuthResponse() {
    }

    public AuthResponse(Long id, String fullName, String email, String phone, String role, String token) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.token = token;
    }

    public AuthResponse(Long id, String fullName, String email, String phone, String role, String token,
                         String approvalStatus, String message) {
        this(id, fullName, email, phone, role, token);
        this.approvalStatus = approvalStatus;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
