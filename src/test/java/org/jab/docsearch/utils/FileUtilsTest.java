package org.jab.docsearch.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test class at FileUtils
 *
 * @author henschel
 * @version $Id: FileUtilsTest.java 121 2009-05-12 20:58:13Z henschel $
 */
public class FileUtilsTest {

    @Test
    public void testGetFileExtension() {
        // null
        //assertEquals(null, fileUtils.getFileExtension(null));

        // empty
        assertEquals("unknown", FileUtils.getFileExtension(""));
        assertEquals("unknown", FileUtils.getFileExtension(" "));

        // without extension
        assertEquals("unknown", FileUtils.getFileExtension("filename"));
        assertEquals("unknown", FileUtils.getFileExtension("filename."));
        assertEquals("unknown", FileUtils.getFileExtension("filename.."));

        // with one extension
        assertEquals("extension", FileUtils.getFileExtension("filename.extension"));

        // with more than one extension
        assertEquals("extension2", FileUtils.getFileExtension("filename.extension1.extension2"));
    }
}
