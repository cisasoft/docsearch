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

import java.util.Timer;
import java.util.TimerTask;

import org.jab.docsearch.servlet.DsResults;
import org.jab.docsearch.utils.DateTimeUtils;

/**
 * Simple demo that uses java.util.Timer to schedule a task to execute once 5
 * seconds have passed.
 *
 * @version $Id: DsReloader.java 93 2008-02-27 17:20:11Z henschel $
 */
public final class DsReloader {
    private DsResults ds;


    /**
     * Constructor
     *
     * @param minutes
     * @param ds
     */
    public DsReloader(int minutes, DsResults ds) {
        this.ds = ds;
        Timer timer = new Timer();
        timer.schedule(new ReloadTask(), minutes * 60000, minutes * 60000);
    }


    /**
     * CLass ReloadTask
     */
    private class ReloadTask extends TimerTask {
        /**
         * Implement TimerTask.run()
         */
        @Override
        public void run() {
            System.out.println("[" + DateTimeUtils.getToday() + " " + DateTimeUtils.getTime() + "] Reloading indexes - please wait");
            ds.loadIndexes();
            // timer.cancel(); //Terminate the timer thread
        }
    }
}
