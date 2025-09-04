package com.bgmi.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired  // ✅ Use PasswordEncoder interface
    private PasswordEncoder passwordEncoder;
    
    
    
    @Override
    public List<Player> getAllPlayer()
    {
        List<Player> players = new ArrayList<>();
        players = playerRepo.findAll();
        return players;
    }

    public Optional<Player> getOptionalPlayer(String gameId) 
    {
        return playerRepo.findByGameId(gameId);
    }


    @Override
    public Player updatePlayer(String gameId, Player player)
    {
        Player existingPlayer = playerRepo.findByGameId(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Player", "gameId", gameId));

        // Update fields
        existingPlayer.setName(player.getName());
        existingPlayer.setEmail(player.getEmail());
        existingPlayer.setPhoneNumber(player.getPhoneNumber());

        // Only update photo path if it's provided
        if (player.getPhotoPath() != null && !player.getPhotoPath().isEmpty()) {
            existingPlayer.setPhotoPath(player.getPhotoPath());
        }

        return playerRepo.save(existingPlayer);
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
        player.setPassword(passwordEncoder.encode(player.getPassword()));
        return playerRepo.save(player);
        
        
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
        Player player = this.playerRepo.findByPhoneNumber(phoneNumber).orElseThrow(()->new ResourceNotFoundException("Player", "phone Number", phoneNumber));
        return player;
    }

    @Override
    public Player getPlayer(String gameId)
    {
        Optional<Player> optionalPlayer = playerRepo.findByGameId(gameId);
        if(optionalPlayer.isPresent())
        {
            return optionalPlayer.get();
        }
        else
        {
            return new Player();
        }
    }

    
    
    
    
}
