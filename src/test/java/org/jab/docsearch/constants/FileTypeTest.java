package org.jab.docsearch.constants;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test class at FileType
 *
 * @author henschel
 * @version $Id: FileTypeTest.java 164 2011-03-22 21:58:55Z henschel $
 */
public class FileTypeTest {

	/**
	 * Test fromValue method
	 */
	@Test
	public void testFromValue() {

		// unknown
		assertEquals(FileType.UNKNOWN, FileType.fromValue(null));
		assertEquals(FileType.UNKNOWN, FileType.fromValue(""));
		assertEquals(FileType.UNKNOWN, FileType.fromValue("fromValue"));

		// known
		assertEquals(FileType.HTML, FileType.fromValue("html"));
		assertEquals(FileType.HTML, FileType.fromValue("HTML"));
	}
}
