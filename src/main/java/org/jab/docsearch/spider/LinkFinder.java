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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.jab.docsearch.DocSearch;
import org.jab.docsearch.DocSearcherIndex;
import org.jab.docsearch.FileEnvironment;
import org.jab.docsearch.utils.DateTimeUtils;
import org.jab.docsearch.utils.FileUtils;
import org.jab.docsearch.utils.I18n;
import org.jab.docsearch.utils.NetUtils;
import org.jab.docsearch.utils.Utils;

/**
 * Class LinkFinder
 *
 * FIXME NPE if ds is null, but this is only if one constructor called!
 *
 * @version $Id: LinkFinder.java 172 2012-09-14 15:24:32Z henschel $
 */
public final class LinkFinder {
    /**
     * Log4J logger
     */
    private final Logger logger = Logger.getLogger(getClass().getName());
    /**
     * FileEnvironment
     */
    private final FileEnvironment fEnv = FileEnvironment.getInstance();
    /**
     * NetUtils
     */
    private final NetUtils netUtils = new NetUtils();

    private final String USER_NAME = System.getProperty("user.name");
    private int numSkips; // currently not used
    private DocSearch ds = null;
    private IndexWriter iw;
    private int numDeletes;
    private int numChanges;
    private int numNew;
    private int numUnChanged;
    private int numMetaNoIdx;
    private int numFails;
    private long maxFileSizeToGet = 600000;
    private final String pageName;
    private String downloadFileDir = System.getProperty("java.io.tmpdir");
    private DocSearcherIndex dsi;
    private String baseUrlFolder = "";
    private String domainUrl = "";
    private String outFile;
    private String outBadFile;
    private ArrayList<SpiderUrl> links = new ArrayList<SpiderUrl>();
    private final String[] htmlTypes = {
    		"/",
            ".htm", ".html", ".jhtml",
            ".shtm", ".shtml",
            ".asp", ".aspx",
            ".php", ".php3", ".php4", ".php5",
            ".jsp",
            ".cfm", ".cfml",
            ".do"};
    private final String[] nonHtmlTypes = {
            ".zip",
            ".jpg",
            ".bmp",
            ".gif",
            ".db",
            ".cat",
            ".wmf",
            ".tif",
            ".tiff",
            ".swf",
            ".ncd",
            ".pdd",
            ".png",
            ".ppt",
            ".jpeg",
            ".mdb",
            ".msg",
            ".mpp",
            ".log" };
    private final String[] bogusDirs = {
            "_vti_",
            "_private",
            "file:" };
    private int maxLinksToFind = 5000;


    /**
     * Constructor (only for test)
     *
     * @param pageName
     * @param outFile
     */
    public LinkFinder(final String pageName, final String outFile, final String outBadFile) {
        this.pageName = pageName;
        this.outFile = outFile;
        this.outBadFile = outBadFile;

        // init
        init();
    }


    /**
     * Constructor
     *
     * @param pageName
     * @param maxLinksToFind
     * @param ds
     * @param dsi
     * @param iw
     */
    public LinkFinder(final String pageName, final int maxLinksToFind, final DocSearch ds, final DocSearcherIndex dsi,
    		final IndexWriter iw) {
        this.pageName = pageName;
        this.maxLinksToFind = maxLinksToFind;
        this.ds = ds;
        this.dsi = dsi;
        this.iw = iw;

        if (ds != null) {
            downloadFileDir = ds.tempDir;
            maxFileSizeToGet = ds.getMaxFileSize();
        }

        // init
        init();
    }


    /**
     * Constructor
     *
     * @param pageName
     * @param maxLinksToFind
     * @param ds
     * @param dsi
     * @param links
     */
    public LinkFinder(final String pageName, final int maxLinksToFind, final DocSearch ds, final DocSearcherIndex dsi,
    		final ArrayList<SpiderUrl> links) {
        this.pageName = pageName;
        this.maxLinksToFind = maxLinksToFind;
        this.ds = ds;
        this.dsi = dsi;
        this.links = links;

        if (ds != null) {
            downloadFileDir = ds.tempDir;
            maxFileSizeToGet = ds.getMaxFileSize();
        }

        // init
        init();
    }


