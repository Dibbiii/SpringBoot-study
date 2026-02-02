package com.example.gem_springboot.modules.users.internal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity // dice ad Hibernate che questa classe corrisponde a una tabella del DB
@Table(name = "users") // Nome della tabella creata con LiquiBase
public class UserEntity {

    @Id // chiave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Dice che l'id viene generato dal DB tramite auto increment
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // così Jackson quando manda il JSON al frontend salta questo campo
    private String password;

    @Column(name = "created_at")
    //@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss") // formatto la data
    private LocalDateTime createdAt;
}
