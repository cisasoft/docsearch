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
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;

/**
 * This class allows POI excel text extraction to function with string buffers.
 *
 * @version $Id: ExcelListener.java 172 2012-09-14 15:24:32Z henschel $
 */
public class ExcelListener implements HSSFListener {
    /**
     * Logging
     */
    private final Logger log = Logger.getLogger(getClass());

    /**
     * Excel content
     */
    private final StringBuilder excelText = new StringBuilder();


    /**
     * This method listens for incoming records and handles them as required.
     *
     * @param record  The record that was found while reading.
     */
    @Override
	public void processRecord(Record record) {
        try {
            switch (record.getSid()) {
                case NumberRecord.sid: {
                    NumberRecord numrec = (NumberRecord) record;

                    excelText.append(numrec.getValue());
                    excelText.append('\n');

                    break;
                }
                // SSTRecords store a array of unique strings used in Excel
                case SSTRecord.sid: {
                    SSTRecord sstrec = (SSTRecord) record;

                    for (int k = 0; k < sstrec.getNumUniqueStrings(); k++) {
                        excelText.append(sstrec.getString(k));
                        excelText.append('\n');
                    }

                    break;
                }
            }
        }
        catch (Exception e) {
            log.error("processRecords() failed for Excel file", e);
        }
    }


    /**
     * Get text of the Excel document
     *
     * @return text of the Excel document
     */
    public StringBuilder getText() {
        return excelText;
    }
}
