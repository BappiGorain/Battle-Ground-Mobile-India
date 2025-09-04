package com.bgmi.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bgmi.entities.Player;

@Repository
public interface PlayerRepo extends JpaRepository<Player,Long>
{

    Optional<Player> findByGameId(String id);
    Optional<Player> findByEmail(String email);
    Optional<Player> findByPhoneNumber(String phoneNumber);


}
