package org.jab.docsearch.spider;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test class at SpiderUrl
 *
 * @author henschel
 * @version $Id: SpiderUrlTest.java 121 2009-05-12 20:58:13Z henschel $
 */
public class SpiderUrlTest {

    @Test
    public void testConstructor() {
        SpiderUrl su = new SpiderUrl("url|12345|67890|type|md5");
        assertEquals("url", su.getUrl());
        assertEquals(12345, su.getLastModified());
        assertEquals(67890, su.getSize());
        assertEquals("type", su.getContentType());
        assertEquals("md5", su.getMd5());
    }
}
