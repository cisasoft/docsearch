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

/**
 * File type utils
 *
 * @version $Id: FileTypeUtils.java 137 2009-09-18 13:56:27Z henschel $
 */
public final class FileTypeUtils {

    /**
     * Checks file is text codument
     *
     * @param file  filename
     * @return      true for file ending in .txt etc...
     */
    public static boolean isFileTypeText(final String file) {
        String lowerFile = file.toLowerCase();

        if (lowerFile.endsWith(".txt")) {
            return true;
        }

        return false;
    }


    /**
     * Checks is filetype HTML
     *
     * @param file  filename
     * @return      true for links ending in .htm, .shtm, .asp, .jsp, etc...
     */
    public static boolean isFileTypeHTML(final String file) {
        String lowerFile = file.toLowerCase();

        if (lowerFile.endsWith(".html")
                || lowerFile.endsWith(".htm")
                || lowerFile.endsWith(".shtml")
                || lowerFile.endsWith(".shtm")
                || lowerFile.endsWith(".php")
                || lowerFile.endsWith(".asp")
                || lowerFile.endsWith(".jsp")) {
            return true;
        }

        return false;
    }
}
