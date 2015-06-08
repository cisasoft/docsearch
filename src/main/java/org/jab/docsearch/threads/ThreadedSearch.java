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
package org.jab.docsearch.threads;

import org.jab.docsearch.DocSearch;

/**
 * Class ThreadedSearch
 *
 * @version $Id: ThreadedSearch.java 172 2012-09-14 15:24:32Z henschel $
 */
public final class ThreadedSearch implements Runnable {
    private final String actionString;
    private final DocSearch ds;


    /**
     * Constructor
     *
     * @param ds
     * @param actionString
     */
    public ThreadedSearch(DocSearch ds, String actionString) {
        this.actionString = actionString;
        this.ds = ds;
    }


    /**
     * Implement Runnable.run()
     */
    @Override
	public void run() {
        ds.doSearch(actionString);
    }
}
