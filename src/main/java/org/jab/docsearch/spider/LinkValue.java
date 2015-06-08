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
 * Class LinkValue
 *
 * @version $Id: LinkValue.java 162 2011-01-06 17:40:38Z henschel $
 */
public final class LinkValue {
    /**
     * Log4J logger
     */
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final String href;
    private final String page;
    private String realLink;
    private boolean convertedLink = false;


    /**
     * Constructor
     *
     * @param page
     * @param href
     */
    protected LinkValue(final String page, final String href) {
        this.href = href;
        this.page = page;
    }


    /**
     * Gets real link
     *
     * @return  real link
     */
    protected String getRealLink() {
        if (logger.isDebugEnabled()) {
            logger.debug("getRealLink() page='" + page + "' href='" + href + "'");
        }

        if (convertedLink) {
            return realLink;
        }
        else {
            String lowerHref = href.toLowerCase();
            String domainUrl = Utils.getDomainURL(page);
            int domLen = domainUrl.length();

            String pageFold = page;
            if (! page.endsWith("/")) {
                pageFold = page.substring(0, page.lastIndexOf("/") + 1);
            }
            else {
                pageFold = page;
            }

            // check for full link
            if (lowerHref.startsWith("http://")) {
                realLink = href;
            }
            else {
                // check link started with /
                if (lowerHref.startsWith("/")) {
                    // go up to root and make rel link
                    realLink = domainUrl + href.substring(1, href.length());
                }
                // check link started with ./
                else if (lowerHref.startsWith("./")) {
                    realLink = pageFold + href.substring(2, href.length());
                }
                // check link started with ../
                else if (lowerHref.startsWith("../")) {

                	// go up a folder - or so to make rel link
                    boolean folderFound = false;
                    String newFolder = pageFold;
                    String dotPoint = href;

                    // remove last slash
                    if (newFolder.endsWith("/")) {
                        int lastSlash = newFolder.lastIndexOf("/");
                        newFolder = newFolder.substring(0, lastSlash);
                    }

                    // step during directories
                    do {
                        if (dotPoint.startsWith("../")) {
                            dotPoint = dotPoint.substring(3, dotPoint.length());
                        }
                        else {
                            folderFound = true;
                            break;
                        }
                        int lastSlash = newFolder.lastIndexOf("/");
                        if (lastSlash != -1 && newFolder.length() > domLen) {
                            newFolder = newFolder.substring(0, lastSlash);
                        }
                        else {
                            folderFound = true;
                            break;
                        }
                    }
                    while (! folderFound);

                    realLink = newFolder + '/' + dotPoint;
                }
                else {
                    // assume rel link at page location
                    if (! pageFold.endsWith("/")) {
                        realLink = pageFold + '/' + href;
                    }
                    else {
                        realLink = pageFold + href;
                    }
                }
            } // end for not a full link

            if (logger.isDebugEnabled()) {
                logger.debug("getRealLink() real link='" + realLink + "' pagefold='" + pageFold + "'");
            }

            convertedLink = true;
            return realLink;
        }
    }
}
