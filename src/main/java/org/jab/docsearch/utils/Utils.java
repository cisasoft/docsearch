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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jab.docsearch.constants.OSType;
import org.jab.docsearch.spider.SpiderUrl;

/**
 * This utility class primarily performs string manipulation functions.
 *
 * @version $Id: Utils.java 165 2011-05-04 08:06:22Z henschel $
 */
public final class Utils {
    /**
     * Log4J
     */
    private final static Logger logger = Logger.getLogger(Utils.class.getName());
    private final static String PATH_SEPARATOR = FileUtils.PATH_SEPARATOR;
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");


    /**
     * Replace string
     *
     * @param search   search string
     * @param s        string
     * @param replace  replace string
     * @return         A string where "search" has been replaced with "replace" given a string "s"
     */
    public static String replaceAll(final String search, String s, final String replace) {
        if (search == null || s == null || replace == null) {
            return s;
        }

        // first pos of "search"
        int p = s.indexOf(search);

        // replace till all chars replaced
        while (p != -1) {

            if (p > 0) {
                s = s.substring(0, p) + replace  + s.substring(p + search.length());
            }
            else {
                s = replace  + s.substring(p + search.length());
            }

            // search after replace
            p = s.indexOf(search, p + replace.length());
        }

        return s;
    }


    public static double getAverageSearchScore(ArrayList<LogSearch> loggedSearches) {
        int numIs = loggedSearches.size();
        double returnD = 0.0;
        double totalD = 0.0;
        double temD = 0.0;
        LogSearch ls;
        if (numIs > 0) {
            Iterator<LogSearch> it = loggedSearches.iterator();
            while (it.hasNext()) {
                ls = it.next();
                temD = ls.score;
                totalD += temD;
            } // end while
            if (totalD > 0.0)
                returnD = totalD / numIs;
        }
        return returnD;
    } // end for getAverageSearchScore


    public static boolean hasUser(String toMatch, ArrayList<String> listing) {
        int numUs = listing.size();
        boolean returnB = false;
        if (numUs > 0) {
            Iterator<String> it = listing.iterator();
            while (it.hasNext()) {
               String temC = it.next();
                if (temC.equals(toMatch)) {
                    returnB = true;
                    break;
                }
            } // end while
        } // end has users
        return returnB;
    } // end foir hasUserBool


    /**
     * @return true if a URL ends with a slash
     */
    public static boolean endsWithSlash(String toCkeck) {
        boolean retB = false;
        if ((toCkeck.endsWith("/")) || (toCkeck.endsWith("\\")))
            retB = true;
        return retB;
    }


    /**
     * @return url given a filename and the text to find (match) and replace it
     *         with
     */
    public static String getURL(String fileName, String match, String replace) {
        String returnString = "";
        returnString = replace + fileName.substring(match.length(), fileName.length());
        returnString = Utils.replaceAll("\\", returnString, "/");

        // System.out.println("Converted file("+fileName+") to
        // "+returnString+"\nmatch:"+match+"\nreplace:"+replace);
        return returnString;
    }


    /**
     * @return number of slashes (/) in a URL
     */
    public static int countSlash(String urlToCount) {
        int returnInt = 0;
        int totalLen = urlToCount.length();
        int startSpot = urlToCount.indexOf(PATH_SEPARATOR);
        if (startSpot != -1) {
            returnInt++;
            do {
                startSpot++;
                if (startSpot > totalLen) {
                    break;
                }

                startSpot = urlToCount.indexOf(PATH_SEPARATOR, startSpot);
                if (startSpot == -1) {
                    break;
                }

                returnInt++;

                // System.out.println("Found slash");
            } while (startSpot != -1);
        }

        return returnInt;
    }


    /**
     * @return the number of rows in a html file containing an html table
     */
    public static int getNumObjectRows(String fileName) {
        int returnInt = 0;
        // load file and count <tr tags
        FileInputStream fi = null;
        try {
            File origFile = new File(fileName);
            if (origFile.exists()) {
                fi = new FileInputStream(origFile);
                int curI = 0; // reset i
                char curChar = ' ';
                char lastChar = ' ';
                char nextToLastChar = ' ';
                StringBuffer rowString;
                while (curI != -1) {
                    curI = fi.read();
                    if (curI != -1) {
                        //
                        lastChar = nextToLastChar;
                        nextToLastChar = curChar;
                        curChar = (char) curI;
                        rowString = new StringBuffer();
                        rowString.append(lastChar);
                        rowString.append(nextToLastChar);
                        rowString.append(curChar);
                        if (rowString.toString().toLowerCase().equals("<tr"))
                            returnInt++;
                    } // curI!=-1
                    else {
                        break;
                    }
                }
            } // end for file exists
            else {
                returnInt = -1;
            }
        } // end of trying
        catch (IOException ioe) {
            returnInt = 0;
        }
        finally {
        	IOUtils.closeQuietly(fi);
        }
        return returnInt;
    }


