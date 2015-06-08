package org.jab.docsearch.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class at OpenDocument
 *
 * @author henschel
 * @version $Id: OpenDocumentTest.java 146 2009-11-15 18:49:21Z henschel $
 */
public class OpenDocumentTest {

    private OpenDocument converter;


    @BeforeClass
    public static void setUpClass() {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.WARN);
    }


    @Test
    public void testParseWithProblem() {

        // null
        converter = new OpenDocument(null);
        try {
            converter.parse();
            fail("fail, because ConverterException expected");
        }
        catch (ConverterException ce) {
            // ok
        }


        // unkown file
        converter = new OpenDocument("foo.odt");
        try {
            converter.parse();
            fail("fail, because ConverterException expected");
        }
        catch (ConverterException ce) {
            // ok
        }
    }


    @Test
    public void testParse()
            throws ConverterException {

        // file lookup in classpath
        String file = getClass().getResource("/opendocument.odt").getFile();

        // parse
        converter = new OpenDocument(file);
        converter.parse();

        // check content
        assertEquals("Title", "Testdocument", converter.getTitle());
        assertEquals("Author", "Torsten Henschel", converter.getAuthor());
        assertEquals("Keywords", "JUnit", converter.getKeywords());
        assertEquals("Summary", "This is test content.", converter.getSummary());
        assertEquals("Text", "This is test content.", converter.getText());
    }
}
