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
package org.jab.docsearch.servlet;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MultiSearcher;
import org.apache.lucene.search.Query;
import org.jab.docsearch.Index;
import org.jab.docsearch.constants.FileType;
import org.jab.docsearch.threads.DsReloader;
import org.jab.docsearch.utils.DateTimeUtils;
import org.jab.docsearch.utils.FileUtils;
import org.jab.docsearch.utils.I18n;
import org.jab.docsearch.utils.Table;
import org.jab.docsearch.utils.Utils;

/**
 * Class DsResult
 *
 * @version $Id: DsResults.java 172 2012-09-14 15:24:32Z henschel $
 */
public class CallDsResults extends HttpServlet {
    /**
     * Log4J logger
     */
    private final Logger logger = Logger.getLogger(getClass().getName());

    /**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 2982839513492242665L;

	boolean error = false; // used to control flow for error messages
    // Searchers
    IndexSearcher[] searchers; // array of indexes
    MultiSearcher searcher; // object that searches the indexes
    int numIndexes = 0;
    Query query = null; // the Query created by the QueryParser
    Hits hits = null; // the search results
    int startindex = 0; // the first index displayed on this page
    int maxpage = 50; // the maximum items displayed on this page
    String queryString = null; // the query entered in the previous page
    String startVal = null; // string version of startindex
    String maxresults = null; // string version of maxpage
    int thispage = 0;
    final static String pathSep = FileUtils.PATH_SEPARATOR;
    // ICONS
    // TODO alt text to resource
    final static String htmlTag = "<img src=..\"images/" + FileType.HTML.getIcon() + "\" border=\"0\" alt=\"Web Page Document\">";
    final static String wordTag = "<img src=..\"images/" + FileType.MS_WORD.getIcon() + "\" border=\"0\" alt=\"MS Word Document\">";
    final static String excelTag = "<img src=..\"images/" + FileType.MS_EXCEL.getIcon() + "\" border=\"0\" alt=\"MS Excel Document\">";
    final static String pdfTag = "<img src=..\"images/" + FileType.PDF.getIcon() + "\" border=\"0\" alt=\"PDF Document\">";
    final static String textTag = "<img src=..\"images/" + FileType.TEXT.getIcon() + "\" border=\"0\" alt=\"Text Document\">";
    final static String rtfTag = "<img src=..\"images/" + FileType.RTF.getIcon() + "\" border=\"0\" alt=\"RTF Document\">";
    final static String ooImpressTag = "<img src=..\"images/" + FileType.OO_IMPRESS.getIcon() + "\" border=\"0\" alt=\"OpenOffice Impress Document\">";
    final static String ooWriterTag = "<img src=..\"images/" + FileType.OO_WRITER.getIcon() + "\" border=\"0\" alt=\"OpenOffice Writer Document\">";
    final static String ooCalcTag = "<img src=..\"images/" + FileType.OO_CALC.getIcon() + "\" border=\"0\" alt=\"OpenOffice Calc Document\">";
    final static String ooDrawTag = "<img src=..\"images/" + FileType.OO_DRAW.getIcon() + "\" border=\"0\" alt=\"OpenOffice Draw Document\">";
    final static String openDocumentTextTag = "<img src=..\"images/" + FileType.OPENDOCUMENT_TEXT.getIcon() + "\" border=\"0\" alt=\"OpenDocument Text Document\">";

    final static String beginForm = "<table border=\"0\"><tr><td valign=\"top\"><form name=DocSearcherForm action=\"search\" method=\"GET\">New Search : <input type=\"text\" name=\"query\" value=\"";
    final static String middleForm = "\"></input> <input type=\"Submit\" value=\"Go\"></input><br># Results per page: <select name=\"maxresults\"><option value=\"10\">10</option><option value=\"20\">20</option><option value=\"30\">30</option><option value=\"40\">40</option></select><input type=\"hidden\" name=\"startat\" value=\"";
    final static String endForm = "\"></input> </form></td><td> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<form name=help> </td><td valign=\"top\">";

    final static String helpTable = " &nbsp; &nbsp; <input type=button value=\"Tips for Searching\" onClick=\"showHelp()\"></form><script language=\"JavaScript\">\nfunction showHelp() {\nvar showVideo=true;\nif (showVideo) {\n	var OpenWindow=window.open(\"\", \"newwin\", \"menubar=no, resizable=yes, scrollbars=yes, width=600, height=550\");\n	OpenWindow.document.write(\"<HTML>\");\n	OpenWindow.document.write(\"<HEAD><TITLE>Search Help</TITLE></HEAD><body topmargin=0 leftmargin=0>\");\n	OpenWindow.document.write(\" <table width=580 border=0 bgcolor=AFDCDA><tr> <td valign=top><h3 align=left>Basic Searches</h3><p align=left><small>There are 4 types of Basic searches you can perform: <b>Keyword</b>, <b>Phrase</b>, <b>Boolean</b>, and <b>Wild Card</b>.The example queries below describe each of these:</p><table border=1 align=center width=85% bgcolor=ffefda><tr>	<th><small>Example</small></th>	<th><small>Results</small></th></tr><tr>	<td bgcolor=ffeffa><small><font color=blue>summer vacation</font></small></td>	<td bgcolor=ffeffa><small>Finds <b>keywords</b> &quot;summer&quot; and &quot;vacation&quot;</small></td></tr><tr>	<td bgcolor=fdffda><small><font color=blue>&quot;summer vacation&quot;</font></small></td>	<td bgcolor=fdffda><small>Finds the <b>phrase</b> &quot;summer vacation&quot;</small></td></tr><tr>	<td bgcolor=ffeffa><font color=blue><small>&quot;john smith&quot; -&quot;john brown&quot;</font></small></td>	<td bgcolor=ffeffa><small>Finds &quot;john smith&quot; but <b>excludes</b> &quot;john brown&quot;</small></td></tr><tr>	<td bgcolor=fdffda><small><font color=blue>virtu*</font></small></td>	<td bgcolor=fdffda><small>Finds virtuous, virtue, virtuosity, etc... 	<br><br>This is a <b>wild card search</b>.</small></td></tr></table></small><!-- end of inner table --></td><td valign=top bgcolor=FADBCD><h3 align=left>Advanced Searches</h3><small><p align=left>	Advanced Searches are formatted thus: <br><font color=blue><b>+(+field:(search text here) AND +another_field:(more search text)) OR +another_field:(even more search text)</b></font></br><br>The fields you can search in include:</p><ul> <li>title <li>author <li>summary <li>body <li>path (file name) <li>URL  <li>type (the type of document, AKA the document extension... i.e. doc for MS Word, htm for web pages, etc...)</ul><p align=left>	Here's example of a complex search:<br><b><font color=red>+(+(+title:(&quot;Homeland Security&quot;) AND +author:(John)) OR +summary:(DHS -TSA)) AND +type:(pdf)</font></b></p><p align=left> The above example says, find documents where title includes the phrase &quot;Homeland Security&quot;,  the author has text John, or the summary has text DHS - but exclude documents where summary has  text TSA in it - and retrieve only PDF documents.</p></small></td></tr></table> \");\n	OpenWindow.document.write(\"  <p align=center><form><input type=button value=Close onClick=self.close()></form></p>    </body></html>\");\n	} \n\n} \n</script><noscript>JavaScript not enabled </noscript>\n </td></tr></table> ";

    // index file building
    String indexFile = "index_list.htm";
    String tempTextFile = "temp_log_text.txt";
    String logDir = "";
    String lastSearch = "";
    DsReloader reloader;

    ArrayList<DocSearcherIndex> indexes = new ArrayList<DocSearcherIndex>();


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        // read some configs
        ServletConfig appConfig = getServletConfig();
        String userHome = appConfig.getInitParameter("USER_HOME");
        System.out.println(userHome);
        logDir = appConfig.getInitParameter("LOG_DIR");

        // check user home
        if (userHome == null || "".equals(userHome)) {
            System.err.println("No User home directory specified (directory should contain .docSearcher configuration directory). Edit web.xml as needed.");
            return;
        }
        File testFile = new File(userHome);
        if (! testFile.isDirectory()) {
        	System.err.println("Specified user home is a director (" + userHome + ")");
        }

        // check index dir
        String indexDir = addFolder(userHome, "indexes");
        testFile = new File(indexDir);
        if (! testFile.isDirectory()) {
        	System.err.println("Can't find index directory (" + indexDir + ")");
        }

        // check index list file
        indexFile = addFolder(userHome, "index_list.htm");
        testFile = new File(indexFile);
        if (! testFile.isFile()) {
            System.err.println("Missing INDEX FILE:" + indexFile);
        }

        // temp log test
        tempTextFile = addFolder(userHome, "temp_log_text.txt");

        // Load indexes
        try {
            System.out.println("Initializing Lucene search indexes located at " + indexDir);
            loadSearchers();
            setReloader();
        }
        catch (Exception e) {
            System.out.println("ERROR INITIALIZING SEARCH HANDLER!\n" + e.toString() + "\nLucene Index is:" + indexDir);
            e.printStackTrace();
        }
    }


    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        error = false;
        boolean logQuery = true;
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        // String helpString="";
        String logPrefix = "";
        String findText = "";
        int resultsNumFound = 0;
        String requireUrl = "";
        float highestScore = 0;
        String remoteUser = req.getRemoteAddr();
        if (remoteUser != null)
            System.out.println("Search from :" + remoteUser);
        else
            remoteUser = "UNKNOWN";
        //
        StringBuffer errBuf = new StringBuffer();
        String searchForm = "";
        //
        out.println("<html><head><title>Search Results</title></head><body>");
        out.flush();
        log("SEARCH INVOKED...");
        //
        if (error == false) { // did we open the index?
            out.println("<b>");
            queryString = req.getParameter("query"); // get the search
                                                        // criteria
            if (queryString == null) {
                queryString = "";
                logQuery = false;
                error = true;
            } else {
                out.print("<h2 align=\"left\">");
                out.print(I18n.getString("results_for_search"));
                out.print(" <font color=blue>");
                out.print(Utils.convertTextToHTML(queryString));
                out.println("</font></h2>");
                if (lastSearch.equals(queryString)) {
                    logQuery = false;
                }
            }
            out.println("</b></p>");
            // log prefix
            logPrefix = req.getParameter("log");
            if (logPrefix == null) {
                logPrefix = "";
            }
            // require URL
            requireUrl = req.getParameter("url_text_required");
            if (requireUrl == null) {
                requireUrl = "";
            }
            startVal = req.getParameter("startat"); // get the start index
            maxresults = req.getParameter("maxresults"); // get max results
                                                            // per page
            try {
                maxpage = Integer.parseInt(maxresults); // parse the max results
                                                        // first
                startindex = Integer.parseInt(startVal); // then the start
                                                            // index
            } catch (Exception e) {
                errBuf.append("\nMissing maxpage and startvals:");
                errBuf.append(e.toString());
                maxpage = 10;
                startindex = 0;
            }
            if (queryString == null) {
                throw new ServletException("no query specified");
            }
            else if (!queryString.equals("")) {
                searchForm = beginForm + Utils.convertTextToHTML(queryString) + middleForm + "0" + endForm + helpTable;
                out.println(searchForm);
                Analyzer analyzer = new StandardAnalyzer();
                try {
                    if (queryString.indexOf("(") == -1) {
                        findText = "+" + Index.FIELD_KEYWORDS + ":(" + queryString + ")^4" +
                        		" OR +" + Index.FIELD_TITLE + ":(" + queryString + ")^2" +
                        		" OR +" + Index.FIELD_BODY + ":(" + queryString + ")" +
                        		" OR +" + Index.FIELD_SUMMARY + ":(" + queryString + ")^3";
                        if (!requireUrl.equals("")) {
                            findText += " AND +" + Index.FIELD_URL + ":(" + requireUrl + ")";
                        }
                    } else {
                        findText = queryString;
                    }

                    // create query
                    QueryParser queryParser = new QueryParser(Index.FIELD_BODY, analyzer);
                    query = queryParser.parse(queryString);

                } catch (Exception e) {
                    log("SEARCH ERROR: parsing query " + e.toString());
                    e.printStackTrace();
                    error = true;
                } // end for exception
            } // end for query not null
            else {
                searchForm = beginForm + Utils.convertTextToHTML(queryString) + middleForm + "0" + endForm + helpTable;
                out.println(searchForm);
                error = true;
            }
        } // end for no error
        else {
            out.println(" ERROR DETECTED...<br><br>");
            out.println(errBuf.toString());
        }
        out.flush();
        if (error == false && searcher != null) {
            // do the header
            thispage = maxpage;
            hits = searcher.search(query);
            if (hits.length() == 0) {
                out.println("<p>Sorry no results.</p>");
            } // length is zero
            else {
                int lasNum = thispage + startindex;
                if (lasNum > hits.length())
                    lasNum = hits.length();
                out.println("<p align=\"left\"><b>");
                out.println(hits.length());
                out.println("</b> total matches. Showing ");
                out.println((startindex + 1) + "");
                out.println(" to ");
                out.println("" + lasNum);
                out.println(". &nbsp; &nbsp; &nbsp; ");
            }
        } // no err

        if (error == false && searcher != null) {
            resultsNumFound = hits.length();
            if ((startindex + maxpage) < hits.length()) {
                String moreurl = "";
                String linkToSearch = "<a href=\"search?query=" + Utils.convertTextToHTML(queryString) + "&maxresults=" + maxpage + "&startat=" + (startindex + maxpage) + "\">Next</a>";
                String prevLinkToSearch = "<a href=\"search?query=" + Utils.convertTextToHTML(queryString) + "&maxresults=" + maxpage + "&startat=" + (startindex + (maxpage * -1)) + "\">Previous</a>";
                if (startindex > 1)
                    moreurl = "<p align=\"left\"> " + prevLinkToSearch + " &nbsp; &nbsp; &nbsp; &nbsp; " + linkToSearch + " </p>";
                else
                    moreurl = "<p align=\"left\">  " + linkToSearch + " </p>";
                out.println(moreurl);
            }

            out.println("<p align=\"left\">");
            if ((startindex + maxpage) > hits.length()) {
                thispage = hits.length() - startindex;
            }
            for (int i = startindex; i < (thispage + startindex); i++) { // for
                                                                            // each
                                                                            // element
                if (i == 0) {
                    highestScore = hits.score(i);
                }
                Document doc = hits.doc(i);
                String title = doc.get(Index.FIELD_TITLE);
                if (title.trim().equals("")) {
                    title = "Untitled";
                }

                // path
                String url = null;
                String path = doc.get(Index.FIELD_PATH);
                if (path == null || path.equals("")) {
                	url = doc.get(Index.FIELD_URL);
                }
                else {
                	url = "file:///" + path;
                }
                String type = doc.get(Index.FIELD_TYPE);
                String sum = doc.get(Index.FIELD_SUMMARY);
                sum = Utils.convertTextToHTML(sum);
                String author = doc.get(Index.FIELD_AUTHOR);
                if ("".equals(author)) {
                    author = "Unknown Author";
                }
                else {
                    author = formatAuthor(author);
                }
                String size = doc.get(Index.FIELD_SIZE);
                //
                String curDate = doc.get(Index.FIELD_MODDATE);
                if ("".equals(curDate)) {
                    curDate = "Unknown Date";
                }
                else {
                    curDate = DateTimeUtils.getDateParsedFromIndex(curDate);
                }
                out.print("<a href=\"");
                out.print(url);
                out.print("\">");
                out.print(getImageString(type));
                out.print(title);
                out.println("</a><br><small>");
                out.println(sum);
                // now show date, author, and size
                out.print("</small><br><font color=\"green\"><em>");
                out.print(curDate);
                out.print(", ");
                out.print(getKStyle(size));
                out.print("K bytes, ");
                out.print(author);
                out.print(", <b>");
                out.print(getPercentFromFloat(hits.score(i)));
                out.print("</b>, <br></font></em><font color=\"gray\"><small>");
                out.print(url);
                out.println("</font></small><br><br>");
                out.flush();
            } // end for iterating over the page
            // WRITE CLOSE TO LIST
            out.println("</p>");
            out.flush();
            if ((startindex + maxpage) < hits.length()) {
                String moreurl = "";
                String linkToSearch = "<a href=\"search?query=" + Utils.convertTextToHTML(queryString) + "&maxresults=" + maxpage + "&startat=" + (startindex + maxpage) + "\">Next</a>";
                String prevLinkToSearch = "<a href=\"search?query=" + Utils.convertTextToHTML(queryString) + "&maxresults=" + maxpage + "&startat=" + (startindex + (maxpage * -1)) + "\">Previous</a>";
                if (startindex > 1)
                    moreurl = "<p align=\"left\"> " + prevLinkToSearch + " &nbsp; &nbsp; &nbsp; &nbsp; " + linkToSearch + " </p>";
                else
                    moreurl = "<p align=\"left\">  " + linkToSearch + " </p>";
                out.println(moreurl);
            }
            log("QUERY WAS: " + queryString);
        } // end if no err
        out.println("</body></html>");
        out.flush();
        if (logQuery) {
            lastSearch = queryString;
            logSearch(queryString, remoteUser, logPrefix, resultsNumFound, highestScore);
        }
    } // end for do get


    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.setContentType("Text/HTML");
        PrintWriter out = res.getWriter();
        out.println("<p>POST NOT ALLOWED</p>");
        out.flush();
    } // end for doPost


    /**
     * Gets the image of type.
     *
     * @param typeStr  Type
     * @return         Image String
     */
    private String getImageString(final String typeStr) {
        FileType type = FileType.fromValue(typeStr);

        switch (type) {
            case HTML: return htmlTag;
            case MS_WORD: return wordTag;
            case MS_EXCEL: return excelTag;
            case PDF: return pdfTag;
            case RTF: return rtfTag;
            case OO_WRITER: return ooWriterTag;
            case OO_IMPRESS: return ooImpressTag;
            case OO_CALC: return ooCalcTag;
            case OO_DRAW: return ooDrawTag;
            case OPENDOCUMENT_TEXT: return openDocumentTextTag;
            case TEXT: return textTag;
            default:
            	logger.error("getImageString() FileType." + type + " is not ok here!");
            	return textTag;
        }
    }


