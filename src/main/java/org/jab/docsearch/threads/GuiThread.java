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

import org.apache.log4j.Logger;
import org.jab.docsearch.DocSearch;

/**
 * This class moves GUI events off the dispatch thread for a more responsive
 * GUI.
 *
 * @version $Id: GuiThread.java 172 2012-09-14 15:24:32Z henschel $
 */
public final class GuiThread implements Runnable {
    /**
     * Log4J logger
     */
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final String actionString;
    private final DocSearch ds;
    private Thread gtt;


    /**
     * Constructor
     *
     * @param ds
     * @param actionString
     */
    public GuiThread(DocSearch ds, String actionString) {
        this.actionString = actionString;
        this.ds = ds;
    }


    /**
     * Start
     */
    public void start() {
        if (gtt == null) {
            gtt = new Thread(this, "gtt");
            gtt.start();
        }
    }


    /**
     * Stop
     */
    public void stop() {
        gtt.interrupt();
        gtt = null;
    }


    /**
     * Implement Runnable.run()
     */
    @Override
	public void run() {
        for (Thread thread = Thread.currentThread(); gtt == thread;) {
            try {
                ds.handleEventCommand(actionString);
            }
            catch (Exception e) {
                logger.fatal("run() Gui Thread error in action=" + actionString, e);
            }
            finally {
                stop();
                if (gtt != null) {
                    gtt.destroy();
                }
            }
        }
    }
}
