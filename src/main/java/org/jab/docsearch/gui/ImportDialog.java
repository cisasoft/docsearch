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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jab.docsearch.DocSearch;
import org.jab.docsearch.FileEnvironment;
import org.jab.docsearch.utils.Messages;

/**
 * Class ImportDialog
 *
 * @version $Id: ImportDialog.java 172 2012-09-14 15:24:32Z henschel $
 */
public final class ImportDialog extends JDialog implements ActionListener {
    /**
     * FileEnvironment
     */
    private final static FileEnvironment fEnv = FileEnvironment.getInstance();
    private final DocSearch monitor;
    private static final String OPEN = Messages.getString("DocSearch.open");
    private static final String CANCEL = Messages.getString("DocSearch.btnCancel");
    private static final String BROWSE = Messages.getString("DocSearch.btnBrowseFiles");
    private static final String SELECT = Messages.getString("DocSearch.btnSelect");
    private static final String PLSCHOOSEFI = Messages.getString("DocSearch.plsChooseAFi");
    private static final String IMPORTDIRECTS = Messages.getString("DocSearch.importDirections");
    private static final String ERRNOTAZIP = Messages.getString("DocSearch.errNotAZip");
    private static final String MSGGETAZIP = Messages.getString("DocSearch.msgGetAZip");
    private static final String FINOTHERE = Messages.getString("DocSearch.errFiDontExist");
    private static final String GETAFI = Messages.getString("DocSearch.msgGetAFi");
    private final JButton okButton = new JButton(OPEN);
    private final JButton cancelButton = new JButton(CANCEL);
    private final JLabel urlLabel = new JLabel(Messages.getString("DocSearch.importFromUrlOrZip"));
    private final JTextField urlOrFile = new JTextField(25);
    private final JLabel dirLabel = new JLabel(Messages.getString("DocSearch.provideUrlOrFileBelow"));
    private final JPanel bp = new JPanel();
    private boolean confirmed = false;


    public ImportDialog(DocSearch monitor, String title, boolean modal) {
        super(monitor, title, modal);
        this.monitor = monitor;
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        // System.out.println("icon dir is:"+monitor.iconDir);
        JButton fileBrowse = new JButton(new ImageIcon(getClass().getResource("/icons/open.gif")));
        fileBrowse.setActionCommand(BROWSE);
        fileBrowse.addActionListener(this);
        fileBrowse.setToolTipText(BROWSE);
        bp.add(cancelButton);
        bp.add(okButton);
        // load up the GUI
        //
        okButton.setMnemonic(KeyEvent.VK_O);
        okButton.setToolTipText(OPEN);
        //
        //
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.setToolTipText(CANCEL);

        //
        // NOW PLACE THE GUI ONTO A GRIDBAG
        // put in the gridbag stuff
        getContentPane().setLayout(new GridLayout(3, 3));
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        getContentPane().setLayout(gridbaglayout);

        //
        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.gridwidth = 3;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 2.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(dirLabel, gridbagconstraints);
        getContentPane().add(dirLabel);

        //
        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 1;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(urlLabel, gridbagconstraints);
        getContentPane().add(urlLabel);

        //
        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 1;
        gridbagconstraints.gridy = 1;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(urlOrFile, gridbagconstraints);
        getContentPane().add(urlOrFile);

        //
        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 2;
        gridbagconstraints.gridy = 1;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(fileBrowse, gridbagconstraints);
        getContentPane().add(fileBrowse);

        //
        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 2;
        gridbagconstraints.gridwidth = 3;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(bp, gridbagconstraints);
        getContentPane().add(bp);
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
        if (screenWidth > frameSize.width)
            newX = (screenWidth - frameSize.width) / 2;
        if (screenHeight > frameSize.height)
            newY = (screenHeight - frameSize.height) / 2;
        if ((newX != 0) || (newY != 0))
            setLocation(newX, newY);
    }


    @Override
	public void actionPerformed(ActionEvent actionevent) {
        String s = actionevent.getActionCommand();
        if (s.equals(OPEN)) {
            confirmed = false;
            String chosenFi = urlOrFile.getText().trim();
            if (chosenFi.equals(""))
                monitor.showMessage(PLSCHOOSEFI, IMPORTDIRECTS);
            else { // some text was there
                if (!chosenFi.toLowerCase().endsWith(".zip"))
                    monitor.showMessage(ERRNOTAZIP, MSGGETAZIP);
                else if (!chosenFi.toLowerCase().startsWith("http:")) {
                    File testExist = new File(chosenFi);
                    if (!testExist.exists())
                        monitor.showMessage(FINOTHERE, GETAFI);
                    else { // file exists
                        confirmed = true;
                        this.setVisible(false);
                    }
                } else { // a URL
                    confirmed = true;
                    this.setVisible(false);
                }
            }
        } else if (s.equals(BROWSE)) {
            JFileChooser fdo = new JFileChooser();
            fdo.setCurrentDirectory(new File(fEnv.getUserHome()));
            int fileGotten = fdo.showDialog(this, SELECT);
            if (fileGotten == JFileChooser.APPROVE_OPTION) {
                File file = fdo.getSelectedFile();
                String fNa = file.toString();
                if (fNa.toLowerCase().endsWith(".zip"))
                    urlOrFile.setText(fNa);
                else
                    monitor.showMessage(ERRNOTAZIP, GETAFI);
            }
        } else if (s.equals(CANCEL)) {
            confirmed = false;
            this.setVisible(false);
        }
    }


    public boolean getConfirmed() {
        return confirmed;
    }


    public String getUrlOrFileText() {
        return urlOrFile.getText();
    }
}
