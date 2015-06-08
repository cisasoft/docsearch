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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.jab.docsearch.DocSearch;
import org.jab.docsearch.utils.I18n;
import org.jab.docsearch.utils.Messages;

/**
 * Class SpiderDialog
 *
 * @version $Id: SpiderDialog.java 172 2012-09-14 15:24:32Z henschel $
 */
public class SpiderDialog extends JDialog implements ActionListener {
    /**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -5900093197738517217L;

	private final DocSearch monitor;
    static final String SPIDER = Messages.getString("DocSearch.spider");
    static final String CANCEL = Messages.getString("DocSearch.btnCancel");
    static final String BROWSE = Messages.getString("DocSearch.btnBrowseFiles");
    static final String URL_LBL = Messages.getString("DocSearch.urlLbl");
    static final String MAX_DOCS_TO_FIND = Messages.getString("DocSearch.maxDocsToSpider");
    static final String SPIDER_TIPS = Messages.getString("DocSearch.msgSpiderTips");
    static final String MISSING_SPIDER_URL = Messages.getString("DocSearch.missingUrlSpider");
    static final String PLS_PROVIDE_URL = Messages.getString("DocSearch.plsProvSpiderUrl");
    static final String ERROR = I18n.getString("error");
    static final String SPIDER_DESC = Messages.getString("DocSearch.spiderDesc");
    static final String SPIDER_DESC_MISSING = Messages.getString("DocSearch.spiderDescMissing");
    static final String PROVIDE_A_NUM_FOR_MAX_URLS = Messages.getString("DocSearch.provNumForMaxUrls");
    JButton okButton = new JButton(SPIDER);
    JButton cancelButton = new JButton(CANCEL);
    JLabel urlLabel = new JLabel(URL_LBL);
    JLabel descLabel = new JLabel(SPIDER_DESC);
    JTextField urlField = new JTextField(19);
    JTextField descField = new JTextField(19);
    JTextField maxUrlsField = new JTextField(9);
    JLabel maxLabel = new JLabel(MAX_DOCS_TO_FIND);
    JPanel bp = new JPanel();
    boolean confirmed = false;
    String spiDesc = "";
    int maxDx = 1000;


    public SpiderDialog(DocSearch monitor, String title, boolean modal) {
        super(monitor, title, modal);
        this.monitor = monitor;
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        maxUrlsField.setText("1000");
        bp.add(okButton);
        bp.add(cancelButton);
        //
        JPanel descPanel = new JPanel();
        descPanel.add(descLabel);
        descPanel.add(descField);
        //
        JPanel urlPanel = new JPanel();
        urlPanel.add(urlLabel);
        urlPanel.add(urlField);
        //
        JPanel maxPanel = new JPanel();
        maxPanel.add(maxLabel);
        maxPanel.add(maxUrlsField);
        //
        JPanel combinedPanel = new JPanel();
        combinedPanel.setLayout(new BorderLayout());
        combinedPanel.setBorder(new TitledBorder(SPIDER_TIPS));
        combinedPanel.add(descPanel, BorderLayout.NORTH);
        combinedPanel.add(urlPanel, BorderLayout.CENTER);
        combinedPanel.add(maxPanel, BorderLayout.SOUTH);
        //
        // load up the GUI
        //
        okButton.setMnemonic(KeyEvent.VK_O);
        okButton.setToolTipText(SPIDER);
        //
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.setToolTipText(CANCEL);
        // NOW PLACE THE GUI ONTO A GRIDBAG
        // put in the gridbag stuff
        getContentPane().setLayout(new GridLayout(1, 2));
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
        gridbagconstraints.weightx = 2.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(combinedPanel, gridbagconstraints);
        getContentPane().add(combinedPanel);

        //
        gridbagconstraints.fill = 1;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 1;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 2.0D;
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
        if (s.equals(SPIDER)) {
            confirmed = false;
            boolean hasURL = false;
            boolean hasMAX = false;
            boolean hasDesc = false;
            int maxNum = -1;
            String maxNumStr = maxUrlsField.getText().trim();
            String urlTyped = urlField.getText().trim();
            String descTyped = descField.getText().trim();
            if (descTyped.equals("")) {
                monitor.showMessage(ERROR, SPIDER_DESC_MISSING);
                hasDesc = false;
            } else {
                hasDesc = true;
                spiDesc = descTyped;
            }
            if (urlTyped.equals("")) {
                monitor.showMessage(ERROR + " : " + MISSING_SPIDER_URL, PLS_PROVIDE_URL);
                hasURL = false;
            } else
                hasURL = true;
            try {
                maxNum = Integer.parseInt(maxNumStr);
                if (maxNum > 0) {
                    hasMAX = true;
                    maxDx = maxNum;
                } else
                    monitor.showMessage(ERROR, PROVIDE_A_NUM_FOR_MAX_URLS);
            } catch (Exception eN) {
                eN.printStackTrace();
                maxNum = -1;
                hasMAX = false;
                monitor.showMessage(ERROR, PROVIDE_A_NUM_FOR_MAX_URLS);
            }
            /* now report results */
            if ((hasURL) && (hasDesc) && (hasMAX)) { // file exists
                confirmed = true;
                this.setVisible(false);
            } else { // a URL_LBL
                confirmed = false;
            }
        } else if (s.equals(CANCEL)) {
            confirmed = false;
            this.setVisible(false);
        }
    }


    public int getMax() {
        return maxDx;
    }


    public String getDesc() {
        return spiDesc;
    }


    public String getUrlFieldText() {
        return urlField.getText();
    }


    public boolean getConfirmed() {
        return confirmed;
    }
}
