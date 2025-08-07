package org.tishfy.springcatalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tishfy.springcatalog.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole_RoleName(String roleName);

    List<User> findAllByOrderByName();
}
