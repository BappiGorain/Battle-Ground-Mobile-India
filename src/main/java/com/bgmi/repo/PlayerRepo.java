package com.bgmi.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bgmi.entities.Player;

@Repository
public interface PlayerRepo extends JpaRepository<Player,String>
{ }
