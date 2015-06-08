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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.jab.docsearch.DocSearch;
import org.jab.docsearch.DocSearcherIndex;
import org.jab.docsearch.FileEnvironment;
import org.jab.docsearch.utils.FileUtils;
import org.jab.docsearch.utils.Messages;

/**
 * Class MetaDialog
 *
 * @version $Id: MetaDialog.java 171 2012-09-07 20:08:01Z henschel $
 */
public final class MetaDialog extends JDialog implements ActionListener {
    /**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 5988838385559961129L;

	/**
     * FileEnvironment
     */
    private final static FileEnvironment fEnv = FileEnvironment.getInstance();
    private static final String CANCEL = Messages.getString("DocSearch.btnCancel");
    private static final String BROWSE = Messages.getString("DocSearch.btnBrowse");
    private static final String SELECT = Messages.getString("DocSearch.btnSelect");
    private static final String dsRunRpt = Messages.getString("DocSearch.btnRunRpt");
    private static final String dsListAll = Messages.getString("DocSearch.cbxListAll");
    private static final String dsReqPath = Messages.getString("DocSearch.cbxReqPath");
    private static final String dsReqAuth = Messages.getString("DocSearch.cbxReqAuth");
    private static final String dsMaxDox = Messages.getString("DocSearch.lblMaxDocsToGet");
    private static final String dsSaveAs = Messages.getString("DocSearch.btnSaveAs");
    private static final String dsGenOPts = Messages.getString("DocSearch.genOpts");
    private static final String dsSaveRptToFi = Messages.getString("DocSearch.lblSaveRptTo");
    private static final String dsMaxAge = Messages.getString("DocSearch.lblMaxAge");
    private static final String dsIdxToRptOn = Messages.getString("DocSearch.lblIdxToRptOn");

    private final JButton okButton = new JButton(dsRunRpt);
    private final JButton cancelButton = new JButton(CANCEL);
    private final JPanel generalPanel = new JPanel();
    private final JPanel advancedPanel = new JPanel();
    private final CheckBoxListener cbl;
    private final JTabbedPane tabbedPane;
    //
    private final JPanel listPanel = new JPanel();
    private final JCheckBox listAll = new JCheckBox(dsListAll);
    //
    private final JPanel pathPanel = new JPanel();
    private final JCheckBox pathRequired = new JCheckBox(dsReqPath);
    private final JTextField pathField = new JTextField(10);
    private final JButton pathBrowseButton = new JButton(BROWSE);
    //
    private final JPanel authPanel = new JPanel();
    private final JCheckBox authRequired = new JCheckBox(dsReqAuth);
    private final JTextField authField = new JTextField(25);
    //
    private final JPanel maxPanel = new JPanel();
    private final JLabel maxDocsLabel = new JLabel(dsMaxDox);
    private final JComboBox maxDocs = new JComboBox();
    //
    private final JPanel reportPanel = new JPanel();
    private final JLabel reportFileLabel = new JLabel(dsSaveRptToFi);
    private final JTextField reportField = new JTextField(10);
    private final JButton browseReportFileButton = new JButton(dsSaveAs);
    //
    private final JPanel datePanel = new JPanel();
    private final JCheckBox dateRequired = new JCheckBox(dsMaxAge);
    private final JTextField dateField = new JTextField(11);
    //
    private final JLabel dirLabel = new JLabel(dsIdxToRptOn);
    private final JPanel indexChoicePanel = new JPanel();
    private final JPanel bp = new JPanel();
    private final JComboBox indexChoice = new JComboBox();
    //
    public boolean confirmed = false;

    /**
     * Constructor
     *
     * @param monitor
     * @param title
     * @param modal
     */
    public MetaDialog(DocSearch monitor, String title, boolean modal) {
        super(monitor, title, modal);

        //
        int numIs = monitor.numIndexes();
        DocSearcherIndex di;
        // int indexToReportOn = -1;
        for (int i = 0; i < numIs; i++) {
            di = monitor.getDSIndex(i);
            if (i == 0) {
                pathField.setText(di.getPath());
            }

            indexChoice.addItem(di.getName());
        }

        indexChoicePanel.add(dirLabel);
        indexChoicePanel.add(indexChoice);

        //
        cbl = new CheckBoxListener();

        // set up the buttons
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        browseReportFileButton.addActionListener(this);
        pathBrowseButton.addActionListener(this);

        //
        tabbedPane = new JTabbedPane();

        //
        pathPanel.add(pathRequired);
        pathPanel.add(pathField);
        pathPanel.add(pathBrowseButton);

        //
        authPanel.add(authRequired);
        authPanel.add(authField);

        //
        listPanel.add(listAll);

        //
        generalPanel.setLayout(new BorderLayout());
        generalPanel.setBorder(new TitledBorder(dsGenOPts));
        generalPanel.add(listAll, BorderLayout.NORTH);
        generalPanel.add(authPanel, BorderLayout.CENTER);
        generalPanel.add(pathPanel, BorderLayout.SOUTH);

        //
        tabbedPane.addTab(Messages.getString("DocSearch.lblLstgAuthPath"), null, generalPanel, Messages.getString("DocSearch.lblTipLAP"));

        //
        maxDocs.addItem("100");
        maxDocs.addItem("500");
        maxDocs.addItem("5000");
        maxDocs.addItem("10000");
        maxDocs.addItem("50000");
        maxDocs.addItem("100000");
        maxPanel.add(maxDocsLabel);
        maxPanel.add(maxDocs);
        //
        datePanel.add(dateRequired);
        datePanel.add(dateField);
        //
        reportPanel.add(reportFileLabel);
        reportPanel.add(reportField);
        reportPanel.add(browseReportFileButton);
        //
        advancedPanel.setLayout(new BorderLayout());
        advancedPanel.setBorder(new TitledBorder(Messages.getString("DocSearch.lbladvopts")));
        advancedPanel.add(datePanel, BorderLayout.NORTH);
        advancedPanel.add(maxPanel, BorderLayout.CENTER);
        advancedPanel.add(reportPanel, BorderLayout.SOUTH);
        //
        tabbedPane.addTab(Messages.getString("DocSearch.lblDateMaxSave"), null, advancedPanel, Messages.getString("DocSearch.lblTipDMS"));
        //
        bp.add(okButton);
        bp.add(cancelButton);
        // load up the GUI
        //
        okButton.setMnemonic(KeyEvent.VK_R);
        okButton.setToolTipText(dsRunRpt);
        //
        //
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.setToolTipText(CANCEL);
        //
        // NOW PLACE THE GUI ONTO A GRIDBAG
        // put in the gridbag stuff
        getContentPane().setLayout(new GridLayout(1, 3));
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        getContentPane().setLayout(gridbaglayout);
        //
        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 0.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(indexChoicePanel, gridbagconstraints);
        getContentPane().add(indexChoicePanel);

        //
        //
        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 1;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(tabbedPane, gridbagconstraints);
        getContentPane().add(tabbedPane);

        //
        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 2;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(bp, gridbagconstraints);
        getContentPane().add(bp);

        //
        pathRequired.addActionListener(cbl);
        authRequired.addActionListener(cbl);
        dateRequired.addActionListener(cbl);
        listAll.addActionListener(cbl);
    }


