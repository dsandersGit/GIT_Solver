import java.util.ArrayList;

import javax.swing.ImageIcon;

public class DS {
	
	public static ArrayList<MC_Freeze> freezs = new ArrayList<MC_Freeze>();
	
	public static double 		rawData [][] 	= null; 				// RawData : Sample / Variable
	public static String[]		SampleNames 	= null;
	public static String[]		AreaNames 		= null;
	public static String[]		ClassNames 		= null;
	public static int[]			classIndex 		= null;					// Color per sample
	public static double 		normData [][] 	= null;
	
	public static int[]			classAllIndices	= null;					// [] Index of present Classes
	public static int[]			classAllIndPop	= null;					// [] Population of each Classes, index like classIndices
	public static String[]		classAllIndNme	= null;					// [] Population of each Classes, index like classIndices
	public static boolean[][]	fixedTrainSet	= null;					//
	//public static boolean[]		noTrainingSet	= null;					//
	
	public static double[][] 	normParas		= null;
	public static String 	    fileName		= null;
	
	public static int			numVars			= 0;
	public static int			numSamples		= 0;
	public static int			numClasses		= 0;
	
	public static JSONObject	js_Ensemble		= null;
	
	//public static String 	    TP_FP_TN_FN		= null;
	
	public static StringBuffer 	txtSummary		= null;
	
	
	public DS () {
		 UI.labStatusIcon.setIcon(new ImageIcon(ClassLoader.getSystemResource("colBlue.png")));
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
		for ( int i=0;i<clasIndex.size();i++) {
			classAllIndices[i] 	= clasIndex.get(i);
			classAllIndPop[i] 	= clasIndexCount.get(i);
			classAllIndNme[i]	= clasIndexName.get(i);
		}
		
		if ( Opts.fixTrainSet ) {
			getFixedTrainSet();
		}
		
		// ***
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
	                rest.add(f);// TODO: Training Daten nicht nur aus Targets, sondern auch Foes
	                foeCount++;
	            }
	        }
	        int count = 0;

	        while (count < targetCount*(1.-Opts.trainRatio)) {// TODO: Training Daten nicht nur aus Targets, sondern auch Foes
	            int rnd = (int) (Math.random()*tgts.size());
	            tgts.remove(rnd);
	            count++;
	        }
	        count = 0;
	        while (count < foeCount*(1.-Opts.trainRatio)) {// TODO: Training Daten nicht nur aus Targets, sondern auch Foes
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
	public static String getDSsAsString() {
		StringBuffer out = new StringBuffer();
			out.append("classAllIndices,classAllIndPop,classAllIndNme" + "\n");
			for (int i=0;i<classAllIndices.length;i++) {
				out.append(classAllIndices[i] + "," + classAllIndPop[i] + "," +"\""+classAllIndNme[i]+"\"" + "\n" );
			}
			
			if ( normParas == null) {
				out.append("normParas[0],null" + "\n" );
				out.append("normParas[1],null" + "\n" );
			}else {
				out.append(">normParas[0]" );
				for ( int i=0;i<DS.numVars; i++) {
					out.append(", " + normParas[0][i]);	
				}
				out.append( "\n" );
				out.append(">normParas[1]" );
				for ( int i=0;i<numVars; i++) {
					out.append(", " + normParas[1][i]);	
				}
			}
			out.append( "\n" );
		return out.toString();
	}
	public static JSONObject getDSsAsJson() {
		JSONObject out = new JSONObject();
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
