import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.prefs.Preferences;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MultiVariate_R {
	private static String r_Path = "";
	private static String r_Script = "";
	public static String r_Data = "";
	
	//private static Color[]ClassCols = null;
	
	public static double[][]eigenVectors = null; // [PC] / [Area]
	public static double[]eigenValues = null;
	public static double[]eigenValuesPercental = null;
	public static double[]means = null;
	public static double[]variances = null;
	public static double[]PC_centered = null;
	public static double[]PC_OLD_centered = null;
	
	public static int pca_depth = 3;
	
	public static double[] loadings = null;
	
	public static double[][]PCAs = null;
	public static double[][]LDAs = null;
	public static int LDA_numPCA = 6;
	public static double[][]LDAscales = null;
	public static double[]LDAsvariances = null;

	public static double maxPCA1 = 0;
	public static double maxPCA2 = 0;
	public static double minPCA1 = 0;
	public static double minPCA2 = 0;
	
	public static double minPCA4 = 0;
	public static double minPCA3 = 0;
	public static double maxPCA3 = 0;
	public static double maxPCA4 = 0;
	
	public static double maxAllPCA = 0;
	public static double minAllPCA = 0;
	
	public static double[][] data = null;
	
	private static java.lang.Process p 					= null;
	public static Thread threadIn = null;
	public static Color[] ClassCols = null;
	
	public MultiVariate_R(double[][] rawData,String[] sampleNames,String[] areaNames,String[] cNames, Color[] cols) {
		
		Preferences prefs = null;
		prefs = Preferences.userRoot().node("Solver");
	
		r_Path = prefs.get("R_Path", "");
		r_Script = prefs.get("R_Script", "");
		r_Data =  prefs.get("R_Data", "");
//		
		if ( r_Path.length()<1 || r_Script.length()<1 ||  r_Data.length()<1) setPaths();
		if ( r_Path.length()<1 || r_Script.length()<1 ||  r_Data.length()<1) return;
		if(rawData==null || rawData.length<1)return;
		data = rawData;
		ClassCols = cols;
		int fNum = rawData.length;
		int aNum = rawData[0].length;
		
		// HACK TO R
				// Schreibe Daten in Table
	
				StringBuffer out = new StringBuffer();
				// HEADER
				out.append("\"sample\"");
				out.append("," + "\"class\"");
				for(int a = 0;a<aNum;a++){
						out.append(",\"" + areaNames[a]+"\"");	
				}
				out.append("\n");
				// DATA
				for(int f = 0;f<fNum;f++){
					out.append("\"" + sampleNames[f] +"\",");	
					out.append("\"" + cNames[f] +"\"");	
					for(int a = 0;a<aNum;a++){
							out.append("," + data[f][a]);
					}
					out.append("\n");
				}
				try {
					FileWriter fr = null;
					BufferedWriter br = null;
			
					 fr = new FileWriter(new File(r_Data));
				 	 br = new BufferedWriter(fr);
				 	 br.write(out.toString());
			
				 	br.close();
				 	fr.close();
				} 
				catch ( IOException e ) { 
				  e.printStackTrace();
				  JOptionPane.showMessageDialog(null, "Could not write " + r_Data, "R_Data out", JOptionPane.ERROR_MESSAGE);
				} 
				
			 	
			 	// > R
			 	ProcessBuilder pb = null;
			 	pb = new ProcessBuilder(r_Path, r_Script);
			
				
				if (pb == null)return;
				pb.redirectErrorStream(false);
				try {
					  p = pb.start();
					  
					  	LogErrorStreamReader lesr = new LogErrorStreamReader(p.getErrorStream());
					  	Thread threadErr = new Thread(lesr, "LogErrorStreamReader");
					  	 	threadErr.start();
			            LogStreamReader lsr = new LogStreamReader(p.getInputStream());
			            threadIn = new Thread(lsr, "LogStreamReader");
			            	
			            	threadIn.start();
				} catch (IOException e1) {
		
					e1.printStackTrace();
			
				}
			
	}
	
	
public static void setPaths(){
		
		  JPanel tmp = new JPanel();
		  tmp.setLayout(new BoxLayout(tmp,BoxLayout.Y_AXIS));
		  tmp.add(new JLabel("<HTML><H2>PCA is calculated via 'The R Project for Statistical Computing'</H2>"));
		  tmp.add(new JLabel("<HTML><H3>download: https://www.r-project.org/</H3>"));
		  tmp.add(new JLabel("Define Paths to External Files"));
		  tmp.add(new JLabel("Path to the RScript.exe (installed R/typically C:\\Program Files\\R\\R-N.N.N\\bin\\RScript.exe )"));
		  JScrollPane sc = new JScrollPane(tmp);
		  
		  
		  String info = r_Path; if ( info.length()<1 ) info  = "not set";
		  JButton but_R_Path = new JButton(info);
		  but_R_Path.addActionListener(new ActionListener(){
			   public void actionPerformed(ActionEvent e) {
				   File file ;
					JFileChooser chooser = new JFileChooser();
			    	chooser.setDialogTitle("Path to the RScript.exe (installed R) ");
			    	chooser.setCurrentDirectory(new File(r_Path).getParentFile());
				    FileNameExtensionFilter filter = new FileNameExtensionFilter(" RScript.exe","exe");
					chooser.setFileFilter(filter);
				    int returnVal = chooser.showOpenDialog(null);
				    if(returnVal == JFileChooser.APPROVE_OPTION) {
				    	file = chooser.getSelectedFile();
					}else{
						return;
					}
				    String fname = file.getAbsolutePath();
				    r_Path = fname;
				    if ( fname != null) {
				    	r_Path = fname;
				    	but_R_Path.setText(r_Path);
				    }
			   }
		   });
		  tmp.add(but_R_Path);
		  
		  tmp.add(new JLabel("Path to the local data exchange file *.csv."));
		  info = r_Data; if ( info.length()<1 ) info  = "not set";
		  JButton but_R_Data = new JButton(info);
		  but_R_Data.addActionListener(new ActionListener(){
			   public void actionPerformed(ActionEvent e) {
				   File file ;
					JFileChooser chooser = new JFileChooser();
			    	chooser.setDialogTitle("Path to the local data exchange file *.csv.");
			    	chooser.setCurrentDirectory(new File(r_Data).getParentFile());
				    FileNameExtensionFilter filter = new FileNameExtensionFilter("comma separated values","csv");
					chooser.setFileFilter(filter);
				    int returnVal = chooser.showOpenDialog(null);
				    if(returnVal == JFileChooser.APPROVE_OPTION) {
				    	file = chooser.getSelectedFile();
					}else{
						return;
					}
				    String fname = file.getAbsolutePath();
				    if ( fname != null)
				    	if ( !fname.toLowerCase().endsWith(".csv")) fname += ".csv";	
				    if (  fname != null) {
				    	r_Data = fname;
				    	but_R_Data.setText(r_Data);
				    }
			   }
		   });
		  tmp.add(but_R_Data);
		  
		  tmp.add(new JLabel("Path to the local PCA_LDA.R script *.R"));
		  info = r_Script; if ( info.length()<1 ) info  = "not set";
		  JButton but_R_Script = new JButton(info);
		  but_R_Script.addActionListener(new ActionListener(){
			   public void actionPerformed(ActionEvent e) {
				   File file ;
					JFileChooser chooser = new JFileChooser();
			    	chooser.setDialogTitle("Path to the local DynaMiner R script *.R");
			    	chooser.setCurrentDirectory(new File(r_Script).getParentFile());
				    FileNameExtensionFilter filter = new FileNameExtensionFilter("RScript","r");
					chooser.setFileFilter(filter);
				    int returnVal = chooser.showOpenDialog(null);
				    if(returnVal == JFileChooser.APPROVE_OPTION) {
				    	file = chooser.getSelectedFile();
					}else{
						return;
					}
				    String fname = file.getAbsolutePath();
				    r_Script = fname;
				    if ( r_Script!= null) but_R_Script.setText(r_Script);
				    if ( fname != null) {
				    	r_Script = fname;
				    	but_R_Script.setText(r_Script);
				    }
				    JOptionPane.showMessageDialog(null,"You may need to adjust the exchange file path in the script" ,"Adjust Data Path",JOptionPane.INFORMATION_MESSAGE);
					   Desktop desktop = null;
				        if (Desktop.isDesktopSupported()) {
				        	desktop = Desktop.getDesktop();
				        }
				        if(desktop!=null)
							try {
								desktop.open(new File(r_Script));
							} catch (IOException e1) {
								// 
								e1.printStackTrace();
							}
			   }

		   });
		  tmp.add(but_R_Script);
		  
		  tmp.setPreferredSize(new Dimension(600,600));
		  JOptionPane.showMessageDialog(null,sc,"Settings",JOptionPane.INFORMATION_MESSAGE);
		  
		  Preferences prefs = null;
			prefs = Preferences.userRoot().node("Solver");

			prefs.put("R_Path", r_Path);
			prefs.put("R_Script", r_Script);
			prefs.put("R_Data", r_Data);
		  
	}
}
class LogStreamReader implements Runnable {

