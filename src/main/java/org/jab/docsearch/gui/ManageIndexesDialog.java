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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jab.docsearch.DocSearch;
import org.jab.docsearch.DocSearcherIndex;
import org.jab.docsearch.utils.DateTimeUtils;
import org.jab.docsearch.utils.Messages;

/**
 * Class ManageIndexesDialog
 *
 * @version $Id: ManageIndexesDialog.java 171 2012-09-07 20:08:01Z henschel $
 */
public class ManageIndexesDialog extends JDialog implements ActionListener {

    static final String APPLY = Messages.getString("DocSearch.btnApply");
    static final String CANCEL = Messages.getString("DocSearch.btnCancel");
    static final String SBD = Messages.getString("DocSearch.lblSrchdByDflt");
    static final String REMOVE = Messages.getString("DocSearch.lblRemove");
    static final String EXPORT = Messages.getString("DocSearch.lblExport");
    static final String UPDATE = Messages.getString("DocSearch.lblUpdate");
    static final String DAYSOLD = Messages.getString("DocSearch.lblDaysOld");
    static final String UPDATES = Messages.getString("DocSearch.lblUpdates");
    static final String IDXCNTS = Messages.getString("DocSearch.lblIdxCnt");
    JPanel[] panels;
    JLabel dirLabel = new JLabel(Messages.getString("DocSearch.lblAdjIdxs"));
    JButton okButton = new JButton(APPLY);
    JButton cancelButton = new JButton(CANCEL);
    JCheckBox[] sbd; // search by default
    JCheckBox[] del; // delete or remove
    JCheckBox[] upd; // update
    JCheckBox[] expi; // update
    JLabel[] nameLabels;
    int numIndexes = 0;
    public boolean returnBool = false;
    DocSearch monitor;
    Font f = new Font("Times", Font.BOLD, 16);
    int numPanels = 0;


    public ManageIndexesDialog(DocSearch monitor, String title, boolean modal) {
        super(monitor, title, modal);

        // super(parent, "Generate Meta Tag Table", true);
        this.monitor = monitor;

        // accessibility info
        //
        okButton.setMnemonic(KeyEvent.VK_A);
        okButton.setToolTipText(APPLY);

        //
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.setToolTipText(CANCEL);

        //
        dirLabel.setFont(f);
        if (!monitor.indexesEmpty()) {
            numIndexes = monitor.numIndexes();
            sbd = new JCheckBox[numIndexes]; // search by default
            del = new JCheckBox[numIndexes]; // delete or remove
            upd = new JCheckBox[numIndexes]; // update now
            expi = new JCheckBox[numIndexes]; // export
            nameLabels = new JLabel[numIndexes];
            Iterator<DocSearcherIndex> iterator = monitor.getIndexIterator();
            DocSearcherIndex di;
            int i = 0;
            while (iterator.hasNext()) {
                di = iterator.next();
                sbd[i] = new JCheckBox(SBD);
                sbd[i].setSelected(di.getShouldBeSearched());
                del[i] = new JCheckBox(REMOVE);
                expi[i] = new JCheckBox(EXPORT);
                expi[i].setToolTipText(" --> " + di.getArchiveDir());
                upd[i] = new JCheckBox(UPDATE + " : " + DateTimeUtils.getDaysOld(di.getLastIndexed()) + " " + DAYSOLD);
                upd[i].setToolTipText(UPDATES + di.getIndexPath());
                nameLabels[i] = new JLabel(di.getName());
                nameLabels[i].setToolTipText(IDXCNTS + di.getIndexPath());
                i++;
            }
            // end of iteration
        }

        numPanels = 2 + numIndexes;
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        panels = new JPanel[numPanels];
        for (int i = 0; i < numPanels; i++) {
            panels[i] = new JPanel();
        }

        //
        panels[0].add(dirLabel);

        //
        for (int i = 0; i < numIndexes; i++) {
            panels[1 + i].add(nameLabels[i]);
            panels[1 + i].add(sbd[i]);
            panels[1 + i].add(del[i]);
            panels[1 + i].add(upd[i]);
            panels[1 + i].add(expi[i]);
        }

        //
        panels[numPanels - 1].add(okButton);
        panels[numPanels - 1].add(cancelButton);

        // now for the gridbag
        getContentPane().setLayout(new GridLayout(1, numPanels));
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        getContentPane().setLayout(gridbaglayout);
        JPanel allPanels = new JPanel();
        JScrollPane tagsPane = new JScrollPane(allPanels);
        tagsPane.setPreferredSize(new Dimension(730, 320));
        allPanels.setLayout(new GridLayout(1, numPanels - 2));
        gridbaglayout = new GridBagLayout();
        gridbagconstraints = new GridBagConstraints();
        allPanels.setLayout(gridbaglayout);

        // populate the scrollpane
        for (int i = 1; i < (numPanels - 1); i++) {
            //
            gridbagconstraints.fill = 1;
            gridbagconstraints.insets = new Insets(1, 1, 1, 1);
            gridbagconstraints.gridx = 0;
            gridbagconstraints.gridy = i;
            gridbagconstraints.gridwidth = 1;
            gridbagconstraints.gridheight = 1;
            gridbagconstraints.weightx = 0.0D;
            gridbagconstraints.weighty = 0.0D;
            gridbaglayout.setConstraints(panels[i], gridbagconstraints);
            allPanels.add(panels[i]);
        }
        // end for adding panels

        getContentPane().setLayout(new GridLayout(1, 3));
        gridbaglayout = new GridBagLayout();
        gridbagconstraints = new GridBagConstraints();
        getContentPane().setLayout(gridbaglayout);

        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(panels[0], gridbagconstraints);
        getContentPane().add(panels[0]);

        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 1;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(tagsPane, gridbagconstraints);
        getContentPane().add(tagsPane);

        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 2;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(panels[numPanels - 1], gridbagconstraints);
        getContentPane().add(panels[numPanels - 1]);
    }


    public void init() {
        pack();

        // center this dialog
        Rectangle frameSize = getBounds();
        Dimension screenD = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenD.width;
        int screenHeight = screenD.height;
        int newX = 0;
        int newY = 0;
        if (screenWidth > frameSize.width) {
            newX = (screenWidth - frameSize.width) / 2;
        }

        if (screenHeight > frameSize.height) {
            newY = (screenHeight - frameSize.height) / 2;
        }

        if ((newX != 0) || (newY != 0)) {
            setLocation(newX, newY);
        }

    }


    @Override
    public void actionPerformed(ActionEvent actionevent) {
        String s = actionevent.getActionCommand();
        if (s.equals(APPLY)) {
            returnBool = true;
            this.setVisible(false);
        }

        if (s.equals(CANCEL)) {
            returnBool = false;
            this.setVisible(false);
        }

    }


    public boolean deletionSelected(int toGet) {
        return del[toGet].isSelected();
    }


    public boolean updateSelected(int toGet) {
        return upd[toGet].isSelected();
    }


    public boolean exportSelected(int toGet) {
        return expi[toGet].isSelected();
    }


    public boolean searchSelected(int toGet) {
        return sbd[toGet].isSelected();
    }
}
