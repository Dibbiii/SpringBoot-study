package com.example.gem_springboot.modules.users.internal;

import com.example.gem_springboot.modules.users.UserRequest;
import com.example.gem_springboot.modules.users.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

// componentModel = "spring" crea un Bean Spring iniettabile con @Autowired o Costruttore
@Mapper(componentModel = "spring")
public interface UserMapper {
    // Da DTO Input a Entity per il salvataggio -> ignoro ID e CreatedAt perché li genera il DB
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    UserEntity toEntity(UserRequest request);

    // Da Entity a DTO Output per la risposta -> MapStruct mappa i campi con lo stesso nome automaticamente
    UserResponse toDto(UserEntity entity);
}
