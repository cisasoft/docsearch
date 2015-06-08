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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Class ArchiveMetaData
 *
 * @version $Id: ArchiveMetaData.java 90 2007-10-13 21:13:02Z henschel $
 */
public class ArchiveMetaData {

    String zipFileString = "";
    String description = "";
    String metaFileName = "";
    String dateIndexed = "";
    String location = "";
    boolean searchedByDefault = true;
    boolean isWeb = false;
    int indexPolicy = 0;


    public ArchiveMetaData(String zipFileString) {
        this.zipFileString = zipFileString;
        metaFileName = Utils.getFolderOnly(zipFileString);
        metaFileName = FileUtils.addFolder(metaFileName, "archives.htm");
        if (zipFileString.toLowerCase().startsWith("http")) {
            loadMetaDataUrl(metaFileName, zipFileString);
        } else {
            File testMetaFile = new File(metaFileName);
            if (testMetaFile.exists()) {
                System.out.println("Loading meta data from file..." + metaFileName);
                loadMetaData(metaFileName, zipFileString);
            } // end for metaFile exists
            else { // metaFile not found - so fudge some defaults
                System.out.println("Loading meta data file missing..." + metaFileName);
                description = Utils.getNameOnly(zipFileString);
                int descSize = description.length();
                if (descSize > 5)
                    description = description.substring(0, descSize - 4);
                description = Utils.replaceAll("_", description, " ");
                dateIndexed = DateTimeUtils.getToday();
            }
        } // end for of zipfile is a file not a URL
    } // end for constructor


    public void loadMetaData(String metaFN, String zFn) {
        // load up our metaData
        String curDesc = "";
        String curDI = "";
        // String curSBD="";
        // String curIsWeb="";
        String curCONTENT = "";
        String BEGIN_CELL = "<td";
        String END_CELL = "</td";
        String curZFN = "";
        // String BEGIN_ROW="<tr";
        String END_ROW = "</tr";
        String zfNameOnly = Utils.getNameOnly(zFn);
        boolean inTag = false;
        StringBuffer tagBuf = new StringBuffer();
        StringBuffer textBuf = new StringBuffer();
        String tag = "";
        String lowerTag = "";
        File loadFile = new File(metaFN);
        int colNum = 0;
        int rowNum = 0;
        try {
            FileInputStream fi = new FileInputStream(loadFile);
            int curI = 0; // reset i
            char curChar = ' ';
            while (curI != -1) {
                curI = fi.read();
                if (curI != -1) {
                    curChar = (char) curI;
                    if (curChar == '<') {
                        // beginning of a tag
                        inTag = true;
                    } else if (curChar == '>') {
                        tagBuf.append(curChar);
                        tag = tagBuf.toString();
                        lowerTag = tag.toLowerCase();
                        String content = textBuf.toString();
                        // see what we got
                        if (lowerTag.startsWith(END_ROW)) {
                            rowNum++;
                            colNum = 0;
                        }
                        else if (lowerTag.startsWith(BEGIN_CELL)) {
                            tagBuf = new StringBuffer();
                            textBuf = new StringBuffer();
                        }
                        else if (lowerTag.startsWith(END_CELL)) {
                            if (rowNum > 0) {
                                switch (colNum) {
                                    case 0: // desc
                                        curDesc = content;
                                        break;
                                    case 1: // date idxd
                                        curDI = content;
                                        break;
                                    case 2: // zip file name
                                        curZFN = content;
                                        break;
                                    default: // url or folder
                                        curCONTENT = content;
                                        break;
                                }
                                if (colNum == 3) {
                                    if (curZFN.equals(zfNameOnly)) {
                                        description = curDesc;
                                        dateIndexed = curDI;
                                        location = curCONTENT;
                                        if (location.toLowerCase().startsWith("http"))
                                            isWeb = true;
                                        break;
                                    } else
                                        System.out.println(curZFN + "!=" + zfNameOnly);
                                }
                            }
                            colNum++;
                            textBuf = new StringBuffer();
                        }
                        inTag = false;
                        tagBuf = new StringBuffer();
                    } // end for end of a tag
                } // end for curI not at EOF
                else
                    break;
                if (inTag)
                    tagBuf.append(curChar);
                else if (curChar != '>')
                    textBuf.append(curChar);
            } // end while reading
            fi.close();
        } catch (IOException ioe) {
            System.out.println("ERR obtaining meta data (" + metaFN + ") " + ioe.toString());
        }
    } // end for loadMetaData


