import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/*
 * user-interface
 *  + graphical user interface, forms and actions 
 *  
 *  Copyright(c) 2009-2023, Daniel Sanders, All rights reserved.
 *  https://github.com/dsandersGit/GIT_Solver
 */

public class UI {
	public static 		JFrame jF = new JFrame();
	public static 		JTable tableClassify = new JTable();
	static 				JScrollPane sc= new JScrollPane(tableClassify);
	public static 		DefaultTableModel tmtableClassify;
	
	public static 		JTable tableStat = new JTable();
	static 				JScrollPane scStat= new JScrollPane(tableStat);
	public static 		DefaultTableModel tmtableStat;
	
	static 		JMenuBar 		mBar 				= null;
	static 		JLabel			labVars				= new JLabel("Vars: 0");
	static 		JLabel			labSamples			= new JLabel("Samples: 0");
	static 		JLabel			labClasses			= new JLabel("Classes: 0");
	//static 		JLabel			labStatus			= new JLabel("Status: ---");
	static 		JLabel			labStatusIcon		= null;
	static 		JLabel			labAccuracy			= new JLabel("---");
	static 		JProgressBar	proStatus			= new JProgressBar();
	static 		JLabel			labTimePerRun		= new JLabel("Process: ---");
	static 		JLabel			labRun				= new JLabel("RUN: ---");
	
	
	static JMenu menuFile = new JMenu( " File"); 
	static JMenu 		menuAction = new JMenu( " Action"); 
	static JMenuItem 	menuActionClassify = new JMenuItem(" Classify"); 
	static JMenuItem 	menuActionTrainImmediateStop = new JMenuItem(" Stop Training Now");
	static JMenuItem 	menuActionTrain = new JMenuItem(" Train"); 
	static JMenuItem 	menuFileSaveEnsemble = new JMenuItem(" Save Ensemble"); 
	static JMenuItem 	menuFileLoadEnsemble = new JMenuItem(" Load Ensemble"); 
	static JMenu 		menuExport = new JMenu( " Export");
	static JMenuItem 	menuFileSplitData = new JMenuItem(" Split Data"); 
	
	static int tab_Classify = 0;
	static int tab_Train = 1;
	static int tab_Distance = 1;
	static int tab_Algo = 1;
	static int tab_Accuracy = 4;
	static int tab_3D = -1;
	static int tab_Opts = 1;
	static int tab_Summary = 0;
	static int tab_Statistics = 0;
	
	public static 		JTabbedPane maintabbed 		= new JTabbedPane();
	public static 		SP_PlotCanvas sp 			= new SP_PlotCanvas();
	public static 		SP_PlotCanvas spDst 		= new SP_PlotCanvas();
	public static 		SP_PlotCanvas sp1D 			= new SP_PlotCanvas();
	public static 		SP_PlotCanvas sp2D 			= new SP_PlotCanvas();
	public static 		SP_PlotCanvas spSpread 		= new SP_PlotCanvas();
//	public static 		HeatMap heatMap				= new HeatMap();
	public static 		JTextArea txtOpts 			= new JTextArea();
	public static 		JTextArea txtEnsemble 		= new JTextArea();
	static 				JScrollPane scEnsemble		= new JScrollPane(txtEnsemble);
	public static 		JTextArea txtSummary 		= new JTextArea();
	static 				JScrollPane scSummary		= new JScrollPane(txtSummary);
	public static 		ThreeDee tab3D = new ThreeDee();
	static 				JLabel iconAlgo = new Icon_Solver();
	
	static JButton jbLoad = new JButton();
	static JButton jbTrain = new JButton();
	static JButton jbLoadEns = new JButton();
	static JButton jbSaveEns = new JButton();
	static JButton jbClassify = new JButton();
	static JButton jb_Stop = new JButton("Stop Training");
	static JButton jb_DefaultOptions = new JButton("Default Options");
	
	static JPanel panLive = new JPanel();
	static JPanel panLiveAcc = new JPanel();
	
