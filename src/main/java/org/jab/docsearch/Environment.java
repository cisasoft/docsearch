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
package org.jab.docsearch;

import org.apache.log4j.Logger;
import org.jab.docsearch.constants.OSType;

/**
 * This class contains all variables for a running session.<br>
 * The class is an Singlton.
 *
 * @version $Id: Environment.java 176 2012-11-04 19:12:28Z henschel $
 */
public final class Environment {
    /**
     * Log4J
     */
    private final Logger logger = Logger.getLogger(getClass().getName());
    /**
     * Environment variable
     */
    private static Environment env = new Environment();
    /**
     * Is GUI mode
     */
    private boolean isGUIMode;
    /**
     * OS Type
     * @see OSType
     */
    private int oSType;
    /**
     * CD directory
     */
    private String cDROMDir;


    /**
     *
     */
    private Environment() {
        logger.info("Environment() created");
    }


    /**
     * Gets instance of Environment
     *
     * @return  instance of Environment
     */
    public static Environment getInstance() {
        return env;
    }


    /**
     * Is GUI mode
     *
     * @return  true is GUI mode on
     */
    public boolean isGUIMode() {
       return isGUIMode;
    }


    /**
     * Sets GUI mode
     *
     * @param isGUIMode  GUI mode
     */
    public void setGUIMode(boolean isGUIMode) {
       this.isGUIMode = isGUIMode;
    }


    /**
     * Gets OS Type
     *
     * @see  OSType
     * @return  OS type
     */
    public int getOSType() {
        return oSType;
    }


    /**
     * Sets OS Type
     *
     * @see  OSType
     * @param oSType  OS Type
     */
    public void setOSType(int oSType) {
        this.oSType = oSType;
    }


    /**
     * Gets CDROM directory
     *
     * @return  CDROM directory
     */
    public String getCDROMDir() {
        return cDROMDir;
    }


    /**
     * Sets CDROM directory
     *
     * @param cDROMDir  CDROM directory
     */
    public void setCDROMDir(String cDROMDir) {
        this.cDROMDir = cDROMDir;
    }


    /**
     * Run DocSearcher in WebStart?
     *
     * @return  true, if DocSearcher runs in WebStart
     */
    public boolean isWebStart() {
        String prop = System.getProperty("dsstart", "local");
        return "webstart".equals(prop);
    }
}
