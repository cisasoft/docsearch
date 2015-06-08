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

import java.awt.BorderLayout;
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
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.jab.docsearch.DocSearch;
import org.jab.docsearch.FileEnvironment;
import org.jab.docsearch.utils.DocTypeHandler;
import org.jab.docsearch.utils.Messages;

/**
 * Class NewHandlerDialog
 *
 * @version $Id: NewHandlerDialog.java 172 2012-09-14 15:24:32Z henschel $
 */
public final class NewHandlerDialog extends JDialog implements ActionListener {
    /**
     * Environment
     */
    private final static FileEnvironment fEnv = FileEnvironment.getInstance();
    private final JPanel[] panels;
    private static final String ADD = Messages.getString("DocSearch.ok");
    private static final String CANCEL = Messages.getString("DocSearch.btnCancel");
    private static final String BROWSE = Messages.getString("DocSearch.btnBrowse");
    private static final String MISSI = Messages.getString("DocSearch.msgMI");
    private static final String PROVIDEALL = Messages.getString("DocSearch.provAll");
    private static final String SELECT = Messages.getString("DocSearch.btnSelect");
    //
    //
    private final JButton okButton = new JButton(ADD);
    private final JButton cancelButton = new JButton(CANCEL);
    public boolean returnBool = false;

    // start in
    private final JPanel descP = new JPanel();
    public JLabel descLabel = new JLabel(Messages.getString("DocSearch.hdnlrNew"));
    public JTextField descField = new JTextField(25);
    //
    private final JPanel appP = new JPanel();
    private final JLabel appLabel = new JLabel(Messages.getString("DocSearch.appl"));
    public JTextField appField = new JTextField(25);
    private final JButton browseButton = new JButton(BROWSE);
    //
    private final JPanel extP = new JPanel();
    private final JLabel extLabel = new JLabel(Messages.getString("DocSearch.applEx"));
    public JTextField extField = new JTextField(25);
    //
    private final JPanel topP = new JPanel();
    private final JPanel bp = new JPanel();
    private final int numPanels = 2;
    private final DocSearch monitor;
    private final Font f = new Font("Times", Font.BOLD, 16);


    public NewHandlerDialog(DocSearch monitor, String title, boolean modal) {
        super(monitor, title, modal);
        // super(parent, "Generate Meta Tag Table", true);
        this.monitor = monitor;
        descP.add(descLabel);
        descP.add(descField);
        //
        appP.add(appLabel);
        appP.add(appField);
        appP.add(browseButton);
        //
        extP.add(extLabel);
        extP.add(extField);
        //
        topP.setLayout(new BorderLayout());
        topP.add(descP, BorderLayout.NORTH);
        topP.add(appP, BorderLayout.CENTER);
        topP.add(extP, BorderLayout.SOUTH);
        // accessibility info
        //
        okButton.setMnemonic(KeyEvent.VK_O);
        okButton.setToolTipText(ADD);
        //
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.setToolTipText(CANCEL);
        //
        bp.add(okButton);
        bp.add(cancelButton);
        //
        browseButton.setMnemonic(KeyEvent.VK_B);
        browseButton.setToolTipText(BROWSE);
        //
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        browseButton.addActionListener(this);
        //
        panels = new JPanel[numPanels];
        for (int i = 0; i < numPanels; i++)
            panels[i] = new JPanel();

        panels[0].add(topP);
        panels[0].setBorder(new TitledBorder(Messages.getString("DocSearch.hndlrItems")));
        //
        panels[1].add(bp);
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
            if ((appField.getText().trim().equals("")) || (extField.getText().trim().equals("")) || (descField.getText().trim().equals("")))
                monitor.showMessage(MISSI, PROVIDEALL);
            else {
                returnBool = true;
                this.setVisible(false);
            }
        } else if (s.equals(BROWSE)) {
            JFileChooser fdo = new JFileChooser();
            fdo.setCurrentDirectory(new File(fEnv.getUserHome()));
            int fileGotten = fdo.showDialog(this, SELECT);
            if (fileGotten == JFileChooser.APPROVE_OPTION) {
                File file = fdo.getSelectedFile();
                String fNa = file.toString();
                appField.setText(fNa);
            }
        }
        if (s.equals(CANCEL)) {
            returnBool = false;
            this.setVisible(false);
        }
    }


    public void setDTH(DocTypeHandler dh) {
        //
        appField.setText(dh.getApp());
        extField.setText(dh.getExtension());
        descField.setText(dh.getDesc());
    }
}
