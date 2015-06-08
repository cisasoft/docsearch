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
package org.jab.docsearch.utils;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * Class Logging
 *
 * @version $Id: Logging.java 112 2009-05-06 06:21:54Z henschel $
 */
public final class Logging {
    /**
     * Inits logging for DocSearcher
     */
    public void init() {
        // init Log4J with xml config
        DOMConfigurator.configure(getClass().getResource("/org/jab/docsearch/log4j.xml"));

        // logger test
        Logger logger = Logger.getLogger(getClass().getName());
        logger.debug("init() logging is initialized");
    }


    /**
     * add logfile appender from Log4J
     */
    public void addFileLogger() {
        // get root logger
        Logger logger = Logger.getRootLogger();

        logger.info("addFileLogger() add logfile Appender");

        // create layout
        PatternLayout layout = new PatternLayout();
        layout.setConversionPattern("%d %-6p [%c] %m%n");

        // create appender
        RollingFileAppender logfileAppender = null;
        try {
            logfileAppender = new RollingFileAppender(layout, "docsearcher.log");
            logfileAppender.setName("logfile");
            logfileAppender.setMaxBackupIndex(5);
            logfileAppender.setMaxFileSize("5MB");
            logfileAppender.setThreshold(Level.DEBUG);
            logfileAppender.setImmediateFlush(true);
        }
        catch(IOException ioe) {
            logger.fatal("addFileLogger() failed", ioe);
        }

        // add appender
        if (logfileAppender != null) {
            logger.addAppender(logfileAppender);
        }

        if (logger.getAppender("logfile") != null) {
            logger.info("addFileLogger() logfile logger successfully added");
        }
        else {
            logger.error("addFileLogger() logfile logger not successfully added");
        }
    }
}
