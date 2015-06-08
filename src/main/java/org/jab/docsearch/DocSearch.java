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
package org.jab.docsearch;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Layout;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeFilter;
import org.apache.lucene.search.Searcher;
import org.jab.docsearch.constants.FileType;
import org.jab.docsearch.constants.OSType;
import org.jab.docsearch.filters.FolderFilter;
import org.jab.docsearch.filters.GenericFilter;
import org.jab.docsearch.gui.CdAssistantDialog;
import org.jab.docsearch.gui.DocSplashViewer;
import org.jab.docsearch.gui.DsProperties;
import org.jab.docsearch.gui.ImportDialog;
import org.jab.docsearch.gui.JComponentVista;
import org.jab.docsearch.gui.ManageIndexesDialog;
import org.jab.docsearch.gui.ManifestDialog;
import org.jab.docsearch.gui.MessageConfirmRunner;
import org.jab.docsearch.gui.MessageRunner;
import org.jab.docsearch.gui.NewBookmarkDialog;
import org.jab.docsearch.gui.NewIndexDialog;
import org.jab.docsearch.gui.ProgressPanel;
import org.jab.docsearch.gui.SpiderDialog;
import org.jab.docsearch.spider.LinkFinder;
import org.jab.docsearch.threads.EmailThread;
import org.jab.docsearch.threads.GuiThread;
import org.jab.docsearch.threads.ThreadedSearch;
import org.jab.docsearch.utils.ArchiveMetaData;
import org.jab.docsearch.utils.DateTimeUtils;
import org.jab.docsearch.utils.DocTypeHandler;
import org.jab.docsearch.utils.DocTypeHandlerUtils;
import org.jab.docsearch.utils.FileTypeUtils;
import org.jab.docsearch.utils.FileUtils;
import org.jab.docsearch.utils.I18n;
import org.jab.docsearch.utils.LogAnalysis;
import org.jab.docsearch.utils.Logging;
import org.jab.docsearch.utils.Messages;
import org.jab.docsearch.utils.MetaReport;
import org.jab.docsearch.utils.NoticeLevel;
import org.jab.docsearch.utils.SimpleBookmark;
import org.jab.docsearch.utils.Table;
import org.jab.docsearch.utils.UnZippHandler;
import org.jab.docsearch.utils.Utils;
import org.jab.docsearch.utils.ZippHandler;

/**
 * This is main DocSearcher class that extends JFrame. It implements a web
 * browser like interface to a tailored Lucene search index.
 *
 * @see DocSearcherIndex DocSearcherIndex
 * @version $Id: DocSearch.java 176 2012-11-04 19:12:28Z henschel $
 */
public final class DocSearch extends JFrame implements ActionListener {
    /**
     * Serial
     */
    private static final long serialVersionUID = -4356725852543564364L;
    /**
     * Log4J
     */
    private Logger logger = null;
    /**
     * Environment
     */
    private final Environment env = Environment.getInstance();
    /**
     * FileEnvironment
     */
    private final FileEnvironment fEnv = FileEnvironment.getInstance();
    private boolean isWorking = false;
    private final String USER_NAME = System.getProperty("user.name");
    private final String cdRomDefaultHome = FileUtils.addFolder(System.getProperty("user.dir"), "cdrom_indexes");
    private final String cdRomIdxList = FileUtils.addFolder(cdRomDefaultHome, "cdrom_indexes_list.htm");
    private final String cdRomIdxDir = FileUtils.addFolder(cdRomDefaultHome, "indexes");
    // TODO move tempDir to FileEnvironment
    public String tempDir = System.getProperty("java.io.tmpdir");
    // private String cdRomTempIdxDir = FileUtils.addFolder(tempDir, "indexes");
    public String lafChosen = "";
    //
    private final boolean isCDSearchTool = isThisOnACd(cdRomDefaultHome);
    private boolean currentlySearching = false;
    //
    public String gateway = "";
    public String gatewayPwd = "";
    public String gatewayUser = "";
    public String emailFormat = "text";
    public String sendEmailNotice = "";
    public ArrayList<String> adminEmails = new ArrayList<String>();

    // TODO move this list into the Utils class
    public List<DocTypeHandler> handlerList;

    private final static String[] searchOpts = { "body_and_title", "title", "summary", "body", "keywords" };
    private final static String[] searchOptsLabels = {
        I18n.getString("search_in_body_and_tile"),
        I18n.getString("search_in_tile"),
        I18n.getString("search_in_summary"),
        I18n.getString("search_in_body"),
        I18n.getString("search_in_keywords") };
    /**
     * All filetype for searching
     */
    // TODO create java as filetype and insert a icon
    private final static String[] fileTypesToFind = {
        FileType.HTML.getSuffixes(),
        FileType.OPENDOCUMENT_TEXT.getSuffixes(),
        FileType.OO_WRITER.getSuffixes() + " " + FileType.OO_CALC.getSuffixes() + " " +
                FileType.OO_IMPRESS.getSuffixes() + " " + FileType.OO_DRAW.getSuffixes(),
        FileType.MS_WORD.getSuffixes() + " " + FileType.MS_EXCEL.getSuffixes(),
        FileType.RTF.getSuffixes(),
        FileType.PDF.getSuffixes(),
        FileType.TEXT.getSuffixes(),
        "java" };
    private final static String[] fileTypesToFindLabel = {
        I18n.getString("filetype.webpage_files"),
        I18n.getString("filetype.opendocument"),
        I18n.getString("filetype.openoffice_files"),
        I18n.getString("filetype.ms_office_files"),
        I18n.getString("filetype.rtf_files"),
        I18n.getString("filetype.pdf_files"),
        I18n.getString("filetype.text_files"),
        I18n.getString("filetype.sourcecode_java_files") };
    private final static String fileString = "file:///";
    private final static String pathSep = FileUtils.PATH_SEPARATOR;
    public final static GenericFilter wf = new GenericFilter();
    public final static FolderFilter ff = new FolderFilter();

    // printing vars
    private static final int kDefaultX = 740;
    private static final int kDefaultY = 480;
    // private static final int prefScale = 0;

    //
    private final String htmlTag;
    private final String wordTag;
    private final String excelTag;
    private final String pdfTag;
    private final String textTag;
    private final String rtfTag;
    private final String ooImpressTag;
    private final String ooWriterTag;
    private final String ooCalcTag;
    private final String ooDrawTag;
    private final String openDocumentTextTag;
    private boolean loadExternal = false;
    private String defaultSaveFolder = "";
    private String blankFile = "";
    private String lastSearch = I18n.getString("init_last_search");
    private boolean hasErr = false;
    private boolean isLoading = true;
    private String errString = I18n.getString("error_unknown");

    // TODO move this files to FileEnvironment or remove this variables if not
    // used (oo after rewrite parsing)
    public String rtfTextFile = FileUtils.addFolder(tempDir, "temp_rtf_file_" + USER_NAME + ".txt");
    public String htmlTextFile = FileUtils.addFolder(tempDir, "temp_html_file_" + USER_NAME + ".txt");
    public String ooTextFile = FileUtils.addFolder(tempDir, "temp_oo_file_" + USER_NAME + ".xml");
    public String ooMetaTextFile = FileUtils.addFolder(tempDir, "temp_oo_meta_file_" + USER_NAME + ".xml");
    public String ooTextOnlyFile = FileUtils.addFolder(tempDir, "temp_oo_text_file_" + USER_NAME + ".txt");

    // GUI items
    private String curStatusString = I18n.getString("ds.isloading");
    private JMenu bookMarkMenu;
    private JButton buttonStop;
    private final JComboBox searchField;
    private final JLabel searchLabel;
    private final JButton searchButton;
    private final int numPanels = 1;
    private final String[] colors;
    private final JScrollPane scrollPane;
    private final JEditorPane editorPane;
    private boolean hasStartPage = false;
    private final String startPageString;
    private final String helpPageString;
    private JComponentVista vista;

    private final JRadioButton keywords;
    private final JRadioButton phrase;
    private final JComboBox searchIn;
    private final JCheckBox useType;
    private final JComboBox fileType;
    private final JCheckBox useSize;
    private final JTextField sizeToField;
    private final JTextField sizeFromField;
    private final ProgressPanel pPanel;
    private final ButtonGroup bg;
    private long maxFileSizeInt = 1000000;
    public String curPage = "home"; // home always means the last searchresults
    private ArrayList<DocSearcherIndex> indexes = new ArrayList<DocSearcherIndex>();
    private final ArrayList<SimpleBookmark> bookmarksList = new ArrayList<SimpleBookmark>();
    private final JLabel dirLabel = new JLabel(I18n.getString("status_up"));

    // GUI items for advanced searching
    private final JCheckBox useDate;
    private final JTextField fromField;
    private final JTextField toField;
    private final CheckBoxListener cbl;
    private final JPanel authorPanel;
    private final JCheckBox useAuthor;
    private final JTextField authorField;
    private int maxNumHitsShown = 250;

    public Index idx;

    // STRING CONSTANTS
    public static final String dsNotIdxdMeta = Messages.getString("DocSearch.nonMetaSpiders");
    public static final String dsCkgFoUpdtsToDoc = Messages.getString("DocSearch.ckgFoUpdts");
    public static final String ckFoUpdaTo = Messages.getString("DocSearch.ckForUpdatesTo");
    public static final String dsErrPrint = Messages.getString("DocSearch.errPrinting");
    public static final String dsErrIdxg = Messages.getString("DocSearch.errIdxg");
    public static final String dsErrObtSi = Messages.getString("DocSearch.errObtSize");
    public static final String dsUpdts = Messages.getString("DocSearch.updates");
    public static final String idxFldr = Messages.getString("DocSearch.idxFldr");
    public static final String dsFSE = Messages.getString("DocSearch.errFilSave");
    public static final String dsWasSaved = Messages.getString("DocSearch.wasSaved");
    public static final String dsErrParseNums = Messages.getString("DocSearch.errParseNumFs");
    public static final String dsSrchStr = Messages.getString("DocSearch.srchStr");
    public static final String dsNumDelFiles = Messages.getString("DocSearch.deletedFiles");
    public static final String dsNumUnchangedFiles = Messages.getString("DocSearch.unchangedFiles");
    public static final String dsNumchangedFiles = Messages.getString("DocSearch.changedFiles");
    public static final String dsMxNumHits = Messages.getString("DocSearch.maxNumHits");
    public static final String dsFailIdxDocs = Messages.getString("DocSearch.fldUpdates");
    public static final String dsTtlDxInIdx = Messages.getString("DocSearch.ttlDox");
    public static final String dsErrParseScore = Messages.getString("DocSearch.errParseScore");
    public static final String dsWasNotSearched = Messages.getString("DocSearch.wns");
    public static final String dsTotHits = Messages.getString("DocSearch.totHits");
    public static final String dsErrPrfSrch = Messages.getString("DocSearch.errPerfSrch");
    public static final String dsCrptdIdx = Messages.getString("DocSearch.crptdIdx");
    public static final String dsMkIdx = Messages.getString("DocSearch.makeIdx");
    public static final String dsErrLdgFi = Messages.getString("DocSearch.errLdgFi");
    public static final String dsErrSetPa = Messages.getString("DocSearch.errSetPa");
    public static final String dsErrLdgPa = Messages.getString("DocSearch.errLdgPa");
    public static final String dsErrSaFi = Messages.getString("DocSearch.errSaveFi");
    public static final String dsErrIdxgFi = Messages.getString("DocSearch.errIdxng");
    public static final String dsFndFldr = Messages.getString("DocSearch.foundFldr");
    public static final String dsIdxCrtd = Messages.getString("DocSearch.idxCreated");
    public static final String dsIdxsRblt = Messages.getString("DocSearch.idxsReblt");
    public static final String dsBknLink = Messages.getString("DocSearch.bknLink");
    public static final String lnkNoChanges = Messages.getString("DocSearch.nonCHangedLink");
    public static final String dsReIdxgLnk = Messages.getString("DocSearch.reIdxgLnk");
    public static final String dsSpdrUpdteComp = Messages.getString("DocSearch.dsSpiderUpdtComp");
    public static final String dsLkngFoLnx = Messages.getString("DocSearch.lookingForLinks");
    public static final String dsSpiderNewUrl = Messages.getString("DocSearch.dsSpiderNewUrl");
    public static final String dsRmvgIdxFis = Messages.getString("DocSearch.rmvgIdxFis");
    private String defaultHndlr = "";


    /**
     * Method main: start method
     *
     * @param args
     */
    public static void main(String[] args) {

        // init logging
        Logging logging = new Logging();
        logging.init();

        // parse params
        boolean hasCommands = false;
        String commandString = null;
        String indexString = null;
        boolean enableLogfile = false;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (arg.startsWith("enable_logfile")) {
                enableLogfile = true;
            }
            else {
                if (commandString == null) {
                    hasCommands = true;
                    commandString = arg;
                }
                else {
                    indexString = arg;
                }
            }
        }

        // add logfile logger if enable_logfile option as startargument
        if (enableLogfile) {
            logging.addFileLogger();
        }

        // Environment
        Environment env = Environment.getInstance();
        // FileEnvironment
        FileEnvironment fEnv = FileEnvironment.getInstance();

        // splash
        DocSplashViewer splash = new DocSplashViewer("DocSearcher " + I18n.getString("ds.version") + " " + I18n.getString("ds.isloading"));
        if (!hasCommands) {
            splash.display();
        }

        // gets OS type
        env.setOSType(Utils.getOSType());

        // gets GUI mode
        if (hasCommands) {
            env.setGUIMode(false);
        }
        else {
            env.setGUIMode(true);
        }

        // gets CDROM dir
        env.setCDROMDir(Utils.getCDROMDir(env.getOSType()));

        // users home
        fEnv.setUserHome(Utils.getUserHome(env.getOSType(), System.getProperty("user.home")));

        final DocSearch sw = new DocSearch();
        splash.setMonitor(sw);

        sw.init();

        splash.close();

