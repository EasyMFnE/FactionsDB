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

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.mcstats.MetricsLite;

/**
 * Main plugin class, responsible for its own setup, logging, reloading, and
 * shutdown operations. Maintains instances of ConfigHelper, FdbCommand, and
 * PlayerListener, and handles active bans and saving/loading.
 * 
 * @author Eric Hildebrand
 */
public class FactionsDB extends JavaPlugin {
    
    /**
     * Simple runnable class for automatically saving deathbans on an interval.
     */
    private class Autosave extends BukkitRunnable {
        @Override
        public void run() {
            saveBans();
        }
    }
    
    private Map<String, DeathBan> activeBans;
    
    private final File BANS_FILE = new File(getDataFolder().getAbsolutePath()
            + File.separator + "bans.yml"); /* plugins/FactionsDB/bans.yml */
    private final int AUTOSAVE_PERIOD = 1200; /* 6000 ticks = 5 minutes */
    private BukkitTask autosaveTask;
    
    private ConfigHelper configHelper = null;
    private FdbCommand fdbCommand = null;
    private PlayerListener playerListener = null;
    
    /* Strings for fancyLog() methods */
    private final String logPrefix = ChatColor.RED + "[FactionsDB] ";
    private final String logColor = ChatColor.YELLOW.toString();
    
    /**
     * Add a new Deathban into the system and return a reference to it.
     * 
     * @param player
     *            Player to deathban
     * @return The resulting DeathBan
     */
    protected DeathBan addBan(OfflinePlayer player) {
        DeathBan ban = new DeathBan(player);
        activeBans.put(getKey(player), ban);
        return ban;
    }
    
    /**
     * Log a message to the console using color, with a specific logging Level.
     * If there is no console open, log the message without any coloration.
     * 
     * @param level
     *            Level at which the message should be logged
     * @param message
     *            The message to be logged
     */
    protected void fancyLog(Level level, String message) {
        if (getServer().getConsoleSender() != null) {
            getServer().getConsoleSender().sendMessage(
                    logPrefix + logColor + message);
        } else {
            getServer().getLogger().log(level,
                    ChatColor.stripColor(logPrefix + message));
        }
    }
    
    /**
     * Log a message to the console using color, defaulting to the Info level.
     * If there is no console open, log the message without any coloration.
     * 
     * @param message
     *            The message to be logged
     */
    protected void fancyLog(String message) {
        fancyLog(Level.INFO, message);
    }
    
    /**
     * Get an existing deathban by player.
     * 
     * @param player
     *            Player to look up
     * @return DeathBan, or null if none exist
     */
    protected DeathBan getBan(OfflinePlayer player) {
        return activeBans.get(getKey(player));
    }
    
    /**
     * Get an existing deathban by lookup key
     * 
     * @param key
     *            Key to use for lookup
     * @return DeathBan, or null if none exist
     */
    protected DeathBan getBanByKey(String key) {
        return activeBans.get(key);
    }
    
    /**
     * Get the set of active DeathBan entries
     * 
     * @return All active deathban entries
     */
    protected Set<Entry<String, DeathBan>> getBanEntries() {
        return activeBans.entrySet();
    }
    
    /**
     * Get a collection of active DeathBans.
     * 
     * @return All active deathbans
     */
    protected Collection<DeathBan> getBans() {
        return activeBans.values();
    }
    
    /**
     * @return the configuration helper instance
     */
    public ConfigHelper getConfigHelper() {
        return configHelper;
    }
    
    /**
     * Get the String key representing a player. UUID.toString() if in UUID
     * mode, otherwise it will be the player's name in lower-case.
     * 
     * @param player
     *            Player to get key for
     * @return Key representing the Player
     */
    private String getKey(OfflinePlayer player) {
        if (getConfigHelper().isUuidMode()) {
            return player.getUniqueId().toString();
        }
        return player.getName().toLowerCase();
    }
    
    /**
     * Check to see if a player has a standing deathban
     * 
     * @param player
     *            The player to check
     * @return Whether the player has a deathban
     */
    protected boolean hasBan(OfflinePlayer player) {
        return activeBans.containsKey(getKey(player));
    }
    
    /**
     * Check for an existing deathban by lookup key
     * 
     * @param key
     *            Key to use for lookup
     * @return Whether a ban exists for the key
     */
    protected boolean hasBanByKey(String key) {
        return activeBans.containsKey(key);
    }
    
