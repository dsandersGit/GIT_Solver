import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class DS {
	
	public static ArrayList<MC_Freeze> freezs = new ArrayList<MC_Freeze>();
	
	public static double 		rawData [][] 	= null; 				// RawData : Sample / Variable
	public static String[]		SampleNames 	= null;
	public static String[]		AreaNames 		= null;
	public static boolean[] 	usedAreas 		= null;					// Ignored Areas during import
	public static String[]		ClassNames 		= null;
	public static int[]			classIndex 		= null;					// Color per sample
	public static int[]			listClassIndex 		= null;					// Color per sample
	public static double 		normData [][] 	= null;
	
	public static int[]			classAllIndices	= null;					// [] Index of present Classes
	public static int[]			classAllIndPop	= null;					// [] Population of each Classes, index like classIndices
	public static String[]		classAllIndNme	= null;					// [] Population of each Classes, index like classIndices
	public static boolean[][]	fixedTrainSet	= null;					//
	//public static boolean[]		noTrainingSet	= null;					//
	
	public static double[][] 	normParas		= null;
	public static String 	    fileName		= "";
	public static File	 	    filePath		= null;
	
	public static int			numVars			= 0;
	public static int			numSamples		= 0;
	public static int			numClasses		= 0;
	
	public static float[] 		sepaX 	= null;
	public static float[] 		sepaY 	= null;
	
	public static JSONObject	js_Ensemble		= null;
	
	//public static String 	    TP_FP_TN_FN		= null;
	
	public static StringBuffer 	txtSummary		= null;
	//public static Color[]		classCols 		= null;
	public static Color[]		classCols 		= 
		{
			new Color(230,25,75),	// rot
			new Color(60,180,75),	// grün
			new Color(0,130,200),	// marineblau
			new Color(0,0,128),		// dunkelblau
			new Color(170,110,40),  //hellbraun
			new Color(0,0,0),		// schwarz
			new Color(70,240,240),	// cyan
			new Color(170,255,195),	// damped green
			new Color(220,190,255),	// damped violett
			new Color(128,0,0),		//dunkelbraun
			new Color(128,128,0),	// grünbraun
			new Color(0,128,128),	// olive
			new Color(255,255,25),	// gelb
			new Color(210,245,60),	// hellgruen
			new Color(145,30,180),	// violett
			new Color(240,50,230),	// pink
			new Color(128,128,128),	// erde
			new Color(250,190,212),	// damped pink
			new Color(255,215,180),	// damped orange
			new Color(255,250,200)	// damped yellow

	};	// 20 Colors = Max Count for Classes
	
	public static BufferedImage[] legendImage = null;
	// 91: 
	//public static String variableID = "-no_ID-";
	public static String variableID = "";

	
	
	public DS () {
		UI.labStatusIcon.setIcon(new ImageIcon(ClassLoader.getSystemResource("colBlue.png")));
		
		if ( AreaNames == null) System.out.println("AreaNames is null");
		
		DS.numVars 	= AreaNames.length;
		DS.numSamples = SampleNames.length;
		
		// Number of Classes
		ArrayList<Integer> clasIndex 		= new ArrayList<Integer>();
		ArrayList<Integer> clasIndexCount 	= new ArrayList<Integer>();
		ArrayList<String>  clasIndexName 	= new ArrayList<String>();
		for (int f=0;f<numSamples; f++) {
			boolean isIn = false;
			for ( int i=0;i<clasIndex.size();i++) {
				if ( classIndex[f] == clasIndex.get(i)) {
					clasIndexCount.set(i, clasIndexCount.get(i)+1);
					clasIndexName.set(i, ClassNames[f]);
					isIn = true;
				};
			}
			if ( !isIn ) {
				clasIndex.add(classIndex[f]);
				clasIndexCount.add(1);
				clasIndexName.add( ClassNames[f]);
			}
		}
		numClasses = clasIndex.size();

		classAllIndices = new int[numClasses];
		classAllIndPop = new int[numClasses];
		classAllIndNme = new String[numClasses];
		for ( int i=0;i<numClasses;i++) {
			classAllIndices[i] 	= clasIndex.get(i);
			classAllIndPop[i] 	= clasIndexCount.get(i);
			classAllIndNme[i]	= clasIndexName.get(i);
		}
		listClassIndex = new int[numSamples];
		for (int f=0;f<numSamples; f++) {
			listClassIndex	[f] = Tools.getIndexOfTarget(classIndex[f]);
		}
		
		
		legendImage = new BufferedImage[numClasses];
		for ( int i=0;i<numClasses;i++) {
			legendImage[i] = Tools.getLegendImage(i, classAllIndNme[i]) ;
		}
			
		sepaX = new float[DS.numSamples];
		sepaY = new float[DS.numSamples];
//		75: Fixed Trainset Init moved from DS to SolverStart
//		if ( Opts.fixTrainSet ) {
//			getFixedTrainSet();
//		}
		
		
		//bootsStrapp(10000, 500);
		
		
		// ***
	}
	private static void bootsStrapp(int bsBoost, int bsSet) {
		/*
		 * Array Histogrammit Daten Klasse[], Feature[], 
		 */
		
		StringBuffer collect = new StringBuffer();
		
		double[] aMin = new double[DS.numVars];
		double[] aMax = new double[DS.numVars];
		for (int a=0;a<DS.numVars;a++){
			 for (int f=0;f<DS.numSamples;f++){
				 if ( f==0 || aMin[a] > DS.rawData[f][a] )  aMin[a] = DS.rawData[f][a];
				 if ( f==0 || aMax[a] < DS.rawData[f][a] )  aMax[a] = DS.rawData[f][a];
			 }
        }
		for (int a=0;a<DS.numVars;a++){
			System.out.println(a+"\t"+aMin[a]+"\t"+aMax[a]);
		}
		System.out.println("----------------------------------------------------");
		// -----------------------------------
		 
		System.out.print("path\tclass\tindex\tsample\trun");										// HEADER
		for (int a=0;a<DS.numVars;a++){
			System.out.print("\t"+AreaNames[a]);	
		}
		System.out.print("\n");
		
		
		for (int c=0;c<classAllIndices.length;c++) {										// Durch die Klassen
			float count = 0;
			int histoAvg[][] =new int[numVars][101];
			int histoSd[][] =new int[numVars][101];
			
			
			ArrayList<Integer> classMembers = new ArrayList<Integer> ();					// Nur Elemente der aktuellen Klasse
			for (int f=0;f<DS.numSamples;f++){					
				if ( listClassIndex[f] == c) {											// Je Klasse					
					classMembers.add(f);
				}
			}
			int classMemberCount = classMembers.size();
			
			for (int b=0;b<bsBoost;b++) {													// Bootsstapping Cycles
				
				int[] newSamplingMembers =  new int[classMemberCount];						// Re-Sampling
				for (int f=0;f<classMemberCount;f++){
					int pos = (int) (Math.random()*classMemberCount);
					newSamplingMembers[f] = classMembers.get(pos);
				}
				
//				for (int a=0;a<DS.numVars;a++){
//					double sum = 0;double sCount=0;
//					for (int f=0;f<classMemberCount;f++){										// Bootsstapping Average
//						int val = (int) (100*(rawData[newSamplingMembers[f]][a]-aMin[a])/(aMax[a]-aMin[a]));
//						sum+=val;sCount++;
//					}
//					histoAvg[a][(int)(sum/sCount)]++;
//				}
		
				for(int a = 0;a<DS.numVars;a++){
					
					double[] vals = new double[classMemberCount];
					double avg = 0;
					double ce = 0;
					for (int f=0;f<classMemberCount;f++){
						int val = (int) (100*(rawData[newSamplingMembers[f]][a]-aMin[a])/(aMax[a]-aMin[a]));
						vals[f] = 	val;
						avg+=		val;
						ce++;
					}
					double sd = Tools.calculateSD(vals)[1];
					avg /= ce;
					histoAvg[a][(int)avg]++;
					histoSd[a][(int)sd]++;
				}
			}
			
			collect.append(classAllIndNme[c]);
			
			
			double[] colAvg = new double[DS.numVars];
			double[] colSd = new double[DS.numVars];
			
			for(int a = 0;a<DS.numVars;a++){
				int posMaxAvg = 0;	int posValAvg= 0;
				int posMaxSd = 0;	int posValSd= 0;
				for (int i=0;i<101;i++) {
					if ( posValAvg < histoAvg[a][i]) {
						posValAvg = histoAvg[a][i]; 
						posMaxAvg = i;
						
					}
					if ( posValSd < histoSd[a][i]) {
						posValSd = histoSd[a][i]; 
						posMaxSd = i;
					}
				}
				colAvg[a] = posMaxAvg;//((aMax[a]-aMin[a]) * posMaxAvg)/100+aMin[a];;
				colSd[a] = posMaxSd;//((aMax[a]-aMin[a]) * posMaxSd)/100+aMin[a];;;
				System.out.println(a+"\t"+colAvg[a]);
				System.out.println(a+"\t"+colSd[a]);
			}
			// =(C$1/(C$3*(2*PI())^0.5))
			for (int i=0;i<101;i++) {	
				
				// =F2*EXP(-((E2-C$2)^2)/(2*C$3^2))
				for(int a = 0;a<DS.numVars;a++){
					//System.out.print(((aMax[a]-aMin[a]) * i)/100+aMin[a]);
					double normBase = bsSet/ (colSd[a]* Math.sqrt(2*Math.PI));
					double erg = normBase * Math.exp(-Math.pow(i-colAvg[a],2)/(2*Math.pow(colSd[a], 2)));
					//erg = ((aMax[a]-aMin[a]) * erg)/100+aMin[a];
					System.out.print("\t"+erg);
				}
				System.out.print("\n");
			}
//			
//			for (int i=0;i<101;i++) {															// Debug Ausgabe
//				System.out.print("\t"+classAllIndNme[c]  +"\t"+ c+"\t"+i+"\t"+i);
//				for (int a=0;a<DS.numVars;a++){
//				//	histo[a][i] = (bsSet * histo[a][i] ) / bsBoost;	
//					System.out.print("\t"+histoAvg[a][i]);
//					System.out.print("\t"+histoSd[a][i]);
//					for (int p=0;p<histoAvg[a][i];p++) {
//						double val = ((aMax[a]-aMin[a]) * histoAvg[a][i])/100-aMin[a];
//						collect.append("\t"+val);
//					}
//				}
//				System.out.print("\n");
//			}
		}
		
		
		
	}
	public static void getFixedTrainSet() {
		
		
		fixedTrainSet = new boolean[numClasses][DS.numSamples];
		for (int c = 0;c <numClasses;c++) {
	        boolean[] tSet = new boolean[DS.numSamples];
	        float numOfTargets = 0;
	        for (int f=0;f<DS.numSamples;f++){                                // anzahl targets
	            if ( DS.classIndex[f] == classAllIndices[c] ) numOfTargets++;
	        }
	        float targetCount = 0;float foeCount=0;
	        ArrayList<Integer> tgts = new ArrayList<Integer>();
	        ArrayList<Integer> rest = new ArrayList<Integer>();
	        for (int f=0;f<DS.numSamples;f++){
	            if ( DS.classIndex[f] == classAllIndices[c] ) {
	                tgts.add(f);
	                targetCount++;
	            }else {
	                rest.add(f);
	                foeCount++;
	            }
	        }
	        int count = 0;

	        while (count < targetCount*(1.-Opts.trainRatio)) {
	            int rnd = (int) (Math.random()*tgts.size());
	            tgts.remove(rnd);
	            count++;
	        }
	        count = 0;
	        while (count < foeCount*(1.-Opts.trainRatio)) {
	            int rnd = (int) (Math.random()*rest.size());
	            rest.remove(rnd);
	            count++;
	        }
	        for (int f=0;f<DS.numSamples;f++){
	            tSet[f] = false;
	            for (int i=0;i<tgts.size();i++){
	                if (f == tgts.get(i))tSet[f] = true;
	            }
	            for (int i=0;i<rest.size();i++){
	                if (f == rest.get(i))tSet[f] = true;
	            }
	        }
	        fixedTrainSet[c] = tSet;
		}
	     
	}

//	
	public static JSONObject getDSsAsJson() {
		JSONObject out = new JSONObject();
		out.put("variableID", 	variableID);
		out.put("fileName", 	DS.fileName);
		StringBuffer tmp = new StringBuffer();
		for ( int i=0;i<classAllIndices.length; i++) {
			if ( i>0)tmp.append(",");
			tmp.append(classAllIndices[i]);
		}
		out.put("classAllIndices", 	tmp.toString());
		tmp = new StringBuffer();
		for ( int i=0;i<classAllIndPop.length; i++) {
			if ( i>0)tmp.append(",");
			tmp.append(classAllIndPop[i]);
		}
		out.put("classAllIndPop", 	tmp.toString());
		tmp = new StringBuffer();
		for ( int i=0;i<classAllIndNme.length; i++) {
			if ( i>0)tmp.append(",");
			tmp.append(classAllIndNme[i]);
		}
		out.put("classAllIndNme", 	tmp.toString());
	
		if ( normParas == null) {
			out.put("normParas[0]","null");
			out.put("normParas[1]","null");
		}else {
			tmp = new StringBuffer();
			for ( int i=0;i<DS.numVars; i++) {
				if ( i>0)tmp.append(",");
				tmp.append(normParas[0][i]);	
			}
			out.put("normParas[0]", 	tmp.toString());
			 tmp = new StringBuffer();
			for ( int i=0;i<numVars; i++) {
				if ( i>0)tmp.append(",");
				tmp.append(normParas[1][i]);	
			}
			out.put("normParas[1]", 	tmp.toString());
		}
		tmp = new StringBuffer();
		for (int i=0;i<DS.numVars;i++) {
			if ( i>0)tmp.append(",");
			tmp.append(DS.AreaNames[i]);	
		}
		out.put("VariableNames", 		tmp.toString());
		
		tmp = new StringBuffer();
		for (int i=0;i<DS.numSamples;i++) {
			if ( i>0)tmp.append(",");
			tmp.append(classIndex[i]);	
		}
		out.put("ClassIndices", 		tmp.toString());
		
		tmp = new StringBuffer();
		if ( usedAreas != null) {
			for (int i=0;i<DS.usedAreas.length;i++) {
				if ( i>0)tmp.append(",");
				if ( usedAreas[i]) {
					tmp.append("1");
				}else {
					tmp.append("0");
				}
			}
			out.put("UsedAreas", 		tmp.toString());
		}
		
		return out;
	}
	public static boolean saveModel() {
		return true;
	}
	
	public static void setEnsemble ( JSONObject ens) {
		js_Ensemble = ens;
		UI.refreshStatus();
	}
	
}
