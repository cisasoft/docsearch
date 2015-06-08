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
 * Class SimpleBookmark
 * 
 * @version $Id: SimpleBookmark.java 38 2005-10-12 20:58:49Z henschel $
 */
public class SimpleBookmark {
    private String description = "";
    private String url = "";


    /**
     * Constructor
     *
     * @param url
     * @param description
     */
    public SimpleBookmark(String url, String description) {
        this.url = url;
        this.description = description;
    }


    /**
     * Gets description
     *
     * @return  description of the SimpleBookmark
     */
    public String getDescription() {
        return description;
    }


    /**
     * Gets URL
     *
     * @return  URL of the SimpleBookmark
     */
    public String getURL() {
        return url;
    }
}
