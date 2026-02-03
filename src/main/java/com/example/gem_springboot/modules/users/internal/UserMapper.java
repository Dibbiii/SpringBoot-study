package com.example.gem_springboot.modules.users.internal;

import com.example.gem_springboot.modules.users.UserRequest;
import com.example.gem_springboot.modules.users.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

// componentModel = "spring" crea un Bean Spring iniettabile con @Autowired o Costruttore
@Mapper(componentModel = "spring")
public interface UserMapper {
    // Da DTO Input a Entity per il salvataggio -> ignoro ID e CreatedAt perché li genera il DB
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "authorities", ignore = true)
    UserEntity toEntity(UserRequest request);

    // Da Entity a DTO Output per la risposta -> MapStruct mappa i campi con lo stesso nome automaticamente
    UserResponse toDto(UserEntity entity);

    // Aggiorno oggetto esistente (@MappingTarget) con i dati del DTO
    @Mapping(target = "id", ignore = true) // Non cambiare mai l'ID
    @Mapping(target = "createdAt", ignore = true) // Non cambiare la data creazione
    @Mapping(target = "authorities", ignore = true)
    void updateUserFromRequest(
        UserRequest request,
        @MappingTarget UserEntity entity
    );
}
