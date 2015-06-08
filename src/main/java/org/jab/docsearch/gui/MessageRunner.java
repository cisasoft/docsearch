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
package org.jab.docsearch.gui;

import org.jab.docsearch.DocSearch;

/**
 * Class MessageRunner
 *
 * @version $Id: MessageRunner.java 172 2012-09-14 15:24:32Z henschel $
 */
public final class MessageRunner implements Runnable {
    private final String title;
    private final String details;
    private final DocSearch docS;


    /**
     * Constructor
     *
     * @param title    Message title
     * @param details  Message text
     * @param docS     Docsearcher instance
     */
    public MessageRunner(String title, String details, DocSearch docS) {
        this.title = title;
        this.details = details;
        this.docS = docS;
    }


    /**
     * Overwrite Thread.run();
     */
    @Override
	public void run() {
        docS.showMessageDialog(title, details);
    }
}
