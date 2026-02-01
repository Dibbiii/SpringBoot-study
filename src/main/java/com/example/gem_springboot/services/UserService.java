package com.example.gem_springboot.services;

import com.example.gem_springboot.models.dto.UserResponse;
import com.example.gem_springboot.models.entities.UserEntity;
import com.example.gem_springboot.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserResponse findAllPaginated(
        String filter,
        String sortBy,
        String order,
        int skip,
        int limit
    ) {
        int pageNumber = skip / limit; // Spring Data usa le pagine (0, 1, 2), non skip e limit diretti

        // Creo l'oggetto sort
        Sort.Direction direction = order.equalsIgnoreCase("desc")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);

        // Creo l'oggetto pageable
        Pageable pageable = PageRequest.of(pageNumber, limit, sort);

        // Eseguo la query reale sul DB
        Page<UserEntity> pageResult;

        if (filter != null && !filter.isEmpty()) {
            pageResult = userRepository.searchByText(filter, pageable);
        } else {
            // Se non c'è nessun filtro restituisco tutta la lista paginata
            pageResult = userRepository.findAll(pageable);
        }

        // Restituisco il DTO
        return new UserResponse(
            pageResult.getContent(),
            pageResult.getTotalElements()
        );
    }

    public Optional<UserEntity> findById(Long id) {
        return userRepository.findById(id);
    }

    public UserEntity createUser(UserEntity user) {
        // Controllo duplicati email
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException(
                "Email already exists: " + user.getEmail()
            );
        }
        return userRepository.save(user);
    }

    public Optional<UserEntity> updateUser(UserEntity user, Long id) {
        return userRepository
            .findById(id)
            .map(existing -> {
                existing.setUsername(user.getUsername());
                existing.setEmail(user.getEmail());
                return userRepository.save(existing);
            });
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
