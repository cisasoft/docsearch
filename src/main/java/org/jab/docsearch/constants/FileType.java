/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the
 * Free Software Foundation, Inc.,
 * 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.jab.docsearch.constants;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;

/**
 * This enum contains all filetypes.
 *
 * @version $Id: FileType.java 169 2012-09-06 18:48:15Z henschel $
 */
public enum FileType {

	// unknown
	UNKNOWN(null, null),

	// HTML
	HTML("ft_html.png", "html htm shtml shtm php asp jsp"),

	// text
	TEXT("ft_text.png", "txt"),

	// MS Word
	MS_WORD("ft_word.png", "doc"),

	// MS Excel
	MS_EXCEL("ft_excel.png", "xls"),

	// PDF
	PDF("ft_pdf.png", "pdf"),

	// RTF
	RTF("ft_rtf.png", "rtf"),

	// StarOffice/OpenOffice Writer
	OO_WRITER("ft_oowriter.png", "sxw"),

	// StarOffice/OpenOffice Impress
	OO_IMPRESS("ft_ooimpress.png", "sxi, sxp"),

	// StarOffice/OpenOffice Calc
	OO_CALC("ft_oocalc.png", "sxc"),

	// StarOffice/OpenOffice Draw
	OO_DRAW("ft_oodraw.png", "sxd"),

	// OpenDocument text
	OPENDOCUMENT_TEXT("ft_oo.png", "odt");


	/**
	 * HashMap with all known filetypes.
	 */
	private final static HashMap<String, FileType> FILETYPE_MAP = new HashMap<String, FileType>();

	static {
		// HTML
		FILETYPE_MAP.put("html", FileType.HTML);
		FILETYPE_MAP.put("htm", FileType.HTML);
		FILETYPE_MAP.put("shtml", FileType.HTML);
		FILETYPE_MAP.put("shtm", FileType.HTML);
		FILETYPE_MAP.put("php", FileType.HTML);
		FILETYPE_MAP.put("asp", FileType.HTML);
		FILETYPE_MAP.put("jsp", FileType.HTML);
		// text
		FILETYPE_MAP.put("txt", FileType.TEXT);
		// MS Word
		FILETYPE_MAP.put("doc", FileType.MS_WORD);
		// MS Excel
		FILETYPE_MAP.put("xls", FileType.MS_EXCEL);
		// PDF
		FILETYPE_MAP.put("pdf", FileType.PDF);
		// RTF
		FILETYPE_MAP.put("rtf", FileType.RTF);
		// StarOffice/OpenOffice Writer
		FILETYPE_MAP.put("sxw", FileType.OO_WRITER);
		// StarOffice/OpenOffice Impress
		FILETYPE_MAP.put("sxi", FileType.OO_IMPRESS);
		FILETYPE_MAP.put("sxp", FileType.OO_IMPRESS);
		// StarOffice/OpenOffice Calc
		FILETYPE_MAP.put("sxc", FileType.OO_CALC);
		// StarOffice/OpenOffice Draw
		FILETYPE_MAP.put("sxd", FileType.OO_DRAW);
		// OpenDocument text
		FILETYPE_MAP.put("odt", FileType.OPENDOCUMENT_TEXT);
	}


	/**
	 * Filetype icon
	 */
	private String icon;

	/**
	 * File suffixes
	 */
	private String suffixes;


	/**
	 * Konstructor
	 */
	private FileType(final String icon, final String suffixes) {
		this.icon = icon;
		this.suffixes = suffixes;
	}


	/**
	 * Gets the FileType Enum from filename
	 *
	 * @param fileTypeStr  Filetype string
	 * @return             FileType enum
	 */
	public static FileType fromValue(final String fileTypeStr) {
		FileType result = null;

		// null or empty?
		if (! StringUtils.isBlank(fileTypeStr)) {
			result = FILETYPE_MAP.get(fileTypeStr.toLowerCase());
		}

		// return
		return result != null ? result : FileType.UNKNOWN;
	}


	/**
	 * Gets the icon.
	 *
	 * @return  the icon string
	 */
	public String getIcon() {
		return icon;
	}


	/**
	 * Get the suffixes.
	 *
	 * @return the suffixes string
	 */
	public String getSuffixes() {
	    return suffixes;
	}
}
