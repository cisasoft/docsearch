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
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.poifs.eventfilesystem.POIFSReader;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * Class for handling MS Excel files
 *
 * @version $Id: Excel.java 172 2012-09-14 15:24:32Z henschel $
 */
public class Excel
        extends AbstractConverter
        implements ConverterInterface {

    private final String filename;


    /**
     * Contructor
     *
     * @param filename
     */
    public Excel(String filename) {
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

        // get meta data
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(filename);

            POIFSReader r = new POIFSReader();
            MyPOIFSReaderListener mpfsrl = new MyPOIFSReaderListener();
            r.registerListener(mpfsrl, "\005SummaryInformation");
            r.read(fin);

            fin.close();

            // get meta data
            documentTitle = mpfsrl.getTitle();
            documentAuthor = mpfsrl.getAuthor();
            documentKeywords = mpfsrl.getKeywords();
        }
        catch (IOException ioe) {
            log.error("parse() failed at Excel file=" + filename, ioe);
            throw new ConverterException("Excel::parse() failed at Excel file=" + filename, ioe);
        }
        catch (Exception e) {
            log.error("parse() failed at Excel file=" + filename, e);
            throw new ConverterException("Excel::parse() failed", e);
        }
        finally {
            IOUtils.closeQuietly(fin);
        }

        if (log.isDebugEnabled()) {
            log.debug("parse() Excel file='" + filename + "'" + Layout.LINE_SEP +
                    "title='" + documentTitle + "'" + Layout.LINE_SEP +
                    "author='" + documentAuthor + "'" + Layout.LINE_SEP +
                    "keywords='" + documentKeywords + "'");
        }

        // get text
        DocumentInputStream din = null;
        ExcelListener el = new ExcelListener();
        try {
            // proceed to write to file
            // create a new file input stream with the input file specified
            // at the command line
            fin = new FileInputStream(filename);

            POIFSFileSystem poifs = new POIFSFileSystem(fin);
            din = poifs.createDocumentInputStream("Workbook");
            HSSFRequest req = new HSSFRequest();
            req.addListenerForAllRecords(el);
            HSSFEventFactory factory = new HSSFEventFactory();
            factory.processEvents(req, din);

            fin.close();

            // get text
            documentText = el.getText().toString();
        }
        catch (IOException ioe) {
            log.error("parse() failed at Excel file=" + filename, ioe);
            throw new ConverterException("Excel::parse() failed at Excel file=" + filename, ioe);
        }
        catch (Exception e) {
            log.error("parse() failed", e);
            throw new ConverterException("Excel::parse() failed", e);
        }
        finally {
            IOUtils.closeQuietly(din);
            IOUtils.closeQuietly(fin);
        }
    }
}
