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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.jab.docsearch.Environment;
import org.jab.docsearch.FileEnvironment;
import org.jab.docsearch.constants.OSType;

/**
 * Class DocTypeHandler Utils
 *
 * @version $Id: DocTypeHandlerUtils.java 130 2009-07-21 10:26:00Z henschel $
 */
public class DocTypeHandlerUtils {
    /**
     * Logging
     */
    private final Logger logger = Logger.getLogger(getClass());

    /**
     * Handler file
     */
    public final static String HANDLER_FILE = "handlers_list.htm";


    /**
     * Gets the initial handlers.
     *
     * @param env  Environment
     * @return     List with DocTypeHandler
     */
    public List<DocTypeHandler> getInitialHandler(final Environment env) {
    	logger.debug("getInitialHandler() entered");

    	List<DocTypeHandler> dthList = new ArrayList<DocTypeHandler>();
    	DocTypeHandler dh;

        switch (env.getOSType()) {
            case OSType.WIN_32: // windows
                break;
            case OSType.LINUX: // linux
                if (FileUtils.fileExists("/usr/bin/xpdf")) {
                    dh = new DocTypeHandler("pdf", "Portable Document Format", "/usr/bin/xpdf");
                    dthList.add(dh);
                }
                else if (FileUtils.fileExists("/usr/bin/kghostview")) {
                    dh = new DocTypeHandler("pdf", "Portable Document Format", "/usr/bin/kghostview");
                    dthList.add(dh);
                }
                if (FileUtils.fileExists("/usr/bin/oowriter")) {
                    dh = new DocTypeHandler("sxw", "Star Office  or Open Office Writer", "/usr/bin/oowriter");
                    dthList.add(dh);
                    dh = new DocTypeHandler("doc", "Microsoft Word Document", "/usr/bin/oowriter");
                    dthList.add(dh);
                    dh = new DocTypeHandler("rtf", "Rich Text Format Document", "/usr/bin/oowriter");
                    dthList.add(dh);
                }
                if (FileUtils.fileExists("/usr/bin/oocalc")) {
                    dh = new DocTypeHandler("sxc", "Star Office  or Open Office Calc", "/usr/bin/oocalc");
                    dthList.add(dh);
                    dh = new DocTypeHandler("xls", "Microsoft Excel Spreadsheet", "/usr/bin/oocalc");
                    dthList.add(dh);
                }
                if (FileUtils.fileExists("/usr/bin/ooimpress")) {
                    dh = new DocTypeHandler("sxi", "Star Office  or Open Office Impress", "/usr/bin/ooimpress");
                    dthList.add(dh);
                    dh = new DocTypeHandler("ppt", "Microsoft Powerpoint Presentation", "/usr/bin/ooimpress");
                    dthList.add(dh);
                }
                if (FileUtils.fileExists("/usr/bin/kate")) {
                    dh = new DocTypeHandler("txt", "Text File", "/usr/bin/kate");
                    dthList.add(dh);
                    dh = new DocTypeHandler("java", "Java Source Code", "/usr/bin/kate");
                    dthList.add(dh);
                }
                else if (FileUtils.fileExists("/usr/bin/gedit")) {
                    dh = new DocTypeHandler("txt", "Text File", "/usr/bin/gedit");
                    dthList.add(dh);
                    dh = new DocTypeHandler("java", "Java Source Code", "/usr/bin/gedit");
                    dthList.add(dh);
                }
                if (FileUtils.fileExists("/usr/bin/konqueror")) {
                    dh = new DocTypeHandler("htm", "Web Page", "/usr/bin/konqueror");
                    dthList.add(dh);
                    dh = new DocTypeHandler("html", "Web Page", "/usr/bin/konqueror");
                    dthList.add(dh);
                    dh = new DocTypeHandler("jsp", "Web Page", "/usr/bin/konqueror");
                    dthList.add(dh);
                    dh = new DocTypeHandler("asp", "Web Page", "/usr/bin/konqueror");
                    dthList.add(dh);
                    dh = new DocTypeHandler("cfm", "Web Page", "/usr/bin/konqueror");
                    dthList.add(dh);
                    dh = new DocTypeHandler("shtml", "Web Page", "/usr/bin/konqueror");
                    dthList.add(dh);
                }
                else if (FileUtils.fileExists("/usr/bin/quanta")) {
                    dh = new DocTypeHandler("htm", "Web Page", "/usr/bin/quanta");
                    dthList.add(dh);
                    dh = new DocTypeHandler("html", "Web Page", "/usr/bin/quanta");
                    dthList.add(dh);
                    dh = new DocTypeHandler("jsp", "Web Page", "/usr/bin/quanta");
                    dthList.add(dh);
                    dh = new DocTypeHandler("asp", "Web Page", "/usr/bin/quanta");
                    dthList.add(dh);
                    dh = new DocTypeHandler("cfm", "Web Page", "/usr/bin/quanta");
                    dthList.add(dh);
                    dh = new DocTypeHandler("shtml", "Web Page", "/usr/bin/quanta");
                    dthList.add(dh);
                }
                else if (FileUtils.fileExists("/usr/bin/mozilla")) {
                    dh = new DocTypeHandler("htm", "Web Page", "/usr/bin/mozilla");
                    dthList.add(dh);
                    dh = new DocTypeHandler("html", "Web Page", "/usr/bin/mozilla");
                    dthList.add(dh);
                    dh = new DocTypeHandler("jsp", "Web Page", "/usr/bin/mozilla");
                    dthList.add(dh);
                    dh = new DocTypeHandler("asp", "Web Page", "/usr/bin/mozilla");
                    dthList.add(dh);
                    dh = new DocTypeHandler("cfm", "Web Page", "/usr/bin/mozilla");
                    dthList.add(dh);
                    dh = new DocTypeHandler("shtml", "Web Page", "/usr/bin/mozilla");
                    dthList.add(dh);
                }
                else if (FileUtils.fileExists("/usr/bin/galeon")) {
                    dh = new DocTypeHandler("htm", "Web Page", "/usr/bin/galeon");
                    dthList.add(dh);
                    dh = new DocTypeHandler("html", "Web Page", "/usr/bin/galeon");
                    dthList.add(dh);
                    dh = new DocTypeHandler("jsp", "Web Page", "/usr/bin/galeon");
                    dthList.add(dh);
                    dh = new DocTypeHandler("asp", "Web Page", "/usr/bin/galeon");
                    dthList.add(dh);
                    dh = new DocTypeHandler("cfm", "Web Page", "/usr/bin/galeon");
                    dthList.add(dh);
                    dh = new DocTypeHandler("shtml", "Web Page", "/usr/bin/galeon");
                    dthList.add(dh);
                }
                if (FileUtils.fileExists("/usr/bin/ark")) {
                    dh = new DocTypeHandler("zip", "Zip Archive", "/usr/bin/ark");
                    dthList.add(dh);
                }
                if (FileUtils.fileExists("/usr/bin/kpaint")) {
                    dh = new DocTypeHandler("png", "Portable Network Graphics", "/usr/bin/kpaint");
                    dthList.add(dh);
                    dh = new DocTypeHandler("gif", "GIF Graphics", "/usr/bin/kpaint");
                    dthList.add(dh);
                    dh = new DocTypeHandler("jpg", "JPG Graphics", "/usr/bin/kpaint");
                    dthList.add(dh);
                    dh = new DocTypeHandler("jpeg", "JPEG Graphics", "/usr/bin/kpaint");
                    dthList.add(dh);
                }
                else if (FileUtils.fileExists("/usr/bin/gimp")) {
                    dh = new DocTypeHandler("png", "Portable Network Graphics", "/usr/bin/gimp");
                    dthList.add(dh);
                    dh = new DocTypeHandler("gif", "GIF Graphics", "/usr/bin/gimp");
                    dthList.add(dh);
                    dh = new DocTypeHandler("jpg", "JPG Graphics", "/usr/bin/gimp");
                    dthList.add(dh);
                    dh = new DocTypeHandler("jpeg", "JPEG Graphics", "/usr/bin/gimp");
                    dthList.add(dh);
                }
                break;
            case OSType.UNIX: // unix
                break;
            case OSType.MAC: // mac
                break;
        }

        // back
        return dthList;
    }


