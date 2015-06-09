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

import java.util.Calendar;

//不用修改本类中的代码
/**
 * This is a wrapper class for a lucene index.
 *
 * @version $Id: DocSearcherIndex.java 171 2012-09-07 20:08:01Z henschel $
 */
public class DocSearcherIndex {
    private boolean shouldBeSearched;
    private String name = "";
    private String path = "";
    private String indexPath = "";
    private final int depth;
    private String lastIndexed = "";
    private final boolean isWeb;
    private String match = "";
    private String replace = "";
    private String archiveDir = "";
    private boolean isSpider; // indicates if our index is made from crawling
    private final int indexPolicy;
    //private boolean isAnImport = false;


    /**
     * consrtutor notes: path specifies the folder where the files to be
     * searched are located, name of the index,
     * shouldBeSearched means should be searched by default, depth refers to
     * number of subfolders from the start (path) to search in, indexerPath is
     * the location (folder) for the actual lucene index on disc, isWeb should
     * be true if the index is mean for web based searching, match and replace
     * are used for convering files to URLs for web based searching, lastIndexed
     * is a string format for date of last index update, indexPolicy specifies
     * when an index should be updated, and archiveDir specifies a folder where
     * the lucene index can be zipped up to
     *
     * @param path
     * @param name
     * @param shouldBeSearched
     * @param depth
     * @param indexPath
     * @param isWeb
     * @param match
     * @param replace
     * @param lastIndexed
     * @param indexPolicy
     * @param archiveDir
     */
    public DocSearcherIndex(String path, String name, boolean shouldBeSearched, int depth, String indexPath, boolean isWeb, String match, String replace, String lastIndexed,
            int indexPolicy, String archiveDir) {
        this.archiveDir = archiveDir;
        this.indexPolicy = indexPolicy;
        this.isWeb = isWeb;
        this.match = match;
        this.replace = replace;
        this.shouldBeSearched = shouldBeSearched;
        this.path = path;
        this.depth = depth;
        this.name = name;
        this.indexPath = indexPath;
        this.lastIndexed = lastIndexed;
        if (path.equals(replace) && (path.startsWith("http") || path.startsWith("ftp:"))) {
            isSpider = true;
        }
    }


    /**
     * Contructor
     *
     * @param path
     * @param name
     * @param shouldBeSearched
     * @param depth
     * @param indexPath
     * @param isWeb
     * @param match
     * @param replace
     * @param indexPolicy
     * @param archiveDir
     */
    public DocSearcherIndex(String path, String name, boolean shouldBeSearched, int depth, String indexPath, boolean isWeb, String match, String replace, int indexPolicy,
            String archiveDir) {
        this.archiveDir = archiveDir;
        this.indexPolicy = indexPolicy;
        this.isWeb = isWeb;

        if (match.startsWith("[cdrom]")) {
            this.match = match;
            this.replace = replace;
        }
        else if (!isWeb) {
            this.match = "na";
            this.replace = "na";
        }
        else {
            this.match = match;
            this.replace = replace;
        }

        this.shouldBeSearched = shouldBeSearched;
        this.path = path;
        // currently removed, because unused
        //if (path.toLowerCase().endsWith(".zip")) {
        //    isAnImport = true;
        //} else {
        //    isAnImport = false;
        //}

        this.depth = depth;
        this.name = name;
        this.indexPath = indexPath;

        // calendar info
        Calendar nowD = Calendar.getInstance();
        String mon = Integer.toString(nowD.get(Calendar.MONTH) + 1);
        String year = Integer.toString(nowD.get(Calendar.YEAR));
        String day = Integer.toString(nowD.get(Calendar.DAY_OF_MONTH));
        this.lastIndexed = mon + "/" + day + "/" + year;
    }


    /**
     * True if the index is intended for CD based use
     *
     * @return for CD targetted indexes
     */
    public boolean isCdrom() {
        if (match.startsWith("[cdrom]")) {
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * String representation of the folder where the content for the search
     * index is located...i.e. the stuff you want to search.
     *
     * @return the path in the filesystem where the content can be found
     */
    public String getPath() {
        return path;
    }


    /**
     * The folder where the lucene index is located
     *
     * @return folder where the lucene index can be found
     */
    public String getIndexPath() {
        return indexPath;
    }


    /**
     * The folder where the lucene index is located
     *
     * @param indexPath  lucene index path
     */
    public void setIndexPath(String indexPath) {
        this.indexPath = indexPath;
    }


    /**
     * The folder where the index can be archived to in zip format
     *
     * @return a string representing the archive folder
     */
    public String getArchiveDir() {
        return archiveDir;
    }


    /**
     * Name of the index
     *
     * @return name of the index
     */
    public String getName() {
        return name;
    }


    /**
     * String format of the date the content was last indexed
     *
     * @return a string representing date the index was last updated
     */
    public String getLastIndexed() {
        return lastIndexed;
    }


    /**
     * String format of the date the content was last indexed
     *
     * @param lastIndexed a string representing date the index was last updated
     */
    public void setLastIndexed(String lastIndexed) {
        this.lastIndexed = lastIndexed;
    }


    /**
     * Should the index be searched by default
     *
     * @return true if the index should be searched by default
     */
    public boolean getShouldBeSearched() {
        return shouldBeSearched;
    }


    /**
     * Should the index be searched by default
     *
     * @param shouldBeSearched  true if the index should be searched by default
     */
    public void setShouldBeSearched(boolean shouldBeSearched) {
        this.shouldBeSearched = shouldBeSearched;
    }


    /**
     * Is the content intended for web based searching
     *
     * @return true if intended for web based searching
     */
    public boolean getIsWeb() {
        return isWeb;
    }


    /**
     * Index update policies: 0=when I say so, 1=on startup, 2=daily, etc...
     *
     * @return the policy for updating the DocSearcherIndex
     */
    public int getIndexPolicy() {
        return indexPolicy;
    }


    /**
     * The number of subfolders for indexing
     *
     * @return number of subfolders down the tree to index, starting with the
     *         path where the content is located
     */
    public int getDepth() {
        return depth;
    }


    /**
     * The text to look for when converting a filename to a URL for web based
     * searching
     *
     * @return text to find in conversion of a filename to a URL
     */
    public String getMatch() {
        return match;
    }


    /**
     * The text to insert (switch in given some match text) when converting a
     * filename to a URL for web based searching
     *
     * @return text to replace in conversion of a filename to a URL
     */
    public String getReplace() {
        return replace;
    }


    /**
     * Indicates if our index is made from crawling
     *
     * @return true if our index is made from crawling
     */
    public boolean getIsSpider() {
        return isSpider;
    }


	@Override
	public String toString() {
		return "DocSearcherIndex [shouldBeSearched=" + shouldBeSearched
				+ ", name=" + name + ", path=" + path + ", indexPath="
				+ indexPath + ", depth=" + depth + ", lastIndexed="
				+ lastIndexed + ", isWeb=" + isWeb + ", match=" + match
				+ ", replace=" + replace + ", archiveDir=" + archiveDir
				+ ", isSpider=" + isSpider + ", indexPolicy=" + indexPolicy
				+ "]";
	}
    
}