    /**
     * Method init
     */
    private void init() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                @Override
				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    // nothing
                }

                @Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    // nothing
                }
            }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        }
        catch (Exception e) {
            logger.error("init() failed", e);
        }
    }


    /**
     * Checks URL is a HTML file.
     *
     * @param url  url string
     * @return     true is HTML
     */
    private boolean isHtml(final String url) {
        String lowerUrl = url.toLowerCase();

        // if dynamic, than html
        // TODO dynamic url isn't always a html
        if (url.indexOf("?") != -1) {
            return true;
        }

        // check file suffix
        for (String tmp : htmlTypes) {
            if (lowerUrl.endsWith(tmp)) {
                return true;
            }
        }

        return false;
    }


    /**
     * Checks page is skip type
     *
     * @param url  url string
     * @return     true is skip type
     */
    private boolean skipType(final String url) {
        String lowerUrl = url.toLowerCase();

        // check file suffix
        for (String tmp: nonHtmlTypes) {
            if (lowerUrl.endsWith(tmp)) {
                return true;
            }
        }

        return false;
    }


    /**
     * Checks for bogus directory
     *
     * @param url  url string
     * @return     true is bogus directory
     */
    private boolean hasBogusDirs(final String url) {
        String lowerUrl = url.toLowerCase();

        // check file suffix
        for (String tmp: bogusDirs) {
            if (lowerUrl.indexOf(tmp) != -1) {
                return true;
            }
        }

        return false;
    }


    /**
     * Gets the next URL number
     *
     * @return  next URL number or -1 if no next URL available
     */
    private int getNextUrlNo() {
        int curPos = 0;

        for (SpiderUrl spy : links) {
            if (! spy.getIsSpidered() && ! spy.getIsDeadLink()) {
            	return curPos;
            }
            curPos++;
        }

        return -1;
    }


    /**
     * Gets link name by number
     *
     * @param linkNumber  link number in list
     * @return            link name
     */
    private String getLinkNameByNo(final int linkNumber) {
        SpiderUrl spy = links.get(linkNumber);

        return spy.getUrl();
    }


    /**
     * Gets link URL by number
     *
     * @param linkNumber  link number in list
     * @return            URL
     */
    private SpiderUrl getSpiderUrl(final int linkNumber) {
        SpiderUrl spy = links.get(linkNumber);

        return spy;
    }


    /**
     * Get all links from page
     */
    public void getAllLinks() {
        // writes links from a page out to a file
        String urlStr = pageName;
        String shortUrl = "";
        numUnChanged = 0;
        numSkips = 0;
        int numSuccesses = 0;
        int numFailed = 0;
        int numNoRobots = 0;
        addLink(urlStr);
        domainUrl = Utils.getDomainURL(urlStr);
        if (logger.isDebugEnabled()) {
            logger.debug("getAllLinks() domain url='" + domainUrl + "'");
        }
        SpiderUrl curl = new SpiderUrl(urlStr);
        baseUrlFolder = Utils.getBaseURLFolder(urlStr);
        int curLinkNo = 0;
        boolean completedSpider = false;
        boolean isDead = false;
        int curPread = 0;
        if (ds != null) {
            ds.setIsWorking(true);
            ds.setProgressMax(maxLinksToFind);
            ds.setCurProgressMSG("Spidering Files...");
        }
        int numSpidered = 0;
        int curSuccessNo = 0;

        // start spider
        while (curLinkNo != -1) {
        	BufferedInputStream urlStream = null;
        	FileOutputStream fileOutStream = null;

        	try {
                completedSpider = false;
                isDead = false;
                if (ds != null) {
                    ds.setCurProgress(curPread);
                    if (!ds.getIsWorking()) {
                        break;
                    }
                }
                curLinkNo = getNextUrlNo();
                if (curLinkNo == -1) {
                    logger.debug("getAllLinks() end of links reached.");
                    break;
                }
                else {
                    urlStr = getLinkNameByNo(curLinkNo);
                    logger.info("getAllLinks() analyzing page='" + urlStr + "'");
                    curl = getSpiderUrl(curLinkNo);
                }

                shortUrl = Utils.concatEnd(urlStr, 33);
                setStatus(I18n.getString("connecting_to") + " " + shortUrl);

                // open url
                URL url = new URL(urlStr);
                URLConnection conn = url.openConnection();
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setRequestProperty("User-Agent", "DocSearcher " + I18n.getString("ds.version"));
                conn.connect();
                urlStream = new BufferedInputStream(conn.getInputStream());

                // filesize
                int fileSize = conn.getContentLength();
                if (fileSize > maxFileSizeToGet) {
                    String ex = I18n.getString("skipping_file_too_big") + " (" + fileSize + " > " + maxFileSizeToGet + ") " + shortUrl;
                    setStatus(ex);
                    throw new Exception(ex);
                }

                setStatus(I18n.getString("downloading_uc") + "... " + shortUrl + " " + fileSize + " " + I18n.getString("bytes"));
                curl.setSize(fileSize);

                // last modified
                long curModified = conn.getLastModified(); // was .getDate();
                curl.setLastModified(curModified);

                // content type
                String curContentType = netUtils.getContentType(conn);
                curl.setContentType(curContentType);

                // build the value for downloadFile
                String dnldTmpName = getDownloadFileName(curl.getContentType(), urlStr.toLowerCase());
                String downloadFile = FileUtils.addFolder(downloadFileDir, dnldTmpName);

                // TODO it is better to use content type!
                boolean curIsWebPage = isHtml(urlStr.toLowerCase()) || (curContentType.toLowerCase().indexOf("html") != -1);

                logger.debug("getAllLinks() saving to " + downloadFile);
                fileOutStream = new FileOutputStream(downloadFile);
                int curSize = 0;
                int curI;
                int lastPercent = 0;
                StringBuilder tag = new StringBuilder();
                String link = null;
                boolean inTag = false;
                boolean getFileSizeFromStream = false;
                if (fileSize == -1) {
                    getFileSizeFromStream = true;
                }

                while ((curI = urlStream.read())!= -1) {
                	fileOutStream.write(curI);

                    curSize++;
                    if (ds != null) {
                        if (! ds.getIsWorking()) {
                            break;
                        }
                    }

                    // fix problem if filesize not in content length
                    if (getFileSizeFromStream) {
                        fileSize = curSize + urlStream.available();
                    }

                    // notify of download progress
                    if (curSize > 0 && (curSize % 10) == 0) {
                        int curPercent = (curSize * 100) / fileSize;
                        if (curPercent != lastPercent) {
                            lastPercent = curPercent;
                            setStatus(I18n.getString("downloading_uc") + "... : (" + shortUrl + ") --> " + curPercent + " %" + " ( " + (numSuccesses + numFailed + numNoRobots) + "/" + getNumLinksFound() + ")");
                        }
                    } // end for percent updates
                    else if (curSize % 40 == 0) {
                        setStatus(I18n.getString("downloading_uc") + "... : (" + shortUrl + ") --> " + curSize + " " + I18n.getString("bytes"));
                    }

                    // handle links
                    if (curIsWebPage) {
                        char c = (char) curI;
                        // LOOK AT THE TAGS

                        // start tag
                        if (c == '<') {
                            inTag = true;
                            tag = new StringBuilder();
                        }
                        // end tag
                        else if (c == '>') {
                            inTag = false;
                            tag.append(c);
                            String realTag = tag.toString();
                            String lowerTag = realTag.toLowerCase();

                        	// TODO fix problem with spaces before =

                            // link
                            if (lowerTag.startsWith("<a ")) {
                                link = Utils.getTagString("href=", realTag);
                                link = Utils.getNormalUrl(link);
                                doPossibleAdd(urlStr, link);
                            }
                            // area
                            else if (lowerTag.startsWith("<area")) {
                                link = Utils.getTagString("href=", realTag);
                                link = Utils.getNormalUrl(link);
                                doPossibleAdd(urlStr, link);
                            }
                            // TODO is in param realy a link?
                            else if (lowerTag.startsWith("<param")) {
                                String appletParam = Utils.getTagString("name=", realTag);
                                if (appletParam.toLowerCase().equals("url")) {
                                    link = Utils.getTagString("value=", realTag);
                                    link = Utils.getNormalUrl(link);
                                    doPossibleAdd(urlStr, link);
                                }
                            }
                        }

                        // in tag
                        if (inTag) {
                        	tag.append(c);
                        }
                    }

                    // filesize ok
                    if (getFileSizeFromStream && fileSize > maxFileSizeToGet) {
                        break;
                    }
                } // end while downloading
                curPread++;
                fileOutStream.close();
                urlStream.close();
                curl.setMd5(FileUtils.getMD5Sum(downloadFile));

                // now add out document
                if (ds != null) {
                    curSuccessNo = ds.idx.addDocToIndex(downloadFile, iw, dsi, false, curl);
                    switch (curSuccessNo) {
                        case 0: // good
                            numSuccesses++;
                            break;
                        case 1: // bad
                            numFailed++;
                            break;
                        case 2: // meta robots - no index
                            numNoRobots++;
                            break;
                    }
                }

                // delete temp file
                if (! FileUtils.deleteFile(downloadFile)) {
                    logger.warn("getAllLinks() can't delete file '" + downloadFile + "'");
                }

                numSpidered++;
                completedSpider = true;

                // max links found
                if (numSpidered > maxLinksToFind) {
                    break;
                }
        	}
            catch (Exception e) {
                logger.fatal("getAllLinks() failed", e);
                setStatus(I18n.getString("error") + " : " + e.toString());
                isDead = true;
            }
            finally {
            	// close resources
            	IOUtils.closeQuietly(urlStream);
            	IOUtils.closeQuietly(fileOutStream);

                curl.setSpidered(completedSpider);
                curl.setIsDeadLink(isDead);
                setStatus(I18n.getString("download_complete") + " " + shortUrl);
            }
        } // end for iterating over links

        if (ds != null) {
            ds.resetProgress();
        }
        saveAllLinks();

        logger.info("getAllLinks() " + numSpidered + " total web pages spidered for links.");

        showMessage(I18n.getString("spidering_complete") + " (" + Utils.concatStrToEnd(pageName, 28) + ") ",
                numSpidered + " " + I18n.getString("documents_indexed") + " " +
                getNumLinksFound() + " " + I18n.getString("links_found") + "\n\n" +
                numSuccesses + " " + I18n.getString("documents_spidered_successful") + "\n\n" +
                numFailed + " " + I18n.getString("documents_spidered_failed") + "\n\n" +
                numNoRobots + " " + I18n.getString("documents_not_spidered"));
    }


    /**
     * Has base url
     *
     * @param toCheck
     * @return
     */
    private boolean hasBaseUrl(final String toCheck) {
        boolean retB = false;
        if (baseUrlFolder.equals("")) {
            retB = true;
            logger.info("hasBaseUrl() no base url!");
        } else {
            String lowerCheck = toCheck.toLowerCase();
            String lowerBase = baseUrlFolder.toLowerCase();
            if (lowerCheck.startsWith(lowerBase))
                retB = true;
        }
        return retB;
    }


    /**
     * Sets status
     *
     * @param toSet
     */
    private void setStatus(final String toSet) {
        if (ds != null) {
            ds.setStatus(toSet);
        }
    }


    /**
     * Method doPossibleAdd
     *
     * @param page
     * @param link
     */
    private void doPossibleAdd(final String page, final String link) {
        if (logger.isDebugEnabled()) {
            logger.debug("doPossibleAdd('" + page + "', '" + link + "') entered");
        }

        String tLink = link.trim();

        if (! tLink.equals("") && tLink.indexOf("mailto:") == -1) {
        	// remove anchor
            if (tLink.indexOf("#") != -1) {
                int anchorPos = tLink.indexOf("#");
                tLink = tLink.substring(0, anchorPos);
            }
            // replace \\
            if (tLink.indexOf("\\") != -1) {
            	tLink = Utils.replaceAll("\\", tLink, "/");
            }
            // use string in ''
            if (tLink.startsWith("'") && tLink.endsWith("'")) {
            	tLink = tLink.substring(1, tLink.length() - 1);
            }

            LinkValue lv = new LinkValue(page, tLink);
            String realUrl = lv.getRealLink();

            //
            if (realUrl.toLowerCase().startsWith(domainUrl.toLowerCase()) && ! hasBogusDirs(realUrl) && hasBaseUrl(realUrl) && ! skipType(realUrl)) {
            	addLink(realUrl);
            } // end for url has domain prefix
            else {
                if (logger.isDebugEnabled()) {
                    logger.debug("doPossibleAdd() real url='" + realUrl + "' skipped because it doesn't start with '" + domainUrl + "', or url is not an indexible file type.");
                }
            }
        }
        else {
            if (logger.isDebugEnabled()) {
                logger.debug("doPossibleAdd() link='" + link + "' was a mailto or empty.");
            }
        }
    }


    /**
     * Adds link to list
     *
     * @param newUrl
     */
    private void addLink(final String newUrl) {
        if (logger.isDebugEnabled()) {
            logger.debug("addLink() try to add link='" + newUrl + "'");
        }

        if (StringUtils.isBlank(newUrl)) {
        	logger.warn("addLink() url is empty");
        	return;
        }


        int newLen = newUrl.length();
        int curPos = 0;
        boolean okToAdd = true;

        // search in list
        for (SpiderUrl spy : links) {
            String curUrlString = spy.getUrl();
            int curLen = curUrlString.length();

            // more speed,
            // check length, because links are length sorted (desc)
            if (newLen == curLen) {
                if (newUrl.equalsIgnoreCase(curUrlString)) {
                    okToAdd = false;
                }
            }
            else if (newLen > curLen) {
                break;
            }
            curPos++;
        }

        if (okToAdd) {
            SpiderUrl surl = new SpiderUrl(Utils.replaceAll("|", newUrl, "%7C"));
            links.add(curPos, surl);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("addLink() skipping double link='" + newUrl + "'");
            }
        }
    }


    /**
     * getNumLinksFound
     *
     * @return
     */
    private int getNumLinksFound() {
        return links.size();
    }


    /**
     * Saves all links
     */
    private void saveAllLinks() {
        logger.debug("saveAllLinks() entered");

        // now save the output to a file
        if (dsi != null) {
            outFile = fEnv.getSpiderIndexURLFile(dsi.getName());
            outBadFile = fEnv.getSpiderBadIndexURLFile(dsi.getName());
        }

        File saveFile = new File(outFile);
        File saveBadFile = new File(outBadFile);

        PrintWriter pw = null;
        PrintWriter bpw = null;
        try {
            pw = new PrintWriter(new FileWriter(saveFile));
            bpw = new PrintWriter(new FileWriter(saveBadFile));

            // save every link
            for (SpiderUrl spy : links) {
                if (! spy.getIsDeadLink()) {
                    pw.println(spy.getUrl() + '|' + spy.getLastModified() + '|' + spy.getSize() + '|' + spy.getContentType() + '|' + spy.getMd5());
                }
                else {
                    bpw.println(spy.getUrl());
                }
            }
            setStatus(links.size() + " total links found, " + numSkips + " links skipped.");
        }
        catch (IOException ioe) {
            logger.fatal("saveAllLinks() failed", ioe);
        }
        finally {
            if (pw != null) {
                pw.close();
            }
            if (bpw != null) {
                bpw.close();
            }
        }
    }


    /**
     * Shows message
     *
     * @param title
     * @param details
     */
    private void showMessage(final String title, final String details) {
        if (ds != null) {
            ds.showMessage(title, details);
        }
        else {
            logger.info("showMessage() " + title + "\n" + details);
        }
    }


    /**
     * Checks file for links
     *
     * @param origFile
     * @param thisPageName
     */
    private void checkFileForLinks(final String origFile, final String thisPageName) {
        String urlStr = thisPageName;
        String shortUrl = Utils.concatEnd(thisPageName, 33);
        setStatus(DocSearch.dsLkngFoLnx + " " + shortUrl);
        domainUrl = Utils.getDomainURL(urlStr);
        String realTag;
        String lowerTag;
        boolean inTag = false;
        String link = null;
        long lastPcnt = 0;
        File testFi = new File(origFile);
        long thisFileSize = testFi.length();
        long curDnldnd = 0;
        StringBuilder tagBuf = new StringBuilder();
        baseUrlFolder = Utils.getBaseURLFolder(urlStr);

        Reader in = null;
        try {
            // open file
            in = new BufferedReader(new InputStreamReader( new FileInputStream(origFile)));

            int ch;
            while ((ch = in.read()) > -1) {
                char c = (char) ch;
                curDnldnd++;

                if (thisFileSize > 0) {
                    if (curDnldnd % 10 == 0) {
                        long curPcnt = (curDnldnd * 100) / thisFileSize;
                        if (curPcnt != lastPcnt) {
                            lastPcnt = curPcnt;
                            setStatus(DocSearch.dsLkngFoLnx + " " + shortUrl + " --> (" + curPcnt + " %)");
                        }
                    }
                }

                // start tags
                if (c == '<') {
                    inTag = true;
                    tagBuf = new StringBuilder();
                }
                // end tags
                else if (c == '>') {
                    tagBuf.append(c);
                    realTag = tagBuf.toString();
                    lowerTag = realTag.toLowerCase();

                    if (lowerTag.startsWith("</a")) {
                        doPossibleAdd(thisPageName, link);
                    }
                    else if (lowerTag.startsWith("<area")) {
                        link = Utils.getTagString("href=", realTag);
                        link = Utils.getNormalUrl(link);

                        doPossibleAdd(thisPageName, link);
                    }
                    else if (lowerTag.startsWith("<param")) {
                        String appletParam = Utils.getTagString("name=", realTag);
                        if (appletParam.toLowerCase().equals("url")) {
                            link = Utils.getTagString("value=", realTag);
                            link = Utils.getNormalUrl(link);

                            doPossibleAdd(thisPageName, link);
                        }
                    }
                    //else if (lowerTag.startsWith("<a href=")) {
                    else if (lowerTag.startsWith("<a ")) {
                        link = Utils.getTagString("href=", realTag);
                        link = Utils.getNormalUrl(link);
                    }
                    inTag = false;
                }
                //
                if (inTag) {
                    tagBuf.append(c);
                }
            }

            in.close();
        } catch (IOException ioe) {
            logger.fatal("checkFileForLinks() failed", ioe);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }


    /**
     * Gets number of deletes
     *
     * @return
     */
    public int getNumDeletes() {
        return numDeletes;
    }


    /**
     * Gets number of updates
     *
     * @return
     */
    public int getNumUpdates() {
        return numChanges;
    }


    /**
     * Gets number of news
     * @return
     */
    public int getNumNew() {
        return numNew;
    }


    /**
     * Gets number of fails
     *
     * @return
     */
    public int getNumFails() {
        return numFails;
    }


    /**
     * Gets number of unchanges
     * @return
     */
    public int getNumUnchanged() {
        return numUnChanged;
    }


    /**
     * Gets number of meta no index
     *
     * @return
     */
    public int getNumMetaNoIdx() {
        return numMetaNoIdx;
    }


    /**
     * Method update
     *
     * @throws IOException
     */
    public void update() throws IOException {
        numDeletes = 0;
        numChanges = 0;
        numNew = 0;
        numFails = 0;
        numUnChanged = 0;
        numMetaNoIdx = 0;

        IndexReader ir = IndexReader.open(dsi.getIndexPath());
        int maxNumDocs = ir.maxDoc();
        int maxTotal = maxNumDocs + maxNumDocs / 10;
        int curDocNum = 0;
        if (ds != null) {
            ds.setStatus(DocSearch.dsTtlDxInIdx + " " + maxNumDocs);
            ds.setIsWorking(true);
            ds.setProgressMax(maxTotal * 2);
            ds.setCurProgressMSG("Spidering Files...");
        }

        // assign index location to urls currently in the index
        int lastFound = 0;
        for (SpiderUrl spy : links) {
            curDocNum++;

            if (ds != null) {
                ds.setCurProgress(curDocNum);
                if (! ds.getIsWorking()) {
                    break;
                }
            }

            String curFi = spy.getUrl();
            lastFound = ds.idx.spiderIndexNum(lastFound, curFi, ir);
            spy.setIndexLocation(lastFound);

            if (lastFound == -1) {
                logger.debug("update() " + curFi + " currently is not in the index");
            }
        }

        // now iterate over all the spider urls
        int curSpiderNum = getNextUrlNo();
        int totalSpidered = 0;
        while (curSpiderNum != -1) {
            curDocNum++;

            if (ds != null) {
                ds.setCurProgress(curDocNum);
                if (! ds.getIsWorking()) {
                    break;
                }
            }

            SpiderUrl curSpider = getSpiderUrl(curSpiderNum);
            int curNumLinksFound = getNumLinksFound();
            int curIdxNum = curSpider.getIndexLocation();
            // TODO is this getsize realy needed, than the url ist in index?
            long curUrlSize = netUtils.getURLSize(curSpider.getUrl());
            String shortUrl = Utils.concatEnd(curSpider.getUrl(), 33);
            String dnldTmpName = getDownloadFileName(curSpider.getContentType(), curSpider.getUrl().toLowerCase());
            String downloadFile = FileUtils.addFolder(downloadFileDir, dnldTmpName);

            // document is to big
            if (curUrlSize > maxFileSizeToGet) {
                logger.debug("update() '" + shortUrl + "' is to big");
                setStatus(I18n.getString("skipping_file_too_big") + " (" + curUrlSize + " > " + maxFileSizeToGet + ") " + shortUrl);
                curSpider.setSize(curUrlSize);
            }
            // document is in index
            else if (curIdxNum != -1) {
                logger.debug("update() '" + shortUrl + "' is in index");
                setStatus(DocSearch.dsCkgFoUpdtsToDoc + " " + shortUrl + " (" + totalSpidered + " / " + curNumLinksFound + ")");

                int curSpiderStatus = netUtils.getURLStatus(curSpider, downloadFile);
                switch (curSpiderStatus) {
                    case -1: // broken url
                        logger.debug("update() '" + shortUrl + "' is broken");
                        setStatus(DocSearch.dsBknLink + " " + shortUrl);
                        curSpider.setIsDeadLink(true);
                        // remove from index
                        ir.deleteDocument(curIdxNum);
                        numDeletes++;
                        break;
                    case 0: // same
                        logger.debug("update() '" + shortUrl + "' no changes");
                        setStatus(DocSearch.lnkNoChanges + " " + shortUrl);
                        numUnChanged++;
                        totalSpidered++;
                        break;
                    case 1: // changed
                        logger.debug("update() '" + shortUrl + "' is changed");
                        setStatus(DocSearch.dsReIdxgLnk + " " + shortUrl);
                        ir.deleteDocument(curIdxNum);
                        ir.close();
                        iw = new IndexWriter(dsi.getIndexPath(), new StandardAnalyzer(), false);
                        // iw.setUseCompoundFile(true);
                        int curAddedSuccess = ds.idx.addDocToIndex(downloadFile, iw, dsi, false, curSpider);
                        iw.close();
                        ir = IndexReader.open(dsi.getIndexPath());
                        if (curAddedSuccess == 0) {
                            numChanges++;
                            totalSpidered++;
                        }
                        else if (curAddedSuccess == 2) {
                            numMetaNoIdx++;
                        }
                        else if (curAddedSuccess == 1) {
                            logger.warn("update() indexing failed " + shortUrl);
                            numFails++;
                        }

                        // get links from downloaded file
                        if (isHtml(curSpider.getUrl())) {
                            checkFileForLinks(downloadFile, curSpider.getUrl());
                        }
                        break;
                }
            }
            // document is not in index
            else {
                logger.debug("update() '" + shortUrl + "' is not in index");
                setStatus(DocSearch.dsSpiderNewUrl + " " + shortUrl + " (" + totalSpidered + " / " + curNumLinksFound + ")");

                boolean downloadOk = netUtils.downloadURLToFile(curSpider, downloadFile);
                if (downloadOk) {
                    iw = new IndexWriter(dsi.getIndexPath(), new StandardAnalyzer(), false);
                    // iw.setUseCompoundFile(true);
                    int curAddedSuccess = ds.idx.addDocToIndex(downloadFile, iw, dsi, false, curSpider);
                    iw.close();
                    ir.close();
                    ir = IndexReader.open(dsi.getIndexPath());
                    if (curAddedSuccess == 0) {
                        numNew++;
                        totalSpidered++;
                    }
                    else if (curAddedSuccess == 2) {
                        numMetaNoIdx++;
                    }
                    else if (curAddedSuccess == 1) {
                        logger.warn("update() indexing failed " + shortUrl);
                        numFails++;
                    }
                    if (isHtml(curSpider.getUrl())) {
                        checkFileForLinks(downloadFile, curSpider.getUrl());
                    }
                }
                else {
                    setStatus(DocSearch.dsBknLink + " " + shortUrl);
                    curSpider.setIsDeadLink(true);
                }
            }

            // last things to do
            curSpider.setSpidered(true);
            curSpiderNum = getNextUrlNo();
            if (curSpiderNum == -1) {
                break;
            }
            if (totalSpidered > maxTotal) {
                break;
            }

            // delete temp file
            if (! FileUtils.deleteFile(downloadFile)) {
                logger.warn("update() can't delete file '" + downloadFile + "'");
            }
        }

        setStatus(DocSearch.dsSpdrUpdteComp + " " + dsi.getName());
        saveAllLinks();

        // update the date of the index
        dsi.setLastIndexed(DateTimeUtils.getToday());
        ir.close();
        ds.resetProgress();
    }


    /**
     * Gets a tempfilename
     *
     * @param contentType  content type of object
     * @param url          URL
     * @return             temp filename
     */
    protected String getDownloadFileName(final String contentType, final String url) {
        if (logger.isDebugEnabled()) {
            logger.debug("getDownloadFileName('" + contentType + "', '" + url + "') entered");
        }

        StringBuilder result = new StringBuilder();
        result.append("temp_spidered_document_").append(USER_NAME);

        // first: content type
        if (contentType.toLowerCase().endsWith("html")) {
            result.append(".htm");
        }
        // second: extension
        else if (url.endsWith("/")) {
            result.append(".htm");
        }
        else {
            String extension = FileUtils.getFileExtension(url);
            if (extension.equals("unknown")) {
                result.append(".htm");
            }
            else {
                result.append('.').append(extension);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("getDownloadFileName() result='" + result + "'");
        }


        return result.toString();
    }
}
