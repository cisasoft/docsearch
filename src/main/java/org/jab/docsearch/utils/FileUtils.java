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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.twmacinta.util.MD5;
import com.twmacinta.util.MD5InputStream;

/**
 * This class contains useful method for handling files.
 *
 * @version $Id: FileUtils.java 157 2010-12-27 22:26:55Z henschel $
 */
public final class FileUtils {
    /**
     * Logging
     */
    private final static Logger logger = Logger.getLogger(FileUtils.class);
    /**
     * Path separator
     */
    public final static String PATH_SEPARATOR = System.getProperty("file.separator");


    /**
     * Checks file exist and is not a directory
     *
     * @param filename  file to check
     * @return          true if the file exists
     */
    public static boolean fileExists(final String filename) {
        if (filename == null) {
            logger.warn("fileExist() failed because filename is null");
            return false;
        }

        File file = new File(filename);

        // check file exist
        if (file.exists()) {
            // check file is directory
            return ! file.isDirectory();
        }
        else {
            return false;
        }
    }


    /**
     * Checks file exist and is not a directory
     *
     * @param filename  file to check
     * @return          true if the file exists
     */
    public static boolean deleteFile(final String filename) {
        if (filename == null) {
            logger.warn("deleteFile() failed because filename is null");
            return false;
        }

        File file = new File(filename);

        // check file exist
        if (file.exists() && ! file.isDirectory()) {
            return file.delete();
        }
        else {
            return false;
        }
    }


    /**
     * Gets file size
     *
     * @param filename  file name
     * @return          number of bytes in a file
     */
    public static long getFileSize(String filename) {
        if (! fileExists(filename)) {
            logger.warn("getFileSize() " + filename + " is not a file");
            return 0;
        }

        try {
            return new File(filename).length();
        }
        catch (SecurityException se) {
            logger.fatal("getFileSize() failed", se);
            return 0;
        }
    }


    /**
     * Copy file
     *
     * @param sourceFilename       source file
     * @param destinationFilename  destination file
     */
    public static boolean copyFile(String sourceFilename, String destinationFilename) {
        if (sourceFilename == null) {
            logger.warn("copyFile() failed because sourceFilename is null");
            return false;
        }
        if (destinationFilename == null) {
            logger.warn("copyFile() failed because destinationFilename is null");
            return false;
        }

        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel fcin = null;
        FileChannel fcout = null;
        try {
            // open stream and channel from input
            fis = new FileInputStream(sourceFilename);
            fcin = fis.getChannel();

            // open stream and channel from output
            fos = new FileOutputStream(destinationFilename);
            fcout = fos.getChannel();

            fcin.transferTo(0, fcin.size(), fcout);

            return true;
        }
        catch (IOException ioe) {
            logger.fatal("copyFile() failed", ioe);
            return false;
        }
        catch (SecurityException se) {
            logger.fatal("copyFile() failed", se);
            return false;
        }
        finally {
            try {
                if (fcin != null) {
                    fcin.close();
                }
            }
            catch (IOException ioe) {
                logger.fatal("copyFile() can't close FileChannel");
            }

            try {
                if (fis != null) {
                    fis.close();
                }
            }
            catch (IOException ioe) {
                logger.fatal("copyFile() can't close FileInputStream");
            }

            try {
                if (fcout != null) {
                    fcout.close();
                }
            }
            catch (IOException ioe) {
                logger.fatal("copyFile() can't close FileChannel");
            }

            try {
                if (fos != null) {
                    fos.close();
                }
            }
            catch (IOException ioe) {
                logger.fatal("copyFile() can't close FileOutputStream");
            }
        }
    }


    /**
     * Add folder to path
     *
     * @param startFolder  start folder
     * @param addedObject  object for adding
     * @return             start folder + add object
     */
    public static String addFolder(String startFolder, String addedObject) {
        if (startFolder == null) {
            logger.warn("addFolder() startFolder is null");
            startFolder = "";
        }
        if (addedObject == null) {
            logger.warn("addFolder() addedObject is null");
            addedObject = "";
        }

        // check if PATH_SEPARATOR is allready at the end of start folder
        if (! startFolder.endsWith(PATH_SEPARATOR)) {
            return startFolder + PATH_SEPARATOR + addedObject;
        }
        else {
            return startFolder + addedObject;
        }
    }


    /**
     * Saves StringBuffer to file
     *
     * @param filename  file name
     * @param content   content for file
     * @return          true save was ok
     */
    public static boolean saveFile(String filename, StringBuffer content) {

        FileWriter writer = null;
        try {
            writer = new FileWriter(filename);

            writer.write(content.toString());
            //for (int i = 0, j = content.length();  i < j; i++) {
            //    writer.write(content.charAt(i));
            //}
            return true;
        }
        catch (IOException ioe) {
            logger.fatal("saveFile() failed", ioe);
            return false;
        }
        finally {
        	IOUtils.closeQuietly(writer);
        }
    }


    /**
     * Saves StringBuffer to file
     *
     * @param filename  file name
     * @param savePath  path
     * @param content   content for file
     * @return          true save was ok
     */
    public static boolean saveFile(String filename, String savePath, StringBuffer content) {

        String completeFileName = null;

        // add file to path
        completeFileName = addFolder(savePath, filename);

        // save content to file
        return saveFile(completeFileName, content);
    }


    /**
     * Gets MD5Sum for file
     *
     * @param file  file
     * @return      MD5Sum for a file
     */
    public static String getMD5Sum(String file) {

        byte[] buf = new byte[65536];

        MD5InputStream md5In = null;
        try {
            md5In = new MD5InputStream(new BufferedInputStream(new FileInputStream(file)));
            while (md5In.read(buf) != -1) {
                // do nothing?
            }

            return MD5.asHex(md5In.hash());
        }
        catch (IOException ioe) {
            logger.fatal("getMD5Sum() failed for file='" + file + "'", ioe);
            return "";
        }
        finally {
            try {
                if (md5In != null) {
                    md5In.close();
                }
            }
            catch (IOException ioe) {
                logger.fatal("getMD5Sum() can't close MD5InputStream", ioe);
            }
        }
    }


    /**
     * The extension of a filename - that indicates what kind of file it is.
     *
     * @param filename  filename
     * @return          last extension without point
     */
    public static String getFileExtension(String filename) {
        int lastPoint = filename.lastIndexOf(".");

        // only a extension exist
        if (lastPoint != -1 && lastPoint + 1 != filename.length()) {
            return filename.substring(lastPoint + 1, filename.length());
        }
        else {
            return "unknown";
        }
    }
}