	public UI() {

		ImageIcon icon_App		= 	new ImageIcon(ClassLoader.getSystemResource("icon_solver.png"));
		jF.setIconImage(icon_App.getImage());
		
		
		initTables();
		sc = new JScrollPane(tableClassify);
		sc.setOpaque(false);
		sc.setBackground(SolverStart.backColor);
		sc.setPreferredSize(new Dimension (800,600));
		scStat = new JScrollPane(tableStat);
		scStat.setOpaque(false);
		scStat.setBackground(SolverStart.backColor);
		scStat.setPreferredSize(new Dimension (800,600));
		
		jF.setJMenuBar(setMyMenu());
		sp.setTitle("Accuracy and Gain");
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
		
		txtSummary.setEditable(false);

		txtOpts.setOpaque(false);
		txtOpts.setBackground(SolverStart.backColor);
		txtOpts.setForeground(SolverStart.frontColor);
		txtOpts.setFont(new Font("Consolas", Font.PLAIN, 20));
	
		txtEnsemble.setOpaque(false);
		txtEnsemble.setEditable(false);
		txtEnsemble.setBackground(SolverStart.backColor);
		txtEnsemble.setForeground(SolverStart.frontColor);
		txtEnsemble.setFont(new Font("Consolas", Font.PLAIN, 12));
		
		txtSummary.setOpaque(false);
		txtSummary.setLineWrap(true);
		txtSummary.setBackground(SolverStart.backColor);
		txtSummary.setForeground(SolverStart.frontColor);
		txtSummary.setFont(new Font("Consolas", Font.PLAIN, 12));
		
		panLive.setLayout(new GridLayout(2,1));
		panLive.add(sp);
		panLive.add(spDst);
		
		panLiveAcc.setLayout(new GridLayout(2,1));
		panLiveAcc.add(sp1D);
		panLiveAcc.add(sp2D);
		
		JPanel panOptions = new JPanel();
		panOptions.setLayout(new BorderLayout());
		panOptions.add(txtOpts, BorderLayout.CENTER);
		panOptions.add(jb_DefaultOptions, BorderLayout.SOUTH);
		jb_DefaultOptions.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			txtOpts.setText(SolverStart.defOptions.toString(3));
		}});
		
		maintabbed.add("Data",scSummary);
		maintabbed.add("Options",panOptions);
		maintabbed.add("Live", panLive);
		maintabbed.add("Validation",scStat);
		maintabbed.add("Trends", panLiveAcc);
		maintabbed.add("Classification", sc);
		maintabbed.addTab("3D",tab3D);
		maintabbed.add("Ensemble",scEnsemble);
		maintabbed.add("Algorithm", iconAlgo);
		maintabbed.add("spSpread", spSpread);
		
