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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import org.jab.docsearch.filters.FolderFilter;

/**
 * This is a wrapper class for a lucene index.
 *
 * @version $Id: ZippHandler.java 146 2009-11-15 18:49:21Z henschel $
 */
public class ZippHandler {
    /**
     * Log4J
     */
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final static FolderFilter ff = new FolderFilter();
    private final static int BUFFER = 2048;
    private final String filename;
    private final String archiveFromDir;
    private final String pathSep = System.getProperty("file.separator");


    /**
     * Constructor
     *
     * @param filename
     * @param archiveFromDir
     */
    public ZippHandler(String filename, String archiveFromDir) {
        this.filename = filename;
        this.archiveFromDir = archiveFromDir;
    }


    /**
     * Method zip
     *
     * @throws IOException
     */
    public void zip() throws IOException {
        logger.debug("zip() Archiving Directory (Recursively) " + archiveFromDir + " to archive " + filename);

        ZipOutputStream out = null;
        try {
            out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(filename)));
            byte[] data = new byte[BUFFER];
            int count = 0;

            // get a list of files from current directory
            ArrayList<String> folderList = new ArrayList<String>();
            folderList.add(archiveFromDir);
            int curItemNo = 0;
            int lastFoldNum = 0;
            String newFoundFolder = "";

            do {
                String curFolderString = folderList.get(curItemNo);
                File curFolderFile = new File(curFolderString);

                // handle any subfolders --> add them to our folderlist
                String [] foldersString = curFolderFile.list(ff);
                int numFolders = foldersString.length;

                // add our folders into the array
                for (int i = 0; i < numFolders; i++) {
                    newFoundFolder = FileUtils.addFolder(curFolderString, foldersString[i]);
                    folderList.add(newFoundFolder);
                    lastFoldNum++;
                }
                // end for adding folders

                String [] filesString = curFolderFile.list();
                int numFiles = filesString.length;
                for (int i = 0; i < numFiles; i++) {
                    // add them to our folderlist
                    String curFi = curFolderString + pathSep + filesString[i];
                    curFi = Utils.replaceAll(pathSep + pathSep, curFi, pathSep);

                    // add the file to our archive
                    File testDir = new File(curFi);
                    if (! testDir.isDirectory()) {
                        BufferedInputStream origin = null;
                        try {
                            String curZipName = getZipName(curFi, curFolderString);
                            logger.debug("zip() adding " + curZipName);

                            // zip entry
                            ZipEntry entry = new ZipEntry(curZipName);
                            out.putNextEntry(entry);

                            // copy data to zip
                            origin = new BufferedInputStream(new FileInputStream(curFi), BUFFER);
                            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                                out.write(data, 0, count);
                            }
                        }
                        finally {
                            if (origin != null) {
                                try {
                                    origin.close();
                                }
                                catch (IOException ioe) {
                                    logger.fatal("zip() can't close BufferedInputStream", ioe);
                                }
                            }
                        }
                    }
                }

                curItemNo++;

            } while (curItemNo <= lastFoldNum);
        }
        catch (IOException ioe) {
            logger.fatal("zip() failed", ioe);
            throw ioe;
        }
        finally {
            try {
                if (out != null) {
                    out.close();
                }
            }
            catch (IOException ioe) {
                logger.fatal("zip() can't close ZipOutputStream", ioe);
            }
        }
    }


    /**
     * Gets zip name
     *
     * @param file
     * @param folder
     * @return
     */
    public String getZipName(String file, String folder) {
        int charsInFolder = folder.length();
        int charsInStartFolder = archiveFromDir.length();
        int charsInFile = file.length();
        if (charsInFolder >= charsInStartFolder) {
            // return the differend
            return file.substring(charsInStartFolder + 1, charsInFile);
        } else {
            return file;
        }
    }


    /**
     * Main method
     *
     * @param args
     */
    public static void main(String[] args) {
        BasicConfigurator.configure();
        Logger mainLogger = Logger.getLogger(ZippHandler.class);

        String zipArchive = args[0];
        String target = args[1];

        if (zipArchive != null && target != null) {
            ZippHandler zh = new ZippHandler(args[0], args[1]);
            try {
                zh.zip();
            }
            catch (IOException ioe) {
                mainLogger.error("failed");
            }
        }
        else {
            mainLogger.info("main() usage :\njava ZippHanlder zip_File target_Dir");
        }
    }
}
