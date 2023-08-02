import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JTable;

public class Classify {
	
	public  Classify() {
		JSONObject ensemble = DS.js_Ensemble;
		if (ensemble== null )return;
		JSONObject ds = ensemble.getJSONObject("DS");
		JSONObject opts = ensemble.getJSONObject("Opts");
		JSONArray models = ensemble.getJSONArray("model");
		
		int targetColorIndex = -1;
		String targetName 		= "";
		double accuracyTest 	= 0;
		double accuracyTrain 	= 0;
		double split 			= -1;
		double maxDst		= 0;
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
		doNormData ();
		// ***
		numVars = DS.normParas[0].length;
		DS.numClasses = DS.classAllIndices.length;
		
		double[][] mc = new double[DS.numVars][Opts.numDims];
		double[] avgs = new double[Opts.numDims];
		double[][] sumUpClassification 		= new double[DS.numSamples][DS.classAllIndices.length];
		double[] fullBonusClassification = new double[DS.classAllIndices.length];
		
		
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
			int trainCount = 0;int targetCount = 0;
			itmp  		= in.getString(		"TrainSet").split(",");
			for (int p=0;p<Opts.numDims; p++) {
				if ( itmp[p].trim().equals("1"))
						trainCount++;
				targetCount++;
			}
			
			double bonus = accuracyTest * accuracyTest; 													/// mmmmhhhh ?????
			if ( trainCount == targetCount)
				bonus = accuracyTrain * accuracyTrain; 														/// Training ohne TestDaten

			
			fullBonusClassification[getTargetColorIndexPos (targetColorIndex)] += 1;						//; + bonus ?
			doClassify (mc, split, avgs, targetColorIndex, sumUpClassification, bonus, maxDst);
		}

		
		
		String[] finalClass = new String[DS.numSamples];
		int[] finalClassIndex = new int[DS.numSamples];
		double[] finalClassBonus = new double[DS.numSamples];
		
		//new UI();
		Object[] header = new Object[DS.numClasses+6];
		header[0] = ("run");
		header[1] = ("sample");
		header[2] = ("classindex");
		header[3] = ("classname");
		header[4] = ("classification");
		header[5] = ("match");
		for (int j=0;j<DS.numClasses;j++) {
			header[6+j] = (DS.classAllIndNme[j]);
		}
		Object[][] row = new Object[DS.numSamples][DS.numClasses+6];
		
		float matchCount = 0;
		float allCount = 0;
		
		
		for (int f=0;f<DS.numSamples;f++){
			
			 double max=-1;
			 ArrayList<Integer> all = new ArrayList<Integer>();
			 
			 row[f][0] = (f+1);
			 row[f][1] = DS.SampleNames[f];
			 row[f][2] = DS.classIndex[f];
			 row[f][3] = DS.ClassNames[f];
			 
			 for (int i=0;i<DS.classAllIndices.length;i++) {
				
				 double val = sumUpClassification[f][i]/fullBonusClassification[i]; 

				 row[f][i+6] = Tools.myRound(val,6);
				 
				 if ( max <  val) {
					 max = val;
					 all.clear();
					 all.add(i);
				 }else {
					 if ( max == val) {
						 all.add(i);
					 }
				 }
			 }
			 
			 if ( max > 0 ) {
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
			 row[f][4] = finalClass[f];
			 
			 if (finalClass[f].equals(DS.ClassNames[f])) {						// contains
				 matchCount++;
				 row[f][5] = "++++++";
			 }else {
				 row[f][5] = "------";
			 }
			 allCount++;
		 }

		UI.tmtable.setDataVector(row, header);
		UI.labAccuracy.setText("Accuracy: " + Tools.myRound(100* (matchCount/allCount),1) + "%");
	
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
		if ( maxDst == 0)return ;
		for (int f=0;f<DS.numSamples;f++){
			distances[f] /= maxDst;
		}
	  
	    // doCLassify
	    int[] classification = new int [DS.numSamples];
        for (int f=0;f<DS.numSamples;f++){
            if ( distances[f] < split/100) {
                classification[f] = target;
                sumUpClass[f][indexPos] += bonus;
            }else {
                classification[f] = -1;
            }
        }
       
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