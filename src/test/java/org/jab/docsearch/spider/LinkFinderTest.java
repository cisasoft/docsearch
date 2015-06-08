package org.jab.docsearch.spider;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.jab.docsearch.utils.FileUtils;
import org.jab.docsearch.utils.Utils;
import org.junit.Test;

/**
 * Test class at LinkFinder
 *
 * @author henschel
 * @version $Id: LinkFinderTest.java 121 2009-05-12 20:58:13Z henschel $
 */
public class LinkFinderTest {

    private final String USER_NAME = System.getProperty("user.name");

    @Test
    public void testGetDownloadFileName() {
        LinkFinder lf = new LinkFinder("in", "out", "outbad");

        String startString = "temp_spidered_document_" + USER_NAME;

        // null
        //assertEquals(startString + ".htm", lf.getDownloadFileName(null, null));

        // empty
        assertEquals(startString + ".htm", lf.getDownloadFileName("", ""));

        // end with /
        assertEquals(startString + ".htm", lf.getDownloadFileName("", "directory/"));

        // with extension
        assertEquals(startString + ".htm", lf.getDownloadFileName("", "filename.htm"));
        assertEquals(startString + ".odt", lf.getDownloadFileName("", "filename.odt"));
    }


    @Test
    public void testGetAllLinks1() {
        try {
            // parse file
            LinkFinder lf = new LinkFinder("http://docsearcher.henschelsoft.de/test/getalllinks1.html", "test-out.txt", "test-outbad.txt");
            lf.getAllLinks();

            // test result

            // good links
            ArrayList<SpiderUrl> spiderUrlList = Utils.getSpiderLinks("test-out.txt");
            assertEquals("Size", 2, spiderUrlList.size());
            assertEquals("link 1", "http://docsearcher.henschelsoft.de/test/getalllinks1a.html", spiderUrlList.get(0).getUrl());
            assertEquals("link 2", "http://docsearcher.henschelsoft.de/test/getalllinks1.html", spiderUrlList.get(1).getUrl());

            // bad links
            ArrayList<SpiderUrl> spiderUrlBadList = Utils.getSpiderLinks("test-outbad.txt");
            assertEquals("Size", 1, spiderUrlBadList.size());
            assertEquals("link 1", "http://docsearcher.henschelsoft.de/test/getalllinks1-bad.html", spiderUrlBadList.get(0).getUrl());
        }
        // cleanup
        finally {
            FileUtils.deleteFile("test-out.txt");
            FileUtils.deleteFile("test-outbad.txt");
        }
    }
}
