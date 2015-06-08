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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jab.docsearch.DocSearch;
import org.jab.docsearch.utils.I18n;
import org.jab.docsearch.utils.Messages;

/**
 * Class NewBookmarkDialog
 *
 * @version $Id: NewBookmarkDialog.java 172 2012-09-14 15:24:32Z henschel $
 */
public class NewBookmarkDialog extends JDialog implements ActionListener {
    JPanel[] panels;
    static final String ADD = Messages.getString("DocSearch.btnAdd");
    static final String CANCEL = Messages.getString("DocSearch.btnCancel");
    static final String MSGDSC = Messages.getString("DocSearch.errBkmrkMsgDesc");
    JButton okButton = new JButton(ADD);
    JButton cancelButton = new JButton(CANCEL);
    boolean returnBool = false;

    // start in
    JLabel titleLabel = new JLabel(Messages.getString("DocSearch.lblDesc"));
    JTextField descField = new JTextField(25);

    // search depth
    JLabel locationLabel = new JLabel(Messages.getString("DocSearch.lblUrl"));
    JTextField locationField = new JTextField(45);
    int numPanels = 4;
    DocSearch monitor;
    Font f = new Font("Times", Font.BOLD, 16);
    JLabel dirLabel = new JLabel(Messages.getString("DocSearch.lblModifyTitle"));


    public NewBookmarkDialog(DocSearch monitor, String title, boolean modal) {
        super(monitor, title, modal);

        // super(parent, "Generate Meta Tag Table", true);
        this.monitor = monitor;
        locationField.setEditable(false);

        // accessibility info
        //
        okButton.setMnemonic(KeyEvent.VK_A);
        okButton.setToolTipText(ADD);

        //
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.setToolTipText(CANCEL);

        //
        dirLabel.setFont(f);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        panels = new JPanel[numPanels];
        for (int i = 0; i < numPanels; i++) {
            panels[i] = new JPanel();
        }

        panels[0].add(dirLabel);

        //
        panels[1].add(titleLabel);
        panels[1].add(descField);

        panels[2].add(locationLabel);
        panels[2].add(locationField);

        panels[3].add(okButton);
        panels[3].add(cancelButton);
        panels[2].setBackground(Color.orange);
        panels[1].setBackground(Color.orange);

        // now for the gridbag
        getContentPane().setLayout(new GridLayout(1, numPanels));
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        getContentPane().setLayout(gridbaglayout);
        for (int i = 0; i < numPanels; i++) {
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
            getContentPane().add(panels[i]);
        }
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
        if (s.equals(ADD)) {
            StringBuilder errBuf = new StringBuilder();
            boolean hasErr = false;
            if (descField.getText().trim().equals("")) {
                hasErr = true;
                errBuf.append(MSGDSC);
            }

            if (hasErr) {
                monitor.showMessage(I18n.getString("error"), errBuf.toString());
            }
            else {
                returnBool = true;
                this.setVisible(false);
            }
        }

        if (s.equals(CANCEL)) {
            returnBool = false;
            this.setVisible(false);
        }
    }


    public boolean isConfirmed() {
        return returnBool;
    }


    public String getDesc() {
        return descField.getText();
    }


    public void setDesc(String toSet) {
        descField.setText(toSet);
    }


    public String getBMLocation() {
        return locationField.getText();
    }


    public void setBMLocation(String toSet) {
        locationField.setText(toSet);
    }

}
