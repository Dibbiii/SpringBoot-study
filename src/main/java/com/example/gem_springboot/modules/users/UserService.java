package com.example.gem_springboot.modules.users;

import com.example.gem_springboot.modules.users.internal.UserEntity;
import com.example.gem_springboot.modules.users.internal.UserMapper;
import com.example.gem_springboot.modules.users.internal.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UsersList findAllPaginated(
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

        List<UserResponse> dtos = pageResult
            .getContent()
            .stream()
            .map(userMapper::toDto)
            .toList();

        return new UsersList(dtos, pageResult.getTotalElements());
    }

    public Optional<UserResponse> findById(Long id) {
        return userRepository.findById(id).map(userMapper::toDto);
    }

    public UserResponse createUser(UserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Lo username è già in uso");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email already exists");
        }
        UserEntity entity = userMapper.toEntity(request);
        entity = userRepository.save(entity);
        // Converto in Dto
        return userMapper.toDto(entity);
    }

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
                    throw new RuntimeException("Lo username è già in uso");
                }

                // Controllo email solo se è cambiata
                if (
                    !existingUser.getEmail().equals(request.email()) &&
                    userRepository.existsByEmail(request.email())
                ) {
                    throw new RuntimeException("L'email è già in uso");
                }

                userMapper.updateUserFromRequest(request, existingUser);
                UserEntity saved = userRepository.save(existingUser);
                return userMapper.toDto(saved);
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
