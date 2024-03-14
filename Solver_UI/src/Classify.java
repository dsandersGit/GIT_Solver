import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.SwingUtilities;


/*
 * data-model-application 
 *  + data classification based on trained/loaded model 
 *  
 *  Copyright(c) 2009-2024, Daniel Sanders, All rights reserved.
 *  https://github.com/dsandersGit/GIT_Solver
 */

public class Classify {
	
	public static double accuracyTest = 0;
	public static double accuracyTrain = 0;
	public static StringBuffer confMatrixout = new StringBuffer();
	public static int[][] confusionMatrixTrain = new int[DS.numClasses][DS.numClasses];
	public static int[][] confusionMatrixTest = new int[DS.numClasses][DS.numClasses];
	
	public  Classify() {

		JSONObject ensemble = DS.js_Ensemble;
		if (ensemble== null )return;
		if ( !ensemble.has("model")) return;
		JSONObject ds 		= ensemble.getJSONObject("DS");
		JSONObject opts 	= ensemble.getJSONObject("Opts");
		JSONArray models 	= ensemble.getJSONArray("model");
		

		int targetColorIndex 	= -1;
		String targetName 		= "";
		accuracyTest 	= 0;
		accuracyTrain 	= 0;
		double split 			= -1;
		double maxDst			= 0;
		int numVars				= -1;
		int numClasses			= -1;
		Opts.numDims 			= opts.getInt(		"numDims");
		Opts.dstType 			= opts.getString(	"dstType");
		Opts.normType 			= opts.getString(	"normType");
		
		String np0 				= ds.getString(		"normParas[0]");
		String np1 				= ds.getString(		"normParas[1]");
		String[] line 			= np0.split(","); 
		DS.normParas 			= new double[2][line.length];
		
		for (int i=0; i<line.length;i++) {
			DS.normParas[0][i] = Double.parseDouble(line[i].trim());
		}
		line = np1.split(","); 
		for (int i=0; i<line.length;i++) {
			DS.normParas[1][i] = Double.parseDouble(line[i].trim());
		}
		String tmp 				= ds.getString("classAllIndices");
		line = tmp.split(","); 
		DS.classAllIndices = new int[line.length];
		for (int i=0; i<line.length;i++) {
			DS.classAllIndices[i] = Integer.parseInt(line[i].trim());
		}
		tmp 					= ds.getString("classAllIndNme");
		line = tmp.split(","); 
		DS.classAllIndNme = new String[line.length];
		for (int i=0; i<line.length;i++) {
			DS.classAllIndNme[i] = line[i].trim();
		}
		tmp 					= ds.getString("classAllIndPop");
		line = tmp.split(","); 
		DS.classAllIndPop = new int[line.length];
		for (int i=0; i<line.length;i++) {
			DS.classAllIndPop[i] = Integer.parseInt(line[i].trim());
		}
	
		// ***
		numVars = DS.normParas[0].length;
		DS.numClasses = DS.classAllIndices.length;
		
		// ***
		// 87
		doNormData ();
		//
		
//		UI.tmtableClassify.setRowCount(0);
//		UI.tmtableClassify.setColumnCount(0);
		
		Object[] tHeader = new Object[DS.numClasses+7];
		tHeader[0]	= "# num";
		tHeader[1]	= "sample";
		tHeader[2]	= "classindex";
		tHeader[3]	= "classname";
		tHeader[4]	= "classification";
		tHeader[5]	= "match";
		tHeader[6]	= "train/validation[TARGET]";
		for (int j=0;j<DS.numClasses;j++) {
			tHeader[7+j] = DS.classAllIndNme[j];
		}
		
//		UI.tmtableClassify.addColumn("run");
//		UI.tmtableClassify.addColumn("sample");
//		UI.tmtableClassify.addColumn("classindex");
//		UI.tmtableClassify.addColumn("classname");
//		UI.tmtableClassify.addColumn("classification");
//		UI.tmtableClassify.addColumn("match");
//		UI.tmtableClassify.addColumn("train/validation");
//		for (int j=0;j<DS.numClasses;j++) {
//			UI.tmtableClassify.addColumn(DS.classAllIndNme[j]);
//		}
		
		double[][] mc 						= new double[DS.numVars][Opts.numDims];
		double[] avgs 						= new double[Opts.numDims];
		double[][] sumUpClassification 		= new double[DS.numSamples][DS.classAllIndices.length];
		double[] fullBonusClassification 	= new double[DS.classAllIndices.length];
		
		
		for (int i=0;i<models.length(); i++) {
			JSONObject in = models.getJSONObject(i);
			accuracyTest 		= in.getDouble(		"accuracyTest");
			accuracyTrain 		= in.getDouble(		"accuracyTrain");
			targetColorIndex	= in.getInt(		"targetColorIndex");
			targetName			= in.getString(		"targetLabel");
			split				= in.getDouble(		"split");
			maxDst				= in.getDouble(		"maxDistance");
			
			for (int p=0;p<Opts.numDims; p++) {
				String vTemp = in.getString(		"Vector"+p);
				line = vTemp.split(","); 
				for (int a=0; a<line.length;a++) {
					mc[a][p] = Double.parseDouble(line[a].trim());
				}		
			}
			String[] itmp  		= in.getString(		"Averages").split(",");
			for (int p=0;p<Opts.numDims; p++) {
				avgs[p] = Double.parseDouble(itmp[p].trim()); 
			}
			int trainCount = 0;int testCount = 0;
			itmp  		= in.getString(		"TrainSet").split(",");
			for (int f=0;f<itmp.length; f++) {
				if ( itmp[f].trim().equals("1")) {
						trainCount++;
				
				}
				testCount++;
			}
			
			
			double bonus = accuracyTest * accuracyTest; 														
			if ( trainCount == testCount)
				bonus = accuracyTrain * accuracyTrain; 														/// Training ohne TestDaten
			fullBonusClassification[getTargetColorIndexPos (targetColorIndex)] += 1;						//; + bonus ?
			doClassify (mc, split, avgs, targetColorIndex, sumUpClassification, bonus, maxDst );
		}

		
		
		String[] finalClass = new String[DS.numSamples];
		int[] finalClassIndex = new int[DS.numSamples];
		double[] finalClassBonus = new double[DS.numSamples];
		confusionMatrixTrain = new int[DS.numClasses][DS.numClasses];
		confusionMatrixTest = new int[DS.numClasses][DS.numClasses];
		
		
		// Test Train/
		int add = 7; // sonst 6
		
//		UI.tmtableClassify.setRowCount(0);
		
		float matchCountTest		= 0;
		float matchCountTrain		= 0;
		float allCountTest			= 0;
		float allCountTrain			= 0;
		
//		matchCountTarget 	= new int[DS.numClasses];
//		allCountTarget 		= new int[DS.numClasses];
		
		Object[][] nData = new Object [DS.numSamples] [DS.numClasses+add];
		for (int f=0;f<DS.numSamples;f++){
			double max=-5;
			 ArrayList<Integer> all = new ArrayList<Integer>();
			 int index = Tools.getIndexOfTarget (DS.classIndex[f]);
		 
			 nData[f][0] = Tools.txtLen(""+(f+1));
			 nData[f][1] = DS.SampleNames[f];
			 nData[f][2] = DS.classIndex[f];
			 nData[f][3] = DS.ClassNames[f];
			 
			 int maxClassNum = -1;
			 for (int i=0;i<DS.classAllIndices.length;i++) {
				 double val = sumUpClassification[f][i]/fullBonusClassification[i]; 
				 nData[f][i+add] = Tools.myRound(val,6);
				 if ( max <  val || i==0) {
					 max = val;
					 maxClassNum = i;
					 all.clear();
					 all.add(i);
				 }else {
					 if ( max == val) {
						 all.add(i);
					 }
				 }
			 }
			 
			 if ( max > -5 ) {
				 finalClass[f] = "";
				 finalClassIndex[f] = -1;
				 for (int i=0;i<all.size();i++) {
					 if ( i>0) finalClass[f] += "|";
					 finalClass[f] += 	 DS.classAllIndNme[all.get(i)];
					 finalClassIndex[f] = DS.classAllIndices[all.get(i)];
					 if ( finalClass[f].contains("|")) finalClassIndex[f] = -1;					// Doppeltes Match
				 }
			 }else {
				 finalClass[f] = "-none-";
				 finalClassBonus[f] = 0;
				 finalClassIndex[f] = -1;
			 }
			 
			 if ( maxClassNum > -1) 
				 if ( DS.fixedTrainSet != null)
					 if (!DS.fixedTrainSet[f]) {
						 confusionMatrixTest [Tools.getIndexOfTarget(DS.classIndex[f])][maxClassNum] ++;
					 }else {
						 confusionMatrixTrain [Tools.getIndexOfTarget(DS.classIndex[f])][maxClassNum] ++;
					 }
			 
			 nData[f][4] = finalClass[f];
			 if (finalClass[f].equals(DS.ClassNames[f])) {						// contains
				 if ( (Opts.fixTrainSet && DS.fixedTrainSet != null)) {
					 if (DS.fixedTrainSet[f]) {
						 matchCountTrain++;
					 }else {
						 matchCountTest++;
					 }
				 }else {
					 matchCountTrain++;
				 }
//				 matchCountTarget[Tools.getIndexOfTarget(DS.classIndex[f])]++;
				 nData[f][5] = "++++++";
			 }else {
				 nData[f][5] = "------";
			 }
			 if ( Opts.fixTrainSet && DS.fixedTrainSet != null) {
				 if (DS.fixedTrainSet[f]) {
					 nData[f][6] = "training";
				 }else {
					 nData[f][6] = "validation";
				 }
			 }else {
				 nData[f][6] = " - - - ";
			 }
			 if ( (Opts.fixTrainSet && DS.fixedTrainSet != null)) {
				 if (DS.fixedTrainSet[f]) {
					 allCountTrain++;
				 }else {
					 allCountTest++;
				 }
			 }else {
				 allCountTrain++;
			 }
		}
		UI.scClassify.setViewportView(UI.tableClassify);
		UI.tmtableClassify.setDataVector(nData, tHeader);
	
		int[] trainRatio = new int[DS.numClasses];
		int[] testRatio = new int[DS.numClasses];
		
		// 101
		for (int i=0;i<DS.numClasses;i++) {
			float isInTrain = 0;
			float isOutTrain = 0;
			float isInTest = 0;
			float isOutTest = 0;
			for (int j=0;j<DS.numClasses;j++) {
				if ( i==j) {
					isInTrain += confusionMatrixTrain [i][j];
					isInTest += confusionMatrixTest [i][j];
				}else {
					isOutTrain += confusionMatrixTrain [i][j];
					isOutTest += confusionMatrixTest [i][j];
				}
			}
			
			trainRatio[i] = Math.round(100*isInTrain/(isInTrain+isOutTrain));
			testRatio[i] = Math.round(100*isInTest/(isInTest+isOutTest));
		}
	
		confMatrixout = new StringBuffer();
		StringBuffer txtOut = new StringBuffer();
		if ( DS.fixedTrainSet != null) {
			txtOut.append("ConfusionMatrix: [true\\predict]\n");
			txtOut.append(Tools.txtLen ("Training:")+"\t");
			confMatrixout.append("Trn."+"\n");
			for (int i=0;i<DS.numClasses;i++) {
				txtOut.append(Tools.txtLen (DS.classAllIndNme[i])+"\t");
			}
			txtOut.append(Tools.txtLen ("RATIO [%]") + "\n");
			for (int i=0;i<DS.numClasses;i++) {
				txtOut.append(Tools.txtLen (DS.classAllIndNme[i])+"\t");
				confMatrixout.append(DS.classAllIndNme[i]+"|");
				for (int j=0;j<DS.numClasses;j++) {
					txtOut.append(Tools.txtLen ("" + confusionMatrixTrain [i][j])+"\t");
					confMatrixout.append("" + confusionMatrixTrain [i][j]+"|");
				}
				txtOut.append(Tools.txtLen ("" + trainRatio [i])+"\n");
				confMatrixout.append("\n");
			}
			txtOut.append(""+"\n");
			confMatrixout.append(""+"\n");
			
			txtOut.append(Tools.txtLen ("Validation:")+"\t");
			confMatrixout.append("Vald."+"\n");
			for (int i=0;i<DS.numClasses;i++) {
				txtOut.append(Tools.txtLen (DS.classAllIndNme[i])+"\t");
			}
			txtOut.append(Tools.txtLen ("RATIO [%]") + "\n");
			for (int i=0;i<DS.numClasses;i++) {
				txtOut.append(Tools.txtLen (DS.classAllIndNme[i])+"\t");
				confMatrixout.append(DS.classAllIndNme[i]+"|");
				for (int j=0;j<DS.numClasses;j++) {
					txtOut.append(Tools.txtLen ("" + confusionMatrixTest [i][j])+"\t");
					confMatrixout.append("" + confusionMatrixTest [i][j]+"|");
				}
				txtOut.append(Tools.txtLen ("" + testRatio [i])+"\n");
				confMatrixout.append("\n");
			}
			txtOut.append(""+"\n");
			confMatrixout.append(""+"\n");
		}
		
		UI.txtClassify.setText(txtOut.toString());
		accuracyTrain = Tools.myRound(100* (matchCountTrain/allCountTrain),1);
		accuracyTest = Tools.myRound(100* (matchCountTest/allCountTest),1);
		if (  allCountTest == 0) {
			accuracyTrain = Tools.myRound(100* (matchCountTrain/allCountTrain),1);
			accuracyTest = accuracyTrain;
		}
		UI.labAccuracy.setText("Accuracy [Train/Validation %]: " + Tools.myRound(100* (matchCountTrain/allCountTrain),1) + " / " + Tools.myRound(100* (matchCountTest/allCountTest),1));	

		
	
	}
	public static String[] getLatestConfusionMatrix() {
		
		return null;
	}
	public static int getTargetColorIndexPos (int target) {
		for (int i=0;i<DS.classAllIndices.length;i++) {
			if ( target == DS.classAllIndices[i]) return i;
		}
		return -1;
	}
	private static void doClassify (double[][] mc, double split, double[] avgs, int target, double[][] sumUpClass, double bonus, double maxDst) {
		int indexPos = getTargetColorIndexPos (target); 
		double[][] mcPCA = new double[Opts.numDims][DS.numSamples];
		// calcPlot
	    for (int f=0;f<DS.numSamples;f++){
            double[] sum = new double[Opts.numDims];
            for (int i=0;i<Opts.numDims;i++) {
                for (int a=0;a<DS.numVars;a++){
                	sum[i] += DS.normData[f][a] * mc[a][i];
                }
                mcPCA[i][f] = sum[i];
            }
        }
	    // getDistances
	    double[] distances = new double[DS.numSamples];
		for (int f=0;f<DS.numSamples;f++){
	            double val = 0;
	            for (int i=0;i<Opts.numDims;i++) {
	                val += (Math.pow(mcPCA[i][f]-avgs[i], 2) );                
	            }
	        distances[f]=Math.pow(val, .5);
	      //  if ( distances[f]>maxDist ) maxDist = distances[f];					// CRUCIAL 
	        
	    }
		
		// 88
//		if ( maxDst == 0)return ;
//		for (int f=0;f<DS.numSamples;f++){
//			distances[f] /= maxDst;
//		}
		if ( maxDst != 0) {
		for (int f=0;f<DS.numSamples;f++){
			distances[f] /= maxDst;
		}
		}else {
			System.out.println("WARNING: maxDst = 0");
			for (int f=0;f<DS.numSamples;f++){
				distances[f] = 0;
			}
		}
		
		
		// 100: Massive Change
		boolean tryTest =  Opts.dstType.contentEquals("EGO") ;
		int[] classification = new int [DS.numSamples];
		if ( tryTest) {											// Development
			double mid = split/100.;
			for (int f=0;f<DS.numSamples;f++){
	            if ( distances[f] < mid) {
	                classification[f] = target;
	                // 104
	                sumUpClass[f][indexPos] += bonus * (mid-distances[f])/mid ;
	            }else {
	                classification[f] = -1;
	                sumUpClass[f][indexPos] -= bonus* ((distances[f]-mid)/(1.-mid)) ;
	            }
	        }
		}else {
			for (int f=0;f<DS.numSamples;f++){
	            if ( distances[f] < split/100) {
	                classification[f] = target;
	                sumUpClass[f][indexPos] += bonus ;
	            }else {
	                classification[f] = -1;
	                sumUpClass[f][indexPos] -= bonus;
	            }
	        }	
		}
	    // doCLassify
	}
	
	
	private static void doNormData () {

		if ( Opts.normType.equals("MaxMinNorm")) {
			
		    for(int a = 0;a<DS.numVars;a++){
		    	double dif = DS.normParas[1][a]-DS.normParas[0][a];
		    	for(int f = 0;f<DS.numSamples;f++){
		        	if(dif>0){
	                    DS.normData[f][a] = 1000*(DS.rawData[f][a]-DS.normParas[0][a])/dif-500;
	                }else{
	                	DS.normData[f][a] = 0;
	                }
	            }
	        }
		    return;
		}
		
		if ( Opts.normType.equals("Pareto")) {
			for(int a = 0;a<DS.numVars;a++){
				double sd = DS.normParas[1][a];
				double avg = DS.normParas[0][a];
				for(int f = 0;f<DS.numSamples;f++){
					 DS.normData[f][a] = 1000 * (DS.rawData[f][a]-avg) / sd;
				}
			}
			return;
        }
		if ( Opts.normType.equals("None")) {
			for(int a = 0;a<DS.numVars;a++){
	            for(int f = 0;f<DS.numSamples;f++){
	            	 DS.normData[f][a] = DS.rawData[f][a];
	            }
	        }
			return;
		}
		return;
	}
	public static JSONObject readEnsemble(String fileName) {
		File file = new File(fileName);
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
        
        JSONObject main = new JSONObject(contents.toString());
        
        String[] trunObj = {"DS", "Opts"};
        boolean valid = true;
        for (int i=0;i<trunObj.length; i++) {
        	 if( ! main.has(trunObj[i])) valid=false;;
        }
        String[] trunArr = {"FingerPrints", "model"};
        for (int i=0;i<trunArr.length; i++) {
        	if( ! main.has(trunArr[i])) valid=false;;
        }
       
       if (!valid) return null; 
        return main;
	}
	public static void setOptions() {
		JSONObject ensemble = DS.js_Ensemble;
		JSONObject opts = ensemble.getJSONObject("Opts");
		UI.txtOpts.setText(opts.toString(3));
	}
	public static boolean checkEnsembleFingerPrints() {
		boolean isCorrupt = false;
		JSONObject ensemble = DS.js_Ensemble;
		if (ensemble== null )return false;
		JSONObject opts = ensemble.getJSONObject("Opts");
		JSONArray fps = ensemble.getJSONArray("FingerPrints");
		JSONArray models = ensemble.getJSONArray("model");
		for (int i=0;i<models.length(); i++) {
			JSONObject in = models.getJSONObject(i);
			if ( !Tools.getFingerPrint(in.toString()+opts.toString()).equals(fps.get(i))) {
				isCorrupt = true;
			}
		}	
		return isCorrupt;
	}
}
