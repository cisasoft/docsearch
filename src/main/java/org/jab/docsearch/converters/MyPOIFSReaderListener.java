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

import org.apache.poi.hpsf.PropertySet;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.SummaryInformation;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderEvent;
import org.apache.poi.poifs.eventfilesystem.POIFSReaderListener;

/**
 * Class MyPOIFSReaderListener
 *
 * @version $Id: MyPOIFSReaderListener.java 172 2012-09-14 15:24:32Z henschel $
 */
class MyPOIFSReaderListener implements POIFSReaderListener {
    /**
     * Title of document
     */
    private String title;
    /**
     * Author of document
     */
    private String author;
    /**
     * Keywords of document
     */
    private String keywords;


    /**
     * Implementation for  POIFSReaderListener
     */
    @Override
	public void processPOIFSReaderEvent(final POIFSReaderEvent e) {
        SummaryInformation si = null;
        try {
            PropertySet ps = PropertySetFactory.create(e.getStream());
            si = (SummaryInformation) ps;
        }
        catch (Exception ex) {
            throw new RuntimeException("Property set stream \"" + e.getPath() + e.getName() + "\": " + ex);
        }

        // title
        title = si.getTitle();

        // author
        author = si.getLastAuthor();

        // keywords
        keywords = si.getKeywords();
    }


    /**
     * Gets the title of the document
     *
     * @return  title of document or null
     */
    public String getTitle() {
        return title;
    }


    /**
     * Gets the author of the document
     *
     * @return  author of document or null
     */
    public String getAuthor() {
        return author;
    }


    /**
     * Gets the keywords of the document
     *
     * @return  keywords of document or null
     */
    public String getKeywords() {
        return keywords;
    }
}
