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

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Layout;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.hwpf.extractor.WordExtractor;

/**
 * Class for handling MS Word files.
 *
 * @version $Id: Word.java 172 2012-09-14 15:24:32Z henschel $
 */
public class Word
        extends AbstractConverter
        implements ConverterInterface {

    private final String filename;


    /**
     * Constructor
     *
     * @param filename
     */
    public Word(String filename) {
        this.filename = filename;
    }


    /**
     * @see ConverterInterface#parse()
     */
    @Override
	public void parse()
            throws ConverterException {
        if (filename == null) {
            log.error("parse() filename is null");
            throw new ConverterException("Word::parse() filename is null");
        }

        // get metadata and text
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(filename);

            WordExtractor we = new WordExtractor(fin);

            // get meta data
            SummaryInformation si = we.getSummaryInformation();
            documentAuthor = si.getAuthor();
            documentTitle = si.getTitle();
            documentKeywords = si.getKeywords();

            // get text
            documentText = we.getText();
        }
        catch (IOException ioe) {
            log.error("parse() failed at Word file=" + filename, ioe);
            throw new ConverterException("Word::parse() failed at Word file=" + filename, ioe);
        }
        catch (Exception e) {
            log.error("parse() failed at Word file=" + filename, e);
            throw new ConverterException("Word::parse() failed", e);
        }
        finally {
            IOUtils.closeQuietly(fin);
        }

        if (log.isDebugEnabled()) {
            log.debug("parse() Word file='" + filename + "'" + Layout.LINE_SEP +
                    "title='" + documentTitle + "'" + Layout.LINE_SEP +
                    "author='" + documentAuthor + "'" + Layout.LINE_SEP +
                    "keywords='" + documentKeywords + "'");
        }
    }
}