    private BufferedReader reader;
   

    public LogStreamReader(InputStream is) {
        this.reader = new BufferedReader(new InputStreamReader(is));
    }

    public void run() {
    	
		int m = MultiVariate_R.data.length;//file
		int n = MultiVariate_R.data[0].length;//area
		
		MultiVariate_R.eigenVectors = null;// new double[n][n];	// 
		MultiVariate_R.eigenValuesPercental = new double[n];	// 
		MultiVariate_R.eigenValues = new double[n];	//
		MultiVariate_R.means = new double[n];
		MultiVariate_R.variances  = new double[n];
		MultiVariate_R.PC_centered = new double[n];
		MultiVariate_R.PCAs = new double[m][n]; // Samples / PCA
		MultiVariate_R.LDAs = null;// new double[m][2]; // Samples / LDA
		MultiVariate_R.LDAsvariances = null;
		MultiVariate_R.LDAscales = null;
		
		
		boolean kill = false;
		int numPC = 0;
		int numArea = 0;
		int numLDA = 0;
		int numPCinLDA = 0;
		
        try {
            String line = reader.readLine();
            while (line != null && !kill) {

//           	System.out.println(line);
            	//in.append(line);
            	String[] elms = line.replace("\"", "").split(":");
            	if(elms.length>1) {
            		if(elms[0].endsWith("DIM_PC")) {
            			numPC 						= Integer.parseInt(elms[1]);
            			numArea 					= Integer.parseInt(elms[2]);
            			MultiVariate_R.eigenVectors 	= new double[numArea][numPC];
            			MultiVariate_R.eigenValuesPercental	= new double[numPC];
            			MultiVariate_R.PCAs				= new double[m][numPC];
            		}
            		if(elms[0].endsWith("rotation")) {
            			int a 						=   Integer.parseInt(elms[1])-1;
            			int p 						=   Integer.parseInt(elms[2])-1;
            			double val 					= Double.parseDouble(elms[3]);
            			MultiVariate_R.eigenVectors[a][p] = val;
            		}
            		if(elms[0].endsWith("PCA_variance")) {
            			int p 						= Integer.parseInt(elms[1])-1;
            			double val 					= Double.parseDouble(elms[2]);
            			MultiVariate_R.eigenValuesPercental[p] = val*100;
            		}
            		if(elms[0].endsWith("DIM_LDA")) {
            			numLDA 						= Integer.parseInt(elms[1]);
            			numPCinLDA 					= Integer.parseInt(elms[2]);
            			MultiVariate_R.LDAsvariances 	= new double[numLDA];
            			MultiVariate_R.LDAscales 		= new double[numPCinLDA][numLDA];
            			MultiVariate_R.LDAs  			= new double[m][numLDA];
            		}
            		if(elms[0].endsWith("LDA_variance")) {
            			int p 						= Integer.parseInt(elms[1])-1;
            			double val 					= Double.parseDouble(elms[2]);
            			MultiVariate_R.LDAsvariances[p] = val*100;
            		}
            		if(elms[0].endsWith("scaling")) {
            			int a 						=   Integer.parseInt(elms[1])-1;
            			int p 						=   Integer.parseInt(elms[2])-1;
            			double val 					= Double.parseDouble(elms[3]);
            			MultiVariate_R.LDAscales[a][p] 	= val*1000;
            		}
            	} // elms lenght
            	

            	  line = reader.readLine();	
            }
            reader.close();

            
        } catch (IOException e) {
            e.printStackTrace();
        }
        

        //MultiVariate_R.r_Output = in.toString();
        finishPCA(m, n);
    }
    private void finishPCA(int m, int n){
    	
    	// TEST: PCA hier brechnen anstatt übernehmen
    	// !!! VERIFIED PCA
    	 for(int files=0;files<m;files++){	// files
    		 for(int pc=0;pc<MultiVariate_R.eigenVectors[0].length;pc++){	 // [PC]
    			 double sum=0;	 
    			 for(int area=0;area<MultiVariate_R.eigenVectors.length;area++){	 // [area]
    				 sum += MultiVariate_R.data[files][area]* MultiVariate_R.eigenVectors[area][pc];
    			 }
    			 MultiVariate_R.PCAs[files][pc] =  sum;
    		
    		 }
    		
    	 }

    	// TEST: LDA hier brechnen anstatt übernehmen
    	// !!! VERIFIED LDA
    	 if (MultiVariate_R.LDAs != null) {
	    	 int countPCA = 6;
	    	 if ( countPCA > MultiVariate_R.LDAsvariances.length )countPCA = MultiVariate_R.LDAsvariances.length; //PCAs / LDA
	    	 MultiVariate_R.LDA_numPCA = MultiVariate_R.LDAscales.length;
	    	 
	    	 if ( MultiVariate_R.LDAs == null ) MultiVariate_R.LDAs = new double[m][countPCA]; // Samples / LDA
	    	 for(int files=0;files<m;files++){	// files
	    		 for(int lda=0;lda<MultiVariate_R.LDAscales[0].length;lda++){	 // [PC]
	        		 double sum=0;
	    			 for(int pcs=0;pcs<MultiVariate_R.LDAscales.length;pcs++){	 // [area]
	    				 sum += MultiVariate_R.PCAs[files][pcs]* MultiVariate_R.LDAscales[pcs][lda];
	    			 }
	    			 MultiVariate_R.LDAs[files][lda] =  sum;
	    		 }
	    		 //;
	    		
	    	}
    	 }
//    	  // PCA In die Mitte setzen
//	     for(int p=0;p<n;p++){	// AREA
//	    	 double avg = 0;
//	    	 double count = 0;
//	    	 for(int i=0;i<m;i++){	// files
//		    		 avg += MultiVariate_R.PCAs[i][p];
//		    		 count ++;
//	    	 }
//	    	 avg = avg/count;
//	    	 MultiVariate_R.PC_centered[p] = avg;
//	    	 for(int i=0;i<m;i++){	// files
//	    		 MultiVariate_R.PCAs[i][p] -=  avg;
//	    	 }
//	     }
	
	  
	 
	  for(int i=0;i<MultiVariate_R.PCAs.length;i++){
	    	 if(i==0){
	    		 MultiVariate_R.maxPCA1 = MultiVariate_R.PCAs[i][0];
	    		 MultiVariate_R.minPCA1 = MultiVariate_R.PCAs[i][0];
	    		 if(MultiVariate_R.PCAs[i].length>1)MultiVariate_R.maxPCA2 = MultiVariate_R.PCAs[i][1];
	    		 if(MultiVariate_R.PCAs[i].length>1)MultiVariate_R.minPCA2 = MultiVariate_R.PCAs[i][1];
	    		 if(MultiVariate_R.PCAs[i].length>2)MultiVariate_R.maxPCA3 = MultiVariate_R.PCAs[i][2];
	    		 if(MultiVariate_R.PCAs[i].length>2)MultiVariate_R.minPCA3 = MultiVariate_R.PCAs[i][2];
	    		 if(MultiVariate_R.PCAs[i].length>3)MultiVariate_R.maxPCA4 = MultiVariate_R.PCAs[i][3];
	    		 if(MultiVariate_R.PCAs[i].length>3)MultiVariate_R.minPCA4 = MultiVariate_R.PCAs[i][3];

	    	 }
	    	 if(MultiVariate_R.maxPCA1<MultiVariate_R.PCAs[i][0])MultiVariate_R.maxPCA1=MultiVariate_R.PCAs[i][0];
	    	 if(MultiVariate_R.minPCA1>MultiVariate_R.PCAs[i][0])MultiVariate_R.minPCA1=MultiVariate_R.PCAs[i][0];
	    	 if(MultiVariate_R.PCAs[i].length>1)if(MultiVariate_R.maxPCA2<MultiVariate_R.PCAs[i][1])MultiVariate_R.maxPCA2=MultiVariate_R.PCAs[i][1];
	    	 if(MultiVariate_R.PCAs[i].length>1)if(MultiVariate_R.minPCA2>MultiVariate_R.PCAs[i][1])MultiVariate_R.minPCA2=MultiVariate_R.PCAs[i][1];
	    	 if(MultiVariate_R.PCAs[i].length>2)if(MultiVariate_R.maxPCA3<MultiVariate_R.PCAs[i][2])MultiVariate_R.maxPCA3=MultiVariate_R.PCAs[i][2];
	    	 if(MultiVariate_R.PCAs[i].length>2)if(MultiVariate_R.minPCA3>MultiVariate_R.PCAs[i][2])MultiVariate_R.minPCA3=MultiVariate_R.PCAs[i][2];
	    	 if(MultiVariate_R.PCAs[i].length>3)if(MultiVariate_R.maxPCA4<MultiVariate_R.PCAs[i][3])MultiVariate_R.maxPCA4=MultiVariate_R.PCAs[i][3];
	    	 if(MultiVariate_R.PCAs[i].length>3)if(MultiVariate_R.minPCA4>MultiVariate_R.PCAs[i][3])MultiVariate_R.minPCA4=MultiVariate_R.PCAs[i][3];
	}
	
	  MultiVariate_R.maxAllPCA = MultiVariate_R.maxPCA1;
	  MultiVariate_R.minAllPCA = MultiVariate_R.minPCA1;
	if ( MultiVariate_R.maxAllPCA < MultiVariate_R.maxPCA1 ) MultiVariate_R.maxAllPCA = MultiVariate_R.maxPCA1;
	if ( MultiVariate_R.maxAllPCA < MultiVariate_R.maxPCA2 ) MultiVariate_R.maxAllPCA = MultiVariate_R.maxPCA2;
	if ( MultiVariate_R.maxAllPCA < MultiVariate_R.maxPCA3 ) MultiVariate_R.maxAllPCA = MultiVariate_R.maxPCA3;
	if ( MultiVariate_R.maxAllPCA < MultiVariate_R.maxPCA4 ) MultiVariate_R.maxAllPCA = MultiVariate_R.maxPCA4;
	
	if ( MultiVariate_R.minAllPCA > MultiVariate_R.minPCA1 ) MultiVariate_R.minAllPCA = MultiVariate_R.minPCA1;
	if ( MultiVariate_R.minAllPCA > MultiVariate_R.minPCA2 ) MultiVariate_R.minAllPCA = MultiVariate_R.minPCA2;
	if ( MultiVariate_R.minAllPCA > MultiVariate_R.minPCA3 ) MultiVariate_R.minAllPCA = MultiVariate_R.minPCA3;
	if ( MultiVariate_R.minAllPCA > MultiVariate_R.minPCA4 ) MultiVariate_R.minAllPCA = MultiVariate_R.minPCA4;

	
	 Path source = Paths.get(MultiVariate_R.r_Data);

	  try{

	    // rename a file in the same directory
	    Files.move(source, source.resolveSibling(new File(MultiVariate_R.r_Data).getName()+".bak"),StandardCopyOption.REPLACE_EXISTING);

	  } catch (IOException e) {
	    e.printStackTrace();
	  }
	  set3D_PCA();
    }