//		maintabbed.add("[Exp.] HeatMap", heatMap);
		
		sp1D.setTitle("Accuracy Development");
		sp1D.setXAxis("# cycle");
		sp1D.setYAxis("accuracy [%]");
		sp2D.setTitle("Sum of Loadings");
		sp2D.setXAxis("# variable");
		sp2D.setYAxis("loadings ");
		
		
		tab_Classify 	= 5;
		tab_Train 		= 2;
		tab_Distance 	= 2;
		tab_Accuracy 	= 4;
		tab_Algo		= 7;
		tab_3D 			= 6;
		tab_Opts 		= 1;
		tab_Summary 	= 0;
		tab_Statistics 	= 3;
		
		txtOpts.setWrapStyleWord(true);
		txtOpts.setLineWrap(true);
		txtOpts.setText(Opts.getOptsAsJson().toString(3));
		
		jF.add(getControlToolBar(), BorderLayout.EAST);
		
		JPanel main = new JPanel();
		main.setLayout(new GridLayout(1,1));
		main.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		main.add(maintabbed);
		jF.add(main,BorderLayout.CENTER);
		jF.add(initStatusBar(),BorderLayout.SOUTH);
		jF.pack();
		jF.setLocationRelativeTo(null);
		jF.setVisible(true);
		jF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		refreshStatus();
	
	}
	
	private static JPanel initStatusBar() {
		
		JPanel pan = new JPanel();
		JPanel panL = new JPanel();
		JPanel panR = new JPanel();
		pan.setLayout(new GridLayout(2,1));
		panL.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panR.setLayout(new FlowLayout(FlowLayout.RIGHT));
		
		jb_Stop.setEnabled(false);
		jb_Stop.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			SolverStart.immediateStop = true;
		}});
		labStatusIcon = new JLabel(new ImageIcon(ClassLoader.getSystemResource("colBlue.png")));
		
		panL.add(labRun);
		panL.add(labAccuracy);
		panL.add(labSamples);
		panL.add(labVars);
		panL.add(labClasses);
		
		panR.add(labTimePerRun);		
		panR.add(proStatus);
		panR.add(jb_Stop);
		panR.add(labStatusIcon);
		
		pan.add(panL);
		pan.add(panR);
		return pan;
	}
	static boolean refreshOptions() {

		JSONObject jo_Opts = null; 
		 try {
			 jo_Opts = new JSONObject(txtOpts.getText());
		    } catch (JSONException e) {
		    	txtOpts.setText(Opts.getOptsAsJson().toString(3));
		        return false;
		    }
		 try {
			if (jo_Opts.has("normType")) 	Opts.normType 		= jo_Opts.getString("normType");
			if (jo_Opts.has("numDims")) 	Opts.numDims 		= jo_Opts.getInt("numDims");
			if (jo_Opts.has("trainRatio"))	Opts.trainRatio 	= jo_Opts.getDouble("trainRatio");
			if (jo_Opts.has("numCycles")) 	Opts.numCycles 		= jo_Opts.getInt("numCycles");
			if (jo_Opts.has("noBetterStop"))Opts.noBetterStop 	= jo_Opts.getInt("noBetterStop");
			if (jo_Opts.has("minBetter")) 	Opts.minBetter 		= jo_Opts.getDouble("minBetter");
			if (jo_Opts.has("plotTimer")) 	Opts.plotTimer 		= jo_Opts.getInt("plotTimer");
			if (jo_Opts.has("fixTrainSet")) Opts.fixTrainSet 	= jo_Opts.getBoolean("fixTrainSet");
			if (jo_Opts.has("activation")) 	Opts.activation		= jo_Opts.getString("activation");

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
		jbLoad.setEnabled(true);
		jbSaveEns.setEnabled(true);
		jbTrain.setEnabled(true);
		jbClassify.setEnabled(true);
		menuActionTrain.setEnabled(true);
		menuActionClassify.setEnabled(true);
		jb_Stop.setEnabled(false);
		jbLoadEns.setEnabled(true);
		
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
			JSONObject ds 		= DS.js_Ensemble.getJSONObject("DS");
			if (ds.has("variableID")) {
			String modelVarID = ds.getString("variableID");
			if ( DS.variableID.length() > 0)
				if ( !DS.variableID.equals(modelVarID))
					ensMatchData = false;	
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
			jbLoad.setEnabled(false);
			jbLoadEns.setEnabled(false);
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
		
		tableClassify = new JTable() {
	        private static final long serialVersionUID = 1L;
	        public boolean isCellEditable(int row, int column) {                
	                return false;               
	        };
	    };
	    tmtableClassify =(DefaultTableModel) tableClassify.getModel();
		tableClassify.getTableHeader().setOpaque(false);
		tableClassify.getTableHeader().setBackground(SolverStart.backColor);
		Font fnt = tableClassify.getTableHeader().getFont(); 
		tableClassify.getTableHeader().setFont(fnt.deriveFont(Font.BOLD));
		
		tableClassify.getTableHeader().setReorderingAllowed(false);
		tableClassify.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableClassify.setAutoCreateRowSorter(true);
		
		
		tableStat= new JTable() {
	        private static final long serialVersionUID = 1L;
	        public boolean isCellEditable(int row, int column) {                
	                return false;               
	        };
	    };
		tmtableStat =(DefaultTableModel) tableStat.getModel();
		tableStat.getTableHeader().setOpaque(false);
		tableStat.getTableHeader().setBackground(SolverStart.backColor);
		fnt = tableStat.getTableHeader().getFont(); 
		tableStat.getTableHeader().setFont(fnt.deriveFont(Font.BOLD));
		
		tableStat.getTableHeader().setReorderingAllowed(false);
		tableStat.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//tableStat.setAutoCreateRowSorter(true);
		tmtableStat.addColumn("run");
		tmtableStat.addColumn("type");
		tmtableStat.addColumn("Target");
		tmtableStat.addColumn("TP_Train");
		tmtableStat.addColumn("FP_Train");
		tmtableStat.addColumn("TN_Train");
		tmtableStat.addColumn("FN_Train");
		tmtableStat.addColumn("Sensitivity_Train");
		tmtableStat.addColumn("Speciticity_Train");
		
		tmtableStat.addColumn("TP_Validation");
		tmtableStat.addColumn("FP_Validation");
		tmtableStat.addColumn("TN_Validation");
		tmtableStat.addColumn("FN_Validation");
		tmtableStat.addColumn("Sensitivity_Validation");
		tmtableStat.addColumn("Speciticity_Validation");
		tmtableStat.addColumn("Accuracy_Validation");

		tableClassify.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
	           
	            if ( column>6) {
		            double val =  0;
		            try {
		            	val = Double.parseDouble((String)(""+value));
		            }catch(NumberFormatException ee){
		        	}
		            int red = 0;
		            int green = (int) (val * 255);
		            if ( green > 255) green = 255;
		            if ( green < 0 ) {
		            	green = 0;red = -green;
		            	if ( red>255)red = 255;
		            }
		            if ( val < 0.5) {
		            	setForeground(Color.WHITE);
		            }else {
		            	setForeground(Color.BLACK);
		            }
		            setBackground(new Color(red,green, 100));
	            }
	            return this;
	        }
	    });
		
		tableStat.addMouseListener(new MouseAdapter() {
//	          public void mouseClicked(MouseEvent e) {
//	              for (int i=0;i<tableStat.getSelectedRows().length;i++) {
//	              	boolean[] kill = new boolean[tableStat.getSelectedRows().length]
//	              	int val = tableStat.getSelectedRows()[i];
//	              	System.out.println(tmtableStat.getValueAt(val, 0));
//	              }
//	          }
	          public void mouseReleased(MouseEvent e){
	        	  if(e.getModifiers()!=InputEvent.BUTTON3_MASK) return;
	        	  int selCount = tableStat.getSelectedRows().length;
	        	  JPopupMenu inPOP = new JPopupMenu();
	        	  JMenuItem item=null;
	        	  item = new JMenuItem("Delete selected models");
	        	  if(selCount==0)item.setEnabled(false);
	        	  inPOP.add(item);
				  item.addActionListener(new ActionListener(){
					  public void actionPerformed(ActionEvent e) {
						  boolean[] killList = new boolean [DS.freezs.size()];
						  for (int j= DS.freezs.size()-1;j>=0;j--) {
								if(tableStat.isRowSelected(j)) {
									killList[j] = true;
								}else {
									killList[j] = false;
								}
						  }
						  for (int j= DS.freezs.size()-1;j>=0;j--) {
							  if ( killList[j] ) {
								  DS.freezs.remove(j);
								  tmtableStat.removeRow(j);
							  }
						  }
						  Tools.compileModels();
						  tableStat.repaint();
					  }});
				  inPOP.show(e.getComponent(), e.getX(), e.getY());
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
			loadEnsemble(false);
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
		menuFileLoadClip.setAccelerator(KeyStroke.getKeyStroke('V',InputEvent.CTRL_DOWN_MASK));
		menuFile.add(menuFileLoadClip);
		menuFile.add(new JSeparator());
		menuFile.add(menuFileSplitData);
		menuFile.add(new JSeparator());
		menuFile.add(menuFileLoadEnsemble);
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
		JMenuItem menuExportClassification = new JMenuItem(" Classification"); 
		menuExport.add(menuExportClassification);
		JMenuItem menuExportTP_FP_TN_FN = new JMenuItem(" Validation"); 
		menuExport.add(menuExportTP_FP_TN_FN);
		JMenuItem menuExportDistances = new JMenuItem(" 1D Scores"); 
		menuExport.add(menuExportDistances);
		menuExport.addSeparator();
		JMenuItem menuExportPlot = new JMenuItem(" Copy current plot "); 
		menuExport.add(menuExportPlot);
		// ---------------------------------------------------------------------
		JMenu menuAbout = new JMenu( " About");
		JMenuItem menuAboutAbout = new JMenuItem(" About "+SolverStart.app); 
		menuAbout.add(menuAboutAbout);
		JMenuItem menuAboutLicense = new JMenuItem(" License"); 
		menuAbout.add(menuAboutLicense);
		JMenuItem menuAboutCredits = new JMenuItem(" Credits"); 
		menuAbout.add(menuAboutCredits);
		// ---------------------------------------------------------------------
		mBar.add(menuFile);
		mBar.add(menuAction);
		mBar.add(menuExport);
		mBar.add(menuAbout);
		
		menuFileLoadDsFormat.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			loadData();
		}});
		menuFileLoadClip.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			SolverStart.importDataCSV(null);
			new DS();												// INITS
			DS.normParas = Tools.doNormData ();				// Daten Normalisieren

			
			DS.txtSummary = null;
			DS.fileName =  "Clipboard";
			tmtableClassify.setColumnCount(0);
			tmtableClassify.setRowCount(0);
			
			new DS();												// INITS
			
			DS.normParas = Tools.doNormData ();				// Daten Normalisieren
		
			UI.maintabbed.setSelectedIndex(UI.tab_Summary);
			refreshStatus();
			SolverStart.analyzeRawData("Clipboard");
			UI.maintabbed.setSelectedIndex(UI.tab_Summary);
		}});
		menuFileSplitData.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			refreshOptions();
			StringBuffer outTrain = new StringBuffer();
			StringBuffer outTest = new StringBuffer();
			outTrain.append("Class");
        	for (int j=0;j< DS.numVars; j++) {
        		outTrain.append("," + DS.AreaNames[j]);	
        	}
        	outTrain.append("\n");
        	outTest.append("Class");
        	for (int j=0;j< DS.numVars; j++) {
        		outTest.append("," + DS.AreaNames[j]);	
        	}
        	outTest.append("\n");
        	
			int targetCount=0;
			ArrayList<Integer> tgts = new ArrayList<Integer>();
			for (int f=0;f<DS.numSamples;f++){
                tgts.add(f);
                targetCount++;
	        }
			int count = 0;
	        while (count < targetCount*(1.-Opts.trainRatio)) {
	            int rnd = (int) (Math.random()*tgts.size());
	            tgts.remove(rnd);
	            count++;
	        }
	        for (int f=0;f<DS.numSamples;f++){
	        	String cls = DS.ClassNames[f];
	        	
	        	if ( cls.length()<1 || Tools.isNumeric(cls) ) {
	        		cls = "class"+DS.classAllIndices[Tools.getIndexOfTarget(DS.classIndex[f])];
	        	}
	        	boolean isIn = false;
	            for (int i=0;i<tgts.size();i++){
	                if (f == tgts.get(i)) {
	                	outTrain.append(cls);
	                	for (int j=0;j< DS.numVars; j++) {
	                		outTrain.append("," + DS.rawData[f][j]);	
	                	}
	                	outTrain.append("\n");
	                	isIn = true;
	                }
	            }
	            if ( !isIn ) {
	            	outTest.append(cls);
                	for (int j=0;j< DS.numVars; j++) {
                		outTest.append("," + DS.rawData[f][j]);	
                	}
                	outTest.append("\n");
	            }
	        }
			
			Preferences prefs;
		    prefs = Preferences.userRoot().node("Solver");
		    String path = prefs.get("path", ".");
		    String[] type = {"CSV"};
		    String[] sht = {"CSV"};
			File f = Tools.getFile("Select file to save", path,type,sht, true);
			if ( f == null) return;

			File fTrain = new File(f.getAbsolutePath()+"_Train.csv");
			File fTest  = new File(f.getAbsolutePath()+"_Test.csv");
			
			FileWriter fwTrain = null;
			FileWriter fwTest = null;
			try {
				fwTrain = new FileWriter(fTrain, false);
				fwTest = new FileWriter(fTest, false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		    BufferedWriter bwTrain = new BufferedWriter(fwTrain);
		    BufferedWriter bwTest = new BufferedWriter(fwTest);
		    try {
		    	fwTrain.write(outTrain.toString());
		    	fwTest.write(outTest.toString());
			} catch (JSONException | IOException e1) {
				e1.printStackTrace();
			}
		  
		    try {
		    	bwTrain.close();
		    	bwTest.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}});
		menuFileLoadEnsemble.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			loadEnsemble(false);
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
		
