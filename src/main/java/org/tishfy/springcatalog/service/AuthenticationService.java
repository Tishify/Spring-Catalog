package org.tishfy.springcatalog.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
@Slf4j
@Transactional
public class AuthenticationService {
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailService userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            log.debug("Attempting to authenticate user: {}", request.getEmail());

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getEmail(), request.getPassword()
            ));

            log.debug("Authentication successful for: {}", request.getEmail());
        } catch (BadCredentialsException e) {
            log.debug("Authentication failed for {}: {}", request.getEmail(), e.getMessage());
            return AuthenticationResponse.failure("Invalid email or password");
        }

        User user = userRepository.findByEmailWithRole(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (!Boolean.TRUE.equals(user.isEnabled())) {
            log.debug("User account disabled: {}", request.getEmail());
            return AuthenticationResponse.failure("User account is disabled");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        Map<String, Object> claims = new HashMap<>();
        String roleName = user.getRole() != null ? user.getRole().getRoleName() : null;
        claims.put("role", roleName);

        String token = jwtUtil.generateToken(userDetails.getUsername(), claims);

        log.debug("Generated JWT token for user: {}", user.getEmail());

        return AuthenticationResponse.successMinimal(user.getEmail(), roleName, token);
    }
}