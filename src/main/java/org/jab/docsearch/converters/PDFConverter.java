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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Layout;
import org.pdfbox.pdfparser.PDFParser;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.pdmodel.encryption.AccessPermission;
import org.pdfbox.util.PDFTextStripper;

/**
 * This class extract text of PDF document
 *
 * @version $Id: PDFConverter.java 172 2012-09-14 15:24:32Z henschel $
 */
public class PDFConverter
        extends AbstractConverter
        implements ConverterInterface {

    private final String filename;


    /**
     * Cronstructor
     *
     * @param fileName  PDF file
     */
    public PDFConverter(String fileName) {
        this.filename = fileName;
    }


    /**
     * @see ConverterInterface#parse()
     */
    @Override
	public void parse()
            throws ConverterException {
        if (StringUtils.isEmpty(filename)) {
            log.error("parse() filename is null");
            throw new ConverterException("PDFConverter::parse() filename is null");
        }

        // PD Document
        PDDocument document = null;
        Writer output = null;
        try {
            document = getPDDocument();

            // check document is readable
            AccessPermission ap = document.getCurrentAccessPermission();
            if (! ap.canExtractContent()) {
                log.info("parse() Document (" + filename + ") isn't readable for DocSearcher.");
                throw new ConverterException("parse() can't read PDF file");
            }

            // write the text to temp file
            try {
                log.debug("parse() Attempting to extract text from (" + filename + ")");

                output = new StringWriter();

                PDFTextStripper stripper = new PDFTextStripper();
                stripper.writeText(document, output);

                log.debug("parse() Successfully stripped out text from (" + filename + ")");
            }
            catch (IOException ioe) {
                log.error("parse() failed", ioe);
                throw new ConverterException("PDFConverter::parse() failed", ioe);
            }

            // get the meta data
            PDDocumentInformation info = document.getDocumentInformation();
            documentTitle = info.getTitle();
            documentAuthor = info.getAuthor();
            documentKeywords = info.getKeywords();
            if (document != null) {
                documentText = output.toString();
            }
        }
        catch (IOException ioe) {
            log.error("parse() failed", ioe);
            throw new ConverterException("parse() failed", ioe);
        }
        finally {
            // close stream
            IOUtils.closeQuietly(output);

            // close document
            try {
                if (document != null) {
                    document.close();
                }
            }
            catch (IOException ioe) {
                log.fatal("parse() can't close PDDocument", ioe);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("parse() PDF file='" + filename + "'" + Layout.LINE_SEP +
                    "title='" + documentTitle + "'" + Layout.LINE_SEP +
                    "author='" + documentAuthor + "'" + Layout.LINE_SEP +
                    "keywords='" + documentKeywords + "'");
        }
    }


    /**
     * Method parseDocument
     *
     * @return  PDDocument
     * @throws IOException  if error
     */
    private PDDocument getPDDocument()
            throws IOException {

        InputStream input = null;
        try {
            // get file as stream
            input = new FileInputStream(filename);

            // init PDFParser with stream
            PDFParser parser = new PDFParser(input);
            parser.setTempDirectory(new File(System.getProperty("java.io.tmpdir")));

            // parse
            parser.parse();

            // return Document
            return parser.getPDDocument();
        }
        finally {
            IOUtils.closeQuietly(input);
        }
    }
}
