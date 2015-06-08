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
package org.jab.docsearch.spider;

import org.apache.log4j.Logger;
import org.jab.docsearch.utils.Utils;

/**
 * Class SpiderUrl
 *
 * @version $Id: SpiderUrl.java 163 2011-01-06 21:37:42Z henschel $
 */
public final class SpiderUrl {
    /**
     * Log4J logger
     */
    private final Logger logger = Logger.getLogger(getClass().getName());
    private String url;
    private boolean isSpidered;
    private boolean isDeadlink;
    private String contentType = "UNKNOWN";
    private String errorCode = "";
    private long lastModified;
    private long size;
    private String md5 = "";
    private int indexLocation = -1;


    /**
     * Constructor
     *
     * @param line
     */
    public SpiderUrl(final String line) {
        if (line.indexOf("|") == -1) {
            url = line;
        }
        else {
            // loads each value seperated by a "|"
            int curPart = 0;
            int lineLen = line.length();
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < lineLen; i++) {
                char c = line.charAt(i);
                if ((c == '|') || (i == lineLen - 1)) {
                    if (i == lineLen - 1) {
                        temp.append(c);
                    }

                    switch (curPart) {
                        case 0: // href
                            url = temp.toString();
                            break;
                        case 1: // time
                            try {
                                this.lastModified = Long.parseLong(temp.toString());
                            }
                            catch (NumberFormatException nfe) {
                                logger.error("SpiderUrl() failed", nfe);
                            }
                            break;
                        case 2: // size
                            try {
                                size = Long.parseLong(temp.toString());
                            }
                            catch (NumberFormatException nfe) {
                                logger.error("SpiderUrl() failed", nfe);
                            }
                            break;
                        case 3: // type
                            contentType = temp.toString();
                            break;
                        default: // md5
                            md5 = temp.toString();
                            break;
                    }
                    curPart++;
                    temp = new StringBuilder();
                }
                else {
                    temp.append(c);
                }
            }
        }
    }


    /**
     * Sets MD5
     *
     * @param md5
     */
    public void setMd5(final String md5) {
        this.md5 = md5;
    }


    /**
     * Gets MD5
     *
     * @return MD5
     */
    public String getMd5() {
        return md5;
    }


    /**
     * Gets URL
     *
     * @return URL
     */
    public String getUrl() {
        return Utils.replaceAll("%7C", url, "|");
    }


    /**
     * Gets is spidered
     *
     * @return  Flag is spidered
     */
    public boolean getIsSpidered() {
        return isSpidered;
    }


    /**
     * Sets is spidered
     *
     * @param isSpidered
     */
    public void setSpidered(final boolean isSpidered) {
        this.isSpidered = isSpidered;
    }


    /**
     * Gets is deas link
     *
     * @return Flag is dead link
     */
    public boolean getIsDeadLink() {
        return isDeadlink;
    }


    /**
     * Sets is dead link
     *
     * @param isDeadlink
     */
    public void setIsDeadLink(final boolean isDeadlink) {
        this.isDeadlink = isDeadlink;
    }


    /**
     * Gets error code
     *
     * @return  Error code
     */
    public String getErrorCode() {
        return errorCode;
    }


    /**
     * Sets error code
     *
     * @param errorCode
     */
    public void setErrCode(final String errorCode) {
        this.errorCode = errorCode;
    }


    /**
     * Sets last modified
     * @param lastModified
     */
    public void setLastModified(final long lastModified) {
        this.lastModified = lastModified;
    }


    /**
     * Gets last modified
     *
     * @return  Last modifed
     */
    public long getLastModified() {
        return lastModified;
    }


    /**
     * Sets size
     *
     * @param size
     */
    public void setSize(final long size) {
        this.size = size;
    }


    /**
     * Gets size
     *
     * @return  Size
     */
    public long getSize() {
        return size;
    }


    /**
     * Sets content type
     *
     * @param contentType
     */
    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }


    /**
     * Gets content type
     *
     * @return  Content type
     */
    public String getContentType() {
        return contentType;
    }


    /**
     * Sets index location
     *
     * @param indexLocation
     */
    public void setIndexLocation(final int indexLocation) {
        this.indexLocation = indexLocation;
    }


    /**
     * Gets index location
     *
     * @return  Index location
     */
    public int getIndexLocation() {
        return indexLocation;
    }
}
