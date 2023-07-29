import java.io.File;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Tools {
	public static String getFingerPrint(String txt) {
		
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		md.update(txt.getBytes(StandardCharsets.UTF_8));
		byte[] digest = md.digest();
	    return String.format("%032x", new BigInteger(1, digest));
	 }
	public static boolean isNumeric(String str) {
        return str != null && str.matches("[-+]?\\d*\\.?\\d+");
    }
	public static String txtLen (String txt) {
		while (txt.length()<16) {
			txt = txt + ".";
		}
		while (txt.length()>16) {
			txt = txt.substring(0,txt.length()-1);
		}
		return txt;
	}
	public  static double[] calculateSD(double numArray[])
    {
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.length;

        for(double num : numArray) {
            sum += num;
        }

        double mean = sum/length;

        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        double erg[] = new double[2];
        erg[0] = mean;
        erg[1]  = Math.sqrt(standardDeviation/length);
        
        //return Math.sqrt(standardDeviation/length);
        return erg;
    }
	public  static double[] calculateSD(float numArray[])
    {
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.length;

        for(double num : numArray) {
            sum += num;
        }
        double mean = sum/length;
        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        double erg[] = new double[2];
        erg[0] = mean;
        erg[1]  = Math.sqrt(standardDeviation/length);
        
        return erg;
    }
	static double[][] calculateAurocs() {
		 // Equations: https://en.wikipedia.org/wiki/Mann%E2%80%93Whitney_U_test
		double[][] aurocs = new double[DS.numVars][2];
		for(int a=0;a<DS.numVars;a++){
        	ArrayList<Double> 	sortData 	= new ArrayList<Double>();
       	 	ArrayList<Integer> sortDataCol 	= new ArrayList<Integer>();
        	for(int f=0;f<DS.numSamples;f++){                                             // FILES
            	double data = DS.rawData [f][a];
            	int classIndex = Tools.getIndexOfTarget(DS.classIndex[f]);
            	boolean isIn = false;
                for(int j=0;j<sortData.size();j++){										// SORT
                    if ( sortData.get(j)>data) {
                        sortData.add	(j, data);
                        sortDataCol.add(j, classIndex);
                        isIn = true;
                        break;
                    }
                }
                if ( !isIn) {
                    sortData.add(data);
                    sortDataCol.add(classIndex);
                }
        	}
        	int[] sum 	= new int[DS.numClasses];	// Sum of ranks per ClassIndex
        	int[] count = new int[DS.numClasses];	// Sum of samples per ClassIndex
        	int countAll = sortData.size();
        	for(int i=0;i<sortData.size();i++){
        		sum		[sortDataCol.get(i)] 	+= (i+1);
        		count	[sortDataCol.get(i)] 	++	;
        	}
        	double AUROC =0 ;double U = 0;
        	double AurocMax = 0;int pos = 0;
        	for (int i=0;i<DS.numClasses;i++) {
        		if (count[i]>0) {
        			U 		= sum[i] - (count[i]*(count[i]+1)/2.);
        			AUROC	= U / (count[i]*(countAll-count[i]));
        		
        		}
        		if ( AurocMax < AUROC ) {
        			AurocMax = AUROC;
        			pos = i;
        		}
        	}
        	aurocs[a][0] = AurocMax;
        	aurocs[a][1] = pos;
		}
		return aurocs;
	}
	static double[][] doNormData () {
		DS.normData = new double[DS.numSamples][DS.numVars];
		double[][] erg = null;
		if ( Opts.normType.equals("MaxMinNorm")) {
			erg = new double[2][DS.numVars];
	        double[] minRawData= new double[DS.numVars];
	        double[] maxRawData= new double[DS.numVars];
	        for(int a = 0;a<DS.numVars;a++){
	            for(int f = 0;f<DS.numSamples;f++){
	                if(minRawData[a]>DS.rawData[f][a] || f==0)minRawData[a] = DS.rawData[f][a];
	                if(maxRawData[a]<DS.rawData[f][a] || f==0)maxRawData[a] = DS.rawData[f][a];
	            }
	        }
		    for(int a = 0;a<DS.numVars;a++){
		    	erg[0][a] = minRawData[a];
		    	erg[1][a] = maxRawData[a];
		    	double dif = maxRawData[a]-minRawData[a];
		    	for(int f = 0;f<DS.numSamples;f++){
		        	if(dif>0){
	                    DS.normData[f][a] = 1000*(DS.rawData[f][a]-minRawData[a])/dif-500;
	                }else{
	                	DS.normData[f][a] = 0;
	                }
	            }
	        }
		    return erg; 
		}
		
		if ( Opts.normType.equals("Pareto")) {
			erg = new double[2][DS.numVars];
			double[] ParetoScaleAvg	= new double[DS.numVars];
			double[] ParetoScaleSd	= new double[DS.numVars];
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
				erg[0][a] = ParetoScaleAvg[a];
		    	erg[1][a] = ParetoScaleSd[a];
				for(int f = 0;f<DS.numSamples;f++){
					 DS.normData[f][a] = 1000 * (DS.rawData[f][a]-avg) / sd;
				}
			}
			return erg;
        }
		if ( Opts.normType.equals("None")) {
			for(int a = 0;a<DS.numVars;a++){
	            for(int f = 0;f<DS.numSamples;f++){
	            	 DS.normData[f][a] = DS.rawData[f][a];
	            }
	        }
			return null;
		}
		return erg;
	}
	public static int getIndexOfTarget ( int target) {
		for (int i=0;i<DS.numClasses; i++) {
    		if ( DS.classAllIndices[i] == target) {
    			return i;
    		}
    	}
		return -1;
	}
	public static void sumryAdd ( String txt) {
		if ( DS.txtSummary == null )
			 DS.txtSummary = new StringBuffer();
		 DS.txtSummary.append(txt);
		UI.txtSummary.setText( DS.txtSummary.toString());
	}
	public static double myRound(double wert,int digits){
		double fak = Math.pow(10, digits);
		return Math.round(wert * fak)/fak;
	}
	public static File getFile(String title, String folder, String[] fileType, String[] shortFileType, boolean forSave){
		
		JFileChooser chooser = new JFileChooser();
		if ( shortFileType!= null) {
			
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setFileFilter(new FileNameExtensionFilter(fileType[0],shortFileType[0]));
			for (int i=1;i<shortFileType.length;i++) {
				chooser.addChoosableFileFilter(new FileNameExtensionFilter(fileType[i], shortFileType[i]));
			}
		}
		    chooser.setDialogTitle(title);
		    if(folder!=null) chooser.setCurrentDirectory(new File(folder));
		    int returnVal = -1;
		    if ( ! forSave) {
		    	returnVal = chooser.showOpenDialog(UI.jF);
		    }else {
		    	returnVal = chooser.showSaveDialog(UI.jF);
		    }
		    if(returnVal == JFileChooser.APPROVE_OPTION)
	   			return chooser.getSelectedFile();
		    return null;
	}
	static String[] getvarNamesFromEnsemble ( ) {
		
		JSONObject ensemble = DS.js_Ensemble;
		JSONObject ds = ensemble.getJSONObject("DS");
		String tmp 					= ds.getString("VariableNames");
		return tmp.split(",");
	}
}
