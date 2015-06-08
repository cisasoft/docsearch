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

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;

import org.apache.log4j.Logger;

/**
 * Class RtfToText
 *
 * @version $Id: RtfToText.java 100 2009-02-24 21:18:06Z henschel $
 */
public class RtfToText {
    /**
     * Logging
     */
    private final Logger log = Logger.getLogger(getClass());
    private final String filename;
    private final String tempFile;


    /**
     * Constructor
     *
     * @param filename
     * @param tempFile
     */
    public RtfToText(String filename, String tempFile) {
        this.filename = filename;
        this.tempFile = tempFile;
    }


    /**
     * Parse RTF document
     *
     * @throws IOException
     */
    public void parse() throws IOException {
        FileReader reader = null;
        FileWriter writer = null;
        try {
            reader = new FileReader(filename);
            writer = new FileWriter(tempFile);

            DefaultStyledDocument doc = new DefaultStyledDocument();

            new RTFEditorKit().read(reader, doc, 0);

            writer.write(doc.getText(0, doc.getLength()));
        }
        catch (BadLocationException ble) {
            log.fatal("parse() failed with BadLocationException", ble);
        }
        catch (IOException ioe) {
            log.fatal("parse() failed with IOException", ioe);
            throw ioe;
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException ioe) {
                    log.fatal("parse() can't close FileReader", ioe);
                }
            }

            if (writer != null) {
                try {
                    writer.close();
                }
                catch (IOException ioe) {
                    log.fatal("parse() can't close FileWriter", ioe);
                }
            }
        }
    }
}
