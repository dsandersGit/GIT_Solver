import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
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
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/*
 * user-interface
 *  + graphical user interface, forms and actions 
 *  
 *  Copyright(c) 2009-2024, Daniel Sanders, All rights reserved.
 *  https://github.com/dsandersGit/GIT_Solver
 */

public class UI {
	public static 		JFrame jF = new JFrame();
	public static 		JTable tableClassify = new JTable();
	static 				JScrollPane scClassify= new JScrollPane(tableClassify);
	public static 		DefaultTableModel tmtableClassify;
	
	public static 		JTable tableConfusionMatrix = new JTable();
	static 				JScrollPane scConfusionMatrix= new JScrollPane(tableConfusionMatrix);
	public static 		DefaultTableModel tmConfusionMatrix;
	
	public static 		JTable tableValidation = new JTable();
	static 				JScrollPane scValidation= new JScrollPane(tableValidation);
	public static 		DefaultTableModel tmtableValidation;
	
	
	static 		JMenuBar 		mBar 				= null;
	static 		JLabel			labVars				= new JLabel("Features: 0");
	static 		JLabel			labSamples			= new JLabel("Samples: 0");
	static 		JLabel			labClasses			= new JLabel("Classes: 0");
	//static 		JLabel			labStatus			= new JLabel("Status: ---");
	static 		JLabel			labStatusIcon		= null;
	static 		JLabel			labAccuracy			= new JLabel("---");
	static 		JProgressBar	proStatus			= new JProgressBar();
//	static 		JProgressBar	proNoBetterStop		= new JProgressBar();
	static 		JLabel			labTimePerRun		= new JLabel("Process: ---");
	static 		JLabel			labRun				= new JLabel("RUN: ---");
	
	
	static JMenu 				menuFile = new JMenu( " File"); 
	static JMenu 				menuAction = new JMenu( " Action");
	static JMenu 				menuSettings = new JMenu( " Settings"); 
	static JMenuItem 			menuActionClassify = new JMenuItem(" Classify"); 
	static JMenuItem 			menuActionTrainImmediateStop = new JMenuItem(" Stop Training Now");
	
	static JMenuItem 			menuActionTrain = new JMenuItem(" Train");
	static JMenuItem 			menuActionBatchTrain = new JMenuItem(" Train Batch"); 
	static JMenuItem 			menuFileSaveEnsemble = new JMenuItem(" Save Ensemble"); 
	static JMenuItem 			menuFileLoadEnsemble = new JMenuItem(" Load Ensemble"); 
	static JMenuItem 			menuActionOptions = new JMenuItem(" Algorithm Options");
	static JMenuItem 			menuActionChk_QR = new JCheckBoxMenuItem(" QR-Receipt",true);
	static JCheckBoxMenuItem 	menuActionChk_Jump = new JCheckBoxMenuItem(" Jump Tabs",true);
	static JCheckBoxMenuItem 	menuActionChk_SendEns = new JCheckBoxMenuItem(" Dump Ensemble on Save",false);				// 129 Send Ensemble to Caller
	
	static JMenu 				menuExport = new JMenu( " Export");
	static JMenuItem 			menuFileSaveData = new JMenuItem(" Save Data");  
	
	static int tab_Classify = 0;
	static int tab_Live = 1;
	static int tab_Algo = 1;
	static int tab_Trends = 4;
	static int tab_3D = -1;
	static int tab_ensemble = -1;
	static int tab_Summary = 0;
	static int tab_Statistics = 0;
	static int tab_spMining = 0;
	static int tab_PCA = 9;
	
	public static 		JTabbedPane maintabbed 		= new JTabbedPane();
	public static 		SP_PlotCanvas sp 			= new SP_PlotCanvas();
	public static 		SP_PlotCanvas spDst 		= new SP_PlotCanvas();
	public static 		SP_PlotCanvas sp1D 			= new SP_PlotCanvas();
	public static 		SP_PlotCanvas sp2D 			= new SP_PlotCanvas();
	public static 		Pan_Mining	 panMining		= new Pan_Mining();
	
	//public static 		SP_PlotCanvas spSpread 		= new SP_PlotCanvas();
//	public static 		HeatMap heatMap				= new HeatMap();
	public static 		JTextArea txtOpts 			= new JTextArea();

//	public static 		JTextArea txtEnsemble 		= new JTextArea();
//	static 				JScrollPane scEnsemble		= new JScrollPane(txtEnsemble);
	public static 		JTextArea txtSummary 		= new JTextArea();
	static 				JScrollPane scSummary		= new JScrollPane(txtSummary);
	public static 		ThreeDee tab3D = new ThreeDee();
	public static 		ThreeDee tabPCA3D = new ThreeDee();
	public static 		JPanel panPCA3D = new JPanel();
//	static 				JLabel iconAlgo = new Icon_Solver();
	static 				JLabel iconClassify = null;
	
	static 				JButton jbLoad 	= new JButton();
	static 				JButton jbTrain = new JButton();
	static 				JButton jbLoadEns = new JButton();
	static 				JButton jbSaveEns = new JButton();
	static 				JButton jbClassify = new JButton();
	static 				JButton jb_Stop = new JButton("Stop Training");
	static 				JButton jB_Shuffle = new JButton(" Shuffle Now");
	static 				JButton jB_Skip = new JButton(" Next");
	static 				JButton jb_DefaultOptions = new JButton("Default Options");
	