    private String formatAuthor(String emailStr) {
        String returnStr = emailStr;
        if ((emailStr.indexOf(".") != -1) && ((emailStr.indexOf("@") != -1) || (emailStr.indexOf("#") != -1))) {
            returnStr = Utils.replaceAll("#", returnStr, "@");
            returnStr = "<a href=\"mailto:" + returnStr + "\">" + returnStr + "</a>";
        }
        return returnStr;
    } // end for formatAuthor


    private String getPercentFromFloat(double aDouble) {
        String returnString = "";
        double newDouble = 100.0 * aDouble;
        String tempString = "" + newDouble;
        int tempLen = tempString.length();
        StringBuffer newSb = new StringBuffer();
        char curChar = ' ';
        int pastDot = 0;
        boolean foundDot = false;
        for (int i = 0; i < tempLen; i++) {
            curChar = tempString.charAt(i);
            if (curChar == '.')
                foundDot = true;
            else if (foundDot)
                pastDot++;
            newSb.append(curChar);
            if (pastDot >= 1) {
                newSb.append(" %");
                break;
            }
        }
        returnString = newSb.toString();
        return returnString;
    }


    private String getKStyle(String toGet) {
        String returnString = "";
        try {
            double l = Double.parseDouble(toGet);
            double newD = l / 1024.0;
            returnString = getChangeFromFloat(newD) + " ";
        } catch (Exception eN) {
            returnString = toGet;
        } // end for eN
        return returnString;
    } // end for getKStyle


