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
package org.jab.docsearch.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jab.docsearch.spider.SpiderUrl;

/**
 * This class contains useful methods for network
 *
 * @version $Id: NetUtils.java 163 2011-01-06 21:37:42Z henschel $
 */
public final class NetUtils {
    /**
     * Log4J
     */
    private final Logger logger = Logger.getLogger(NetUtils.class.getName());
    /**
     * DocSearcher user agent
     */
    private final String USER_AGENT = "DocSearcher " + I18n.getString("ds.version");


    /**
     * Gets URL size (content)
     *
     * @param url  URL for connect
     * @return     size in bytes of a url or 0 if broken or timed out connection
     */
    public long getURLSize(final String url) {
        try {
            URL tmpURL = new URL(url);
            URLConnection conn = tmpURL.openConnection();

            // set connection parameter
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setUseCaches(false);
            conn.setRequestProperty("User-Agent", USER_AGENT);

            // connect
            conn.connect();

            long contentLength = conn.getContentLength();

            if (logger.isDebugEnabled()) {
                logger.debug("getURLSize() content lentgh=" + contentLength + " of URL='" + url + "'");
            }

            return contentLength;
        }
        catch (IOException ioe) {
            logger.error("getURLSize() failed for URL='" + url + "'", ioe);
            return 0;
        }
    }


    /**
     * Gets URL modified date as long
     *
     * @param url  URL to connect
     * @return         date of URLs modification or 0 if an error occurs
     */
    public long getURLModifiedDate(final String url) {
        try {
            URL tmpURL = new URL(url);
            URLConnection conn = tmpURL.openConnection();

            // set connection parameter
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setUseCaches(false);
            conn.setRequestProperty("User-Agent", USER_AGENT);

            // connect
            conn.connect();

            long modifiedDate = conn.getLastModified();

            if (logger.isDebugEnabled()) {
                logger.debug("getURLModifiedDate() modified date=" + modifiedDate + " of URL='" + url + "'");
            }

            return modifiedDate;
        }
        catch (IOException ioe) {
            logger.error("getURLModifiedDate() failed for URL='" + url + "'", ioe);
            return 0;
        }
    }


    /**
     * Downloads URL to file
     *
     * Content type, content length, last modified and md5 will be set in SpiderUrl
     *
     * @param spiderUrl  URL to download
     * @param file       URL content downloads to this file
     * @return           true if URL successfully downloads to a file
     */
    public boolean downloadURLToFile(final SpiderUrl spiderUrl, final String file) {

        boolean downloadOk = false;
        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        try {
            // if the file downloads - save it and return true
            URL url = new URL(spiderUrl.getUrl());
            URLConnection conn = url.openConnection();

            // set connection parameter
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setUseCaches(false);
            conn.setRequestProperty("User-Agent", USER_AGENT);

            // connect
            conn.connect();

            // content type
            spiderUrl.setContentType(getContentType(conn));

            // open streams
            inputStream = new BufferedInputStream(conn.getInputStream());
            outputStream = new BufferedOutputStream(new FileOutputStream(file));

            // copy data from URL to file
            long size = 0;
            int readed = 0;
            while ((readed = inputStream.read()) != -1) {
                size++;
                outputStream.write(readed);
            }

            // set values
            spiderUrl.setContentType(getContentType(conn));
            spiderUrl.setSize(size);

            downloadOk = true;
        }
        catch (IOException ioe) {
            logger.fatal("downloadURLToFile() failed for URL='" + spiderUrl.getUrl() + "'", ioe);
            downloadOk = false;
        }
        finally {
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(inputStream);
        }

        // set values
        if (downloadOk) {
            spiderUrl.setMd5(FileUtils.getMD5Sum(file));
        }

        return downloadOk;
    }


    /**
     * Downloads URL to file
     *
     * @param conn  URL Connection
     * @param file  URL content downloads to this file
     * @return      true if URL successfully downloads to a file
     */
    public boolean downloadURLToFile(URLConnection conn, final String file) {

        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        try {
            // open streams
            inputStream = new BufferedInputStream(conn.getInputStream());
            outputStream = new BufferedOutputStream(new FileOutputStream(file));

            // copy data from URL to file
            int readed = 0;
            while ((readed = inputStream.read()) != -1) {
                outputStream.write(readed);
            }

            return true;
        }
        catch (IOException ioe) {
            logger.fatal("downloadURLToFile() failed to download", ioe);
            return false;
        }
        finally {
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(inputStream);
        }
    }


    /**
     * Get SpiderUrl status
     *
     * Content type, content length, last modified and md5 will be set in SpiderUrl if url is changed
     *
     * @param spiderUrl  URL to check
     * @param file       URL content downloads to to this file
     * @return           -1 if link is broken, 0 if the file is unchanged or 1 if the file
     *                   is different... part of caching algoritm.
     */
    public int getURLStatus(final SpiderUrl spiderUrl, final String file) {
        // -1 means broken link
        //  0 means same file
        //  1 means changed

        int status;

        try {
            // attempt to obtain status from date
            URL url = new URL(spiderUrl.getUrl());
            URLConnection conn = url.openConnection();

            // set connection parameter
            conn.setDoInput(true);
            conn.setDoOutput(false);
            conn.setUseCaches(false);
            conn.setRequestProperty("User-Agent", USER_AGENT);

            // connect
            conn.connect();

            // content type
            spiderUrl.setContentType(getContentType(conn));

            // check date
            long spiDate = spiderUrl.getLastModified();
            long urlDate = conn.getLastModified();

            // same if date is equal and not zero
            if (spiDate == urlDate && urlDate != 0) {
                // the file is not changed
                status = 0;
            }
            else {
                // download the URL and compare hashes
                boolean downloaded = downloadURLToFile(conn, file);
                if (downloaded) {
                    // download ok

                    // compare file hashes
                    String fileHash = FileUtils.getMD5Sum(file);

                    if (fileHash.equals(spiderUrl.getMd5())) {
                        // same
                        status = 0;
                    }
                    else {
                        // changed
                        status = 1;

                        // set changed values
                        spiderUrl.setSize(FileUtils.getFileSize(file));
                        spiderUrl.setLastModified(urlDate);
                        spiderUrl.setMd5(fileHash);
                    }
                }
                else {
                    // download failed

                    // broken link
                    status = -1;
                }
            }
        }
        catch (IOException ioe) {
            logger.error("getURLStatus() failed for URL='" + spiderUrl.getUrl() + "'", ioe);
            status = -1;
        }

        return status;
    }


    /**
     * Gets content type from connection and set default if null
     *
     * @param con  URL connection
     * @return     Content type
     */
    public String getContentType(final URLConnection con) {
        String contentType = con.getContentType();

        if (contentType != null) {
            logger.debug("getContentType() content type is '" + contentType + "'");
        }
        else {
            logger.debug("getContentType() Null content type - assuming html anyway.");
            // FIXME check this, if we need this default value!!!
            contentType = "html";
        }

        return contentType;
    }
}
