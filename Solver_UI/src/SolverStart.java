import java.awt.Color;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;


public class SolverStart {
	
	/*
	 * Main Class. 
	 *  + starts the app
	 *  + data import and check 
	 *  + data classification training commander
	 *  
	 *  Copyright(c) 2009-2023, Daniel Sanders, All rights reserved.
	 *  https://github.com/dsandersGit/GIT_Solver
	 */
	
	/*
	 * THOUGHTS:
	 * Reduce Variable Count via SPEARMAN Reduktion ?
	 * Might be beneficial to do bootstrapping for small sample sizes (resampling with duplicates )
	 */
	
	/*
	 * MINDMAP Algo Name: 
	 * Center Same, Disperse Others   CS-DO
	 * Disperse Non Target
	 * Unique Center Distance Disperse UC-DD
	 * Unique Separate by Distance US_D  
	 * Iterate Disperse from Some ID-S
	 * Iterating Geometrical Dispersion IGD
	 * Iterative Spatial Dispersion ISD
	 * Iterating Spatial expulsion ISE
	 * Iterating Spatial adjustment ISA
	 * Iterating Spatial Class Discrimination ISCD
	 * Iterating Spatial Segregation ISS
	 * Iterating Spatial Partitioning ISP * 
	 * Iterative Spatial Isolation ISI *****
	 */
	
	/*
	 * 34: Fingerprint Model und Opts
	 * 35: Vieles
	 * 36: Validation in Table
	 * 37: BugFix Estimated runtime
	 * 38: BugFix AUROC < 0.5
	 * 39: ImmediateStop > no Freeze
	 * 40: Mnemonics
	 * 41: Rename to Fingerprinter
	 * 42: exchange divide by inv for speed
	 * 43: rename to 'solver'
	 * 44: Median Option for centering
	 * 45: Rename > uSort
	 * 46: UI
	 * 47: BugFix: Import DS_data with [label] tag
	 * 48: AutoSave Ensemble desactivated
	 * 49: Double Run, Less Options
	 * 50: Double Run removed
	 * 51: Redo Double Run, Shrink ensemble
	 * 52: First round > Fully random
	 * 53: Booster
	 * 54: AutoLoad lastEnsemble
	 * 55: Options reduced
	 * 56: anotherIndication
	 * 57: anotherIndication removed, less options
	 * 58: BugFixes
	 * 59: Back to 'solver'
	 * 60: New Feature Split Dataset into Files
	 * 61: numeric classes during split save
	 * 62: Algo Plan
	 * 63: Low Prune Probability ( Opts )
	 * 64: Added: Accuracy Development per cycles > Start with unnecessary many classes, stop when accuracy does not increase
	 * 65: Major Changes: +Export Plots, + Import, +3D View, + Live Classification Change, + Trends & Loadings
	 * 66: ClassColors > 
	 * 67: Remove individual Models in Validation-Tab by InPOP (context menu)
	 * 68: Fork problem
	 * 69: allCountTarget bug fixed: unknown classes < 0
	 * 70: variableID
	 * 71: ClassColor in LIVE
	 * 72: Train/Test change only cycle wise
	 * 73: Accuracy Train and Test, Classify: Train / Testing when fixed, Opts adapted
	 * 74: Re-lable Test > Validation
	 * 75: Fixed Trainset Init moved from DS to SolverStart
	 * 76: + random probability in accuracy plot
	 * 77: Color reshuffle
	 * 78: void
	 * 79: UC.addColumn swaped
	 * 80: Bug 'comma in Variable Names' fixed, Classification background color adapted 
	 * 81: refreshStatus: ensMatchData checks for varName and '\"'+varName+'\"'
	 * 82: Speci(t)ficity Easter Egg
	 * 83: Correct Confusion Matrices
	 * 84: (i+1)+"]"+DS.AreaNames
	 * 85: doNormData() position BUGFIX
	 * 86: isNumeric: BUG scientific numbers fixed
	 * 	 */
 
	
	public static String 	app 			= "solver [ISI]";
	public static String 	appAdd 			= " 0.1.86";
	public static String 	revision 		= " 86";
	public static boolean 	isRunning 		= false;
	public static boolean 	immediateStop 	= false;
	public static long 		plotTimer 		= -1;
	public static boolean 	darkMode 		= false;
	public static Color 	backColor 		= Color.DARK_GRAY;
	public static Color 	frontColor 		= Color.LIGHT_GRAY;
	public static JSONObject defOptions     = null;
	public static String dataFileName = "";
	
