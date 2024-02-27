import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
/*
 * tools
 *  + divers tool 
 *  
 *  Copyright(c) 2009-2023, Daniel Sanders, All rights reserved.
 *  https://github.com/dsandersGit/GIT_Solver
 */
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
        //return str != null && str.matches("[-+]?\\d*\\.?\\d+");
        try {  
            Double.parseDouble(str);  
            return true;
          } catch(NumberFormatException e){  
            return false;  
          }  
    }
	
	public static String txtLen (String txt) {
		if (Tools.isNumeric(txt)) {
			while (txt.length()<16) {
				txt = "." + txt ;
			}
		}else {
			while (txt.length()<16) {
				txt = txt + ".";
			}
			while (txt.length()>16) {
				txt = txt.substring(0,txt.length()-1);
			}	
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
	static double[] calculateAuroc(int area) {
		 // Equations: https://en.wikipedia.org/wiki/Mann%E2%80%93Whitney_U_test
		
		
       	ArrayList<Double> 	sortData 	= new ArrayList<Double>();
      	 	ArrayList<Integer> sortDataCol 	= new ArrayList<Integer>();
       	for(int f=0;f<DS.numSamples;f++){                                             // FILES
           	double data = DS.rawData [f][area];
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
//      
       
       	int[] sum 	= new int[DS.numClasses];	// Sum of ranks per ClassIndex
       	int[] count = new int[DS.numClasses];	// Sum of samples per ClassIndex
       	int countAll = sortData.size();
       	for(int i=0;i<sortData.size();i++){
       		sum		[sortDataCol.get(i)] 	+= (i+1);
       		count	[sortDataCol.get(i)] 	++	;
       	}
       	double AUROC =0 ;double U = 0;
       	double AurocMax = 0;int pos = 0;
       	double[] AUROC_List = new double[DS.numClasses];
       	for (int i=0;i<DS.numClasses;i++) {
       		if (count[i]>0) {
       			U 		= sum[i] - (count[i]*(count[i]+1)/2.);
       			AUROC	= U / (count[i]*(countAll-count[i]));
       			if ( AUROC < 0.5 )AUROC = 1-AUROC;
       			AUROC_List[i] = AUROC;
//       			System.out.println(i+"\t"+AUROC);
       		}
       		if ( AurocMax < AUROC ) {
       			AurocMax = AUROC;
       			pos = i;
       		}
       	}

		
		return AUROC_List;
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
        			if ( AUROC < 0.5 )AUROC = 1-AUROC;
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
		// 85 System.out.println(Opts.normType);

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
	            //85
	           //if ( maxRawData[a] == minRawData[a] ) maxRawData[a] =+ 1; 
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
		    	// 88
				for(int f = 0;f<DS.numSamples;f++){
					if ( sd == 0) {
						DS.normData[f][a] = 0;
					}else {
						DS.normData[f][a] = 1000 * (DS.rawData[f][a]-avg) / sd;	
					}
					 
					 
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
	
			return new double[2][DS.numVars];
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
	public static Color getClassColor ( int index) {
		if ( index < DS.classCols.length) return DS.classCols[index];
		return Color.black;
	}
	public static String getClassNameOfIndex ( int index) {
		return DS.classAllIndNme[index];
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
	public static BufferedImage getLegendImage(int index, String name) {
		BufferedImage legendImage = new BufferedImage(1, 1,BufferedImage.TYPE_INT_RGB );
		Font font24 = legendImage.getGraphics().getFont().deriveFont(16.0f);
		FontMetrics fm = legendImage.getGraphics().getFontMetrics(font24);
		
		int width = fm.stringWidth(name);
		legendImage = new BufferedImage(13+width,15,BufferedImage.TYPE_INT_RGB );
		Graphics g = legendImage.getGraphics();
		g.setFont(font24);
		g.setColor(SolverStart.backColor);
		g.fillRect(0,0,legendImage.getWidth(),legendImage.getHeight());
		g.setColor(SolverStart.frontColor);
		
		g.drawString(name,13,13);
		g.setColor(getClassColor(index));
		g.fillRect(2, 2, 10,10);
		g.setColor(Color.gray);
		g.drawLine(1, 2, 1, 12);
		g.drawLine(1, 12, 11, 12);
		g.dispose();
		return legendImage;
	}
	public static File getFile(String title, String folder, String[] fileType, String[] shortFileType, boolean forSave){
		
		JFileChooser chooser = new JFileChooser();
		if ( shortFileType!= null) {
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setFileFilter(new FileNameExtensionFilter(fileType[0],shortFileType));
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
	// 80
//	static String[] getvarNamesFromEnsemble ( ) {
//		
//		JSONObject ensemble = DS.js_Ensemble;
//		JSONObject ds = ensemble.getJSONObject("DS");
//		String tmp 					= ds.getString("VariableNames");
//		return tmp.split(",");
//	}
	static String[] getvarNamesFromEnsemble ( ) {
        if ( DS.js_Ensemble == null ) return null;
        if ( !DS.js_Ensemble.has("DS") ) return null;
        JSONObject ensemble = DS.js_Ensemble;
        JSONObject ds = ensemble.getJSONObject("DS");
        String tmp                     = ds.getString("VariableNames");
       
        String[] elms = tmp.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        return elms;
    }
	public static void compileModels() {
		JSONObject main = DS.js_Ensemble;
		main.remove("model");
		main.remove("FingerPrints");
		for (int j=0;j<DS.freezs.size();j++) {
			JSONObject modl = DS.freezs.get(j).getModelAsJson();
			main.append("model", modl);
			main.append("FingerPrints", Tools.getFingerPrint(modl.toString()+Opts.getOptsAsJson().toString()));
		}
		DS.setEnsemble(main);
		if ( DS.js_Ensemble != null ) {
			Classify.setOptions();
			UI.txtEnsemble.setText(DS.js_Ensemble.toString(3));
		}
	}
	static double getSpearman_Rank_Correlation(double[] x, double[] y){
		//https://en.wikipedia.org/wiki/Spearman's_rank_correlation_coefficient
		        float [] sortX = new float[  x.length];
		        float [] sortY = new float[  y.length];
		        double p = 0;double num = 0;
		        for(int i=0;i<x.length;i++){
		            double val = 0;
		            float c = 0;
		                try { val = x[i];}catch (java.lang.ArrayIndexOutOfBoundsException e) {}
		                float numSame = 0f;
		                for(int j=0;j<y.length;j++){
	                        double inVal = 0;
	                        try { inVal = x[j];}catch (java.lang.ArrayIndexOutOfBoundsException e) {}
	                        if ( val==inVal )numSame++;
	                        if ( val<inVal )c++;
		                }
		                if (numSame>1 ) {
		                    for (float k=0;k<numSame; k++) {
		                        sortX[i] += c + k;
		                    }
		                    sortX[i] /= numSame;
		                }else {
		                    sortX[i] = c;
		                }


		                c=0;val=0;
		                try { val = y[i];}catch (java.lang.ArrayIndexOutOfBoundsException e) {}
		                numSame = 0f;
		                for(int j=0;j<y.length;j++){
	                        double inVal = 0;
	                        try { inVal = y[j];}catch (java.lang.ArrayIndexOutOfBoundsException e) {}
	                        if ( val==inVal )numSame++;
	                        if ( val<inVal )c++;
		                }
		                if (numSame>1 ) {
		                    for (float k=0;k<numSame; k++) {
		                        sortY[i] += c + k;
		                    }
		                    sortY[i] /= numSame;
		                }else {
		                    sortY[i] = c;
		                }
		                num++;
		            p += Math.pow(sortX[i]-sortY[i],2);
		        }
		        return 1.-6.*p/(num*(Math.pow(num,2)-1));
		    }
	public static void txtHelp(String selectedText) {
		String[] items = {"dstType","normType","activation"};
		String[] help = {"dstType",
						"normType",
						"<B>DxA</B> Use both distance + accuracy as discriminant function<BR>"
						+ "<B>D+A</B> use alternating both<BR>"
						+ "<B>A use only accuracy<BR>"};
		int pos = -1;
		for (int i=0;i<items.length; i++) {
			if ( selectedText.equals(items[i]))
				pos = i;
		}
		if ( pos < 0 )return;
		JOptionPane.showMessageDialog(UI.jF, "<HTML>"+help[pos]+"</HTML>");

		
	} 
}
class ImageTransferable implements Transferable, ClipboardOwner
{
  Image image;
  ImageTransferable(Image img)
  {
    this.image = img;
  }
  public DataFlavor[] getTransferDataFlavors()
  {
    return new DataFlavor[]{DataFlavor.imageFlavor};
  }
  public boolean isDataFlavorSupported(DataFlavor flavor)
  {
    return DataFlavor.imageFlavor.equals(flavor);
  }
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
  {
    return image;
  }
  public void lostOwnership(Clipboard clipboard, Transferable contents) {}
}