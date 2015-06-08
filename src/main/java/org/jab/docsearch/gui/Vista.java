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
import java.awt.geom.Point2D;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

/**
 * Class Vista
 *
 * @version $Id: Vista.java 172 2012-09-14 15:24:32Z henschel $
 */
public class Vista implements Pageable {
    private int mNumPagesX;
    private int mNumPagesY;
    private int mNumPages;
    private Printable mPainter;
    private PageFormat mFormat;


    public Vista() {
        // nothing
    }


    public Vista(float width, float height, Printable painter, PageFormat format) {
        setPrintable(painter);
        setPageFormat(format);
        setSize(width, height);
    }


    protected void setPrintable(Printable painter) {
        mPainter = painter;
    }


    protected void setPageFormat(PageFormat pageFormat) {
        mFormat = pageFormat;
    }


    protected void setSize(float width, float height) {
        mNumPagesX = (int) (((width + mFormat.getImageableWidth()) - 1) / mFormat.getImageableWidth());
        mNumPagesY = (int) (((height + mFormat.getImageableHeight()) - 1) / mFormat.getImageableHeight());
        mNumPages = mNumPagesX * mNumPagesY;
    }


    @Override
	public int getNumberOfPages() {
        return mNumPages;
    }


    protected PageFormat getPageFormat() {
        return mFormat;
    }


    @Override
	public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
        if (pageIndex >= mNumPages) {
            throw new IndexOutOfBoundsException();
        }

        return getPageFormat();
    }


    @Override
	public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
        if (pageIndex >= mNumPages) {
            throw new IndexOutOfBoundsException();
        }

        double originX = (pageIndex % mNumPagesX) * mFormat.getImageableWidth();
        double originY = (pageIndex / mNumPagesX) * mFormat.getImageableHeight();
        Point2D.Double origin = new Point2D.Double(originX, originY);

        return new TranslatedPrintable(mPainter, origin);
    }

    public static final class TranslatedPrintable implements Printable {
        private final Printable mPainter;
        private final Point2D mOrigin;


        public TranslatedPrintable(Printable painter, Point2D origin) {
            mPainter = painter;
            mOrigin = origin;
        }


        @Override
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(-mOrigin.getX(), -mOrigin.getY());
            mPainter.print(g2, pageFormat, 1);

            return PAGE_EXISTS;
        }
    }
}