	public static float[] rollingAccuracy			= null;    // gain / class
	public static float[] rollingAccuracyX			= null;
	static String[] uiColorKeys = {"control","info","nimbusBase","nimbusAlertYellow","nimbusDisabledText"+
			"nimbusFocus","nimbusGreen","nimbusInfoBlue","nimbusLightBackground","nimbusOrange","nimbusRed"+
			"nimbusSelectedText","nimbusSelectionBackground","text"	};
	static Color[] uiBaseColors = new Color[uiColorKeys.length];
	static Color[] uiDarkColors = {new Color( 128, 128, 128),new Color(128,128,128),new Color( 18, 30, 49),
			 new Color( 248, 187, 0), new Color( 128, 128, 128),new Color(115,164,209),new Color(176,179,50),
			 new Color( 66, 139, 221),new Color( 18, 30, 49),new Color(191,98,4),new Color(169,46,34),
			 new Color( 255, 255, 255), new Color( 104, 93, 156),new Color( 230, 230, 230)};
	
	public static void main(String[] args) {
		
//		// taken from https://stackoverflow.com/questions/36128291/how-to-make-a-swing-application-have-dark-nimbus-theme-netbeans
//		for (int i=0;i<uiColorKeys.length;i++) {
//			uiBaseColors[i] = UIManager.getColor(uiColorKeys[i]);
//		}
//		if (darkMode) {
//			frontColor 	= new Color(255,255,253);
//			backColor 	= Color.DARK_GRAY;
//			for (int i=0;i<uiColorKeys.length;i++) {
//				UIManager.put(uiColorKeys[i],uiDarkColors[i]);
//			}
//		}
//		
//		
////		  UIManager.put( "control", new Color( 128, 128, 128) );
////		  UIManager.put( "info", new Color(128,128,128) );
////		  UIManager.put( "nimbusBase", new Color( 18, 30, 49) );
////		  UIManager.put( "nimbusAlertYellow", new Color( 248, 187, 0) );
////		  UIManager.put( "nimbusDisabledText", new Color( 128, 128, 128) );
////		  UIManager.put( "nimbusFocus", new Color(115,164,209) );
////		  UIManager.put( "nimbusGreen", new Color(176,179,50) );
////		  UIManager.put( "nimbusInfoBlue", new Color( 66, 139, 221) );
////		  UIManager.put( "nimbusLightBackground", new Color( 18, 30, 49) );
////		  UIManager.put( "nimbusOrange", new Color(191,98,4) );
////		  UIManager.put( "nimbusRed", new Color(169,46,34) );
////		  UIManager.put( "nimbusSelectedText", new Color( 255, 255, 255) );
////		  UIManager.put( "nimbusSelectionBackground", new Color( 104, 93, 156) );
////		  UIManager.put( "text", new Color( 230, 230, 230) );
//		  try {
//		    for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//		      if ("Nimbus".equals(info.getName())) {
//		          javax.swing.UIManager.setLookAndFeel(info.getClassName());
//		          break;
//		      }
//		    }
//		  } catch (ClassNotFoundException e) {
//		    e.printStackTrace();
//		  } catch (InstantiationException e) {
//		    e.printStackTrace();
//		  } catch (IllegalAccessException e) {
//		    e.printStackTrace();
//		  } catch (javax.swing.UnsupportedLookAndFeelException e) {
//		    e.printStackTrace();
//		  } catch (Exception e) {
//		    e.printStackTrace();
//		  }
		
		try {
			backColor 	= new Color(255,255,253);
			frontColor 	= Color.DARK_GRAY;
	          UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			}catch( Exception be ) { be.printStackTrace(); }
		
		defOptions = Opts.getOptsAsJson();
		new UI();
		UI.loadEnsemble(true);
		
		if(args.length>0 && args[0].endsWith(".dat")){
			if ( new File(args[0]).exists() ) {
				if(args[0].endsWith(".dat")){
					importData(args[0]);
					File f = new File(args[0]);
					DS.txtSummary = null;
					DS.fileName =  f.getName();
					UI.tmtableClassify.setColumnCount(0);
					UI.tmtableClassify.setRowCount(0);
					new DS();										
					DS.normParas = Tools.doNormData ();				
					SolverStart.dataFileName = f.getName();
					
					SolverStart.analyzeRawData(f.getName());
					UI.maintabbed.setSelectedIndex(UI.tab_Summary);
					UI.refreshStatus();
				}
			}
		}
		
		if(args.length>1)
			DS.variableID = args[1]; 
	}
	public static void trainPattern() throws IOException {

		// populates with running the accuracy of train / test results
		ArrayList<Float> rollingAccuracyTest = new ArrayList<Float>();
		ArrayList<Float> rollingAccuracyTrain = new ArrayList<Float>();
		@SuppressWarnings("unchecked")
		ArrayList<Float> rollingAccuracyX = new ArrayList<Float>();

		int rACount = 0;													// running counter
		immediateStop = false;												// flag for user interrupting current training session
		isRunning = true;													// flag for running current training session
		
		// cleaning stuff
		UI.labAccuracy.setText("Accuracy: ---");
		UI.sp1D.dats.clear();
		UI.sp1D.refreshPlot();
		UI.sp2D.dats.clear();
		UI.sp2D.refreshPlot();
		UI.menuFile.setEnabled(false);
		UI.txtEnsemble.setText("");
		UI.tmtableStat.setRowCount(0);
		UI.txtClassify.setText("");
		//72: Train/Test change only cycle wise
		//DS.fixedTrainSet = null;
		
		// Basic probability of classification based on class number and population (two equal classes same population = 50% of random correct draw)
		double prob = 0;
		for (int c=0;c<DS.numClasses;c++) {
			prob += (double)DS.classAllIndPop[c] * ((double)DS.classAllIndPop[c] / (double)DS.numSamples);
		}
		prob /= (double) DS.numSamples; 
		
		DS.normParas = Tools.doNormData ();	// 85
		
		UI.labTimePerRun.setText("Process: THIS MIGHT TAKE SOME TIME");
		UI.refreshStatus();													// fills status and determines user interface UI options 
		Runner.cleanRunner();												// reset Runner > training session module
		
		// Preparing Ensemble JSON
		JSONObject main = new JSONObject();
		main.put("creator", SolverStart.app + SolverStart.appAdd + " r" + SolverStart.revision);
		StringBuffer out = new StringBuffer();
		out.append(Opts.getOptsAsJson().toString(3));
		out.append(DS.getDSsAsJson().toString(3));
		main.put("Opts", Opts.getOptsAsJson());
		main.put("DS", DS.getDSsAsJson());
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	    LocalDateTime now = LocalDateTime.now();
	    main.put("DateOfCreation"				, ""+dtf.format(now));
	    main.put("ObjectType"					, "ML_Ensemble");
	    main.put("ObjectVersion"				, "01.00"); 
	    main.put("Support"						,"N/A");
	    
	   
	    
		// Time / run estimation
	    long tme = System.currentTimeMillis();
		long tmeStart = System.currentTimeMillis();
		double tmeCount = 0;
		double timeSum = 0;
		int cycles = Opts.numCycles*DS.numClasses;
		double avgTime = 0;
		
		// Activation Equation selection
		boolean activationIsDst = false;
		boolean activationBoth = false;
		if ( Opts.activation.equals("DxA") )activationIsDst = true;		
		if ( Opts.activation.equals("D+A") )activationBoth = true;

		UI.labStatusIcon.setIcon(new ImageIcon(ClassLoader.getSystemResource("colYellow.png")));	// UI button color
		for (int i=0;i<Opts.numCycles;i++) {
			//72: Train/Test change only cycle wise
			//75: Fixed Trainset Init moved from DS to SolverStart
			if ( !Opts.fixTrainSet || i==0) DS.getFixedTrainSet();									// fetch the (initial) training set
			UI.proStatus.setValue(100*i/Opts.numCycles);
			for (int j=0;j<DS.classAllIndices.length;j++) {
				if ( !SolverStart.immediateStop ) {	
					if ( activationBoth) {
						new Runner(DS.classAllIndices[j],DS.classAllIndNme[j],false);
						if ( DS.freezs.size() > 0) {
							long currTime = ((System.currentTimeMillis()-tme));
							tmeCount++;
							timeSum+=currTime;
							avgTime =  (timeSum/(1000*tmeCount));
							UI.labTimePerRun.setText("Process: "+((System.currentTimeMillis()-tmeStart)/1000) + "/"+ Tools.myRound(avgTime*cycles,1)+"[s]");
							tme = System.currentTimeMillis();
							if ( !SolverStart.immediateStop ) fillStatistics("D+A > A");
						}
						new Runner(DS.classAllIndices[j],DS.classAllIndNme[j],true);
						if ( DS.freezs.size() > 0) {
							long currTime = ((System.currentTimeMillis()-tme));
							tmeCount++;
							timeSum+=currTime;
							avgTime =  (timeSum/(1000*tmeCount));
							UI.labTimePerRun.setText("Process: "+((System.currentTimeMillis()-tmeStart)/1000) + "/"+ Tools.myRound(avgTime*cycles,1)+"[s]");
							tme = System.currentTimeMillis();
							if ( !SolverStart.immediateStop ) fillStatistics("D+A > DxA");
						}
						
					}else {
						new Runner(DS.classAllIndices[j],DS.classAllIndNme[j],activationIsDst);
						if ( DS.freezs.size() > 0) {
							long currTime = ((System.currentTimeMillis()-tme));
							tmeCount++;
							timeSum+=currTime;
							avgTime =  (timeSum/(1000*tmeCount));
							UI.labTimePerRun.setText("Process: "+((System.currentTimeMillis()-tmeStart)/1000) + "/"+ Tools.myRound(avgTime*cycles,1)+"[s]");
							tme = System.currentTimeMillis();
							if ( !SolverStart.immediateStop )
								if ( activationIsDst) {
									fillStatistics("DxA");
								}else {
									fillStatistics("A");
								}
						}
	
					}
										
				}

				
			}
			// Cycle wise Classification
			// 73
			if ( !SolverStart.immediateStop && Opts.showDevelopment) {	
				UI.sp1D.dats.clear();
				main.remove("model");
				main.remove("FingerPrints");
				for (int j=0;j<DS.freezs.size();j++) {
					JSONObject modl = DS.freezs.get(j).getModelAsJson();
					main.append("model", modl);
					main.append("FingerPrints", Tools.getFingerPrint(modl.toString()+Opts.getOptsAsJson().toString()));
				}
	
				DS.setEnsemble(main);
				new Classify();
				rACount++;
				rollingAccuracyTest.add( (float)Classify.accuracyTest);
				rollingAccuracyTrain.add( (float)Classify.accuracyTrain);
				
				//
				
				rollingAccuracyX.add( (float)rACount);
				
				float[] rATest 			= new float[rollingAccuracyTest.size()];
				float[] rATrain 		= new float[rollingAccuracyTest.size()];
				float[] basicProb 		= new float[rollingAccuracyTest.size()];

				
				float[] rAx 		= new float[rollingAccuracyTest.size()];
				for (int j=0;j<rATest.length;j++) {
					rATest[j] 	= rollingAccuracyTest.get(j);
					rATrain[j] 	= rollingAccuracyTrain.get(j);
					rAx[j] 	= rollingAccuracyX.get(j);
					basicProb[j] = (float)prob*100; 
				}
				
				// Basic Probability
				
				
				
				
				UI.sp1D.setXY(rAx, rATrain, 13, Color.red, "Train", true, true, true);	
				UI.sp1D.setXY(rAx, rATest, 13, Color.blue, "Validation", true, true, true);
				UI.sp1D.setXY(rAx, basicProb, 13, Color.black, "random", false, true, false);

					UI.sp1D.refreshPlot();

					// Loadings
					float[][] weight = new float[DS.numClasses][DS.numVars];
					float[] wx = new float[DS.numVars];
					for (int j=0;j<wx.length; j++) {
						wx[j] = j+1;
					}
					for (int j=0;j<DS.freezs.size(); j++) {
						MC_Freeze mc = DS.freezs.get(j);
						int index = mc.targetColorIndex;
						int index0 = Tools.getIndexOfTarget(index);
						for (int p=0;p<Opts.numDims; p++) {
							for (int a=0; a<mc.eigenVec.length;a++) {
								weight[index0][a] += Math.abs(mc.eigenVec[a][p]);
							}
						}
					}
					UI.sp2D.dats.clear();
					for (int a=0; a<weight.length;a++) {
						UI.sp2D.setXY(wx, weight[a], 12,  Tools.getClassColor(a), DS.classAllIndNme[a], true, true, true);
					}
					UI.sp2D.refreshPlot();
			}
		}
		
				
		main.remove("model");
		main.remove("FingerPrints");
		for (int i=0;i<DS.freezs.size();i++) {
			JSONObject modl = DS.freezs.get(i).getModelAsJson();
		
			main.append("model", modl);

			main.append("FingerPrints", Tools.getFingerPrint(modl.toString()+Opts.getOptsAsJson().toString()));
		}
		
		DS.setEnsemble(main);
		new Classify();
		
		UI.proStatus.setValue(0);
		DS.setEnsemble(main);
		UI.txtEnsemble.setText(main.toString(3));
		UI.labTimePerRun.setText("Process: ---");
	    UI.menuFile.setEnabled(true);
	    isRunning = false;
	    UI.labStatusIcon.setIcon(new ImageIcon(ClassLoader.getSystemResource("colGreen.png")));
	    UI.refreshStatus();
	}

