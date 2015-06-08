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

/**
 * Class LogSearch
 * 
 * @version $Id: LogSearch.java 12 2005-10-01 17:09:50Z henschel $
 */
public class LogSearch {
    String time = "";
    String ip = "";
    int numResults = 0;
    double score = 0.0;
    String searchText = "";


    LogSearch(String time, String ip, int numResults, double score, String searchText) {
        this.time = time;
        this.ip = ip;
        this.numResults = numResults;
        this.score = score;
        this.searchText = searchText;
    }
}
