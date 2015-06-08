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

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

/**
 * Class EmailThread
 *
 * @version $Id: EmailThread.java 172 2012-09-14 15:24:32Z henschel $
 */
public final class EmailThread implements Runnable {
    /**
     * Log4J logger
     */
    private final Logger logger = Logger.getLogger(getClass().getName());
    private final String[] addresses;
    private final Properties props;
    private final String from;
    private final String msg;
    private final String subj;
    private Thread checker;
    private boolean isText = true;


    /**
     * Constructor
     *
     * @param addresses
     * @param props
     * @param from
     * @param msg
     * @param subj
     */
    public EmailThread(String[] addresses, Properties props, String from, String msg, String subj) {
        this.addresses = addresses;
        this.props = props;
        this.from = from;
        this.msg = msg;
        this.subj = subj;
    }


    /**
     * Set text format
     *
     * @param toSet
     */
    public void setTextFormat(boolean toSet) {
        isText = toSet;
    }


    /**
     * Start
     */
    public void start() {
        if (checker == null) {
            checker = new Thread(this, "checker");
            checker.start();
        }
    }


    /**
     * Stop
     */
    public void stop() {
        checker.interrupt();
        checker = null;
    }


    /**
     * Implement Runnable.run()
     */
    @Override
	public void run() {
        for (Thread thread = Thread.currentThread(); checker == thread;) {
            try {
                sendEmail();
            }
            catch (Exception e) {
                logger.fatal("run() Email Thread error", e);
            }
            finally {
                stop();
                if (checker != null) {
                    checker.destroy();
                }
            }
        }
    }


    /**
     * Send email
     *
     * @throws MessagingException
     */
    public void sendEmail() throws MessagingException {
        // method send out our email
        Session session = Session.getDefaultInstance(props, null);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        int numAdds = addresses.length;
        for (int i = 0; i < numAdds; i++) {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(addresses[i]));
        }
        message.setSubject(subj);
        if (! isText) {
            message.setContent(msg, "text/html");
        }
        else {
            message.setText(msg);
        }
        Transport.send(message);
    }
}