	private static void fillStatistics(String type) {
	
		
		//	FILL STATISTICS
		Object[] row = new Object[16];
		MC_Freeze mc = DS.freezs.get(DS.freezs.size()-1);
		String val = ""+DS.freezs.size();
		while (val.length()<6) {
			val = "0"+val;
		}
		
		row[0] 			= val; //DS.freezs.size();
		row[1] 			= type;
		row[2] 			= DS.classAllIndNme[Tools.getIndexOfTarget(mc.targetColorIndex)];
		row[3] 			= mc.tp_fp_tn_fn[0][0];
		row[4] 			= mc.tp_fp_tn_fn[1][0];
		row[5] 			= mc.tp_fp_tn_fn[2][0];
		row[6] 			= mc.tp_fp_tn_fn[3][0];
		double sens 	= (double)(mc.tp_fp_tn_fn[0][0])/(double)(mc.tp_fp_tn_fn[0][0]+mc.tp_fp_tn_fn[3][0]);
		row[7] 			= Tools.myRound(sens,4);
		double spes 	= (double)(mc.tp_fp_tn_fn[2][0])/(double)(mc.tp_fp_tn_fn[2][0]+mc.tp_fp_tn_fn[1][0]);
		row[8] 			= Tools.myRound(spes,4);
		row[9] 			= mc.tp_fp_tn_fn[0][1];
		row[10] 		= mc.tp_fp_tn_fn[1][1];
		row[11] 		= mc.tp_fp_tn_fn[2][1];
		row[12] 		= mc.tp_fp_tn_fn[3][1];
		sens = (double)(mc.tp_fp_tn_fn[0][1])/(double)(mc.tp_fp_tn_fn[0][1]+mc.tp_fp_tn_fn[3][1]);
		row[13] 		= Tools.myRound(sens,4);
		spes = (double)(mc.tp_fp_tn_fn[2][1])/(double)(mc.tp_fp_tn_fn[2][1]+mc.tp_fp_tn_fn[1][1]);
		row[14] 		= Tools.myRound(spes,4);
		row[15] 		= Tools.myRound(((float)(mc.tp_fp_tn_fn[0][1]+ mc.tp_fp_tn_fn[2][1]))/ ((float)(mc.tp_fp_tn_fn[0][1]+mc.tp_fp_tn_fn[1][1]+mc.tp_fp_tn_fn[2][1]+mc.tp_fp_tn_fn[3][1])),4);
		
		UI.tmtableStat.addRow(row);
		

//		UI.heatMap.repaint();
	}
	public static void classify() {
		new Classify();
	}
	
