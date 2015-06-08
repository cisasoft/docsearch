package org.jab.docsearch.spider;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test class at LinkValue
 *
 * @author henschel
 * @version $Id: LinkValueTest.java 162 2011-01-06 17:40:38Z henschel $
 */
public class LinkValueTest {

    @Test
    public void testGetDownloadFileName() {
        LinkValue lv = null;

        // null
        //lv = new LinkValue(null, null);
        //assertEquals("", lv.getRealLink());

        // empty
        lv = new LinkValue("", "");
        assertEquals("/", lv.getRealLink());

        // full link
        lv = new LinkValue("http://domain/file1", "http://domain/");
        assertEquals("http://domain/", lv.getRealLink());

        // file as link
        lv = new LinkValue("http://domain/file1.html", "file2");
        assertEquals("http://domain/file2", lv.getRealLink());

        // file as link and start with /
        lv = new LinkValue("http://domain/file1", "/file2");
        assertEquals("http://domain/file2", lv.getRealLink());

        // file as link and start with ./
        lv = new LinkValue("http://domain/file1", "./file2");
        assertEquals("http://domain/file2", lv.getRealLink());

        // file as link and start with ../
        lv = new LinkValue("http://domain/", "../file2");
        // is not realy ok
        assertEquals("http://domain/file2", lv.getRealLink());

        // file as link and start with ../
        lv = new LinkValue("http://domain/dir1/file1", "../file2");
        assertEquals("http://domain/file2", lv.getRealLink());

        // file as link and start with ../
        lv = new LinkValue("http://domain/dir1/dir2/file1", "../file2");
        assertEquals("http://domain/dir1/file2", lv.getRealLink());

        // file as link and start with ../
        lv = new LinkValue("http://domain/dir1/dir2/file1", "../../file2");
        assertEquals("http://domain/file2", lv.getRealLink());

        // file as link and start with ../
        lv = new LinkValue("http://domain/dir1/file1", "../dir2/file2");
        assertEquals("http://domain/dir2/file2", lv.getRealLink());

        // file as link and start with ../
        lv = new LinkValue("http://domain/dir1/dir2/file1", "../../dir3/dir4/file2");
        assertEquals("http://domain/dir3/dir4/file2", lv.getRealLink());
    }
}