//		menuActionOptions.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){	
//		
//			JLabel lab_noBetter = new JLabel ("Number of tolerated non-improvement " + Opts.noBetterStop );
//			JSlider sld_noBetter = new JSlider (100,10000,1000);
//			sld_noBetter.setMajorTickSpacing(500);
//			sld_noBetter.setMinorTickSpacing(100);
//			sld_noBetter.setPaintTicks(true);
//			sld_noBetter.addChangeListener(new ChangeListener() {
//		        @Override
//		        public void stateChanged(ChangeEvent event) {
//		            int val = ((JSlider)event.getSource()).getValue();
//		            lab_noBetter.setText("Number of tolerated non-improvement " + val);
//		        }
//		    });
//			JLabel lab_actMode = new JLabel ("Set activation function");
//			String[] actModes = {"DxA", "D+A", "A"};
//			JComboBox<String> cbo_act = new JComboBox<String>(actModes);
//			
//			JLabel lab_dstMode = new JLabel ("Set distance function");
//			String[] dstModes = {"GROUP", "EGO"};
//			JComboBox<String> cbo_dst = new JComboBox<String>(dstModes);
//			
//			JLabel lab_train = new JLabel ("Set train/test ratio");
//			JSlider sld_train = new JSlider (10,100,70);
//			sld_train.setMajorTickSpacing(10);
//			sld_train.setPaintTicks(true);
//			sld_train.addChangeListener(new ChangeListener() {
//		        @Override
//		        public void stateChanged(ChangeEvent event) {
//		            int val = ((JSlider)event.getSource()).getValue();
//		            lab_train.setText("Set train/test ratio " + ((float)val/100.));
//		        }
//		    });
//			JCheckBox chk_fixTrain = new JCheckBox("Fix trainset", Opts.fixTrainSet);
//			
//			JPanel jp = new JPanel();
//		 	jp.setLayout(new BoxLayout(jp,BoxLayout.Y_AXIS));
//			Object[] array = new Object[1];
//			jp.add(lab_noBetter);
//			jp.add(sld_noBetter);
//			jp.add(lab_actMode);
//			jp.add(cbo_act);
//			jp.add(lab_dstMode);
//			jp.add(cbo_dst);
//			jp.add(lab_train);
//			jp.add(sld_train);
//			jp.add(chk_fixTrain);
//			array[0] = jp;
//		    int eingabe = JOptionPane.showConfirmDialog(null,array,"Options", JOptionPane.OK_CANCEL_OPTION);
//		    if(eingabe != 0){
//		    	return;
//		    }
//			
//		}});
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
	
		menuExportClassification.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){	
			
			StringBuffer out = new StringBuffer();
			for (int i=0;i<tableClassify.getColumnCount();i++) {
				if ( i>0 ) out.append("\t");
				out.append(tableClassify.getColumnName(i));
			}
			out.append("\n");
			for (int y=0;y<tableClassify.getRowCount();y++) {
				for (int x=0;x<tableClassify.getColumnCount();x++) {	
					if ( x>0 ) out.append("\t");
					out.append(tableClassify.getValueAt(y, x));
				}
				out.append("\n");
			}
			StringSelection stringSelection = new StringSelection( out.toString() );
		    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents( stringSelection, stringSelection );
		}});
		menuExportTP_FP_TN_FN.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			StringBuffer out = new StringBuffer();
			
			out.append("Validation\tTarget\tTP\tFP\tTN\tFN\tTEST\tTP\tFP\tTN\tFN]");
			out.append("\n");
			for (int i=0;i<DS.freezs.size();i++) {
				MC_Freeze mc = DS.freezs.get(i);

				out.append(i+1);
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
		menuExportPlot.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			BufferedImage img=null;
			if(maintabbed.getSelectedIndex()==tab_Distance ){
				 img = new BufferedImage(panLive.getWidth(), panLive.getHeight(), BufferedImage.TYPE_INT_RGB);
			    Graphics g = img.getGraphics();
			    g.setColor(panLive.getForeground());
			    g.setFont(panLive.getFont());
			    panLive.paintAll(g);
			 }
			if(maintabbed.getSelectedIndex()==tab_Accuracy ){
				 img = new BufferedImage(panLiveAcc.getWidth(), panLiveAcc.getHeight(), BufferedImage.TYPE_INT_RGB);
			    Graphics g = img.getGraphics();
			    g.setColor(panLiveAcc.getForeground());
			    g.setFont(panLiveAcc.getFont());
			    panLiveAcc.paintAll(g);
			 }
			if(maintabbed.getSelectedIndex()==tab_Algo ){
				 img = new BufferedImage(iconAlgo.getWidth(), iconAlgo.getHeight(), BufferedImage.TYPE_INT_RGB);
			    Graphics g = img.getGraphics();
			    g.setColor(iconAlgo.getForeground());
			    g.setFont(iconAlgo.getFont());
			    iconAlgo.paintAll(g);
			 }
			if(maintabbed.getSelectedIndex()==tab_3D ){
				 img = new BufferedImage(tab3D.getWidth(), tab3D.getHeight(), BufferedImage.TYPE_INT_RGB);
			    Graphics g = img.getGraphics();
			    g.setColor(tab3D.getForeground());
			    g.setFont(tab3D.getFont());
			    tab3D.paintAll(g);
			 }
			 if(img!=null){
				    ImageTransferable it = new ImageTransferable(img);
			        Clipboard clip=Toolkit.getDefaultToolkit().getSystemClipboard();
			        clip.setContents(it,null);
				 }
		}});
		
		// *******************************
		menuAboutAbout.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			JOptionPane.showMessageDialog(jF, "<HTML><H3>"+SolverStart.app+" rev."+SolverStart.revision+"</H3>"
					+ "<I>Copyright 2009-2023 Daniel Sanders</I><BR>Check for updates:<BR>"
					+ "https://github.com/dsandersGit/GIT_Solver/tags");
		}});
		menuAboutLicense.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			JOptionPane.showMessageDialog(jF, "<HTML><I>Licensed under GNU General Public License v3.0</I>", SolverStart.app,  JOptionPane.INFORMATION_MESSAGE);
		}});
		menuAboutCredits.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			JOptionPane.showMessageDialog(null, "<HTML> used libraries and dependencies: <ul>"
					+ "<li>JSON java libraries from JSON.org</li>"
					//+ "<li>FlatLaf - Flat Look and Feel Theme</li>"
					+ "</ul><BR>", "CREDITS", JOptionPane.INFORMATION_MESSAGE);
		}});
		//-------------------------------------------------------------------------------------------------------------------
		return mBar;
	}
	
	
	private static void train() {
		DS.js_Ensemble = null;
		
		DS.freezs.clear();
		
		if ( !refreshOptions() ) {
			JOptionPane.showMessageDialog(jF, "<HTML><H3>Options malformed > set to last well-formed</H3>");
			return;
		}
		
		SolverStart.immediateStop = false;
		Runner.cleanRunner ();
		tmtableClassify.setColumnCount(0);
		tmtableClassify.setRowCount(0);
		maintabbed.setSelectedIndex(tab_Distance);
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
		
		prefs.put("lastEnsemble", f.getAbsolutePath());
		
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
	static void loadEnsemble(boolean auto) {
		Preferences prefs;
	    prefs = Preferences.userRoot().node("Solver");
	    File f = null;
	    if ( auto ) {
	    	f = new File ( prefs.get("lastEnsemble", ".") );
	    	if ( !f.exists() || !f.getName().toLowerCase().endsWith(".ens") )
	    		return;
	    }else {
		    String path = prefs.get("path", ".");
		    String[] type = {"DS_Ensemble"};
		    String[] sht = {"ENS"};
			f = Tools.getFile("Select dataset file", path,type,sht, false);
			if ( f == null) return;
			if ( !f.exists()) return;
	    }
		
		
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
	    String[] type = {"CSV, DS_Data"};
	    String[] sht = {"CSV", "DAT"};
		File f = Tools.getFile("Select dataset file", path,type,sht, false);
		if ( f == null) return;
		if ( !f.exists()) return;
		prefs.put("path", f.getParent());
		if ( f.getName().toLowerCase().endsWith(".dat"))
			SolverStart.importData(f.getAbsolutePath());
		if ( f.getName().toLowerCase().endsWith(".csv"))
			SolverStart.importDataCSV(f.getAbsolutePath());
		
		DS.fixedTrainSet = null;
		DS.txtSummary = null;
		DS.fileName =  f.getName();
		tmtableClassify.setColumnCount(0);
		tmtableClassify.setRowCount(0);
		
		new DS();												// INITS
		
//		SolverStart.bootstrap() ;
		
		DS.normParas = Tools.doNormData ();				// Daten Normalisieren
		SolverStart.dataFileName = f.getName();
		
		SolverStart.analyzeRawData(f.getName());
		UI.maintabbed.setSelectedIndex(UI.tab_Summary);
		refreshStatus();
	}
	
}
