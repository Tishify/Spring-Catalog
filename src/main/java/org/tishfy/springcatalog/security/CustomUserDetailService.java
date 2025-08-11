package org.tishfy.springcatalog.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tishfy.springcatalog.model.User;
import org.tishfy.springcatalog.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email: {}", email);

        User user = userRepository.findByEmailWithRole(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        log.debug("Found user: {}, enabled: {}", user.getEmail(), user.isEnabled());

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRole() != null && user.getRole().getRoleName() != null) {
            // Spring expects roles to be prefixed with "ROLE_"
            String roleName = "ROLE_" + user.getRole().getRoleName();
            authorities.add(new SimpleGrantedAuthority(roleName));
            log.debug("Assigned authority: {}", roleName);
        } else {
            log.warn("User {} has no role assigned", email);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash() == null ? "" : user.getPasswordHash())
                .disabled(!Boolean.TRUE.equals(user.isEnabled()))
                .authorities(authorities)
                .build();
    }
}