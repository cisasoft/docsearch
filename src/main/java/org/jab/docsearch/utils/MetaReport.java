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

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.jab.docsearch.DocSearch;
import org.jab.docsearch.DocSearcherIndex;
import org.jab.docsearch.Index;
import org.jab.docsearch.constants.FileType;
import org.jab.docsearch.gui.MetaDialog;

/**
 * Class MetaReport
 *
 * @version $Id: MetaReport.java 171 2012-09-07 20:08:01Z henschel $
 */
public class MetaReport {
    /**
     * Log4J logger
     */
    private final Logger logger = Logger.getLogger(getClass().getName());
    private DocSearch ds;
    private static final String dsNotMsgMeta = Messages.getString("DocSearch.hasNeededMetaData");
    private static final String dsMsgMeta = Messages.getString("DocSearch.missingMeta");
    private static final String dsSkip = Messages.getString("DocSearch.skipDoc");
    private static final String dsGood = Messages.getString("DocSearch.good");
    private static final String dsBad = Messages.getString("DocSearch.bad");
    private static final String dsMetaRpt = Messages.getString("DocSearch.metaDataRpt");
    private static final String dsPoorMeta = Messages.getString("DocSearch.poorMD");
    private static final String dsGoodMetaNum = Messages.getString("DocSearch.goodMetaNum");
    private static final String dsMetaOO = Messages.getString("DocSearch.metaOverall");
    private static final String dsTblDsc = Messages.getString("DocSearch.tableDesc");
    //private static final String dsCombFi = Messages.getString("DocSearch.combFi");
    private static final String[] allFields = {
        Messages.getString("DocSearch.File"),
        Messages.getString("DocSearch.titleText"),
        Messages.getString("DocSearch.author"),
        Messages.getString("DocSearch.date"),
        Messages.getString("DocSearch.dsSummary"),
        Messages.getString("DocSearch.keywordsText"),
        Messages.getString("DocSearch.size"),
        Messages.getString("DocSearch.type") };


    /**
     * Gets MetaReport
     *
     * @param ds
     */
    public void getMetaReport(DocSearch ds) {
        this.ds = ds;

        MetaDialog md = new MetaDialog(ds, Messages.getString("DocSearch.metaDataOptions"), true);
        md.init();
        md.setVisible(true);
        if (md.confirmed) {
            try {
                int maxNum = Integer.parseInt(md.getMaxDocs());
                int maxDaysOld = Integer.parseInt(md.getDateFieldText());

                // generate report
                doMetaDataReport(ds.getDSIndex(md.getSelectedIndexNum()),
                        md.getListAllIsSelected(),
                        md.getPathRequiredSelected(),
                        md.getPathFieldText(),
                        md.getAuthSelected(),
                        md.getAuthFieldText(),
                        md.getReportFieldText(),
                        maxNum,
                        md.getDateRequiredSelected(),
                        maxDaysOld);

                ds.setStatus(Messages.getString("DocSearch.statusReportComplete"));
            }
            catch (NumberFormatException nfe) {
                ds.setStatus(Messages.getString("DocSearch.statusReportError") + nfe.toString());
            }
        }
        else {
            ds.setStatus(Messages.getString("DocSearch.statusReportCancelled"));
        }
    }


