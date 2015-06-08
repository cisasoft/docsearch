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
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.jab.docsearch.DocSearch;
import org.jab.docsearch.DocSearcherIndex;
import org.jab.docsearch.FileEnvironment;
import org.jab.docsearch.filters.GifFilter;
import org.jab.docsearch.filters.SwingFolderFilter;
import org.jab.docsearch.filters.WebFilter;
import org.jab.docsearch.utils.I18n;
import org.jab.docsearch.utils.Messages;

/**
 * Class CdAssistantDialog
 *
 * @version $Id: CdAssistantDialog.java 172 2012-09-14 15:24:32Z henschel $
 */
public final class CdAssistantDialog extends JDialog implements ActionListener {
    /**
     * FileEnvironment
     */
    private final static FileEnvironment fEnv = FileEnvironment.getInstance();

    private final JPanel[] panels;
    private static final String OK = Messages.getString("DocSearch.ok");
    private static final String CANCEL = Messages.getString("DocSearch.btnCancel");
    private static final String SELECT = Messages.getString("DocSearch.btnSelect");
    private final JPanel advancedPanel = new JPanel();
    private final JPanel basicPanel = new JPanel();
    private final JPanel bp = new JPanel();
    //
    private final JPanel cdRootPanel = new JPanel();
    private static final String CD_ROOT_DIR = Messages.getString("DocSearch.cdRoot");
    private static final String SEL_CD_ROOT_DIR = Messages.getString("DocSearch.selCdRoot");
    private final JLabel cdRootLbl = new JLabel(CD_ROOT_DIR);
    private final JButton selCdRoot = new JButton(SEL_CD_ROOT_DIR);
    private final JTextField cdRootField = new JTextField(16);
    //
    private final JPanel splashPanel = new JPanel();
    private static final String SPLASH_IMAGE = Messages.getString("DocSearch.splash");
    private static final String SEL_SPLASH_IMAGE = Messages.getString("DocSearch.selSplash");
    private final JLabel splashLbl = new JLabel(SPLASH_IMAGE);
    private final JButton selsplash = new JButton(SEL_SPLASH_IMAGE);
    private final JTextField splashField = new JTextField(16);
    //
    private final JPanel startPanel = new JPanel();
    private static final String START_PAGE = Messages.getString("DocSearch.startPage");
    private static final String SEL_START_PAGE = Messages.getString("DocSearch.selStart");
    private final JLabel startLbl = new JLabel(START_PAGE);
    private final JButton selstart = new JButton(SEL_START_PAGE);
    private final JTextField startField = new JTextField(16);
    //
    private final JPanel helpPanel = new JPanel();
    private static final String HELP_PAGE = Messages.getString("DocSearch.helpPage");
    private static final String SEL_HELP_PAGE = Messages.getString("DocSearch.selHelp");
    private final JLabel helpLbl = new JLabel(HELP_PAGE);
    private final JButton selhelp = new JButton(SEL_HELP_PAGE);
    private final JTextField helpField = new JTextField(16);
    //
    private final JPanel copyPanel = new JPanel();
    private static final String COPY_ALL = Messages.getString("DocSearch.copyAll");
    private final JCheckBox copyAllBx = new JCheckBox(COPY_ALL);
    //
    private final JPanel cdIdxsPanel = new JPanel();
    private final JPanel cdWHolePanel = new JPanel();
    private final JPanel idxsPanels[];
    private static final String SEL_CD_IDXS = Messages.getString("DocSearch.selCdIdxs");
    private final JScrollPane cdIdxsPane;
    private final JCheckBox[] cdIdxs;
    private final JTabbedPane tabbedPane = new JTabbedPane();
    //
    private final JButton okButton = new JButton(OK);
    private final JButton cancelButton = new JButton(CANCEL);
    private boolean returnBool = false;
    //
    private final int numPanels = 2;
    private final DocSearch monitor;