    private String getChangeFromFloat(double aDouble) {
        String returnString = "";
        String tempString = "" + aDouble;
        int tempLen = tempString.length();
        StringBuffer newSb = new StringBuffer();
        char curChar = ' ';
        int pastDot = 0;
        boolean foundDot = false;
        for (int i = 0; i < tempLen; i++) {
            curChar = tempString.charAt(i);
            if (curChar == '.')
                foundDot = true;
            else if (foundDot)
                pastDot++;
            newSb.append(curChar);
            if (pastDot >= 1) {
                break;
            }
        }
        returnString = newSb.toString();
        return returnString;
    }


    @Override
    public void log(String toWrite) {
        System.out.println("SEARCH FORM:" + toWrite);
    } // end for log


    private void showMessage(String title, String desc) {
        System.out.println(title + "\n" + desc);
    }


    public void loadIndexes() {
        synchronized (this) {
            // check for indexFile
            int numIndexes = 0;
            File testIndex = new File(indexFile);
            StringBuffer errS = new StringBuffer();
            boolean loadErr = false;
            DocSearcherIndex curI;
            File tempFile;
            String tempFileString = "";
            String tempIndexerPath = "";
            String tempDesc = "";
            int tempDepth = 0;
            int updatePolicy = 0;
            String updateStr = "";
            int tempSbd = 0;
            boolean tempBool = false;
            boolean tempWebBool = false;
            String tempIsWeb = "";
            String tempReplace = "";
            String tempMatch = "";
            String tempDateStr = "";
            if (testIndex.exists()) {
                Table tempTable = new Table(12, 100);
                tempTable.htmlLoad(indexFile, "");
                int numI = tempTable.colSize();
                // parse it
                for (int i = 0; i < numI; i++) {
                    //
                    try {
                        tempDesc = tempTable.inItem(0, i);
                        tempFileString = tempTable.inItem(1, i);
                        tempSbd = Integer.parseInt(tempTable.inItem(2, i));
                        tempIndexerPath = tempTable.inItem(4, i);
                        // isWeb content
                        tempIsWeb = tempTable.inItem(5, i);
                        tempWebBool = false;
                        tempReplace = "";
                        tempMatch = "";
                        tempDateStr = "";
                        if (tempIsWeb != null) {
                            if (tempIsWeb.equals("true")) {
                                tempWebBool = true;
                                tempMatch = tempTable.inItem(6, i);
                                tempReplace = tempTable.inItem(7, i);
                            }
                        }
                        tempDateStr = tempTable.inItem(8, i);
                        updateStr = tempTable.inItem(9, i);
                        if (updateStr == null)
                            updatePolicy = 0;
                        else
                            updatePolicy = Integer.parseInt(updateStr);
                        if (tempDateStr == null) {
                            tempDateStr = DateTimeUtils.getToday();
                        }
                        if (tempSbd == 1) {
                            tempBool = false;
                        }
                        else {
                            tempBool = true;
                        }
                        tempDepth = Integer.parseInt(tempTable.inItem(3, i));
                        tempFile = new File(tempFileString);
                        if ((tempFile.exists()) || (tempFileString.startsWith("http"))) {
                            curI = new DocSearcherIndex(tempFileString, tempDesc, tempBool, tempDepth, tempIndexerPath, tempWebBool, tempMatch, tempReplace, tempDateStr, updatePolicy);
                            indexes.add(curI);
                            setStatus("Index " + curI.desc + " is " + curI.getDaysOld() + " days old (" + curI.lastIndexed + ")");
                            numIndexes++;
                        } else {
                            loadErr = true;
                            errS.append("Folder : " + tempFileString + "\n\tNo longer exists.\n\n");
                        } // end for file doesn't exist
                    } catch (Exception eN) {
                        loadErr = true;
                        errS.append(eN.toString() + "\n\n");
                    }
                } // end for parsing
            } // end for proceeding to load
            if (numIndexes == 0) {
                loadErr = true;
                errS.append("No indexes have been created yet.");
            }
            if (loadErr)
                showMessage("Error loading indexes", errS.toString());
            else
                setStatus("Found " + numIndexes + " total indexes.");
        } // end of synchronization
    } // end for loadIndexes