	static JPanel panLive = new JPanel();
	static JPanel panTrends = new JPanel();
	
	
	public UI() {

		ImageIcon icon_App		= 	new ImageIcon(ClassLoader.getSystemResource("icon_solver.png"));
		jF.setIconImage(icon_App.getImage());
		
		
		initTables();
		scClassify = new JScrollPane(tableClassify);
		scClassify.setOpaque(false);
		scClassify.setBackground(SolverStart.backColor);
		
		
		scConfusionMatrix =  new JScrollPane(tableConfusionMatrix );
		scConfusionMatrix.setOpaque(false);
		scConfusionMatrix.setBackground(SolverStart.backColor);
		
		//scClassify.setPreferredSize(new Dimension (500,400));
		JPanel pan_classify = new JPanel();
		//pan_classify.setLayout(new BoxLayout(pan_classify,BoxLayout.Y_AXIS));
		pan_classify.setLayout(new GridLayout(2,1));
		pan_classify.add(scClassify);
		pan_classify.add(scConfusionMatrix);
		
		scValidation = new JScrollPane(tableValidation);
		scValidation.setOpaque(false);
		scValidation.setBackground(SolverStart.backColor);
		scValidation.setPreferredSize(new Dimension (800,600));
		
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
		
		sp1D.setInnerBackGroundColor(SolverStart.backColor);
		sp1D.setOuterBackGroundColor(SolverStart.backColor);
		sp1D.setBaseColor(SolverStart.frontColor);
		sp2D.setInnerBackGroundColor(SolverStart.backColor);
		sp2D.setOuterBackGroundColor(SolverStart.backColor);
		sp2D.setBaseColor(SolverStart.frontColor);
		
		txtSummary.setEditable(false);
		
		txtOpts.setOpaque(false);
		txtOpts.setBackground(SolverStart.backColor);
		txtOpts.setForeground(SolverStart.frontColor);
		txtOpts.setFont(new Font("Consolas", Font.PLAIN, 20));
		
		ThreeDee.genBackColor 	=	 SolverStart.backColor;
		ThreeDee.genFrontColor 	= SolverStart.frontColor;
		tab3D.setOpaque(true);

		txtSummary.setOpaque(false);
		txtSummary.setLineWrap(true);
		txtSummary.setBackground(SolverStart.backColor);
		txtSummary.setForeground(SolverStart.frontColor);
		txtSummary.setFont(new Font("Consolas", Font.PLAIN, 12));
		
		panLive.setLayout(new GridLayout(2,1));
		panLive.add(sp);
		panLive.add(spDst);
		
		
		panTrends.setLayout(new GridLayout(2,1));
		panTrends.add(sp1D);
		panTrends.add(sp2D);
		
		panPCA3D.setLayout(new BorderLayout());
		panPCA3D.add(tabPCA3D, BorderLayout.CENTER);
		
		JButton jb_SavePCA = new JButton("Copy PCA & Model");
		jb_SavePCA.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			StringBuffer out = new StringBuffer();
			out.append(MultiVariate_R.pcaDataExport);
			out.append(MultiVariate_R.pcaModelExport);
			StringSelection stringSelection = new StringSelection(  out.toString() );
		    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents( stringSelection, stringSelection );
		}});
		//JButton jb_ApplyPCA = new JButton("Apply PCA Model");
		JButton jb_SetPcaData = new JButton("Set PCA Model as DataSet");
		jb_SetPcaData.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			if ( MultiVariate_R.pcaDataExport == null ) return;
			File f = null;
			try {
				f = File.createTempFile("temp", null);
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			if ( f== null ) return;
	        f.deleteOnExit();
			FileWriter fw = null;
			try {
				fw = new FileWriter(f, false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		    BufferedWriter bw = new BufferedWriter(fw);
		    try {
		    	fw.write(MultiVariate_R.pcaDataExport.toString());
			} catch (JSONException | IOException e1) {
				e1.printStackTrace();
			}
		  
		    try {
		    	bw.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		    SolverStart.importDataCSV(f.getAbsolutePath(), "\t");
			DS.txtSummary = null;
			DS.fileName =  "PCA";
			
			DS.filePath = null;
			tmtableClassify.setColumnCount(0);
			tmtableClassify.setRowCount(0);
			new DS();												// INITS
			DS.normParas = Tools.doNormData ();				// Daten Normalisieren
			DS.isPCA = true;
			
			refreshStatus();
			SolverStart.analyzeRawData(DS.fileName );
			if ( UI.menuActionChk_Jump.isSelected() )UI.maintabbed.setSelectedIndex(UI.tab_Summary);
		}});

		
		
		JPanel pan_actPca = new JPanel();
		pan_actPca.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pan_actPca.add(jb_SavePCA);
		//pan_actPca.add(jb_ApplyPCA);
		pan_actPca.add(jb_SetPcaData);
		panPCA3D.add(pan_actPca,BorderLayout.SOUTH);
		
		maintabbed.add("Data",scSummary);
		maintabbed.add("PCA",panPCA3D);
		maintabbed.add("Visual Check", panMining);
		maintabbed.add("Live", panLive);
		maintabbed.add("Validation",scValidation);
		maintabbed.add("Trends", panTrends);
		maintabbed.add("Classification", pan_classify);
		maintabbed.addTab("3D",tab3D);
//		maintabbed.add("Ensemble",scEnsemble);
//		maintabbed.add("Algorithm", iconAlgo);
		maintabbed.add("Ensemble",new EnsembleTree());
	
		
		
		tab_Summary 	= 0;
		tab_PCA			= 1;
		tab_spMining	= 2;
		tab_Live 		= 3;
		tab_Statistics 	= 4;
		tab_Trends	 	= 5;
		tab_Classify 	= 6;
		tab_3D 			= 7;
		tab_ensemble	= 8;
//		tab_Algo		= 7;
		

//		tab_Opts 		= 1;
		
		
		
		
		ChangeListener changeListener = new ChangeListener() {
		 public void stateChanged(ChangeEvent changeEvent) {
		        JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
		        int index = sourceTabbedPane.getSelectedIndex();
		        SolverStart.doRedrawOnClick = true;
		       
		        if ( index == tab_PCA) {
		        	int cnt = 0;
		        	for (int a=0;a<DS.numVars;a++) {
		        		if ( DS.selectedArea[a]) {
		        			cnt++;
		        		}
		        	}
		        	double[][] 	dat = new double[DS.numSamples][cnt];
		        	String[] 	are	= new String[cnt];
		        	cnt = 0;
		        	for (int f=0;f<DS.numSamples;f++) {
		        		cnt=0;
			        	for (int a=0;a<DS.numVars;a++) {
			        		if ( DS.selectedArea[a]) {
			        			are[cnt]	= DS.AreaNames [a];
			        			dat[f][cnt] = DS.normData[f][a];
			        			cnt++;
			        		}
			        	}
		        	}
		        	Thread thread = new Thread(new Runnable()
					{
					   public void run()
					   {
						   new MultiVariate_R(dat,DS.SampleNames,are,DS.ClassNames, DS.classCols);
				        	tabPCA3D.repaint();
					   }
					});
					thread.start();
		        	

		        }
		      }
		    };
		maintabbed.addChangeListener(changeListener);
		
		sp1D.setTitle("Accuracy Development");
		sp1D.setXAxis("# cycle");
		sp1D.setYAxis("accuracy [%]");
		sp2D.setTitle("Avg. Loadings/Importance [%]");
		sp2D.setXAxis("# feature");
		sp2D.setYAxis("loadings ");
		

		
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
		
		
//		jF.addComponentListener(new ComponentAdapter() 
//		{  
//		        public void componentResized(ComponentEvent evt) {
//		            Component c = (Component)evt.getSource();
//		            ImageIcon imageIcon = new ImageIcon(ClassLoader.getSystemResource("solver_algo.png"));
//		        	Image image = imageIcon.getImage(); // transform it 
//		        	Image newimg = image.getScaledInstance(iconAlgo.getWidth(), iconAlgo.getHeight(),  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way  
//		        	imageIcon = new ImageIcon(newimg);  // transform it back
//
//		        	iconAlgo.setIcon(imageIcon);
//		        }
//		});
		
		Preferences prefs;
	    prefs = Preferences.userRoot().node("Solver");
		//114
		menuActionChk_QR.setSelected(prefs.getBoolean("menuActionChk_QR", false));
		menuActionChk_Jump.setSelected(prefs.getBoolean("menuActionChk_Jump", true));
		menuActionChk_SendEns.setSelected(prefs.getBoolean("menuActionChk_SendEns", false));
		MultiVariate_R.r_Path = prefs.get("R_Path", "");
		MultiVariate_R.r_Script = prefs.get("R_Script", "");
		MultiVariate_R.r_Data =  prefs.get("R_Data", "");
		if ( MultiVariate_R.r_Path.length()<1 || MultiVariate_R.r_Script.length()<1 ||  MultiVariate_R.r_Data.length()<1) 
			maintabbed.setEnabledAt(tab_PCA,false);
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
		jB_Skip.setEnabled(false);
		jB_Skip.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			SolverStart.immediateSkip = true;
		}});
		jB_Shuffle.setEnabled(false);
		jB_Shuffle.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			SolverStart.doShuffle = true;
		}});
		labStatusIcon = new JLabel(new ImageIcon(ClassLoader.getSystemResource("colBlue.png")));
		labStatusIcon.setToolTipText("Ensemble Readiness");
		
