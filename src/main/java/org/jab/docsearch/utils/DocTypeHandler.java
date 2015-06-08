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
 * Class DocTypeHandler
 *
 * @version $Id: DocTypeHandler.java 130 2009-07-21 10:26:00Z henschel $
 */
public final class DocTypeHandler {
    private final String extension;
    private final String desc;
    private final String app;


    /**
     * Contructor
     *
     * @param extension  DocType extension
     * @param desc       DocType descritpion
     * @param app        DocType application
     */
    public DocTypeHandler(final String extension, final String desc, final String app) {
        this.desc = desc;
        this.extension = extension.toLowerCase();
        this.app = app;
    }


    /**
     * Checks the extension with DocTypeHandler
     *
     * @param ext  Extension
     * @return     True, if the extension is compatible with the DocTypeHandler
     */
    public boolean isCompat(final String ext) {
        return (ext.equalsIgnoreCase(extension));
    }


    /**
     * Gets a combined description of the DocTypeHandler.
     *
     * @return  combined description
     */
    public String descName() {
        return extension + " (" + desc + ") " + Utils.getNameOnly(app);
    }


    /**
     * Gets DocType application.
     *
     * @return  DocType application
     */
    public String getApp() {
        return app;
    }


    /**
     * Gets DocType extension.
     *
     * @return  DocType extension
     */
    public String getExtension() {
        return extension;
    }


    /**
     * Gets DocType descrition.
     *
     * @return  DocType descrition
     */
    public String getDesc() {
        return desc;
    }
}