    // FIXME Is this the same class from org.jab.docsearch.DocSearcherIndex?
    public class DocSearcherIndex {

        boolean shouldBeSearched = false;
        String desc = "";
        String path = "";
        String indexerPath = "";
        int depth = 0;
        String lastIndexed = "";
        boolean isWeb = false;
        String match = "";
        String replace = "";
        int indexPolicy = 0;


        DocSearcherIndex(String path, String desc, boolean shouldBeSearched, int depth, String indexerPath, boolean isWeb, String match, String replace, String lastIndexed, int indexPolicy) {
            this.indexPolicy = indexPolicy;
            this.isWeb = isWeb;
            this.match = match;
            this.replace = replace;
            this.shouldBeSearched = shouldBeSearched;
            this.path = path;
            this.depth = depth;
            this.desc = desc;
            this.indexerPath = indexerPath;
            this.lastIndexed = lastIndexed;
        }


        DocSearcherIndex(String path, String desc, boolean shouldBeSearched, int depth, String indexerPath, boolean isWeb, String match, String replace, int indexPolicy) {
            this.indexPolicy = indexPolicy;
            this.isWeb = isWeb;
            if (!isWeb) {
                this.match = "na";
                this.replace = "na";
            } else {
                this.match = match;
                this.replace = replace;
            }
            this.shouldBeSearched = shouldBeSearched;
            this.path = path;
            this.depth = depth;
            this.desc = desc;
            this.indexerPath = indexerPath;

            // calendar info
            this.lastIndexed = DateTimeUtils.getToday();
        }


