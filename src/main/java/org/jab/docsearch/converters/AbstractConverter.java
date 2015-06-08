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
package org.jab.docsearch.converters;

import org.apache.log4j.Logger;

/**
 *
 * Abstract class AbstractConverter
 *
 * @version $Id: AbstractConverter.java 146 2009-11-15 18:49:21Z henschel $
 */
public abstract class AbstractConverter {
    /**
     * Logging
     */
    protected Logger log = Logger.getLogger(getClass());
    /**
     * Document tilte
     */
    protected String documentTitle = null;
    /**
     * Document author
     */
    protected String documentAuthor = null;
    /**
     * Document text
     */
    protected String documentText = null;
    /**
     * Document keywords
     */
    protected String documentKeywords = null;
    /**
     * Max document summary length
     */
    protected int MAX_SUMMARY_LENGTH = 300;


    /**
     * Gets the author of the document
     *
     * @return  author of document or null
     */
    public final String getAuthor() {
        return documentAuthor;
    }


    /**
     * Gets the title of the document
     *
     * @return  title of document or null
     */
    public final String getTitle() {
        return documentTitle;
    }


    /**
     * Gets the text of the document
     *
     * @return  text of document or null
     */
    public final String getText() {
        return documentText;
    }


    /**
     * Gets the summary of the document
     *
     * @return  summary of document or null
     */
    public final String getSummary() {
        String documentSummary = null;

        // build summary from text
        if (documentText != null) {
            if (documentText.length() <= MAX_SUMMARY_LENGTH) {
                documentSummary = documentText;
            }
            else {
                documentSummary = documentText.substring(0, MAX_SUMMARY_LENGTH - 3) + "...";
            }
        }

        return documentSummary;
    }


    /**
     * Gets the keywords of the document
     *
     * @return  keywords of document or null
     */
    public final String getKeywords() {
        return documentKeywords;
    }
}
