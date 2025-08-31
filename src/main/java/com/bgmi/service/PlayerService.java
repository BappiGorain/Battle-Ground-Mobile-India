package com.bgmi.service;

import java.util.List;

import com.bgmi.entities.Player;

public interface PlayerService
{
    List<Player> getAllPlayer();
    Player getSinglePlayer(String id);
    Player updatePlayer(String id , Player player);
    void deletePlayer(String playerId);
    Player addPlayer(Player player);
    Player getPlayerByEmail(String email);
    Player getPlayerByPhoneNumber(String phoneNumber);

}
