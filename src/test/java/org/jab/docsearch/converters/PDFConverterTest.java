package org.jab.docsearch.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.jab.docsearch.utils.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class at PDFConverter
 *
 * @author henschel
 * @version $Id: PDFConverterTest.java 140 2009-09-22 19:19:56Z henschel $
 */
public class PDFConverterTest {

    private PDFConverter converter;


    @BeforeClass
    public static void setUpClass() {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.WARN);
    }


    @Test
    public void testParseWithProblem() {

        // filename null
        converter = new PDFConverter(null);
        try {
            converter.parse();
            fail("fail, because ConverterException expected");
        }
        catch (ConverterException ce) {
            // ok
        }

        // unkown file
        converter = new PDFConverter("foo.pdf");
        try {
            converter.parse();
            fail("fail, because ConverterException expected");
        }
        catch (ConverterException ce) {
            // ok
        }
    }


    @Test
    public void testParsePDF_1_4()
            throws ConverterException {

        // file lookup in classpath
        String file = getClass().getResource("/pdf-1.4.pdf").getFile();

        // parse
        converter = new PDFConverter(file);
        converter.parse();

        // check content
        assertEquals("Title", "Testdocument", converter.getTitle());
        assertEquals("Author", "Torsten Henschel", converter.getAuthor());
        assertEquals("Keywords", "JUnit", converter.getKeywords());
        if ("/".equals(FileUtils.PATH_SEPARATOR)) {
        	assertEquals("Summary", "This is test content.\n", converter.getSummary());
        	assertEquals("Text", "This is test content.\n", converter.getText());
        }
        else {
        	assertEquals("Summary", "This is test content.\r\n", converter.getSummary());
        	assertEquals("Text", "This is test content.\r\n", converter.getText());
        }
    }
}
