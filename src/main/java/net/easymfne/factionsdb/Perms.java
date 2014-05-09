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

import org.bukkit.permissions.Permissible;

/**
 * This class provides a static method to check user permissions.
 * 
 * @author Eric Hildebrand
 */
public class Perms {
    
    /**
     * Check if a user is exempt from being deathbanned
     * 
     * @param p
     *            User
     * @return Exemption status
     */
    public static boolean isExempt(Permissible p) {
        return p.hasPermission("factionsdb.ban.exempt");
    }
    
}