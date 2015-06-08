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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.Toolkit;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;

import org.apache.log4j.Logger;
import org.jab.docsearch.DocSearch;

/**
 * Class DocSplashViewer
 *
 * @version $Id: DocSplashViewer.java 172 2012-09-14 15:24:32Z henschel $
 */
public class DocSplashViewer extends JPanel {
    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(getClass().getName());
    private Image image = null;
    private String labelText = "Loading Please wait...";
    private JWindow j;
    private int imageHeight;
    private int imageWwidth;
    private MediaTracker tracker;
    private DocSearch monitor;
    private final int sizeW = 900;
    private final int sizeH = 940;
    private String lastStatus = "";
    private boolean hasMon = false;


    /**
     * Constructor of DocSpashViewer
     *
     * @param labelText
     */
    public DocSplashViewer(final String labelText) {
        image = loadImage();
        this.labelText = labelText;

        repaint();
    }


    /**
     * Close splash screen
     */
    public void close() {
        if (j != null) {
            j.dispose();
        }
    }


    /**
     * Sets monitor
     *
     * @param mon
     */
    public void setMonitor(final DocSearch mon) {
        hasMon = true;
        monitor = mon;

        repaint();

        StatusThread st = new StatusThread();
        st.start();
    }


    /**
     * @see JPanel#paintComponents(Graphics)
     */
    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.blue);
        setBackground(Color.white);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.white);
        g.fillRect(0, 0, sizeW, sizeH);
        g.drawRect(0, 0, imageWwidth + 2, imageHeight + 2);

        if ((imageWwidth != 0) && (imageHeight != 0)) {
            g.drawImage(image, 1, 1, imageWwidth, imageHeight, this);
        }

        g.fillRect(0, 0, imageWwidth, 17);
        g.setColor(Color.black);
        g.drawString(labelText, 10, 13);
    }


    /**
     * Load image
     *
     * @param imageFileName  image file name
     * @return               return loaded image or null
     */
    private Image loadImage() {
        Image newImage = null;

        try {
            newImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/splash.gif"));
            tracker = new MediaTracker(this);
            tracker.addImage(newImage, 0);
            tracker.waitForAll();
            imageHeight = newImage.getHeight(this);
            imageWwidth = newImage.getWidth(this);
            if (imageHeight <= 0 || imageWwidth <= 0) {
                logger.error("loadImage() failed to load splash image");
            }
        }
        catch (InterruptedException ie) {
            logger.error("loadImage() Image problem", ie);
        }

        return newImage;
    }


    /**
     * @see JPanel#getHeight()
     */
    @Override
    public int getHeight() {
        return imageHeight;
    }


    /**
     * @see JPanel#getWidth()
     */
    @Override
    public int getWidth() {
        return imageWwidth;
    }


    /**
     * Diplay dialog
     */
    public void display() {
        j = new JWindow();
        j.getContentPane().setLayout(new GridLayout(1, 1));

        GridBagLayout gridbaglayout = new GridBagLayout();

        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        j.getContentPane().setLayout(gridbaglayout);

        JScrollPane imageScroll = new JScrollPane(this);
        imageScroll.setPreferredSize(new Dimension(365, 365));

        gridbagconstraints.fill = GridBagConstraints.BOTH;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 1.0D;
        gridbaglayout.setConstraints(imageScroll, gridbagconstraints);
        j.getContentPane().add(imageScroll);

        Dimension screenD = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenD.width;
        int screenHeight = screenD.height;

        j.setLocation((screenWidth / 2) - (350 / 2), (screenHeight / 2) - (250 / 2));
        j.pack();
        j.setVisible(true);

        repaint();
    }


    /**
     * @see JPanel#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize() {
        int newW = 360;
        int newH = 360;

        if (image != null) {
            newW = image.getWidth(this);
            newH = image.getHeight(this);
        }

        return new Dimension(newW, newH);
    }


    /**
     * Class StatusThread
     */
    public class StatusThread implements Runnable {
        Thread statusValidator;

        /**
         * Method start
         */
        public void start() {
            if (statusValidator == null) {
                statusValidator = new Thread(this, "statusValidator");
                statusValidator.start();
            }
        }


        /**
         * Method stop
         */
        public void stop() {
            statusValidator.interrupt();
            statusValidator = null;
        }


        /**
         * Implementation of method run from interfaxe Runnable
         */
        @Override
		public void run() {
            for (Thread thread = Thread.currentThread(); statusValidator == thread;) {
                try {
                    // we run validation in a thread so as not to interfere with repaints of GUI
                    if (hasMon) {
                        labelText = monitor.getCurStatus();
                        if (! lastStatus.equals(labelText)) {
                            lastStatus = labelText;
                            repaint();
                        }
                    }
                }
                catch (Exception e) {
                    logger.error("run() Loading thread was stopped!", e);
                }
                finally {
                    stop();
                    if (statusValidator != null) {
                        statusValidator.destroy();
                    }
                }
            }
        }
    }
}
