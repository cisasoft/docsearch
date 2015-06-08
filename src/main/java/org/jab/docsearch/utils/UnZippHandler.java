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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Class UnZippHandler
 *
 * @version $Id: UnZippHandler.java 89 2007-04-23 20:16:34Z henschel $
 */
public class UnZippHandler {
    final static int BUFFER = 2048;
    String fileName = "";
    String targetDir = "";
    String pathSep = System.getProperty("file.separator");


    public UnZippHandler(String fileName, String targetDir) {
        this.fileName = fileName;
        this.targetDir = targetDir;
    }


    public void unZip() throws IOException {
        // does folder targetDir exist?
        File testTargetDir = new File(targetDir);
        if (!testTargetDir.exists()) {
            throw new IOException("TARGET DIRECTORY (" + targetDir + ") DOESN'T EXIST!");
        }
        else { // target directory exists
            System.out.println("Unzipping archive: " + fileName + " to directory: " + targetDir);
            // int numFilesExtracted = 0;
            // int numFolders = 0;
            // File curZipFile;
            File curExtractFolderFile;
            String curExtractFolderString = "";
            String newFile = "";
            String curZipFolderString = "";
            String entryFileNameOnly = "";
            FileOutputStream fos;
            BufferedOutputStream dest;
            FileInputStream fis = new FileInputStream(fileName);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            int count;
            boolean madeFolder = false;
            String entryName = "";
            byte[] data = new byte[BUFFER];
            ZipEntry entry;
            try {
                while ((entry = zis.getNextEntry()) != null) {
                    entryName = entry.getName();
                    entryFileNameOnly = Utils.getNameOnly(entryName);
                    curZipFolderString = Utils.getFolderOnly(entryName);
                    if (!curZipFolderString.equals("")) {
                        curExtractFolderString = FileUtils.addFolder(targetDir, curZipFolderString);
                    } else {
                        curExtractFolderString = targetDir;
                    }

                    newFile = FileUtils.addFolder(curExtractFolderString, entryFileNameOnly);
                    if (pathSep.equals("\\")) {
                        newFile = Utils.replaceAll("/", newFile, "\\");
                    } else {
                        newFile = Utils.replaceAll("\\", newFile, "/");
                    }

                    System.out.println("Extracting from zip:" + entryName + " to " + newFile);
                    curExtractFolderFile = new File(curExtractFolderString);
                    if (!curExtractFolderFile.exists()) {
                        madeFolder = curExtractFolderFile.mkdir();
                    } else {
                        madeFolder = true;
                    }

                    if ((madeFolder) && (!entryFileNameOnly.equals(""))) {
                        fos = new FileOutputStream(newFile);
                        dest = new BufferedOutputStream(fos, BUFFER);
                        while ((count = zis.read(data, 0, BUFFER)) != -1) {
                            dest.write(data, 0, count);
                        }

                        dest.flush();
                        dest.close();
                    }
                }
                zis.close();
            } catch (Exception eZ) {
                eZ.printStackTrace();
            } finally {
                // debug output?
            }
        }
    }


    public static void main(String[] args) {
        String zipArchive = args[0];
        String target = args[1];
        if ((zipArchive != null) && (target != null)) {
            UnZippHandler uz = new UnZippHandler(args[0], args[1]);
            try {
                uz.unZip();
            } catch (Exception eR) {
                System.out.println(eR.toString());
            }
        } else {
            System.out.println("usage :\njava UnZippHanlder zip_File destination_Dir");
        }
    }

}