    /**
     * Loads the handler from file.
     *
     * @param fileName  Filename
     * @return          List wirh DocTypeHandler
     */
    public List<DocTypeHandler> loadHandler(final String fileName) {
    	logger.debug("loadHandler() entered");

      	List<DocTypeHandler> dthList = new ArrayList<DocTypeHandler>();

        String endRow = "</tr";
        String endCell = "</td";
        // String startRow="<tr";
        String startCell = "<td";
        // boolean inCell=false;
        boolean inTag = false;
        StringBuffer tagBuf = new StringBuffer();
        StringBuffer textBuf = new StringBuffer();
        String lowerTag = "";
        String tag = "";
        int curCell = 0;

        // get count of object rows
        int numRowsForObject = Utils.getNumObjectRows(fileName);
        if (numRowsForObject > 0) {
            int numCells = 3;
            String[] cells = new String[numCells];
            for (int i = 0; i < numCells; i++) {
                cells[i] = "";
            }
            // load and parse the file
            FileInputStream fi = null;
            try {
                fi = new FileInputStream(fileName);
                int curI;
                while((curI = fi.read()) != -1) {
                    char curChar = (char) curI;

                    // begin of a tag
                    if (curChar == '<') {
                        inTag = true;
                    }
                    // end of a tag
                    else if (curChar == '>') {
                        tagBuf.append(curChar);
                        tag = tagBuf.toString();
                        lowerTag = tag.toLowerCase();
                        if (lowerTag.startsWith(endCell)) {
                            cells[curCell] = textBuf.toString();
                            textBuf = new StringBuffer();
                            curCell++;
                        } // end of a cell
                        else if (lowerTag.startsWith(endRow)) {
                            curCell = 0;
                            DocTypeHandler dth = new DocTypeHandler(cells[0], cells[1], cells[2]);
                            dthList.add(dth);
                        } // end of a row
                        else if (lowerTag.startsWith(startCell)) {
                            textBuf = new StringBuffer();
                        } // end of startRow
                        // always reset buffer
                        inTag = false;
                        tagBuf = new StringBuffer();
                    } // end for end of a tag

                    // tag or text ?
                    if (inTag) {
                        tagBuf.append(curChar);
                    }
                    else if (curChar != '>') {
                        textBuf.append(curChar);
                    }
                }
            }
            catch (IOException ioe) {
                logger.fatal("loadHandler() error loading from:" + fileName, ioe);
            }
            finally {
            	IOUtils.closeQuietly(fi);
            }

        }
        else {
            logger.info("loadHandler() Nothing found: " + fileName);
        }

        // back
        return dthList;
    }


