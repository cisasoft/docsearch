package org.jab.docsearch.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class at Excel
 *
 * @author henschel
 * @version $Id: ExcelTest.java 146 2009-11-15 18:49:21Z henschel $
 */
public class ExcelTest {

    private Excel converter;


    @BeforeClass
    public static void setUpClass() {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.WARN);
    }


    @Test
    public void testParseWithProblem() {

        // null
        converter = new Excel(null);
        try {
            converter.parse();
            fail("fail, because ConverterException expected");
        }
        catch (ConverterException ce) {
            // ok
        }


        // unkown file
        converter = new Excel("foo.xls");
        try {
            converter.parse();
            fail("fail, because ConverterException expected");
        }
        catch (ConverterException ce) {
            // ok
        }
    }


    @Test
    public void testParseExcel5_oo3() {

        // file lookup in classpath
        String file = getClass().getResource("/excel-5-oo3.xls").getFile();

        // parse
        converter = new Excel(file);
        try {
            converter.parse();
            fail("ConverterException expected");
        }
        catch (ConverterException ce) {
            // ok
        }
    }


    @Test
    public void testParseExcel8_oo3()
            throws ConverterException {

        // file lookup in classpath
        String file = getClass().getResource("/excel-8-oo3.xls").getFile();

        // parse
        converter = new Excel(file);
        converter.parse();

        // check content
        assertEquals("Title", "Testdocument", converter.getTitle());
        assertEquals("Author", "Torsten Henschel", converter.getAuthor());
        assertEquals("Keywords", "JUnit", converter.getKeywords());
        assertEquals("Summary", "This is test content.\n", converter.getSummary());
        assertEquals("Text", "This is test content.\n", converter.getText());
    }


    @Test
    public void testParseExcel8_o2007()
            throws ConverterException {

        // file lookup in classpath
        String file = getClass().getResource("/excel-8-o2007.xls").getFile();

        // parse
        converter = new Excel(file);
        converter.parse();

        // check content
        assertEquals("Title", "Testdocument", converter.getTitle());
        assertEquals("Author", "Tine", converter.getAuthor());
        assertEquals("Keywords", "JUnit", converter.getKeywords());
        assertEquals("Summary", "This is test content.\n", converter.getSummary());
        assertEquals("Text", "This is test content.\n", converter.getText());
    }


    @Test
    public void testParseExcel8_o2007_pwd() {

        // file lookup in classpath
        String file = getClass().getResource("/excel-8-o2007-pwd.xls").getFile();

        // parse
        converter = new Excel(file);
        try {
            converter.parse();
            fail("fail, because ConverterException expected");
        }
        catch (ConverterException ce) {
            // ok
        }
    }


    @Test
    public void testParseExcelOOXML_o2007() {

        // file lookup in classpath
        String file = getClass().getResource("/excel-ooxml-o2007.xlsx").getFile();

        // parse
        converter = new Excel(file);
        try {
            converter.parse();
            fail("fail, because ConverterException expected");
        }
        catch (ConverterException ce) {
            // ok
        }
    }
}
