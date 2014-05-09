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

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.mcore.ps.PS;

/**
 * The class that monitors and reacts to server events: PlayerDeathEvent,
 * PlayerRespawnEvent, AsyncPlayerPreLoginEvent, and PlayerJoinEvent.
 * 
 * @author Eric Hildebrand
 */
public class PlayerListener implements Listener {
    
    private FactionsDB plugin = null;
    private HashMap<Player, PlayerDeathEvent> deathMap = null;
    
    /**
     * Instantiate by getting a reference to the plugin instance, registering
     * each of the defined EventHandlers, and initializing the DeathEvent store.
     * 
     * @param plugin
     *            Reference to FactionsDB plugin instance
     */
    public PlayerListener(FactionsDB plugin) {
        this.plugin = plugin;
        deathMap = new HashMap<Player, PlayerDeathEvent>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    /**
     * Empty and null DeathEvent store, and unregister all registered
     * EventHandlers, preventing further reactions.
     */
    public void close() {
        HandlerList.unregisterAll(this);
        deathMap.clear();
        deathMap = null;
    }
    
    /**
     * Helper method that determines the world for a player to respawn into,
     * based on the configuration and the player's current world.
     * 
     * @param player
     *            Player being respawned
     * @return World to respawn in
     */
    private World getRespawnWorld(Player player) {
        String worldName = plugin.getConfigHelper().getBanSpawnWorld();
        if (worldName != null) {
            World world = plugin.getServer().getWorld(worldName);
            if (world != null) {
                return world;
            }
        }
        return player.getWorld();
    }
    
    /**
     * When a player passes the Login check and joins the server, check for
     * existing DeathBans. If one is found and expired (it would have to be, to
     * pass the above Login check), the DeathBan is removed, the player is
     * greeted, and the player's power level is boosted from their current
     * amount by the configured amount.
     * 
     * @param event
     *            JoinEvent
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onLogin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (plugin.hasBan(player)) {
            DeathBan ban = plugin.getBan(player);
            if (ban.isExpired(plugin.getConfigHelper().getBanDurationMillis())) {
                plugin.removeBan(player);
                UPlayer uPlayer = UPlayer.get(player);
                uPlayer.setPower(uPlayer.getPower()
                        + plugin.getConfigHelper().getPowerBoost());
                player.sendMessage(plugin
                        .getConfigHelper()
                        .formatDisplayString(
                                plugin.getConfigHelper().getStringReturn(), ban));
            }
        }
    }
    
    /**
     * When a player dies in a world with power loss, and in a region of that
     * world that allows power loss, cache that death event for later use when
     * the player attempts to respawn.
     * 
     * @param event
     *            DeathEvent to examine and potentially cache
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Faction faction = BoardColls.get().getFactionAt(
                PS.valueOf(event.getEntity()));
        if (!faction.getFlag(FFlag.POWERLOSS)) {
            return;
        }
        if (MConf.get().worldsNoPowerLoss.contains(event.getEntity().getWorld()
                .getName())) {
            return;
        }
        deathMap.put(event.getEntity(), event);
    }
    
    /**
     * When a non-exempt player respawns, check their current power level. If it
     * is below the configured threshold, adjust their respawn location to the
     * configured world spawn, create and run a new DeathBan, and have the
     * PluginManager call this event for integration by other plugins.
     * 
     * @param event
     *            RespawnEvent
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UPlayer uplayer = UPlayer.get(player);
        
        /* If we care about the player and their power is too low */
        if (!Perms.isExempt(event.getPlayer())
                && uplayer.getPower() <= plugin.getConfigHelper()
                        .getPowerThreshold()) {
            /* Adjust their respawn location as configured */
            event.setRespawnLocation(getRespawnWorld(player).getSpawnLocation());
            
            /* Create a new DeathBan and schedule the DeathBanner */
            DeathBan ban = plugin.addBan(event.getPlayer());
            plugin.getServer().getScheduler()
                    .runTask(plugin, new DeathBanner(plugin, player, ban));
            
            /* Call the custom event so other plugins can catch this event. */
            Bukkit.getServer()
                    .getPluginManager()
                    .callEvent(
                            new PlayerDeathBanEvent(player, deathMap
                                    .get(player), ban));
        }
        
        /* Remove player's DeathEvent from cache, it is no longer needed. */
        deathMap.remove(player);
    }
    
    /**
     * When a player attempts to log in to the server, check for existing
     * DeathBans. If there is a DeathBan and it is not expired yet, prevent the
     * login with the appropriate message.
     * 
     * @param event
     *            LoginEvent
     */
    @EventHandler(ignoreCancelled = true)
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        String key = (plugin.getConfigHelper().isUuidMode() ? event
                .getUniqueId().toString() : event.getName().toLowerCase());
        if (plugin.hasBanByKey(key)) {
            DeathBan ban = plugin.getBanByKey(key);
            if (!ban.isExpired(plugin.getConfigHelper().getBanDurationMillis())) {
                event.setLoginResult(Result.KICK_BANNED);
                event.setKickMessage(plugin.getConfigHelper()
                        .formatDisplayString(
                                plugin.getConfigHelper().getStringLogin(), ban));
            }
        }
    }
    
}
