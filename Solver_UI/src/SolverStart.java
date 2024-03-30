import java.awt.Color;
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
import java.util.Objects;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;


public class SolverStart {
	
	/*
	 * Main Class. 
	 *  + starts the app
	 *  + data import and check 
	 *  + data classification training commander
	 *  
	 *  Copyright(c) 2009-2024, Daniel Sanders, All rights reserved.
	 *  https://github.com/dsandersGit/GIT_Solver
	 */
	
	/*
	 * INTERNAL CHANGELOG
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
	 * 87: redo: normData inClassify
	 * 88: sd == 0 Bug
	 * 89: Check Options Malform
	 * 90: Export NormData
	 * 91: Debug variable ID > Remove
	 * 92: Copy data when saving ensemble
	 * 93: Feature 'Skip' > go on to next target
	 * 94: Feature 'Shuffle' > new random eigenvectors 
	 * 95: Skip change to Next, more inuitive
	 * 96: Remove Boost
	 * 97: System.out of ensemble to external LogStreamReader
	 * 98: Save raw data
	 * 99: BugFix
	 * 100: dstType EGO now will change fullClassification algorithm > more individual, distance to class style
	 * 101: Ratio Train / Test in ConfusionMatrix
	 * 102: Opts.minBetter reinstalled
	 * 103: Deleted Summary
	 * 104: Better EGO distance
	 * 105: Split in Live View
	 * 106: Nicer Split in Live View
	 * 107: Resize Algo Image
	 * 108: Load respects "<areaSelection>" in *.dat files WARNING> must be respected for ENSEMBLE out!!!!
	 * 109: Bug during import o *.dat
	 * 110: Option Dialog, Classify_Table Background Color Gradient
	 * 111: QR Code receipt https://github.com/nayuki/QR-Code-generator/tree/master
	 * 112: Classify Table 'run' sortable
	 * 113: UI.tableClassify Viewpoint improved
	 * 114: Pref Selection QR Code
	 * 115: Pan_Mining > LITE Visual Data Mining Feature for gradient and step detection + Option for Feature deactivation
	 * 116: LITE Visual Data Mining Feature plus Average Line
	 * 117: LITE Visual Data Mining Feature plus Average Line
	 * 118: Visual Check with score label bars
	 * 119; AUROC OFF, wrong estimations (same value ranking), Status correct num features, Mining Line limited to 1000, Mining Labels AVG+-STD
	 * 120: Ensemble-Tree
	 * 121: Batch Processing
	 * 122: new UI()
	 * 123: PCA support vis 'The R Project for Statistical Computing'
	 * 124: PCA support via 'The R Project for Statistical Computing'
	 * 125: QR with Bacthes
	 * 126: PCA support & Centered 
	 * 127: CSV quotes
	 * 128: Algo-scheme shifted
	 * 129: Option: Send Ensemble to Caller
	 * 130: BUGFIX: Train/Validation Fixed Trainset was by CLASS, not GLOBAL -> System 'learned' foes
	 * 131: Always new Eigenvectors
	 * 132: PC-Export, enables running PCA-ML
	 * 133: PC-Analysis > Dataset, enables running PCA-ML
	 * 134: PC-Analysis > Dataset, PCA Mod in Ensemble
	 * 135: UI upgardes, clear freezes in batches
	 * 136: UnitTest
	 * 137: fireTableDataChanged();
	 * 138: R ErrorStream
	 * 139: R PM
	 * 140: R no data 
	 * 	 */
 
	public static String 		app 			= "solver [ISI]";
	public static String 		appAdd 			= " 0.5";
	public static String 		revision 		= " 139";
	
	public static boolean 		isRunning 		= false;
	public static boolean 		immediateStop 	= false;
	public static boolean 		immediateSkip 	= false;				// 93
	public static boolean 		doShuffle		= false;				// 94
	public static boolean 		doRedrawOnClick	= true;					// 94
	
	public static long 			plotTimer 		= -1;
	public static Color 		backColor 		= Color.DARK_GRAY;
	public static Color 		frontColor 		= Color.LIGHT_GRAY;
	public static JSONObject 	defOptions    	= null;					// default options
	public static float[] 		rollingAccuracy	= null;    				// gain / class
	public static float[] 		rollingAccuracyX= null;
	
