package com.bgmi.entities;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true,nullable = false)
    private String gameId;
    @NotEmpty(message = "Name is required")
    private String name;
    @Email(message = "Please provide a valid email address")
    private String email;
    @NotNull(message = "Please provide a vailid passord")
    private String password;
    @NotEmpty(message = "Phone number is required")
    private String phoneNumber;
    private String photoPath = "default_bgmi_avatar.png";
    private boolean emailVerified = false;
    private boolean phoneVerified = false;
    private boolean enabled = true;

}
