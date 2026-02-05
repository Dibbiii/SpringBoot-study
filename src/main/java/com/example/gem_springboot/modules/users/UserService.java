package com.example.gem_springboot.modules.users;

import com.example.gem_springboot.modules.users.UserFilter;
import com.example.gem_springboot.modules.users.internal.UserEntity;
import com.example.gem_springboot.modules.users.internal.UserMapper;
import com.example.gem_springboot.modules.users.internal.UserRepository;
import com.example.gem_springboot.modules.users.internal.UserSpecifications;
import com.example.gem_springboot.shared.DuplicateResourceException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher events;

    public UsersList findAllPaginated(
        UserFilter filter,
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

        // Costruisco la specification dinamica
        Specification<UserEntity> spec = UserSpecifications.withFilter(filter);

        // Il Repository accetta Specification e Pageable grazie all'interfaccia aggiunta
        Page<UserEntity> pageResult = userRepository.findAll(spec, pageable);

        List<UserResponse> dtos = pageResult
            .getContent()
            .stream()
            .map(userMapper::toDto)
            .toList();

        return new UsersList(dtos, pageResult.getTotalElements());
    }

    @Cacheable(value = "users", key = "#id")
    public Optional<UserResponse> findById(Long id) {
        return userRepository.findById(id).map(userMapper::toDto);
    }

    @Transactional // Garantisce che l'operazione sia atomica
    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new DuplicateResourceException(
                "Username already exists: " + request.username()
            );
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateResourceException(
                "Email already exists: " + request.email()
            );
        }
        UserEntity entity = userMapper.toEntity(request);
        entity.setPassword(passwordEncoder.encode(request.password()));
        entity = userRepository.save(entity);

        events.publishEvent(
            new UserCreatedEvent(
                entity.getId(),
                entity.getEmail(),
                entity.getUsername()
            )
        );

        // Converto in Dto
        return userMapper.toDto(entity);
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id") // Cancella l'utente modificato dalla cache
    public Optional<UserResponse> updateUser(UserRequest request, Long id) {
        return userRepository
            .findById(id)
            // MapStruct prende i dati da 'request' e li copia dentro 'existingUser'
            // Solo i campi che matchano (username, email, password) vengono aggiornati.
            .map(existingUser -> {
                // Controllo username solo se è cambiato
                if (
                    !existingUser.getUsername().equals(request.username()) &&
                    userRepository.existsByUsername(request.username())
                ) {
                    throw new DuplicateResourceException(
                        "Username already exists: " + request.username()
                    );
                }

                // Controllo email solo se è cambiata
                if (
                    !existingUser.getEmail().equals(request.email()) &&
                    userRepository.existsByEmail(request.email())
                ) {
                    throw new DuplicateResourceException(
                        "Email already exists: " + request.email()
                    );
                }

                userMapper.updateUserFromRequest(request, existingUser);
                existingUser.setPassword(
                    passwordEncoder.encode(request.password())
                );
                UserEntity saved = userRepository.save(existingUser);
                return userMapper.toDto(saved);
            });
    }

    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
