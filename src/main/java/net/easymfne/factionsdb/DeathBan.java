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

import java.util.Calendar;

import org.bukkit.OfflinePlayer;

/**
 * Class representation of a FactionsDB ban, storing the time it was issued.
 * 
 * @author Eric Hildebrand
 */
public class DeathBan {
    
    private String name;
    private long timestamp;
    
    /**
     * Construct by automatically getting the current time.
     */
    public DeathBan(OfflinePlayer player) {
        name = player.getName();
        timestamp = Calendar.getInstance().getTimeInMillis();
    }
    
    /**
     * Initialize with a specified timestamp.
     * 
     * @param timestamp
     *            Time of ban in milliseconds since epoch
     */
    public DeathBan(String name, long timestamp) {
        this.name = name;
        this.timestamp = timestamp;
    }
    
    /**
     * Get the name associated with the ban. Used for display.
     * 
     * @return Associated name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Get number of milliseconds left in the ban, given a specified duration.
     * 
     * @param duration
     *            Duration of bans, in milliseconds
     * @return Number of milliseconds left, or 0 if expired
     */
    public long getTimeLeft(long duration) {
        return Math.max(0, (timestamp + duration)
                - Calendar.getInstance().getTimeInMillis());
    }
    
    /**
     * @return Timestamp of ban in milliseconds since epoch
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Check to see if the ban is expired, given a specified duration.
     * 
     * @param duration
     *            Length of ban in milliseconds
     * @return True if ban is expired, false otherwise
     */
    public boolean isExpired(long duration) {
        return (timestamp + duration) < Calendar.getInstance()
                .getTimeInMillis();
    }
    
}
