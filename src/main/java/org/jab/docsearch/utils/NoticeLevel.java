/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.jab.docsearch.utils;

import org.apache.log4j.Level;

/**
 * Class NoticeLevel
 * 
 * @version $Id: NoticeLevel.java 31 2005-10-10 19:54:11Z henschel $
 */
public final class NoticeLevel extends Level {
    /**
     * Level NOTICE is higher than Level FATAL
     */
    public final static int NOTICE_INT = Level.FATAL_INT + 1;
    /**
     * Level NOTICE String
     */
    private final static String NOTICE_STR = "NOTICE";
    /**
     * NOTICE Syslog equivalent
     */
    private static final int SYSLOG_NOTICE_INT = 7;
    /**
     * Level NOTICE 
     */
    public static final NoticeLevel NOTICE = new NoticeLevel(NOTICE_INT, NOTICE_STR, SYSLOG_NOTICE_INT);

    
    /**
     * Constructor
     *
     * @param level        level int
     * @param strLevel     level string
     * @param syslogEquiv  SysLog equivalent
     */
    protected NoticeLevel(int level, String strLevel, int syslogEquiv) {
        super(level, strLevel, syslogEquiv);
    }


    /**
     * Convert the string passed as argument to a level. If the
     * conversion fails, then this method returns {@link #TRACE}.
     *
     * @param sArg  level string
     * @return      Level
     */
    public static Level toLevel(String sArg) {
        return toLevel(sArg, NoticeLevel.NOTICE);
    }


    /**
     * Convert the string passed as argument to a level. If the
     * conversion fails, then this method returns default value.
     *
     * @param sArg          level string
     * @param defaultValue  default value
     * @return              Level
    */
    public static Level toLevel(String sArg, Level defaultValue) {
        if(sArg == null) {
            return defaultValue;
        }

        String stringVal = sArg.toUpperCase();
   
        if (stringVal.equals(NOTICE_STR)) {
            return NoticeLevel.NOTICE;
        }
     
        return Level.toLevel(sArg, defaultValue);    
    }


    /**
     * Convert the int passed as argument to a level.
     *
     * @param i  level string
     * @return   Level
     */
    public static Level toLevel(int i) throws  IllegalArgumentException {
        
        if (i == NOTICE_INT) {
            return NoticeLevel.NOTICE;
        }

        return Level.toLevel(i);
    }
}