        private int getDaysOld() {
            int returnInt = 0;
            // long tempLong;
            if (!lastIndexed.equals("")) {
                try {
                    long indexTime;
                    long nowTime;
                    java.util.Date today = new java.util.Date(DateTimeUtils.getToday());
                    java.util.Date then = new java.util.Date(lastIndexed);
                    nowTime = today.getTime();
                    indexTime = then.getTime();
                    // perform math to compute the actual number of days
                    if (nowTime > indexTime) {
                        indexTime = indexTime / (1000 * 60 * 60 * 24);
                        nowTime = nowTime / (1000 * 60 * 60 * 24);
                        returnInt = (int) (nowTime - indexTime);
                    }
                } catch (Exception eD) {
                    System.out.println("Error obtaining days old for index " + desc + "\n" + eD.toString());
                }
            }
            return returnInt;
        }
    } // end for docSearcherIndex class


    /**
     * Combine folder and folder
     *
     * @param startFolder
     * @param addedFolder
     * @return             combined folder string
     */
    private String addFolder(final String startFolder, final String addedFolder) {
    	// combine
        String returnFolder = startFolder + pathSep + addedFolder;
        // replace // with /
        returnFolder = Utils.replaceAll(pathSep + pathSep, returnFolder, pathSep);
        return returnFolder;
    }


