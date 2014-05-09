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

/**
 * Simple runnable class for performing the actions instigated by a DeathBan.
 */
public class DeathBanner implements Runnable {
    
    private FactionsDB plugin;
    private Player player;
    private DeathBan ban;
    
    /**
     * Initialize and instantiate necessary references.
     * 
     * @param plugin
     *            Reference to FactionsDB plugin
     * @param player
     *            Reference to player being banned
     * @param ban
     *            Reference to the ban
     */
    public DeathBanner(FactionsDB plugin, Player player, DeathBan ban) {
        this.plugin = plugin;
        this.player = player;
        this.ban = ban;
    }
    
    /**
     * Broadcast the player's ban to the server with the appropriate message.
     */
    private void broadcastDeathBan() {
        plugin.getServer().broadcastMessage(
                plugin.getConfigHelper().formatDisplayString(
                        plugin.getConfigHelper().getStringBroadcast(), ban));
    }
    
    /**
     * Kick the player from the server with the appropriate message.
     */
    private void kickPlayer() {
        player.kickPlayer(plugin.getConfigHelper().formatDisplayString(
                plugin.getConfigHelper().getStringKick(), ban));
    }
    
    /**
     * Kick the player from the server and, if configured, broadcast it.
     */
    @Override
    public void run() {
        kickPlayer();
        if (plugin.getConfigHelper().isBanBroadcast()) {
            broadcastDeathBan();
        }
    }
    
}
