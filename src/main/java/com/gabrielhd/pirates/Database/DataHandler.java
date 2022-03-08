package com.gabrielhd.pirates.Database;

import com.gabrielhd.pirates.Player.*;

public interface DataHandler
{
    void loadPlayer(PlayerData p0);
    
    void uploadPlayer(PlayerData p0);
    
    void close();
}