    /**
     * Method returns the filename with out parent directories
     *
     * @param fileString  fileString
     * @return            a filename without its parent directory included in the name
     */
    public static String getNameOnly(final String fileString) {
        if (fileString == null) {
            return null;
        }

        // remove parent objects
        if (fileString.indexOf(PATH_SEPARATOR) != -1) {
            return fileString.substring(fileString.lastIndexOf(PATH_SEPARATOR) + 1, fileString.length());
        }
        else if (fileString.indexOf("\\") != -1) {
            return fileString.substring(fileString.lastIndexOf("\\") + 1, fileString.length());
        }
        else if (fileString.indexOf("/") != -1) {
            return fileString.substring(fileString.lastIndexOf("/") + 1, fileString.length());
        }
        else {
            return fileString;
        }
    }


    /**
     * @return parent folder for a file named in fileString
     */
    public static String getFolderOnly(String fileString) {
        if (fileString.indexOf(PATH_SEPARATOR) != -1)
            return fileString.substring(0, fileString.lastIndexOf(PATH_SEPARATOR));
        else if (fileString.indexOf("\\") != -1)
            return fileString.substring(0, fileString.lastIndexOf("\\"));
        else if (fileString.indexOf("/") != -1)
            return fileString.substring(0, fileString.lastIndexOf("/"));
        else
            return "";
    }


