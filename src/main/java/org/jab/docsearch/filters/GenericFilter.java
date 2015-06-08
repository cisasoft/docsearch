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
package org.jab.docsearch.filters;

import java.io.File;
import java.io.FilenameFilter;

import org.jab.docsearch.constants.FileType;
import org.jab.docsearch.utils.FileUtils;

/**
 * Class GenericFilter
 *
 * @version $Id: GenericFilter.java 172 2012-09-14 15:24:32Z henschel $
 */
public class GenericFilter implements FilenameFilter {
    /**
     * Implment FilenameFilter.accept(File, String)
     */
    @Override
	public boolean accept(File directory, String filename) {
        File tmpFile = new File(directory, filename);
        if (tmpFile.isDirectory()) {
            return false;
        }

        // check filetype is known
        String extension = FileUtils.getFileExtension(filename);
        FileType fileType = FileType.fromValue(extension);

        return FileType.UNKNOWN != fileType;
    }
}
