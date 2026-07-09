package ca.udem.maville.security;

// public class AuthResponse {
//     private String email;
//     private String role;
//     private String userId;   // resident ID (Long) or provider companyNumber (String)
    
//     public AuthResponse() {}
//     public AuthResponse(String email, String role, String userId) {
//         this.email = email;
//         this.role = role;
//         this.userId = userId;
//     }
//     // getters and setters
//     public String getEmail() { return email; }
//     public void setEmail(String email) { this.email = email; }
//     public String getRole() { return role; }
//     public void setRole(String role) { this.role = role; }
//     public String getUserId() { return userId; }
//     public void setUserId(String userId) { this.userId = userId; }
// }

public class JwtAuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String email;
    private String role;
    private String userId;

    public JwtAuthResponse() {}

    public JwtAuthResponse(String accessToken, String email, String role, String userId) {
        this.accessToken = accessToken;
        this.email = email;
        this.role = role;
        this.userId = userId;
    }

    public String getAccessToken() {
        return accessToken;
    }
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    public String getTokenType() {
        return tokenType;
    }
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
}

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
}