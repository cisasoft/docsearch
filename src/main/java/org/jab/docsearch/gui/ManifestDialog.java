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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.jab.docsearch.DocSearch;
import org.jab.docsearch.utils.Messages;

/**
 * Class ManifestDialog
 *
 * @version $Id: ManifestDialog.java 172 2012-09-14 15:24:32Z henschel $
 */
public class ManifestDialog extends JDialog implements ActionListener {
    static final String IMPORT = Messages.getString("DocSearch.importIndex");
    static final String CANCEL = Messages.getString("DocSearch.btnCancel");
    static final String SBD = Messages.getString("DocSearch.lblSrchdByDflt");
    static final String DESCFORNEWIDX = Messages.getString("DocSearch.lblDescForNewIdx");
    static final String MSGDESC = Messages.getString("DocSearch.errIdxMsgDesc");
    static final String PROVIDXDESCPLS = Messages.getString("DocSearch.errIdxMsgDescProv");
    final static String[] updateChoices = { Messages.getString("DocSearch.optWhenISaySo"), Messages.getString("DocSearch.optDuringStartup"), Messages.getString("DocSearch.optIdxGtOne"), Messages.getString("DocSearch.optIdxGtFive"), Messages.getString("DocSearch.optIdxGtThiry"), Messages.getString("DocSearch.optIdxGtSixty"), Messages.getString("DocSearch.optIdxGtNintey"), Messages.getString("DocSearch.optIdxGtOneEighty"), Messages.getString("DocSearch.optIdxGtYr") };
    DocSearch monitor;
    JButton okButton = new JButton(IMPORT);
    JButton cancelButton = new JButton(CANCEL);
    JPanel descPanel = new JPanel();
    JLabel desLabel = new JLabel(DESCFORNEWIDX);
    JTextField descField = new JTextField(25);
    JPanel checkPanels = new JPanel();
    JCheckBox sbdBox = new JCheckBox(SBD);
    JCheckBox isWebBox = new JCheckBox(Messages.getString("DocSearch.lblIsWebIdx"));
    JLabel updateLabel = new JLabel(Messages.getString("DocSearch.lblUpdPol"));
    JPanel indexFreqPanel = new JPanel();
    JComboBox indexFreq = new JComboBox(updateChoices);
    JLabel dirLabel = new JLabel(Messages.getString("DocSearch.lblIdxDescImp"));
    JPanel bp = new JPanel();
    public boolean confirmed = false;
    JPanel propsPanel = new JPanel();


    public ManifestDialog(DocSearch monitor, String title, boolean modal) {
        super(monitor, title, modal);
        this.monitor = monitor;
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        indexFreqPanel.add(updateLabel);
        indexFreqPanel.add(indexFreq);
        descPanel.add(desLabel);
        descPanel.add(descField);
        checkPanels.add(sbdBox);
        checkPanels.add(isWebBox);
        propsPanel.setLayout(new BorderLayout());
        propsPanel.setBorder(new TitledBorder(Messages.getString("DocSearch.idxProps")));
        propsPanel.add(descPanel, BorderLayout.NORTH);
        propsPanel.add(checkPanels, BorderLayout.CENTER);
        propsPanel.add(indexFreqPanel, BorderLayout.SOUTH);
        bp.add(cancelButton);
        bp.add(okButton);
        // load up the GUI
        okButton.setMnemonic(KeyEvent.VK_I);
        okButton.setToolTipText(IMPORT);
        //
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.setToolTipText(CANCEL);
        //
        // NOW PLACE THE GUI ONTO A GRIDBAG
        // put in the gridbag stuff
        getContentPane().setLayout(new GridLayout(3, 1));
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
        gridbagconstraints.weightx = 1.0D;
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
        gridbagconstraints.weighty = 1.0D;
        gridbaglayout.setConstraints(propsPanel, gridbagconstraints);
        getContentPane().add(propsPanel);

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
        if (s.equals(IMPORT)) {
            if (descField.getText().trim().equals("")) {
                monitor.showMessage(MSGDESC, PROVIDXDESCPLS);
            } else {
                confirmed = true;
                this.setVisible(false);
            }
        } else if (s.equals(CANCEL)) {
            confirmed = false;
            this.setVisible(false);
        }
    }


    public boolean getConfirmed() {
        return confirmed;
    }


    public boolean webBoxSelected() {
        return isWebBox.isSelected();
    }


    public boolean sbdBoxSelected() {
        return sbdBox.isSelected();
    }


    public String getDescFieldText() {
        return descField.getText();
    }


    public int indexFreqIdx() {
        return indexFreq.getSelectedIndex();
    }
}
