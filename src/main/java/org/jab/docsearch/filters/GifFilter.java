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

import javax.swing.filechooser.FileFilter;

import org.jab.docsearch.utils.FileUtils;
import org.jab.docsearch.utils.Messages;

/**
 * Class GifFilter
 *
 * @version $Id: GifFilter.java 93 2008-02-27 17:20:11Z henschel $
 */
public class GifFilter extends FileFilter {
    /**
     * Overwrite FileWriter.accept(File)
     */
    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }

        String extension = FileUtils.getFileExtension(file.getName());
        return "gif".equals(extension);
    }


    /**
     * Overwrite FileWriter.getDescription()
     */
    @Override
    public String getDescription() {
        return Messages.getString("DocSearch.gifs");
    }
}
