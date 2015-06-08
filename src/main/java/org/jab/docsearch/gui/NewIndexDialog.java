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
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.jab.docsearch.DocSearch;
import org.jab.docsearch.FileEnvironment;
import org.jab.docsearch.utils.FileUtils;
import org.jab.docsearch.utils.I18n;
import org.jab.docsearch.utils.Messages;
import org.jab.docsearch.utils.Utils;

/**
 * Class NewIndexDialog
 *
 * @version $Id: NewIndexDialog.java 171 2012-09-07 20:08:01Z henschel $
 */
public final class NewIndexDialog extends JDialog implements ActionListener {
    /**
     * FileEnvironment
     */
    private final static FileEnvironment fEnv = FileEnvironment.getInstance();
    private final static String IS_CD_IDX = Messages.getString("DocSearch.isCdIdx");
    private final static String TYPE_OF_IDX = Messages.getString("DocSearch.typeOfNewIdx");
    private final static String IS_WEB_IDX = Messages.getString("DocSearch.lblWSIdxg");
    private final static String IS_LOCAL = Messages.getString("DocSearch.localIdx");
    private final static String BROWSEFLDRS = Messages.getString("DocSearch.btnBrowseFolders");
    private final static String SELECT = Messages.getString("DocSearch.btnSelect");
    private final static String SELFOLD = Messages.getString("DocSearch.btnSelFold");
    private final static String SELFLDREP = Messages.getString("DocSearch.btnSelLocalFold");
    private final static String MAD = Messages.getString("DocSearch.errIdxMsgDesc");
    private final static String MII = Messages.getString("DocSearch.msgMI");
    private final static String MISL = Messages.getString("DocSearch.errMissStLoc");
    private final static String MISWR = Messages.getString("DocSearch.errwsUrlRep");
    private final static String MISRQ = Messages.getString("DocSearch.errRepPtrnWb");
    private final static String MISRPMF = Messages.getString("DocSearch.errMsgPmF");
    private final static String MISSEP = Messages.getString("DocSearch.errMsgSep");
    private final static String[] updateChoices = { Messages.getString("DocSearch.optWhenISaySo"), Messages.getString("DocSearch.optDuringStartup"), Messages.getString("DocSearch.optIdxGtOne"), Messages.getString("DocSearch.optIdxGtFive"), Messages.getString("DocSearch.optIdxGtThiry"), Messages.getString("DocSearch.optIdxGtSixty"), Messages.getString("DocSearch.optIdxGtNintey"), Messages.getString("DocSearch.optIdxGtOneEighty"), Messages.getString("DocSearch.optIdxGtYr") };

    private final static String pathSep = FileUtils.PATH_SEPARATOR;

    private final JPanel[] panels;
    private final JButton okButton;
    private final JButton cancelButton;
    private boolean returnBool = false;

    // start in
    private final JLabel startLabel = new JLabel(Messages.getString("DocSearch.lblStrtInFldr"));
    private final JLabel nameLabel = new JLabel(I18n.getString("index_name") + ":");
    private final JLabel typeOfIdxLbl = new JLabel(TYPE_OF_IDX);
    private final JTextField nameField = new JTextField(25);
    private final JTextField startField = new JTextField(25);
    private final JButton startInButton = new JButton(SELFOLD);

    // search depth
    private final JLabel searchDepthLabel = new JLabel(Messages.getString("DocSearch.lblNumSbFldrs"));
    private final RadioListener rl = new RadioListener();
    private final JComboBox sdChoice = new JComboBox();
    private final int numPanels = 6;
    private final JCheckBox searchByDefault = new JCheckBox(Messages.getString("DocSearch.lblSrchdByDflt"));

    // RADIO BUTTONS FOR TYPE OF INDEX
    private final ButtonGroup idxTypeGroup = new ButtonGroup();
    private final JRadioButton isWeb = new JRadioButton(IS_WEB_IDX);
    private final JRadioButton isCd = new JRadioButton(IS_CD_IDX);
    private final JRadioButton isLocal = new JRadioButton(IS_LOCAL);
    private final JPanel typeOfIdx = new JPanel();