    public void saveHandler(final FileEnvironment fEnv, final List<DocTypeHandler> dthList) {
    	logger.debug("saveHandler() entered");

    	// saves handler info into html table
    	// exist handler?
    	if (! dthList.isEmpty()) {
            StringBuffer s = new StringBuffer();
            s.append("<html><head><title>DocSearcher File Handler Type Listing</title>");
            s.append("<body><h1>DocSearcher File Handler Type Listing</h1><p align=\"left\">");
            s.append("Listed below are the file type handlers used to open various links by DocSearcher.</p>");
            s.append("<table border=\"1\">");

            // add all handler
            for (DocTypeHandler dth : dthList) {
                s.append("<tr>");
                s.append("<td>");
                s.append(dth.getExtension());
                s.append("</td>");
                s.append("<td>");
                s.append(dth.getDesc());
                s.append("</td>");
                s.append("<td>");
                s.append(dth.getApp());
                s.append("</td>");
                s.append("</tr>");
            }

            s.append("</table></body></html>");

            // save the file
            FileUtils.saveFile(HANDLER_FILE, fEnv.getWorkingDirectory(), s);
        }
    }


    /**
     * Gets the handler of external link.
     *
     * @param externalLink  External link
     * @return              Handler
     */
    public String hasHandler(final List<DocTypeHandler> dthList, final String externalLink) {
    	logger.debug("hasHandler() entered");

    	String ext = null;
        String extHandler = "";

        int lastDot = externalLink.lastIndexOf(".");
        if (lastDot != -1) {
            // get extension
            ext = externalLink.substring(lastDot + 1, externalLink.length());
            logger.debug("hasHandler() extension is: " + ext);

            // get handler of extension
            if (! dthList.isEmpty()) {
                for (DocTypeHandler dh : dthList) {
                    if (dh.isCompat(ext)) {
                        extHandler = dh.getApp();
                        break;
                    }
                }
            }
            else {
                logger.info("hasHandler() no external handlers defined!");
            }
        }
        else {
            logger.info("hasHandler() no extension found '" + externalLink + "'");
        }

        if (logger.isDebugEnabled()) {
            logger.debug("hasHandler() " + ext + " -> " + extHandler);
        }

        return extHandler;
    }
}
