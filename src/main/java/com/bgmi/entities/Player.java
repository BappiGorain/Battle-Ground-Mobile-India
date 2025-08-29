package com.bgmi.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Player
{

    @Id
    private String gameId;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private boolean emailVerified = false;
    private boolean phoneVerified = false;
}