//		panL.add(proNoBetterStop);
		panL.add(labRun);
		Dimension d = labRun.getPreferredSize();
		labRun.setPreferredSize(new Dimension(d.width+100,d.height));
		panL.add(labAccuracy);
		panL.add(labSamples);
		panL.add(labVars);
		panL.add(labClasses);
		
		panR.add(labTimePerRun);		
		panR.add(proStatus);
		panR.add(jb_Stop);
		panR.add(jB_Skip);
		panR.add(jB_Shuffle);
		panR.add(labStatusIcon);
		
		pan.add(panL);
		pan.add(panR);
		return pan;
	}
	static boolean refreshOptions(String txt) {

		String TxtBuffer = txt;
		
		JSONObject jo_Opts = null; 
		 try {
			 jo_Opts = new JSONObject(TxtBuffer);
		    } catch (JSONException e) {
		        return false;
		    }
		 
		 // 89 
		 if ( checkOptions(jo_Opts)== null ) {
			 try {
				if (jo_Opts.has("normType")) 		Opts.normType 		= jo_Opts.getString("normType");
				if (jo_Opts.has("numDims")) 		Opts.numDims 		= jo_Opts.getInt("numDims");
				if (jo_Opts.has("trainRatio"))		Opts.trainRatio 	= jo_Opts.getDouble("trainRatio");
				if (jo_Opts.has("numCycles")) 		Opts.numCycles 		= jo_Opts.getInt("numCycles");
				if (jo_Opts.has("noBetterStop"))	Opts.noBetterStop 	= jo_Opts.getInt("noBetterStop");
				if (jo_Opts.has("minBetter")) 		Opts.minBetter 		= jo_Opts.getDouble("minBetter");
				if (jo_Opts.has("plotTimer")) 		Opts.plotTimer 		= jo_Opts.getInt("plotTimer");
				if (jo_Opts.has("fixTrainSet")) 	Opts.fixTrainSet 	= jo_Opts.getBoolean("fixTrainSet");
				if (jo_Opts.has("activation")) 		Opts.activation		= jo_Opts.getString("activation");
				if (jo_Opts.has("dstType"))			Opts.dstType		= jo_Opts.getString("dstType");			//99
				if (jo_Opts.has("maxTime"))			Opts.maxTime		= jo_Opts.getLong("maxTime");			//99
	
			  } catch (JSONException e) {
			        return false;
			  }
		}else {
			
			return false;
		}
		return true;
	}
	static String checkOptions(JSONObject jo_Opts) {
		 try {
			if (!jo_Opts.has("dstType")) return "dstType";
				if ( !jo_Opts.getString("dstType").equals("GROUP") )
					if ( !jo_Opts.getString("dstType").equals("EGO") ) return "dstType";
			if (!jo_Opts.has("normType")) return "normType";
			if ( !jo_Opts.getString("normType").equals("Pareto") )
				if ( !jo_Opts.getString("normType").equals("MaxMinNorm") )
					if ( !jo_Opts.getString("normType").equals("None") )return "normType";
			if (!jo_Opts.has("numDims"))  return "numDims";
				if ( jo_Opts.getInt("numDims")<1 || jo_Opts.getInt("numDims")> 10) return "numDims";
			if (!jo_Opts.has("trainRatio")) return "trainRatio";
				if ( jo_Opts.getDouble("trainRatio")<0 || jo_Opts.getDouble("trainRatio")>1) return "trainRatio";
			if (!jo_Opts.has("numCycles"))  return "numCycles";
				if ( jo_Opts.getInt("numCycles")<1 || jo_Opts.getInt("numCycles")> 99) return "numCycles";
			if (!jo_Opts.has("noBetterStop")) return "noBetterStop";
//				if ( jo_Opts.getInt("noBetterStop")<1 ) return false;
			if (!jo_Opts.has("minBetter")) 	 return "minBetter";
				if ( jo_Opts.getDouble("minBetter")<0 || jo_Opts.getDouble("minBetter")>1) return "minBetter";
			if (!jo_Opts.has("maxTime")) 	 return "maxTime";
				if ( jo_Opts.getLong("maxTime")<0 ) return "maxTime";	
			//if (!jo_Opts.has("plotTimer")) 	 return "plotTimer";
			if (!jo_Opts.has("fixTrainSet"))  return "fixTrainSet";
				boolean tst = false;
				try {
					tst = jo_Opts.getBoolean("fixTrainSet");
				} catch (JSONException e) {
			        return "fixTrainSet";
				  }
				//if ( jo_Opts.getBoolean("fixTrainSet") || !jo_Opts.getBoolean("fixTrainSet") ) return "fixTrainSet";
			if (!jo_Opts.has("activation")) 	 return "activation";
				if ( !jo_Opts.getString("activation").equals("DxA") )
					if ( !jo_Opts.getString("activation").equals("D+A") )
						if ( !jo_Opts.getString("activation").equals("D") )
							if ( !jo_Opts.getString("activation").equals("A") )return "activation";

		  } catch (JSONException e) {
		        return "parsingError";
		  }
		 return null;
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
		menuActionBatchTrain.setEnabled(true);
		menuActionClassify.setEnabled(true);
		menuActionOptions.setEnabled(true);
		jb_Stop.setEnabled(false);
		jB_Skip.setEnabled(false);
		jB_Shuffle.setEnabled(false);
//		proNoBetterStop.setValue(0);
		jbLoadEns.setEnabled(true);
		UI.maintabbed.setEnabledAt(tab_spMining, true);
		
		//jF.setTitle(SolverStart.app+SolverStart.appAdd+" ["+SolverStart.dataFileName+"]");
		jF.setTitle(SolverStart.app+SolverStart.appAdd+" ["+DS.fileName+"]");
		
		
		boolean noData 			= false;
		boolean noEns 			= false;
		boolean ensMatchData 	= false;
		boolean isRunning 		= false;
		
		if ( DS.numClasses < 2) {
			noData = true;
		}
//		labVars.setText("Features: " + DS.numVars);
		int cnt = 0;
		for (int i=0;i<DS.numVars;i++) {
			if ( DS.selectedArea[i] ) cnt++;	
		}
		labVars.setText("Features: " + cnt);
		labSamples.setText("Samples: " + DS.numSamples);
		labClasses.setText("Classes: " + DS.numClasses);
		
		if ( DS.js_Ensemble != null) {
			ensMatchData = true;
			String[] ensVarNames = Tools.getvarNamesFromEnsemble ( );
			if ( ensVarNames.length != DS.numVars) {
				ensMatchData = false;
			}else {
				for (int i=0;i<DS.numVars;i++) {				// 144
					if ( !DS.AreaNames[i].equals(ensVarNames[i]) && !DS.AreaNames[i].replace("\"", "").equals(ensVarNames[i].replace("\"", ""))) ensMatchData = false;	
				}
			}
			// 91
			JSONObject ds 		= DS.js_Ensemble.getJSONObject("DS");			// WhatFor, to block shared ensembles?
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
			menuActionBatchTrain.setEnabled(false);
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
			menuActionBatchTrain.setEnabled(false);
			jb_Stop.setEnabled(true);
			jB_Skip.setEnabled(true);
			jB_Shuffle.setEnabled(true);
			jbLoad.setEnabled(false);
			jbLoadEns.setEnabled(false);
			menuActionOptions.setEnabled(false);
			UI.maintabbed.setEnabledAt(tab_spMining, false);
		}
		
		if ( !noEns)
			if ( !ensMatchData ) {	
//				if (DS.js_Ensemble	!= null )JOptionPane.showMessageDialog(jF, "Ensemble feature label / count does not match data set", "Solver", JOptionPane.INFORMATION_MESSAGE);
				UI.labStatusIcon.setIcon(new ImageIcon(ClassLoader.getSystemResource("colRed.png")));
				menuActionClassify.setEnabled(false);
				jbClassify.setEnabled(false);
			}else {
				UI.labStatusIcon.setIcon(new ImageIcon(ClassLoader.getSystemResource("colGreen.png")));
				menuActionClassify.setEnabled(true);
				jbClassify.setEnabled(true);
			}
		
		jF.repaint();
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
		
		tableConfusionMatrix = new JTable() {
	        private static final long serialVersionUID = 1L;
	        public boolean isCellEditable(int row, int column) {                
	                return false;               
	        };
	    };
	    tmConfusionMatrix =(DefaultTableModel) tableConfusionMatrix.getModel();
	    tableConfusionMatrix.getTableHeader().setOpaque(false);
	    tableConfusionMatrix.getTableHeader().setBackground(SolverStart.backColor);
		fnt = tableConfusionMatrix.getTableHeader().getFont(); 
		tableConfusionMatrix.getTableHeader().setFont(fnt.deriveFont(Font.BOLD));
		
		tableConfusionMatrix.getTableHeader().setReorderingAllowed(false);
		tableConfusionMatrix.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		tableConfusionMatrix.setAutoCreateRowSorter(false);
		
		
		tableValidation= new JTable() {
	        private static final long serialVersionUID = 1L;
	        public boolean isCellEditable(int row, int column) {                
	                return false;               
	        };
	    };
		tmtableValidation =(DefaultTableModel) tableValidation.getModel();
		tableValidation.getTableHeader().setOpaque(false);
		tableValidation.getTableHeader().setBackground(SolverStart.backColor);
		fnt = tableValidation.getTableHeader().getFont(); 
		tableValidation.getTableHeader().setFont(fnt.deriveFont(Font.BOLD));
		
		tableValidation.getTableHeader().setReorderingAllowed(false);
		tableValidation.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		//tableValidation.setAutoCreateRowSorter(true);
		tmtableValidation.addColumn("run");
		tmtableValidation.addColumn("type");
		tmtableValidation.addColumn("Target");
		tmtableValidation.addColumn("TP_Train");
		tmtableValidation.addColumn("FP_Train");
		tmtableValidation.addColumn("TN_Train");
		tmtableValidation.addColumn("FN_Train");
		tmtableValidation.addColumn("Sensitivity_Train");
		tmtableValidation.addColumn("Specificity_Train");
		
		tmtableValidation.addColumn("TP_Validation");
		tmtableValidation.addColumn("FP_Validation");
		tmtableValidation.addColumn("TN_Validation");
		tmtableValidation.addColumn("FN_Validation");
		tmtableValidation.addColumn("Sensitivity_Validation");
		tmtableValidation.addColumn("Specificity_Validation");
		tmtableValidation.addColumn("Accuracy_Validation");

		// 110
		tableClassify.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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

	            setForeground(Color.BLACK);
	            int red = 255;
	            int green = 255;
	            int blue = 255;
	             
	            if ( val > 0) {
	            	red 	-= (int) (val * 84);
	            	green 	-= (int) (val * 8);
	            	blue	-= (int) (val * 78);
	            	setForeground(Color.BLACK);
	            }
	            if ( red < 0) red = 0;
	            if ( green < 0) green = 0;
	            if ( blue < 0) blue = 0;
	            if ( red > 255) red = 200;
	            if ( green > 255) green = 200;
	            if ( blue > 255) blue = 200;
	            
	            setBackground(new Color(red,green, blue));
            }
	            if ( column == 5)
	            	if ( value.equals("------")) {
	            		//setBackground(Color.decode("#ffeeda"));
	            		setBackground(SolverStart.backOrange);
	            	}else {
	            		//setBackground(Color.decode("#abf7B1"));
	            		setBackground(SolverStart.backGreen);
	            	}
	            return this;
	        }
	    });
		tableValidation.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			private static final long serialVersionUID = 1L;
			@Override
	        public Component getTableCellRendererComponent(JTable table,
	                Object value, boolean isSelected, boolean hasFocus,
	                int row, int column) {

	            JLabel label = (JLabel)super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	            Font font = label.getFont();
	            label.setFont(font.deriveFont(Font.PLAIN));
	            setBackground(SolverStart.backColor);setForeground(SolverStart.frontColor);
	            if ( column>2 && column<9) {
	            	//setBackground(Color.decode("#F8D568")); // 248 213 104
	            	setBackground(SolverStart.backOrange);
	            }
	            if ( column>8 && column<16) {
	            	//setBackground(new Color(0,255, 147));
	            	setBackground(SolverStart.backGreen);
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
				train(1);
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
			Tools.doNormData ();
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
		menuFile.add(menuFileSaveData);
		menuFile.add(new JSeparator());
		menuFile.add(menuFileLoadEnsemble);
		menuFile.add(menuFileSaveEnsemble);
		menuFileSaveEnsemble.setAccelerator(KeyStroke.getKeyStroke('S',InputEvent.CTRL_DOWN_MASK));
		menuFile.add(new JSeparator());
		JMenuItem menuFileExit = new JMenuItem(" Quit"); 
		menuFileExit.setAccelerator(KeyStroke.getKeyStroke('Q',InputEvent.CTRL_DOWN_MASK));
		menuFile.add(menuFileExit);	
		// ---------------------------------------------------------------------
		JMenuItem menuActionUnitTest = new JMenuItem(" [Benchmark]");
		menuActionUnitTest.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			new UnitTest();
		}});
		
		menuAction.add(menuActionTrain);
		menuActionTrain.setAccelerator(KeyStroke.getKeyStroke('T',InputEvent.CTRL_DOWN_MASK));
		menuAction.add(menuActionBatchTrain);																					// 121
		menuAction.add(menuActionTrainImmediateStop);
		menuActionTrainImmediateStop.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_CANCEL, InputEvent.CTRL_DOWN_MASK));
		menuAction.add(new JSeparator());
		menuAction.add(menuActionClassify);
		menuAction.add(new JSeparator());
		menuAction.add(menuActionUnitTest);
		
		// ---------------------------------------------------------------------
		menuSettings.add(menuActionOptions);
		JMenuItem menuSettingsRPath = new JMenuItem(" Activate PCA"); 
		menuSettings.add(menuSettingsRPath);
		JMenuItem menuSettingsAlgo = new JMenuItem(" Show Algorithm"); 
		menuSettings.add(menuSettingsAlgo);
		menuSettings.add(new JSeparator());
		menuSettings.add(menuActionChk_QR);									
		menuSettings.add(menuActionChk_Jump);
		menuSettings.add(menuActionChk_SendEns);

		
		
		// ---------------------------------------------------------------------

		JMenuItem menuExportVectors = new JMenuItem(" Vectors"); 
		menuExport.add(menuExportVectors);
		
		JMenuItem menuExportClassification = new JMenuItem(" Classification"); 
		menuExport.add(menuExportClassification);
		JMenuItem menuExportTP_FP_TN_FN = new JMenuItem(" Validation"); 
		menuExport.add(menuExportTP_FP_TN_FN);
		JMenuItem menuExportDistances = new JMenuItem(" 1D Scores"); 
		menuExport.add(menuExportDistances);
		JMenuItem menuExportAccuracies = new JMenuItem(" Accuracies");
		menuExport.add(menuExportAccuracies);
		JMenuItem menuExportNormData = new JMenuItem(" Normalized Data "); 
		menuExport.add(menuExportNormData);
		menuExport.addSeparator();
		JMenuItem menuExportPlot = new JMenuItem(" Copy current plot "); 
		menuExport.add(menuExportPlot);
