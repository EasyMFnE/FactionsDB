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

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * The class that handles the "/factionsdb" command for the plugin.
 * 
 * @author Eric Hildebrand
 */
public class FdbCommand implements CommandExecutor {
    
    private FactionsDB plugin = null;
    
    /**
     * Initialize by instantiating reference to the plugin and registering this
     * class to handle the '/factionsdb' command.
     * 
     * @param plugin
     *            Reference to FactionsDB plugin instance
     */
    public FdbCommand(FactionsDB plugin) {
        this.plugin = plugin;
        plugin.getCommand("factionsdb").setExecutor(this);
    }
    
    /**
     * Release the '/factionsdb' command from its ties to this class.
     */
    public void close() {
        plugin.getCommand("factionsdb").setExecutor(null);
    }
    
    /**
     * This method handles user commands.
     * 
     * Usage: "/fdb reload", "/fdb list", "/fdb pardon <name|*>"
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command,
            String label, String[] args) {
        /* Use case: "/factionsdb list" */
        if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
            List<String> names = new ArrayList<String>();
            for (DeathBan ban : plugin.getBans()) {
                names.add(ban.getName());
            }
            sender.sendMessage("Active deathbans: "
                    + (names.isEmpty() ? "(none)" : StringUtils.join(names,
                            ", ")));
            return true;
        }
        /* Use case: "/factionsdb reload" */
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.reload();
            sender.sendMessage("Configuration reloaded from disk.");
            return true;
        }
        /* Use case: "/factionsdb pardon <...>" */
        if (args.length > 1 && args[0].equalsIgnoreCase("pardon")) {
            List<String> names = new ArrayList<String>();
            for (int i = 1; i < args.length; i++) {
                if (args[i].equals("*")) {
                    names.addAll(pardonAll());
                } else {
                    String name = pardonName(args[i]);
                    if (name != null) {
                        names.add(name);
                    }
                }
            }
            sender.sendMessage("Pardoned " + names.size()
                    + (names.size() == 1 ? " player: " : " players: ")
                    + StringUtils.join(names, ", "));
            return true;
        }
        
        return false;
    }
    
    /**
     * Helper method to pardon all standing DeathBans and generate a list for
     * names of those pardoned.
     * 
     * @return Names of the pardoned
     */
    private List<String> pardonAll() {
        List<String> names = new ArrayList<String>();
        for (Entry<String, DeathBan> ban : plugin.getBanEntries()) {
            DeathBan deathban = plugin.removeBanByKey(ban.getKey());
            if (deathban != null) {
                names.add(deathban.getName());
            }
        }
        return names;
    }
    
    /**
     * Helper method to pardon a DeathBan based on a String, giving a name back
     * as a result if a ban was pardoned. Otherwise, returns null.
     * 
     * @param name
     *            Name to use for the pardon
     * @return Name of the player pardoned, or null
     */
    private String pardonName(String name) {
        for (Entry<String, DeathBan> ban : plugin.getBanEntries()) {
            if (ban.getValue().getName().equalsIgnoreCase(name)) {
                DeathBan result = plugin.removeBanByKey(ban.getKey());
                if (result != null) {
                    return result.getName();
                }
            }
        }
        return null;
    }
}