    public CdAssistantDialog(DocSearch monitor, String title, boolean modal) {
        super(monitor, title, modal);
        this.monitor = monitor;
        //
        bp.add(okButton);
        bp.add(cancelButton);
        //
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        selhelp.addActionListener(this);
        selstart.addActionListener(this);
        selsplash.addActionListener(this);
        selCdRoot.addActionListener(this);
        //
        cdRootPanel.add(cdRootLbl);
        cdRootPanel.add(cdRootField);
        cdRootPanel.add(selCdRoot);
        //
        splashPanel.add(splashLbl);
        splashPanel.add(splashField);
        splashPanel.add(selsplash);
        //
        startPanel.add(startLbl);
        startPanel.add(startField);
        startPanel.add(selstart);
        //
        helpPanel.add(helpLbl);
        helpPanel.add(helpField);
        helpPanel.add(selhelp);
        //
        copyPanel.add(copyAllBx);
        //
        ArrayList<DocSearcherIndex> cdIdxsArray = monitor.getCdArrayList();
        int numCDIdxs = cdIdxsArray.size();
        idxsPanels = new JPanel[numCDIdxs];
        cdIdxs = new JCheckBox[numCDIdxs];
        DocSearcherIndex di;
        //
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        //
        cdIdxsPanel.setLayout(new GridLayout(1, numCDIdxs));
        gridbaglayout = new GridBagLayout();
        gridbagconstraints = new GridBagConstraints();
        cdIdxsPanel.setLayout(gridbaglayout);
        //
        for (int i = 0; i < numCDIdxs; i++) {
            di = cdIdxsArray.get(i);
            cdIdxs[i] = new JCheckBox(di.getName());
            idxsPanels[i] = new JPanel();
            idxsPanels[i].add(cdIdxs[i]);
            //
            gridbagconstraints.fill = 1;
            gridbagconstraints.insets = new Insets(1, 1, 1, 1);
            gridbagconstraints.gridx = 0;
            gridbagconstraints.gridy = i;
            gridbagconstraints.gridwidth = 1;
            gridbagconstraints.gridheight = 1;
            gridbagconstraints.weightx = 0.0D;
            gridbagconstraints.weighty = 0.0D;
            gridbaglayout.setConstraints(idxsPanels[i], gridbagconstraints);
            cdIdxsPanel.add(idxsPanels[i]);
        }
        cdIdxsPane = new JScrollPane(cdIdxsPanel);
        cdIdxsPane.setPreferredSize(new Dimension(380, 220));
        cdWHolePanel.setLayout(new BorderLayout());
        cdWHolePanel.setBorder(new TitledBorder(SEL_CD_IDXS));
        cdWHolePanel.add(cdIdxsPane, BorderLayout.CENTER);
        //
        basicPanel.setLayout(new BorderLayout());
        basicPanel.add(cdRootPanel, BorderLayout.NORTH);
        basicPanel.add(cdWHolePanel, BorderLayout.CENTER);
        basicPanel.add(copyPanel, BorderLayout.SOUTH);
        //
        advancedPanel.setLayout(new BorderLayout());
        advancedPanel.add(splashPanel, BorderLayout.NORTH);
        advancedPanel.add(startPanel, BorderLayout.CENTER);
        advancedPanel.add(helpPanel, BorderLayout.SOUTH);
        //
        JPanel advPW = new JPanel();
        advPW.add(advancedPanel);
        //
        tabbedPane.addTab(Messages.getString("DocSearch.basicCdOpts"), null, basicPanel, Messages.getString("DocSearch.basicCdOptsTip"));
        tabbedPane.addTab(Messages.getString("DocSearch.advancedCdOpts"), null, advPW, Messages.getString("DocSearch.advancedCdOptsTip"));
        //
        panels = new JPanel[numPanels];
        for (int i = 0; i < numPanels; i++)
            panels[i] = new JPanel();
        panels[0].add(tabbedPane);
        panels[1].add(bp);
        // now for the gridbag
        getContentPane().setLayout(new GridLayout(1, numPanels));
        gridbaglayout = new GridBagLayout();
        gridbagconstraints = new GridBagConstraints();
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
        copyAllBx.setSelected(true);
    }


