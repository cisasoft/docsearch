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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * I18n Class
 *
 * @version $Id: I18n.java 171 2012-09-07 20:08:01Z henschel $
 */
public final class I18n {
    /**
     * Log4J
     */
    private final static Logger logger = Logger.getLogger(I18n.class);
    /**
     * ResourceBundle
     */
    private static ResourceBundle mainBundle = ResourceBundle.getBundle("org.jab.docsearch.DocSearcher");


    /**
     * Contructor
     */
    private I18n() {
        // private
    }


    /**
     * Get translation for given key
     *
     * @param key  translation key
     * @return     translated text
     */
    public static String getString(final String key) {
        try {
            return mainBundle.getString(key);
        }
        catch (MissingResourceException mre) {
            logger.warn("getString() can't find resource '" + key + "'");
        }

        return "NF[" + key + ']';
    }


    /**
     * Get mnemonic for given key
     *
     * @param key  translation key
     * @return     Mnemonic value
     */
    public static int getMnemonic(final String key) {
        String tmp = null;

        try {
            tmp = mainBundle.getString(key);
        }
        catch (MissingResourceException mre) {
            logger.warn("getMnemonic() can't find resource '" + key + "'");
        }

        if (tmp != null && tmp.length() > 0) {
            return tmp.charAt(0);
        }
        else {
            return -1;
        }
    }
}