    /**
     * Gets OS type
     *
     * @see OSType
     * @return  OSType number
     */
    public static int getOSType() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.indexOf("linux") != -1) {
            return OSType.LINUX;
        }
        else if (os.indexOf("windows") != -1) {
            return OSType.WIN_32;
        }
        else if (os.indexOf("nix") != -1) {
            return OSType.UNIX;
        }
        else if (os.indexOf("mac") != -1) {
            return OSType.MAC;
        }
        else {
            return OSType.UNKNOWN;
        }
    }


    /**
     * Gets user home
     *
     * @param osNum
     * @param defaultOsUserHome
     * @return                   users home directory - varies by platform
     */
    public static String getUserHome(final int osNum, final String defaultOsUserHome) {
        String returnString = defaultOsUserHome;

        // FIXME Warum soviel Ausnahmen? Vielleicht kann man das so zusammenfassen, das es keine Unterscheidung
        // mehr nach OS gibt.
        switch (osNum) {
            case OSType.WIN_32: {
                String tempHome = Messages.getString("win32.home");
                if (tempHome.equals("$HOME")) {
                    returnString = System.getProperty("user.home");
                }
                else if (tempHome.equals(".")) {
                    returnString = System.getProperty("user.dir");
                }
                break;
            }
            case OSType.LINUX: {
                String tempHome = Messages.getString("lin.home");
                if (tempHome.equals("$HOME")) {
                    returnString = System.getProperty("user.home");
                }
                else if (tempHome.equals(".")) {
                    returnString = System.getProperty("user.dir");
                }
                break;
            }
            case OSType.UNIX: {
                returnString = System.getProperty("user.dir");
                break;
            }
            case OSType.MAC: {
                returnString = System.getProperty("user.dir");
                break;
            }
            default: { // don't know what it is
                returnString = System.getProperty("user.dir");
                break;
            }
        }

        logger.log(NoticeLevel.NOTICE, "getUserHome() using HOME directory=" + returnString);

        return returnString;
    }


    /**
     * Gets CDROM directory
     *
     * @return path to a CD ROM - varies depending on the platform
     */
    public static String getCDROMDir(int osNum) {
        if (logger.isDebugEnabled()) {
            logger.debug("getCDROMDir('" + osNum + "')");
        }

        String tempCdDir = System.getProperty("user.dir");
        if (! tempCdDir.equals("")) {
            File testFile = new File(tempCdDir);
            if (testFile.exists()) {
                logger.info("getCDROMDir() CDROM Dir: " + tempCdDir);
                return tempCdDir;
            }
            else {
                logger.error("getCDROMDir() NO CDROM DIR FOUND... ");
                return "";
            }
        }
        else {
            logger.info("getCDROMDir() NO CDROM DIR FOUND... ");
            return "";
        }
    }


    /**
     * @return a concatenated string of to 37 chars in length- stips of the
     *         beginning
     */
    public static String concatStr(String toConcat) {
        if (toConcat.length() > 37)
            return "~" + toConcat.substring(toConcat.length() - 37, toConcat.length());
        else
            return toConcat;
    } // end for concat


    /**
     * @return a concatenated string of the specified length in chars - stips of
     *         the ending
     */
    public static String concatStrTo(String toConcat, int len) {
        if (toConcat.length() > len)
            return "~" + toConcat.substring(toConcat.length() - len, toConcat.length());
        else
            return toConcat;
    } // end for concat


    /**
     * @return a concatenated string of the specified length in chars - stips of
     *         the beginning
     */
    public static String concatStrToEnd(String toConcat, int len) {
        if (toConcat.length() > len)
            return toConcat.substring(0, len) + "...";
        else
            return toConcat;
    } // end for concat


    /**
     * Looks for tag attribute in tag
     *
     * @param toLookFor
     * @param toLookIn
     * @return attribute from the text of an html tag...
     */
    public static String getTagString(final String toLookFor, final String toLookIn) {
        String toLookInLower = toLookIn.toLowerCase();

        if (toLookInLower.indexOf(toLookFor) == -1) {
            return "";
        }
        else {
            boolean firstQFnd = true;
            StringBuilder tempS = new StringBuilder();
            int endPos = toLookIn.length();
            int startPos = toLookInLower.indexOf(toLookFor) + toLookFor.length();

            for (int i = startPos; i < endPos; i++) {
                char tC = toLookIn.charAt(i);
                // FIXME it is posible 'value' or "value"
                if (toLookIn.charAt(i) == '"') {
                    if (! firstQFnd) {
                        break;
                    }
                    else {
                        firstQFnd = false;
                    }
                }
                else if (toLookIn.charAt(i) == '>') {
                    break;
                }
                else {
                    tempS.append(tC);
                }
            }

            if (tempS.toString().trim().equals("")) {
                return "";
            }
            else {
                return tempS.toString();
            }
        }
    }


    /**
     * @return text given some HTML --- strips out markup
     */
    public static String nonTagText(String removeStr) {
        StringBuffer retBuf = new StringBuffer();
        int strLen = removeStr.length();
        boolean inTag = false;
        char curChar = ' ';
        for (int i = 0; i < strLen; i++) {
            curChar = removeStr.charAt(i);
            if (curChar == '<')
                inTag = true;
            else if (curChar == '>') {
                retBuf.append(" ");
                inTag = false;
            }
            if ((!inTag) && (curChar != '>'))
                retBuf.append(curChar);
            else if ((!inTag) && (curChar == '>'))
                retBuf.append(" ");
        } // end for string
        return retBuf.toString();
    } // end for nonTagText


    /**
     * Gets domain from URL
     *
     * @param  url  URL
     * @return      website name of a web page given a url
     */
    public static String getDomainURL(final String url) {
        if (url != null) {

            int slashCount = 0;
            for (int i = 0; i < url.length(); i++) {
                if (url.charAt(i) == '/') {
                    slashCount++;
                    if (slashCount == 3) {
                        return url.substring(0, i +1);
                    }
                }
            }

            // here no third slash found
            if (slashCount == 2) {
                return url + '/';
            }
        }

        logger.warn("getDomainUrl() couldn't retrieve domain from: " + url);
        return "";
    }


    /**
     * @return fully qualified URL from a relative hyperlink in a web page
     *         specified by baseUrl
     */
    public static String getRealUrl(String link, String baseUrl) {
        String returnS = "";
        String domainUrl = Utils.getDomainURL(baseUrl);
        int anchorPos = link.indexOf("#");
        if (anchorPos != -1)
            link = link.substring(0, anchorPos);
        // DETERMINE THE BASE URL
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        if (link.startsWith("http")) {
            returnS = link;
        }
        else if (link.indexOf("/") == -1) {
            returnS = baseUrl + "/" + link;
        }
        else if (link.startsWith("/")) {
            returnS = domainUrl + link;
        }
        else if (link.indexOf("../") == -1) {
            returnS = baseUrl + "/" + link;
        }

        if (returnS.equals("")) {
            logger.error("getRealUrl() Failed to construct full URL from link:" + link + "\nFound on page:" + baseUrl);
        }
        return returnS;
    }


    /**
     * Convert HTML entities to their text equivalents
     *
     * @param link  link
     * @return      url with HTML entities converted to their text equivalents
     */
    public static String getNormalUrl(String link) {
        link = Utils.replaceAll("&amp;", link, "&");
        link = Utils.replaceAll("%2B", link, "+");
        link = Utils.replaceAll("%2F", link, "/");
        link = Utils.replaceAll("%2f", link, "/");
        link = Utils.replaceAll("%2C", link, ",");
        link = Utils.replaceAll("%22", link, "\"");
        link = Utils.replaceAll("%23", link, "#");
        link = Utils.replaceAll("%24", link, "$");
        link = Utils.replaceAll("%3A", link, ":");
        link = Utils.replaceAll("%25", link, "%");
        link = Utils.replaceAll("+", link, " ");
        link = Utils.replaceAll("%26", link, "&");
        link = Utils.replaceAll("%27", link, "'");
        link = Utils.replaceAll("%20", link, " ");
        link = Utils.replaceAll("%28", link, "(");
        link = Utils.replaceAll("%29", link, ")");
        link = Utils.replaceAll("%5E", link, "^");
        link = Utils.replaceAll("%3f", link, "?");
        link = Utils.replaceAll("|", link, "%7C");
        return link;
    }


    /**
     * @return text concatenated to a specified size
     */
    public static String concatEnd(String toShorten, int size) {
        String returnString = "";
        int comS = toShorten.length();
        if (comS > size) {
            returnString = "..." + toShorten.substring(comS - size, comS);
        } else
            returnString = toShorten;
        return returnString;
    } // end of concat


    /**
     * Gets the parent folder of thr URL.
     *
     * @param url URL
     * @return    parent folder for a URL
     */
    public static String getBaseURLFolder(final String url) {
        String baseURL = null;

        if (url != null && url.trim().length() != 0) {

            // Is URL without slash after toplevel domain?
            int doubleSlash = url.indexOf("//");
            if (doubleSlash != -1) {

                if (url.indexOf("/", doubleSlash + 2) != -1) {
                    // Removes all after last slash
                    int lastSlash = url.lastIndexOf("/");
                    if (lastSlash != -1) {
                        baseURL = url.substring(0, lastSlash + 1);
                    }
                }
                else {
                    baseURL = url + "/";
                }
            }
        }

        if (baseURL == null) {
            baseURL = url;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("getBaseUrlFolder() Requiring BASE URL : " + baseURL);
        }

        return baseURL;
    }


    /**
     * Gets all SpiderLinks from file
     *
     * @param  filename
     * @return           an ArrayList of SpiderUrls for links in a downloaded file
     */
    public static ArrayList<SpiderUrl> getSpiderLinks(final String filename) {
        ArrayList<SpiderUrl> spiderList = new ArrayList<SpiderUrl>();

        if (filename == null) {
            logger.warn("getSpiderLinks() filename is null");
            return spiderList;
        }

        BufferedReader reader = null;
        try {
            File loadFile = new File(filename);
            if (! loadFile.exists()) {
                logger.warn("getSpiderLinks() Spider URL file does not exist " + filename);
            }
            else {
                reader = new BufferedReader(new FileReader(loadFile));

                String line;
                while ((line = reader.readLine()) != null) {
                    SpiderUrl tempUrl = new SpiderUrl(line);
                    spiderList.add(tempUrl);
                }
            }
        }
        catch (IOException ioe) {
            logger.error("getSpiderLinks() Error loading spider url links from file " + filename, ioe);
        }
        finally {
            IOUtils.closeQuietly(reader);
        }

        // return the arraylist of spider url objects
        return spiderList;
    }


    /**
     * @return text with html entities in place of the and sign and greater than
     *         and less than signs
     */
    public static String convertTextToHTML(String text) {
        String returnString = text;

        returnString = Utils.replaceAll("&", returnString, "&amp;"); // and symbol
        returnString = Utils.replaceAll("\n", returnString, "&nbsp;"); // spacer
        returnString = Utils.replaceAll("<", returnString, "&lt;"); // less than
        returnString = Utils.replaceAll(">", returnString, "&gt;"); // greater than
        returnString = Utils.replaceAll("\"", returnString, "&quot;"); // quot

        return returnString;
    }


    /**
     * Gets percent string from float with format xx.x %
     * TODO rewrite method, also with locale for use corrent . or ,
     *
     * @param var  float for converting
     * @return     PErcent string in format xx.x %
     */
    public static String getPercentStringFromScore(float var) {
        var *= 100.0f;

        return getStringFromFloat(var) + " %";
    }


    /**
     * Gets string from float with format xx.x
     * TODO rewrite method, also with locale for use corrent . or ,
     *
     * @param var  float for converting
     * @return     String in format xx.x
     */
    public static String getStringFromFloat(double var) {
        String temp = Double.toString(var);
        int length = temp.length();
        StringBuilder buf = new StringBuilder();

        int pastDot = 0;
        boolean foundDot = false;
        for (int i = 0; i < length; i++) {
            char curChar = temp.charAt(i);

            if (curChar == '.') {
                foundDot = true;
            }
            else if (foundDot) {
                pastDot++;
            }

            buf.append(curChar);

            if (pastDot >= 1) {
                break;
            }
        }

        return buf.toString();
    }


    /**
     * Get values in kilo format
     * TODO rewrite method, also with kilo byte and locale for Ko for France!! update also all references
     * TODO an also with locale for use corrent . or ,
     *
     * @param toGet
     * @return
     */
    public static String getKStyle(String toGet) {
        String returnString = null;

        try {
            double temp = Double.parseDouble(toGet) / 1024.0;
            returnString = getStringFromFloat(temp);
        }
        catch (Exception e) {
            returnString = toGet;
        }

        return returnString + " k";
    }

}