    private void set3D_PCA() {
//		int counter =0;
//		for(int i=0;i<plotter.optsAreas.length;i++){
//	    	if(plotter.optsAreas[i].isSelected())counter++;
//	    }
		UI.tabPCA3D.clearAll();
		UI.tabPCA3D.set2DText(10, 20, "Not enough data available");

//		if (counter < 3) return;
		if ( MultiVariate_R.eigenValuesPercental.length<3) return;
		if ( MultiVariate_R.PCAs == null) return;
		if ( MultiVariate_R.PCAs[0].length < 3) return;
		UI.tabPCA3D.clearAll();
		
		for(int i=0;i<DS.numClasses;i++){
			UI.tabPCA3D.setLegend(DS.legendImage[i])	;
		}
		
		int max = 0;
        for(int i=0;i<MultiVariate_R.PCAs.length;i++){
        	for(int j=0;j<MultiVariate_R.PCAs[i].length;j++){
        		if ( max < Math.abs(MultiVariate_R.PCAs[i][j]) ) max = (int) Math.abs(MultiVariate_R.PCAs[i][j]);
        	}
        }
        // ACHSE Central
        UI.tabPCA3D.setPreLine(0,0,0, true, 255, 0, 0);
        UI.tabPCA3D.setPreLine(0,max,0, true, 255, 0, 0);
        UI.tabPCA3D.setPreLine(0,0,0, true, 255, 0, 0);
        UI.tabPCA3D.setPreLine(0,0,max, true, 0, 255, 0);
        UI.tabPCA3D.setPreLine(0,0,0, true, 0, 255, 0);
        UI.tabPCA3D.setPreLine(-3*max,0,0, true, 0, 0, 255);
        UI.tabPCA3D.setPreLine(3*max,0,0, true, 0, 0, 255);

//        float val =  (float) MultiVariate_R.eigenValuesPercental[0];
//        UI.tabPCA3D.setText(max, max, -max,  "PC_1 ["+ SinglePCA.myRound(val,1)+"%]");
//        val = (float) (MultiVariate_R.eigenValuesPercental[1]);
//        UI.tabPCA3D.setText(-max, -max, -max,  "PC_2 ["+ SinglePCA.myRound(val,1)+"%]");
//        val = (float) (MultiVariate_R.eigenValuesPercental[2]);
//        UI.tabPCA3D.setText(-max, max, max, "PC_3 ["+ SinglePCA.myRound(val,1)+"%]");
        
        UI.tabPCA3D.set2DText(10, 20, "Scale: -"+max+" - "+max );
        
        UI.tabPCA3D.faktor = UI.tabPCA3D.getWidth()/(4.*max);
        UI.tabPCA3D.cFak = 0.5/UI.tabPCA3D.faktor;

//
//        
//        for(int i=0;i<ds.LAVclasses.size();i++){
//			ds_class tmp = ds.LAVclasses.get(i);
//			tab3D_PCA.setLegend(tmp.legendImage)	;
//		}

        double step = (2*max)/10.;
        for (int ma = 0;ma<11; ma++) {
       	 int m = (int) (ma-max+(ma*step));
       	UI.tabPCA3D.setPreLine(-max,-max,-m, false, 200, 200, 200);
       	UI.tabPCA3D.setPreLine(-max,max,-m, true, 200, 200, 200);
       	 
       	UI.tabPCA3D.setPreLine(-max,m,-max, false,  200, 200, 200);
       	UI.tabPCA3D.setPreLine(-max,m,max, true,  200, 200, 200);
       	 
       	UI.tabPCA3D.setPreLine(m,-max,-max, false,  200, 200, 200);
       	UI.tabPCA3D.setPreLine(m,-max,max, true,  200, 200, 200);
       	 
       	UI.tabPCA3D.setPreLine(-max,-max,-m, false,  200, 200, 200);
       	UI.tabPCA3D.setPreLine(max,-max,-m, true,  200, 200, 200);
       	 
       	UI.tabPCA3D.setPreLine(-max,m,-max, false, 200, 200, 200);
       	UI.tabPCA3D.setPreLine(max,m,-max, true,  200, 200, 200);
       	 
       	UI.tabPCA3D.setPreLine(m,-max,-max, false,  200, 200, 200);
       	UI.tabPCA3D.setPreLine(m,max,-max, true, 200, 200, 200);
        }
        
        
        for(int i=0;i<MultiVariate_R.PCAs.length;i++){
//       	 System.out.println(MultiVariate_R.PCAs[i][0]+"\t"+MultiVariate_R.PCAs[i][1]+"\t"+MultiVariate_R.PCAs[i][2]);
			int x = (int)MultiVariate_R.PCAs[i][0];
			int y = (int)MultiVariate_R.PCAs[i][1];
			int z = (int)MultiVariate_R.PCAs[i][2];
			Color c = MultiVariate_R.ClassCols[DS.listClassIndex[i]];
			Color cn = new Color(c.getRed(),c.getGreen(),c.getBlue(), 150 );
			//if ( plotBuilder.doShowLabel)tab3D_PCA.setCube(x, y, z, plotBuilder.pointSize*5, cn, PerformPCA.SampleNames[i]);//(px,py,wert-min,ads,red,green,blue);
    			UI.tabPCA3D.setCube(x, y, z, 15, cn, null);//(px,py,wert-min,ads,red,green,blue);
//    			System.out.println(x+"\t"+ y+"\t"+ z);
        }
        UI.tabPCA3D.repaint();
	}
}

class LogErrorStreamReader implements Runnable {

    private BufferedReader reader;
    StringBuffer logger = new StringBuffer();
    public LogErrorStreamReader(InputStream is) {
        this.reader = new BufferedReader(new InputStreamReader(is));
    }

    public void run() {
        try {
            String line = reader.readLine();
            while (line != null) {
                
            	System.out.println(line);
            	
            	logger.append(line+"\n");
            	
            	if (  line.contains("Execution halted" )){
            		line = null;

            	}else {
                          line = reader.readLine();
            	}
            }
            reader.close();
           // MultiVariate_R.showError(logger.toString());
        } catch (IOException e) {
            e.printStackTrace();
            
        }
        
    }
    
}