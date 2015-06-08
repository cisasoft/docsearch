package org.jab.docsearch.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test class at Word
 *
 * @author henschel
 * @version $Id: WordTest.java 146 2009-11-15 18:49:21Z henschel $
 */
public class WordTest {

    private Word converter;


    @BeforeClass
    public static void setUpClass() {
        BasicConfigurator.resetConfiguration();
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.WARN);
    }


    @Test
    public void testParseWithProblem() {

        // null
        converter = new Word(null);
        try {
            converter.parse();
            fail("fail, because ConverterException expected");
        }
        catch (ConverterException ce) {
            // ok
        }


        // unkown file
        converter = new Word("foo.doc");
        try {
            converter.parse();
            fail("fail, because ConverterException expected");
        }
        catch (ConverterException ce) {
            // ok
        }
    }


    @Test
    public void testParseWord6_oo3() {

        // file lookup in classpath
        String file = getClass().getResource("/word-6-oo3.doc").getFile();

        // parse
        converter = new Word(file);
        try {
            converter.parse();
            fail("ConverterException expected");
        }
        catch (ConverterException ce) {
            // ok
        }
    }


    @Test
    public void testParseWord8_oo3()
            throws ConverterException {

        // file lookup in classpath
        String file = getClass().getResource("/word-8-oo3.doc").getFile();

        // parse
        converter = new Word(file);
        converter.parse();

        // check content
        assertEquals("Title", "Testdocument", converter.getTitle());
        assertEquals("Author", "Torsten Henschel", converter.getAuthor());
        assertEquals("Keywords", "JUnit", converter.getKeywords());
        assertEquals("Summary", "This is test content.\r\n", converter.getSummary());
        assertEquals("Text", "This is test content.\r\n", converter.getText());
    }


    @Test
    public void testParseWord8_o2007()
            throws ConverterException {

        // file lookup in classpath
        String file = getClass().getResource("/word-8-o2007.doc").getFile();

        // parse
        converter = new Word(file);
        converter.parse();

        // check content
        assertEquals("Title", "Testdocument", converter.getTitle());
        assertEquals("Author", "Torsten Henschel", converter.getAuthor());
        assertEquals("Keywords", "JUnit", converter.getKeywords());
        assertEquals("Summary", "This is test content.\r\n", converter.getSummary());
        assertEquals("Text", "This is test content.\r\n", converter.getText());
    }


    @Test
    public void testParseWord8_o2007_pwd() {

        // file lookup in classpath
        String file = getClass().getResource("/word-8-o2007-pwd.doc").getFile();

        // parse
        converter = new Word(file);
        try {
            converter.parse();
            fail("fail, because ConverterException expected");
        }
        catch (ConverterException ce) {
            // ok
        }
    }


    @Test
    public void testParseWordOOXML_o2007() {

        // file lookup in classpath
        String file = getClass().getResource("/word-ooxml-o2007.docx").getFile();

        // parse
        converter = new Word(file);
        try {
            converter.parse();
            fail("fail, because ConverterException expected");
        }
        catch (ConverterException ce) {
            // ok
        }
    }
}
