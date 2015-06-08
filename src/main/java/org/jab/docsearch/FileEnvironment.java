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
package org.jab.docsearch;

import org.apache.log4j.Logger;
import org.jab.docsearch.utils.FileUtils;
import org.jab.docsearch.utils.Utils;

/**
 * This class contains all file variables for a running session.<br>
 * The class is an Singleton.
 *
 * @version $Id: FileEnvironment.java 176 2012-11-04 19:12:28Z henschel $
 */
public final class FileEnvironment {
    /**
     * Log4J
     */
    private final Logger logger = Logger.getLogger(getClass().getName());
    /**
     * Environment variable
     */
    private static FileEnvironment env = new FileEnvironment();
    /**
     * Users home
     */
    private String userHome;
    /**
     * Working directory
     */
    private String workingDir;
    /**
     * Index directory
     */
    private String indexDir;
    /**
     * Archive directory
     */
    private String archiveDir;
    /**
     * Start directory
     */
    private String startDir;
    /**
     * Content directory
     */
    private String contentDir;
    /**
     * Temp directory
     */
    private String tempDir;

    /**
     * Index list file
     */
    private String indexListFile;
    /**
     * Bookmark file
     */
    private String bookmarkFile;
    /**
     * Constant help_page
     */
    public final static String FILENAME_HELP_PAGE = "help_page.htm";
    /**
     * Constant help_page in webstart
     */
    public final static String FILENAME_HELP_PAGE_WS = "help_page_ws.htm";
    /**
     * Constant start_page
     */
    public final static String FILENAME_START_PAGE = "start_page.htm";
    /**
     * Constant start_page in webstart
     */
    public final static String FILENAME_START_PAGE_WS = "start_page_ws.htm";
    /**
     * Constant splash image
     */
    public final static String FILENAME_SPLASH_IMAGE = "splash.gif";

    /**
     *
     */
    private FileEnvironment() {
        logger.info("FileEnvironment() created");
    }


    /**
     * Gets instance of FileEnvironment
     *
     * @return  instance of FileEnvironment
     */
    public static FileEnvironment getInstance() {
        return env;
    }


    /**
     * Gets users home directory
     *
     * @return  users home directory
     */
    public String getUserHome() {
        return userHome;
    }


    /**
     * Sets users home directory
     *
     * @param userHome  users home directory
     */
    public void setUserHome(String userHome) {
        this.userHome = userHome;
    }


    /**
     * Gets working directory
     *
     * @return  working directory
     */
    public String getWorkingDirectory() {
        if (workingDir == null) {
        	workingDir = FileUtils.addFolder(userHome, ".docSearcher");
        }
        return workingDir;
    }


    /**
     * Sets working directory
     *
     * @param workingDir  working directory
     */
    public void setWorkingDirectory(String workingDir) {
        this.workingDir = workingDir;
    }


    /**
     * Gets index directory
     *
     * @return index directory
     */
    public String getIndexDirectory() {
        if (workingDir == null) {
            getWorkingDirectory();
        }

        indexDir = FileUtils.addFolder(workingDir, "indexes");

        return indexDir;
    }


    /**
     * Sets index directory
     *
     * @param indexDir  index directory
     */
    public void setIndexDirectory(String indexDir) {
        this.indexDir = indexDir;
    }


    /**
     * Gets archive directory
     *
     * @return archive directory
     */
    public String getArchiveDirectory() {
        if (workingDir == null) {
            getWorkingDirectory();
        }

        archiveDir = FileUtils.addFolder(workingDir, "archives");

        return archiveDir;
    }


    /**
     * Resets archive directory
     */
    public void resetArchiveDirectory() {
        this.archiveDir = null;
    }


    /**
     * Gets start directory
     *
     * @return start directory
     */
    public String getStartDirectory() {
        if (startDir == null) {
            startDir = System.getProperty("user.dir");
        }

        return startDir;
    }


    /**
     * Gets content directory
     *
     * @return content directory
     */
    public String getContentDirectory() {
        if (contentDir == null) {
            contentDir = FileUtils.addFolder(getStartDirectory(), "content");
        }

        return contentDir;
    }


    /**
     * Gets user preferences file
     *
     * @return preferences file
     */
    public String getOldUserPreferencesFile() {
        if (workingDir == null) {
            getWorkingDirectory();
        }

        return FileUtils.addFolder(workingDir, "docSearch_prefs.txt");
    }


    /**
     * Gets user preferences file
     *
     * @return preferences file
     */
    public String getUserPreferencesFile() {
        if (workingDir == null) {
            getWorkingDirectory();
        }

        return FileUtils.addFolder(workingDir, "docsearcher.properties");
    }


    /**
     * Gets index list file
     *
     * @return index list file
     */
    public String getIndexListFile() {
        if (workingDir == null) {
            getWorkingDirectory();
        }

        indexListFile = FileUtils.addFolder(workingDir, "index_list.htm");

        return indexListFile;
    }


    /**
     * Resets index list file
     */
    public void resetIndexListFile() {
        this.indexListFile = null;
    }


    /**
     * Gets bookmark file
     *
     * @return bookmark file
     */
    public String getBookmarkFile() {
        if (workingDir == null) {
            getWorkingDirectory();
        }

        bookmarkFile = FileUtils.addFolder(workingDir, "bookmarks.htm");

        return bookmarkFile;
    }


    /**
     * Resets bookmark file
     */
    public void resetBookmarkFile() {
        this.bookmarkFile = null;
    }


    /**
     * Gets temp directory
     *
     * @return temp directory
     */
    public String getTempDirectory() {
        if (tempDir == null) {
            tempDir = System.getProperty("java.io.tmpdir");
        }

        return tempDir;
    }


    /**
     * Sets temp directory
     *
     * @param tempDir  temp directory
     */
    public void setTempDirectory(final String tempDir) {
        this.tempDir = tempDir;
    }


    /**
     * Gets Spider URL file of index
     *
     * @param indexName  Index name
     * @return           Path to file
     */
    public String getSpiderIndexURLFile(final String indexName) {
        String folder = getIndexDirectory();
        String file = Utils.replaceAll(" ", indexName, "_") + ".txt";

        return FileUtils.addFolder(folder, file);
    }


    /**
     * Gets Spider bad URL file of index
     *
     * @param indexName  Index name
     * @return           Path to file
     */
    public String getSpiderBadIndexURLFile(final String indexName) {
        String folder = getIndexDirectory();
        String file = Utils.replaceAll(" ", indexName, "_") + "_bad_links.txt";

        return FileUtils.addFolder(folder, file);
    }


    /**
     * Gets the URL for an icon, in dependency to Java Webstart
     *
     * @param icon  Icon filename
     * @return      URL or null
     */
    public String getIconURL(final String icon) {
        String result = null;

        // Java webstart or local
        if (Environment.getInstance().isWebStart()) {
            result = getClass().getResource("/icons/" + icon).toExternalForm();
        }
        else {
            result = "icons/" + icon;
        }

        return result;
    }
}
