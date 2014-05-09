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
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class with static methods for converting time between representations
 * in String and long format.
 */
public class Util {
    
    private final static double SECOND = 1000;
    private final static double MINUTE = SECOND * 60;
    private final static double HOUR = MINUTE * 60;
    private final static double DAY = HOUR * 24;
    
    /**
     * Calculate the number of milliseconds represented by a time String.
     * 
     * @param timeString
     *            String representing a time
     * @return Number of milliseconds
     * @throws TimeFormatException
     */
    public static long calculateMillis(String timeString)
            throws TimeFormatException {
        if (timeString.matches("^[0-9.]+[sS]$")) {
            return (long) (SECOND * getTimeDouble(timeString));
        } else if (timeString.matches("^[0-9.]+[mM]")) {
            return (long) (MINUTE * getTimeDouble(timeString));
        } else if (timeString.matches("^[0-9.]+[hH]")) {
            return (long) (HOUR * getTimeDouble(timeString));
        } else if (timeString.matches("^[0-9.]+[dD]")) {
            return (long) (DAY * getTimeDouble(timeString));
        } else {
            throw new TimeFormatException(timeString);
        }
    }
    
    /**
     * Convert a time in milliseconds to a user-friendly String format.
     * 
     * @param time
     *            Number of milliseconds
     * @return User-friendly String
     */
    public static String generateTimeString(long time) {
        List<String> timeStrings = new ArrayList<String>();
        long d = TimeUnit.MILLISECONDS.toDays(time);
        time -= TimeUnit.DAYS.toMillis(d);
        long h = TimeUnit.MILLISECONDS.toHours(time);
        time -= TimeUnit.HOURS.toMillis(h);
        long m = TimeUnit.MILLISECONDS.toMinutes(time);
        time -= TimeUnit.MINUTES.toMillis(m);
        long s = TimeUnit.MILLISECONDS.toSeconds(time);
        
        if (d > 0) {
            timeStrings.add(d + (d == 1 ? " day" : " days"));
        }
        if (h > 0) {
            timeStrings.add(h + (h == 1 ? " hour" : " hours"));
        }
        if (m > 0) {
            timeStrings.add(m + (m == 1 ? " minute" : " minutes"));
        }
        if (s > 0 || timeStrings.size() < 1) {
            timeStrings.add(s + (s == 1 ? " second" : " seconds"));
        }
        
        return StringUtils.join(timeStrings, ", ");
    }
    
    /**
     * Strip a time string of all alphabetic characters and attempt to parse it
     * as a double.
     * 
     * @param timeString
     *            Time string to parse
     * @return Parsed number from the string
     * @throws TimeFormatException
     */
    private static double getTimeDouble(String timeString)
            throws TimeFormatException {
        try {
            return Double.parseDouble(timeString.replaceAll("[sSmMhHdD]", ""));
        } catch (Exception e) {
            throw new TimeFormatException(timeString);
        }
    }
    
}
