import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class UI {
	public static 		JFrame jF = new JFrame();
	public static 		JTable table = new JTable();
	static 				JScrollPane sc= new JScrollPane(table);
	public static 		DefaultTableModel tmtable;
	
	public static 		JTable tableStat = new JTable();
	static 				JScrollPane scStat= new JScrollPane(tableStat);
	public static 		DefaultTableModel tmtableStat;
	
	static 		JMenuBar 		mBar 				= null;
	static 		JLabel			labVars				= new JLabel("Vars: 0");
	static 		JLabel			labSamples			= new JLabel("Samples: 0");
	static 		JLabel			labClasses			= new JLabel("Classes: 0");
	static 		JLabel			labStatus			= new JLabel("Status: ---");
	static 		JLabel			labStatusIcon		= null;
	static 		JLabel			labAccuracy			= new JLabel("---");
	static 		JProgressBar	proStatus			= new JProgressBar();
	static 		JLabel			labTimePerRun		= new JLabel("Process: ---");
	
	
	static JMenu menuFile = new JMenu( " File"); 
	static JMenu 		menuAction = new JMenu( " Action"); 
	static JMenuItem 	menuActionClassify = new JMenuItem(" Classify"); 
	static JMenuItem 	menuActionTrainImmediateStop = new JMenuItem(" Stop Training Now");
	static JMenuItem 	menuActionTrain = new JMenuItem(" Train"); 
	static JMenuItem 	menuFileSaveEnsemble = new JMenuItem(" Save Ensemble"); 
	static JMenuItem 	menuFileLoadEnsemble = new JMenuItem(" Load Ensemble"); 
	static JMenu 		menuExport = new JMenu( " Export");
	
	static int tab_Classify = 0;
	static int tab_Train = 1;
	static int tab_Distance = 1;
	static int tab_3D = 1;
	static int tab_Opts = 1;
	static int tab_Summary = 0;
	static int tab_Statistics = 0;
	
	public static 		JTabbedPane maintabbed = new JTabbedPane();
	public static 		SP_PlotCanvas sp = new SP_PlotCanvas();
	public static 		SP_PlotCanvas spDst = new SP_PlotCanvas();
	
	public static 		JTextArea txtOpts = new JTextArea();
	public static 		JTextArea txtEnsemble = new JTextArea();
	static 				JScrollPane scEnsemble= new JScrollPane(txtEnsemble);
	public static 		JTextArea txtSummary = new JTextArea();
	static 				JScrollPane scSummary= new JScrollPane(txtSummary);
	public static 		ThreeDee tab3D = new ThreeDee();
	
	static JButton jbLoad = new JButton();
	static JButton jbTrain = new JButton();
	static JButton jbLoadEns = new JButton();
	static JButton jbSaveEns = new JButton();
	static JButton jbClassify = new JButton();
	static JButton jb_Stop = new JButton("Stop Training");
	
	
	public UI() {

		ImageIcon icon_App		= 	new ImageIcon(ClassLoader.getSystemResource("icon_solver.png"));
		jF.setIconImage(icon_App.getImage());
		
		
		initTables();
		sc = new JScrollPane(table);
		sc.setOpaque(false);
		sc.setBackground(SolverStart.backColor);
		sc.setPreferredSize(new Dimension (800,600));
		scStat = new JScrollPane(tableStat);
		scStat.setOpaque(false);
		scStat.setBackground(SolverStart.backColor);
		scStat.setPreferredSize(new Dimension (800,600));
		
		jF.setJMenuBar(setMyMenu());
		sp.setTitle("Accuracy and Activation");
		sp.setXAxis("run");
		sp.setYAxis("accuracy");
		spDst.setMargin(100,50);
		sp.setInnerBackGroundColor(SolverStart.backColor);
		sp.setOuterBackGroundColor(SolverStart.backColor);
		sp.setBaseColor(SolverStart.frontColor);
		
		spDst.setTitle("SCORES");
		spDst.setXAxis("score");
		spDst.setYAxis("sequence");
		spDst.setMargin(100,50);
		spDst.setInnerBackGroundColor(SolverStart.backColor);
		spDst.setOuterBackGroundColor(SolverStart.backColor);
		spDst.setBaseColor(SolverStart.frontColor);
		txtOpts.setOpaque(false);
		txtOpts.setBackground(SolverStart.backColor);
		txtOpts.setForeground(SolverStart.frontColor);
		txtOpts.setFont(new Font("Consolas", Font.PLAIN, 20));
		txtEnsemble.setOpaque(false);
		txtEnsemble.setBackground(SolverStart.backColor);
		txtEnsemble.setForeground(SolverStart.frontColor);
		txtEnsemble.setFont(new Font("Consolas", Font.PLAIN, 12));
		
		txtSummary.setOpaque(false);
		txtSummary.setLineWrap(true);
		txtSummary.setBackground(SolverStart.backColor);
		txtSummary.setForeground(SolverStart.frontColor);
		txtSummary.setFont(new Font("Consolas", Font.PLAIN, 12));
		
		JPanel panLive = new JPanel();
		panLive.setLayout(new GridLayout(2,1));
		panLive.add(sp);
		panLive.add(spDst);
		
		maintabbed.add("Data",scSummary);
		maintabbed.add("Options",txtOpts);
		maintabbed.add("Live", panLive);
		maintabbed.addTab("3D",tab3D);
		maintabbed.add("Validation",scStat);
		maintabbed.add("Ensemble",scEnsemble);
		maintabbed.add("Classification", sc);
		
		tab_Classify 	= 6;
		tab_Train 		= 2;
		tab_Distance 	= 2;
		tab_3D 			= 3;
		tab_Opts 		= 1;
		tab_Summary 	= 0;
		tab_Statistics 	= 4;
		
		
		txtOpts.setWrapStyleWord(true);
		txtOpts.setLineWrap(true);
		txtOpts.setText(Opts.getOptsAsJson().toString(3));
		
		jF.add(getControlToolBar(), BorderLayout.EAST);
		
		jF.add(maintabbed,BorderLayout.CENTER);
		jF.add(initStatusBar(),BorderLayout.SOUTH);
		jF.pack();
		jF.setLocationRelativeTo(null);
		jF.setVisible(true);
		jF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		refreshStatus();
	
	}
	
	private static JPanel initStatusBar() {
		
		JPanel pan = new JPanel();
		pan.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		jb_Stop.setEnabled(false);
		jb_Stop.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			SolverStart.immediateStop = true;
		}});
		
		labStatusIcon = new JLabel(new ImageIcon(ClassLoader.getSystemResource("colBlue.png")));
		
		pan.add(labTimePerRun);
		pan.add(proStatus);
		pan.add(labStatus);
		pan.add(labAccuracy);
		pan.add(labSamples);
		pan.add(labVars);
		pan.add(labClasses);
		pan.add(jb_Stop);
		pan.add(labStatusIcon);
		return pan;
	}
	static boolean refreshOptions() {

		JSONObject jo_Opts = null; //new JSONObject(txtOpts.getText());
		 try {
			 jo_Opts = new JSONObject(txtOpts.getText());
		    } catch (JSONException e) {
		    	txtOpts.setText(Opts.getOptsAsJson().toString(3));
		        return false;
		    }
		 try {
			Opts.dstType 		= jo_Opts.getString("dstType");
			Opts.normType 		= jo_Opts.getString("normType");
			Opts.numDims 		= jo_Opts.getInt("numDims");
			Opts.trainRatio 	= jo_Opts.getDouble("trainRatio");
			Opts.numEnsemble 	= jo_Opts.getInt("numEnsemble");
			Opts.noBetterStop 	= jo_Opts.getInt("noBetterStop");
			Opts.minBetter 		= jo_Opts.getDouble("minBetter");
			Opts.plotTimer 		= jo_Opts.getInt("plotTimer");
			Opts.fixTrainSet 	= jo_Opts.getBoolean("fixTrainSet");
			Opts.doTheLeft 		= jo_Opts.getBoolean("doTheLeft");
			Opts.kickStart 		= jo_Opts.getBoolean("kickStart");
			Opts.activation		= jo_Opts.getString("activation");
		  } catch (JSONException e) {
			  txtOpts.setText(Opts.getOptsAsJson().toString(3));
		        return false;
		  }
		 
		return true;
	}
	public static void refreshStatus() {
		
		
		menuActionClassify.setEnabled(true);
		menuFileSaveEnsemble.setEnabled(true);
		menuFileLoadEnsemble.setEnabled(true);
		menuExport.setEnabled(true);
		jbSaveEns.setEnabled(true);
		jbTrain.setEnabled(true);
		jbClassify.setEnabled(true);
		menuActionTrain.setEnabled(true);
		menuActionClassify.setEnabled(true);
		jb_Stop.setEnabled(false);
		
		jF.setTitle(SolverStart.app+SolverStart.appAdd+" ["+SolverStart.dataFileName+"]");
		
		boolean noData 			= false;
		boolean noEns 			= false;
		boolean ensMatchData 	= false;
		boolean isRunning 		= false;
		
		if ( DS.numClasses < 2) {
			noData = true;
		}
		labVars.setText("Vars: " + DS.numVars);
		labSamples.setText("Samples: " + DS.numSamples);
		labClasses.setText("Classes: " + DS.numClasses);
		if ( DS.js_Ensemble != null) {
			ensMatchData = true;
			String[] ensVarNames = Tools.getvarNamesFromEnsemble ( );
			if ( ensVarNames.length != DS.numVars) {
				ensMatchData = false;
			}else {
				for (int i=0;i<DS.numVars;i++) {
					if ( !DS.AreaNames[i].equals(ensVarNames[i]))ensMatchData = false;	
				}
			}
			
		}else {
			noEns = true;
			menuActionClassify.setEnabled(false);
			menuFileSaveEnsemble.setEnabled(false);
			menuExport.setEnabled(false);
			jbSaveEns.setEnabled(false);
			jbClassify.setEnabled(false);
		}
		isRunning =  SolverStart.isRunning;
		UI.labStatusIcon.setIcon(new ImageIcon(ClassLoader.getSystemResource("col13.png")));
		if ( noData ) {
			UI.labStatusIcon.setIcon(new ImageIcon(ClassLoader.getSystemResource("colBlue.png")));
			jbTrain.setEnabled(false);
			jbClassify.setEnabled(false);
			menuActionTrain.setEnabled(false);
			menuActionClassify.setEnabled(false);
		}
		if ( isRunning ) {
			UI.labStatusIcon.setIcon(new ImageIcon(ClassLoader.getSystemResource("colYellow.png")));
			menuActionClassify.setEnabled(false);
			jbClassify.setEnabled(false);
			menuFileLoadEnsemble.setEnabled(false);
			menuFileSaveEnsemble.setEnabled(false);
			menuExport.setEnabled(false);
			jbSaveEns.setEnabled(false);
			jbTrain.setEnabled(false);
			menuActionTrain.setEnabled(false);
			jb_Stop.setEnabled(true);
		}
		
		if ( !noEns)
			if ( !ensMatchData ) {	
				UI.labStatusIcon.setIcon(new ImageIcon(ClassLoader.getSystemResource("colRed.png")));
				menuActionClassify.setEnabled(false);
				jbClassify.setEnabled(false);
			}else {
				UI.labStatusIcon.setIcon(new ImageIcon(ClassLoader.getSystemResource("colGreen.png")));
				menuActionClassify.setEnabled(true);
				jbClassify.setEnabled(true);
			}
		
		
	}
	
	private static void initTables() {
		
		table = new JTable();
		tmtable =(DefaultTableModel) table.getModel();
		table.getTableHeader().setOpaque(false);
		table.getTableHeader().setBackground(SolverStart.backColor);
		Font fnt = table.getTableHeader().getFont(); 
		table.getTableHeader().setFont(fnt.deriveFont(Font.BOLD));
		
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setAutoCreateRowSorter(true);
		
		tableStat= new JTable();
		tmtableStat =(DefaultTableModel) tableStat.getModel();
		tableStat.getTableHeader().setOpaque(false);
		tableStat.getTableHeader().setBackground(SolverStart.backColor);
		fnt = tableStat.getTableHeader().getFont(); 
		tableStat.getTableHeader().setFont(fnt.deriveFont(Font.BOLD));
		
		tableStat.getTableHeader().setReorderingAllowed(false);
		tableStat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableStat.setAutoCreateRowSorter(true);
		tmtableStat.addColumn("run");
		tmtableStat.addColumn("Target");
		tmtableStat.addColumn("TP_Train");
		tmtableStat.addColumn("FP_Train");
		tmtableStat.addColumn("TN_Train");
		tmtableStat.addColumn("FN_Train");
		tmtableStat.addColumn("Sensitivity_Train");
		tmtableStat.addColumn("Speciticity_Train");
		
		tmtableStat.addColumn("TP_Test");
		tmtableStat.addColumn("FP_Test");
		tmtableStat.addColumn("TN_Test");
		tmtableStat.addColumn("FN_Test");
		tmtableStat.addColumn("Sensitivity_Test");
		tmtableStat.addColumn("Speciticity_Test");
		
		table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
	        /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
	        public Component getTableCellRendererComponent(JTable table,
	                Object value, boolean isSelected, boolean hasFocus,
	                int row, int column) {

	            JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	            Font font = label.getFont();
	            label.setFont(font.deriveFont(Font.PLAIN));
	            setBackground(SolverStart.backColor);setForeground(SolverStart.frontColor);
	            
	            if ( column>5) {
		            double val =  0;
		            try {
		            	val = Double.parseDouble((String)(""+value));
		            }catch(NumberFormatException ee){
		        	}
		            int green = (int) (val * 255);
		            if ( green > 255) green = 255;
		            if ( val < 0.5) {
		            	setForeground(Color.WHITE);
		            }else {
		            	setForeground(Color.BLACK);
		            }
		            setBackground(new Color(100,green, 100));
	            }
	            return this;
	        }
	
	    });
	}
	
	private JToolBar getControlToolBar(){
		JToolBar toolbar = new JToolBar("TOOLBAR"); 
		toolbar.setFloatable(true);
		toolbar.setOrientation(JToolBar.VERTICAL);
		toolbar.setBorder( BorderFactory.createRaisedBevelBorder() );
		
		jbLoad.setToolTipText("<HTML><B>Select dataset to load</B></HTML>");
		jbTrain.setToolTipText("<HTML><B>Train dataset</B></HTML>");
		jbLoadEns.setToolTipText("<HTML><B>Load Model Ensemble</B></HTML>");
		jbSaveEns.setToolTipText("<HTML><B>Save Model Ensemble</B></HTML>");
		jbClassify.setToolTipText("<HTML><B>Classify Dataset using Model Ensemble</B></HTML>");
		
		jbLoad.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			loadData();
		}});
		jbTrain.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			if (SolverStart.isRunning) {
				SolverStart.immediateStop = true;
			}else {
				train();
			}
		}});
		jbLoadEns.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			loadEnsemble();
		}});
		jbSaveEns.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			saveEnsemble();
		}});
		jbClassify.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			maintabbed.setSelectedIndex(tab_Classify);
			SolverStart.classify();
			refreshStatus();
		}});
		ClassLoader.getSystemClassLoader();
		jbLoad.setIcon( new ImageIcon(ClassLoader.getSystemResource("icon_data.png")));
		jbTrain.setIcon( new ImageIcon(ClassLoader.getSystemResource("icon_cpu.png")));
		jbLoadEns.setIcon( new ImageIcon(ClassLoader.getSystemResource("icon_load_ens.png")));
		jbSaveEns.setIcon( new ImageIcon(ClassLoader.getSystemResource("icon_save_ens.png")));
		jbClassify.setIcon( new ImageIcon(ClassLoader.getSystemResource("icon_classify.png")));
		
		toolbar.add(jbLoad);
		toolbar.addSeparator();
		toolbar.add(jbTrain);
		toolbar.addSeparator();
		toolbar.add(jbLoadEns);
		toolbar.add(jbSaveEns);
		toolbar.addSeparator();
		toolbar.add(jbClassify);
					
		JSeparator ts = new  JSeparator(JSeparator.VERTICAL);
		ts.setMinimumSize(new Dimension(2,1));
		ts.setPreferredSize(new Dimension(2,1));
		ts.setSize(new Dimension(2,1));
		toolbar.add(ts);
		
		return toolbar;
	}
	private static JMenuBar setMyMenu(){
		mBar = new JMenuBar();
		
		

		JMenuItem menuFileLoadDsFormat = new JMenuItem(" Load Dataset"); 
		menuFileLoadDsFormat.setAccelerator(KeyStroke.getKeyStroke('L',InputEvent.CTRL_DOWN_MASK));
		menuFile.add(menuFileLoadDsFormat);
		JMenuItem menuFileLoadClip = new JMenuItem(" Import from Clipboard"); 
		//menuFileLoadClip.setToolTipText("TOOLTIP not yet set"); 
		//menuFileLoadClip.setEnabled(false);
		menuFile.add(menuFileLoadClip);
		menuFile.add(new JSeparator());
		
		//menuFileLoadEnsemble.setToolTipText("TOOLTIP not yet set"); 
		menuFile.add(menuFileLoadEnsemble);
		//menuFileSaveEnsemble.setToolTipText("TOOLTIP not yet set"); 
		menuFile.add(menuFileSaveEnsemble);
		menuFileSaveEnsemble.setAccelerator(KeyStroke.getKeyStroke('S',InputEvent.CTRL_DOWN_MASK));
		menuFile.add(new JSeparator());
		JMenuItem menuFileExit = new JMenuItem(" Quit"); 
		menuFileExit.setAccelerator(KeyStroke.getKeyStroke('Q',InputEvent.CTRL_DOWN_MASK));
		menuFile.add(menuFileExit);	
		// ---------------------------------------------------------------------		
		menuAction.add(menuActionTrain);
		menuActionTrain.setAccelerator(KeyStroke.getKeyStroke('T',InputEvent.CTRL_DOWN_MASK));
		menuAction.add(menuActionTrainImmediateStop);
		menuActionTrainImmediateStop.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_CANCEL, InputEvent.CTRL_DOWN_MASK));
		menuAction.add(new JSeparator());
		menuAction.add(menuActionClassify);	
		// ---------------------------------------------------------------------
		
		JMenuItem menuExportVectors = new JMenuItem(" Vectors"); 
		menuExport.add(menuExportVectors);
		JMenuItem menuExportVectorsWeight = new JMenuItem(" Weighed Vectors (VIP Scores)"); 
		menuExport.add(menuExportVectorsWeight);
		JMenuItem menuExportClassification = new JMenuItem(" Classification"); 
		menuExport.add(menuExportClassification);
		JMenuItem menuExportTP_FP_TN_FN = new JMenuItem(" Validation"); 
		menuExport.add(menuExportTP_FP_TN_FN);
		JMenuItem menuExportDistances = new JMenuItem(" 1D Scores"); 
		menuExport.add(menuExportDistances);
		
		// ---------------------------------------------------------------------
		JMenu menuAbout = new JMenu( " About");
