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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jab.docsearch.utils.Messages;

/**
 * Class DsEmail
 *
 * @version $Id: DsEmail.java 172 2012-09-14 15:24:32Z henschel $
 */
public class DsEmail extends JDialog implements ActionListener {
    DsProperties rr;
    JButton okButton = new JButton(Messages.getString("DocSearch.ok"));
    JTextField nameField = new JTextField(22);
    JLabel nameLbl = new JLabel(Messages.getString("DocSearch.emailAddr"));
    JButton cancelButton = new JButton(Messages.getString("DocSearch.cancel"));
    JPanel bPanel = new JPanel();
    int numPanels = 2;
    JPanel[] panels;
    public boolean confirmed = false;


    public DsEmail(DsProperties rr, String title, boolean modal) {
        super(rr, title, modal);
        //
        panels = new JPanel[numPanels];
        for (int i = 0; i < numPanels; i++)
            panels[i] = new JPanel();
        bPanel.add(nameLbl);
        bPanel.add(nameField);
        panels[0].add(bPanel);
        panels[1].add(okButton);
        panels[1].add(cancelButton);
        //
        this.rr = rr;
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        // load up the GUI
        // NOW PLACE THE GUI ONTO A GRIDBAG
        // put in the gridbag stuff
        getContentPane().setLayout(new GridLayout(1, numPanels));
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        getContentPane().setLayout(gridbaglayout);
        //
        for (int i = 0; i < numPanels; i++) {
            gridbagconstraints.fill = 1;
            gridbagconstraints.insets = new Insets(1, 1, 1, 1);
            gridbagconstraints.gridx = 0;
            gridbagconstraints.gridy = i;
            gridbagconstraints.gridwidth = 1;
            gridbagconstraints.gridheight = 1;
            gridbagconstraints.weightx = 1.0D;
            gridbagconstraints.weighty = 1.0D;
            gridbaglayout.setConstraints(panels[i], gridbagconstraints);
            getContentPane().add(panels[i]);
        }
    } // end for constructor


    public void init() {
        pack();
        // center this dialog
        Rectangle frameSize = getBounds();
        int newX = 0;
        int newY = 0;
        Dimension screenD = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenD.width;
        int screenHeight = screenD.height;
        if (screenWidth > frameSize.width)
            newX = (screenWidth - frameSize.width) / 2;
        if (screenHeight > frameSize.height)
            newY = (screenHeight - frameSize.height) / 2;
        if ((newX != 0) || (newY != 0))
            setLocation(newX, newY);
        // end of centering the dialog
        setBackground(Color.white);
    } // end for init


    @Override
	public void actionPerformed(ActionEvent actionevent) {
        String s = actionevent.getActionCommand();
        if (s.equals(DsProperties.OK)) {
            confirmed = true;
            this.setVisible(false);
        } // end for ok
        if (s.equals(DsProperties.CANCEL)) {
            confirmed = false;
            this.setVisible(false);
        } // end for ok
    } // end for actionPerformed

} // end for class