    private void setSearchers(ArrayList<DocSearcherIndex> curList) throws IOException {
        int numI = curList.size();
        // determine the size by finding which are to be searched
        ArrayList<DocSearcherIndex> searchList = new ArrayList<DocSearcherIndex>();
        if (numI > 0) {
            DocSearcherIndex curI;
            for (int i = 0; i < numIndexes; i++) {
                curI = indexes.get(i);
                if (curI.shouldBeSearched) {
                    searchList.add(curI);
                }
            }
            // now set up our searchers
            int numToSearch = searchList.size();
            if (numToSearch > 0) {
                searchers = new IndexSearcher[numToSearch];
                for (int i = 0; i < numToSearch; i++) {
                    curI = searchList.get(i);
                    searchers[i] = new IndexSearcher(IndexReader.open(curI.indexerPath));
                }
                searcher = new MultiSearcher(searchers);
            } // numtosearch greater than zero
            else
                log("NO INDEXES FOUND");
        } else
            log("NO INDEXES FOUND");
    } // end for setSearchers


    private void setStatus(String m) {
        log(m);
    }


    private void loadSearchers() throws IOException {
        loadIndexes();
        numIndexes = indexes.size();
        if (numIndexes > 0)
            setSearchers(indexes);
        else
            log("NO INDEXES!");
    } // end for loadSearchers


