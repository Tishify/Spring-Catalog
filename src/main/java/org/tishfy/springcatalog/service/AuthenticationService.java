package org.tishfy.springcatalog.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.tishfy.springcatalog.dto.AuthenticationRequest;
import org.tishfy.springcatalog.dto.AuthenticationResponse;
import org.tishfy.springcatalog.model.User;
import org.tishfy.springcatalog.repository.UserRepository;
import org.tishfy.springcatalog.security.CustomUserDetailService;
import org.tishfy.springcatalog.security.JwtUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailService userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getEmail(), request.getPassword()
            ));
        } catch (BadCredentialsException e) {
            return AuthenticationResponse.failure("Invalid email or password");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!Boolean.TRUE.equals(user.isEnabled())) {
            return AuthenticationResponse.failure("User account is disabled");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole()!=null? user.getRole().getRoleName() : null);

        String token = jwtUtil.generateToken(userDetails.getUsername(), claims);

        return AuthenticationResponse.successMinimal(user.getEmail(), user.getRole() != null ? user.getRole().getRoleName() : null, token);
    }
}
