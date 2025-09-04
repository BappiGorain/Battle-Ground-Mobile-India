package com.bgmi.entities;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class UserPrincipal implements UserDetails
{

    private Player player;

    public UserPrincipal(Player player)
    {
        this.player = player;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() 
    {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_PLAYER")); // Default Role of everyUser is Player
    }

    @Override
    public String getPassword() 
    {
        return player.getPassword();
    }

    @Override
    public String getUsername() {
        return player.getGameId();
    }

    public boolean isAccountNonExpired() {
      return true;
   }

   public boolean isAccountNonLocked() {
      return true;
   }

   public boolean isCredentialsNonExpired() {
      return true;
   }

   @Override
   public boolean isEnabled()
   {
      return true;
   }

}