        if (!hasCommands) {
            sw.setVisible(true);
            sw.checkUpdates();
        }
        else {
            if ("update".equals(commandString) && indexString == null) {
                sw.checkUpdates();
                System.exit(0);
            }
            else {
                sw.doCommand(commandString, indexString);
            }
        }
    }


    /**
     * Constructor
     */
    public DocSearch() {
        super(I18n.getString("ds.windowtitle"));

        // get logger
        logger = Logger.getLogger(getClass().getName());

        //
        File testCDFi = new File(cdRomDefaultHome);
        Properties sys = new Properties(System.getProperties());
        if (testCDFi.exists()) {
            sys.setProperty("disableLuceneLocks", "true");
            logger.info("DocSearch() Disabling Lucene Locks for CDROM indexes");
        }
        else {
            sys.setProperty("disableLuceneLocks", "false");
        }

        //
        checkCDROMDir();
        defaultHndlr = getBrowserFile();
        loadSettings();

        //
        pPanel = new ProgressPanel("", 100L);
        pPanel.init();

        phrase = new JRadioButton(I18n.getString("label.phrase"));
        searchField = new JComboBox();
        searchIn = new JComboBox(searchOptsLabels);
        JLabel searchTypeLabel = new JLabel(I18n.getString("label.search_type"));
        JLabel searchInLabel = new JLabel(I18n.getString("label.search_in"));
        keywords = new JRadioButton(I18n.getString("label.keyword"));
        //
        searchLabel = new JLabel(I18n.getString("label.search_for"));
        searchButton = new JButton(I18n.getString("button.search"));
        searchButton.setActionCommand("ac_search");
        searchButton.setMnemonic(KeyEvent.VK_A);
        // TODO alt text to resource
        htmlTag = "<img src=\"" + fEnv.getIconURL(FileType.HTML.getIcon()) + "\" border=\"0\" alt=\"Web Page Document\">";
        wordTag = "<img src=\"" + fEnv.getIconURL(FileType.MS_WORD.getIcon()) + "\" border=\"0\" alt=\"MS Word Document\">";
        excelTag = "<img src=\"" + fEnv.getIconURL(FileType.MS_EXCEL.getIcon()) + "\" border=\"0\" alt=\"MS Excel Document\">";
        pdfTag = "<img src=\"" + fEnv.getIconURL(FileType.PDF.getIcon()) + "\" border=\"0\" alt=\"PDF Document\">";
        textTag = "<img src=\"" + fEnv.getIconURL(FileType.TEXT.getIcon()) + "\" border=\"0\" alt=\"Text Document\">";
        rtfTag = "<img src=\"" + fEnv.getIconURL(FileType.RTF.getIcon()) + "\" border=\"0\" alt=\"RTF Document\">";
        ooImpressTag = "<img src=\"" + fEnv.getIconURL(FileType.OO_IMPRESS.getIcon()) + "\" border=\"0\" alt=\"OpenOffice Impress Document\">";
        ooWriterTag = "<img src=\"" + fEnv.getIconURL(FileType.OO_WRITER.getIcon()) + "\" border=\"0\" alt=\"OpenOffice Writer Document\">";
        ooCalcTag = "<img src=\"" + fEnv.getIconURL(FileType.OO_CALC.getIcon()) + "\" border=\"0\" alt=\"OpenOffice Calc Document\">";
        ooDrawTag = "<img src=\"" + fEnv.getIconURL(FileType.OO_DRAW.getIcon()) + "\" border=\"0\" alt=\"OpenOffice Draw Document\">";
        openDocumentTextTag = "<img src=\"" + fEnv.getIconURL(FileType.OPENDOCUMENT_TEXT.getIcon()) + "\" border=\"0\" alt=\"OpenDocument Text Document\">";
        //
        idx = new Index(this);
        colors = new String[2];
        colors[0] = "ffeffa";
        colors[1] = "fdffda";
        if (env.isWebStart()) {
            startPageString = getClass().getResource("/" + FileEnvironment.FILENAME_START_PAGE_WS).toString();
            helpPageString = getClass().getResource("/" + FileEnvironment.FILENAME_HELP_PAGE_WS).toString();
            if (startPageString != null) {
                logger.debug("DocSearch() Start Page is: " + startPageString);
                hasStartPage = true;
            }
            else {
                logger.error("DocSearch() Start Page NOT FOUND where expected: " + startPageString);
            }
        }
        else {
            startPageString = FileUtils.addFolder(fEnv.getStartDirectory(), FileEnvironment.FILENAME_START_PAGE);
            helpPageString = FileUtils.addFolder(fEnv.getStartDirectory(), FileEnvironment.FILENAME_HELP_PAGE);
            File startPageFile = new File(startPageString);
            if (startPageFile.exists()) {
                logger.debug("DocSearch() Start Page is: " + startPageString);
                hasStartPage = true;
            }
            else {
                logger.error("DocSearch() Start Page NOT FOUND where expected: " + startPageString);
            }
        }

        defaultSaveFolder = FileUtils.addFolder(fEnv.getWorkingDirectory(), "saved_searches");
        searchField.setEditable(true);
        searchField.addItem("");

        bg = new ButtonGroup();
        bg.add(phrase);
        bg.add(keywords);
        keywords.setSelected(true);

        keywords.setToolTipText(I18n.getString("tooltip.keyword"));
        phrase.setToolTipText(I18n.getString("tooltip.phrase"));

        int iconInt = 2;
        searchField.setPreferredSize(new Dimension(370, 22));

        // application icon
        Image iconImage = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/ds.gif"));
        this.setIconImage(iconImage);

        // menu bar
        JMenuBar menuBar = createMenuBar();
        // add menu to frame
        setJMenuBar(menuBar);

        // tool bar
        JToolBar toolbar = createToolBar();

        editorPane = new JEditorPane("text/html", lastSearch);
        editorPane.setEditable(false);
        editorPane.addHyperlinkListener(new Hyperactive());
        if (hasStartPage) {
            try {
                editorPane.setContentType("text/html");
                if (setPage("home")) {
                    logger.info("DocSearch() loaded start page: " + startPageString);
                }
            }
            catch (Exception e) {
                editorPane.setText(lastSearch);
            }
        }
        else {
            logger.warn("DocSearch() no start page loaded");
        }

        scrollPane = new JScrollPane(editorPane);
        scrollPane.setPreferredSize(new Dimension(1024, 720));
        scrollPane.setMinimumSize(new Dimension(900, 670));
        scrollPane.setMaximumSize(new Dimension(1980, 1980));

        // create panels
        // add printing stuff
        vista = new JComponentVista(editorPane, new PageFormat());

        JPanel topPanel = new JPanel();
        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(searchButton);

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(searchTypeLabel);
        bottomPanel.add(keywords);
        bottomPanel.add(phrase);
        bottomPanel.add(searchInLabel);
        bottomPanel.add(searchIn);

        // GUI items for advanced searching
        useDate = new JCheckBox(I18n.getString("label.use_date_property"));
        fromField = new JTextField(11);
        JLabel fromLabel = new JLabel(I18n.getString("label.from"));
        JLabel toLabel = new JLabel(I18n.getString("label.to"));
        toField = new JTextField(11);
        cbl = new CheckBoxListener();
        authorPanel = new JPanel();
        useAuthor = new JCheckBox(I18n.getString("label.use_auth_property"));
        authorField = new JTextField(31);
        JLabel authorLabel = new JLabel(I18n.getString("label.author"));
        authorPanel.add(useAuthor);
        authorPanel.add(authorLabel);
        authorPanel.add(authorField);

        // combine stuff
        JPanel datePanel = new JPanel();
        datePanel.add(useDate);
        datePanel.add(fromLabel);
        datePanel.add(fromField);
        datePanel.add(toLabel);
        datePanel.add(toField);

        JPanel metaPanel = new JPanel();
        metaPanel.setLayout(new BorderLayout());
        metaPanel.setBorder(new TitledBorder(I18n.getString("label.date_and_author")));
        metaPanel.add(datePanel, BorderLayout.NORTH);
        metaPanel.add(authorPanel, BorderLayout.SOUTH);

        useDate.addActionListener(cbl);
        useAuthor.addActionListener(cbl);

        fromField.setText(DateTimeUtils.getLastYear());
        toField.setText(DateTimeUtils.getToday());
        authorField.setText(System.getProperty("user.name"));

        JPanel[] panels = new JPanel[numPanels];
        for (int i = 0; i < numPanels; i++) {
            panels[i] = new JPanel();
        }

        // add giu to panels
        panels[0].setLayout(new BorderLayout());
        panels[0].add(topPanel, BorderLayout.NORTH);
        panels[0].add(bottomPanel, BorderLayout.SOUTH);
        panels[0].setBorder(new TitledBorder(I18n.getString("label.search_critera")));
        searchButton.addActionListener(this);

        JPanel fileTypePanel = new JPanel();
        useType = new JCheckBox(I18n.getString("label.use_filetype_property"));
        useType.addActionListener(cbl);
        fileType = new JComboBox(fileTypesToFindLabel);
        JLabel fileTypeLabel = new JLabel(I18n.getString("label.find_only_these_filetypes"));
        fileTypePanel.add(useType);
        fileTypePanel.add(fileTypeLabel);
        fileTypePanel.add(fileType);

        JPanel sizePanel = new JPanel();
        useSize = new JCheckBox(I18n.getString("label.use_filesize_property"));
        useSize.addActionListener(cbl);
        // TODO l18n kbytes
        JLabel sizeFromLabel = new JLabel(I18n.getString("label.from") + " KByte");
        JLabel sizeToLabel = new JLabel(I18n.getString("label.to") + " KByte");
        sizeFromField = new JTextField(10);
        sizeFromField.setText("0");
        sizeToField = new JTextField(10);
        sizeToField.setText("100");
        sizePanel.add(useSize);
        sizePanel.add(sizeFromLabel);
        sizePanel.add(sizeFromField);
        sizePanel.add(sizeToLabel);
        sizePanel.add(sizeToField);

        JPanel sizeAndTypePanel = new JPanel();
        sizeAndTypePanel.setLayout(new BorderLayout());
        sizeAndTypePanel.setBorder(new TitledBorder(I18n.getString("label.filetype_and_size")));
        sizeAndTypePanel.add(fileTypePanel, BorderLayout.NORTH);
        sizeAndTypePanel.add(sizePanel, BorderLayout.SOUTH);

        // set up the tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab(I18n.getString("label.general"), null, panels[0], I18n.getString("tooltip.general_search_criteria"));
        tabbedPane.addTab(I18n.getString("label.date_and_author"), null, metaPanel, I18n.getString("tooltip.date_auth_options"));
        tabbedPane.addTab(I18n.getString("label.filetype_and_size"), null, sizeAndTypePanel, I18n.getString("tooltip.filetype_and_size_options"));

        // gridbag
        getContentPane().setLayout(new GridLayout(1, numPanels + iconInt + 1));
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        getContentPane().setLayout(gridbaglayout);

        gridbagconstraints.fill = GridBagConstraints.HORIZONTAL;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(toolbar, gridbagconstraints);
        getContentPane().add(toolbar);

        int start = 1;
        for (int i = 0; i < numPanels; i++) {
            if (i == 0) {
                gridbagconstraints.fill = GridBagConstraints.HORIZONTAL;
                gridbagconstraints.insets = new Insets(1, 1, 1, 1);
                gridbagconstraints.gridx = 0;
                gridbagconstraints.gridy = i + start;
                gridbagconstraints.gridwidth = 1;
                gridbagconstraints.gridheight = 1;
                gridbagconstraints.weightx = 1.0D;
                gridbagconstraints.weighty = 0.0D;
                gridbaglayout.setConstraints(tabbedPane, gridbagconstraints);
                getContentPane().add(tabbedPane);
            }
            else {
                gridbagconstraints.fill = GridBagConstraints.HORIZONTAL;
                gridbagconstraints.insets = new Insets(1, 1, 1, 1);
                gridbagconstraints.gridx = 0;
                gridbagconstraints.gridy = i + start;
                gridbagconstraints.gridwidth = 1;
                gridbagconstraints.gridheight = 1;
                gridbagconstraints.weightx = 1.0D;
                gridbagconstraints.weighty = 0.0D;
                gridbaglayout.setConstraints(panels[i], gridbagconstraints);
                getContentPane().add(panels[i]);
            }
        }

        // now add the results area
        gridbagconstraints.fill = GridBagConstraints.HORIZONTAL;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = iconInt;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 1.0D;
        gridbaglayout.setConstraints(scrollPane, gridbagconstraints);
        getContentPane().add(scrollPane);
        JPanel statusP = new JPanel();
        statusP.setLayout(new BorderLayout());
        statusP.add(dirLabel, BorderLayout.WEST);
        statusP.add(pPanel, BorderLayout.EAST);

        // now add the status label
        gridbagconstraints.fill = GridBagConstraints.HORIZONTAL;
        gridbagconstraints.insets = new Insets(1, 1, 1, 1);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = numPanels + iconInt;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 0.0D;
        gridbaglayout.setConstraints(statusP, gridbagconstraints);
        getContentPane().add(statusP);

        //
        File testArchDir = new File(fEnv.getArchiveDirectory());
        if (!testArchDir.exists()) {
            boolean madeDir = testArchDir.mkdir();
            if (!madeDir) {
                logger.warn("DocSearch() Error creating directory: " + fEnv.getArchiveDirectory());
            }
            else {
                logger.info("DocSearch() Directory created: " + fEnv.getArchiveDirectory());
            }
        }
        loadIndexes();

        // DocTypeHandler
        String handlersFiName;
        if (!isCDSearchTool) {
            handlersFiName = FileUtils.addFolder(fEnv.getWorkingDirectory(), DocTypeHandlerUtils.HANDLER_FILE);
        }
        else {
            handlersFiName = FileUtils.addFolder(cdRomDefaultHome, DocTypeHandlerUtils.HANDLER_FILE);
        }
        DocTypeHandlerUtils dthUtils = new DocTypeHandlerUtils();
        if (!FileUtils.fileExists(handlersFiName)) {
            logger.warn("DocSearch() Handlers file not found at: " + handlersFiName);
            handlerList = dthUtils.getInitialHandler(env);
        }
        else {
            handlerList = dthUtils.loadHandler(handlersFiName);
        }
    }


    /**
     * Creates menu bar
     *
     * @param menuBar  Menu bar
     */
    private JMenuBar createMenuBar() {

    	// menu bar
        JMenuBar menuBar = new JMenuBar();

        // ----- menu file

    	// menu item print
        JMenuItem menuItemPrint = new JMenuItem(I18n.getString("menuitem.print"));
        menuItemPrint.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK));
        menuItemPrint.setActionCommand("ac_print");
        menuItemPrint.addActionListener(this);

        // scale X
        JRadioButtonMenuItem scaleXRadioBut = new JRadioButtonMenuItem(I18n.getString("print_scale_width"));
        scaleXRadioBut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Event.CTRL_MASK));
        scaleXRadioBut.addActionListener(new ScaleXListener());
        // scale Y
        JRadioButtonMenuItem scaleYRadioBut = new JRadioButtonMenuItem(I18n.getString("print_scale_length"));
        scaleYRadioBut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK));
        scaleYRadioBut.addActionListener(new ScaleYListener());
        // scale fit
        JRadioButtonMenuItem scaleFitRadioBut = new JRadioButtonMenuItem(I18n.getString("print_scale_to_fit"));
        scaleFitRadioBut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Event.CTRL_MASK));
        scaleFitRadioBut.addActionListener(new ScaleFitListener());
        // scale half
        JRadioButtonMenuItem scaleHalfRadioBut = new JRadioButtonMenuItem(I18n.getString("print_scale_half"));
        scaleHalfRadioBut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, Event.CTRL_MASK));
        scaleHalfRadioBut.addActionListener(new ScaleHalfListener());
        // scale double
        JRadioButtonMenuItem scale2RadioBut = new JRadioButtonMenuItem(I18n.getString("print_scale_2x"));
        scale2RadioBut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Event.CTRL_MASK));
        scale2RadioBut.addActionListener(new Scale2Listener());
        // scale off
        JRadioButtonMenuItem scaleOffRadioBut = new JRadioButtonMenuItem(I18n.getString("print_scale_off"), true);
        scaleOffRadioBut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));

        ButtonGroup scaleSetGroup = new ButtonGroup();
        scaleSetGroup.add(scale2RadioBut);
        scaleSetGroup.add(scaleFitRadioBut);
        scaleSetGroup.add(scaleHalfRadioBut);
        scaleSetGroup.add(scaleOffRadioBut);
        scaleSetGroup.add(scaleXRadioBut);
        scaleSetGroup.add(scaleYRadioBut);

        // build complete menu print preferences
        JMenu menuPrintPref = new JMenu(I18n.getString("menuitem.print_preferences"), true);
        menuPrintPref.add(scaleXRadioBut);
        menuPrintPref.add(scaleYRadioBut);
        menuPrintPref.add(scaleFitRadioBut);
        menuPrintPref.add(scaleHalfRadioBut);
        menuPrintPref.add(scale2RadioBut);
        menuPrintPref.addSeparator();
        menuPrintPref.add(scaleOffRadioBut);

        // menu item exit
        JMenuItem menuItemExit = new JMenuItem(I18n.getString("menuitem.exit"));
        menuItemExit.setActionCommand("ac_exit");
        menuItemExit.addActionListener(this);

        // build complete menu file
        JMenu menuFile = new JMenu(I18n.getString("menu.file"));
        menuFile.add(menuItemPrint);
        menuFile.add(menuPrintPref);
        menuFile.addSeparator();
        menuFile.add(menuItemExit);

        // add menu to menu bar
        menuBar.add(menuFile);

        // ----- menu index

    	// menu item new
        JMenuItem menuItemNewIndex = new JMenuItem(I18n.getString("menuitem.new_index"));
        menuItemNewIndex.setActionCommand("ac_newindex");
        menuItemNewIndex.addActionListener(this);
        // menu item new spider
        JMenuItem menuItemNewSpiderIndex = new JMenuItem(I18n.getString("menuitem.new_spider_index"));
        menuItemNewSpiderIndex.setActionCommand("ac_newspiderindex");
        menuItemNewSpiderIndex.addActionListener(this);
        // menu item manage
        JMenuItem menuItemManageIndex = new JMenuItem(I18n.getString("menuitem.manage_indexes"));
        menuItemManageIndex.setActionCommand("ac_manageindex");
        menuItemManageIndex.addActionListener(this);
        // menu item rebuild
        JMenuItem menuItemRebuildIndex = new JMenuItem(I18n.getString("menuitem.rebuild_all_indexes"));
        menuItemRebuildIndex.setActionCommand("ac_rebuildindexes");
        menuItemRebuildIndex.addActionListener(this);
        // menu item import
        JMenuItem menuItemImportIndex = new JMenuItem(I18n.getString("menuitem.import_index"));
        menuItemImportIndex.setActionCommand("ac_importindex");
        menuItemImportIndex.addActionListener(this);

        // build complete menu index
        JMenu indexMenu = new JMenu(I18n.getString("menu.index"));
        indexMenu.add(menuItemNewIndex);
        indexMenu.add(menuItemNewSpiderIndex);
        indexMenu.add(menuItemManageIndex);
        indexMenu.add(menuItemRebuildIndex);
        indexMenu.addSeparator();
        indexMenu.add(menuItemImportIndex);

        // add menu to menu bar
        menuBar.add(indexMenu);

        // ----- menu bookmark

        // menu item add
        JMenuItem menuItemAddBookmark = new JMenuItem(I18n.getString("menuitem.add_bookmark"));
        menuItemAddBookmark.setActionCommand("ac_addbookmark");
        menuItemAddBookmark.addActionListener(this);
        // menu item delete all
        JMenuItem menuItemDeleteAll = new JMenuItem(I18n.getString("menuitem.delete_all_bookmarks"));
        menuItemDeleteAll.setActionCommand("ac_deleteallbookmarks");
        menuItemDeleteAll.addActionListener(this);

        // build complete menu index
        bookMarkMenu = new JMenu(I18n.getString("menu.bookmarks"));
        bookMarkMenu.add(menuItemAddBookmark);
        bookMarkMenu.add(menuItemDeleteAll);
        bookMarkMenu.addSeparator();

        // add menu to menu bar
        menuBar.add(bookMarkMenu);

        // ----- menu report

        // menu item metadata report
        JMenuItem menuItemMetadataReport = new JMenuItem(I18n.getString("menuitem.metadata_report"));
        menuItemMetadataReport.setActionCommand("ac_metadata_report");
        menuItemMetadataReport.addActionListener(this);
        // menu item servlet report
        JMenuItem menuItemServletReport = new JMenuItem(I18n.getString("menuitem.servlet_log_report"));
        menuItemServletReport.setActionCommand("ac_servlet_log_report");
        menuItemServletReport.addActionListener(this);

        // build complete menu report
        JMenu reportMenu = new JMenu(I18n.getString("menu.reports"));
        reportMenu.add(menuItemMetadataReport);
        reportMenu.add(menuItemServletReport);

        // add menu to menu bar
        menuBar.add(reportMenu);

        // ----- menu tools

        // menu item makr cd
        JMenuItem menuItemMakeCD = new JMenuItem(I18n.getString("menuitem.make_cd"));
        menuItemMakeCD.setActionCommand("ac_makecd");
        menuItemMakeCD.addActionListener(this);
        // menu item setting
        JMenuItem menuItemSetting = new JMenuItem(I18n.getString("menuitem.settings"));
        menuItemSetting.setActionCommand("ac_settings");
        menuItemSetting.addActionListener(this);

        // build complete menu report
        JMenu menuTool = new JMenu(I18n.getString("menu.tools"));
        menuTool.add(menuItemMakeCD);
        menuTool.addSeparator();
        menuTool.add(menuItemSetting);

        // add menu to menu bar
        menuBar.add(menuTool);

        // ----- menu help

        // menu item search tip
        JMenuItem menuItemSearchTip = new JMenuItem(I18n.getString("menuitem.search_tips"));
        menuItemSearchTip.setActionCommand("ac_search_tips");
        menuItemSearchTip.addActionListener(this);
        // menu item about
        JMenuItem menuItemAbout = new JMenuItem(I18n.getString("menuitem.about"));
        menuItemAbout.setActionCommand("ac_about");
        menuItemAbout.addActionListener(this);

        // build complete menu help
        JMenu menuHelp = new JMenu(I18n.getString("menu.help"));
        menuHelp.add(menuItemSearchTip);
        menuHelp.add(menuItemAbout);

        // add menu to menu bar
        menuBar.add(menuHelp);

        // finished
        return menuBar;
    }


    private JToolBar createToolBar() {

    	// tool bar
    	JToolBar toolBar = new JToolBar();

    	// file open
        JButton buttonOpen = new JButton(new ImageIcon(getClass().getResource("/icons/fileopen.png")));
        buttonOpen.setToolTipText(I18n.getString("tooltip.open"));
        buttonOpen.setActionCommand("ac_open");
        buttonOpen.addActionListener(this);
        buttonOpen.setMnemonic(KeyEvent.VK_O);
        buttonOpen.setEnabled(! env.isWebStart()); // disable in WebStart
        toolBar.add(buttonOpen);

        // file save
        JButton buttonSave = new JButton(new ImageIcon(getClass().getResource("/icons/filesave.png")));
        buttonSave.setToolTipText(I18n.getString("tooltip.save"));
        buttonSave.setActionCommand("ac_save");
        buttonSave.addActionListener(this);
        buttonSave.setMnemonic(KeyEvent.VK_S);
        buttonSave.setEnabled(! env.isWebStart()); // disable in WebStart
        toolBar.add(buttonSave);
        toolBar.addSeparator();

        // open browser
        JButton buttonBrowser = new JButton(new ImageIcon(getClass().getResource("/icons/html.png")));
        buttonBrowser.setToolTipText(I18n.getString("tooltip.open_in_browser"));
        buttonBrowser.setActionCommand("ac_openinbrowser");
        buttonBrowser.addActionListener(this);
        buttonBrowser.setMnemonic(KeyEvent.VK_E);
        buttonBrowser.setEnabled(! env.isWebStart()); // disable in WebStart
        toolBar.add(buttonBrowser);
        toolBar.addSeparator();

        // home
        JButton buttonHome = new JButton(new ImageIcon(getClass().getResource("/icons/home.png")));
        buttonHome.setToolTipText(I18n.getString("tooltip.home"));
        buttonHome.setActionCommand("ac_home");
        buttonHome.addActionListener(this);
        buttonHome.setMnemonic(KeyEvent.VK_H);
        toolBar.add(buttonHome);

        // refresh
        JButton buttonRefresh = new JButton(new ImageIcon(getClass().getResource("/icons/refresh.png")));
        buttonRefresh.setToolTipText(I18n.getString("tooltip.refresh"));
        buttonRefresh.setActionCommand("ac_refresh");
        buttonRefresh.addActionListener(this);
        buttonRefresh.setMnemonic(KeyEvent.VK_L);
        toolBar.add(buttonRefresh);
        toolBar.addSeparator();

        // result
        JButton buttonResult = new JButton(new ImageIcon(getClass().getResource("/icons/search_results.png")));
        buttonResult.setToolTipText(I18n.getString("tooltip.results"));
        buttonResult.setActionCommand("ac_result");
        buttonResult.addActionListener(this);
        buttonResult.setMnemonic(KeyEvent.VK_R);
        toolBar.add(buttonResult);
        toolBar.addSeparator();

        // bookmark
        JButton buttonBookMark = new JButton(new ImageIcon(getClass().getResource("/icons/bookmark.png")));
        buttonBookMark.setToolTipText(I18n.getString("tooltip.add_bookmark"));
        buttonBookMark.setActionCommand("ac_addbookmark");
        buttonBookMark.addActionListener(this);
        buttonBookMark.setMnemonic(KeyEvent.VK_M);
        toolBar.add(buttonBookMark);
        toolBar.addSeparator();

        // print
        JButton buttonPrint = new JButton(new ImageIcon(getClass().getResource("/icons/fileprint.png")));
        buttonPrint.setToolTipText(I18n.getString("tooltip.print"));
        buttonPrint.setActionCommand("ac_print");
        buttonPrint.addActionListener(this);
        buttonPrint.setMnemonic(KeyEvent.VK_P);
        toolBar.add(buttonPrint);
        toolBar.addSeparator();

        // setting
        JButton buttonSetting = new JButton(new ImageIcon(getClass().getResource("/icons/configure.png")));
        buttonSetting.setToolTipText(I18n.getString("tooltip.settings"));
        buttonSetting.setActionCommand("ac_settings");
        buttonSetting.addActionListener(this);
        buttonSetting.setMnemonic(KeyEvent.VK_HOME);
        toolBar.add(buttonSetting);
        toolBar.addSeparator();

        // stop
        buttonStop = new JButton(new ImageIcon(getClass().getResource("/icons/stop.png")));
        buttonStop.setToolTipText(I18n.getString("tooltip.stop"));
        buttonStop.setActionCommand("ac_stop");
        buttonStop.addActionListener(this);
        buttonStop.setMnemonic(KeyEvent.VK_X);
        toolBar.add(buttonStop);
        toolBar.addSeparator();

        //
        toolBar.setFloatable(false);

    	// finished
    	return toolBar;
    }

    /**
     * Method init
     */
    private void init() {
        //
        // GUI BUILDING
        // close window item
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowevent) {
                doExit();
            }
        });

        // center on the screen
        Dimension screenD = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = screenD.width;
        int screenHeight = screenD.height;
        setSize(kDefaultX, kDefaultY);
        int newX = 0;
        int newY = 0;
        if (screenWidth > kDefaultX) {
            newX = (screenWidth - kDefaultX) / 2;
        }

        if (screenHeight > kDefaultY) {
            newY = (screenHeight - kDefaultY) / 2;
        }

        if ((newX != 0) || (newY != 0)) {
            setLocation(newX, newY);
        }

        // now determine if we need to create an index
        if (!hasIndex() && !isCDSearchTool) {
            curStatusString = I18n.getString("init_index");
            String contentFolderS = FileUtils.addFolder(fEnv.getStartDirectory(), "content");
            File testCF = new File(contentFolderS);
            if (testCF.exists()) {
                try {
                    DocSearcherIndex di = new DocSearcherIndex(contentFolderS, "default index", true, 20, FileUtils.addFolder(fEnv.getIndexDirectory(), "default"), false, "", "", 0, fEnv.getArchiveDirectory());
                    createNewIndex(di, false);
                }
                catch (IOException ioe) {
                    showMessage(I18n.getString("error_creating_index"), ioe.toString());
                }
            }
        }

        // HANDLE ERRORS
        isLoading = false;
        if (hasErr) {
            showMessage(I18n.getString("error_init"), errString);
        }

        // set up our fields;
        cb();
        setIsWorking(false);
        listAllProps();
    }


    /**
     * Checks the policy for updating the DocSearcherIndexes and if it is time
     * for updating - starts that process
     */
    private void checkUpdates() {
        int numDis = indexes.size();
        if (numDis > 0) {
            setStatus(I18n.getString("please_wait...") + " " + I18n.getString("update_index"));
            Iterator<DocSearcherIndex> it = indexes.iterator();
            DocSearcherIndex di;
            int curDays = 0;
            while (it.hasNext()) {
                di = it.next();
                curDays = DateTimeUtils.getDaysOld(di.getLastIndexed());
                setStatus(ckFoUpdaTo + di.getName());
                switch (di.getIndexPolicy()) {
                    case 0: // When I say so
                        break;
                    case 1: // During Startup
                        idx.updateIndex(di);
                        break;
                    case 2: // When Index > 1 Day Old
                        if (curDays > 1) {
                            idx.updateIndex(di);
                        }
                        break;
                    case 3: // When Index > 5 Days old
                        if (curDays > 5) {
                            idx.updateIndex(di);
                        }
                        break;
                    case 4: // When Index > 30 Days Old
                        if (curDays > 30) {
                            idx.updateIndex(di);
                        }
                        break;
                    case 5: // When Index > 60 Days Old
                        if (curDays > 60) {
                            idx.updateIndex(di);
                        }
                        break;
                    case 6: // When Index > 90 Days Old
                        if (curDays > 90) {
                            idx.updateIndex(di);
                        }
                        break;
                    case 7: // When Index > 180 Days Old
                        if (curDays > 180) {
                            idx.updateIndex(di);
                        }
                        break;
                    case 8: // When Index > 365 Days Old
                        if (curDays > 365) {
                            idx.updateIndex(di);
                        }
                        break;
                    default: // whatever
                        break;
                }
            }
        }
        setStatus(I18n.getString("update_check_complete"));
    }


    /**
     * Handle's main GUI events
     */
    public void handleEventCommand(String s) {
        logger.debug("handleEventCommand('" + s + "') entered");

        try {
            // we run validation in a thread so as not to interfere
            // with repaints of GUI
            if (s.equals("ac_exit")) {
                doExit();
            }
            else if (s.equals("ac_makecd")) {
                doSearchableCdWiz();
            }
            else if (s.equals("ac_settings")) {
                doHandlers();
            }
            else if (s.equals("ac_newspiderindex")) {
                doNewSpiderIdx();
            }
            else if (s.equals("ac_refresh")) {
                setPage(curPage);
            }
            else if (s.equals("ac_metadata_report")) {
                doMetaReport();
            }
            else if (s.equals("ac_servlet_log_report")) {
                getSeachLogReport();
            }
            else if (s.equals("ac_about")) {
                showMessage(I18n.getString("windowtitle.about"), I18n.getString("version") + " " + I18n.getString("ds.version") + "\n\n" + I18n.getString("windowcontent.about"));
            }
            else if (s.equals("ac_open")) {
                doOpen();
            }
            else if (s.equals("ac_save")) {
                doSave();
            }
            else if (s.equals("ac_print")) {
            	doPrint();
            }
            else if (s.equals("ac_search_tips")) {
                showMessage(I18n.getString("windowtitle.search_tips"), I18n.getString("windowcontent.search_tips"));
            }
            else if (s.equals("ac_openinbrowser")) {
                doExternal(curPage);
            }
            else if (s.equals("ac_newindex")) {
                doNewIndex();
            }
            else if (s.equals("ac_rebuildindexes")) {
                rebuildIndexes();
            }
            else if (s.equals("ac_stop")) {
                doStop();
            }
            else if (s.equals("ac_search")) {
                doThreadedSearch();
            }
            else if (s.equals("ac_manageindex")) {
                doManageIndexes();
            }
            else if (s.equals("ac_importindex")) {
                getImportInfo();
            }
            else if (s.equals("ac_addbookmark")) {
                doBookmark();
            }
            else if (s.equals("ac_deleteallbookmarks")) {
                // TODO extra method
                bookmarksList.clear();
                File bmf = new File(fEnv.getBookmarkFile());
                boolean wasCleared = true;
                String errString = null;
                if (bmf.exists()) {
                    try {
                        if (!bmf.delete()) {
                            wasCleared = false;
                            errString = "Bookmarks not deleted";
                        }
                    }
                    catch (SecurityException se) {
                        logger.fatal("handleEventCommand() failed with SecurityException", se);
                        wasCleared = false;
                        errString = se.toString();
                    }
                }

                if (!wasCleared) {
                    showMessage(I18n.getString("error_remove_bookmarks"), errString);
                }
                else {
                    showMessage(I18n.getString("bookmarks_cleared"), I18n.getString("bookmarks_cleared_info"));
                }
            }
            else if (s.equals("ac_result")) {
                editorPane.setContentType("text/html");
                editorPane.setText(lastSearch);
                editorPane.select(0, 0);

                curPage = "results";
            }
            else if (s.equals("ac_home")) {
                editorPane.setContentType("text/html");
                if (hasStartPage) {
                    if (!setPage("home")) {
                        // error message
                        // TODO better error message
                        editorPane.setText(errString);
                    }

                    curPage = "home";
                }
                else {
                    editorPane.setText(lastSearch);
                    editorPane.select(0, 0);
                    curPage = "results";
                }
            }
            else {
                if (s.startsWith("file:/") || s.startsWith("http")) {
                    setPage(s);
                }
            }
        }
        catch (Exception e) {
            logger.error("handleEventCommand() action thread was stopped!", e);
        }
    }


    /**
     * Method actionPerformed
     *
     * @param actionevent
     */
    @Override
    public void actionPerformed(ActionEvent actionevent) {
        String a = actionevent.getActionCommand();
        GuiThread g = new GuiThread(this, a);
        g.start();
    }


    /**
     * Checks to see that a DocSearcherIndex has its lucene index folder
     *
     * @return true if the lucene index folder for the index exists
     */
    private boolean hasIndex() {
        boolean returnBool = true;
        File indexFolder = new File(fEnv.getIndexDirectory());
        if (!indexFolder.exists()) {
            returnBool = false;
        }
        else if (env.isGUIMode()) {
            setStatus(idxFldr + " " + fEnv.getIndexDirectory() + " " + I18n.getString("lower_exists"));
        }
        return returnBool;
    }


    /**
     * changes the label at the bottom of the frame to reflect ungoing progress,
     * etc...
     */
    public void setStatus(String toSet) {
        if (!isLoading && env.isGUIMode()) {
            dirLabel.setText(toSet);
        }
        else {
            System.out.println(toSet);
        }
    }


    /**
     * Display's a question (details) and returns a 1 if no or 0 if yes is
     * answered
     *
     * @return 0 for yes and 1 for no
     */
    public int getConfirmation(String details, String title) {
        int n = JOptionPane.showConfirmDialog(this, title, details, JOptionPane.YES_NO_OPTION);
        return n;
    }


    /**
     * Question dialog for yes or no that returns true for yes and false for no
     *
     * @return true for yes and false for no
     */
    @SuppressWarnings("unused")
    private boolean getConfirmationMessage(String title, String details) {
        MessageConfirmRunner mct = new MessageConfirmRunner(title, details, this);

        try {
            SwingUtilities.invokeAndWait(mct);

            if (mct.getReturnInt() == JOptionPane.YES_OPTION) {
                return true;
            }
        }
        catch (InterruptedException ie) {
            logger.fatal("getConfirmationMessage() failed with InterruptedException", ie);
        }
        catch (InvocationTargetException ite) {
            logger.fatal("getConfirmationMessage() failed with InvocationTargetException", ite);
        }

        return false;
    }


    /**
     * Displays a dialog for informational or error messages - invokes a
     * MessageRunner so as not to be on the dispatch (GUI) thread
     *
     * @see MessageRunner
     */
    public void showMessage(String title, String details) {
        MessageRunner mesThread = new MessageRunner(title, details, this);
        try {
            SwingUtilities.invokeLater(mesThread);
        }
        catch (Exception e) {
            logger.error("showMessage() failed", e);
        }
    }


    /**
     * Does the actual work for Displaying an informational dialog - invoked via
     * a MessageRunner so as not to be on the dispatch (GUI) thread
     *
     * @see MessageRunner
     */
    public void showMessageDialog(String title, String body) {
        if (!isLoading && env.isGUIMode()) {
            int messageType = JOptionPane.INFORMATION_MESSAGE;
            if (title.toLowerCase().indexOf(I18n.getString("lower_error")) != -1) {
                messageType = JOptionPane.ERROR_MESSAGE;
            }

            JOptionPane pane = new JOptionPane(body, messageType);
            JDialog dialog = pane.createDialog(this, title);
            dialog.setVisible(true);
        }
        else {
            logger.info("showMessageDialog() \n* * * " + title + " * * *\n\t" + body);
        }
    }


    /**
     * Method getSearchIndexes
     *
     * @return
     */
    @SuppressWarnings("unused")
    private String getSearchedIndexes() {
        StringBuffer rb = new StringBuffer();

        // iterate over the di s
        int numIndexes = 0;
        if (!indexes.isEmpty()) {
            numIndexes = indexes.size();

            // add the items
            Iterator<DocSearcherIndex> iterator = indexes.iterator();
            if (numIndexes > 0) {
                rb.append("<ul>");
            }
            while (iterator.hasNext()) {
                DocSearcherIndex curI = iterator.next();
                if (curI.getShouldBeSearched()) {
                    rb.append("<li><font color=\"blue\">");
                    rb.append(curI.getName());
                    rb.append("</font></li>");
                }
            }

            if (numIndexes > 0) {
                rb.append("</ul>");
            }
        }

        if (numIndexes == 0) {
            return "<p align=\"left\"><b>" + I18n.getString("none") + "</b></p>";
        }
        else {
            return rb.toString();
        }
    }


    /**
     * Get all hits from search result with filesize range
     *
     * @param hits
     *            Hits from search
     * @param minSize
     *            min filesize in kilobytes
     * @param maxSize
     *            max filesize in kilobytes
     * @return Arraylit with 2 other ArrayList. List 0 contains the documents
     *         and List 1 constain the Float score value
     */
    private ArrayList[] getHitsForFilesizeRange(Hits hits, int minSize, int maxSize) {
        ArrayList[] returnList = new ArrayList[2];
        returnList[0] = new ArrayList();
        returnList[1] = new ArrayList();

        // search hits with correct filesize
        for (int i = 0; i < hits.length(); i++) {
            try {
                int tempSize = Integer.parseInt(hits.doc(i).get(Index.FIELD_SIZE));
                if ((tempSize >= minSize) && (tempSize <= maxSize)) {
                    returnList[0].add(hits.doc(i));
                    returnList[1].add(Float.valueOf(hits.score(i)));
                }
            }
            catch (Exception e) {
                setStatus(dsErrObtSi + " " + e.toString());
            }
        }

        return returnList;
    }

    /**
     * Class Scale2Listener
     */
    public class Scale2Listener implements ActionListener {
        @Override
		public void actionPerformed(ActionEvent evt) {
            vista = new JComponentVista(editorPane, new PageFormat());
            vista.setScale(2.0, 2.0);
        }
    }

    /**
     * Class ScaleFitListener
     */
    public class ScaleFitListener implements ActionListener {
        @Override
		public void actionPerformed(ActionEvent evt) {
            vista = new JComponentVista(editorPane, new PageFormat());
            vista.scaleToFit(false);
        }
    }

    /**
     * Class ScaleHalfListener
     *
     * @author henschel
     *
     */
    public class ScaleHalfListener implements ActionListener {
        @Override
		public void actionPerformed(ActionEvent evt) {
            vista = new JComponentVista(editorPane, new PageFormat());
            vista.setScale(0.5, 0.5);
        }
    }

    /**
     * Class ScaleOffLister
     */
    public class ScaleOffListener implements ActionListener {
        @Override
		public void actionPerformed(ActionEvent evt) {
            vista = new JComponentVista(editorPane, new PageFormat());
        }
    }

    /**
     * Class ScaleXListener
     */
    public class ScaleXListener implements ActionListener {
        @Override
		public void actionPerformed(ActionEvent evt) {
            vista = new JComponentVista(editorPane, new PageFormat());
            vista.scaleToFitX();
        }
    }

    /**
     * Class ScaleYListener
     */
    public class ScaleYListener implements ActionListener {
        @Override
		public void actionPerformed(ActionEvent evt) {
            vista = new JComponentVista(editorPane, new PageFormat());
            vista.scaleToFitY();
        }
    }


    /**
     * Method setSearching
     *
     * @param toSet
     */
    public void setSearching(boolean toSet) {
        currentlySearching = toSet;
    }


    /**
     * Method doSearch
     *
     * @param searchText  Search text
     */
    public void doSearch(String searchText) {
        // TODO format date with locale

        setStatus(I18n.getString("please_wait...") + " " + I18n.getString("searching") + " --> " + searchText);
        setSearching(true);
        setIsWorking(true);
        int srchMaxPos = indexes.size() * 3;
        if (srchMaxPos > 0) {
            pPanel.setMaxPos(srchMaxPos);
        }
        synchronized (this) {
            ArrayList[] sizeList = null;
            if (phrase.isSelected()) {
                if (searchText.indexOf("\"") == -1) {
                    searchText = "\"" + searchText + "\"";
                }
            }
            // for each di - search and add the results
            int grandTotalHits = 0;
            int selectedFields = searchIn.getSelectedIndex();
            String sField = searchOpts[selectedFields];

            StringBuffer searchedIndexes = new StringBuffer();
            StringBuffer bodyBuf = new StringBuffer();

            StringBuffer hitBuf = new StringBuffer();
            hitBuf.append("<html><head><title>");
            hitBuf.append(I18n.getString("results_for_search"));
            hitBuf.append(' ');
            hitBuf.append(searchText);
            hitBuf.append("</title></head><body><h1 align=\"center\">");
            hitBuf.append(I18n.getString("results_for_search"));
            hitBuf.append(": ");
            hitBuf.append("<strong><font color=\"blue\">");
            hitBuf.append(searchText);
            hitBuf.append("</font></strong></h1>");
            if (env.isGUIMode()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("doSearch() search for '" + searchText + "'");
                }
            }
            else {
                System.out.println(I18n.getString("results_for_search") + ": " + searchText);
            }

            if (!indexes.isEmpty()) {
                try {
                    int curSrchPos = 0;

                    // add the items
                    Iterator<DocSearcherIndex> iterator = indexes.iterator();
                    while (iterator.hasNext()) {
                        curSrchPos++;
                        if (curSrchPos > 0) {
                            pPanel.setCurPos(curSrchPos);
                        }
                        DocSearcherIndex currentIndex = iterator.next();
                        boolean isCdRomIdx = currentIndex.isCdrom();
                        StringBuffer tempBuf = new StringBuffer();
                        if (currentIndex.getShouldBeSearched()) {
                            String findText = "";

                            // create searcher
                            Searcher searcher = new IndexSearcher(currentIndex.getIndexPath());
                            if ((searchText.indexOf("(") != -1) || (searchText.indexOf("[") != -1)) {
                                findText = searchText;
                            }
                            else {
                                // add body and title, or single field to query
                                if (selectedFields == 0) {
                                    findText = "+(" + Index.FIELD_BODY + ":(" + searchText + ") OR " + Index.FIELD_TITLE + ":(" + searchText + "))";
                                }
                                else {
                                    // TODO dont use sField directly, because it
                                    // is translated in other languages
                                    findText = "+" + sField + ":(" + searchText + ")";
                                }

                                // add author to query
                                if (useAuthor.isSelected()) {
                                    findText += " +" + Index.FIELD_AUTHOR + ":(" + authorField.getText() + ")";
                                }

                                // add filetype to query
                                if (useType.isSelected()) {
                                    findText += " +" + Index.FIELD_TYPE + ":(" + fileTypesToFind[fileType.getSelectedIndex()] + ")";
                                }
                            }
                            logger.debug("doSearch() query string '" + findText + "'");
                            setStatus(dsSrchStr + ": " + findText + "...");

                            // create query
                            QueryParser queryParser = new QueryParser(Index.FIELD_BODY, new StandardAnalyzer());
                            Query query = queryParser.parse(findText);

                            // check for date filter and search in index
                            Hits hits;
                            if (useDate.isSelected()) {
                                String dateFrom = DateTimeUtils.getDateStringForIndex(DateTimeUtils.getDateFromString(fromField.getText()));
                                String dateTo = DateTimeUtils.getDateStringForIndex(DateTimeUtils.getDateFromString(toField.getText()));
                                RangeFilter rangeFilter = new RangeFilter(Index.FIELD_MODDATE, dateFrom, dateTo, true, true);
                                if (logger.isDebugEnabled()) {
                                    logger.debug("doSearch() search with date range '" + rangeFilter + "'");
                                }

                                hits = searcher.search(query, rangeFilter);
                            }
                            else {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("doSearch() search without date range");
                                }

                                hits = searcher.search(query);
                            }

                            // check for search with filesize
                            int numHits;
                            if (useSize.isSelected()) {
                                int minFilesize = 1;
                                int maxFilesize = 2;
                                try {
                                    minFilesize = Integer.parseInt(sizeFromField.getText()) * 1024;
                                    maxFilesize = Integer.parseInt(sizeToField.getText()) * 1024;
                                }
                                catch (Exception e) {
                                    setStatus(dsErrParseNums + " " + e.toString());
                                }
                                sizeList = getHitsForFilesizeRange(hits, minFilesize, maxFilesize);
                                numHits = sizeList[0].size();
                            }
                            else {
                                numHits = hits.length(); // NOT A SIZE QUERY
                            }

                            searchedIndexes.append("<li> <font color=\"blue\">");
                            searchedIndexes.append(currentIndex.getName());
                            searchedIndexes.append("</font> (<b>");
                            searchedIndexes.append(numHits);
                            searchedIndexes.append("</b> ");
                            searchedIndexes.append(I18n.getString("documents"));
                            searchedIndexes.append(")</li>");
                            if (env.isGUIMode()) {
                                if (logger.isDebugEnabled()) {
                                    logger.debug("doSearch() Index: " + currentIndex.getName());
                                }
                            }
                            else {
                                System.out.println(I18n.getString("index") + ": " + currentIndex.getName());
                            }
                            grandTotalHits += numHits;
                            tempBuf.append("<p align=\"center\"><b>");
                            tempBuf.append(numHits);
                            tempBuf.append("</b> ");
                            tempBuf.append(I18n.getString("documents_found_in_index"));
                            tempBuf.append("<b> ");
                            tempBuf.append(currentIndex.getName());
                            tempBuf.append("</b></p>");

                            curSrchPos++;
                            if (curSrchPos > 0) {
                                pPanel.setCurPos(curSrchPos);
                            }
                            for (int i = 0; i < numHits; i++) {
                                if (i > maxNumHitsShown) {
                                    setStatus(dsMxNumHits + " (" + maxNumHitsShown + ") " + I18n.getString("exceeded") + " (" + numHits + ").");
                                    break;
                                }

                                // get document and score from result or special
                                // result
                                Document currentDocument;
                                float currentScore;

                                // filesize result?
                                if (useSize.isSelected()) {
                                    currentDocument = (Document) sizeList[0].get(i);
                                    currentScore = ((Float) sizeList[1].get(i)).floatValue();
                                }
                                else {
                                    currentDocument = hits.doc(i);
                                    currentScore = hits.score(i);
                                }

                                // title
                                String currentTitle = Utils.convertTextToHTML(currentDocument.get(Index.FIELD_TITLE));

                                // filesize
                                String currentFilesize = currentDocument.get(Index.FIELD_SIZE);

                                // path or url
                                String currentFile;
                                if (!currentIndex.getIsWeb()) {
                                    if (!isCdRomIdx) {
                                        currentFile = currentDocument.get(Index.FIELD_PATH);
                                    }
                                    else {
                                        currentFile = getCDROMPath(currentDocument.get(Index.FIELD_URL));
                                    }
                                }
                                else {
                                    currentFile = currentDocument.get(Index.FIELD_URL);
                                }

                                // type
                                String currentTypeStr = currentDocument.get(Index.FIELD_TYPE);
                                FileType currentType = FileType.fromValue(currentTypeStr);

                                // author
                                String currentAuthor = currentDocument.get(Index.FIELD_AUTHOR);
                                if ("".equals(currentAuthor)) {
                                    currentAuthor = I18n.getString("unknown");
                                }

                                // date
                                String currentDate = currentDocument.get(Index.FIELD_MODDATE);
                                if ("".equals(currentDate)) {
                                    currentDate = I18n.getString("unknown");
                                }
                                else {
                                    currentDate = DateTimeUtils.getDateParsedFromIndex(currentDate);
                                }

                                String currentSummary = Utils.convertTextToHTML(currentDocument.get(Index.FIELD_SUMMARY));

                                // add it to our page - doc size title score
                                tempBuf.append("<p align=\"left\">");
                                if (!currentIndex.getIsWeb()) {
                                    tempBuf.append("<a href=\"");
                                    tempBuf.append(fileString);
                                    tempBuf.append(currentFile);
                                    tempBuf.append("\">");
                                }
                                else {
                                    tempBuf.append("<a href=\"");
                                    tempBuf.append(currentFile);
                                    tempBuf.append("\">");
                                }
                                switch (currentType) {
                                    case HTML: // html
                                        tempBuf.append(htmlTag);
                                        break;
                                    case MS_WORD: // ms word
                                        tempBuf.append(wordTag);
                                        break;
                                    case MS_EXCEL: // ms excel
                                        tempBuf.append(excelTag);
                                        break;
                                    case PDF: // pdf
                                        tempBuf.append(pdfTag);
                                        break;
                                    case RTF: // rtf
                                        tempBuf.append(rtfTag);
                                        break;
                                    case OO_WRITER: // openoffice writer
                                        tempBuf.append(ooWriterTag);
                                        break;
                                    case OO_IMPRESS: // openoffice impress
                                        tempBuf.append(ooImpressTag);
                                        break;
                                    case OO_CALC: // openoffice calc
                                        tempBuf.append(ooCalcTag);
                                        break;
                                    case OO_DRAW: // openoffice draw
                                        tempBuf.append(ooDrawTag);
                                        break;
                                    case OPENDOCUMENT_TEXT: // opendocument text
                                        tempBuf.append(openDocumentTextTag);
                                        break;
                                    case TEXT:
                                        tempBuf.append(textTag);
                                        break;
                                    default:
                                        logger.error("doSearch() FileType." + currentType + " is not ok here!");
                                }
                                tempBuf.append("&nbsp;");
                                tempBuf.append(currentTitle);
                                tempBuf.append("</a><br>");
                                tempBuf.append(currentSummary);
                                tempBuf.append("<font color=\"green\"><br><em>");
                                tempBuf.append(currentDate);
                                tempBuf.append(", ");
                                tempBuf.append(Utils.getKStyle(currentFilesize));
                                tempBuf.append("bytes, ");
                                tempBuf.append(currentAuthor);
                                tempBuf.append(", <b>");
                                tempBuf.append(Utils.getPercentStringFromScore(currentScore));
                                tempBuf.append("</b></em></font><br><font color=\"gray\">");
                                tempBuf.append(currentFile);
                                tempBuf.append("</font>");
                                tempBuf.append("</p>");
                                if (env.isGUIMode()) {
                                    if (logger.isDebugEnabled()) {
                                        logger.debug("doSearch() \n\n* " + currentTitle + "\n" + currentSummary + "\n" + currentDate + ", " + Utils.getKStyle(currentFilesize) + "bytes, " + currentAuthor + ", " + Utils.getPercentStringFromScore(currentScore) + "\n" + currentFile);
                                    }
                                }
                                else {
                                    System.out.println("\n\n* " + currentTitle + "\n" + currentSummary + "\n" + currentDate + ", " + Utils.getKStyle(currentFilesize) + "bytes, " + currentAuthor + ", " + Utils.getPercentStringFromScore(currentScore) + "\n" + currentFile);
                                }
                            } // end for hits
                              // now add our results
                            curSrchPos++;
                            if (curSrchPos > 0) {
                                pPanel.setCurPos(curSrchPos);
                            }
                            // add the footer
                            bodyBuf.append(tempBuf);

                            // close index
                            searcher.close();
                        } // end if shouldbesearched
                        else {
                            tempBuf.append("<p align=\"left\">");
                            tempBuf.append(I18n.getString("index"));
                            tempBuf.append("  &nbsp; <b>");
                            tempBuf.append(currentIndex.getName());
                            tempBuf.append("</b>  &nbsp; ");
                            tempBuf.append(dsWasNotSearched);
                            tempBuf.append("</p>");
                            bodyBuf.append(tempBuf);
                        }
                    } // end while hasmore indexes

                    // finish up the page
                    hitBuf.append("<p align=\"left\"><strong>");
                    hitBuf.append(grandTotalHits);
                    hitBuf.append("</strong> ");
                    hitBuf.append(I18n.getString("documents_found_in_all_indexes"));
                    hitBuf.append("</p>");
                    hitBuf.append("<ul>");
                    hitBuf.append(searchedIndexes);
                    hitBuf.append("</ul>");
                    hitBuf.append(bodyBuf);
                    hitBuf.append("</body></html>");
                    if (env.isGUIMode()) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("doSearch() " + dsTotHits + ": " + grandTotalHits);
                        }
                    }
                    else {
                        System.out.println("\n" + dsTotHits + ": " + grandTotalHits);
                    }

                    // save last search
                    lastSearch = hitBuf.toString();

                    // show result
                    if (env.isGUIMode()) {
                        editorPane.setText(lastSearch);
                        editorPane.select(0, 0);

                        // add search text in combobox if not exist
                        int searchFieldCount = searchField.getItemCount();
                        boolean inThere = false;
                        for (int i = 1; i < searchFieldCount; i++) {
                            String tmpSrchTxt = (String) searchField.getItemAt(i);
                            if (tmpSrchTxt != null) {
                                if (tmpSrchTxt.equals(searchText)) {
                                    inThere = true;
                                }
                            }
                        }
                        // if search text new, than put it to searchfield and
                        // select them
                        if (!inThere) {
                            searchField.addItem(searchText);
                            searchField.setSelectedIndex(searchField.getItemCount() - 1);
                        }
                        vista = new JComponentVista(editorPane, new PageFormat());

                        // set current page to result
                        curPage = "results";
                    }
                    else {
                        // TODO check if here ouput for command line needed!
                    }
                }
                catch (IOException ioe) {
                    logger.fatal("doSearch() failed with IOException", ioe);
                    showMessage(dsErrPrfSrch, dsCrptdIdx + " :\n" + ioe.toString());
                }
                catch (NullPointerException npe) {
                    logger.fatal("doSearch() failed with NullPointerException", npe);
                    showMessage(dsErrPrfSrch, dsCrptdIdx + " :\n" + npe.toString());
                }
                catch (Exception e) {
                    logger.fatal("doSearch() failed with Exception", e);
                    showMessage(dsErrPrfSrch, e.toString());
                }
            }
            else {
                showMessage(dsErrPrfSrch, dsMkIdx);
            }
        }

        setStatus(I18n.getString("search_complete"));
        setSearching(false);
        setIsWorking(false);
        pPanel.reset();
    }


    /**
     * Load properties file.
     *
     * @param propertiesFile
     *            Properties file
     * @return Properties
     */
    private Properties loadProperties(String propertiesFile) {
        logger.debug("loadProperties('" + propertiesFile + "') entered");

        // cursor "wait"
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // read property file
        Properties props = new Properties();
        FileInputStream fileIn = null;
        try {
            File propsFile = new File(propertiesFile);
            if (propsFile.isFile()) {
                fileIn = new FileInputStream(propsFile);
                props.load(fileIn);
            }
            else {
                logger.error("loadProperties() " + propertiesFile + " isn't a file!");
            }
        }
        catch (IOException ioe) {
            logger.fatal("loadProperties() failed", ioe);
            showMessage(dsErrLdgFi, "\n" + propertiesFile + "\n\n : " + ioe.toString());
        }
        finally {
            IOUtils.closeQuietly(fileIn);
        }

        // cursor "default"
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        return props;
    }


    /**
     * Set the page.
     *
     * @param pageToSet
     *            Page name
     * @return True, if page is successfully set.
     */
    private boolean setPage(final String pageToSet) {
        logger.debug("setPage('" + pageToSet + "') entered");

        boolean returnBool = true;

        try {
            if (FileTypeUtils.isFileTypeHTML(pageToSet) || FileTypeUtils.isFileTypeText(pageToSet)) {
                editorPane.setContentType("text/html");
                editorPane.setPage(fileString + blankFile);
            }
        }
        catch (NullPointerException npe) {
            logger.fatal("setPage() failed", npe);
            setStatus(dsErrSetPa + " : " + npe.toString() + "\n" + pageToSet);
        }
        catch (IOException ioe) {
            logger.fatal("setPage() failed", ioe);
            setStatus(dsErrSetPa + " : " + ioe.toString() + "\n" + pageToSet);
        }

        // which page?
        if ("home".equals(pageToSet)) {
            setStatus(I18n.getString("loading") + " : *" + pageToSet + "*");
            editorPane.setContentType("text/html");
            if (hasStartPage) {
                try {
                    if (env.isWebStart()) {
                        editorPane.setPage(startPageString);
                    } else {
                        editorPane.setPage(fileString + startPageString);
                    }
                }
                catch (IOException ioe) {
                    logger.fatal("setPage() failed", ioe);
                    editorPane.setText(lastSearch);
                }
            }
            else {
                editorPane.setText(lastSearch);
            }
            editorPane.select(0, 0);
        }
        else if ("help".equals(pageToSet)) {
            setStatus(I18n.getString("loading") + " : *" + pageToSet + "*");
            editorPane.setContentType("text/html");
            if (hasStartPage) {
                try {
                    if (env.isWebStart()) {
                        editorPane.setPage(helpPageString);
                    } else {
                        editorPane.setPage(fileString + helpPageString);
                    }
                }
                catch (IOException ioe) {
                    logger.fatal("setPage() failed", ioe);
                    editorPane.setText(lastSearch);
                }
            }
            else {
                editorPane.setText(lastSearch);
            }
            editorPane.select(0, 0);
        }
        else if ("result".equals(pageToSet)) {
            setStatus(I18n.getString("loading") + " : *" + pageToSet + "*");
            editorPane.setContentType("text/html");
            editorPane.setText(lastSearch);
            editorPane.select(0, 0);
        }
        else {
            if (FileTypeUtils.isFileTypeHTML(pageToSet) || FileTypeUtils.isFileTypeText(pageToSet)) {
                if (FileTypeUtils.isFileTypeHTML(pageToSet)) {
                    editorPane.setContentType("text/html");
                }
                else if (FileTypeUtils.isFileTypeText(pageToSet)) {
                    editorPane.setContentType("text/plain");
                }

                try {
                    // set the page
                    if (pageToSet.startsWith("file:/")) {
                        String newPageToSet = pageToSet.substring(6, pageToSet.length());
                        editorPane.setPage(fileString + newPageToSet);
                    }
                    else if (pageToSet.startsWith("http:")) {
                        editorPane.setPage(pageToSet);
                    }
                    else if (pageToSet.startsWith("ftp")) {
                        editorPane.setPage(pageToSet);
                    }
                    else {
                        editorPane.setPage(fileString + pageToSet);
                    }
                    setStatus(I18n.getString("loaded") + " " + pageToSet);
                }
                catch (IOException ioe) {
                    logger.fatal("setPage() failed", ioe);
                    returnBool = false;
                    showMessage(dsErrLdgPa, "\n" + pageToSet + ioe.toString());
                }
            }
            else {
                doExternal(pageToSet);
            }
        }

        // store page name
        // clean last search text
        if (returnBool) {
            curPage = pageToSet;
        }

        return returnBool;
    }


    private void doOpen() {
        JFileChooser fdo = new JFileChooser();
        fdo.setCurrentDirectory(new File(defaultSaveFolder));
        int fileGotten = fdo.showDialog(this, I18n.getString("button.open"));
        if (fileGotten == JFileChooser.APPROVE_OPTION) {
            File file = fdo.getSelectedFile();
            String fileName = file.toString();
            // get document stream and save it
            if (!fileName.startsWith("http")) {
                setPage(fileString + fileName);
            }
            else {
                setPage(fileName);
            }
        }
        // end if approved
    }


    private void doSave() {
        setStatus(I18n.getString("tooltip.save"));

        // defaultSaveFolder
        JFileChooser fds = new JFileChooser();
        fds.setDialogTitle(I18n.getString("windowtitle.save"));

        String saveName;
        if (curPage.equals("results")) {
            saveName = "results.htm";
        }
        else if (curPage.equals("home")) {
            saveName = "home.htm";
        }
        else {
            saveName = Utils.getNameOnly(curPage);
        }
        saveName = FileUtils.addFolder(defaultSaveFolder, saveName);
        fds.setCurrentDirectory(new File(defaultSaveFolder));
        fds.setSelectedFile(new File(saveName));

        int fileGotten = fds.showDialog(this, I18n.getString("button.save"));
        if (fileGotten == JFileChooser.APPROVE_OPTION) {
            File saveFile = fds.getSelectedFile();
            setStatus(I18n.getString("button.save") + saveFile);

            // get document stream and save it
            String saveText = editorPane.getText();
            PrintWriter pw = null;
            try {
                pw = new PrintWriter(new FileWriter(saveFile));
                pw.print(saveText);
            }
            catch (IOException ioe) {
                logger.fatal("doSave() failed with IOException", ioe);
                showMessage(dsErrSaFi, "\n" + saveFile);
            }
            finally {
                IOUtils.closeQuietly(pw);
            }
        }
    }


    private void doPrint() {
        PrinterJob pj = PrinterJob.getPrinterJob();
        pj.setJobName("docSearcher");
        pj.setPageable(vista);
        try {
            if (pj.printDialog()) {
                pj.print();
            }
        }
        catch (PrinterException pe) {
            logger.fatal("doPrint() failed with PrinterException", pe);
            showMessage(dsErrPrint, pe.toString());
        }
    }


    private void doExit() {
        try {
            if (!isCDSearchTool) {
                saveIndexes();
                new DocTypeHandlerUtils().saveHandler(fEnv, handlerList);
                saveSettings();
            }
        }
        catch (Exception eR) {
            showMessage(dsErrSaFi, eR.toString());
        }
        finally {
            System.exit(0);
        }
    }


    private void saveIndexes() {
        if (!indexes.isEmpty()) {
            // int numIndexes = indexes.size();
            StringBuffer sB = new StringBuffer();
            sB.append("<html><head><title>DocSearcher Index Listing</title><meta name=\"robots\" content=\"noindex,nofollow\"></head>");
            sB.append("<body><h1>DocSearcher Index Listing</h1><p align = left>");
            sB.append("Listed below are the paths and whether they are to be searched by default.</p>");
            sB.append("\n<table border = 1>\n");
            // add the items
            Iterator<DocSearcherIndex> iterator = indexes.iterator();
            DocSearcherIndex curI;
            while (iterator.hasNext()) {
                curI = iterator.next();
                sB.append("\n<tr>");
                sB.append("\n<td>");
                sB.append(curI.getName());
                sB.append("</td>");
                sB.append("\n<td>");
                sB.append(curI.getPath());
                sB.append("</td>");
                sB.append("\n<td>");
                if (curI.getShouldBeSearched()) {
                    sB.append('0');
                }
                else {
                    sB.append('1');
                }
                sB.append("</td>");
                sB.append("\n<td>");
                sB.append(curI.getDepth());
                sB.append("</td>");
                sB.append("\n<td>");
                sB.append(curI.getIndexPath());
                sB.append("</td>");
                // now the isWeb stuff and date
                sB.append("\n<td>");
                if (curI.getIsWeb()) {
                    sB.append("true");
                }
                else {
                    sB.append("false");
                }
                sB.append("</td>");
                sB.append("\n<td>");
                sB.append(curI.getMatch());
                sB.append("</td>");
                sB.append("\n<td>");
                sB.append(curI.getReplace());
                sB.append("</td>");
                sB.append("\n<td>");
                sB.append(curI.getLastIndexed());
                sB.append("</td>");
                sB.append("\n<td>");
                sB.append(curI.getIndexPolicy());
                sB.append("</td>");
                sB.append("\n<td>");
                sB.append(curI.getArchiveDir());
                sB.append("</td>");
                sB.append("</tr>\n");
            }
            // end of iteration

            // close up the html
            sB.append("\n</table></body></html>");
            FileUtils.saveFile("index_list.htm", fEnv.getWorkingDirectory(), sB);

            // save the file
        }
        // end for not empty

        if (!bookmarksList.isEmpty()) {
            // int numIndexes = bookmarksList.size();
            StringBuffer sB = new StringBuffer();
            sB.append("<html><head><title>DocSearcher Bookmark Listing</title>");
            sB.append("<body><h1>DocSearcher Bookmark Listing</h1><p align=\"left\">");
            sB.append("Listed below are the bookmarks for DocSearcher.</p>");
            sB.append("<table border=\"1\">");

            // add the items
            Iterator<SimpleBookmark> iterator = bookmarksList.iterator();
            while (iterator.hasNext()) {
                SimpleBookmark curI = iterator.next();
                sB.append("<tr>");
                sB.append("<td>");
                sB.append(curI.getDescription());
                sB.append("</td>");
                sB.append("<td>");
                sB.append(curI.getURL());
                sB.append("</td>");
                sB.append("</tr>");
            }

            sB.append("</table></body></html>");
            FileUtils.saveFile("bookmarks.htm", fEnv.getWorkingDirectory(), sB);
        }
        // end for bookmarks not empty
    }


    /**
     * Loads the DocSearcher indexes
     */
    private void loadIndexes() {
        // check for indexFile
        int numIndexes = 0;
        String indexFileName;
        if (isCDSearchTool) {
            indexFileName = cdRomIdxList;
            logger.info("loadIndexes() Using index information on CD : " + cdRomIdxList);
        }
        else {
            logger.info("loadIndexes() Loading NON - CD oriented indexes from " + fEnv.getIndexListFile());
            indexFileName = fEnv.getIndexListFile();
        }
        File testIndex = new File(indexFileName);
        StringBuffer errS = new StringBuffer();
        boolean loadErr = false;
        DocSearcherIndex curI;
        int tempDepth = 0;
        int updatePolicy = 0;
        boolean tempBool = false;
        boolean tempWebBool = false;
        boolean tempIsCdrom = false;
        if (testIndex.exists()) {
            Table tempTable = new Table(11, 100);
            tempTable.htmlLoad(indexFileName, "");
            int numI = tempTable.colSize();
            // parse it
            for (int i = 0; i < numI; i++) {
                //
                try {
                    String tempDesc = tempTable.inItem(0, i);
                    String tempFileString = tempTable.inItem(1, i);
                    int tempSbd = Integer.parseInt(tempTable.inItem(2, i));
                    String tempIndexerPath = tempTable.inItem(4, i);
                    // isWeb content
                    String tempIsWeb = tempTable.inItem(5, i);
                    tempWebBool = false;
                    String tempReplace = "";
                    String tempMatch = "";
                    if ("true".equals(tempIsWeb)) {
                        tempWebBool = true;
                        tempMatch = tempTable.inItem(6, i);
                        tempReplace = tempTable.inItem(7, i);
                    }
                    // CDROM Stuff tempIsCdrom tempReplaceStr
                    String tempMatchStr = tempTable.inItem(6, i);
                    if (tempMatchStr != null && tempMatchStr.startsWith("[cdrom]")) {
                        tempMatch = tempTable.inItem(6, i);
                        tempIsCdrom = true;
                        tempReplace = tempTable.inItem(7, i);
                        // cdRomIdxDir
                        if (isCDSearchTool) {
                            tempIndexerPath = FileUtils.addFolder(cdRomIdxDir, Utils.getNameOnly(tempIndexerPath));
                        }
                        logger.info("loadIndexes() Using CDROM Lucene index path: " + tempIndexerPath);
                    }
                    String tempDateStr = tempTable.inItem(8, i);
                    String updateStr = tempTable.inItem(9, i);
                    if (updateStr == null) {
                        updatePolicy = 0;
                    }
                    else {
                        updatePolicy = Integer.parseInt(updateStr);
                    }

                    if (tempDateStr == null) {
                        tempDateStr = DateTimeUtils.getToday();
                    }

                    if (tempSbd == 1) {
                        tempBool = false;
                    }
                    else {
                        tempBool = true;
                    }

                    String tempArch = tempTable.inItem(10, i);
                    if (tempArch == null) {
                        tempArch = fEnv.getArchiveDirectory();
                    }

                    tempDepth = Integer.parseInt(tempTable.inItem(3, i));
                    File tempFile = new File(tempFileString);
                    if (tempFileString.toLowerCase().endsWith(".zip")) {
                        curI = new DocSearcherIndex(tempFileString, tempDesc, tempBool, tempDepth, tempIndexerPath, tempWebBool, tempMatch, tempReplace, tempDateStr, updatePolicy, tempArch);
                        indexes.add(curI);
                        if (env.isGUIMode()) {
                            setStatus(I18n.getString("index") + " " + curI.getName() + " : " + DateTimeUtils.getDaysOld(curI.getLastIndexed()) + " " + I18n.getString("days_old") + " : " + curI.getLastIndexed());
                        }

                        numIndexes++;
                    }
                    else if (tempIsCdrom) {
                        curI = new DocSearcherIndex(tempFileString, tempDesc, tempBool, tempDepth, tempIndexerPath, tempWebBool, tempMatch, tempReplace, tempDateStr, updatePolicy, tempArch);
                        indexes.add(curI);
                        setStatus(I18n.getString("index") + " " + curI.getName() + " : " + DateTimeUtils.getDaysOld(curI.getLastIndexed()) + " " + I18n.getString("days_old") + " : " + curI.getLastIndexed());
                        numIndexes++;
                    }
                    else if ((tempFile.exists()) || (tempFileString.startsWith("http"))) {
                        curI = new DocSearcherIndex(tempFileString, tempDesc, tempBool, tempDepth, tempIndexerPath, tempWebBool, tempMatch, tempReplace, tempDateStr, updatePolicy, tempArch);
                        indexes.add(curI);
                        setStatus(I18n.getString("index") + " " + curI.getName() + " : " + DateTimeUtils.getDaysOld(curI.getLastIndexed()) + " " + I18n.getString("days_old") + " : " + curI.getLastIndexed());
                        numIndexes++;
                    }
                    else {
                        loadErr = true;
                        errS.append(tempFileString).append("\n\t").append(I18n.getString("lower_not_exist")).append("\n\n");
                    }
                    // end for file doesn't exist
                }
                catch (Exception e) {
                    loadErr = true;
                    errS.append(e.toString()).append("\n\n");
                }
            }
        }

        if (numIndexes == 0) {
            loadErr = true;
            errS.append(dsMkIdx);
        }

        // now load the bookmarks
        // from the bookmarksFile
        File testBMF = new File(fEnv.getBookmarkFile());
        if (testBMF.exists()) {
            Table tempTable = new Table(2, 200);
            tempTable.htmlLoad(fEnv.getBookmarkFile(), "");
            int numI = tempTable.colSize();

            // parse it
            for (int i = 0; i < numI; i++) {
                addNewBookmark(new SimpleBookmark(tempTable.inItem(1, i), tempTable.inItem(0, i)));
            }
        }

        if (loadErr) {
            showMessage(I18n.getString("error_loading_index"), errS.toString());
        }
        else {
            setStatus(numIndexes + " " + I18n.getString("total_index"));
        }
    }


    /**
     * ensures default properties files are present for user settings
     */
    private void checkDefaults() {
        // working directory
        File workingDirFile = new File(fEnv.getWorkingDirectory());
        if (!workingDirFile.exists()) {
            workingDirFile.mkdir();
        }

        // default directory
        File defaultSaveFolderFile = new File(defaultSaveFolder);
        if (!defaultSaveFolderFile.exists()) {
            defaultSaveFolderFile.mkdir();
        }

        // blank file
        blankFile = FileUtils.addFolder(fEnv.getWorkingDirectory(), "blank_page.htm");
        File blankPageFile = new File(blankFile);
        if (!blankPageFile.exists()) {
            StringBuffer bp = new StringBuffer();
            bp.append("<html><head><title>").append(I18n.getString("loading")).append("</title></head><body><h1>").append(I18n.getString("loading")).append("</h1></body></html>");
            FileUtils.saveFile("blank_page.htm", fEnv.getWorkingDirectory(), bp);
        }

        // index directory
        File indexFolder = new File(fEnv.getIndexDirectory());
        if (!indexFolder.exists()) {
            setStatus(fEnv.getIndexDirectory() + " " + I18n.getString("lower_not_exist"));
            indexFolder.mkdir();
        }
    }


    /**
     * creates a new docSearcher index
     *
     * @param di
     *            DocSearcherIndex
     * @param isCdRomIndx
     *            is CDROM index
     * @throws IOException
     *             IO problem
     */
    private void createNewIndex(DocSearcherIndex di, boolean isCdRomIndx) throws IOException {
        setStatus(I18n.getString("indexing") + " (" + di.getIndexPath() + ") ");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        checkDefaults();
        StringBuffer failedBuf = new StringBuffer();
        String indexDirNew = di.getIndexPath();
        File indexFolder = new File(indexDirNew);
        long curFileSizeBytes = 0;
        int addedSuccessfully = 0;
        StringBuilder noRobotsBuf = new StringBuilder();
        noRobotsBuf.append('\n').append(I18n.getString("files_not_indexed_robot"));
        int numNoIndex = 0;
        boolean newIndex = false;
        if (!indexFolder.exists()) {
            setStatus(indexDirNew + " " + I18n.getString("lower_not_exist"));
            indexFolder.mkdir();
            newIndex = true;
        }
        // BUILD THE INDEX
        File contentFolder = new File(di.getPath());
        int totalFolders = 1;
        int totalFiles = 0;
        int numErrors = 0;
        // String urlStr = "";
        // String dateStr = "";
        // File tempDFile;
        int curSize = 1;
        if (contentFolder.exists()) {
            ArrayList<String> folderList = new ArrayList<String>();
            folderList.add(di.getPath()); // add in our contentDir
            String curFolderString = di.getPath();
            String[] filesString;
            String[] foldersString;
            File curFolderFile;
            int curItemNo = 0;
            int lastItemNo = 0;
            int numFiles = 0;
            int numFolders = 0;
            int curSubNum = 0;
            int startSubNum = Utils.countSlash(di.getPath());
            int maxSubNum = startSubNum + di.getDepth();
            // creating the index
            IndexWriter writer = new IndexWriter(indexDirNew, new StandardAnalyzer(), newIndex);
            // writer.setUseCompoundFile(true);
            do {
                // create our folder file
                curFolderString = folderList.get(curItemNo);
                curFolderFile = new File(curFolderString);
                curSubNum = Utils.countSlash(curFolderString);
                try {
                    // handle any subfolders --> add them to our folderlist
                    foldersString = curFolderFile.list(ff);
                    numFolders = foldersString.length;
                    for (int i = 0; i < numFolders; i++) {
                        // add them to our folderlist
                        String curFold = curFolderString + pathSep + foldersString[i] + pathSep;
                        curFold = Utils.replaceAll(pathSep + pathSep, curFold, pathSep);
                        folderList.add(curFold);
                        lastItemNo++;
                        totalFolders++;
                        // debug output
                        setStatus(dsFndFldr + " " + curFold);
                    }
                    // end for having more than 0 folder
                }
                catch (Exception e) {
                    logger.fatal("createNewIndex() failed", e);
                    setStatus(curFolderString + " : " + e.toString());
                }

                // add our files
                try {
                    filesString = curFolderFile.list(wf);
                    numFiles = filesString.length;
                    logger.info("createNewIndex() Indexing " + numFiles + " files...");
                    for (int i = 0; i < numFiles; i++) {
                        // add them to our folderlist
                        String curFi = curFolderString + pathSep + filesString[i];
                        curFi = Utils.replaceAll(pathSep + pathSep, curFi, pathSep);
                        curFileSizeBytes = FileUtils.getFileSize(curFi);
                        if (curFileSizeBytes > getMaxFileSize()) {
                            setStatus(I18n.getString("skipping_file_too_big") + " (" + curFileSizeBytes + ") " + filesString[i]);
                        }
                        else { // file size is not too big
                            setStatus(I18n.getString("please_wait...") + " " + curFi + " # " + curSize);
                            curSize++;
                            addedSuccessfully = idx.addDocToIndex(curFi, writer, di, isCdRomIndx, null);
                            switch (addedSuccessfully) {
                                case 1: // error
                                    numErrors++;
                                    if (numErrors < 8) {
                                        failedBuf.append('\n').append(curFi);
                                    }
                                    break;
                                case 2: // meta robots = noindex
                                    numNoIndex++;
                                    if (numNoIndex < 8) {
                                        noRobotsBuf.append('\n').append(curFi);
                                    }
                                    break;
                                default: // OK
                                    totalFiles++;
                                    break;
                            } // end of switch
                        } // end for not skipping, file size is not too big
                    }
                    // end for files
                }
                // end of trying to get files
                catch (Exception e) {
                    logger.error("createNewIndex() failed", e);
                    setStatus(I18n.getString("error") + " " + e.toString());
                }
                // increment our curItem
                folderList.set(curItemNo, null); // remove memory overhead as
                // you go!
                curItemNo++;
                if (curSubNum >= maxSubNum) {
                    break;
                }
            }
            while (curItemNo <= lastItemNo);

            writer.close(); // close the writer
            indexes.add(di);
        }
        else {
            hasErr = true;
            errString = fEnv.getContentDirectory() + " " + I18n.getString("lower_not_exist");
        }
        // end for content dir Missing
        if (hasErr) {
            showMessage(I18n.getString("error_creating_index"), errString);
        }
        else {
            StringBuilder resultsBuf = new StringBuilder();
            resultsBuf.append(I18n.getString("added_to_index"));
            resultsBuf.append(" \"");
            resultsBuf.append(di.getName());
            resultsBuf.append("\" ");
            resultsBuf.append(totalFiles);
            resultsBuf.append(' ');
            resultsBuf.append(I18n.getString("files_from"));
            resultsBuf.append(' ');
            resultsBuf.append(totalFolders);
            resultsBuf.append(' ');
            resultsBuf.append(I18n.getString("folders"));
            resultsBuf.append("\n\n");
            resultsBuf.append(I18n.getString("starting_in_folder"));
            resultsBuf.append("\n\n\t");
            resultsBuf.append(' ');
            resultsBuf.append(di.getPath());
            resultsBuf.append("\n\n");
            resultsBuf.append(I18n.getString("for_a_depth_of"));
            resultsBuf.append(' ');
            resultsBuf.append(di.getDepth());
            resultsBuf.append('.');

            if (numErrors > 0) {
                resultsBuf.append('\n');
                resultsBuf.append(numErrors);
                resultsBuf.append(' ');
                resultsBuf.append(I18n.getString("files_not_indexed")).append('.');
                resultsBuf.append(failedBuf);
            }

            if (numNoIndex > 0) {
                resultsBuf.append("\n\n" + numNoIndex);
                resultsBuf.append('\n');
                resultsBuf.append(I18n.getString("files_not_indexed_robot"));
                resultsBuf.append(noRobotsBuf);
            }

            showMessage(dsIdxCrtd, resultsBuf.toString());
        }

        setStatus(dsIdxCrtd);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }


    /**
     * creates a new index
     */
    private void doNewIndex() {
        NewIndexDialog nid = new NewIndexDialog(this, I18n.getString("windowtitle.new_index"), true);
        nid.init();
        nid.setVisible(true);
        if (nid.getConfirmed()) {
            //
            String name = nid.getNameFieldText();
            String nameNoUnd = Utils.replaceAll(" ", name, "_");
            boolean isWeb = nid.isWebSelected();
            boolean isCdRomIndx = nid.isCDSelected();
            String replace = nid.replaceFieldText();
            String match = nid.matchFieldText();
            int policy = nid.getPolicy();
            try {
                createNewIndex(new DocSearcherIndex(nid.startFieldText(), name, nid.sbdSelected(), nid.getSDChoice(), FileUtils.addFolder(fEnv.getIndexDirectory(), nameNoUnd), isWeb, match, replace, policy, nid.archiveFieldText()), isCdRomIndx);
                // debug
                if (isCdRomIndx) {
                    logger.info("doNewIndex() CD Idx.. replace is : " + replace + ", match: " + match);
                }
            }
            catch (IOException ioe) {
                showMessage(I18n.getString("error_creating_index"), ioe.toString());
            }
        }
    }


    private void addNewBookmark(SimpleBookmark sbm) {
        bookmarksList.add(sbm);
        JMenuItem bmi = new JMenuItem(sbm.getDescription());
        bmi.setActionCommand(sbm.getURL());
        bmi.addActionListener(this);
        bookMarkMenu.add(bmi);
    }


    private void rebuildIndexes() {
        if (!indexes.isEmpty()) {
            for (DocSearcherIndex di : indexes) {
                idx.updateIndex(di);
            }
        }
        setStatus(dsIdxsRblt);
    }


    /**
     * Method look for some browsers existed on the system
     *
     * TODO why this method call every program start
     *
     * @return browser file string
     */
    private String getBrowserFile() {
        File testExist;
        String returnString = "";

        switch (env.getOSType()) {
            case OSType.WIN_32: // windows
                returnString = "C:\\Program Files\\Microsoft Internet\\Iexplore.exe";
                testExist = new File(returnString);
                if (!testExist.exists()) {
                    returnString = "C:\\Program Files\\Microsoft\\Internet Explorer\\Iexplore.exe";
                    testExist = new File(returnString);
                    if (!testExist.exists()) {
                        returnString = "C:\\Program Files\\Plus!\\Microsoft Internet\\Iexplore.exe";
                        testExist = new File(returnString);
                        if (!testExist.exists()) {
                            returnString = "C:\\Program Files\\Internet Explorer\\IEXPLORE.EXE";
                        }
                        else {
                            returnString = "start";
                        }
                    }
                }

                break;
            case OSType.LINUX: // Linux
                returnString = "/usr/bin/konqueror";
                testExist = new File(returnString);
                if (!testExist.exists()) {
                    returnString = "/usr/bin/mozilla";
                    testExist = new File(returnString);
                    if (!testExist.exists()) {
                        returnString = "/usr/bin/netscape";
                    }
                    else {
                        returnString = "/usr/local/bin/mozilla";
                    }
                }

                break;
            case OSType.UNIX: // Unix variant
                returnString = "/usr/bin/konqueror";
                testExist = new File(returnString);
                if (!testExist.exists()) {
                    returnString = "/usr/bin/mozilla";
                    testExist = new File(returnString);
                    if (!testExist.exists()) {
                        returnString = "/usr/bin/netscape";
                    }
                    else {
                        returnString = "/usr/local/bin/mozilla";
                    }
                }
                break;
            case OSType.MAC: // MAC
                returnString = "open";
                break;
            default: // UNKNOWN
                returnString = "/usr/bin/netscape";
                break;
        }

        logger.debug("getBrowserFile() return value=" + returnString);

        return returnString;
    }


    /**
     * Open the document with external viewer.
     *
     * @param externalLink Document to open
     */
    public void doExternal(String externalLink) {
        logger.debug("doExternal('" + externalLink + "') entered");

        String lowerL = externalLink.toLowerCase();

        // is link home or result?
        if (externalLink.equals("home") || externalLink.equals("results")) {
            showMessage(I18n.getString("not_externally_viewable"), I18n.getString("not_externally_viewable_message"));
            return;
        }

        // get handler of link
        String app = new DocTypeHandlerUtils().hasHandler(handlerList, externalLink);

        // special handling of file
        if (lowerL.startsWith("file:/")) {
            // remove "file:/"
            externalLink = externalLink.substring(6, externalLink.length());

            // handle of / and win32
            if (OSType.WIN_32 == env.getOSType()) {
                // replace all / with \
                externalLink = Utils.replaceAll("/", externalLink, "\\");
            }
        }

        // special handling of http
        if (lowerL.startsWith("http")) {
            // replace whitespace with %20
            externalLink = Utils.replaceAll(" ", externalLink, "%20");
        }

        // handler found and not http
        if (app.equals("") || lowerL.startsWith("http")) {
            app = defaultHndlr;
        }
        else {
            logger.debug("doExternal() using user specified handler '" + app + "'");
        }

        // open external application with file or link
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("doExternal() Executing runtime command " + Layout.LINE_SEP + "application=" + app + Layout.LINE_SEP + "file=" + externalLink);
            }

            // status bar
            setStatus(app + " " + externalLink);

            // build command
            String[] cmdArray = new String[2];
            cmdArray[0] = app;
            cmdArray[1] = externalLink;

            // exec comman
            Runtime.getRuntime().exec(cmdArray);
        }
        catch (SecurityException se) {
            showMessage(I18n.getString("error"), "\n" + app + " " + externalLink + "\n" + se.toString());
        }
        catch (IOException ioe) {
            showMessage(I18n.getString("error"), "\n" + app + " " + externalLink + "\n" + ioe.toString());
        }
    }


    private void doBookmark() {
        if ((curPage.equals("home")) || (curPage.equals("results"))) {
            showMessage(I18n.getString("not_bookmarkable"), I18n.getString("save_results_please"));
        }
        // end for not bookmarkable
        else {
            // obtain title - if there was one
            String nbt = getTitle();
            NewBookmarkDialog nbd = new NewBookmarkDialog(this, I18n.getString("windowtitle.bookmark"), true);
            nbd.setDesc(nbt);
            String toAdd = curPage;
            if ((toAdd.startsWith("file://")) && (!toAdd.startsWith("file:///"))) {
                toAdd = "file:///" + toAdd.substring(6, toAdd.length());
            }
            nbd.setBMLocation(toAdd);
            nbd.init();
            nbd.setVisible(true);
            if (nbd.isConfirmed()) {
                addNewBookmark(new SimpleBookmark(nbd.getBMLocation(), nbd.getDesc()));
            }
        }
    }


    private void doManageIndexes() {
        if (!indexes.isEmpty()) {
            int numIndexes = indexes.size();
            ManageIndexesDialog min = new ManageIndexesDialog(this, I18n.getString("windowtitle.index_properties"), true);
            min.init();
            min.setVisible(true);
            if (min.returnBool) {
                // proceed to make the changes
                ArrayList<DocSearcherIndex> newIndex = new ArrayList<DocSearcherIndex>();
                for (int i = 0; i < numIndexes; i++) {
                    // set searched
                    if (!min.deletionSelected(i)) {
                        DocSearcherIndex di = indexes.get(i);
                        di.setShouldBeSearched(min.searchSelected(i));
                        if (min.updateSelected(i)) {
                            idx.updateIndex(di);
                        }
                        if (min.exportSelected(i)) {
                            doExport(di);
                        }
                        newIndex.add(di);
                    }
                    else {
                        // recursively delete the content
                        // in the selected index
                        DocSearcherIndex di = indexes.get(i);
                        deleteRecursive(di.getIndexPath());

                        // remove some files
                        // spider index
                        String linksFile = fEnv.getSpiderIndexURLFile(di.getName());
                        FileUtils.deleteFile(linksFile);
                        String badLinksFile = fEnv.getSpiderBadIndexURLFile(di.getName());
                        FileUtils.deleteFile(badLinksFile);
                    }
                    // end for deleting and index
                }
                indexes = newIndex;
            }
        }
        else {
            showMessage(I18n.getString("error"), I18n.getString("please_create_new_index"));
        }
    }


    private void deleteRecursive(String folderToDelete) {
        int curFoldNum = 0;
        File curFolderFile;
        String curFold = "";
        String[] subFolds;
        int numSubFolds = 0;
        int totalFolds = 0;
        int numFiles = 0;
        String curFolderString = "";
        String curFileString = "";
        File testFile;
        try {
            // first obtain a list of all folders
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            ArrayList<String> allFold = new ArrayList<String>();
            allFold.add(folderToDelete);
            setStatus(dsRmvgIdxFis);
            do {
                // get list of sub folders
                curFolderString = allFold.get(curFoldNum);
                curFolderFile = new File(curFolderString);
                subFolds = curFolderFile.list(ff);
                numSubFolds = subFolds.length;
                for (int y = 0; y < numSubFolds; y++) {
                    curFold = curFolderString + pathSep + subFolds[y] + pathSep;
                    curFold = Utils.replaceAll(pathSep + pathSep, curFold, pathSep);
                    allFold.add(curFold);
                    totalFolds++;
                }
                curFoldNum++;
            }
            while (curFoldNum < totalFolds);
            // next get a list of all files
            ArrayList<String> allFiles = new ArrayList<String>();
            Iterator<String> foldIt = allFold.iterator();
            String[] filesString;
            while (foldIt.hasNext()) {
                curFolderString = foldIt.next();
                curFolderFile = new File(curFolderString);
                // get the files
                filesString = curFolderFile.list();
                numFiles = filesString.length;
                for (int y = 0; y < numFiles; y++) {
                    // add the files
                    curFileString = curFolderString + pathSep + filesString[y];
                    curFileString = Utils.replaceAll(pathSep + pathSep, curFileString, pathSep);
                    testFile = new File(curFileString);
                    if (!testFile.isDirectory()) {
                        allFiles.add(curFileString);
                    }
                }
            }
            // end for iterating
            // delete all files
            Iterator<String> fileIt = allFiles.iterator();
            while (fileIt.hasNext()) {
                curFileString = fileIt.next();
                testFile = new File(curFileString);
                testFile.delete();
            }
            // end while deleteing
            // delete all folders
            int numFoldTotal = allFiles.size();
            for (int y = numFoldTotal - 1; y >= 0; y--) {
                curFolderString = allFiles.get(y);
                curFolderFile = new File(curFolderString);
                logger.info("deleteRecursive() " + I18n.getString("deleting") + " " + curFolderString);
                curFolderFile.delete();
            }
            // delete last folder
            curFolderFile = new File(folderToDelete);
            curFolderFile.delete();
        }
        // end for trrying recursive delete
        catch (Exception e) {
            showMessage(I18n.getString("error"), e.toString());
        }
        finally {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            setStatus(I18n.getString("index_removed"));
        }
    }


    private void cb() {
        if (useAuthor.isSelected()) {
            authorField.setEnabled(true);
        }
        else {
            authorField.setEnabled(false);
        }

        if (useDate.isSelected()) {
            fromField.setEnabled(true);
            toField.setEnabled(true);
        }
        else {
            fromField.setEnabled(false);
            toField.setEnabled(false);
        }

        if (useSize.isSelected()) {
            sizeFromField.setEnabled(true);
            sizeToField.setEnabled(true);
        }
        else {
            sizeFromField.setEnabled(false);
            sizeToField.setEnabled(false);
        }

        if (useType.isSelected()) {
            fileType.setEnabled(true);
        }
        else {
            fileType.setEnabled(false);
        }
    }


    protected void doZipArchiveUpdate(DocSearcherIndex di) {
        // try and obtain meta data from the file
        // based on this new meta data see if we can obtain new index
        // fourth column
        String tempArchiveDir = Utils.getFolderOnly(di.getPath());
        String metaFileName = FileUtils.addFolder(tempArchiveDir, "archives.htm");
        String tempManifestFileName = FileUtils.addFolder(fEnv.getWorkingDirectory(), "temp_manifest.htm");
        boolean okToUpdate = true;
        if (metaFileName.toLowerCase().startsWith("http:")) {
            okToUpdate = downloadURLToFile(metaFileName, tempManifestFileName);
            metaFileName = tempManifestFileName;
        }
        if (okToUpdate) {
            // load the meta data
            try {
                Table tempTable = new Table(6, 100);
                tempTable.htmlLoad(metaFileName, "");
                String newIndexDate = di.getLastIndexed();
                okToUpdate = tempTable.loadOK;
                if (okToUpdate) {
                    // search for our new date
                    int numArchs = tempTable.colSize();
                    boolean foundArch = false;
                    int matchInt = 0;
                    for (int i = 0; i < numArchs; i++) {
                        if (tempTable.inItem(0, i).equals(di.getName())) {
                            matchInt = i;
                            foundArch = true;
                            break;
                        }
                    }
                    // end for iterating
                    if (foundArch) {
                        if (!tempTable.inItem(1, matchInt).equals(di.getLastIndexed())) {
                            newIndexDate = tempTable.inItem(1, matchInt);
                            String downloadFileName = FileUtils.addFolder(tempArchiveDir, tempTable.inItem(2, matchInt));
                            if (downloadFileName.toLowerCase().startsWith("http:")) {
                                String tempZipFileName = FileUtils.addFolder(fEnv.getWorkingDirectory(), "temp_zip_download.zip");
                                okToUpdate = downloadURLToFile(downloadFileName, tempZipFileName);
                                downloadFileName = tempZipFileName;
                            }
                            if (okToUpdate) {
                                // now delete recursively the directory
                                // and extract the new zip
                                setStatus(I18n.getString("deleting_uc") + " " + di.getIndexPath());
                                deleteRecursive(di.getIndexPath());
                                File newIndP = new File(di.getIndexPath());
                                boolean madeFold = newIndP.mkdir();
                                if (newIndP.exists())
                                    madeFold = true;
                                // boolean finalSuccess = true;
                                if (madeFold) {
                                    setStatus(I18n.getString("unzipping") + " " + downloadFileName + " --> " + di.getIndexPath());
                                    UnZippHandler uz = new UnZippHandler(downloadFileName, di.getIndexPath());
                                    try {
                                        uz.unZip();
                                        setStatus(I18n.getString("archive_unziped"));
                                        di.setLastIndexed(newIndexDate);
                                    }
                                    catch (IOException ioe) {
                                        // finalSuccess = false;
                                        showMessage(I18n.getString("error"), ioe.toString());
                                    }
                                }
                                else {
                                    showMessage(I18n.getString("error_cant_recreate_folder"), di.getIndexPath());
                                }
                            }
                            // end if ok to update
                        }
                        // end if dates different
                        else {
                            showMessage(I18n.getString("no_updates_for_archive"), I18n.getString("last_update_was") + di.getLastIndexed());
                        }
                    }
                    // end if found arch
                    else {
                        showMessage(I18n.getString("unable_to_locate_meta_data_in_file"), I18n.getString("unable_to_locate_meta_data_in_file") + " " + metaFileName);
                    }
                }
                else {
                    showMessage(I18n.getString("unable_to_locate_meta_data_in_file"), I18n.getString("unable_to_locate_meta_data_in_file") + " " + metaFileName);
                }
            }
            // end for trying to update Zip
            catch (Exception e) {
                logger.error("doZipArchiveUpdate() failed", e);
                setStatus(I18n.getString("unable_to_locate_meta_data_in_file") + " " + metaFileName);
                okToUpdate = false;
            }
        }
    }


    private void doExport(DocSearcherIndex di) {
        // zip contents and place in archive Dir
        String archiveZipFileName = Utils.replaceAll(" ", di.getName(), "_");
        if (!archiveZipFileName.toLowerCase().endsWith(".zip")) {
            archiveZipFileName += ".zip";
        }
        String content = di.getPath();
        if (di.getIsWeb()) {
            content = di.getMatch();
        }
        String zipFileNameOnly = archiveZipFileName;
        archiveZipFileName = FileUtils.addFolder(di.getArchiveDir(), archiveZipFileName);
        ZippHandler zh = new ZippHandler(archiveZipFileName, di.getIndexPath());
        boolean zipSuccess = true;
        String errMsg = null;
        setStatus(I18n.getString("archiving_index") + " " + di.getName() + " --> " + archiveZipFileName + ", " + I18n.getString("lower_please_wait") + "...");
        try {
            zh.zip();
        }
        catch (IOException ioe) {
            errMsg = ioe.toString();
            zipSuccess = false;
        }
        finally {
            setStatus(I18n.getString("archiving_done"));
        }

        if (zipSuccess) {
            showMessage(I18n.getString("archiving_success"), di.getName() + " " + I18n.getString("was_archived_to_file") + "\n" + archiveZipFileName);
        }
        else {
            showMessage(I18n.getString("error"), errMsg);
        }

        // OK now update the archive table
        updateArchiveTable(di.getName(), di.getLastIndexed(), zipFileNameOnly, di.getArchiveDir(), content);
    }


    private void updateArchiveTable(String name, String lastIndexed, String zipFileName, String archDir, String content) {
        boolean hasErr = false;
        String errMsg = "";
        try {
            String archivesFileName = FileUtils.addFolder(archDir, "archives.htm");
            setStatus(I18n.getString("archive_table_updating") + " " + archivesFileName);
            File textArchiveIndex = new File(archivesFileName);
            if (textArchiveIndex.exists()) {
                // read and update the file
                Table tempTable = new Table(6, 100);
                tempTable.htmlLoad(archivesFileName, "");
                tempTable.setCaption("DocSearcher Lucene Search Index Archive Listing");
                int numI = tempTable.colSize();
                String tempName = "";
                int foundAtNum = numI;
                // parse it
                for (int i = 1; i < numI; i++) {
                    tempName = tempTable.inItem(0, i);
                    if (tempName.equals(name)) {
                        foundAtNum = i;
                        break;
                    }
                }
                // end for iterating
                //
                tempTable.add(name, 0, foundAtNum);
                tempTable.add(lastIndexed, 1, foundAtNum);
                tempTable.add(zipFileName, 2, foundAtNum);
                tempTable.add(content, 3, foundAtNum);
                // save it
                int k = tempTable.colSize();
                int l = tempTable.rowSize();
                tempTable.fpSave(archivesFileName, k, l);
            }
            else {
                // create a new archive index
                Table tempTable = new Table(6, 102);
                tempTable.setCaption("DocSearcher Lucene Search Index Archive Listing");
                //
                // add the header
                tempTable.add("Description", 0, 0);
                tempTable.add("Date of Indexing", 1, 0);
                tempTable.add("Archive Zip File", 2, 0);
                tempTable.add("Directory or Content", 3, 0);
                // add the data
                tempTable.add(name, 0, 1);
                tempTable.add(lastIndexed, 1, 1);
                tempTable.add(zipFileName, 2, 1);
                tempTable.add(content, 3, 1);
                // save it
                int k = tempTable.colSize();
                int l = tempTable.rowSize();
                tempTable.fpSave(archivesFileName, k, l);
            }
        }
        // end for try
        catch (Exception eT) {
            hasErr = true;
            errMsg = eT.toString();
        }

        if (hasErr) {
            showMessage(I18n.getString("error"), errMsg);
        }
        else {
            setStatus(I18n.getString("archive_table_updated"));
        }
    }


    private boolean downloadURLToFile(String urlString, String fileToSaveAs) {
        int numBytes = 0;
        int curI = 0;
        FileOutputStream dos = null;
        InputStream urlStream = null;
        int lastPercent = 0;
        int curPercent = 0;
        try {
            URL url = new URL(urlString);
            File saveFile = new File(fileToSaveAs);
            dos = new FileOutputStream(saveFile);
            URLConnection conn = url.openConnection();
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.connect();
            urlStream = conn.getInputStream();
            int totalSize = conn.getContentLength();
            while (curI != -1) {
                curI = urlStream.read();
                byte curBint = (byte) curI;
                if (curI == -1) {
                    break;
                }
                dos.write(curBint);
                numBytes++;
                if (totalSize > 0) {
                    curPercent = (numBytes * 100) / totalSize;
                    if (curPercent != lastPercent) {
                        setStatus(curPercent + " " + I18n.getString("percent_downloaded") + " " + I18n.getString("of_file") + " " + urlString + " " + I18n.getString("total_bytes") + " " + totalSize);
                        lastPercent = curPercent;
                    }
                }
                // end if total size not zero
            }
            urlStream.close();
            dos.close();
        }
        catch (IOException ioe) {
            logger.fatal("downloadURLToFile() failed", ioe);
            showMessage(I18n.getString("error_download_file"), ioe.toString());
            return false;
        }
        finally {
        	IOUtils.closeQuietly(dos);
        	IOUtils.closeQuietly(urlStream);
        }

        return true;
    }


    private void doImport(String zipFileString, String description, String dateIndexed, boolean searchedByDefault, boolean isWeb, int indexPolicy) {
        // may be just a URL
        boolean successFulDownload = true;
        if (dateIndexed.equals("")) {
            dateIndexed = DateTimeUtils.getToday();
        }
        //
        boolean zipSuccess = true;
        String errMsg = "";
        String indexFolderName = Utils.replaceAll(" ", description, "_");
        String loadString = zipFileString;
        if (loadString.toLowerCase().startsWith("http://")) {
            // download zip and load the downloaded zip file
            String nameOnlyStr = Utils.getNameOnly(loadString);
            loadString = FileUtils.addFolder(fEnv.getArchiveDirectory(), nameOnlyStr);
            successFulDownload = downloadURLToFile(zipFileString, loadString);
            if (!successFulDownload) {
                errMsg = I18n.getString("error_download_file") + " " + zipFileString;
                zipSuccess = false;
            }
        }
        // end for downloading the zip
        indexFolderName = FileUtils.addFolder(fEnv.getIndexDirectory(), indexFolderName);
        File newIndexFolder = new File(indexFolderName);
        boolean dirMade = newIndexFolder.mkdir();
        if (newIndexFolder.exists()) {
            setStatus(I18n.getString("error") + " : " + I18n.getString("folder_already_exists") + " --> " + indexFolderName);
            dirMade = true;
        }
        if ((dirMade) && (successFulDownload)) {
            UnZippHandler uz = new UnZippHandler(loadString, indexFolderName);
            setStatus(I18n.getString("importing") + " " + description + " " + I18n.getString("lower_please_wait") + "...");
            try {
                uz.unZip();
                // add it in there
                DocSearcherIndex di = new DocSearcherIndex(zipFileString, description, searchedByDefault, 0, indexFolderName, isWeb, "", "", indexPolicy, fEnv.getArchiveDirectory());
                indexes.add(di);
            }
            catch (IOException ioe) {
                errMsg = ioe.toString();
                zipSuccess = false;
            }
            finally {
                setStatus(I18n.getString("import_done"));
            }

            if (zipSuccess) {
                showMessage(I18n.getString("import_success"), description + " " + I18n.getString("was_imported_point"));
            }
            else {
                showMessage(I18n.getString("error_importing") + " : " + description, errMsg);
            }
        }
        else {
            showMessage(I18n.getString("error_importing"), I18n.getString("error_cant_recreate_folder") + "  " + indexFolderName);
        }
    }


    private void getImportInfo() {
        ImportDialog id = new ImportDialog(this, I18n.getString("import_ds_index"), true);
        id.init();
        id.setVisible(true);
        if (id.getConfirmed()) {
            String importZipFileName = id.getUrlOrFileText().trim();
            String importFolderOnly = Utils.getFolderOnly(importZipFileName);
            String importManifestName = FileUtils.addFolder(importFolderOnly, "archives.htm");
            String zipArchNameOnly = Utils.getNameOnly(importZipFileName);
            boolean foundManifest = false;
            boolean foundManifestData = false;
            boolean doImport = true;
            //
            String zipFileString = importZipFileName;
            String description = "Unknown";
            String dateIndexed = "";
            boolean searchedByDefault = true;
            boolean isWeb = true;
            int indexPolicy = 0;
            //
            if (importManifestName.toLowerCase().startsWith("http:")) {
                // convert URL to file
                String tempManifestFileName = FileUtils.addFolder(fEnv.getWorkingDirectory(), "temp_manifest.htm");
                foundManifest = downloadURLToFile(importManifestName, tempManifestFileName);
                if (foundManifest) {
                    importManifestName = tempManifestFileName;
                }
            }
            // end for manifest
            else {
                File testManifestFile = new File(importManifestName);
                if (testManifestFile.exists()) {
                    foundManifest = true;
                }
            }
            // end for not a URL
            if (foundManifest) {
                // try and retrieve the manifest data
                Table tempTable = new Table(11, 100);
                tempTable.htmlLoad(importManifestName, "");
                int numI = tempTable.colSize();
                // int tempSbd = 0;
                String tempArchFile = "";
                String tempDesc = "";
                String tempDateIndexed = "";
                // parse it
                for (int i = 1; i < numI; i++) {
                    //
                    try {
                        tempDesc = tempTable.inItem(0, i);
                        tempArchFile = tempTable.inItem(2, i);
                        tempDateIndexed = tempTable.inItem(1, i);
                        if (zipArchNameOnly.equals(tempArchFile)) {
                            description = tempDesc;
                            dateIndexed = tempDateIndexed;
                            foundManifestData = true;
                            setStatus(I18n.getString("archive_description_found") + " " + description);
                            break;
                        }
                        else {
                            logger.info("getImportInfo() " + zipArchNameOnly + " != " + tempArchFile);
                        }
                    }
                    catch (Exception eR) {
                        setStatus(I18n.getString("error_parse_manifest") + " " + eR.toString());
                    }
                }
                // end for iterating over manifest file entries
            }

            if (!foundManifestData) {
                // show a dialog to obtain archive meta data
                // IF dialog is cancelled ; doImport = false
                ManifestDialog md = new ManifestDialog(this, I18n.getString("windowtitle.import_index_properties"), true);
                md.init();
                md.setVisible(true);
                if (md.getConfirmed()) {
                    description = md.getDescFieldText().trim();
                    dateIndexed = DateTimeUtils.getToday();
                    isWeb = md.webBoxSelected();
                    searchedByDefault = md.sbdBoxSelected();
                    indexPolicy = md.indexFreqIdx();
                }
                else {
                    doImport = false;
                }
            }

            if (doImport) {
                // create our new index!
                doImport(zipFileString, description, dateIndexed, searchedByDefault, isWeb, indexPolicy);
            }
        }
        // end for confirmed
    }


    /**
     * Command line parsing
     *
     * All output to stdout
     *
     * @param commandString
     * @param indexString
     */
    private void doCommand(String commandString, String indexString) {
        System.out.println("command: " + commandString + " index: " + (indexString == null ? "-" : indexString));

        boolean doSndMail = sendNoticeOnIdxingChanges();
        boolean isTxtFormt = isTextEmailFormat();
        if (indexString != null) {
            indexString = indexString.trim();
        }

        // list index
        if (commandString.equals("list")) {
            if (indexes.size() > 0) {
                Iterator<DocSearcherIndex> it = indexes.iterator();
                int countNum = 1;
                while (it.hasNext()) {
                    DocSearcherIndex di = it.next();
                    System.out.println(countNum + ". " + di.getName());
                    countNum++;
                }
            }
            else {
                System.out.print(I18n.getString("no_index_found"));
            }
        }
        // update index
        else if (commandString.equals("update")) {
            if ("".equals(indexString)) {
                System.out.println(I18n.getString("please_specify_index"));
            }
            else {
                if (indexes.size() > 0) {
                    boolean foundMatch = false;
                    boolean sendAMess = sendNoticeOnIdxingChanges();

                    // step through indexlist and search index
                    for (DocSearcherIndex di : indexes) {
                        if (di.getName().equals(indexString)) {
                            foundMatch = true;

                            idx.setDoEmail(sendAMess);
                            idx.setEmailText(isTextEmailFormat());
                            idx.updateIndex(di);
                            if (sendAMess && idx.getTotalChanges() > 0) {
                                sendEmail(di.getName() + " " + dsUpdts, idx.getUpDateNotes());
                            }

                            break;
                        }
                    }

                    if (!foundMatch) {
                        System.out.println(indexString + I18n.getString("not_found"));
                    }
                }
                else {
                    System.out.println(I18n.getString("no_index_found"));
                }
            }
        }
        // export index
        else if (commandString.equals("export")) {
            if ("".equals(indexString)) {
                System.out.println(I18n.getString("please_specify_index"));
            } else {
                if (indexes.size() > 0) {
                    boolean foundMatch = false;

                    // step through indexlist and export index
                    for (DocSearcherIndex di : indexes) {
                        if (di.getName().equals(indexString)) {
                            foundMatch = true;

                            doExport(di);

                            break;
                        }
                    }

                    if (!foundMatch) {
                        System.out.println(indexString + I18n.getString("not_found"));
                    }
                }
                else {
                    System.out.println(I18n.getString("no_index_found"));
                }
            }
        }
        // search in index
        else if (commandString.startsWith("search:")) {
            String searchT = commandString.substring(7, commandString.length());
            System.out.println(I18n.getString("searching_for") + " " + searchT + " ...");

            if ("".equals(indexString)) {
                System.out.println(I18n.getString("please_specify_index"));
            }
            else {
                if (indexes.size() > 0) {
                    boolean foundMatch = false;

                    // step through indexlist and search in index
                    for (DocSearcherIndex di : indexes) {
                        if (di.getName().equals(indexString)) {
                            foundMatch = true;

                            di.setShouldBeSearched(true);
                            doSearch(searchT);

                            break;
                        }
                        else {
                            di.setShouldBeSearched(false);
                        }
                    }

                    if (!foundMatch) {
                        System.out.println(indexString + I18n.getString("not_found"));
                    }
                }
                else {
                    System.out.println(I18n.getString("no_index_found"));
                }
            }
        }
        // analyze log
        else if (commandString.equals("analyze_log")) {
            try {
                LogAnalysis.doLogAnalysis(this, indexString);
            }
            catch (IOException ioe) {
                System.out.println(I18n.getString("error_analyze_logfile") + " " + indexString + "\n" + ioe.toString());
            }
        }
        // import
        else if (commandString.equals("import")) {
            ArchiveMetaData amd = new ArchiveMetaData(indexString);

            doImport(indexString, amd.getDesc(), amd.getDI(), amd.getSDB(), amd.getIsWeb(), amd.getIndexPolicy());
            // System.out.println("\nIMPORT index -->"+indexString);
            // System.out.println("\ndescription -->"+amd.getDesc());
        }
        // command line help
        else {
            System.out.println(I18n.getString("cmd_usage"));
        }

        doExit();
    }


    private void getSeachLogReport() {
        JFileChooser fdo = new JFileChooser();
        fdo.setCurrentDirectory(new File(fEnv.getWorkingDirectory()));
        int fileGotten = fdo.showDialog(this, I18n.getString("select"));
        if (fileGotten == JFileChooser.APPROVE_OPTION) {
            File file = fdo.getSelectedFile();
            try {
                LogAnalysis.doLogAnalysis(this, file.toString());
            }
            catch (IOException ioe) {
                logger.fatal("getSeachLogReport() failed", ioe);
                setStatus(I18n.getString("error_status_log_report") + ioe.toString());
            }
        }
    }


    private void doMetaReport() {
        MetaReport mr = new MetaReport();
        mr.getMetaReport(this);
    }

    class Hyperactive implements HyperlinkListener {
        /**
         * Log4J
         */
        private final Logger logger = Logger.getLogger(getClass().getName());


        @Override
		public void hyperlinkUpdate(HyperlinkEvent event) {
            if (logger.isDebugEnabled()) {
                logger.debug("hyperlinkUpdate() HyperlinkEvent=" + event.getClass() +
                		" EventType=" + event.getEventType());
            }

            // only activated event
            if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                if (logger.isDebugEnabled()) {
                    logger.debug("hyperlinkUpdate() URL=" + event.getURL());
                }

                if (event instanceof HTMLFrameHyperlinkEvent) {
                    String urlString = event.getURL().toString();
                    doExternal(urlString);
                }
                else {
                    try {
                        String urlString = event.getURL().toString();
                        if ((urlString.toLowerCase().endsWith(".exe")) || (urlString.toLowerCase().endsWith(".sh"))) {
                            try {
                                if (urlString.startsWith("file:/")) {
                                    urlString = urlString.substring(6, urlString.length());
                                }
                                Runtime.getRuntime().exec(urlString);
                            }
                            catch (Exception e) {
                            	logger.fatal("hyperlinkUpdate() failed to open", e);
                                setStatus(urlString + " : " + e.toString());
                            }
                        }
                        else {
                            if (loadExternal) {
                                doExternal(urlString);
                            }
                            else {
                                setPage(urlString);
                            }
                        }
                    }
                    catch (Throwable t) {
                        logger.fatal("hyperLinkUpdate() failed", t);
                        showMessage(I18n.getString("error_loading_page"), I18n.getString("error_reported") +
                        		" " + t.toString());
                    }
                }
            }
        }
    }

    class CheckBoxListener implements ActionListener {
        @Override
		public void actionPerformed(ActionEvent e) {
            // String source = e.getActionCommand();
            cb();
        }
    }


    private void doHandlers() {
        DsProperties hd = new DsProperties(this, I18n.getString("windowtitle.settings"), true);
        hd.init();
        hd.setVisible(true);

        if (hd.getConfirmed()) {
            // check everything
            switch (hd.getReturnInt()) {
                default: // nothing
                    break;
                case 0: // default hdnler
                    defaultHndlr = hd.defltHndlrText();
                    break;
                case 1: // look and feel
                    String newLafChosen = hd.lafSelected();
                    // set new LAF if a new one is set
                    if ((!newLafChosen.equals("")) && (!newLafChosen.equals(lafChosen))) {
                        lafChosen = newLafChosen;
                        try {
                            UIManager.setLookAndFeel(lafChosen);
                            SwingUtilities.updateComponentTreeUI(this);
                            setSize(new Dimension(kDefaultX, kDefaultY));
                        }
                        catch (Exception e) {
                            logger.error("doHandler() failed", e);
                            showMessage(I18n.getString("error"), e.toString());
                        }
                    }

                    // now for max file size to index
                    try {
                        long newMFSI = Long.parseLong(hd.maxSizeField());
                        if (newMFSI > 0) {
                            newMFSI = newMFSI * 1024;
                        }
                        setMaxFileSize(newMFSI);
                    }
                    catch (NumberFormatException nfe) {
                        logger.error("doHandler() failed ", nfe);
                        showMessage(I18n.getString("error"), nfe.toString());
                    }

                    // now for max hits to show
                    String newMaxInt = hd.maxFieldText().trim();
                    if (!newMaxInt.equals("")) {
                        try {
                            int newMaxN = Integer.parseInt(newMaxInt);
                            if ((newMaxN > 0) && (newMaxN < 1000)) {
                                maxNumHitsShown = newMaxN;
                            }
                        }
                        catch (NumberFormatException nfe) {
                            logger.error("doHandler() failed", nfe);
                            showMessage(I18n.getString("error"), nfe.toString());
                        }
                    }

                    if (hd.loadExternalSelected()) {
                        loadExternal = true;
                    }
                    else {
                        loadExternal = false;
                    }
                    break;
                case 2: // index directory
                    //
                    String newIndexDir = hd.getDsDirFieldText().trim();
                    if (!newIndexDir.equals("")) {
                        // copy over our files
                        if (hd.copyDirFilesSelected()) {
                            // including our index list file
                            copyFiles(fEnv.getIndexDirectory(), newIndexDir);
                            // change the file settings on the DsIndex objects
                            Iterator<DocSearcherIndex> iterator = indexes.iterator();
                            DocSearcherIndex curI;
                            String curIdxrStr = "";
                            while (iterator.hasNext()) {
                                curI = iterator.next();
                                curIdxrStr = curI.getIndexPath();
                                if (curIdxrStr.startsWith(fEnv.getIndexDirectory())) {
                                    curI.setIndexPath(newIndexDir + curIdxrStr.substring(fEnv.getIndexDirectory().length(), curIdxrStr.length()));
                                }
                            }
                            setStatus(I18n.getString("finished_copying"));
                        }
                        fEnv.setIndexDirectory(newIndexDir);
                    }
                    String newDirFieldTExt = hd.getTmpFieldText().trim();
                    //
                    if (!newDirFieldTExt.equals("")) {
                        resetTempDir(newDirFieldTExt);
                    }
                    //
                    String newWorkingDirFieldTExt = hd.workingDirFieldText().trim();
                    if (!newWorkingDirFieldTExt.equals("")) {
                        resetWorkingDir(newWorkingDirFieldTExt);
                    }
                    break;
                case 3: // email stuff
                    gateway = hd.getGateWayFieldText();
                    gatewayPwd = hd.gatewayPwdFieldText();
                    gatewayUser = hd.gatewayUserFieldText();
                    if (hd.sendEmailBxSelected()) {
                        sendEmailNotice = "true";
                    }
                    else {
                        sendEmailNotice = "false";
                    }
                    if (hd.textRBSelected()) {
                        emailFormat = hd.TEXT_FORMAT;
                    }
                    else {
                        emailFormat = hd.HTML_FORMAT;
                    }
                    break;
            }
        }
    } // end for doing handlers


    /**
     * Copy files
     *
     * @param sourceFolder
     * @param destinationFolder
     */
    private void copyFiles(String sourceFolder, String destinationFolder) {
        if (logger.isDebugEnabled()) {
            logger.debug("copyFiles('" + sourceFolder + "', '" + destinationFolder + "') entered");
        }

        String stat = I18n.getString("copying_folder") + " " + Utils.concatStr(sourceFolder);

        if (isLoading) {
            curStatusString = stat;
        }
        else {
            setStatus(stat);
        }

        // copy all content from sourceFolder to desctinationFolder
        File sourceFolderFile = new File(sourceFolder);
        File destinationFolderFile = new File(destinationFolder);
        if (sourceFolderFile.isDirectory() && destinationFolderFile.isDirectory()) {

            String allFilesList[] = sourceFolderFile.list();
            int numFiles = allFilesList.length;

            for (int i = 0; i < numFiles; i++) {
                String origFiNa = FileUtils.addFolder(sourceFolder, allFilesList[i]);
                String newNiNa = FileUtils.addFolder(destinationFolder, allFilesList[i]);

                File testDirFi = new File(origFiNa);
                if (testDirFi.isDirectory()) {
                    File newSubFile = new File(newNiNa);
                    if (newSubFile.mkdir()) {
                        copyFiles(origFiNa, newNiNa);
                    }
                    else {
                        setStatus(I18n.getString("error_creating_folder") + " " + newNiNa);
                    }
                }
                else {
                    copyFile(origFiNa, newNiNa);
                }
            }
        }
    }


    /**
     * Copy file
     *
     * @param originalFileName
     * @param newFileName
     */
    private void copyFile(String originalFileName, String newFileName) {
        if (logger.isDebugEnabled()) {
            logger.debug("copyFile('" + originalFileName + "', '" + newFileName + "') entered");
        }

        setStatus(I18n.getString("please_wait...") + " " + I18n.getString("copying_folder") + " " + Utils.concatStr(originalFileName) + " --> " + Utils.concatStr(newFileName));

        if (FileUtils.copyFile(originalFileName, newFileName)) {
            setStatus(I18n.getString("finished_copying") + " " + Utils.concatStr(originalFileName));
        }
        else {
            setStatus(I18n.getString("error") + " : " + Utils.concatStr(originalFileName));
        }
    }


    /**
     * Method checks CDROM directory
     */
    private void checkCDROMDir() {
        String changeStr = "";
        boolean changeCD = false;
        String startDir = fEnv.getStartDirectory();

        if (isCDSearchTool) {
            // look at startDir
            switch (env.getOSType()) {
                case OSType.WIN_32: {
                    changeStr = startDir.substring(0, 2);
                    changeCD = true;
                    break;
                }
                case OSType.LINUX: {
                    // do nothing
                    if (startDir.indexOf("/mnt/cdrom1") != -1) {
                        changeStr = "/mnt/cdrom1";
                        changeCD = true;
                    }
                    else if (startDir.indexOf("/mnt/cdrom2") != -1) {
                        changeStr = "/mnt/cdrom2";
                        changeCD = true;
                    }
                    break;
                }
                case OSType.UNIX: {
                    break;
                }
                case OSType.MAC: {
                    break;
                }
            }
        }

        if (changeCD) {
            env.setCDROMDir(changeStr);
            logger.info("checkCDROMDir() new CDROM dir is: " + changeStr);
        }
    }


    /**
     * Gets CDROM path
     *
     * @param pathToChange
     * @return
     */
    private String getCDROMPath(String pathToChange) {
        // System.out.println("ORIG PATH: "+pathToChange);
        String ptc = pathToChange.substring(7, pathToChange.length());
        if ((ptc.startsWith("/")) || (ptc.startsWith("\\"))) {
            ptc = ptc.substring(1, ptc.length());
        }

        String tmpGetCDROMDr = env.getCDROMDir();
        if (!Utils.endsWithSlash(tmpGetCDROMDr)) {
            return tmpGetCDROMDr + "/" + ptc;
        }
        else {
            return tmpGetCDROMDr + ptc;
        }
    }


    private boolean isThisOnACd(String cdRomIdxList) {
        //
        // looks for startDir/cdrom_indexes
        //
        File testCDFi = new File(cdRomIdxList);
        return testCDFi.exists();
    }


    /**
     * Load settings
     */
    private void loadSettings() {
        logger.info("loadSettings() entered");

        Properties props = null;

        // check for old incorrect setting file format
        File oldPropsFile = new File(fEnv.getOldUserPreferencesFile());
        if (oldPropsFile.isFile()) {
            logger.log(NoticeLevel.NOTICE, "loadSettings() found old settings file and convert it now");

            props = new Properties();

            // read file and set the properties
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(oldPropsFile);
                int c;
                StringBuilder line = new StringBuilder();
                while ((c = fileReader.read()) != -1) {
                    // is end of line
                    if (c == '\n' || c == '\r') {
                        String str = line.toString();
                        int pos = str.indexOf('=');
                        if (pos != -1 && pos + 1 != str.length()) {
                            String key = str.substring(0, pos);
                            String value = str.substring(pos + 1);
                            logger.debug("loadSettings() convert " + key + "=" + value);
                            props.setProperty(key, value);
                        }

                        line = new StringBuilder();
                    }
                    else {
                        line.append((char) c);
                    }
                }
            }
            catch (IOException ioe) {
                logger.fatal("loadSettings() failed", ioe);
                showMessage(dsErrLdgFi, "\n" + oldPropsFile + "\n\n : " + ioe.toString());
            }
            finally {
                IOUtils.closeQuietly(fileReader);
            }

            // delete old properties file
            if (!isCDSearchTool) {
                if (!oldPropsFile.delete()) {
                    logger.error("loadSettings() can't delete old properties file (" + oldPropsFile + ")");
                }
            }
        }
        else {
            // loads prefs stored in saveSettings
            props = loadProperties(fEnv.getUserPreferencesFile());
        }

        // sendEmailNotice
        String tmpStr = props.getProperty("sendEmailNotice", "");
        if (!"".equals(tmpStr)) {
            sendEmailNotice = tmpStr;
        }

        // numAdminEmails
        tmpStr = props.getProperty("numAdminEmails", "");
        int adminEmNo = 0;
        try {
            adminEmNo = Integer.parseInt(tmpStr);
        }
        catch (NumberFormatException nfe) {
            logger.error("loadSettings() failure with numAdminEmails (" + nfe.getMessage() + ")");
            adminEmNo = 0;
        }
        if (adminEmNo > 0) {
            // add to arraylist of emails
            for (int i = 0; i < adminEmNo; i++) {
                tmpStr = props.getProperty("email" + i, "");
                if (!"".equals(tmpStr)) {
                    addEmail(tmpStr);
                }
            }
        }

        // maxFileSizeToIndex
        tmpStr = props.getProperty("maxFileSizeToIndex", "");
        long newMaxFiSiInt = 0;
        if (!"".equals(tmpStr)) {
            try {
                newMaxFiSiInt = Long.parseLong(tmpStr);
                setMaxFileSize(newMaxFiSiInt);
            }
            catch (NumberFormatException nfe) {
                logger.error("loadSettings() failure with maxFileSizeToIndex (" + nfe.getMessage() + ")");
            }
        }
        else {
            logger.info("loadSettings() no prefs for maxFileSizeToIndex found, using default: " + getMaxFileSize());
        }

        // emailFormat
        tmpStr = props.getProperty("emailFormat", "");
        if (!"".equals(tmpStr)) {
            emailFormat = tmpStr;
        }

        // gatewayUser
        tmpStr = props.getProperty("gatewayUser", "");
        if (!"".equals(tmpStr)) {
            gatewayUser = tmpStr;
        }

        // loadExternal
        tmpStr = props.getProperty("loadExternal", "");
        if ("false".equals(tmpStr)) {
            loadExternal = false;
        }
        else {
            loadExternal = true;
        }

        // gatewayPWD
        tmpStr = props.getProperty("gatewayPwd", "");
        if (!"".equals(tmpStr)) {
            gatewayPwd = tmpStr;
        }

        // gateway
        tmpStr = props.getProperty("gateway", "");
        if (!"".equals(tmpStr)) {
            gateway = tmpStr;
        }

        // browser
        tmpStr = props.getProperty("browser", "");
        // TODO if browser empty, try to get from system with method
        // getBrowserFile()
        if (!"".equals(tmpStr)) {
            defaultHndlr = tmpStr;
        }

        // maxNumHitsShown
        tmpStr = props.getProperty("maxNumHitsShown", "");
        if (!"".equals(tmpStr)) {
            try {
                maxNumHitsShown = Integer.parseInt(tmpStr);
            }
            catch (NumberFormatException nfe) {
                logger.error("loadSettings() failure with maxNumHitsShown", nfe);
                maxNumHitsShown = 250;
            }
        }

        // tempDir
        tmpStr = props.getProperty("tempDir", "");
        if (!"".equals(tmpStr)) {
            resetTempDir(tmpStr);
        }

        // laf
        tmpStr = props.getProperty("laf", "");
        if (!"".equals(tmpStr) && !"de.muntjak.tinylookandfeel.TinyLookAndFeel".equals(tmpStr)) {
            lafChosen = tmpStr;
            if (env.isGUIMode()) {
                try {
                    UIManager.setLookAndFeel(lafChosen);
                    SwingUtilities.updateComponentTreeUI(this);
                    pack();
                }
                catch (Exception e) {
                    logger.error("loadSettings() failure with laf", e);
                    setStatus(ERROR + " : " + e.toString());
                }
            }
        }

        // indexDir
        tmpStr = props.getProperty("indexDir", "");
        if (!"".equals(tmpStr)) {
            File testIdxDir = new File(tmpStr);
            if (testIdxDir.isDirectory()) {
                fEnv.setIndexDirectory(tmpStr);
            }
            else {
                logger.info("loadSettings() index dir doesn't exist (" + tmpStr + "). Using Default:" + fEnv.getIndexDirectory());
            }
        }

        // workingDir
        tmpStr = props.getProperty("workingDir", "");
        if (!"".equals(tmpStr)) {
            File testworkingDir = new File(tmpStr);
            if (testworkingDir.isDirectory()) {
                resetWorkingDir(tmpStr);
            }
            else {
                logger.info("loadSettings() index dir doesn't exist (" + tmpStr + "). Using Default:" + fEnv.getWorkingDirectory());
            }
        }
    }


    /**
     * Save Settings
     */
    private void saveSettings() {
        logger.info("saveSettings() entered");

        // saves all preferences
        Properties props = new Properties();

        // gateway
        props.setProperty("gateway", gateway);
        // gatewayPWD
        props.setProperty("gatewayPwd", gatewayPwd);
        // max file size to index
        props.setProperty("maxFileSizeToIndex", Long.toString(getMaxFileSize()));
        // maxNumHitsShown
        props.setProperty("maxNumHitsShown", Integer.toString(maxNumHitsShown));
        // gatewayUser
        props.setProperty("gatewayUser", gatewayUser);
        // emailFormat
        props.setProperty("emailFormat", emailFormat);
        // loadExternal
        props.setProperty("loadExternal", Boolean.toString(loadExternal));
        // sendEmailNotice
        props.setProperty("sendEmailNotice", sendEmailNotice);
        // numAdminEmails
        int numAdm = numGetAdminEmails();
        props.setProperty("numAdminEmails", Integer.toString(numAdm));
        for (int i = 0; i < numAdm; i++) {
            props.setProperty("email" + i, adminEmails.get(i));
        }
        // browser
        props.setProperty("browser", defaultHndlr);
        // laf
        props.setProperty("laf", lafChosen);
        // indexDir
        props.setProperty("indexDir", fEnv.getIndexDirectory());
        // tempDir
        props.setProperty("tempDir", tempDir);
        // workingDir
        props.setProperty("workingDir", fEnv.getWorkingDirectory());

        // save settings
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(fEnv.getUserPreferencesFile());
            props.store(fileOut, "DocSearcher properties");
        }
        catch (IOException ioe) {
            logger.fatal("saveSettings() save settings failed", ioe);
        }
        finally {
            IOUtils.closeQuietly(fileOut);
        }
    }


    private void resetTempDir(String toSet) {
        tempDir = toSet;
        rtfTextFile = FileUtils.addFolder(tempDir, "temp_rtf_file_" + USER_NAME + ".txt");
        ooTextFile = FileUtils.addFolder(tempDir, "temp_oo_file_" + USER_NAME + ".xml");
        ooMetaTextFile = FileUtils.addFolder(tempDir, "temp_oo_meta_file_" + USER_NAME + ".xml");
        ooTextOnlyFile = FileUtils.addFolder(tempDir, "temp_oo_text_file_" + USER_NAME + ".txt");
    }


    private void resetWorkingDir(String toSet) {
        String workingDir = toSet;

        fEnv.setWorkingDirectory(workingDir);
        fEnv.resetIndexListFile();
        fEnv.resetBookmarkFile();
        fEnv.resetArchiveDirectory();

        defaultSaveFolder = FileUtils.addFolder(workingDir, "saved_searches");

        checkDefaults();
    }


    @SuppressWarnings("unused")
    private void sendEmail(StringBuffer message) {
        String compName = null;
        try {
            compName = InetAddress.getLocalHost().toString();
        }
        catch (UnknownHostException uhe) {
            compName = "localhost";
        }

        String subj = DateTimeUtils.getToday() + " " + I18n.getString("email_subject") + " " + compName;
        int numEs = adminEmails.size();
        String[] addrs = new String[numEs];
        for (int i = 0; i < numEs; i++) {
            addrs[i] = getEmail(i);
        }

        // TODO replace hardcoded email address with user defined address
        EmailThread emt = new EmailThread(addrs, getEmailProps(), I18n.getString("email_from_address"), message.toString(), subj);
        // emt.start();
        try {
            emt.sendEmail();
        }
        catch (MessagingException me) {
            logger.fatal("sendEmail() failed", me);
            setStatus(ERROR + " " + me.toString());
        }
    }


    private Properties getEmailProps() {
        //
        Properties retP = System.getProperties();
        retP.put("mail.smtp.host", gateway);
        retP.put("password", gatewayPwd);
        retP.put("username", gatewayUser);
        return retP;
    }


    public void addEmail(String toAdd) {
        adminEmails.add(toAdd);
    }


    public Object[] getEmails() {
        return adminEmails.toArray();
    }


    public void removeEmail(int toRem) {
        adminEmails.remove(toRem);
    }


    public String getEmail(int idx) {
        return adminEmails.get(idx);
    }


    public void setEmail(int idx, String em) {
        adminEmails.set(idx, em);
    }


    private boolean isTextEmailFormat() {
        boolean returnBool = true;

        if (emailFormat.equals(I18n.getString("html_format"))) {
            returnBool = false;
        }

        return returnBool;
    }


    private boolean sendNoticeOnIdxingChanges() {
        boolean returnBool = true;
        if (sendEmailNotice.equals("false")) {
            returnBool = false;
        }
        return returnBool;
    }


    private int numGetAdminEmails() {
        return adminEmails.size();
    }


    private void sendEmail(String subj, StringBuffer message) {
        int numEs = adminEmails.size();
        String[] addrs = new String[numEs];
        for (int i = 0; i < numEs; i++) {
            addrs[i] = getEmail(i);
        }

        // TODO replace hardcoded email address with user defined address
        EmailThread emt = new EmailThread(addrs, getEmailProps(), I18n.getString("email_from_address"), message.toString(), subj);
        // emt.start();
        try {
            emt.setTextFormat(isTextEmailFormat());
            emt.sendEmail();
        }
        catch (MessagingException me) {
            logger.fatal("sendEmail() failed", me);
            setStatus(ERROR + " " + me.toString());
        }
    }


    public ArrayList<DocSearcherIndex> getCdArrayList() {
        ArrayList<DocSearcherIndex> returnArry = new ArrayList<DocSearcherIndex>();
        int numIdxs = indexes.size();
        // boolean hasCdromIdxs=false;
        for (int i = 0; i < numIdxs; i++) {
            DocSearcherIndex di = indexes.get(i);
            if (di.isCdrom()) {
                // hasCdromIdxs=true;
                returnArry.add(di);
            }
        }
        return returnArry;
    }


    private void doSearchableCdWiz() {
        String CREATE_IDX_FIRST = I18n.getString("create_index_first");
        String CREATE_CD_STEPS = I18n.getString("create_index_info");

        int numIdxs = indexes.size();
        if (numIdxs == 0) {
            showMessage(I18n.getString("error"), CREATE_IDX_FIRST + "\n\n" + CREATE_CD_STEPS);
        }
        else {
            ArrayList<DocSearcherIndex> indexesToSelectFrom = new ArrayList<DocSearcherIndex>();
            boolean hasCdromIdxs = false;
            for (int i = 0; i < numIdxs; i++) {
                DocSearcherIndex di = indexes.get(i);
                if (di.isCdrom()) {
                    hasCdromIdxs = true;
                    indexesToSelectFrom.add(di);
                }
            }

            if (!hasCdromIdxs) {
                showMessage(I18n.getString("error"), CREATE_IDX_FIRST + "\n\n" + CREATE_CD_STEPS);
            }
            else {
                // set the defaults
                String cdRootDirName = FileUtils.addFolder(tempDir, "CD_ROOT_DS");
                String splashImageFileName = FileUtils.addFolder(fEnv.getStartDirectory(), FileEnvironment.FILENAME_SPLASH_IMAGE);
                String startPageFileName = FileUtils.addFolder(fEnv.getStartDirectory(), FileEnvironment.FILENAME_START_PAGE);
                String helpPageFileName = FileUtils.addFolder(fEnv.getStartDirectory(), FileEnvironment.FILENAME_HELP_PAGE);
                // boolean copyAllFiles=true;
                CdAssistantDialog cad = new CdAssistantDialog(this, Messages.getString("DocSearch.cdAssistant"), true);
                cad.init();
                cad.setFields(helpPageFileName, splashImageFileName, startPageFileName, cdRootDirName);
                cad.setVisible(true);
                if (cad.confirmed()) {
                    createCDStuff(cad.getCdRootDirName(), cad.getSplashImageFileName(), cad.getStartPageFileName(), cad.getHelpPageFileName(), cad.getCopyAllFiles(), cad.getCDIdxList());
                }
            } // end for we have CDROM indexes to work with
        } // end for we at least have some indexes
    }


    // TODO currently unused, check for remove
    @SuppressWarnings("unused")
    private String getCdsList(ArrayList<DocSearcherIndex> idxs) {
        StringBuffer rb = new StringBuffer();
        if (idxs.size() > 0) {
            Iterator<DocSearcherIndex> it = idxs.iterator();
            while (it.hasNext()) {
                DocSearcherIndex di = it.next();
                rb.append(di.getName());
                rb.append('\n');
            }
        }
        return rb.toString();
    }


    /**
     *
     * @param cdRootDirName
     * @param splashImageFileName
     * @param startPageFileName
     * @param helpPageFileName
     * @param copyAllFiles
     * @param indexesToSelectFrom
     */
    private void createCDStuff(String cdRootDirName, String splashImageFileName, String startPageFileName, String helpPageFileName, boolean copyAllFiles, ArrayList<DocSearcherIndex> indexesToSelectFrom) {
        if (logger.isInfoEnabled()) {
            logger.info("createCDStuff('" + cdRootDirName + "', '" + splashImageFileName + "', '" + startPageFileName + "', '" + helpPageFileName + "', '" + copyAllFiles + "', '" + indexesToSelectFrom + "') entered");
        }

        boolean madeStuff = true;
        StringBuffer errBuf = new StringBuffer();
        StringBuffer sB = new StringBuffer();

        sB.append("<html><head><title>DocSearcher Index List - CDROM</title></head><body>\n<table>");

        if (logger.isDebugEnabled()) {
            logger.debug("createCDStuff() check virtual cdrom root dir = '" + cdRootDirName + "'");
        }
        // CREATE THE CD ROOT DIR
        File cdrDirFi = new File(cdRootDirName);
        if (!cdrDirFi.exists()) {
            madeStuff = cdrDirFi.mkdir();
            if (!madeStuff) {
                errBuf.append('\n');
                // TODO check resource really not exist!
                errBuf.append(Messages.getString("DocSearch.unableToMkCDFold"));
            }
        }

        // COPY THE DOCSEARCH.JAR FILE
        if (madeStuff) {
            // TODO change some hard coded variables to constants!!!
            String newDocSearchFileName = FileUtils.addFolder(cdRootDirName, "DocSearch.jar");
            String oldDocSearchFileName = FileUtils.addFolder(fEnv.getStartDirectory(), "DocSearch.jar");
            String libsDir = FileUtils.addFolder(fEnv.getStartDirectory(), "lib");
            String newlibsFolder = FileUtils.addFolder(cdRootDirName, "lib");
            File testOldDS = new File(oldDocSearchFileName);
            if (testOldDS.exists()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("createCDStuff() copy DocSearch.jar  ('" + oldDocSearchFileName + "')");
                }
                copyFile(oldDocSearchFileName, newDocSearchFileName);
            }
            else {
                logger.error("createCDStuff() can't find DocSearch.jar ('" + oldDocSearchFileName + "')");
                madeStuff = false;
                errBuf.append('\n');
                // TODO check resource really not exist!
                errBuf.append(Messages.getString("DocSearch.unableToFindDSjar"));
            }

            // create lib dir and copy all libs
            File newLibsDir = new File(newlibsFolder);
            madeStuff = newLibsDir.mkdir();
            if (madeStuff) {
                File libsFolder = new File(libsDir);
                String[] libs = libsFolder.list();
                // TODO why not copyFilessss???
                for (int z = 0; z < libs.length; z++) {
                    String curLibFileName = FileUtils.addFolder(libsDir, libs[z]);
                    String newLibFileName = FileUtils.addFolder(newlibsFolder, libs[z]);
                    if (logger.isDebugEnabled()) {
                        logger.debug("createCDStuff() copy lib '" + curLibFileName + "' to '" + newLibFileName + "'");
                    }
                    copyFile(curLibFileName, newLibFileName);
                }
            }
            else {
                logger.error("createCDStuff() can't create lib dir ('" + newlibsFolder + "')");
                errBuf.append('\n');
                // TODO check resource really not exist!
                errBuf.append(Messages.getString("DocSearch.failToCreateDir") + " : " + newlibsFolder);
            }
        }

        // COPY THE START AND HELP PAGES AS WELL AS THE SPLASH and ICONS -
        // IF THEY EXIST!
        if (madeStuff) {
            String newSpFiName = FileUtils.addFolder(cdRootDirName, FileEnvironment.FILENAME_START_PAGE);
            File testSp = new File(startPageFileName);
            if (testSp.exists()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("createCDStuff() copy '" + startPageFileName + "' to '" + newSpFiName + "'");
                }
                copyFile(startPageFileName, newSpFiName);
            }
            String newHpFiName = FileUtils.addFolder(cdRootDirName, FileEnvironment.FILENAME_HELP_PAGE);
            File testHp = new File(helpPageFileName);
            if (testHp.exists()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("createCDStuff() copy '" + helpPageFileName + "' to '" + newHpFiName + "'");
                }
                copyFile(helpPageFileName, newHpFiName);
            }
            String newSplashFiName = FileUtils.addFolder(cdRootDirName, FileEnvironment.FILENAME_SPLASH_IMAGE);
            File testSplashFi = new File(splashImageFileName);
            if (testSplashFi.exists()) {
                if (logger.isDebugEnabled()) {
                    logger.debug("createCDStuff() copy '" + splashImageFileName + "' to '" + newSplashFiName + "'");
                }
                copyFile(splashImageFileName, newSplashFiName);
            }
        }

        // NOW COPY OVER THE INDEX FILES
        if (madeStuff) {
            // TODO change some hard coded variables to constants!!!
            String cdIdxsTemp = FileUtils.addFolder(cdRootDirName, "cdrom_indexes");
            String actualItdxsDir = FileUtils.addFolder(cdIdxsTemp, "indexes");
            File testCDIDXSTemp = new File(cdIdxsTemp);
            if (!testCDIDXSTemp.exists()) {
                madeStuff = testCDIDXSTemp.mkdir();
                if (!madeStuff) {
                    logger.error("createCDStuff() can't create dir '" + testCDIDXSTemp + "'");
                    errBuf.append('\n');
                    // TODO check resource really not exist!
                    errBuf.append(Messages.getString("DocSearch.failToCreateDir") + " : " + cdIdxsTemp);
                }
            }

            // create the sub dir for the indexes themsevels - ...indexes
            if (madeStuff) {
                File testActFi = new File(actualItdxsDir);
                if (!testActFi.exists()) {
                    madeStuff = testActFi.mkdir();
                    if (!madeStuff) {
                        logger.error("createCDStuff() can't create dir '" + testActFi + "'");
                        errBuf.append("\n");
                        // TODO check resource really not exist!
                        errBuf.append(Messages.getString("DocSearch.failToCreateDir") + " : " + actualItdxsDir);
                    }
                }
            }

            // copy the indexes
            if (madeStuff) {
                Iterator<DocSearcherIndex> it = indexesToSelectFrom.iterator();
                while (it.hasNext()) {
                    DocSearcherIndex di = it.next();
                    String curOldIdxDir = di.getIndexPath();
                    String curOldPath = di.getPath();
                    String curPnameOnly = Utils.getNameOnly(curOldPath);
                    String curNameOnly = Utils.getNameOnly(curOldIdxDir);
                    curOldIdxDir = FileUtils.addFolder(fEnv.getIndexDirectory(), curNameOnly);
                    String curNewIdxDir = FileUtils.addFolder(actualItdxsDir, curNameOnly);
                    String curNewPath = FileUtils.addFolder(cdRootDirName, curPnameOnly);

                    // create index dir
                    File newIdxFi = new File(curNewIdxDir);
                    madeStuff = newIdxFi.mkdir();
                    if (!madeStuff) {
                        logger.error("createCDStuff() can't create index dir '" + newIdxFi + "'");
                        errBuf.append("\n");
                        // TODO check resource really not exist!
                        errBuf.append(Messages.getString("DocSearch.failToCreateDir") + " : " + curNewIdxDir);
                    }
                    else {
                        logger.info("createCDStuff() start copying index files from '" + curOldIdxDir + "' to '" + curNewIdxDir + "'");
                        copyFiles(curOldIdxDir, curNewIdxDir);

                        // build our save buffer
                        sB.append("\n<tr>");
                        sB.append("\n<td>");
                        sB.append(di.getName());
                        sB.append("</td>");
                        sB.append("\n<td>");
                        sB.append(di.getPath());
                        sB.append("</td>");
                        sB.append("\n<td>");
                        if (di.getShouldBeSearched()) {
                            sB.append('0');
                        } else {
                            sB.append('1');
                        }
                        sB.append("</td>");
                        sB.append("\n<td>");
                        sB.append(di.getDepth());
                        sB.append("</td>");
                        sB.append("\n<td>");
                        sB.append(di.getIndexPath());
                        sB.append("</td>");
                        // now the isWeb stuff and date
                        sB.append("\n<td>");
                        if (di.getIsWeb()) {
                            sB.append("true");
                        } else {
                            sB.append("false");
                        }
                        sB.append("</td>");
                        sB.append("\n<td>");
                        sB.append(di.getMatch());
                        sB.append("</td>");
                        sB.append("\n<td>");
                        sB.append(di.getReplace());
                        sB.append("</td>");
                        sB.append("\n<td>");
                        sB.append(di.getLastIndexed());
                        sB.append("</td>");
                        sB.append("\n<td>");
                        sB.append(di.getIndexPolicy());
                        sB.append("</td>");
                        sB.append("\n<td>");
                        sB.append(di.getArchiveDir());
                        sB.append("</td>");
                        sB.append("</tr>\n");

                        // copy all files
                        if (copyAllFiles) {
                            // copy from di.path to CD Root
                            File testNewCFi = new File(curNewPath);
                            if (!testNewCFi.exists()) {
                                if (testNewCFi.mkdir()) {
                                    copyFiles(curOldPath, curNewPath);
                                }
                                else {
                                    logger.error("createCDStuff() can't create dir '" + testNewCFi + "'");
                                }
                            }
                        }

                        logger.info("createCDStuff() finished copying index files from '" + curOldIdxDir + "' to '" + curNewIdxDir + "'");
                    }
                }

                sB.append("\n</table></body></html>");

                // save cdrom index list
                FileUtils.saveFile(FileUtils.addFolder(cdIdxsTemp, "cdrom_indexes_list.htm"), sB);
            }
        }

        // check cd creating was successfully
        if (madeStuff) {
            showMessage(I18n.getString("ready_for_burning"), I18n.getString("made_cd_success") + " \n\t " + cdRootDirName);
        }
        else {
            // TODO check resource really not exist
            showMessage(Messages.getString("DocSearch.errCrtgCDStuff"), errBuf.toString());
        }
    }


    private boolean isSearching() {
        return currentlySearching;
    }


    /**
     * Start search
     */
    private void doThreadedSearch() {
        logger.debug("doThreadedSearch() entered");

        if ((searchField.getSelectedItem() != null) && (!isSearching())) {

            // search text
            String searchText = searchField.getSelectedItem().toString().trim();

            if (logger.isDebugEnabled()) {
                int selectedFields = searchIn.getSelectedIndex();
                String searchField = searchOpts[selectedFields];

                logger.debug("doThreadedSearch() " + Layout.LINE_SEP + "searchText=" + searchText + Layout.LINE_SEP + "searchField=" + searchField);
            }

            // start search
            if (!searchText.equals("")) {
                setStatus(I18n.getString("please_wait...") + " " + I18n.getString("searching"));

                // start search
                ThreadedSearch ts = new ThreadedSearch(this, searchText);
                SwingUtilities.invokeLater(ts);
            }
        }
    }


    private void doNewSpiderIdx() {
        // this is the method where spidered indexes are made
        SpiderDialog sd = new SpiderDialog(this, I18n.getString("new_spider_index"), true);
        sd.init();
        sd.setVisible(true);
        if (sd.getConfirmed()) {
            String spiderUrl = sd.getUrlFieldText().trim();
            String desc = sd.getDesc();
            int maxDocsToGet = sd.getMax();
            try {
                createSpiderIdx(spiderUrl, maxDocsToGet, desc);
            }
            catch (Exception e) {
                logger.fatal("doNewSpiderIdx() failed", e);
                setStatus(e.toString());
            }
        }
    }


    /**
     * Create Spider Index
     *
     * @param spiderUrl
     *            URL to spider
     * @param maxDocsToGet
     *            Max documents
     * @param desc
     *            Description
     * @throws IOException
     */
    private void createSpiderIdx(String spiderUrl, int maxDocsToGet, String desc) throws IOException {
        String outFile = FileUtils.addFolder(tempDir, "tmp_spider_links_" + USER_NAME + ".txt");
        String noUnd = Utils.replaceAll(" ", desc, "_");
        String idxFoldr = FileUtils.addFolder(fEnv.getIndexDirectory(), noUnd);

        // setup the index
        File idxpthfi = new File(idxFoldr);
        idxpthfi.mkdir();
        DocSearcherIndex dsi = new DocSearcherIndex(spiderUrl, desc, true, maxDocsToGet, idxFoldr, true, outFile, spiderUrl, DateTimeUtils.getToday(), 0, fEnv.getArchiveDirectory());
        IndexWriter writer = new IndexWriter(idxFoldr, new StandardAnalyzer(), true);
        // writer.setUseCompoundFile(true);

        // start spider
        LinkFinder lf = new LinkFinder(spiderUrl, maxDocsToGet, this, dsi, writer);
        lf.getAllLinks();

        writer.close(); // close the writer
        indexes.add(dsi);
    }


    public long getMaxFileSize() {
        return maxFileSizeInt;
    }


    protected void setMaxFileSize(long toGet) {
        maxFileSizeInt = toGet;
    }


    public String getCurStatus() {
        return curStatusString;
    }


    public String getDefaultHndlr() {
        return defaultHndlr;
    }


    public int getMaxNumHitsShown() {
        return maxNumHitsShown;
    }


    public boolean getLoadExternal() {
        return loadExternal;
    }


    public Iterator<DocSearcherIndex> getIndexIterator() {
        return indexes.iterator();
    }


    public boolean indexesEmpty() {
        return indexes.isEmpty();
    }


    public int numIndexes() {
        return indexes.size();
    }


    public DocSearcherIndex getDSIndex(int toGet) {
        return indexes.get(toGet);
    }


    public boolean getIsWorking() {
        return isWorking;
    }


    public void setIsWorking(boolean toSet) {
        isWorking = toSet;
        buttonStop.setEnabled(isWorking);
    }


    public void doStop() {
        setIsWorking(false);
    }


    public void setProgressMax(long toSet) {
        pPanel.setMaxPos(toSet);
    }


    public void setCurProgress(long toSet) {
        pPanel.setCurPos(toSet);
    }


    public void setCurProgressMSG(String toSet) {
        pPanel.setMSG(toSet);
    }


    public void resetProgress() {
        pPanel.reset();
    }


    public String getLafChosen() {
        return lafChosen;
    }


    /**
     * Method listAllProperties
     */
    private void listAllProps() {
        // Get all system properties
        // user.country = US, user.language = en
        String userCountry = System.getProperty("user.country");
        String userLanguage = System.getProperty("user.language");
        String localityType = userLanguage + '_' + userCountry;
        logger.log(NoticeLevel.NOTICE, "listAllProps() using language: " + localityType);

        // if (! localityType.toLowerCase().equals("en_us")) {
        // logger.log(NoticeLevel.NOTICE,
        // "listAllProps() To create a translation for your language; translate the file docSearch.properties and save it as docSearch_"
        // + localityType + ".properties");
        // }
    }
}
