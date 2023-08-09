import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

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
	 * Reduce Variable Count via SPPEARMAN Reduktion ?
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
	 */
 
	public static String 	app 			= "uSort";
	public static String 	appAdd 			= " 0.1";
	public static String 	revision 		= " 52";
	public static boolean 	isRunning 		= false;
	public static boolean 	immediateStop 	= false;
	public static long 		plotTimer 		= -1;
	public static boolean 	darkMode 		= false;
	public static Color 	backColor 		= Color.DARK_GRAY;
	public static Color 	frontColor 		= Color.LIGHT_GRAY;
	
	public static String dataFileName = "";
	
	public static void main(String[] args) {
		
		try {
	          //UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"); 
			backColor 	= new Color(255,255,253);
			frontColor 	= Color.DARK_GRAY;
	          UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			}catch( Exception be ) { be.printStackTrace(); }
		
		
		new UI();
		
		if(args.length>0 && args[0].endsWith(".dat")){
			if ( new File(args[0]).exists() ) {
				if(args[0].endsWith(".dat")){
					importData(args[0]);
					File f = new File(args[0]);
					DS.txtSummary = null;
					DS.fileName =  f.getName();
					UI.tmtable.setColumnCount(0);
					UI.tmtable.setRowCount(0);
					new DS();										
					DS.normParas = Tools.doNormData ();				
					SolverStart.dataFileName = f.getName();
					
					SolverStart.analyzeRawData(f.getName());
					UI.maintabbed.setSelectedIndex(UI.tab_Summary);
					UI.refreshStatus();
				}
			}
		}
	}
	public static void trainPattern() throws IOException {
		
		UI.menuFile.setEnabled(false);
		UI.txtEnsemble.setText("");
		
		immediateStop = false;
		isRunning = true;
		UI.tmtableStat.setRowCount(0);
		DS.fixedTrainSet = null;
		
		JSONObject main = new JSONObject();
		main.put("creator", SolverStart.app + SolverStart.appAdd);
		StringBuffer out = new StringBuffer();
		out.append(Opts.getOptsAsJson().toString(3));
		out.append(DS.getDSsAsJson().toString(3));
		main.put("Opts", Opts.getOptsAsJson());
		main.put("DS", DS.getDSsAsJson());
		
		UI.labTimePerRun.setText("Process: THIS MIGHT TAKE SOME TIME");
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	    LocalDateTime now = LocalDateTime.now();
	    
	    main.put("DateOfCreation"				, ""+dtf.format(now));
	    main.put(	"ObjectType"				, "ML_Ensemble");
	    main.put(	"ObjectVersion"				, "01.00"); 
	    main.put(	"Support"					,"dsanders@gmx.net");
		long tme = System.currentTimeMillis();
		long tmeStart = System.currentTimeMillis();
		double tmeCount = 0;
		double timeSum = 0;
		int cycles = Opts.numEnsemble*DS.numClasses;
		double avgTime = 0;
		
		boolean activationIsDst = false;
		boolean activationBoth = false;
		if ( Opts.activation.equals("DxA") )activationIsDst = true;		// HERE FIRST ACCURACY; THEN DISTANCE IS APPLIED
		if ( Opts.activation.equals("D+A") )activationBoth = true;
		
		UI.refreshStatus();
		
		UI.labStatusIcon.setIcon(new ImageIcon(ClassLoader.getSystemResource("colYellow.png")));
		for (int i=0;i<Opts.numEnsemble;i++) {
			UI.proStatus.setValue(100*i/Opts.numEnsemble);
			for (int j=0;j<DS.classAllIndices.length;j++) {
				if ( !SolverStart.immediateStop ) {	
					if ( activationBoth) {
						new Runner(DS.classAllIndices[j],DS.classAllIndNme[j],false);
						if ( DS.freezs.size() > 0) {
//							out.append(DS.freezs.get(DS.freezs.size()-1).getModelAsJson().toString(3));
//							JSONObject modl = DS.freezs.get(DS.freezs.size()-1).getModelAsJson();
//							main.append("model", modl);
//							main.append("FingerPrints", Tools.getFingerPrint(modl.toString()+Opts.getOptsAsJson().toString()));
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
//							out.append(DS.freezs.get(DS.freezs.size()-1).getModelAsJson().toString(3));
//							JSONObject modl = DS.freezs.get(DS.freezs.size()-1).getModelAsJson();
//							main.append("model", modl);
//							main.append("FingerPrints", Tools.getFingerPrint(modl.toString()+Opts.getOptsAsJson().toString()));
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
//							out.append(DS.freezs.get(DS.freezs.size()-1).getModelAsJson().toString(3));
//							JSONObject modl = DS.freezs.get(DS.freezs.size()-1).getModelAsJson();
//							main.append("model", modl);
//							main.append("FingerPrints", Tools.getFingerPrint(modl.toString()+Opts.getOptsAsJson().toString()));
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
		}
		
		
		// Remove bad performing models, retain Opts.retainModelNum
		if ( DS.freezs.size()>Opts.retainModelNum) {
			int erg = JOptionPane.showConfirmDialog(UI.jF, "<HTML><H3>Shrink ensemble?</H3> <BR> Number of models will be reduced to "+Opts.retainModelNum+" per class<BR> Only best perfoming models remain.</HTML>", SolverStart.app, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if ( erg == JOptionPane.YES_OPTION) {
				
				while (DS.freezs.size() > Opts.retainModelNum*DS.numClasses) {
					for (int j=0;j<DS.classAllIndices.length;j++) {
						int killTarget = DS.classAllIndices[j];
						int kill = -1;double min = 5;
						for (int i=0;i<DS.freezs.size();i++) {
							MC_Freeze mc = DS.freezs.get(i);
							if ( killTarget == mc.targetColorIndex ) {
								if ( mc.accuracyTest < min || i==0) {
									min = mc.accuracyTest;
									kill = i;
								}
							}
						}// DS.freeze.size
						if (kill>-1) {
							//System.out.println("Kill "+DS.freezs.get(kill).targetLabel + "\t"+DS.freezs.get(kill).accuracyTest);
							DS.freezs.remove(kill);
							UI.tmtableStat.removeRow(kill);
						}
					}
				}
			}
		}
		
		for (int i=0;i<DS.freezs.size();i++) {
			//System.out.println("Remain "+DS.freezs.get(i).targetLabel + "\t"+DS.freezs.get(i).accuracyTest);
			JSONObject modl = DS.freezs.get(i).getModelAsJson();
			main.append("model", modl);
			main.append("FingerPrints", Tools.getFingerPrint(modl.toString()+Opts.getOptsAsJson().toString()));
		}
		
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
		Object[] row = new Object[15];
		MC_Freeze mc = DS.freezs.get(DS.freezs.size()-1);
		row[0] 		= DS.freezs.size();
		row[1] 		= type;
		row[2] 		= DS.classAllIndNme[Tools.getIndexOfTarget(mc.targetColorIndex)];
		row[3] 		= mc.tp_fp_tn_fn[0][0];
		row[4] 		= mc.tp_fp_tn_fn[1][0];
		row[5] 		= mc.tp_fp_tn_fn[2][0];
		row[6] 		= mc.tp_fp_tn_fn[3][0];
		double sens = (double)(mc.tp_fp_tn_fn[0][0])/(double)(mc.tp_fp_tn_fn[0][0]+mc.tp_fp_tn_fn[3][0]);
		row[7] 		= Tools.myRound(sens,4);
		double spes = (double)(mc.tp_fp_tn_fn[2][0])/(double)(mc.tp_fp_tn_fn[2][0]+mc.tp_fp_tn_fn[1][0]);
		row[8] 		= Tools.myRound(spes,4);
		row[9] 		= mc.tp_fp_tn_fn[0][1];
		row[10] 		= mc.tp_fp_tn_fn[1][1];
		row[11] 		= mc.tp_fp_tn_fn[2][1];
		row[12] 		= mc.tp_fp_tn_fn[3][1];
		sens = (double)(mc.tp_fp_tn_fn[0][1])/(double)(mc.tp_fp_tn_fn[0][1]+mc.tp_fp_tn_fn[3][1]);
		row[13] 		= Tools.myRound(sens,4);
		spes = (double)(mc.tp_fp_tn_fn[2][1])/(double)(mc.tp_fp_tn_fn[2][1]+mc.tp_fp_tn_fn[1][1]);
		row[14] 		= Tools.myRound(spes,4);
		
		UI.tmtableStat.addRow(row);
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
				Tools.sumryAdd (Tools.txtLen(DS.AreaNames[i]) + "\t" + Tools.txtLen(""+Tools.myRound(ParetoScaleAvg[i],4))+ "\t" 
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
			Tools.sumryAdd (" You can analyze the data by setting the trainRation to '1.0'. All classe's data will be used for model training"
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

	
	public static void importDataCSV(String datei){								// nur ClassenName und Daten
		 File file = new File(datei);
		 boolean fail = false;
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
	        String[] test = lines[1].split(",");
	        
	        int noFiles = lines.length;
	        int noAreas = test.length-1;
	        
	        int classNamesPos = -1;
	        for (int i=0;i<test.length;i++) {
	        	if (!Tools.isNumeric(test[i]) )classNamesPos = i;
	        }
	        int c0 = 0;
	        for (int i=0;i<test.length;i++) {
	        	if ( Tools.isNumeric(test[i]) )c0++;
	        }
	        test = lines[0].split(",");
	        int c1 = 0;
	        for (int i=0;i<test.length;i++) {
	        	if ( Tools.isNumeric(test[i]) )c1++;
	        }
	        boolean hasHeader = true;
	        if ( c0 == c1 )hasHeader = false;
	        DS.rawData = new double[noFiles][noAreas];
	        DS.AreaNames = new String[noAreas];
	        DS.SampleNames = new String[noFiles];
	        DS.classIndex = new int[noFiles];
	        DS.ClassNames = new String[noFiles];
	         
	        if ( !hasHeader ) {
	        	for (int i=0;i<noAreas; i++) {
	        		 DS.AreaNames [i] = "VAR:"+i;
	        	}
	        }else {
	        	noFiles--;
	        	DS.SampleNames = new String[noFiles];
			    DS.classIndex = new int[noFiles];
			    DS.ClassNames = new String[noFiles];
			    DS.rawData = new double[noFiles][noAreas];
			    int c = 0;
			    
			    if ( hasHeader) {
			    	test = lines[0].split(",");
			    	for (int i=0;i<test.length; i++) {
		        		 if ( i!= classNamesPos) { DS.AreaNames [c] = test[i];
			        		 c++;
		        		 }
		        	}
			    }else {
			    	for (int i=0;i< DS.AreaNames.length; i++) {
			    		 DS.AreaNames[i] = "var_"+1;
			    	}
			    }
			    
			    c=0;
			    
	        }
	        int fstLine = 0;
	        if ( hasHeader) fstLine = 1;
	        ArrayList<String> allClassNames = new ArrayList<String>(); 
	        for(int i=fstLine;i<lines.length;i++){
	        	DS.SampleNames[i-fstLine] = "Sample: "+ (1+i-fstLine);
	        	test = lines[i].split(",");
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
	        if (fail) JOptionPane.showConfirmDialog(null, "<HTML><H3>Import of (some) data failed</H3>", SolverStart.app, JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE);
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
	        		DS.AreaNames[ac] = test[i];
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
		        	if ( DS.ClassNames[i-fstLine-1].equals("")) DS.ClassNames[i-fstLine-1] = tmp[3];
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
	
	public static boolean importDataFromClipboard(){
		
		
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
		 String[] lines = data.toString().split("\n");
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
     	int noAreas;
     		
 		 noFiles = lines.length-1-fstLine;
	         noAreas = test.length-4;
 	
	         if (noFiles<2 || noAreas<2 ) {
	 			abbruch ("No data");
	 			return false;
			}
	         
	         DS.rawData = new double[noFiles][noAreas];
	         DS.AreaNames = new String[noAreas];
	         DS.SampleNames = new String[noFiles];
	         DS.classIndex = new int[noFiles];
	         DS.ClassNames = new String[noFiles];
  	
 	  // Areas
	        String[] tmp = lines[fstLine].split("\t");
	        for(int i=4;i<tmp.length;i++){
	        DS.AreaNames[i-4] = tmp[i];
	        }
		    
	        // Files
	        int c = 0;
	        for(int i=fstLine+1;i<lines.length;i++){
		        if(!lines[i].startsWith("#")){
		        	tmp = lines[i].split("\t");
		        	String ClassName = tmp[1];
		        	int ClassColIndex = (int)Integer.parseInt(tmp[2]);
		        	DS.classIndex[i-fstLine-1] = ClassColIndex;
		        	DS.ClassNames[i-fstLine-1] = ClassName;
		        	if ( DS.ClassNames[i-fstLine-1].equals("")) DS.ClassNames[i-fstLine-1] = tmp[3];
		        	DS.SampleNames[i-fstLine-1] = tmp[3];
			        for(int j=4;j<tmp.length;j++){
			        	DS.rawData[i-fstLine-1][j-4] = Double.parseDouble(tmp[j]);
			        }
		        }else{
		        	// TODO: Comment
		        }
		     }
	        return true;
	}
	
}
