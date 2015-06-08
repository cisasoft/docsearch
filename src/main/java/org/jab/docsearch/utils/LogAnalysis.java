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
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.jab.docsearch.DocSearch;
import org.jab.docsearch.Environment;
import org.jab.docsearch.FileEnvironment;

/**
 * Class LogAnalysis
 *
 * @version $Id: LogAnalysis.java 91 2008-01-09 21:59:11Z henschel $
 */
public class LogAnalysis {
    /**
     * FileEnvironment
     */
    private final static FileEnvironment fEnv = FileEnvironment.getInstance();

    public static void doLogAnalysis(DocSearch ds, String logFile)
         throws IOException {

        // Environment
        Environment env = Environment.getInstance();

        // make sure the file exists
        //
        // things to summarize include:
        //
        // period covered
        String startTime = "";
        String endTime = "";

        //
        // total number of searches,
        int totalNumSearches = 0;

        //
        // total search users
        int numSearchUsers = 0;

        //
        // total search sessions
        // int numSearchSessions = 0;

        //
        // number poor results (below 0.30)
        ArrayList<LogSearch> poorResultsSearches = new ArrayList<LogSearch>(); // logSearch
                                                            // arraylist
        int numBadResults = 0;

        //
        // FREQUENT SEARCHED WORDS
        ArrayList<SearchedWord> searchedWords = new ArrayList<SearchedWord>(); // searchedWord arraylist
        ArrayList<String> usersList = new ArrayList<String>();
        ArrayList<LogSearch> loggedSearches = new ArrayList<LogSearch>(); // logSearch arraylist

        LogSearch tempLogSearch;
        SearchedWord tempSearchedWord;
        double tempScoreD = 0.0;
        String tempTime = "";
        String tempIp = "";
        String tempHits = "";
        int tempHitsInt = 0;
        String tempScore = "";
        String tempSearchText = "";
        int totalUsers = 0;

        //
        StringBuffer readBuf = new StringBuffer(); // for reading in the chars
        //
        StringBuffer badSearchBuf = new StringBuffer();
        File oldLogFile = new File(logFile);
        if (oldLogFile.exists()) {
            badSearchBuf.append('\n');
            // TODO close FileReader save
            FileReader fr = new FileReader(oldLogFile);
            int i;
            String c = "";
            int curItemNo = 0;
            boolean inItem = false;
            String lastIP = "";
            String tempWord = "";
            StringBuffer outputBuf = new StringBuffer();
            while ((i = fr.read()) != -1) {
                if (i == -1) {
                    // handle end of line
                    break;
                }
                else {
                    c = "" + (char) i;
                    if ((c.equals("\n")) || (c.equals("\r"))) {
                        tempSearchText = readBuf.toString();

                        // add out search to the arraylist
                        if (!tempScore.equals("")) {
                            try {
                                tempScoreD = Double.parseDouble(tempScore);
                                tempHitsInt = Integer.parseInt(tempHits);
                                tempLogSearch = new LogSearch(tempTime, tempIp, tempHitsInt, tempScoreD, tempSearchText);
                                loggedSearches.add(tempLogSearch);

                                // parse the words from the searchText
                                int numSearchChars = tempSearchText.length();
                                tempWord = "";
                                StringBuffer tempWB = new StringBuffer();
                                char tempC = ' ';
                                boolean isLet = false;
                                boolean inWord = false;
                                for (int y = 0; y < numSearchChars; y++) {
                                    tempC = tempSearchText.charAt(y);
                                    isLet = (Character.isLetter(tempC));
                                    if (isLet) {
                                        inWord = true;
                                    }
                                    else {
                                        if (inWord) {
                                            tempWord = tempWB.toString();
                                            if (tempWord.length() > 3) {
                                                // add the word to our list
                                                tempSearchedWord = new SearchedWord(tempWord);
                                                int numWords = searchedWords.size();
                                                boolean addedWord = false;
                                                if (numWords > 0) {
                                                    Iterator<SearchedWord> wi = searchedWords.iterator();
                                                    SearchedWord sw;
                                                    String lowerWord = tempWord.toLowerCase();
                                                    while (wi.hasNext()) {
                                                        sw = wi.next();
                                                        if (sw.lowerName.equals(lowerWord)) {
                                                            addedWord = true;
                                                            sw.hit(); // increments
                                                                        // num
                                                                        // searches
                                                        }
                                                    }
                                                }
                                                // end for checking if word is
                                                // in there already
                                                else {
                                                    addedWord = true;
                                                    searchedWords.add(tempSearchedWord);
                                                }
                                                // end for adding the word

                                                if (!addedWord) {
                                                    searchedWords.add(tempSearchedWord);
                                                }
                                            }

                                            tempWB = new StringBuffer();
                                        }

                                        inWord = false;
                                    }
                                    if ((inWord) && (isLet)) {
                                        tempWB.append(tempC);
                                    }
                                }

                                // record the poor hits
                                if ((tempHitsInt == 0) || (tempScoreD < 0.30)) {
                                    poorResultsSearches.add(tempLogSearch);
                                    numBadResults++;
                                }

                                if (! lastIP.equals(tempIp)) {
                                    numSearchUsers++;
                                }

                                lastIP = tempIp;
                                totalNumSearches++;
                            }
                            catch (Exception eN) {
                                ds.setStatus("Error parsing log file:" + eN.toString());
                            }
                        }

                        // reset the line
                        readBuf = new StringBuffer();
                        tempTime = "";
                        tempIp = "";
                        tempHits = "";
                        tempScore = "";
                        curItemNo = 0;
                        tempHitsInt = 0;
                    }
                    else if ((c.equals("[")) || (c.equals("#"))) {
                        inItem = true;
                    }
                    else if (c.equals("]")) {
                        inItem = true;
                        switch (curItemNo) {
                            case 0: // time
                                tempTime = readBuf.toString();
                                break;

                            case 1: // ip
                                tempIp = readBuf.toString();
                                if (! Utils.hasUser(tempIp, usersList)) {
                                    usersList.add(tempIp);
                                }
                                break;

                            case 2: // hits
                                tempHits = readBuf.toString();
                                break;

                            case 3: // score
                                tempScore = readBuf.toString();
                                break;
                        }

                        curItemNo++;
                        readBuf = new StringBuffer();
                        inItem = false;
                    }
                    else {
                        if (inItem) {
                            readBuf.append(c);
                        }
                    }
                }
            }
            // end while reading

            fr.close();
            // load the start and endTimes
            LogSearch ttl;
            int totalSes = loggedSearches.size();
            if (totalSes > 0) {
                ttl = loggedSearches.get(0);
                endTime = ttl.time;
                ttl = loggedSearches.get(totalSes - 1);
                startTime = ttl.time;
            }

            // now show our results
            totalUsers = usersList.size();
            outputBuf.append("There were " + totalNumSearches + " searches performed from " + startTime + " to " + endTime + " from " + totalUsers + " individual users.");
            outputBuf.append("\n\n" + numBadResults + " searches produced unsatisfactory results.");
            double avScore = Utils.getAverageSearchScore(loggedSearches);
            if (avScore > 0.3) {
                outputBuf.append("\n\nAverage search score was Good at " + avScore);
            }
            else {
                outputBuf.append("\n\nAverage search score was Poor at " + avScore);
            }
            outputBuf.append('\n');
            // poor results
            int numPoor = poorResultsSearches.size();
            if (numPoor > 0) {
                badSearchBuf.append("\n\nSearches that produced poor results:\n\n");
                Iterator<LogSearch> pi = poorResultsSearches.iterator();
                LogSearch tl;
                int maxPoor = 200;
                int curP = 0;
                while (pi.hasNext()) {
                    tl = pi.next();
                    curP++;
                    if (tl.searchText.indexOf("\"") == 0) {
                        badSearchBuf.append("\n * Search for \"" + tl.searchText + "\" produced " + tl.numResults + " hits, with a highest score of " + tl.score);
                    }
                    else {
                        badSearchBuf.append("\n * Search for " + tl.searchText + " produced " + tl.numResults + " hits, with a highest score of " + tl.score);
                    }
                    if (curP > maxPoor) {
                        break;
                    }
                }
            }
            // end if numpoor

            // now add our most searched for items
            int numWordsSearched = searchedWords.size();
            if (numWordsSearched > 0) {
                Iterator<SearchedWord> swi = searchedWords.iterator();
                SearchedWord tw;
                ArrayList<SearchedWord> highestSWs = new ArrayList<SearchedWord>();
                int lastHighest = 1;
                while (swi.hasNext()) {
                    tw = swi.next();
                    if (tw.numTimeSearched > 1) {
                        if (tw.numTimeSearched > lastHighest) {
                            lastHighest = tw.numTimeSearched;
                            highestSWs.add(0, tw);
                        }
                    }
                }

                int numHighs = highestSWs.size();
                if (numHighs > 0) {
                    outputBuf.append("\n\n Fequent Search Words:\n");
                    Iterator<SearchedWord> hi = highestSWs.iterator();
                    while (hi.hasNext()) {
                        tw = hi.next();
                        outputBuf.append("\n * Word \"");
                        outputBuf.append(tw.name);
                        outputBuf.append("\" was searched ");
                        outputBuf.append(tw.numTimeSearched);
                        outputBuf.append(" times.");
                    }
                }
            }
            // end if there were words found
            outputBuf.append(badSearchBuf.toString());
            //
            if (! env.isGUIMode()) {
                System.out.println(outputBuf.toString());
            }
            else {
                // show the results
                String analysisFile = FileUtils.addFolder(fEnv.getWorkingDirectory(), "search_log_analysis.txt");
                FileUtils.saveFile(analysisFile, outputBuf);
                ds.curPage = "report";
                ds.doExternal(analysisFile);
            }
        }
        else {
            ds.setStatus("Log File (" + logFile + ") does not exist.");
        }
    }
}