    /**
     * Method init
     */
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

        reportField.setText(FileUtils.addFolder(fEnv.getWorkingDirectory(), "meta_rpt_file.htm"));
        authField.setText(System.getProperty("user.name"));
        dateField.setText("730");
        cb();
    }


    /**
     * Implement actionPerformed from ActionListener
     */
    @Override
    public void actionPerformed(ActionEvent actionevent) {
        String s = actionevent.getActionCommand();
        if (s.equals(dsRunRpt)) {
            // check everything
            confirmed = true;
            this.setVisible(false);
        }
        // end for ok
        else if (s.equals(dsSaveAs)) {
            JFileChooser fdo = new JFileChooser();
            fdo.setCurrentDirectory(new File(fEnv.getUserHome()));
            int fileGotten = fdo.showDialog(this, SELECT);
            if (fileGotten == JFileChooser.APPROVE_OPTION) {
                File file = fdo.getSelectedFile();
                String fNa = file.toString();
                if (!fNa.toLowerCase().endsWith(".htm")) {
                    fNa += ".htm";
                }

                reportField.setText(fNa);
            }
        }
        else if (s.equals(BROWSE)) {
            JFileChooser fdo = new JFileChooser();
            fdo.setCurrentDirectory(new File(fEnv.getUserHome()));
            fdo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int fileGotten = fdo.showDialog(this, SELECT);
            if (fileGotten == JFileChooser.APPROVE_OPTION) {
                File file = fdo.getSelectedFile();
                pathField.setText(file.toString());
            }
        }
        else if (s.equals(CANCEL)) {
            confirmed = false;
            this.setVisible(false);
        }
    }


    /**
     * Method cb
     */
    private void cb() {
        if (dateRequired.isSelected()) {
            dateField.setEnabled(true);
        }
        else {
            dateField.setEnabled(false);
        }

        if (pathRequired.isSelected()) {
            pathField.setEnabled(true);
            pathBrowseButton.setEnabled(true);
        }
        else {
            pathField.setEnabled(false);
            pathBrowseButton.setEnabled(false);
        }

        if (authRequired.isSelected()) {
            authField.setEnabled(true);
        }
        else {
            authField.setEnabled(false);
        }
    }

    /**
     * Class CheckBoxListener
     */
    private class CheckBoxListener implements ActionListener {
        /**
         * Implementaion of ActionListener.actionPerformed
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            cb();
        }
    }


    /**
     * Gets listAllIsSelected
     *
     * @return
     */
    public boolean getListAllIsSelected() {
        return listAll.isSelected();
    }


    /**
     * Gets pathRequiredSelected
     *
     * @return
     */
    public boolean getPathRequiredSelected() {
        return pathRequired.isSelected();
    }


    /**
     * Gets dateRequiredSelected
     *
     * @return
     */
    public boolean getDateRequiredSelected() {
        return dateRequired.isSelected();
    }


    /**
     * Get authSelected
     *
     * @return
     */
    public boolean getAuthSelected() {
        return authRequired.isSelected();
    }


    /**
     * Gets maxDocs
     *
     * @return
     */
    public String getMaxDocs() {
        return (String) maxDocs.getModel().getElementAt(maxDocs.getSelectedIndex());
    }


    /**
     * Gets pathFileText
     *
     * @return
     */
    public String getPathFieldText() {
        return pathField.getText();
    }


    /**
     * Gets authFieldText
     *
     * @return
     */
    public String getAuthFieldText() {
        return authField.getText();
    }


    /**
     * Gets reportFieldText
     *
     * @return
     */
    public String getReportFieldText() {
        return reportField.getText();
    }


    /**
     * Gets dateFieldText
     *
     * @return
     */
    public String getDateFieldText() {
        return dateField.getText();
    }


    /**
     * Gets selectedIndexNum
     *
     * @return
     */
    public int getSelectedIndexNum() {
        return indexChoice.getSelectedIndex();
    }
}