//		JMenuItem menuAboutAbout = new JMenuItem(" About "+SolverStart.app); 
//		menuAbout.add(menuAboutAbout);
		JMenuItem menuAboutLicense = new JMenuItem(" License"); 
		menuAbout.add(menuAboutLicense);
//		JMenuItem menuAboutCredits = new JMenuItem(" Credits"); 
//		menuAbout.add(menuAboutCredits);
		// ---------------------------------------------------------------------
		// -------------------------------------------------------------------------------------
		mBar.add(menuFile);
		mBar.add(menuAction);
		mBar.add(menuExport);
		mBar.add(menuAbout);
		
		menuFileLoadDsFormat.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			loadData();
		}});
		menuFileLoadClip.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			if ( !SolverStart.importDataFromClipboard()) return;
			new DS();												// INITS
			DS.normParas = Tools.doNormData ();				// Daten Normalisieren

			refreshStatus();
			SolverStart.analyzeRawData("Clipboard");
			UI.maintabbed.setSelectedIndex(UI.tab_Summary);
		}});
		menuFileLoadEnsemble.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			loadEnsemble();
		}});
		menuFileSaveEnsemble.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){	
			saveEnsemble();
		    
		}});
		menuFileExit.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			System.exit(0);
		}});

		menuActionTrainImmediateStop.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			SolverStart.immediateStop = true;
		}});
		menuActionTrain.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			train();
		}});
		menuActionClassify.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){	
			maintabbed.setSelectedIndex(tab_Classify);
			SolverStart.classify();
		}});
		// *******************************
		menuExportVectors.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){	
			JSONObject ensemble = DS.js_Ensemble;
			if (ensemble== null )return;
			JSONArray models = ensemble.getJSONArray("model");
			
			StringBuffer out = new StringBuffer();
			out.append("Model Vectors"+"\n");
			out.append("#model\tdim\tTargetClass");
			for (int i=0;i<DS.AreaNames.length; i++) {
				out.append("\t"+DS.AreaNames[i]);
			}
			out.append("\n");
			for (int i=0;i<models.length(); i++) {
				JSONObject in = models.getJSONObject(i);
				int numDims = in.getInt("Opts.numDims");
				//
				for (int p=0;p<numDims; p++) {
					out.append(i+"\t");
					out.append(p+"\t"+in.getString("targetLabel"));
					String vTemp = in.getString(		"Vector"+p);
					String[] line = vTemp.split(","); 
					for (int a=0; a<line.length;a++) {
						out.append("\t"+Double.parseDouble(line[a].trim()));
					}
					out.append("\n");
				}

			}
			StringSelection stringSelection = new StringSelection( out.toString() );
		    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents( stringSelection, stringSelection );
		}});
		menuExportVectorsWeight.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){	
			JSONObject ensemble = DS.js_Ensemble;
			if (ensemble== null )return;
			JSONArray models = ensemble.getJSONArray("model");
			JSONObject ds = ensemble.getJSONObject("DS");
			String VariableNames = ds.getString("VariableNames");
			String[] varNames = VariableNames.split(",");
	
			double[] weight = new double[varNames.length];
			for (int i=0;i<models.length(); i++) {
				JSONObject in = models.getJSONObject(i);
				int numDims = in.getInt("Opts.numDims");
				for (int p=0;p<numDims; p++) {
					String vTemp = in.getString(		"Vector"+p);
					String[] line = vTemp.split(","); 
					for (int a=0; a<line.length;a++) {
						weight[a] += Double.parseDouble(line[a].trim())*Double.parseDouble(line[a].trim());
					}
				}

			}
			for (int a=0; a<weight.length;a++) {
				weight[a] = Math.sqrt(weight[a]);
			}
			StringBuffer out = new StringBuffer();
			out.append("Weighted Vectors" + "\n");
			for (int a=0; a<weight.length;a++) {
				out.append(varNames[a]+"\t"+weight[a] +"\n");
			}
			StringSelection stringSelection = new StringSelection( out.toString() );
		    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents( stringSelection, stringSelection );
		}});
		menuExportClassification.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){	
			
			StringBuffer out = new StringBuffer();
			for (int i=0;i<table.getColumnCount();i++) {
				if ( i>0 ) out.append("\t");
				out.append(table.getColumnName(i));
			}
			out.append("\n");
			for (int y=0;y<table.getRowCount();y++) {
				for (int x=0;x<table.getColumnCount();x++) {	
					if ( x>0 ) out.append("\t");
					out.append(table.getValueAt(y, x));
				}
				out.append("\n");
			}
			StringSelection stringSelection = new StringSelection( out.toString() );
		    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents( stringSelection, stringSelection );
		}});
		menuExportTP_FP_TN_FN.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			StringBuffer out = new StringBuffer();
			
			out.append("Validation\tTRAIN\tTP\tFP\tTN\tFN\tTEST\tTP\tFP\tTN\tFN]");
			out.append("\n");
			for (int i=0;i<DS.freezs.size();i++) {
				MC_Freeze mc = DS.freezs.get(i);

				out.append(i);
				out.append("\t"+Tools.txtLen(DS.classAllIndNme[Tools.getIndexOfTarget(mc.targetColorIndex)]));
				out.append("\t"+mc.tp_fp_tn_fn[0][0]);
				out.append("\t"+mc.tp_fp_tn_fn[1][0]);
				out.append("\t"+mc.tp_fp_tn_fn[2][0]);
				out.append("\t"+mc.tp_fp_tn_fn[3][0]);
				out.append("\t");	
				out.append("\t"+mc.tp_fp_tn_fn[0][1]);
				out.append("\t"+mc.tp_fp_tn_fn[1][1]);
				out.append("\t"+mc.tp_fp_tn_fn[2][1]);
				out.append("\t"+mc.tp_fp_tn_fn[3][1]);

				out.append("\n");
			}
			StringSelection stringSelection = new StringSelection( out.toString() );
		    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents( stringSelection, stringSelection );
		}});
		menuExportDistances.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			StringBuffer out = new StringBuffer();
			out.append("1D-SCORES");
			out.append("\tCN"+"\tCL"+"\tS");
			for (int i=0;i<DS.freezs.size();i++) {
				MC_Freeze mc = DS.freezs.get(i);
				out.append("\t"+"#" + i + "|" + mc.targetLabel);
			}
			out.append("\n");
			for (int j=0;j<DS.numSamples;j++) {
				for (int i=0;i<DS.freezs.size();i++) {
					if ( i== 0 ) out.append("\t"+DS.ClassNames[j]+"\t"+DS.classIndex[j]+"\t"+DS.SampleNames[j]);
					MC_Freeze mc = DS.freezs.get(i);
					out.append("\t"+mc.distances[j]);
				}
				out.append("\n");
			}

			StringSelection stringSelection = new StringSelection( out.toString() );
		    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents( stringSelection, stringSelection );
		}});
		
		
		// *******************************