	// >>>>>>>>>>>>>>>>>>>>>>  Dark Color Settings
	public static boolean 		darkMode 		= false;
	private static String[] uiColorKeys = {"control","info","nimbusBase","nimbusAlertYellow","nimbusDisabledText"+
			"nimbusFocus","nimbusGreen","nimbusInfoBlue","nimbusLightBackground","nimbusOrange","nimbusRed"+
			"nimbusSelectedText","nimbusSelectionBackground","text"	};
	private static Color[] uiBaseColors = new Color[uiColorKeys.length];
	private static Color[] uiDarkColors = {new Color( 128, 128, 128),new Color(128,128,128),new Color( 18, 30, 49),
			 new Color( 248, 187, 0), new Color( 128, 128, 128),new Color(115,164,209),new Color(176,179,50),
			 new Color( 66, 139, 221),new Color( 18, 30, 49),new Color(191,98,4),new Color(169,46,34),
			 new Color( 255, 255, 255), new Color( 104, 93, 156),new Color( 230, 230, 230)};
	// <<<<<<<<<<<<<<<<<<<<<<
	
	public static void main(String[] args) {
		// taken from https://stackoverflow.com/questions/36128291/how-to-make-a-swing-application-have-dark-nimbus-theme-netbeans
		if (darkMode) {
			for (int i=0;i<uiColorKeys.length;i++) {
				uiBaseColors[i] = UIManager.getColor(uiColorKeys[i]);
			}
		
			frontColor 	= new Color(255,255,253);
			backColor 	= Color.DARK_GRAY;
			for (int i=0;i<uiColorKeys.length;i++) {
				UIManager.put(uiColorKeys[i],uiDarkColors[i]);
			}
		
		  UIManager.put( "control", new Color( 128, 128, 128) );
		  UIManager.put( "info", new Color(128,128,128) );
		  UIManager.put( "nimbusBase", new Color( 18, 30, 49) );
		  UIManager.put( "nimbusAlertYellow", new Color( 248, 187, 0) );
		  UIManager.put( "nimbusDisabledText", new Color( 128, 128, 128) );
		  UIManager.put( "nimbusFocus", new Color(115,164,209) );
		  UIManager.put( "nimbusGreen", new Color(176,179,50) );
		  UIManager.put( "nimbusInfoBlue", new Color( 66, 139, 221) );
		  UIManager.put( "nimbusLightBackground", new Color( 18, 30, 49) );
		  UIManager.put( "nimbusOrange", new Color(191,98,4) );
		  UIManager.put( "nimbusRed", new Color(169,46,34) );
		  UIManager.put( "nimbusSelectedText", new Color( 255, 255, 255) );
		  UIManager.put( "nimbusSelectionBackground", new Color( 104, 93, 156) );
		  UIManager.put( "text", new Color( 230, 230, 230) );
		  try {
		    for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
		      if ("Nimbus".equals(info.getName())) {
		          javax.swing.UIManager.setLookAndFeel(info.getClassName());
		          break;
		      }
		    }
		  } catch (ClassNotFoundException e) {
		    e.printStackTrace();
		  } catch (InstantiationException e) {
		    e.printStackTrace();
		  } catch (IllegalAccessException e) {
		    e.printStackTrace();
		  } catch (javax.swing.UnsupportedLookAndFeelException e) {
		    e.printStackTrace();
		  } catch (Exception e) {
		    e.printStackTrace();
		  }
		}else {
		try {
			backColor 	= new Color(255,255,253);
			frontColor 	= Color.DARK_GRAY;
	          UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			}catch( Exception be ) { be.printStackTrace(); }
		}
		
		defOptions = Opts.getOptsAsJson();					// save default startup options

		
       	new UI();							// 122
		
		if(args.length>0 && args[0].endsWith(".dat")){
			if ( new File(args[0]).exists() ) {
				if(args[0].endsWith(".dat")){
					importData(args[0]);
					File f = new File(args[0]);
					DS.txtSummary = null;
					DS.fileName =  f.getName();
					DS.filePath = f;
					UI.tmtableClassify.setColumnCount(0);
					UI.tmtableClassify.setRowCount(0);
					new DS();										
					DS.normParas = Tools.doNormData ();				
					SolverStart.analyzeRawData(f.getName());
					UI.maintabbed.setSelectedIndex(UI.tab_Summary);
					UI.refreshStatus();
				}
			}
		}
		
