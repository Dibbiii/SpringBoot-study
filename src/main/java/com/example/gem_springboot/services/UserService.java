package com.example.gem_springboot.services;

import com.example.gem_springboot.models.UserModel;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final List<UserModel> UsersDatabase = new CopyOnWriteArrayList<>();

    public UserService() {
        UsersDatabase.add(new UserModel("1", "John Doe", "john@example.com"));
        UsersDatabase.add(new UserModel("2", "Jane Tin", "jane@example.com"));
    }

    public List<UserModel> findAll() {
        return UsersDatabase;
    }

    public Optional<UserModel> findById(String id) {
        return UsersDatabase.stream()
            .filter(u -> u.getId().equals(id))
            .findFirst();
    }

    public UserModel createUser(UserModel user) {
        UsersDatabase.add(user);
        return user;
    }

    public Optional<UserModel> updateUser(UserModel user, String id) {
        return findById(id).map(existingUser -> {
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            // Non serve "salvare" di nuovo perché stiamo lavorando sull'oggetto in memoria
            return existingUser;
        });
    }

    public boolean deleteUser(String id) {
        return UsersDatabase.removeIf(u -> u.getId().equals(id));
    }
}