    /**
     * doMetaDataReport
     *
     * @param di
     * @param listAll
     * @param pathRequired
     * @param pathText
     * @param authRequired
     * @param authText
     * @param reportFile
     * @param maxDocs
     * @param useDaysOld
     * @param maxDays
     */
    private void doMetaDataReport(DocSearcherIndex di, boolean listAll, boolean pathRequired, String pathText, boolean authRequired, String authText, String reportFile, int maxDocs, boolean useDaysOld, int maxDays) {
        try {
            // intialize our metrics
            int numBadDocs = 0;
            int totalDocs = 0;
            int numGoodDocs = 0;
            String lineSep = Utils.LINE_SEPARATOR;
            StringBuffer documentBuffer = new StringBuffer();
            StringBuffer metaDataReport = new StringBuffer();

            // initialize the reader
            IndexReader ir = IndexReader.open(di.getIndexPath());
            int numDocs = ir.maxDoc();
            ds.setStatus(numDocs + " " + Messages.getString("DocSearch.numDox") + " " + di.getName());

            // write the start of the table
            documentBuffer.append("<table style=\"empty-cells:show\" border=\"1\">").append(lineSep);
            documentBuffer.append("<tr>").append(lineSep);
            int numHdrs = allFields.length;
            for (int z = 0; z < numHdrs; z++) {
                documentBuffer.append("<th valign=\"top\">");
                documentBuffer.append(allFields[z]);
                documentBuffer.append("</th>").append(lineSep);
            }
            documentBuffer.append("</tr>").append(lineSep);
            for (int i = 0; i < numDocs; i++) {
                if (!ir.isDeleted(i)) {
                    Document doc = ir.document(i);
                    if (doc != null) {
                        boolean curSkip = false;

                        // put in the docs values
                        String path;
                        if (di.getIsWeb()) {
                            path = doc.get(Index.FIELD_URL);
                        }
                        else {
                            path = doc.get(Index.FIELD_PATH);
                        }

                        ds.setStatus("Examining document: " + path);
                        String type = doc.get(Index.FIELD_TYPE);
                        String author = doc.get(Index.FIELD_AUTHOR);
                        String summary = doc.get(Index.FIELD_SUMMARY);
                        String title = doc.get(Index.FIELD_TITLE);
                        String size = doc.get(Index.FIELD_SIZE);
                        String keywords = doc.get(Index.FIELD_KEYWORDS);
                        String date = DateTimeUtils.getDateParsedFromIndex(doc.get(Index.FIELD_MODDATE));

                        // determine if we even need to examine it
                        if (pathRequired) {
                            if (path.indexOf(pathText) == -1) {
                                curSkip = true;
                            }
                        }

                        if (authRequired) {
                            if (author.indexOf(authText) == -1) {
                                curSkip = true;
                            }
                        }

                        // determine if its bad of good
                        if (! curSkip) {
                            totalDocs++;
                            boolean isGood = goodMetaData(title, summary, author, date, keywords, type, useDaysOld, maxDays);

                            // write to our file
                            if (! isGood || listAll) {
                                documentBuffer.append("<tr>").append(lineSep);
                                documentBuffer.append("<td valign=\"top\">"); // path
                                documentBuffer.append(path);
                                documentBuffer.append("</td>").append(lineSep);
                                documentBuffer.append("<td valign=\"top\"><small>");
                                documentBuffer.append(Utils.convertTextToHTML(title));
                                documentBuffer.append("</small></td>").append(lineSep);
                                documentBuffer.append("<td valign=\"top\">");
                                documentBuffer.append(author);
                                documentBuffer.append("</td>").append(lineSep);
                                documentBuffer.append("<td valign=\"top\">");
                                documentBuffer.append(date);
                                documentBuffer.append("</td>").append(lineSep);
                                documentBuffer.append("<td valign=\"top\"><small>");
                                documentBuffer.append(Utils.convertTextToHTML(summary));
                                documentBuffer.append("</small></td>").append(lineSep);
                                documentBuffer.append("<td valign=\"top\"><small>");
                                documentBuffer.append(keywords);
                                documentBuffer.append("</small></td>").append(lineSep);
                                documentBuffer.append("<td valign=\"top\">");
                                documentBuffer.append(size);
                                documentBuffer.append("</td>").append(lineSep);
                                documentBuffer.append("<td valign=\"top\">");
                                documentBuffer.append(type);
                                documentBuffer.append("</td>").append(lineSep);
                                documentBuffer.append("</tr>").append(lineSep);
                            }

                            if (isGood) {
                                ds.setStatus(path + " " + dsNotMsgMeta);
                                numGoodDocs++;
                            }
                            else {
                                ds.setStatus(path + " " + dsMsgMeta);
                                numBadDocs++;
                            }
                        }
                        else {
                            ds.setStatus(dsSkip + " " + path);
                        }
                    }
                }

                if (i > maxDocs) {
                    break;
                }
            }
            documentBuffer.append("</table>").append(lineSep);

            int percentGood = 0;
            if (totalDocs > 0) {
                percentGood = (numGoodDocs * 100) / totalDocs;
            }

            ds.setStatus("%  " + dsGood + ": " + percentGood + " (" + numGoodDocs + " / " + totalDocs + ", " + numBadDocs + " " + dsBad + ").");

            // write complete report with summary
            metaDataReport.append("<html>").append(lineSep);
            metaDataReport.append("<head>").append(lineSep);
            metaDataReport.append("<title>").append(dsMetaRpt).append(' ').append(di.getName()).append("</title>").append(lineSep);
            metaDataReport.append("<meta name=\"description\" content=\"lists documents with poorly searchable meta data\">").append(lineSep);
            metaDataReport.append("<meta name=\"author\" content=\"DocSearcher\">").append(lineSep);
            metaDataReport.append("</head>").append(lineSep);
            metaDataReport.append("<body>").append(lineSep);
            metaDataReport.append("<h1>").append(dsMetaRpt).append(' ').append(di.getName()).append("</h1>").append(lineSep);
            metaDataReport.append("<p align=\"left\"><b>");
            metaDataReport.append(numBadDocs);
            metaDataReport.append("</b> ");
            metaDataReport.append(dsPoorMeta);
            metaDataReport.append(" <br> &amp; <b>");
            metaDataReport.append(numGoodDocs);
            metaDataReport.append("</b> ");
            metaDataReport.append(dsGoodMetaNum);
            metaDataReport.append(".</p>").append(lineSep);
            metaDataReport.append("<p align=\"left\">");
            metaDataReport.append(dsMetaOO);
            metaDataReport.append(" <b>");
            metaDataReport.append(percentGood + "</b> % . </p>");
            metaDataReport.append("<p align=\"left\">");
            metaDataReport.append(dsTblDsc);
            metaDataReport.append(".</p>").append(lineSep);

            // add document buffer
            metaDataReport.append(documentBuffer);

            metaDataReport.append("</body>").append(lineSep);
            metaDataReport.append("</html>").append(lineSep);

            ds.curPage = Messages.getString("DocSearch.report");

            boolean fileSaved = FileUtils.saveFile(reportFile, metaDataReport);
            if (fileSaved) {
                ds.doExternal(reportFile);
            }
        }
        catch (IOException ioe) {
            logger.fatal("doMetaDataReport() create meta data report failed", ioe);
            ds.setStatus(Messages.getString("DocSearch.statusMetaDataError") + di.getName() + ":" + ioe.toString());
        }
    }