    @Override
	public void actionPerformed(ActionEvent actionevent) {
        String s = actionevent.getActionCommand();

        if (s.equals(OK)) {
            if (getCDIdxList().isEmpty()) {
                monitor.showMessage(I18n.getString("error"), Messages.getString("DocSearch.chooseAtLeastOneCDIdx"));
            }
            else {
                returnBool = true;
                this.setVisible(false);
            }
        }
        else if (s.equals(SEL_CD_ROOT_DIR)) {
            JFileChooser fdo = new JFileChooser();
            fdo.setCurrentDirectory(new File(fEnv.getUserHome()));
            fdo.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fdo.setFileFilter(new SwingFolderFilter());
            int fileGotten = fdo.showDialog(this, SELECT);
            if (fileGotten == JFileChooser.APPROVE_OPTION) {
                File file = fdo.getSelectedFile();
                String fNa = file.toString();
                cdRootField.setText(fNa);
            }
        }
        else if (s.equals(SEL_SPLASH_IMAGE)) {
            JFileChooser fdo = new JFileChooser();
            fdo.setCurrentDirectory(new File(fEnv.getUserHome()));
            fdo.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fdo.setFileFilter(new GifFilter());
            int fileGotten = fdo.showDialog(this, SELECT);
            if (fileGotten == JFileChooser.APPROVE_OPTION) {
                File file = fdo.getSelectedFile();
                String fNa = file.toString();
                splashField.setText(fNa);
            }
        }
        else if (s.equals(SEL_START_PAGE)) {
            JFileChooser fdo = new JFileChooser();
            fdo.setCurrentDirectory(new File(fEnv.getUserHome()));
            fdo.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fdo.setFileFilter(new WebFilter());
            int fileGotten = fdo.showDialog(this, SELECT);
            if (fileGotten == JFileChooser.APPROVE_OPTION) {
                File file = fdo.getSelectedFile();
                String fNa = file.toString();
                startField.setText(fNa);
            }
        }
        else if (s.equals(SEL_HELP_PAGE)) {
            JFileChooser fdo = new JFileChooser();
            fdo.setCurrentDirectory(new File(fEnv.getUserHome()));
            fdo.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fdo.setFileFilter(new WebFilter());
            int fileGotten = fdo.showDialog(this, SELECT);
            if (fileGotten == JFileChooser.APPROVE_OPTION) {
                File file = fdo.getSelectedFile();
                String fNa = file.toString();
                helpField.setText(fNa);
            }
        }
        else if (s.equals(CANCEL)) {
            returnBool = false;
            this.setVisible(false);
        }
    }


    public ArrayList<DocSearcherIndex> getCDIdxList() {
        int numToCheck = cdIdxs.length;
        ArrayList<DocSearcherIndex> returnArray = new ArrayList<DocSearcherIndex>();
        ArrayList<DocSearcherIndex> monIdxs = monitor.getCdArrayList();
        for (int i = 0; i < numToCheck; i++) {
            if (cdIdxs[i].isSelected()) {
                returnArray.add(monIdxs.get(i));
            }
        }

        return returnArray;
    }


    public void setFields(String helpStr, String splashStr, String startStr, String cdRootDirName) {
        helpField.setText(helpStr);
        splashField.setText(splashStr);
        startField.setText(startStr);
        cdRootField.setText(cdRootDirName);
    }


    public boolean confirmed() {
        return returnBool;
    }


    public boolean getCopyAllFiles() {
        return copyAllBx.isSelected();
    }


    public String getCdRootDirName() {
        return cdRootField.getText();
    }


    public String getSplashImageFileName() {
        return splashField.getText();
    }


    public String getStartPageFileName() {
        return startField.getText();
    }


    public String getHelpPageFileName() {
        return helpField.getText();
    }
}