//		JMenuItem menuExportPCA = new JMenuItem(" Recent PCA result"); 
//		menuExport.add(menuExportPCA);
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
		mBar.add(menuSettings);
		mBar.add(menuExport);
		mBar.add(menuAbout);
		
		menuFileLoadDsFormat.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			loadData();
		}});
		menuFileLoadClip.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			SolverStart.importDataCSV(null,"\t");
			new DS();												// INITS
			DS.normParas = Tools.doNormData ();				// Daten Normalisieren

			
			DS.txtSummary = null;
			DS.fileName =  "Clipboard";
			DS.filePath = null;
			tmtableClassify.setColumnCount(0);
			tmtableClassify.setRowCount(0);
			
			new DS();												// INITS
			
			DS.normParas = Tools.doNormData ();				// Daten Normalisieren
		
			
			refreshStatus();
			SolverStart.analyzeRawData("Clipboard");
			if ( UI.menuActionChk_Jump.isSelected() )UI.maintabbed.setSelectedIndex(UI.tab_Summary);
		}});
	

		menuFileSaveData.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			// 98
			//refreshOptions();
			StringBuffer out = new StringBuffer();

			out.append("\"Class\"");
        	for (int j=0;j< DS.numVars; j++) {
        		if ( DS.selectedArea[j] )
        			out.append("," + DS.AreaNames[j]);	
        	}
        	out.append("\n");
        
	        for (int f=0;f<DS.numSamples;f++){
	        	String cls = DS.ClassNames[f];
	        	
	        	if ( cls.length()<1 || Tools.isNumeric(cls) ) {
	        		cls = "class"+DS.classAllIndices[Tools.getIndexOfTarget(DS.classIndex[f])];
	        	}
	        	out.append(cls);
	        	for (int j=0;j< DS.numVars; j++) {
	        		if ( DS.selectedArea[j] )
	        			out.append("," + DS.rawData[f][j]);	
            	}
	        	out.append("\n");
	        }
			
			Preferences prefs;
		    prefs = Preferences.userRoot().node("Solver");
		    String path = prefs.get("path", ".");
		    String[] type = {"CSV"};
		    String[] sht = {"CSV"};
			File f = Tools.getFile("Select file to save", path,type,sht, true);
			if ( f == null) return;
			if ( !f.getName().endsWith(".csv"))
				f = new File(f.getAbsolutePath()+".csv");
			
			FileWriter fw = null;
			try {
				fw = new FileWriter(f, false);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		    BufferedWriter bw = new BufferedWriter(fw);
		    try {
		    	fw.write(out.toString());
			} catch (JSONException | IOException e1) {
				e1.printStackTrace();
			}
		  
		    try {
		    	bw.close();
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
			train(1);
		}});
		menuActionBatchTrain.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			String erg = JOptionPane.showInputDialog("Enter batch size");
			if (erg==null)return;
			int batchSize = 0;
			try {
				batchSize = Integer.parseInt(erg);
			}catch(NumberFormatException ee){
	        }
			train(batchSize);
		}});
		menuActionOptions.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			JPanel pan = new JPanel();
			pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));
			boolean success = true;
			JTextArea man = new JTextArea(Opts.getOptsAsJson().toString(3));
		    JScrollPane scrMan = new JScrollPane(man);
		    scrMan.setPreferredSize(new Dimension(400,400));
			Object[] buttons = {"OK", "Default", "Cancel"}; 
			
			pan.add(new JLabel("<HTML>"
					+ "Text is a JSON String to refine the machine learning algorithm.<BR>"
					+ "To start with, set to DEFAULT, should work for most use cases.<BR>"
					+ "You may want to refine for acceleration, training ratio adjustment<BR>"
					+ "or depth of discrimiation seeking.<BR>"
					+ "</HTML>"					));
			pan.add(scrMan);
			 
	         int selected = JOptionPane.showOptionDialog(null,
	        		 pan, 
	                                                     "Algorithm Options", 
	                                                     JOptionPane.DEFAULT_OPTION, 
	                                                     JOptionPane.QUESTION_MESSAGE, 
	                                                     null, buttons, buttons[0]);
	        if (selected == 0)
	        	success = refreshOptions(man.getText());
	        if (selected == 1)
	        	success = refreshOptions(SolverStart.defOptions.toString(3));
	        if (selected == 2);
				
			if ( !success )
				JOptionPane.showMessageDialog(null, "Options malformed. Operation canceled", "Algorithm Options", JOptionPane.WARNING_MESSAGE);
		}});
		menuActionChk_QR.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){	
			Preferences prefs;
		    prefs = Preferences.userRoot().node("Solver");
			prefs.putBoolean("menuActionChk_QR", menuActionChk_QR.isSelected());
		}});
		menuActionChk_Jump.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){	
			Preferences prefs;
		    prefs = Preferences.userRoot().node("Solver");
			prefs.putBoolean("menuActionChk_Jump", menuActionChk_Jump.isSelected());
		}});
		menuActionChk_SendEns.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){	
			Preferences prefs;
		    prefs = Preferences.userRoot().node("Solver");
			prefs.putBoolean("menuActionChk_SendEns", menuActionChk_SendEns.isSelected());
		}});
		
		menuActionClassify.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){	
			///maintabbed.setSelectedIndex(tab_Classify);
			SolverStart.classify();
		}});
		menuActionClassify.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){	
			maintabbed.setSelectedIndex(tab_Classify);
			SolverStart.classify();
		}});
		
		menuSettingsRPath.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){	
			MultiVariate_R.setPaths();
		}});
		menuSettingsAlgo.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){	
			new Icon_Solver();
		}});
		
		menuExportVectors.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){	
			JSONObject ensemble = DS.js_Ensemble;
			if (ensemble== null )return;
			int numClasses 		= ensemble.getJSONObject("DS").getString("classAllIndices").split(",").length;
			String[] varNames 	= ensemble.getJSONObject("DS").getString("VariableNames").split(",");
			
			int numVars 		= varNames.length;
			int numDims 		= ensemble.getJSONObject("Opts").getInt("numDims");
			int numCylces 		= ensemble.getJSONObject("Opts").getInt("numCycles");
			
			
			JSONArray models = ensemble.getJSONArray("model");
			StringBuffer out = new StringBuffer();
			out.append("Model Vectors"+"\n");
			out.append("#model\tdim\tTargetClass");
			for (int i=0;i<varNames.length; i++) {
				out.append("\t"+varNames[i]);
			}
			out.append("\n");
			double[][][] loadings = new double[numDims][numVars][models.length()] ;
			String[] varClasses = new String[models.length()];
			double[] maxDst = new double[models.length()];
			
			for (int i=0;i<models.length(); i++) {
				JSONObject in = models.getJSONObject(i);
				//
				maxDst[i] = in.getDouble("maxDistance");
				for (int p=0;p<numDims; p++) {
					out.append(i+"\t");
					out.append(p+"\t"+in.getString("targetLabel"));
					varClasses[i] = in.getString("targetLabel");
					String vTemp = in.getString(		"Vector"+p);
					String[] line = vTemp.split(","); 
					for (int a=0; a<line.length;a++) {
						out.append("\t"+Double.parseDouble(line[a].trim()));
						loadings[p][a][i] = Double.parseDouble(line[a].trim()); 
					}
					out.append("\n");
				}
			}
			double[][] normLoadings = new double[numVars][models.length()];
			for (int i=0;i<models.length(); i++) {
				for (int a=0;a<numVars; a++) {
					double ldng = 0;
					for (int j=0;j<numDims; j++) {
						ldng += loadings[j][a][i]*loadings[j][a][i]; 
					}
					ldng = Math.sqrt(ldng);
					normLoadings[a][i] = ldng ;
					if ( a>0 ) out.append("\t");
				}
			}
			out.append("\n");
			out.append("Normalized Loadings");
			out.append("\n");
			out.append("model\ttargetClass\t");
			for (int a=0; a<varNames.length;a++) {
				if ( a > 0 )out.append("\t");
				out.append(varNames[a]);
			}
			out.append("\tmaxDst");
			out.append("\n");
			for (int i=0;i<models.length(); i++) {
				double max = 0;
				for (int a=0;a<numVars; a++) {	
					if ( max < normLoadings[a][i] )
						max = normLoadings[a][i];
				}
				out.append((i+1)+"\t"+varClasses[i]);
				for (int a=0;a<numVars; a++) {
					out.append("\t");
					out.append(normLoadings[a][i]/max);
				}
				out.append("\t"+maxDst[i]);
				out.append("\n");
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
			out.append("AccuracyTrain");
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
					out.append("\t"+mc.accuracyTrain);
				}
				out.append("\n");
			}
			out.append("AccuracyValidation");
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
					out.append("\t"+mc.accuracyTest);
				}
				out.append("\n");
			}
			StringSelection stringSelection = new StringSelection( out.toString() );
		    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents( stringSelection, stringSelection );
		}});
		menuExportAccuracies.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			StringBuffer out = new StringBuffer();
			
			out.append("AccuracyTrain" + "\n");
			for (int i=0;i<DS.freezs.size();i++) {
				MC_Freeze mc = DS.freezs.get(i);
				if ( i> 0 ) out.append("\t");
				out.append("#" + i + "|" + mc.targetLabel);
			}
			out.append("\n");
			for (int i=0;i<DS.freezs.size();i++) {
				if ( i> 0 ) out.append("\t");
				MC_Freeze mc = DS.freezs.get(i);
				out.append(mc.accuracyTrain);
			}
			out.append("\n");

			out.append("AccuracyValidation" + "\n");
			for (int i=0;i<DS.freezs.size();i++) {
				MC_Freeze mc = DS.freezs.get(i);
				if ( i> 0 ) out.append("\t");
				out.append("#" + i + "|" + mc.targetLabel);
			}
			out.append("\n");

				for (int i=0;i<DS.freezs.size();i++) {
					if ( i> 0 ) out.append("\t");
					MC_Freeze mc = DS.freezs.get(i);
					out.append(mc.accuracyTest);
				}
				out.append("\n");

			StringSelection stringSelection = new StringSelection( out.toString() );
		    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents( stringSelection, stringSelection );
		}});
		// 90
		menuExportNormData.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			StringBuffer out = new StringBuffer();
			out.append("Normalized Data\n");
			out.append("Sample");
			for (int a=0;a<DS.AreaNames.length;a++) {
				out.append("\t"+DS.AreaNames[a]);	
			}
			out.append("\n");
			for (int f=0;f<DS.SampleNames.length;f++) {
				out.append(DS.SampleNames[f]);
				for (int a=0;a<DS.AreaNames.length;a++) {
					out.append("\t"+DS.normData[f][a]);	
				}
				out.append("\n");
			}

			StringSelection stringSelection = new StringSelection( out.toString() );
		    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents( stringSelection, stringSelection );
		}});
		
		menuExportPlot.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){
			BufferedImage img=null;
			if(maintabbed.getSelectedIndex()==tab_Live ){
				 img = new BufferedImage(panLive.getWidth(), panLive.getHeight(), BufferedImage.TYPE_INT_RGB);
			    Graphics g = img.getGraphics();
			    g.setColor(panLive.getForeground());
			    g.setFont(panLive.getFont());
			    panLive.paintAll(g);
			 }
			if(maintabbed.getSelectedIndex()==tab_Trends ){
				 img = new BufferedImage(panTrends.getWidth(), panTrends.getHeight(), BufferedImage.TYPE_INT_RGB);
			    Graphics g = img.getGraphics();
			    g.setColor(panTrends.getForeground());
			    g.setFont(panTrends.getFont());
			    panTrends.paintAll(g);
			 }

			if(maintabbed.getSelectedIndex()==tab_3D ){
				 img = new BufferedImage(tab3D.getWidth(), tab3D.getHeight(), BufferedImage.TYPE_INT_RGB);
			    Graphics g = img.getGraphics();
			    g.setColor(tab3D.getForeground());
			    g.setFont(tab3D.getFont());
			    tab3D.paintAll(g);
			 }
			if(maintabbed.getSelectedIndex()==tab_PCA ){
				 img = new BufferedImage(tabPCA3D.getWidth(), tabPCA3D.getHeight(), BufferedImage.TYPE_INT_RGB);
			    Graphics g = img.getGraphics();
			    g.setColor(tabPCA3D.getForeground());
			    g.setFont(tabPCA3D.getFont());
			    tabPCA3D.paintAll(g);
			 }
			if(maintabbed.getSelectedIndex()==tab_spMining ){
				 img = new BufferedImage(Pan_Mining.scatterPlot.getWidth(), Pan_Mining.scatterPlot.getHeight(), BufferedImage.TYPE_INT_RGB);
			    Graphics g = img.getGraphics();
			    g.setColor(Pan_Mining.scatterPlot.getForeground());
			    g.setFont(Pan_Mining.scatterPlot.getFont());
			    Pan_Mining.scatterPlot.paintAll(g);
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
					+ "<I>Copyright 2009-2024 Daniel Sanders</I><BR>Check for updates:<BR>"
					+ "https://github.com/dsandersGit/GIT_Solver/<BR></HTML>");
		}});
		menuAboutLicense.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			JOptionPane.showMessageDialog(jF, "<HTML><I>Licensed under GNU General Public License v3.0</I></HTML>", SolverStart.app,  JOptionPane.INFORMATION_MESSAGE);
		}});
		menuAboutCredits.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			JOptionPane.showMessageDialog(null, "<HTML> used libraries and dependencies: <ul>"
					+ "<li>JSON java libraries from JSON.org</li>"
					+ "<li>External: 'The R Project for Statistical Computing' https://www.r-project.org/</li>"
					+ "<li>QR Code:<BR>Copyright � 2024 Project Nayuki. (MIT License)<BR>" + 
					"https://www.nayuki.io/page/qr-code-generator-library</li>"
					//+ "<li>FlatLaf - Flat Look and Feel Theme</li>"
					+ "</ul><BR></HTML>"
					+ "", "CREDITS", JOptionPane.INFORMATION_MESSAGE);
		}});
		//-------------------------------------------------------------------------------------------------------------------
		return mBar;
	}
	
	
	public static void train(int batchSize) {
		DS.js_Ensemble = null;
		DS.freezs.clear();
		
		SolverStart.immediateStop = false;
		Runner.cleanRunner ();
		tmtableClassify.setColumnCount(0);
		tmtableClassify.setRowCount(0);
		if ( menuActionChk_Jump.isSelected() ) maintabbed.setSelectedIndex(tab_Live);
		//jF.setTitle(SolverStart.app+SolverStart.appAdd+" ["+SolverStart.dataFileName+"]");
		jF.setTitle(SolverStart.app+SolverStart.appAdd+" ["+DS.fileName+"]");
		
		refreshStatus();
//		try {
//			SolverStart.trainPattern();
//		} catch (IOException e) {
//
//			e.printStackTrace();
//		}
			Thread thread = new Thread(new Runnable()
			{
			   public void run()
			   {
				   try {
				   SolverStart.trainPattern(batchSize);
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
			if ( menuActionChk_SendEns.isSelected() ) {
				System.out.println(DS.js_Ensemble.toString(3));					// 97 System.out of ensemble to external LogStreamReader
			}else {
				System.out.println("Nothing to see here . . .");
			}
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
	    
	    // Copy Data. Clipboard data will not be saved
	    if ( DS.filePath != null ) {
		    File dest = new File(f.getAbsolutePath()+"_"+DS.fileName);
		    try {
				Files.copy(DS.filePath.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
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
		UI.tmConfusionMatrix.setColumnCount(0);
		UI.tmConfusionMatrix.setRowCount(0);
		
		if ( DS.js_Ensemble != null ) {
			EnsembleTree.putEnsemble("Imported: "+f.getName(), "N/A", DS.js_Ensemble);
			Classify.setOptions();
//			UI.txtEnsemble.setText(DS.js_Ensemble.toString(3));
		}
		
	}
	private static void loadData() {
//		if ( !refreshOptions() ) {
//			JOptionPane.showMessageDialog(jF, "<HTML><H3>Options malformed > set to default</H3>");
//			return;
//		}
		
		Preferences prefs;
	    prefs = Preferences.userRoot().node("Solver");
	    String path = prefs.get("path", ".");
	    String[] type = {"CSV, DS_Data"};
	    String[] sht = {"CSV", "DAT"};
		File f = Tools.getFile("Select dataset file", path,type,sht, false);
		if ( f == null) return;
		if ( !f.exists()) return;
		prefs.put("path", f.getParent());
		boolean loadSuccess = false;;
		if ( f.getName().toLowerCase().endsWith(".dat")) {
			SolverStart.importData(f.getAbsolutePath());
			loadSuccess = true;
		}
		if ( f.getName().toLowerCase().endsWith(".csv")) {
			loadSuccess = SolverStart.importDataCSV(f.getAbsolutePath(),",");
		}
		
		
		//if ( !loadSuccess )return;
		
		DS.fixedTrainSet = null;
		DS.txtSummary = null;
		DS.fileName =  f.getName();
		tmtableClassify.setColumnCount(0);
		tmtableClassify.setRowCount(0);
		
		new DS();												// INITS
		
//		SolverStart.bootstrap() ;
		
		DS.normParas = Tools.doNormData ();				// Daten Normalisieren
		//SolverStart.dataFileName = f.getName();
		DS.fileName= f.getName();
		SolverStart.analyzeRawData(f.getName());
		if ( UI.menuActionChk_Jump.isSelected() )UI.maintabbed.setSelectedIndex(UI.tab_Summary);
		refreshStatus();
	}
	
}