    /**
     * Combine files
     *
     * @param firstFile
     * @param secondFile
     */
/*    private void combineFiles(String firstFile, String secondFile) {
        // TODO close streams in finally, better error messages
        synchronized (this) {
            ds.setStatus(plsWait + " -  " + dsCombFi + " (" + firstFile + " & " + secondFile + ")");
            try {
                File firstFi = new File(firstFile);
                FileInputStream firFi = new FileInputStream(firstFi);

                // temporary storage
                String tempFile = FileUtils.addFolder(fEnv.getWorkingDirectory(), "temp_copy.txt");
                File tempFi = new File(tempFile);
                FileOutputStream fo = new FileOutputStream(tempFi);

                //
                File secondFi = new File(secondFile);
                FileInputStream secFi = new FileInputStream(secondFi);

                // write the first file to the temp file
                int curI = 0;
                byte curBint;
                while (curI != -1) {
                    curI = firFi.read();
                    curBint = (byte) curI;
                    if (curI != -1) {
                        fo.write(curBint);
                    }
                    else {
                        break;
                    }
                }
                firFi.close();
                curI = 0;
                while (curI != -1) {
                    curI = secFi.read();
                    curBint = (byte) curI;
                    if (curI != -1) {
                        fo.write(curBint);
                    }
                    else {
                        break;
                    }
                }
                secFi.close();
                fo.close();
                copyFile(tempFile, firstFile);
            }
            // end for try
            catch (Exception eF) {
                // show the err
            }
        }
    }*/


