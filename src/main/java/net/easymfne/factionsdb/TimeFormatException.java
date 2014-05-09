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

/**
 * Simple exception for catching and reporting syntactic errors in time strings.
 */
public class TimeFormatException extends Exception {
    
    private static final long serialVersionUID = -1675968326479550628L;
    
    private String timeString;
    
    public TimeFormatException(String timeString) {
        this.timeString = timeString;
    }
    
    public String getTimeString() {
        return timeString;
    }
    
}
