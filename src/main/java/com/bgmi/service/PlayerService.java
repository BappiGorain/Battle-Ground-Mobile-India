package com.bgmi.service;

import java.util.List;
import java.util.Optional;

import com.bgmi.entities.Player;

public interface PlayerService
{
    List<Player> getAllPlayer();
    Optional<Player> getOptionalPlayer(String gameId);
    Player updatePlayer(String id , Player player);
    void deletePlayer(String playerId);
    Player addPlayer(Player player);
    Player getPlayerByEmail(String email);
    Player getPlayerByPhoneNumber(String phoneNumber);
    Player getPlayer(String gameId);

}
