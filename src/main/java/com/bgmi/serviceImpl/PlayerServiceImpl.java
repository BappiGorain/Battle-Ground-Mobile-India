package com.bgmi.serviceImpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bgmi.entities.Player;
import com.bgmi.exceptions.ResourceNotFoundException;
import com.bgmi.repo.PlayerRepo;
import com.bgmi.service.PlayerService;

@Service
public class PlayerServiceImpl implements PlayerService
{

    @Autowired
    private PlayerRepo playerRepo;
    
    
    @Override
    public List<Player> getAllPlayer()
    {
        List<Player> players = new ArrayList<>();
        players = playerRepo.findAll();
        return players;
    }

    @Override
    public Player getSinglePlayer(String id)
    {
        Player player = playerRepo.findByGameId(id).orElseThrow(()->new ResourceNotFoundException("Player","id",id));
        return  player;
    }

    @Override
    public Player updatePlayer(String id ,Player player)
    {
        
        Player player2 = new Player();

        player2.setName(player.getName());
        player2.setEmail(player.getEmail());
        player2.setGameId(player.getGameId());
        player2.setPhoneNumber(player.getPhoneNumber());
        player2.setPlayerLogo(player.getPlayerLogo());
        Player savedPlayer = playerRepo.save(player2);

        return savedPlayer;
    }

    @Override
    public void deletePlayer(String playerId)
    {
        
        Player player = playerRepo.findByGameId(playerId).orElseThrow(()->new ResourceNotFoundException("Player", "Id", playerId));
        playerRepo.delete(player);
    }

    @Override
    public Player addPlayer(Player player)
    {    
        Player addedUser = playerRepo.save(player);
        return addedUser;
        
    }

    @Override
    public Player getPlayerByEmail(String email)
    {
        Player player = this.playerRepo.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("Player", "email", email));
        return player;
    }

    @Override
    public Player getPlayerByPhoneNumber(String phoneNumber)
    {
        Player player = this.playerRepo.findByEmail(phoneNumber).orElseThrow(()->new ResourceNotFoundException("Player", "phone Number", phoneNumber));
        return player;
    }
}