    public void loadMetaDataUrl(String metaFN, String zFn) {
        // byte curBint;
        int curI = 0;
        InputStream urlStream;
        // int lastPercent = 0;
        // int curPercent = 0;
        String curDesc = "";
        String curDI = "";
        // String curSBD="";
        // String curIsWeb="";
        String curCONTENT = "";
        String BEGIN_CELL = "<td";
        String END_CELL = "</td";
        String curZFN = "";
        // String BEGIN_ROW="<tr";
        String END_ROW = "</tr";
        String zfNameOnly = Utils.getNameOnly(zFn);
        boolean inTag = false;
        StringBuffer tagBuf = new StringBuffer();
        StringBuffer textBuf = new StringBuffer();
        char curChar = ' ';
        String tag = "";
        String lowerTag = "";
        // File loadFile=new File(metaFN);
        int colNum = 0;
        int rowNum = 0;
        try {
            URL url = new URL(metaFN);
            URLConnection conn = url.openConnection();
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.connect();
            urlStream = conn.getInputStream();
            // int totalSize = conn.getContentLength();
            while (curI != -1) {
                curI = urlStream.read();
                if (curI != -1) {
                    curChar = (char) curI;
                    if (curChar == '<') {
                        // beginning of a tag
                        inTag = true;
                    } else if (curChar == '>') {
                        tagBuf.append(curChar);
                        tag = tagBuf.toString();
                        lowerTag = tag.toLowerCase();
                        String content = textBuf.toString();
                        // see what we got
                        if (lowerTag.startsWith(END_ROW)) {
                            rowNum++;
                            colNum = 0;
                        } else if (lowerTag.startsWith(BEGIN_CELL)) {
                            tagBuf = new StringBuffer();
                            textBuf = new StringBuffer();
                        } else if (lowerTag.startsWith(END_CELL)) {
                            if (rowNum > 0) {
                                switch (colNum) {
                                    case 0: // desc
                                        curDesc = content;
                                        break;
                                    case 1: // date idxd
                                        curDI = content;
                                        break;
                                    case 2: // zip file name
                                        curZFN = content;
                                        break;
                                    default: // url or folder
                                        curCONTENT = content;
                                        break;
                                }
                                if (colNum == 3) {
                                    if (curZFN.equals(zfNameOnly)) {
                                        description = curDesc;
                                        dateIndexed = curDI;
                                        location = curCONTENT;
                                        if (location.toLowerCase().startsWith("http"))
                                            isWeb = true;
                                        break;
                                    }
                                }
                            }
                            colNum++;
                            textBuf = new StringBuffer();
                        }
                        inTag = false;
                        tagBuf = new StringBuffer();
                    } // end for end of a tag
                } // end for curI not at EOF
                else
                    break;
                if (inTag)
                    tagBuf.append(curChar);
                else if (curChar != '>')
                    textBuf.append(curChar);
            } // end while reading
            urlStream.close();
        } catch (Exception eR) {
            System.out.println("ERR obtaining meta data (" + metaFN + ") " + eR.toString());
        }
    } // end for loadMetaData


    public String getDesc() {
        return description;
    }


    public String getDI() {
        return dateIndexed;
    }


    public String getLocation() {
        return location;
    }


    public boolean getSDB() {
        return searchedByDefault;
    }


    public int getIndexPolicy() {
        return indexPolicy;
    }


    public boolean getIsWeb() {
        return isWeb;
    }

}
