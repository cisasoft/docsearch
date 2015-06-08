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
import java.awt.Image;
import java.awt.Panel;

/**
 * Class ProgressPanel
 *
 * @version $Id: ProgressPanel.java 137 2009-09-18 13:56:27Z henschel $
 */
public class ProgressPanel extends Panel {
	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1050360540379242726L;

	private long curPos = 0L;
    private long totalTime;
    private final int barWidth = 250;
    private final int barHeight = 22;
    private final int leftX = 10;
    private String curMessage;
    private boolean active = false;
    private Image offScreenBuffer;
    private final Color darkBlue = new Color(10, 50, 105);


    public ProgressPanel(String curMessage, long totalTime) {
        this.totalTime = totalTime;
        this.curMessage = curMessage;

        repaint();
    }


    public void setCurPos(long toSet) {
        curPos = toSet;
        repaint();
    }


    public void setMaxPos(long toSet) {
        totalTime = toSet;
        active = true;
    }


    public void setMSG(String toSet) {
        curMessage = toSet;
    }


    public boolean getActive() {
        return active;
    }


    public void setActive(boolean toSet) {
        active = toSet;
    }


    public void reset() {
        curMessage = "";
        curPos = 0L;
        repaint();
        active = false;
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(barWidth, barHeight);
    }


    public void init() {
        setSize(new Dimension(barWidth, barHeight));
    } // end for constructor


    @Override
    public void paint(Graphics g) {
        if (getActive())
            g.setColor(Color.white);
        else
            g.setColor(getBackground());
        g.fillRect(0, 0, barWidth, barHeight);
        if (curPos > 0L) {
            long curPercent = (curPos * 100) / totalTime;
            int curPercentInt = (int) curPercent;
            int lengthOfBar = (curPercentInt * barWidth) / 100;
            g.setColor(darkBlue);
            g.fillRect(0, 0, lengthOfBar, barHeight);
        }
        g.setColor(Color.red);
        if (!curMessage.equals(""))
            g.drawString(curMessage, leftX, barHeight - 10);
    } // end for paint


    @Override
    public int getHeight() {
        return barWidth;
    }


    @Override
    public int getWidth() {
        return barWidth;
    }


    @Override
    public void update(Graphics g) {
        Graphics gr;
        // Will hold the graphics context from the offScreenBuffer.
        // We need to make sure we keep our offscreen buffer the same size
        // as the graphics context we're working with.
        if (offScreenBuffer == null || (!(offScreenBuffer.getWidth(this) == (int) this.getSize().getWidth() && offScreenBuffer.getHeight(this) == (int) this.getSize().getHeight()))) {
            offScreenBuffer = this.createImage((int) this.getSize().getWidth(), (int) this.getSize().getHeight());
        } // We need to use our buffer Image as a Graphics object:
        gr = offScreenBuffer.getGraphics();
        paint(gr); // Passes our off-screen buffer to our paint method, which,
        // unsuspecting, paints on it just as it would on the Graphics
        // passed by the browser or applet viewer.
        g.drawImage(offScreenBuffer, 0, 0, this);
    } // end for update

}
