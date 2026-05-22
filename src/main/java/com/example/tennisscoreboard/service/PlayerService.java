package com.example.tennisscoreboard.service;

import com.example.tennisscoreboard.entity.Player;

public interface PlayerService {
    Player findOrCreatePlayer(String name);
}