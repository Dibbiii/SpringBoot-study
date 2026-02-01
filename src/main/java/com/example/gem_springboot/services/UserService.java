package com.example.gem_springboot.services;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;

import com.example.gem_springboot.models.entities.UserEntity;

@Service
public class UserService {

    private final List<UserEntity> UsersDatabase = new CopyOnWriteArrayList<>();

    public UserService() {
        UsersDatabase.add(new UserEntity(1L, "John Doe", "john@example.com"));
        UsersDatabase.add(new UserEntity(2L, "Jane Tin", "jane@example.com"));
    }
    
    public Optional<UserEntity> findById(Long id) {
        return UsersDatabase.stream()
            .filter(u -> u.getId().equals(id))
            .findFirst();
    }

    public UserEntity createUser(UserEntity user) {
        UsersDatabase.add(user);
        return user;
    }

    public Optional<UserEntity> updateUser(UserEntity user, Long id) {
        return findById(id).map(existingUser -> {
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            // Non serve "salvare" di nuovo perché stiamo lavorando sull'oggetto in memoria
            return existingUser;
        });
    }

    public boolean deleteUser(Long id) {
        return UsersDatabase.removeIf(u -> u.getId().equals(id));
    }

    public List<UserEntity> findPaginated(int skip, int limit) {
        return UsersDatabase.stream().skip(skip).limit(limit).toList();
    }

    public long count() {
        return UsersDatabase.size();
    }
}
