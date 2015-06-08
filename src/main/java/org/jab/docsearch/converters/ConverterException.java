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
 * Thrown to indecated that the document converter has a problem.
 *
 * @author henschel
 *  @version $Id: ConverterException.java 140 2009-09-22 19:19:56Z henschel $
 */
public class ConverterException
        extends Exception {
    /**
	 * Serial
	 */
	private static final long serialVersionUID = -1038464884026811410L;


	/**
     * @see Exception#Exception(String)
     */
    public ConverterException(String message) {
        super(message);
    }


    /**
     * @see Exception#Exception(String, Throwable)
     */
    public ConverterException(String message, Throwable cause) {
        super(message, cause);
    }
}
