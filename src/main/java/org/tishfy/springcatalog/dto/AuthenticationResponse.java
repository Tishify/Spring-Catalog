package org.tishfy.springcatalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.tishfy.springcatalog.model.User;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String email;
    private String role;
    private String token;
    private boolean success;
    private String message;

    public static AuthenticationResponse successMinimal(String email, String role, String token) {
        return AuthenticationResponse.builder()
                .email(email)
                .role(role)
                .token(token)
                .success(true)
                .message("Authentication successful")
                .build();
    }

    public static AuthenticationResponse failure(String message) {
        return AuthenticationResponse.builder()
                .success(false)
                .message(message)
                .build();
    }

    public boolean isAdmin() {
        return role != null && "ADMIN".equalsIgnoreCase(role);
    }
}