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
 * Class WebPageMetaData
 * 
 * @version $Id: WebPageMetaData.java 27 2005-10-08 21:03:23Z henschel $
 */
public class WebPageMetaData {
    private String title = "";
    private String description = "";
    private String author = "";
    private String filename = "";


    /**
     * Sets webpage title
     *
     * @param title  webpage title
     */
    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * Gets webpage title
     *
     * @return  webpage title
     */
    public String getTitle() {
        return title;
    }


    /**
     * Sets webpage author
     *
     * @param author  webpage author
     */
    public void setAuthor(String author) {
        this.author = author;
    }


    /**
     * Get webpage author
     *
     * @return  webpage author
     */
    public String getAuthor() {
        return author;
    }


    /**
     * Sets webpage description
     *
     * @param description  webpage description
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Gets webpage description
     *
     * @return webpage description
     */
    public String getDescription() {
        return description;
    }


    /**
     * Sets webpage filename
     *
     * @param fileName  webpage filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }


    /**
     * Gets webpage filename
     *
     * @return webpage filename
     */
    public String getFilename() {
        return filename;
    }
}
