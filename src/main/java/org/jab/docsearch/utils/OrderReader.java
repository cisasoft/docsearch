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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

/**
 * Class OrderReader
 * 
 * @version $Id: OrderReader.java 38 2005-10-12 20:58:49Z henschel $
 */
public class OrderReader {
    /**
     * Log4J
     */
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final static int maxItems = 10000;
    private int lastItem;
    private String[] items;
    private String defaultOutName = "zzzOut.txt";


    /**
     * Cronstructor
     */
    protected OrderReader() {
        items = new String[maxItems];
        lastItem = 0;
    }


    /**
     * Add on item to list
     *
     * @param item
     */
    protected void add(String item) {
        if (lastItem < items.length) {
            items[lastItem++] = item;
        }
    }


    /**
     * Prints all content in logfile
     */
    protected void show() {
        logger.debug("show() inside the array are :");
        for (int i = 0; i < lastItem; i++) {
            logger.debug("show() " + i + " = " + items[i]);
        }
    }


    /**
     * Method inItem
     *
     * @param pos
     * @return
     */
    protected String inItem(int pos) {
        return items[pos];
    }


    /**
     * Method save
     *
     * @param fileName
     * @param dirPath
     */
    protected void save(String fileName, String dirPath) {
        if (fileName.equals("")) {
            fileName = defaultOutName;
        }

        PrintWriter fileOut = null;
        try {
            File outputFile;
            if (dirPath.equals("")) {
                outputFile = new File(fileName);
            } else {
                outputFile = new File(dirPath, fileName);
            }

            fileOut = new PrintWriter(new FileWriter(outputFile));

            for (int z = 0; z < lastItem; z++) {
                fileOut.println(items[z]);
            }
        }
        catch (IOException ioe) {
            logger.fatal("save() failed for file='" + dirPath + " " + fileName + "'", ioe);
        }
        finally {
            if (fileOut != null) {
                fileOut.close();
            }
        }
    }


    /**
     * Method save one name
     *
     * @param fileName
     */
    protected void saveOneName(String fileName) {
        if (fileName.equals("")) {
            fileName = defaultOutName;
        }

        PrintWriter fileOut = null;
        try {
            fileOut = new PrintWriter(new FileWriter(fileName));

            for (int z = 0; z < lastItem; z++) {
                fileOut.println(items[z]);
            }

            fileOut.close();
        }
        catch (IOException ioe) {
            logger.fatal("saveOneName() failed for file='" + fileName + "'", ioe);
        }
        finally {
            if (fileOut != null) {
                fileOut.close();
            }
        }
    }


    /**
     * Method add file
     *
     * @param fileName
     */
    protected void addFile(String fileName) {
        if (fileName.equals("")) {
            fileName = defaultOutName;
        }

        BufferedReader completeOrder = null;
        try {
            completeOrder = new BufferedReader(new FileReader(fileName), 1);

            String inputString;
            while ((inputString = completeOrder.readLine()) != null) {
                items[lastItem++] = inputString;
            }
        }
        catch (IOException ioe) {
            logger.fatal("addFile() failed for file='" + fileName + "'", ioe);
        }
        finally {
            try {
                if (completeOrder != null) {
                    completeOrder.close();
                }
            }
            catch (IOException ioe) {
                logger.fatal("addFile() can't close BufferedReader", ioe);
            }
        }
    }
}
