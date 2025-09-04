package com.bgmi.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bgmi.entities.Player;
import com.bgmi.entities.UserPrincipal;
import com.bgmi.repo.PlayerRepo;

@Service
public class MyUserDetailsService implements UserDetailsService
{

    @Autowired
    private PlayerRepo repo;
    
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        Optional<Player> optionalPlayer = repo.findByGameId(username);

        if(optionalPlayer.isPresent())
        {
            Player player = optionalPlayer.get();
            return new UserPrincipal(player);
        }
        else
        {
            System.err.println("user not found");
            throw new UsernameNotFoundException("User Not Found");
        }
    }


}