		if(args.length>1)
			DS.variableID = args[1]; 

		UI.loadEnsemble(true);
	}
	public static void trainPattern(int batchSize) throws IOException {

		DS.getFixedTrainSet();	
		
		for (int batch = 0;batch < batchSize;batch++) {
			
			DS.freezs.clear();
			
			// populates with running the accuracy of train / test results
			ArrayList<Float> rollingAccuracyTest = new ArrayList<Float>();
			ArrayList<Float> rollingAccuracyTrain = new ArrayList<Float>();
			ArrayList<Float> rollingAccuracyX = new ArrayList<Float>();
	
			int rACount = 0;													// running counter
			immediateStop = false;												// flag for user interrupting current training session
			isRunning = true;													// flag for running current training session
			
			// cleaning UI stuff
			UI.labAccuracy.setText("Accuracy: ---");
			UI.sp1D.dats.clear();
			UI.sp1D.refreshPlot();
			UI.sp2D.dats.clear();
			UI.sp2D.refreshPlot();
			UI.menuFile.setEnabled(false);
	//		UI.txtEnsemble.setText("");
			UI.tmtableValidation.setRowCount(0);
			UI.txtClassify.setText("");
			UI.labStatusIcon.setIcon(new ImageIcon(ClassLoader.getSystemResource("colYellow.png")));	// UI button color
			//72: Train/Test change only cycle wise
			
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
			
			StringBuffer out 						= new StringBuffer();
			DateTimeFormatter dtf 					= DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		    LocalDateTime now 						= LocalDateTime.now();
		    
			// Preparing Ensemble JSON
			JSONObject QRmain 						= new JSONObject();
			JSONObject main 						= new JSONObject();
			main.put("creator"						, SolverStart.app + SolverStart.appAdd + " r" + SolverStart.revision);
			out.append(								Opts.getOptsAsJson().toString(3));
			out.append(								DS.getDSsAsJson().toString(3));
			main.put("Opts"							, Opts.getOptsAsJson());
			main.put("DS"							, DS.getDSsAsJson());
		    main.put("DateOfCreation"				, ""+dtf.format(now));
		    main.put("ObjectType"					, "ML_Ensemble");
		    main.put("ObjectVersion"				, "01.00"); 
		    main.put("Support"						,"N/A");
		    if (DS.isPCA && MultiVariate_R.pcaModel != null) {
		    	main.put("PCA"						,MultiVariate_R.pcaModel);	
		    }
			QRmain.put("creator"					, SolverStart.app + SolverStart.appAdd );
			QRmain.put("Date"						, ""+dtf.format(now));
		    
		    
			// Time / run estimation
		    long 	tme 							= System.currentTimeMillis();
			long 	tmeStart 						= System.currentTimeMillis();
			double 	tmeCount 						= 0;
			double 	timeSum 						= 0;
			int 	cycles 							= Opts.numCycles*DS.numClasses;
			double 	avgTime 						= 0;
			
			// Activation Equation selection
			boolean activationIsDst 				= false;
			boolean activationBoth 					= false;
			if ( Opts.activation.equals("DxA") )	activationIsDst = true;		
			if ( Opts.activation.equals("D+A") )	activationBoth = true;
	
	
			for (int i=0;i<Opts.numCycles;i++) {
				//72: Train/Test change only cycle wise
				//75: Fixed Trainset Init moved from DS to SolverStart
				if ( !Opts.fixTrainSet ) DS.getFixedTrainSet();									// fetch the (initial) training set
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
											
					}else {
						cleanUI();
						UI.refreshStatus();
						return;
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
					main.put("confusionMatrix", Classify.confMatrixout.toString());
					
					rACount++;
					rollingAccuracyTest.add		((float)	Classify.accuracyTest);
					rollingAccuracyTrain.add	((float)	Classify.accuracyTrain);
					rollingAccuracyX.add		((float)	rACount);
					
					float[] rATest 				= new float[rollingAccuracyTest.size()];
					float[] rATrain 			= new float[rollingAccuracyTest.size()];
					float[] basicProb 			= new float[rollingAccuracyTest.size()];
				
					float[] rAx 				= new float[rollingAccuracyTest.size()];
					for (int j=0;j<rATest.length;j++) {
						rATest[j] 	= rollingAccuracyTest.get(j);
						rATrain[j] 	= rollingAccuracyTrain.get(j);
						rAx[j] 	= rollingAccuracyX.get(j);
						basicProb[j] = (float)prob*100; 
					}
					
					// Plotting
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
				if ( !SolverStart.immediateStop )  UnitTest.next = false;
			}			// Cycle Loop
			
			
			main.remove("model");
			main.remove("FingerPrints");
			for (int i=0;i<DS.freezs.size();i++) {
				JSONObject modl = DS.freezs.get(i).getModelAsJson();
				main.append("model", modl);
				main.append("FingerPrints", Tools.getFingerPrint(modl.toString()+Opts.getOptsAsJson().toString()));
			}
			
			
			new Classify();
			main.put("confusionMatrix", Classify.confMatrixout.toString());
			
			DS.setEnsemble(main);
			
//			System.out.println(Classify.accuracyTrain );//+ "\t" + Classify.accuracyTest); 
			
	//		UI.txtEnsemble.setText(main.toString(3));
			EnsembleTree.putEnsemble(UI.labAccuracy.getText(), UI.txtClassify.getText(), main);
			cleanUI();
//			UI.proStatus.setValue(0);
//			UI.labTimePerRun.setText("Process: ---");
//			UI.labAccuracy.setText("Process: ---");
//			UI.labRun.setText("Process: ---");
//		    UI.menuFile.setEnabled(true);
//		    isRunning = false;
//		    UI.labStatusIcon.setIcon(new ImageIcon(ClassLoader.getSystemResource("colGreen.png")));
//		    
		   
		    UI.refreshStatus();
		    
		    
		    //111
		    if (UI.menuActionChk_QR.isSelected() && batchSize == 1 ) {									// 125
			    String txtReceipt = QRmain.toString()+"\n"+Classify.confMatrixout.toString();
			    txtReceipt += "\nFP: " + Tools.getFingerPrint(txtReceipt);
			    BufferedImage image = generateBarCode(txtReceipt);
			    JLabel picLabel = new JLabel(new ImageIcon(image));
			    JOptionPane.showMessageDialog(null, picLabel, "Receipt", JOptionPane.PLAIN_MESSAGE, null);
		    }
		} // batch
		if ( UnitTest.logResult)UnitTest.result.add(DS.fileName + "\t"+Classify.accuracyTrain);
//		System.out.println(DS.fileName + "\t"+Classify.accuracyTrain);
		UnitTest.next = true;
	}
	private static void cleanUI() {
		UI.proStatus.setValue(0);
		UI.labTimePerRun.setText("Process: ---");
		UI.labAccuracy.setText("Process: ---");
		UI.labRun.setText("Process: ---");
	    UI.menuFile.setEnabled(true);
	    isRunning = false;
	    UI.labStatusIcon.setIcon(new ImageIcon(ClassLoader.getSystemResource("colGreen.png")));
	}
	private static BufferedImage generateBarCode(String text) throws IOException {
		// https://github.com/nayuki/QR-Code-generator/tree/master
		QrCode.Ecc errCorLvl = QrCode.Ecc.LOW;  // Error correction level
		QrCode qr = QrCode.encodeText(text, errCorLvl);  // Make the QR Code symbol
		BufferedImage img = toImage(qr, 6, 3);          // Convert to bitmap image
		return img;

	}
	private static BufferedImage toImage(QrCode qr, int scale, int border) {
		return toImage(qr, scale, border, 0xFFFFFF, 0x000000);
	}
	private static BufferedImage toImage(QrCode qr, int scale, int border, int lightColor, int darkColor) {
		Objects.requireNonNull(qr);
		if (scale <= 0 || border < 0)
			throw new IllegalArgumentException("Value out of range");
		if (border > Integer.MAX_VALUE / 2 || qr.size + border * 2L > Integer.MAX_VALUE / scale)
			throw new IllegalArgumentException("Scale or border too large");
		
		BufferedImage result = new BufferedImage((qr.size + border * 2) * scale, (qr.size + border * 2) * scale, BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < result.getHeight(); y++) {
			for (int x = 0; x < result.getWidth(); x++) {
				boolean color = qr.getModule(x / scale - border, y / scale - border);
				result.setRGB(x, y, color ? darkColor : lightColor);
			}
		}
		return result;
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
		
		UI.tmtableValidation.addRow(row);
		UI.tmtableValidation.fireTableDataChanged();

	}
	public static void classify() {
		new Classify();
	}
	
	private static void abbruch(String txt ) {
		// TODO: 
	}
	
	
	public  static void analyzeRawData(String file) {
		Tools.sumryAdd ("+ The file ["+file+"] is composed of a matrix of "+DS.numSamples+" samples and "+DS.numVars+" features. ");
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
		

		if ( DS.numClasses < 2) {
			Tools.sumryAdd ("Low Class number, Training is not possible. ");
			Tools.sumryAdd ("You can apply a previously saved enemble for classification. ");
		}else {
	
			Tools.sumryAdd ("*Feature listing: ");
			Tools.sumryAdd ("\n");
			
			double[] ParetoScaleAvg	= new double[DS.numVars];										// AVG and SD
			double[] ParetoScaleSd	= new double[DS.numVars];
			double[][] aurocs = null;
			//if ( DS.numVars < 100 && DS.numSamples < 500) aurocs = Tools.calculateAurocs();
			for(int a = 0;a<DS.numVars;a++){
				double[] vals = new double[DS.numSamples];
				for(int f = 0;f<DS.numSamples;f++){
					vals[f] = 	DS.rawData[f][a];
				}
				double[] sd = Tools.calculateSD(vals);
				
				ParetoScaleAvg[a] = sd[0];
				ParetoScaleSd[a] = sd[1];
			}
			
			
			Tools.sumryAdd (Tools.txtLen("Feature") +"\t"+ Tools.txtLen("Average") + "\t" + Tools.txtLen("Standard Dev.")+ "\t" + Tools.txtLen("AUROC max") + "\n");
			Tools.sumryAdd ("________________\t________________\t________________\t________________" + "\n");
			Tools.sumryAdd ("\n");
			 
			for (int i=0;i< DS.numVars;i++) {
				if ( aurocs != null) {
					Tools.sumryAdd (Tools.txtLen("["+(i+1)+"] "+DS.AreaNames[i]) + "\t" + Tools.txtLen(""+Tools.myRound(ParetoScaleAvg[i],4))+ "\t" 
				+ Tools.txtLen(""+Tools.myRound(ParetoScaleSd[i],4)) +"\t" + Tools.txtLen(DS.classAllIndNme[(int)aurocs[i][1]])+" | "+Tools.myRound(aurocs[i][0],4) + "\n");
				}else{
					Tools.sumryAdd (Tools.txtLen("["+(i+1)+"] "+DS.AreaNames[i]) + "\t" + Tools.txtLen(""+Tools.myRound(ParetoScaleAvg[i],4))+ "\t" 
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
				
				if ( !UnitTest.logResult) {
					int erg = JOptionPane.showConfirmDialog(UI.jF, "<HTML><H3>Low class population number</H3>Set trainRatio to 1.0? <BR> 100% data will be used for training, no testing</HTML>", SolverStart.app, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if ( erg == JOptionPane.YES_OPTION) Opts.trainRatio = 1;
					UI.txtOpts.setText(Opts.getOptsAsJson().toString(3));
				}
				
			}
			
		
	
			if ( DS.numVars < DS.numSamples  ) {																			// MindestAnzahl Population / Class
				Tools.sumryAdd ("+ Number of features is reasonable for training (#Features < #Samples)."+"\n");
			}else {
				Tools.sumryAdd ("[!] The number of features surpasses the number of samples. Training is disadvised.");
				Tools.sumryAdd (" Excessive features typically push models to overfit. Generalization to new data is likely poor.\"\r\n"  
						+ " Use such models only for evaluation, not for production/publishing."+"\n");
			}
			
			if ( DS.numVars < 2  ) {																						// MindestAnzahl Population / Class
				Tools.sumryAdd ("[!] The number of features is too small.");
			}
			Tools.sumryAdd ("\n");
		}
		
		// FUN Digits out
//		int size = 50;
//		BufferedImage img = new BufferedImage(size*9,size*9,BufferedImage.TYPE_BYTE_GRAY);
//		for (int sx=0;sx<size;sx++) {
//			for (int sy=0;sy<size;sy++) {
//				for (int i=0;i<8;i++) {
//					for (int j=0;j<8;j++) {
//						int val = (int)(DS.rawData[sx*size+sy][i*8+j]);
//						if ( (val*15)<255)
//							img.setRGB(sx*9+i, sy*9+j, new Color(val* 15,val* 15,val* 15).getRGB());
//					}
//				}
//			}
//		}
//		File outputfile = new File("digits.png");
//		try {
//			ImageIO.write(img, "png", outputfile);
//			System.out.println(outputfile.getAbsolutePath());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	}
	private static String cleanTxt(String txt) {
		return '\"'+txt.replaceAll("\"", "")+'\"';
	}
	
	public static boolean importDataCSV(String datei, String split){								// nur ClassenName und Daten
		
		 //String split = ",";
		 char cSplit = split.charAt(0);
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
	
		            while ((text = reader.readLine()) != null) { contents.append(text).append("\n");
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
	        DS.timeIndex 		= null;;
	         
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
		        		 if ( i!= classNamesPos) {
		        			 DS.AreaNames [c] = cleanTxt(test[i]); //
			        		 c++;
		        		 }
		        	}
			    }else {
			    	for (int i=0;i< DS.AreaNames.length; i++) {
			    		 DS.AreaNames[i] = cleanTxt("var_"+i);
			    	}
			    }
			    
			    c=0;
			    
	        }
	        int fstLine = 0;
	        if ( hasHeader) fstLine = 1;
	        ArrayList<String> allClassNames = new ArrayList<String>(); 
	        for(int i=fstLine;i<lines.length;i++){
	        	DS.SampleNames[i-fstLine] = cleanTxt("Sample: "+ (1+i-fstLine));
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
	        			 DS.ClassNames[i-fstLine] = cleanTxt(test[j]);;
	        			 boolean isIn = false;
	        			 for (int k=0;k<allClassNames.size();k++) {
	        				 if (DS.ClassNames[i-fstLine].equals(allClassNames.get(k)))isIn = true; 
	        			 }
	        			 if (!isIn) {
	        				 allClassNames.add(cleanTxt(test[j]));
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
	        boolean[] useArea = null;
	        boolean[] selFiles = null;
	        for(int i=0;i<lines.length;i++){
	        	if(!lines[i].startsWith("\\\\")){
	        		test = lines[i].split("\t");
	        		fstLine = i;
	        		break;
	        	}else {
	        		test = lines[i].split("\t");
	        		if(test[1].equals("<areaSelection>")){
	        			useArea = new boolean[test.length-2];
	        			 for(int a=2;a<test.length;a++){
	        				 if(test[a].equals("0"))useArea[a-2]=false;
	        				 if(test[a].equals("1"))useArea[a-2]=true;
	        			 }
	        		}
	        		if(test[1].equals("<fileSelection>")){
	        			selFiles = new boolean[test.length-2];
	        			 for(int a=2;a<test.length;a++){
	        				 if(test[a].equals("0"))selFiles[a-2]=false;
	        				 if(test[a].equals("1"))selFiles[a-2]=true;
	        			 }
	        		}
	        	}
	        }
	        
	        int noFiles;
        	int noAreas = 0;
        	noFiles = lines.length-1-fstLine;
        	
        	test = lines[fstLine].split("\t");
        	if( useArea==null ) {
        		useArea = new boolean[test.length-4];
        		for (int i=0;i<useArea.length;i++) {
        			useArea[i] = true;
        		}
        	}
        	
	        // [Label rausschmeissen]
        	int posTimeIndex = -1;
	        for(int i=4;i<test.length;i++){
	        	if ( test[i].toLowerCase().contains("[label]")) {
	        		useArea[i-4] = false;
	        		if (test[i].toLowerCase().contains("timeindex") && test[i].toLowerCase().contains("[label]" )){
	        			posTimeIndex = i;
	        		}
	        	}else {
	        		if ( useArea[i-4])
	        			noAreas++;
	        	}
	        }
	
		       //  noAreas = test.length-4;
    	
	         if ( noFiles < 1 || noAreas < 1) fail = true;
	         
	         
	         DS.usedAreas 		= useArea;
	         DS.rawData 		= new double[noFiles][noAreas];
	         DS.AreaNames 		= new String[noAreas];
	         DS.SampleNames 	= new String[noFiles];
	         DS.classIndex 		= new int[noFiles];
	         DS.ClassNames 		= new String[noFiles];
	         if ( posTimeIndex >= 0) {
	        	 DS.timeIndex 		= new double[noFiles];
	         }else{
	        	 DS.timeIndex 		= null;;
	         }
//	         DS.noTrainingSet	= new boolean[noFiles];
	         //areaIsLabel = new boolean[noAreas];
     	
    	  // Areas
	      
	        int ac = 0;
	        for(int i=4;i<test.length;i++){
	        	if ( useArea[i-4]) {
	        		DS.AreaNames[ac] = cleanTxt(test[i]);//;'\"'+test[i].replaceAll("\"", "")+'\"';
	        		ac++;
	        	}
	        }
	        
	        // Files
	        int c = 0;
	        for(int i=fstLine+1;i<lines.length;i++){
	        	UI.proStatus.setValue((100*i)/lines.length);
		        if(!lines[i].startsWith("#")){
		        	String[] tmp = lines[i].split("\t");
		        	String ClassName = cleanTxt(tmp[1]);
		        	int ClassColIndex = (int)Integer.parseInt(tmp[2]);
		        	DS.classIndex[i-fstLine-1] = ClassColIndex;
		        	DS.ClassNames[i-fstLine-1] = ClassName;
//		        	if (ClassColIndex ==0 && ClassName.equals("unknown"))DS.noTrainingSet[i-fstLine-1] = true;
		        	if ( DS.ClassNames[i-fstLine-1].equals("")) DS.ClassNames[i-fstLine-1] = "undefined";
		        	DS.SampleNames[i-fstLine-1] = cleanTxt(tmp[3]);
		        	ac=0;
			        for(int j=4;j<tmp.length;j++){
			        	if ( useArea[j-4]) {
			        		try {
			        			DS.rawData[i-fstLine-1][ac] = Double.parseDouble(tmp[j]);
			        		}catch (NumberFormatException e) {
			        			DS.rawData[i-fstLine-1][ac] = 0;
		        			    fail = true;
		        			}
			        		ac++;
			        	}else {																//	import timeIndex is available 'timeindex[label]'
			        		if ( j == posTimeIndex ) {
			        			try {
				        			DS.timeIndex[i-fstLine-1] = Double.parseDouble(tmp[j]);
				        		}catch (NumberFormatException e) {
				        			DS.timeIndex[i-fstLine-1] = 0;
			        			    fail = true;
			        			}
			        		}
			        	}
			        }
		        }else{					// ! "//"
		        	// TODO: Comment
		        }
		     }
	        UI.proStatus.setValue(0);
	        if (fail) JOptionPane.showConfirmDialog(null, "<HTML><H3>Import of data failed</H3>", SolverStart.app, JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE);
	}
	public void makeDataUp(int numClasses, int numData) {
		
        DS.rawData 			= new double[numClasses*numData][numData];
        DS.AreaNames 		= new String[numData];
        DS.SampleNames 		= new String[numClasses*numData];
        DS.classIndex 		= new int[numClasses*numData];
        DS.ClassNames 		= new String[numClasses*numData];
		
		for (int c=0;c<numClasses; c++) {
			for (int d=0;d<numData; d++) {
				DS.ClassNames[c*numData+d] = "Class_"+c;
				DS.classIndex[c*numData+d] = c;
				DS.AreaNames[d] = "Var_"+d;
				DS.SampleNames[c*numData+d] = "Sample_"+ (c*numData+d);
				
			}
		}
	}
}
