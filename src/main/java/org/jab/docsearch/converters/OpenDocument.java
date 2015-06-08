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
package org.jab.docsearch.converters;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Layout;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Class for handling OpenDocument files
 *
 * OpenDocument Text    .odt
 *
 * @version $Id: OpenDocument.java 172 2012-09-14 15:24:32Z henschel $
 */
public class OpenDocument
        extends AbstractConverter
        implements ConverterInterface {

    private final static String CONTENT_FILE = "content.xml";
    private final static String META_FILE = "meta.xml";
    private final static String DC_CREATOR_TAG = "dc:creator";
    private final static String DC_TITLE_TAG = "dc:title";
    private final static String META_KEYWORD = "meta:keyword";
    private final static String OFFICE_BODY_TAG = "office:body";
    private final static String OFFICE_META_TAG = "office:meta";
    private final String filename;


    /**
     * Constructor
     *
     * @param filename
     */
    public OpenDocument(final String filename) {
        this.filename = filename;
    }


    /**
     * @see ConverterInterface#parse()
     */
    @Override
	public void parse()
            throws ConverterException {
        if (filename == null) {
            log.error("parse() filename is null");
            throw new ConverterException("OpenDocument::parse() filename is null");
        }

        // zip
        ZipFile zip = null;
        try {
            zip = new ZipFile(filename);

            // meta file
            {
                ZipEntry metaZipEntry = zip.getEntry(META_FILE);
                if (metaZipEntry != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("parse() found meta file (" + metaZipEntry.getName() + ")");
                    }

                    // read xml with SAX2
                    try {
                        // configure the sax parser factory
                        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                        saxParserFactory.setValidating(false);
                        saxParserFactory.setNamespaceAware(false);

                        // create sax parser
                        SAXParser saxParser = saxParserFactory.newSAXParser();
                        MetaSAXDefaultHandler saxHandler = new MetaSAXDefaultHandler();

                        // create xml reader
                        XMLReader xmlReader = saxParser.getXMLReader();
                        xmlReader.setContentHandler(saxHandler);
                        xmlReader.setErrorHandler(saxHandler);
                        xmlReader.setEntityResolver(saxHandler);

                        // set source
                        InputSource is = new InputSource(zip.getInputStream(metaZipEntry));

                        // action!
                        xmlReader.parse(is);

                        // get title
                        if (saxHandler.titleText.length() != 0) {
                            documentTitle = saxHandler.titleText.toString();
                        }

                        // get author
                        if (saxHandler.creatorText.length() != 0) {
                            documentAuthor = saxHandler.creatorText.toString();
                        }

                        // get keyword
                        if (saxHandler.keywordText.length() != 0) {
                            documentKeywords = saxHandler.keywordText.toString();
                        }
                    }
                    catch (ParserConfigurationException pce) {
                        log.error("parse() failed", pce);
                    }
                    catch (SAXException se) {
                        log.error("parse() failed", se);
                    }
                }
            }

            // content file
            {
                ZipEntry contentZipEntry = zip.getEntry(CONTENT_FILE);
                if (contentZipEntry != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("parse() found content file (" + contentZipEntry.getName() + ")");
                    }

                    // read xml with SAX2
                    try {
                        // configure the sax parser factory
                        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                        saxParserFactory.setValidating(false);
                        saxParserFactory.setNamespaceAware(false);

                        // create sax parser
                        SAXParser saxParser = saxParserFactory.newSAXParser();
                        ContentSAXDefaultHandler saxHandler = new ContentSAXDefaultHandler();

                        // create xml reader
                        XMLReader xmlReader = saxParser.getXMLReader();
                        xmlReader.setContentHandler(saxHandler);
                        xmlReader.setErrorHandler(saxHandler);
                        xmlReader.setEntityResolver(saxHandler);

                        // set source
                        InputSource is = new InputSource(zip.getInputStream(contentZipEntry));

                        // action!
                        xmlReader.parse(is);

                        // get text
                        if (saxHandler.text.length() != 0) {
                            documentText = saxHandler.text.toString();
                        }
                    }
                    catch (ParserConfigurationException pce) {
                        log.error("parse() failed", pce);
                    }
                    catch (SAXException se) {
                        log.error("parse() failed", se);
                    }
                }
            }
        }
        catch (IOException ioe) {
            log.fatal("parse() failed with IOException", ioe);
            throw new ConverterException("OpenDocument::parse() failed with IOException", ioe);
        }
        finally {
            try {
                if (zip != null) {
                    zip.close();
                }
            }
            catch (IOException ioe) {
                log.fatal("parse() can't close ZipFile", ioe);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("parse() OpenDocument file='" + filename + "'" + Layout.LINE_SEP +
                    "title='" + documentTitle + "'" + Layout.LINE_SEP +
                    "author='" + documentAuthor + "'" + Layout.LINE_SEP +
                    "keywords='" + documentKeywords + "'");
        }
    }


    /**
     * SAXDefaultHandler for meta.xml
     */
    private class MetaSAXDefaultHandler extends DefaultHandler {
        private boolean metaTag = false;
        private boolean titleTag = false;
        private boolean creatorTag = false;
        private boolean keywordTag = false;
        private final StringBuffer titleText = new StringBuffer();
        private final StringBuffer creatorText = new StringBuffer();
        private final StringBuffer keywordText = new StringBuffer();

        /**
         * @see DefaultHandler#startElement(String, String, String, Attributes)
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                 throws SAXException {

            // check for office:meta
            if (! metaTag) {
                if (OFFICE_META_TAG.equals(qName)) {
                    metaTag = true;
                    return;
                }
            }
            else {
                // check for dc:title
                if (! titleTag) {
                    if (DC_TITLE_TAG.equals(qName)) {
                        titleTag = true;
                        return;
                    }
                }

                // check for dc:creator
                if (! creatorTag) {
                    if (DC_CREATOR_TAG.equals(qName)) {
                        creatorTag = true;
                        return;
                    }
                }

                // check for meta:keyword
                if (! keywordTag) {
                    if (META_KEYWORD.equals(qName)) {
                        keywordTag = true;
                        return;
                    }
                }
            }
        }


        /**
         * @see DefaultHandler#endElement(String, String, String)
         */
        @Override
        public void endElement(String uri, String localName, String qName)
                 throws SAXException {

            if (metaTag) {
                // check for dc:title
                if (titleTag) {
                    if (DC_TITLE_TAG.equals(qName)) {
                        titleTag = false;
                        return;
                    }
                }

                // check for dc:creator
                if (creatorTag) {
                    if (DC_CREATOR_TAG.equals(qName)) {
                        creatorTag = false;
                        return;
                    }
                }

                // check for office:meta
                if (metaTag) {
                    if (OFFICE_META_TAG.equals(qName)) {
                        metaTag = false;
                        return;
                    }
                }

                // check for meta:keyword
                if (keywordTag) {
                    if (META_KEYWORD.equals(qName)) {
                        keywordTag = false;
                        return;
                    }
                }
            }
        }


        /**
         * @see DefaultHandler#characters(char[], int, int)
         */
        @Override
        public void characters(char[] ch, int start, int length)
                 throws SAXException {

            // title
            if (titleTag) {
                titleText.append(ch, start, length);
            }
            // creator
            else if (creatorTag) {
                creatorText.append(ch, start, length);
            }
            // keyword
            else if (keywordTag) {
                keywordText.append(ch, start, length);
            }
        }


        /**
         * @see DefaultHandler#warning(SAXParseException)
         */
        @Override
        public void warning(SAXParseException spe)
                 throws SAXException {
            log.warn("warning()", spe);
        }



        /**
         * @see DefaultHandler#error(SAXParseException)
         */
        @Override
        public void error(SAXParseException spe)
                 throws SAXException {
            throw spe;
        }



        /**
         * @see DefaultHandler#fatalError(SAXParseException)
         */
        @Override
        public void fatalError(SAXParseException spe)
                 throws SAXException {
            throw spe;
        }
    }


    /**
     * SAXDefaultHandler for content.xml
     */
    private class ContentSAXDefaultHandler extends DefaultHandler {
        private boolean bodyTag = false;
        private final StringBuffer text = new StringBuffer();

        /**
         * @see DefaultHandler#startElement(String, String, String, Attributes)
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                 throws SAXException {
            // check for office:body
            if (! bodyTag) {
                if (OFFICE_BODY_TAG.equals(qName)) {
                    bodyTag = true;
                    return;
                }
            }
        }


        /**
         * @see DefaultHandler#endElement(String, String, String)
         */
        @Override
        public void endElement(String uri, String localName, String qName)
                 throws SAXException {

            // check for office:body
            if (bodyTag) {
                if (OFFICE_BODY_TAG.equals(qName)) {
                    bodyTag = false;
                }
            }
        }


        /**
         * @see DefaultHandler#characters(char[], int, int)
         */
        @Override
        public void characters(char[] ch, int start, int length)
                 throws SAXException {
            if (bodyTag) {
                text.append(ch, start, length);
            }
        }


        /**
         * @see DefaultHandler#warning(SAXParseException)
         */
        @Override
        public void warning(SAXParseException spe)
                 throws SAXException {
            log.warn("warning()", spe);
        }



        /**
         * @see DefaultHandler#error(SAXParseException)
         */
        @Override
        public void error(SAXParseException spe)
                 throws SAXException {
            throw spe;
        }



        /**
         * @see DefaultHandler#fatalError(SAXParseException)
         */
        @Override
        public void fatalError(SAXParseException spe)
                 throws SAXException {
            throw spe;
        }
    }
}
