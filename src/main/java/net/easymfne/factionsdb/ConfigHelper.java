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

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.bukkit.ChatColor;

/**
 * Configuration helper class, with methods for accessing the configuration.
 * 
 * @author Eric Hildebrand
 */
public class ConfigHelper {
    
    private FactionsDB plugin = null;
    
    /* Cached time values */
    private long banDurationMillis;
    private String banDurationString;
    
    /**
     * Instantiate a reference back to the plugin itself, and prepare the cached
     * ban duration (in milliseconds);
     * 
     * @param plugin
     *            The FactionsDB plugin
     * @throws TimeFormatException
     */
    public ConfigHelper(FactionsDB plugin) {
        this.plugin = plugin;
        updateCache();
    }
    
    /**
     * Colorize a string using '&' color codes.
     * 
     * @param string
     *            String to colorize
     * @return Colorized string
     */
    private String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
    
    /**
     * String to format, replacing tags with the relevant information provided.
     * 
     * @param string
     *            String to format
     * @param ban
     *            DeathBan involved
     * @return Finished display String
     */
    public String formatDisplayString(String string, DeathBan ban) {
        String result = string.replace("{0}", "%1$s").replace("{1}", "%2$s")
                .replace("{2}", "%3$.1f").replace("{3}", "%4$s")
                .replace("{4}", "%5$.1f");
        return String.format(result, ban.getName(), getBanDurationString(),
                getPowerThreshold(), Util.generateTimeString(ban
                        .getTimeLeft(getBanDurationMillis())), getPowerBoost());
    }
    
    /**
     * @return Duration of deathbans, in raw configuration String form
     */
    public String getBanDuration() {
        return plugin.getConfig().getString("ban.duration", "0.5h");
    }
    
    /**
     * @return Duration of deathbans, in milliseconds
     */
    public long getBanDurationMillis() {
        return banDurationMillis;
    }
    
    /**
     * @return Duration of deathbans, in user-friendly String form
     */
    public String getBanDurationString() {
        return banDurationString;
    }
    
    /**
     * @return Name of world to send players to upon deathban, or null if
     *         players should be sent to the spawn of their current world
     */
    public String getBanSpawnWorld() {
        return plugin.getConfig().getString("ban.spawn-world", null);
    }
    
    /**
     * @return Power level boost upon returning from a deathban
     */
    public double getPowerBoost() {
        return plugin.getConfig().getDouble("power.boost", 4);
    }
    
    /**
     * @return Power level that triggers a deathban
     */
    public double getPowerThreshold() {
        return plugin.getConfig().getDouble("power.threshold", 0);
    }
    
    /**
     * @return Colorized broadcast message string
     */
    public String getStringBroadcast() {
        return color(plugin.getConfig().getString("strings.broadcast"));
    }
    
    /**
     * @return Colorized kick message string
     */
    public String getStringKick() {
        return color(plugin.getConfig().getString("strings.kick"));
    }
    
    /**
     * @return Colorized login message string
     */
    public String getStringLogin() {
        return color(plugin.getConfig().getString("strings.login"));
    }
    
    /**
     * @return Colorized return message string
     */
    public String getStringReturn() {
        return color(plugin.getConfig().getString("strings.return"));
    }
    
    /**
     * @return Should bans be broadcast to the server?
     */
    public boolean isBanBroadcast() {
        return plugin.getConfig().getBoolean("ban.broadcast", true);
    }
    
    /**
     * @return Send players to spawn when issuing deathban?
     */
    public boolean isBanSendToSpawn() {
        return plugin.getConfig().getBoolean("ban.send-to-spawn", true);
    }
    
    /**
     * @return Should bans be saved and reloaded when server is restarted?
     */
    public boolean isPersistent() {
        return plugin.getConfig().getBoolean("persistence", false);
    }
    
    /**
     * @return Use UUIDs instead of Player names?
     */
    public boolean isUuidMode() {
        return plugin.getConfig().getBoolean("uuid-mode", true);
    }
    
    /**
     * Set the persistence state, in the case that persistence must be disabled
     * for configuration file safety.
     * 
     * @param value
     *            New state
     */
    public void setPersistent(boolean value) {
        plugin.getConfig().set("persistence", false);
    }
    
    /**
     * Update cached String and Long values of deathban duration
     */
    protected void updateCache() {
        try {
            banDurationMillis = Util.calculateMillis(getBanDuration());
        } catch (TimeFormatException e) {
            plugin.fancyLog(
                    Level.WARNING,
                    "Failed to parse configured ban duration '"
                            + e.getTimeString() + "', defaulting to 30m");
            banDurationMillis = TimeUnit.MINUTES.toMillis(30);
        }
        banDurationString = Util.generateTimeString(getBanDurationMillis());
    }
    
}