    private final JTextField replaceField = new JTextField(25);
    private final JButton selectFold = new JButton(SELFLDREP);
    private final JLabel replaceLabel = new JLabel(Messages.getString("DocSearch.lblMtchPtrnFld"));
    private final JLabel matchLabel = new JLabel(Messages.getString("DocSearch.lblReplPtrnFld"));
    private final JTextField matchField = new JTextField(45);
    private final DocSearch monitor;
    private final Font f = new Font("Times", Font.BOLD, 16);
    private final JLabel dirLabel = new JLabel(Messages.getString("DocSearch.lblSrchDpth"));
    private final JPanel webPanel = new JPanel();
    private final JPanel indexFreqPanel = new JPanel();
    private final JComboBox indexFreq = new JComboBox(updateChoices);
    private final JLabel freqLabel = new JLabel(Messages.getString("DocSearch.lblWhenUpdate"));
    private final JTabbedPane tabbedPane;
    private final JPanel archivePanel;
    private final JPanel archiveContentsPanel;
    private final JLabel archiveTitle = new JLabel(Messages.getString("DocSearch.lblArchTitle"));
    private final JLabel archiveTitle2 = new JLabel(Messages.getString("DocSearch.lblZipArchUse"));
    private final JLabel archiveLabel = new JLabel(Messages.getString("DocSearch.lblArchTo"));
    private final JTextField archiveField = new JTextField(33);
    private final JButton archiveBrowseButton = new JButton(BROWSEFLDRS);