    private void logSearch(String queryText, String remoteUser, String prefix, int resultsNumFound, float highestScore) {
        if (!queryText.trim().equals("")) {
            String toLog = "[" + DateTimeUtils.getToday() + " " + DateTimeUtils.getTime() + "] " + "[" + remoteUser + "] [" + resultsNumFound + "] [" + highestScore + "] # " + queryText;
            String thisWeeksLog = DateTimeUtils.getThisMonth() + "_search_log.txt";
            String logFile = addFolder(logDir, thisWeeksLog);
            logThread lt = new logThread();
            lt.logText = toLog;
            lt.logFile = logFile;
            lt.prefix = prefix;
            lt.start();
        }
    } // end for logSearch


    private void addToLogFile(String toLog, String logFile, String prefix) throws IOException {
        synchronized (this) {
            // tempTextFile
            File tempFile = new File(tempTextFile);
            FileWriter filewriter = new FileWriter(tempFile);
            PrintWriter tpw = new PrintWriter(filewriter);
            tpw.println(toLog); // write our line
            // realtext log
            if (prefix.length() < 20)
                toLog = prefix + toLog;
            File oldLogFile = new File(logFile);
            if (oldLogFile.exists()) {
                StringBuffer writeBuf = new StringBuffer();
                String writeString = "";
                String c = "";
                FileReader filereader = new FileReader(oldLogFile);
                int i;
                while ((i = filereader.read()) != -1) {
                    if (i == -1) {
                        writeString = writeBuf.toString();
                        if (writeString.length() > 0)
                            tpw.println();
                        break;
                    } else {
                        c = "" + (char) i;
                        if ((c.equals("\n")) || (c.equals("\r"))) {
                            writeString = writeBuf.toString();
                            if (writeString.length() > 0)
                                tpw.println(writeString);
                            writeBuf = new StringBuffer();
                        } else
                            writeBuf.append(c);
                    }
                } // end for reading
                filereader.close();
                oldLogFile.delete();
            } // end for file exists
            filewriter.close();
            tpw.close();
            if (tempFile.renameTo(oldLogFile))
                System.out.println("Logged " + toLog);
            else
                System.out.println("UNABLE TO LOG " + toLog + " in file " + logFile);
        } // end of synch
    } // end for adding to log file

    private class logThread implements Runnable {
        Thread logT;
        String logText = "";
        String logFile = "";
        String prefix = "";


        public void start() {
            if (logT == null) {
                logT = new Thread(this, "logT");
                logT.start();
            } // end if null

        } // end for start


        public void stop() {
            logT.interrupt();
            logT = null;
        } // end for stop


        @Override
		public void run() {
            for (Thread thread = Thread.currentThread(); logT == thread;)
                try {
                    // we run validation in a thread so as not to interfere with
                    // repaints of GUI
                    addToLogFile(logText, logFile, prefix);
                } catch (Exception eF) {
                    System.out.println("logging thread was stopped!\n" + eF.toString());
                    eF.printStackTrace();
                } finally {
                    stop();
                    if (logT != null)
                        logT.destroy();
                }
        } // end for run
    } // end for logthread class


    private void setReloader() {
        reloader = new DsReloader(60, this); // reloader set for every hour
    } // end for setReloader
}