    /**
     * Check if meta date are good
     *
     * @param title
     * @param description
     * @param author
     * @param date
     * @param keywords
     * @param type
     * @param useDaysOld
     * @param maxDays
     * @return
     */
    private boolean goodMetaData(final String title, final String description, final String author, final String date,
    		final String keywords, final String type, final boolean useDaysOld, final int maxDays) {
        boolean returnbool = true;

        if (useDaysOld) {
            if (DateTimeUtils.getDaysOld(date) > maxDays) {
                returnbool = false;
            }
        }

        FileType fileType = FileType.fromValue(type);
        String lowerTitle = title.toLowerCase();
        int titleLen = title.length();
        int authLen = author.length();
        int descLen = description.length();
        int keywdLen = keywords.length();

        switch (fileType) {
            case HTML: {
                // should have all meta data !
                if ((descLen < 3) || (authLen == 0) || (titleLen < 3) || (keywdLen < 3)) {
                    returnbool = false;
                }
                else if (lowerTitle.startsWith("new_page")) {
                    returnbool = false;
                }

                break;
            }
            case MS_WORD: {
                if ((authLen == 0) || (titleLen < 3)) {
                    returnbool = false;
                }

                break;
            }
            case MS_EXCEL: {
                if ((authLen == 0) || (titleLen < 3)) {
                    returnbool = false;
                }

                break;
            }
            case PDF: {
                if ((authLen == 0) || (titleLen < 3)) {
                    returnbool = false;
                }

                break;
            }
            case RTF: {
                // nothing to check here except date
                break;
            }
            case OO_WRITER: {
                if ((authLen == 0) || (titleLen < 3)) {
                    returnbool = false;
                }

                break;
            }
            case OO_IMPRESS: {
                if ((authLen == 0) || (titleLen < 3)) {
                    returnbool = false;
                }

                break;
            }
            case OO_CALC: {
                if ((authLen == 0) || (titleLen < 3)) {
                    returnbool = false;
                }

                break;
            }
            case OO_DRAW: {
                if ((authLen == 0) || (titleLen < 3)) {
                    returnbool = false;
                }

                break;
            }
            case OPENDOCUMENT_TEXT: {
                if ((authLen == 0) || (titleLen < 3)) {
                    returnbool = false;
                }

                break;
            }
            case TEXT: {
                // nothing to check here
            	break;
            }
            default: {
            	logger.error("doSearch() FileType." + fileType + " is not ok here!");
            }
        }

        return returnbool;
    }


    /**
     * TODO move this method to a FileHelper
     * TODO use new nio instead of handmade copy
     *
     * @param originalFileName
     * @param newFileName
     */
    /*private void copyFile(String originalFileName, String newFileName) {
        boolean ioSuccess = true;
        try {
            File origFile = new File(originalFileName);
            FileInputStream fi = new FileInputStream(origFile);
            File newFile = new File(newFileName);
            FileOutputStream fo = new FileOutputStream(newFile);
            int curI = 0; // reset i
            // byte rB;
            byte curBint;
            while (curI != -1) {
                curI = fi.read();
                if (curI != -1) {
                    curBint = (byte) curI;
                    fo.write(curBint);
                }
                else {
                    break;
                }
            }

            fo.close();
            fi.close();
        } catch (Exception e) {
            ioSuccess = false;
        }
        finally {
            if (! ioSuccess) {
                ds.setStatus(Messages.getString("DocSearch.errFilSave") + " " + newFileName);
            }
        }
    }*/
}
