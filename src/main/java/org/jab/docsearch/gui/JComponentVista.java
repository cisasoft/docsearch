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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.JComponent;

/**
 * Class JComponentVista
 *
 * @version $Id: JComponentVista.java 172 2012-09-14 15:24:32Z henschel $
 */
public class JComponentVista extends Vista implements Printable {
    // private static final boolean SYMMETRIC_SCALING = true;
    // private static final boolean ASYMMETRIC_SCALING = false;
    private double mScaleX;
    private double mScaleY;
    private JComponent mComponent;


    public JComponentVista(JComponent c, PageFormat format) {
        setPageFormat(format);
        setPrintable(this);
        setComponent(c);

        Rectangle componentBounds = c.getBounds(null);
        setSize(componentBounds.width, componentBounds.height);
        setScale(1, 1);
    }


    public void setComponent(JComponent c) {
        mComponent = c;
    }


    public void setScale(double scaleX, double scaleY) {
        mScaleX = scaleX;
        mScaleY = scaleY;
    }


    public void scaleToFitX() {
        PageFormat format = getPageFormat();
        Rectangle componentBounds = mComponent.getBounds(null);
        double scaleX = format.getImageableWidth() / componentBounds.width;
        double scaleY = scaleX;
        if (scaleX < 1) {
            setSize((float) format.getImageableWidth(), (float) (componentBounds.height * scaleY));
            setScale(scaleX, scaleY);
        }
    }


    public void scaleToFitY() {
        PageFormat format = getPageFormat();
        Rectangle componentBounds = mComponent.getBounds(null);
        double scaleY = format.getImageableHeight() / componentBounds.height;
        double scaleX = scaleY;
        if (scaleY < 1) {
            setSize((float) (componentBounds.width * scaleX), (float) format.getImageableHeight());
            setScale(scaleX, scaleY);
        }
    }


    public void scaleToFit(boolean useSymmetricScaling) {
        PageFormat format = getPageFormat();
        Rectangle componentBounds = mComponent.getBounds(null);
        double scaleX = format.getImageableWidth() / componentBounds.width;
        double scaleY = format.getImageableHeight() / componentBounds.height;
        System.out.println("Scale: " + scaleX + " " + scaleY);
        if ((scaleX < 1) || (scaleY < 1)) {
            if (useSymmetricScaling) {
                if (scaleX < scaleY) {
                    scaleY = scaleX;
                } else {
                    scaleX = scaleY;
                }
            }

            setSize((float) (componentBounds.width * scaleX), (float) (componentBounds.height * scaleY));
            setScale(scaleX, scaleY);
        }
    }


    @Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        Graphics2D g2 = (Graphics2D) graphics;
        g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
        Rectangle componentBounds = mComponent.getBounds(null);
        g2.translate(-componentBounds.x, -componentBounds.y);
        g2.scale(mScaleX, mScaleY);
        boolean wasBuffered = mComponent.isDoubleBuffered();
        mComponent.paint(g2);
        mComponent.setDoubleBuffered(wasBuffered);

        return PAGE_EXISTS;
    }
}
