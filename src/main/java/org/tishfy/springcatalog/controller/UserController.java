package org.tishfy.springcatalog.controller;



import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.tishfy.springcatalog.model.User;
import org.tishfy.springcatalog.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping
    public List<User> getUsers() {
        return service.findAll();
    }


    @GetMapping("/{id}")
    public Optional<User> get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) {
        return service.create(user);
    }

    @PutMapping("/{id}")
    public Optional<User> update(@PathVariable Long id, @RequestBody User user) throws ChangeSetPersister.NotFoundException {
        return service.update(id, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) throws ChangeSetPersister.NotFoundException { service.delete(id); }
}