	private static void abbruch(String txt ) {
		// TODO: 
	}
	
	
	public  static void analyzeRawData(String file) {
		Tools.sumryAdd ("+ The file ["+file+"] is composed of a matrix of "+DS.numSamples+" samples and "+DS.numVars+" variables. ");
		Tools.sumryAdd ("Samples are classified in "+DS.numClasses+" classes: ");
		Tools.sumryAdd ("\n");
		Tools.sumryAdd (Tools.txtLen("Class")+"\t"+Tools.txtLen("Population") + "\n");
		Tools.sumryAdd ("________________\t________________" + "\n");
		int min=-1;int minCl = -1; 
		for (int i=0;i< DS.numClasses;i++) {
			Tools.sumryAdd (Tools.txtLen(""+DS.classAllIndNme[i])+"\t"+Tools.txtLen(""+DS.classAllIndPop[i])+"\n");
			if (DS.classAllIndPop[i] < min || min == -1 ) {
				min = DS.classAllIndPop[i];
				minCl = i;
			}
		}
		Tools.sumryAdd ("\n");
		
//		if ( DS.numClasses > 20) {
//			Tools.sumryAdd ("\n");	
//		}
				
		if ( DS.numClasses < 2) {
			Tools.sumryAdd ("Low Class number, Training is not possible. ");
			Tools.sumryAdd ("You can apply a previously saved enemble for classification. ");
		}else {
				
			Tools.sumryAdd ("*Variable listing: ");
			Tools.sumryAdd ("\n");
			
			double[] ParetoScaleAvg	= new double[DS.numVars];										// AVG and SD
			double[] ParetoScaleSd	= new double[DS.numVars];
			double[][] aurocs = null;
			if ( DS.numVars < 100 && DS.numSamples < 500) aurocs = Tools.calculateAurocs();
			for(int a = 0;a<DS.numVars;a++){
				double[] vals = new double[DS.numSamples];
				double avg = 0;
				double c = 0;
				for(int f = 0;f<DS.numSamples;f++){
					vals[f] = 	DS.rawData[f][a];
					avg+=		DS.rawData[f][a];
					c++;
				}
				double sd = Tools.calculateSD(vals)[1];
				avg /= c;
				ParetoScaleAvg[a] = avg;
				ParetoScaleSd[a] = sd;
			}
			Tools.sumryAdd (Tools.txtLen("Variable") +"\t"+ Tools.txtLen("Average") + "\t" + Tools.txtLen("Standard Dev.")+ "\t" + Tools.txtLen("AUROC max") + "\n");
			Tools.sumryAdd ("________________\t________________\t________________\t________________" + "\n");
			Tools.sumryAdd ("\n");
			 
			for (int i=0;i< DS.numVars;i++) {
				if ( aurocs != null) {
					Tools.sumryAdd (Tools.txtLen("["+(i+1)+"] "+DS.AreaNames[i]) + "\t" + Tools.txtLen(""+Tools.myRound(ParetoScaleAvg[i],4))+ "\t" 
				+ Tools.txtLen(""+Tools.myRound(ParetoScaleSd[i],4)) +"\t" + Tools.txtLen(DS.classAllIndNme[(int)aurocs[i][1]])+" | "+Tools.myRound(aurocs[i][0],4) + "\n");
				}else{
					Tools.sumryAdd (Tools.txtLen(DS.AreaNames[i]) + "\t" + Tools.txtLen(""+Tools.myRound(ParetoScaleAvg[i],4))+ "\t" 
							+ Tools.txtLen(""+Tools.myRound(ParetoScaleSd[i],4)) + "\n");
				}
			}
		
			
			Tools.sumryAdd ("----------------------------------------------------------------------------" + "\n");
			if ( min > Opts.minPopulation ) {																		// MindestAnzahl Population / Class
				Tools.sumryAdd ("+ Population is sufficient (>"+Opts.minPopulation+") to perform training and validation."+"\n");
				
			}else {
				Tools.sumryAdd ("[!] Population of e.g. '" + DS.classAllIndNme[minCl] + "' is too small (<"+Opts.minPopulation+"). Training & Validation is disadvised.");
				Tools.sumryAdd (" You can still train the data by setting the trainRation to '1.0'. All classe's data will be used for model training"
						+ ", no testing data will be created. The resulting model is unvalidated and generalization to new data is likely poor."
						+ " Use such models only for evaluation, not for production/publishing."+"\n");
				
				int erg = JOptionPane.showConfirmDialog(UI.jF, "<HTML><H3>Low class population number</H3>Set trainRatio to 1.0? <BR> 100% data will be used for training, no testing</HTML>", SolverStart.app, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if ( erg == JOptionPane.YES_OPTION) Opts.trainRatio = 1;
				UI.txtOpts.setText(Opts.getOptsAsJson().toString(3));
			}
			
		
	
			if ( DS.numVars < DS.numSamples  ) {																			// MindestAnzahl Population / Class
				Tools.sumryAdd ("+ Number of variables is reasonable for training (#Variables < #Samples)."+"\n");
			}else {
				Tools.sumryAdd ("[!] The number of variables surpasses the number of samples. Training is disadvised.");
				Tools.sumryAdd (" Excessive variables typically push models to overfit. Generalization to new data is likely poor.\"\r\n"  
						+ " Use such models only for evaluation, not for production/publishing."+"\n");
			}
			
			if ( DS.numVars < 2  ) {																						// MindestAnzahl Population / Class
				Tools.sumryAdd ("[!] The number of variables is too small.");
			}
			Tools.sumryAdd ("\n");
		}
//		if ( DS.numVars*20 > 500) {
//			int erg = JOptionPane.showConfirmDialog(UI.jF, "<HTML><H3>Auto adjust Options?</H3></HTML>", SolverStart.app, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
//			if ( erg == JOptionPane.YES_OPTION) {
//				double nnn = (DS.numVars-10)/10;
//				int val = (int)(1500*Runner.getSigmoid(nnn));
//				Opts.noBetterStop = val;
//				UI.txtOpts.setText(Opts.getOptsAsJson().toString(3));
//				
//			}
//			
//		}
	}

	
	public static boolean importDataCSV(String datei){								// nur ClassenName und Daten
		
		 String split = ","; char cSplit = ',';
		 boolean fromClip = false;
		 boolean commaAsDecimalSep = false;
		 
		 if ( datei == null) {
			 fromClip = true;
			 split = "\t";cSplit = '\t';
		 }
		 
		 boolean fail = false;
		  StringBuilder contents = new StringBuilder();
		  
		  if ( !fromClip ) {
			  File file = new File(datei);
			  BufferedReader reader = null;
		        try {
		            reader = new BufferedReader(new FileReader(file));
		            String text = null;
	
		            while ((text = reader.readLine()) != null) {
		                contents.append(text)
		                        .append("\n");
		            }
		        } catch (IOException e) {
		            e.printStackTrace();
		        } finally {
		            try {
		                if (reader != null) {
		                    reader.close();
		                }
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		        }
		  }else {
			  String data = null;
				try {
					data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
				} catch (HeadlessException e) {	e.printStackTrace();} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 

				if(data==null) {
					abbruch ("No data");
					return false;
				}
				contents.append(data);
		  }
		  
		    String[] lines = contents.toString().split("\n");
	        String[] test = lines[1].split(split);
	        
	        int noFiles = lines.length;
	        int noAreas = test.length-1;
	        
	        int classNamesPos = -1;
	        for (int i=0;i<test.length;i++) {
	        	if (!Tools.isNumeric(test[i]) ) 
	        		classNamesPos = i;
	        }
	        
	        int c0 = 0;
	        for (int i=0;i<test.length;i++) {
	        	if ( Tools.isNumeric(test[i]) )c0++;
	        }
	        
	        // Header with split chars within quotes
	    	String head = lines[0];
	    	StringBuffer newLine = new StringBuffer ();
	    	boolean inQuote = false;										// Check if Split-Characters are embedded in quotes, replace if is
	    	char cQuote = '"';
	    	for (int i=0;i<head.length();i++) {
	    		char tst = head.charAt(i); 
	    		if(Character.compare(tst, cQuote) == 0){
	    			if (inQuote ) {
	    				inQuote=false;
	    			}else {
	    				inQuote=true;
	    			}
	    		}
	    		if ( Character.compare(tst, cSplit) == 0 && inQuote){
	    			newLine.append("_");
	    		}else {
	    			newLine.append(tst);
	    		}
	    	}
	    	lines[0] = newLine.toString();

	        
	        test = lines[0].split(split);
	        int c1 = 0;
	        for (int i=0;i<test.length;i++) {
	        	if ( Tools.isNumeric(test[i]) )c1++;
	        }
	        boolean hasHeader = true;
	        if ( c0 == c1 )hasHeader = false;
	        // ',' as decimal separator
	        // 86
//		        if ( c0 < noAreas) {
//		        	commaAsDecimalSep = true;
//		        	test = lines[1].replace(",", ".").split(split);
//		        	c0 = 0;
//			        for (int i=0;i<test.length;i++) {
//			        	if ( Tools.isNumeric(test[i]) )c0++;
//			        }
//			        if ( c0 < noAreas) return false;
//		        }
		        
	        
	        DS.rawData = new double[noFiles][noAreas];
	        DS.AreaNames = new String[noAreas];
	        DS.SampleNames = new String[noFiles];
	        DS.classIndex = new int[noFiles];
	        DS.ClassNames = new String[noFiles];
	         
	        if ( !hasHeader ) {
	        	for (int i=0;i<noAreas; i++) {
	        		 DS.AreaNames [i] = '\"'+"VAR:"+i+'\"';
	        		 
	        	}
	        }else {
	        	noFiles--;
	        	DS.SampleNames = new String[noFiles];
			    DS.classIndex = new int[noFiles];
			    DS.ClassNames = new String[noFiles];
			    DS.rawData = new double[noFiles][noAreas];
			    int c = 0;
			    
			    if ( hasHeader) {

			    	test = lines[0].split(split);
			    	for (int i=0;i<test.length; i++) {
		        		 if ( i!= classNamesPos) { DS.AreaNames [c] = '\"'+test[i]+'\"';
			        		 c++;
		        		 }
		        	}
			    }else {
			    	for (int i=0;i< DS.AreaNames.length; i++) {
			    		 DS.AreaNames[i] = '\"'+"var_"+i+'\"';
			    	}
			    }
			    
			    c=0;
			    
	        }
	        int fstLine = 0;
	        if ( hasHeader) fstLine = 1;
	        ArrayList<String> allClassNames = new ArrayList<String>(); 
	        for(int i=fstLine;i<lines.length;i++){
	        	DS.SampleNames[i-fstLine] = "Sample: "+ (1+i-fstLine);
	        	String lne = lines[i]; 
	        	if ( commaAsDecimalSep) lne = lne.replace(",", ".");
	        	test = lne.split(split);
	        	int c=0;
	        	for (int j=0;j<test.length; j++) {
	        		 if ( j!= classNamesPos) {
	        			 try {
		        			 DS.rawData[i-fstLine][c] = Double.parseDouble(test[j]);
		        			 c++;
	        			 }catch (NumberFormatException e) {
	        				 DS.rawData[i-fstLine][c]  = 0;
		        			 fail = true;
		        			}
	        			 
	        		 }else {
	        			 DS.ClassNames[i-fstLine] = test[j];
	        			 boolean isIn = false;
	        			 for (int k=0;k<allClassNames.size();k++) {
	        				 if (DS.ClassNames[i-fstLine].equals(allClassNames.get(k)))isIn = true; 
	        			 }
	        			 if (!isIn) {
	        				 allClassNames.add(test[j]);
	        			 }
	        		 }
	        		 
	        	}
	        }
	        for (int i=0;i<DS.ClassNames.length;i++) {
	        	 for (int k=0;k<allClassNames.size();k++) {
    				 if (DS.ClassNames[i].equals(allClassNames.get(k)))DS.classIndex[i] = k;
    			 }
	        }
	        if (fail) {
	        	JOptionPane.showMessageDialog(null, "<HTML><H3>Import of (some) data failed</H3>", SolverStart.app, JOptionPane.WARNING_MESSAGE);
	        	return false;
	        }

        return true;
	}
	
	
	public static void importData(String datei){
		
	boolean fail = false;
		
	  File file = new File(datei);
	  StringBuilder contents = new StringBuilder();
	  BufferedReader reader = null;
	        try {
	            reader = new BufferedReader(new FileReader(file));
	            String text = null;

	            while ((text = reader.readLine()) != null) {
	                contents.append(text)
	                        .append(System.getProperty(
	                                "line.separator"));
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (reader != null) {
	                    reader.close();
	                }
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	        String[] lines = contents.toString().split(System.getProperty("line.separator"));
	        String[] test = lines[0].split("\t");
	        int fstLine = 0;
	        for(int i=0;i<lines.length;i++){
	        	if(!lines[i].startsWith("\\\\")){
	        		test = lines[i].split("\t");
	        		fstLine = i;
	        		break;
	        	}
	        }
	        
	        int noFiles;
        	int noAreas = 0;
        	noFiles = lines.length-1-fstLine;
        	
        	test = lines[fstLine].split("\t");
        	
	        boolean[] areaIsLabel = new boolean[test.length-4];
	        // [Label rausschmeissen]
	        for(int i=4;i<test.length;i++){
	        	if ( test[i].toLowerCase().contains("[label]")) {
	        		areaIsLabel[i-4] = true;
	        	}else {
	        		noAreas++;
	        	}
	        }

	       //  noAreas = test.length-4;
    	
	         if ( noFiles < 1 || noAreas < 1) fail = true;
	         
	         DS.rawData 		= new double[noFiles][noAreas];
	         DS.AreaNames 		= new String[noAreas];
	         DS.SampleNames 	= new String[noFiles];
	         DS.classIndex 		= new int[noFiles];
	         DS.ClassNames 		= new String[noFiles];
//	         DS.noTrainingSet	= new boolean[noFiles];
	         //areaIsLabel = new boolean[noAreas];
     	
    	  // Areas
	      
	        int ac = 0;
	        for(int i=4;i<test.length;i++){
	        	if ( !areaIsLabel[i-4]) {
	        		DS.AreaNames[ac] = '\"'+test[i]+'\"';
	        		ac++;
	        	}
	        }
	        
	        // Files
	        int c = 0;
	        for(int i=fstLine+1;i<lines.length;i++){
	        	UI.proStatus.setValue((100*i)/lines.length);
		        if(!lines[i].startsWith("#")){
		        	String[] tmp = lines[i].split("\t");
		        	String ClassName = tmp[1];
		        	int ClassColIndex = (int)Integer.parseInt(tmp[2]);
		        	DS.classIndex[i-fstLine-1] = ClassColIndex;
		        	DS.ClassNames[i-fstLine-1] = ClassName;
//		        	if (ClassColIndex ==0 && ClassName.equals("unknown"))DS.noTrainingSet[i-fstLine-1] = true;
		        	if ( DS.ClassNames[i-fstLine-1].equals("")) DS.ClassNames[i-fstLine-1] = "undefined";
		        	DS.SampleNames[i-fstLine-1] = tmp[3];
		        	ac=0;
			        for(int j=4;j<tmp.length;j++){
			        	if ( !areaIsLabel[j-4]) {
			        		try {
			        			DS.rawData[i-fstLine-1][ac] = Double.parseDouble(tmp[j]);
			        		}catch (NumberFormatException e) {
			        			DS.rawData[i-fstLine-1][ac] = 0;
		        			    fail = true;
		        			}
			        		ac++;
			        	}
			        }
		        }else{
		        	// TODO: Comment
		        }
		     }
	        UI.proStatus.setValue(0);
	        if (fail) JOptionPane.showConfirmDialog(null, "<HTML><H3>Import of data failed</H3>", SolverStart.app, JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE);
	}
	
}
