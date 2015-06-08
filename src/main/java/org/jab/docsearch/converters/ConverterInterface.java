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


/**
 * Interface ConverterInterface
 *
 * @version $Id: ConverterInterface.java 140 2009-09-22 19:19:56Z henschel $
 */
public interface ConverterInterface {
    /**
     * Gets the author of the document.
     *
     * @return  author of document or null
     */
    String getAuthor();


    /**
     * Gets the title of the document.
     *
     * @return  title of document or null
     */
    String getTitle();


    /**
     * Gets the text of the document.
     *
     * @return  text of document or null
     */
    String getText();


    /**
     * Gets the summary of the document.
     *
     * @return  summary of document or null
     */
    String getSummary();


    /**
     * Parse Document
     *
     * @throws ConverterException  Converter problem
     */
    void parse() throws ConverterException;
}