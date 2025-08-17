package org.tishfy.springcatalog.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tishfy.springcatalog.model.User;
import org.tishfy.springcatalog.repository.UserRepository;
import org.tishfy.springcatalog.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public User create(User user) {
        user.setUserId(null);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public Optional<User> update(Long id, User patch) {
        return userRepository.findById(id).map(u -> {
            u.setEmail(patch.getEmail());
            u.setName(patch.getName());
            u.setRole(patch.getRole());
            return userRepository.save(u);
        });
    }

    @Override
    @Transactional
    public void delete(Long id) {
        findById(id).map(user -> {
            userRepository.delete(user);
            return null;
        });

    }
}
