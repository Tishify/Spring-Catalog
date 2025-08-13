    package org.tishfy.springcatalog.service;


    import org.springframework.data.crossstore.ChangeSetPersister;
    import org.tishfy.springcatalog.model.User;

    import java.util.List;
    import java.util.Optional;

    public interface UserService {
        List<User> findAll();
        Optional<User> findById(Long id);
        User create(User user);
        Optional<User> update(Long id, User user) throws ChangeSetPersister.NotFoundException;
        void delete(Long id) throws ChangeSetPersister.NotFoundException;
    }
