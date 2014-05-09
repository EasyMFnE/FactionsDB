/*
 * This file is part of the FactionsDB plugin by EasyMFnE.
 * 
 * FactionsDB is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or any later version.
 * 
 * FactionsDB is distributed in the hope that it will be useful, but without any
 * warranty; without even the implied warranty of merchantability or fitness for
 * a particular purpose. See the GNU General Public License for details.
 * 
 * You should have received a copy of the GNU General Public License v3 along
 * with FactionsDB. If not, see <http://www.gnu.org/licenses/>.
 */
package net.easymfne.factionsdb;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Custom Event called when a Player receives a DeathBan, used for integration
 * by other plugins.
 */
public class PlayerDeathBanEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    private Player player;
    private PlayerDeathEvent death;
    private DeathBan ban;
    
    public PlayerDeathBanEvent(Player player, PlayerDeathEvent death,
            DeathBan ban) {
        this.player = player;
        this.death = death;
        this.ban = ban;
    }
    
    /**
     * Get reference to the DeathBan object itself.
     * 
     * @return the DeathBan
     */
    public DeathBan getDeathBan() {
        return ban;
    }
    
    /**
     * Get the PlayerDeathEvent that caused the DeathBan.
     * 
     * @return the PlayerDeathEvent
     */
    public PlayerDeathEvent getDeathEvent() {
        return death;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    /**
     * Get the Player that received the DeathBan.
     * 
     * @return the Player
     */
    public Player getPlayer() {
        return player;
    }
    
}
