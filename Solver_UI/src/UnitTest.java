import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class UnitTest {
	
	public static boolean next 					= true;
	public static boolean logResult 			= false;
	public static ArrayList<String> result 		= new ArrayList<String>();
	static File file							= null;
	
	
	public UnitTest() {
		logResult = true;
		String[] type = {"CSV"};
		String[] sht = {"CSV"};
		file = Tools.getFile("Select file to save", ".",type,sht, true);
		if ( file == null) return;
		if ( file.getName().toLowerCase().endsWith(".csv")) file = new File(file.getAbsolutePath()+"csv");
		result.clear();
		Thread thread = new Thread(new Runnable()
		{
		   public void run()
		   {
			
			   int numRepeat 	= getUserNum ("Repeat","1");
			   int numSamples 	= getUserNum ("numSamples","50");
			   int numVarStart 	= getUserNum ("numFeatures Start","5");
			   int numVarEnd 	= getUserNum ("numFeatures End","30") + 1;
			   logResult = true;
//			   for (int s=10;s<101;s++) {
					for (int v=numVarStart;v<numVarEnd;v++) {
						for (int run=0;run<numRepeat;run++) {
							newDataSet (numSamples, v,2);
							next = false;
							while (!next) {
								try {
									Thread.sleep(500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					}
//				}
				System.out.println("**** UNITTEST ***************************************************************");
				for (int i=0;i<result.size();i++) {
					System.out.println(result.get(i));
				}
				saveResult();
				logResult = false;
		   }
		});
		thread.start();
	}
	private static int getUserNum (String txt, String init) {
		   String erg = JOptionPane.showInputDialog(txt,init);
			if (erg==null)return 0;
			try {
				return Integer.parseInt(erg);
			}catch(NumberFormatException ee){
	        }
			return 0;
	}
	private static void newDataSet (int numSamples, int numVars, int numClasses) {
		
		StringBuffer data = new StringBuffer();
		for (int c=0;c<numClasses;c++) {
			for (int s=0;s<numSamples;s++) {
				data.append("Class"+c);
				for (int v=0;v<numVars;v++) {
					data.append("\t" + Math.random());	
				}
				data.append("\n");
			}
		}
		File f = null;
		try {
			f = File.createTempFile("temp", null);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		if ( f== null ) return;
        f.deleteOnExit();
		FileWriter fw = null;
		try {
			fw = new FileWriter(f, false);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    BufferedWriter bw = new BufferedWriter(fw);
	    try {
	    	fw.write(data.toString());
		} catch (JSONException | IOException e1) {
			e1.printStackTrace();
		}
	  
	    try {
	    	bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    SolverStart.importDataCSV(f.getAbsolutePath(), "\t");
		DS.txtSummary = null;
		DS.fileName =  "UnitTest\t"+numSamples+"\t"+numVars;
		
		DS.filePath = null;
		UI.tmtableClassify.setColumnCount(0);
		UI.tmtableClassify.setRowCount(0);
		new DS();												// INITS
		DS.normParas = Tools.doNormData ();				// Daten Normalisieren
		DS.isPCA = false;
		//UI.maintabbed.setSelectedIndex(UI.tab_Summary);
		UI.refreshStatus();
		SolverStart.analyzeRawData(DS.fileName );
		if ( UI.menuActionChk_Jump.isSelected() )UI.maintabbed.setSelectedIndex(UI.tab_Summary);
		
		UI.train(1) ;
	}
	private static void saveResult() {
		
		FileWriter fw = null;
		try {
			fw = new FileWriter(file, false);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    BufferedWriter bw = new BufferedWriter(fw);
	    try {
	    	for (int i=0;i<result.size();i++) {
//				System.out.println(result.get(i));
				fw.write(result.get(i)+"\n");
			}
		} catch (JSONException | IOException e1) {
			e1.printStackTrace();
		}
	  
	    try {
	    	bw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