    public NewIndexDialog(DocSearch monitor, String title, boolean modal) {
        super(monitor, title, modal);
        // super(parent, "Generate Meta Tag Table", true);
        this.monitor = monitor;
        tabbedPane = new JTabbedPane();
        //
        archiveField.setText(fEnv.getArchiveDirectory());
        // accessibility info
        searchByDefault.setSelected(true);
        sdChoice.setToolTipText(Messages.getString("DocSearch.tipDepth"));
        startField.setToolTipText(Messages.getString("DocSearch.tdFlFld"));
        searchByDefault.setToolTipText(Messages.getString("DocSearch.tipCBD"));
        //
        okButton = new JButton(I18n.getString("button.create_new"));
        okButton.setActionCommand("ac_create");
        okButton.setMnemonic(I18n.getMnemonic("button.create_new.mnemonic"));
        //
        cancelButton = new JButton(I18n.getString("button.cancel"));
        cancelButton.setActionCommand("ac_cancel");
        cancelButton.setMnemonic(I18n.getMnemonic("button.cancel.mnemonic"));
        //
        selectFold.setMnemonic(KeyEvent.VK_S);
        selectFold.setToolTipText(SELFLDREP);
        selectFold.addActionListener(this);
        selectFold.setEnabled(false);
        //
        dirLabel.setFont(f);
        //
        typeOfIdx.add(typeOfIdxLbl);
        typeOfIdx.add(isLocal);
        typeOfIdx.add(isWeb);
        typeOfIdx.add(isCd);
        //
        isLocal.setActionCommand(IS_LOCAL);
        isWeb.setActionCommand(IS_WEB_IDX);
        isCd.setActionCommand(IS_CD_IDX);

        isLocal.addActionListener(rl);
        isWeb.addActionListener(rl);
        isCd.addActionListener(rl);

        idxTypeGroup.add(isLocal);
        idxTypeGroup.add(isWeb);
        idxTypeGroup.add(isCd);
        //
        for (int i = 0; i <= 20; i++)
            sdChoice.addItem(i + "");
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        startInButton.addActionListener(this);
        archiveBrowseButton.addActionListener(this);
        panels = new JPanel[numPanels];
        for (int i = 0; i < numPanels; i++)
            panels[i] = new JPanel();
        //
        JPanel nWebPanel = new JPanel();
        JPanel cWebPanel = new JPanel();
        JPanel sWebPanel = new JPanel();
        //
        archivePanel = new JPanel();
        archiveContentsPanel = new JPanel();
        archiveContentsPanel.add(archiveLabel);
        archiveContentsPanel.add(archiveField);
        archiveContentsPanel.add(archiveBrowseButton);
        archivePanel.setLayout(new BorderLayout());
        archivePanel.setBorder(new TitledBorder(Messages.getString("DocSearch.optsFrArch")));
        archivePanel.add(archiveTitle, BorderLayout.NORTH);
        archivePanel.add(archiveTitle2, BorderLayout.CENTER);
        archivePanel.add(archiveContentsPanel, BorderLayout.SOUTH);
        //
        nWebPanel.add(typeOfIdx);
        cWebPanel.add(matchLabel);
        cWebPanel.add(matchField);
        sWebPanel.add(replaceLabel);
        sWebPanel.add(replaceField);
        sWebPanel.add(selectFold);
        //
        //
        panels[1].add(startLabel);
        panels[1].add(startField);
        panels[1].add(startInButton);
        panels[2].add(searchDepthLabel);
        panels[2].add(sdChoice);
        panels[3].add(searchByDefault);
        //
        JPanel optsPane = new JPanel();
        optsPane.setLayout(new BorderLayout());
        optsPane.setBorder(new TitledBorder(Messages.getString("DocSearch.optsFrLclArch")));
        optsPane.add(panels[1], BorderLayout.NORTH);
        optsPane.add(panels[2], BorderLayout.CENTER);
        optsPane.add(panels[3], BorderLayout.SOUTH);
        webPanel.setLayout(new BorderLayout());
        webPanel.add(nWebPanel, BorderLayout.NORTH);
        webPanel.add(cWebPanel, BorderLayout.CENTER);
        webPanel.add(sWebPanel, BorderLayout.SOUTH);
        //
        indexFreqPanel.setLayout(new BorderLayout());
        indexFreqPanel.setBorder(new TitledBorder(Messages.getString("DocSearch.optsIdxUpdts")));
        JPanel freqP = new JPanel();
        JLabel notice = new JLabel(Messages.getString("DocSearch.lblUpdLtr"));
        JLabel noLabel = new JLabel(Messages.getString("DocSearch.lblUpdRsn"));
        freqP.add(freqLabel);
        freqP.add(indexFreq);
        indexFreqPanel.add(noLabel, BorderLayout.NORTH);
        indexFreqPanel.add(freqP, BorderLayout.CENTER);
        indexFreqPanel.add(notice, BorderLayout.SOUTH);
        //
        webPanel.setBorder(new TitledBorder(Messages.getString("DocSearch.lblwsip")));
        tabbedPane.addTab(Messages.getString("DocSearch.lblgnrlopts"), null, optsPane, Messages.getString("DocSearch.tipgnrlopts"));
        tabbedPane.addTab(Messages.getString("DocSearch.lbladvopts"), null, webPanel, Messages.getString("DocSearch.lbladvrsn"));
        tabbedPane.addTab(Messages.getString("DocSearch.lbludt"), null, indexFreqPanel, Messages.getString("DocSearch.lbludtrsn"));

        // archivePanel
        tabbedPane.addTab(Messages.getString("DocSearch.lblarctb"), null, archivePanel, Messages.getString("DocSearch.lblarchtrsn"));
        panels[0].add(nameLabel);
        panels[0].add(nameField);
        panels[4].add(tabbedPane);
        panels[5].add(okButton);
        panels[5].add(cancelButton);
        panels[2].setBackground(Color.orange);
        panels[1].setBackground(Color.orange);
        panels[3].setBackground(Color.orange);
        searchByDefault.setBackground(Color.orange);

        // now for the gridbag
        getContentPane().setLayout(new GridLayout(1, numPanels));
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        getContentPane().setLayout(gridbaglayout);
        int curP = 0;
        for (int i = 0; i < numPanels; i++) {
            //
            if ((i == 0) || (i >= 4)) {
                gridbagconstraints.fill = 1;
                gridbagconstraints.insets = new Insets(1, 1, 1, 1);
                gridbagconstraints.gridx = 0;
                gridbagconstraints.gridy = curP;
                gridbagconstraints.gridwidth = 1;
                gridbagconstraints.gridheight = 1;
                gridbagconstraints.weightx = 0.0D;
                gridbagconstraints.weighty = 0.0D;
                gridbaglayout.setConstraints(panels[i], gridbagconstraints);
                getContentPane().add(panels[i]);
                curP++;
            }
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
        isLocal.setSelected(true);
        // end of centering the dialog
    }


    @Override
    public void actionPerformed(ActionEvent actionevent) {
        String s = actionevent.getActionCommand();
        if (s.equals("ac_create")) {
            StringBuffer errBuf = new StringBuffer();
            boolean hasErr = false;
            if (nameField.getText().trim().equals("")) {
                hasErr = true;
                errBuf.append(MAD);
                errBuf.append("\n\n");
            }

            if (startField.getText().trim().equals("")) {
                hasErr = true;
                errBuf.append(MISL);
                errBuf.append("\n\n");
            }

            if (isWeb.isSelected()) {
                if ((matchField.getText().equals("")) || (matchField.getText().equals("http://"))) {
                    hasErr = true;
                    errBuf.append(MISWR);
                    errBuf.append("\n\n");
                }
                else if (!matchField.getText().endsWith("/")) {
                    hasErr = true;
                    errBuf.append(MISRQ);
                    errBuf.append("\n\n");
                }

                if (replaceField.getText().equals("")) {
                    hasErr = true;
                    errBuf.append(MISRPMF);
                    errBuf.append("\n\n");
                }
                else if (!replaceField.getText().endsWith(pathSep)) {
                    hasErr = true;
                    errBuf.append(MISSEP + pathSep + ")\n\n");
                }
            } // end for isWeb

            if (hasErr) {
                monitor.showMessage(MII, errBuf.toString());
            }
            else {
                returnBool = true;
                this.setVisible(false);
            }
        }
        else if (s.equals(SELFLDREP)) {
            JFileChooser fdo = new JFileChooser();
            fdo.setCurrentDirectory(new File(fEnv.getUserHome()));
            fdo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int fileGotten = fdo.showDialog(this, SELECT);
            if (fileGotten == JFileChooser.APPROVE_OPTION) {
                File file = fdo.getCurrentDirectory();
                replaceField.setText(file.toString());
            }
        }
        else if (s.equals("ac_cancel")) {
            returnBool = false;
            this.setVisible(false);
        }
        else if (s.equals(SELFOLD)) {
            JFileChooser fdo = new JFileChooser();
            fdo.setCurrentDirectory(new File(fEnv.getUserHome()));
            fdo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int fileGotten = fdo.showDialog(this, SELECT);
            if (fileGotten == JFileChooser.APPROVE_OPTION) {
                File file = fdo.getSelectedFile();
                startField.setText(file.toString());
            }
        }
        else if (s.equals(BROWSEFLDRS)) {
            JFileChooser fdo = new JFileChooser();
            fdo.setCurrentDirectory(new File(fEnv.getUserHome()));
            fdo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int fileGotten = fdo.showDialog(this, SELECT);
            if (fileGotten == JFileChooser.APPROVE_OPTION) {
                File file = fdo.getSelectedFile();
                archiveField.setText(file.toString());
            }
        }
    }

    class RadioListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String CMD = e.getActionCommand();
            if (CMD.equals(IS_WEB_IDX)) {
                replaceField.setEnabled(true);
                matchField.setEnabled(true);
                replaceField.setText(startField.getText());
                matchField.setText("http://");
                selectFold.setEnabled(true);
            }
            else if (CMD.equals(IS_CD_IDX)) {
                replaceField.setEnabled(true);
                matchField.setEnabled(true);
                replaceField.setText(Utils.getFolderOnly(startField.getText()));
                matchField.setText("[cdrom]");
                selectFold.setEnabled(true);
            }
            else if (CMD.equals(IS_LOCAL)) {
                replaceField.setEnabled(false);
                matchField.setEnabled(false);
                replaceField.setText("na");
                matchField.setText("na");
                selectFold.setEnabled(false);
            }
        } // end of action
    } // end for radio listener


    public boolean getConfirmed() {
        return returnBool;
    }


    public String getNameFieldText() {
        return nameField.getText();
    }


    public String replaceFieldText() {
        return replaceField.getText();
    }


    public String matchFieldText() {
        return matchField.getText();
    }


    public String startFieldText() {
        return startField.getText();
    }


    public String archiveFieldText() {
        return archiveField.getText();
    }


    public boolean isWebSelected() {
        return isWeb.isSelected();
    }


    public boolean isCDSelected() {
        return isCd.isSelected();
    }


    public boolean sbdSelected() {
        return searchByDefault.isSelected();
    }


    public int getPolicy() {
        return indexFreq.getSelectedIndex();
    }


    public int getSDChoice() {
        return sdChoice.getSelectedIndex();
    }
}