//		menuAboutAbout.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
//			JOptionPane.showMessageDialog(jF, "<HTML><H3>"+SolverStart.app+"</H3><I>Copyright 2009-2023 Daniel Sanders</I><BR><BR><SMALL>dsanders@gmx.net</SMALL><BR>");
//		}});
		menuAboutLicense.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			JOptionPane.showConfirmDialog(jF, "<HTML><H3>Licensed under GNU General Public License v3.0</H3>", SolverStart.app, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		}});
//		menuAboutCredits.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
//			JOptionPane.showMessageDialog(null, "<HTML>Autho</li></ul><BR>", SolverStart.app, JOptionPane.INFORMATION_MESSAGE);
//		}});
		//-------------------------------------------------------------------------------------------------------------------
		return mBar;
	}
	
	
	private static void train() {
		DS.js_Ensemble = null;
		if ( !refreshOptions() ) {
			JOptionPane.showMessageDialog(jF, "<HTML><H3>Options malformed > set to last well-formed</H3>");
			return;
		}
		
		SolverStart.immediateStop = false;
		Runner.cleanRunner ();
		tmtable.setColumnCount(0);
		tmtable.setRowCount(0);
		maintabbed.setSelectedIndex(tab_Statistics);
		jF.setTitle(SolverStart.app+SolverStart.appAdd+" ["+SolverStart.dataFileName+"]");
		refreshStatus();
		
			Thread thread = new Thread(new Runnable()
			{
			   public void run()
			   {
				   try {
				   SolverStart.trainPattern();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			   }
			});
			thread.start();
	}
	private static void saveEnsemble() {
		if ( DS.js_Ensemble == null) return;
		Preferences prefs;
	    prefs = Preferences.userRoot().node("Solver");
	    String path = prefs.get("path", ".");
	    String[] type = {"DS_Ensemble"};
	    String[] sht = {"ENS"};
		File f = Tools.getFile("Select file to save", path,type,sht, true);
		
		//File f = getFile("Save ensemble file", SolverStart.app,"DS_Ensemble", "ENS", true);
		if ( f == null) return;
		
		if ( !f.getName().toLowerCase().endsWith(".ens")) {
			f = new File(f.getAbsolutePath()+".ens");
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(f, false);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    BufferedWriter bw = new BufferedWriter(fw);
	    try {
			bw.write(DS.js_Ensemble.toString(3));
		} catch (JSONException | IOException e1) {
			e1.printStackTrace();
		}
	    try {
			bw.newLine();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    try {
			bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	private static void loadEnsemble() {
		Preferences prefs;
	    prefs = Preferences.userRoot().node("Solver");
	    String path = prefs.get("path", ".");
	    String[] type = {"DS_Ensemble"};
	    String[] sht = {"ENS"};
		File f = Tools.getFile("Select dataset file", path,type,sht, false);
		
		if ( f == null) return;
		if ( !f.exists()) return;
		DS.setEnsemble(Classify.readEnsemble(f.getAbsolutePath()));
		if ( DS.js_Ensemble == null ) {
			JOptionPane.showMessageDialog(jF, "NOT VALID ENSEMBLE");
			return;
		}
		boolean corrupt = Classify.checkEnsembleFingerPrints();
		if ( corrupt ) {
			JOptionPane.showMessageDialog(jF, "CORRUPT ENSEMBLE");
			DS.js_Ensemble = null;
		}
		refreshStatus();
		if ( DS.js_Ensemble != null ) {
			Classify.setOptions();
			UI.txtEnsemble.setText(DS.js_Ensemble.toString(3));
		}
		
	}
	private static void loadData() {
		if ( !refreshOptions() ) {
			JOptionPane.showMessageDialog(jF, "<HTML><H3>Options malformed > set to default</H3>");
			return;
		}
		
		Preferences prefs;
	    prefs = Preferences.userRoot().node("Solver");
	    String path = prefs.get("path", ".");
	    String[] type = {"CSV" , "DS_Data"};
	    String[] sht = {"CSV" , "DAT"};
		File f = Tools.getFile("Select dataset file", path,type,sht, false);
		if ( f == null) return;
		if ( !f.exists()) return;
		prefs.put("path", f.getParent());
		if ( f.getName().toLowerCase().endsWith(".dat"))
			SolverStart.importData(f.getAbsolutePath());
		if ( f.getName().toLowerCase().endsWith(".csv"))
			SolverStart.importDataCSV(f.getAbsolutePath());
		
		
		DS.txtSummary = null;
		DS.fileName =  f.getName();
		tmtable.setColumnCount(0);
		tmtable.setRowCount(0);
		
		
		new DS();												// INITS
		DS.normParas = Tools.doNormData ();				// Daten Normalisieren
		SolverStart.dataFileName = f.getName();
		
		SolverStart.analyzeRawData(f.getName());
		UI.maintabbed.setSelectedIndex(UI.tab_Summary);
		refreshStatus();
	}
	
}
