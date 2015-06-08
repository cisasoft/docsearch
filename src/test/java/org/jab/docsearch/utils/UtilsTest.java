package org.jab.docsearch.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test class at Utils
 *
 * @author henschel
 * @version $Id: UtilsTest.java 121 2009-05-12 20:58:13Z henschel $
 */
public class UtilsTest {

    @Test
    public void testReplaceAll() {
        // once
        assertEquals("&amp;", Utils.replaceAll("&", "&", "&amp;"));
        assertEquals("&", Utils.replaceAll("&amp;", "&amp;", "&"));

        // begin
        assertEquals("&amp;--------", Utils.replaceAll("&", "&--------", "&amp;"));
        assertEquals("&--------", Utils.replaceAll("&amp;", "&amp;--------", "&"));

        // middle
        assertEquals("----&amp;----", Utils.replaceAll("&", "----&----", "&amp;"));
        assertEquals("----&----", Utils.replaceAll("&amp;", "----&amp;----", "&"));

        // end
        assertEquals("--------&amp;", Utils.replaceAll("&", "--------&", "&amp;"));
        assertEquals("--------&", Utils.replaceAll("&amp;", "--------&amp;", "&"));
    }


    @Test
    public void testConvertTextToHTML() {
        assertEquals(null, Utils.convertTextToHTML(null));
        assertEquals("&amp;&amp;", Utils.convertTextToHTML("&&"));
        assertEquals("&nbsp;", Utils.convertTextToHTML("\n"));
        assertEquals("&lt;", Utils.convertTextToHTML("<"));
        assertEquals("&gt;", Utils.convertTextToHTML(">"));
        assertEquals("&quot;", Utils.convertTextToHTML("\""));
    }


    @Test
    public void testGetBaseURLFolder() {
        assertEquals(null, Utils.getBaseURLFolder(null));
        assertEquals(" ", Utils.getBaseURLFolder(" "));
        assertEquals("Hello", Utils.getBaseURLFolder("Hello"));
        assertEquals("http://docsearcher.henschelsoft.de/", Utils.getBaseURLFolder("http://docsearcher.henschelsoft.de"));
        assertEquals("http://docsearcher.henschelsoft.de/", Utils.getBaseURLFolder("http://docsearcher.henschelsoft.de/"));
        assertEquals("http://docsearcher.henschelsoft.de/", Utils.getBaseURLFolder("http://docsearcher.henschelsoft.de/index.html"));
        assertEquals("http://docsearcher.henschelsoft.de/", Utils.getBaseURLFolder("http://docsearcher.henschelsoft.de/test"));
        assertEquals("http://docsearcher.henschelsoft.de/test/", Utils.getBaseURLFolder("http://docsearcher.henschelsoft.de/test/"));
        assertEquals("http://docsearcher.henschelsoft.de/test/", Utils.getBaseURLFolder("http://docsearcher.henschelsoft.de/test/index.html"));
    }


    @Test
    public void testGetDomainURL() {
        assertEquals("", Utils.getDomainURL(null));
        assertEquals("", Utils.getDomainURL(""));
        assertEquals("", Utils.getDomainURL("Hello"));
        assertEquals("http://docsearcher.henschelsoft.de/", Utils.getDomainURL("http://docsearcher.henschelsoft.de"));
        assertEquals("http://docsearcher.henschelsoft.de/", Utils.getDomainURL("http://docsearcher.henschelsoft.de/"));
        assertEquals("http://docsearcher.henschelsoft.de/", Utils.getDomainURL("http://docsearcher.henschelsoft.de/index.html"));
        assertEquals("http://docsearcher.henschelsoft.de/", Utils.getDomainURL("http://docsearcher.henschelsoft.de/test"));
        assertEquals("http://docsearcher.henschelsoft.de/", Utils.getDomainURL("http://docsearcher.henschelsoft.de/test/"));
        assertEquals("http://docsearcher.henschelsoft.de/", Utils.getDomainURL("http://docsearcher.henschelsoft.de/test/index.html"));
    }
}