    /**
     * Load previously saved DeathBans from disk.
     * 
     * @return Successfulness of loading
     */
    private boolean loadBans() {
        try {
            YamlConfiguration bans = YamlConfiguration
                    .loadConfiguration(BANS_FILE);
            for (String key : bans.getKeys(false)) {
                activeBans.put(
                        key,
                        new DeathBan(bans.getConfigurationSection(key)
                                .getString("name"), bans
                                .getConfigurationSection(key).getLong(
                                        "timestamp")));
                // activeBans.put(key, DeathBan.deserialize(((MemorySection)
                // bans
                // .get(key)).getValues(true)));
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Close all event handlers and command listeners, then null instances to
     * mark them for garbage collection. Displays elapsed time to console when
     * finished.
     */
    @Override
    public void onDisable() {
        long start = Calendar.getInstance().getTimeInMillis();
        fancyLog("=== DISABLE START ===");
        if (autosaveTask != null) {
            autosaveTask.cancel();
        }
        if (getConfigHelper().isPersistent()) {
            if (saveBans()) {
                fancyLog("Active bans saved to disk");
            } else {
                fancyLog(Level.SEVERE, "Active bans could not be saved");
            }
        } else if (BANS_FILE.exists() && BANS_FILE.delete()) {
            fancyLog("Cleaned up leftover bans file");
        }
        playerListener.close();
        playerListener = null;
        fdbCommand.close();
        fdbCommand = null;
        configHelper = null;
        activeBans = null;
        fancyLog("=== DISABLE COMPLETE ("
                + (Calendar.getInstance().getTimeInMillis() - start)
                + "ms) ===");
    }
    
    /**
     * Set up the plugin by: loading config.yml (creating from default if not
     * existent), then instantiating its own ConfigHelper, FdbCommand, and
     * PlayerListener objects. Displays elapsed time to console when finished.
     */
    @Override
    public void onEnable() {
        long start = Calendar.getInstance().getTimeInMillis();
        fancyLog("=== ENABLE START ===");
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveDefaultConfig();
            fancyLog("Saved default config.yml");
        }
        
        activeBans = new HashMap<String, DeathBan>();
        configHelper = new ConfigHelper(this);
        fdbCommand = new FdbCommand(this);
        playerListener = new PlayerListener(this);
        if (getConfigHelper().isPersistent()) {
            if (BANS_FILE.exists() && loadBans()) {
                fancyLog("Loaded saved bans from disk");
                autosaveTask = getServer().getScheduler().runTaskTimer(this,
                        new Autosave(), AUTOSAVE_PERIOD, AUTOSAVE_PERIOD);
                fancyLog("Scheduled autosave task");
            } else if (BANS_FILE.exists()) {
                fancyLog(Level.SEVERE, "Failed to load bans from disk");
                getConfigHelper().setPersistent(false);
                fancyLog(Level.SEVERE, "Persistence and autosaving have been "
                        + "disabled this time, to allow you to fix the issue.");
            } else {
                autosaveTask = getServer().getScheduler().runTaskTimer(this,
                        new Autosave(), AUTOSAVE_PERIOD, AUTOSAVE_PERIOD);
                fancyLog("Scheduled autosave task");
            }
        }
        startMetrics();
        fancyLog("=== ENABLE COMPLETE ("
                + (Calendar.getInstance().getTimeInMillis() - start)
                + "ms) ===");
    }
    
    /**
     * Reload the configuration from disk and perform any necessary functions.
     * Displays elapsed time to console when finished.
     */
    public void reload() {
        long start = Calendar.getInstance().getTimeInMillis();
        fancyLog("=== RELOAD START ===");
        boolean persistence = getConfigHelper().isPersistent();
        reloadConfig();
        getConfigHelper().setPersistent(persistence);
        fancyLog("Configuration reloaded from disk");
        configHelper.updateCache();
        fancyLog("Cache updated");
        fancyLog("=== RELOAD COMPLETE ("
                + (Calendar.getInstance().getTimeInMillis() - start)
                + "ms) ===");
    }
    
    /**
     * Remove and return a DeathBan for a player
     * 
     * @param player
     *            Player to look up
     * @return DeathBan on file, or null if none exists
     */
    protected DeathBan removeBan(OfflinePlayer player) {
        return activeBans.remove(getKey(player));
    }
    
    /**
     * Remove and return a DeathBan for a certain String key
     * 
     * @param key
     *            Specific String key to look up
     * @return DeathBan on file, or null if none exists
     */
    protected DeathBan removeBanByKey(String key) {
        return activeBans.remove(key);
    }
    
    /**
     * Save currently active DeathBans to disk for persistence.
     * 
     * @return Successfulness of save
     */
    private boolean saveBans() {
        YamlConfiguration bans = new YamlConfiguration();
        Iterator<Entry<String, DeathBan>> iterator = activeBans.entrySet()
                .iterator();
        while (iterator.hasNext()) {
            Entry<String, DeathBan> entry = iterator.next();
            bans.set(entry.getKey() + ".name", entry.getValue().getName());
            bans.set(entry.getKey() + ".timestamp", entry.getValue()
                    .getTimestamp());
        }
        try {
            bans.save(BANS_FILE);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * If possible, instantiate Metrics and connect with mcstats.org
     */
    private void startMetrics() {
        MetricsLite metrics;
        try {
            metrics = new MetricsLite(this);
            if (metrics.start()) {
                fancyLog("Metrics enabled.");
            }
        } catch (IOException e) {
            fancyLog(Level.WARNING, "Metrics exception: " + e.getMessage());
        }
    }
    
}
