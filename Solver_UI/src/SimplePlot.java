import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
/*
 *  Copyright(c) 2009-2023, Daniel Sanders, All rights reserved.
 *  https://github.com/dsandersGit/GIT_Solver
 */

public class SimplePlot{

		
	/*
	 * Einfacher 2D-Plot von Scatter Daten. 
	 * 2015 Daniel Sanders dsanders.gmx.net
	 */
	
	JFrame jF = new JFrame();
	SP_PlotCanvas canvas = new SP_PlotCanvas();
	
	public static void main(String[] args) {
		new SimplePlot(true);
	}
	
	public SimplePlot(){
		basicDesign();	
		jF.addKeyListener(new KeyListener() {
		    public void keyPressed(KeyEvent e) { 	
		    	if(e.getExtendedKeyCode() == 86) {										//PASTE from CLIP
		    		if(e.isControlDown()){
		    			canvas.dats.clear();
		    			importFromClipBoard();   
		    			
		    		}
		    	}
		    }
		
		    public void keyReleased(KeyEvent e) {
		    }
		
		    public void keyTyped(KeyEvent e) {
		    }
		  
		});
		jF.setVisible(true);
	}
	
	public SimplePlot(boolean menubar){
		basicDesign();
		jF.setJMenuBar(getMenuBar());
		jF.setVisible(true);
	}
	public SimplePlot(int x,int y){
		basicDesign();
		jF.setLocation(x, y);
		jF.setVisible(true);
	}
	
	private JMenuBar getMenuBar(){
	
		JMenuBar mBar = new JMenuBar();
		//----------------------------------------------------------------------------------------------------		
		JMenu menuData = new JMenu( "Data" );
			JMenuItem menuDataAddData = new JMenuItem("Add data from clibboard");
			menuDataAddData.setAccelerator(KeyStroke.getKeyStroke('V',InputEvent.CTRL_MASK));
			menuDataAddData.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
				importFromClipBoard();   
			}});
			menuData.add(menuDataAddData);
			
			JMenuItem menuDataReplaceData = new JMenuItem("Paste data from clibboard");
			menuDataReplaceData.setAccelerator(KeyStroke.getKeyStroke('X',InputEvent.CTRL_MASK));
			menuDataReplaceData.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){	
				canvas.dats.clear();
				importFromClipBoard();   
			}});
			menuData.add(menuDataReplaceData);
		//----------------------------------------------------------------------------------------------------
		mBar.add(menuData);

		
		

		return mBar;
	}
	private void basicDesign(){
		jF.setLayout(new BorderLayout());
		jF.add(canvas,BorderLayout.CENTER);
		canvas.setPreferredSize(new Dimension(800,600));
		jF.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		jF.setSize(new Dimension(800,600));
		jF.setResizable(true);
		//jF.setLocationRelativeTo(null);
		
	}


	
	
	public void importFromClipBoard(){
		
	    String result = "";
	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	    Transferable contents = clipboard.getContents(null);
	    boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
	    if (hasTransferableText) {
	      try {
	        result = (String)contents.getTransferData(DataFlavor.stringFlavor);
	      }
	      catch (UnsupportedFlavorException | IOException ex){
	        System.out.println(ex);
	        ex.printStackTrace();
	      }
	    }
	    
	    Object[] options = {"X | Y | Y | Y | ..", "X | Y | X | Y | .."};
		 
        int selected = JOptionPane.showOptionDialog(null,
                                                    "Select data format",
                                                    "Paste data from clipboard",
                                                    JOptionPane.DEFAULT_OPTION, 
                                                    JOptionPane.QUESTION_MESSAGE, 
                                                    null, options, options[1]);
        
        if(selected == 0)getClipDataXYYY(result);
        if(selected == 1)getClipDataXYXY(result);
        canvas.setOuterBackGroundColor(Color.WHITE);
        canvas.setInnerBackGroundColor(Color.WHITE);
	  
			
	}
	private void getClipDataXYXY(String s){
		
		int cols = 0;
		int rows = 0;
		int skip = 0;
		String[] lns = s.split("\n");
		
		boolean success = false;
		while(!success){
			String[] vals = lns[rows].replace(",", ".").split("\t");
			cols = vals.length;
			float[] valn = new float[vals.length];
			try{
				for(int k=0;k<vals.length;k++){
					valn[k] = Float.parseFloat(vals[k]);
				}
				success = true;
				rows++;
			}catch (java.lang.NumberFormatException e){
				System.out.println(lns[rows]);
				success = false;
				skip++;
				rows++;
			}
			if(rows > lns.length-1)success = false;
		}
	
		String xlab = "x-axis";
		String ylab = "y-axis";
		String title = "Clipboard";
		String[] d = lns[skip-2].split("\t");
		if(skip > 1){
			if(d.length > 0)title = d[0];
			if(d.length > 1)xlab =  lns[skip-2].split("\t")[1];
			if(d.length > 2)ylab =  lns[skip-2].split("\t")[2];
		}
		
		
		float[][] x = new float[cols/2][lns.length-skip];
		float[][] y = new float[cols/2][lns.length-skip];
		boolean[][] ok = new boolean[cols/2][lns.length-skip];
		String[] sNames = new String[cols/2];
		
		
		for(int i = 0;i<cols;i+=2){
			if(skip > 0){
				sNames[i/2] =  lns[skip-1].split("\t")[i+1];
			}else{
				sNames[i/2] =  "#"+(i/2);
			}
			
		}
		for(int i = skip;i<lns.length;i++){
			String[] vals = lns[i].replace(",", ".").split("\t");

			success = true;
			
			for(int k=0;k<vals.length-1;k+=2){
				try{
					x[k/2][i-skip] = Float.parseFloat(vals[k]);
					y[k/2][i-skip] = Float.parseFloat(vals[k+1]);
					ok [k/2][i-skip] = true;
				}catch (java.lang.NumberFormatException e){
					ok [k/2][i-skip] = false;
				}
			}
		}
		
		
	
		
		Color[] coli = {Color.black,Color.RED,Color.blue,
			Color.magenta,Color.ORANGE,Color.pink,
			Color.cyan,Color.gray, Color.DARK_GRAY,Color.LIGHT_GRAY};
		
		for(int i=0;i<cols/2;i++){
			canvas.dats.add(new SP_PlotData(x[i],y[i], ok[i],12,coli[i%coli.length],sNames[i],true,false,true));
//			System.out.println(i);
		}
		
		
		
		canvas.setXAxis(xlab);
		canvas.setYAxis(ylab);
		canvas.setTitle(title);
		canvas.doRedraw = true;
		canvas.getMinMax();
		canvas.refreshPlot();
		
	}
	
	
	private void getClipDataXYYY(String s){
		
		int cols = 0;
		int rows = 0;
		int skip = 0;
		String[] lns = s.split("\n");
		
		boolean success = false;
		while(!success){
			String[] vals = lns[rows].replace(",", ".").split("\t");
			cols = vals.length;
			float[] valn = new float[vals.length];
			try{
				for(int k=0;k<vals.length;k++){
					valn[k] = Float.parseFloat(vals[k]);
				}
				success = true;
			
			}catch (java.lang.NumberFormatException e){
				System.out.println(lns[rows]);
				success = false;
				skip++;
				
			}
			rows++;
			if(rows > lns.length-1)break;
		}
		
		String xlab = "x-axis";
		String ylab = "y-axis";
		String title = "Clipboard";
		String[] d = lns[skip-1].split("\t");
		if(skip > 0){
			//if(d.length > 0)title = d[0];
			if(d.length > 0)xlab =  lns[skip-1].split("\t")[0];
			if(d.length > 1)ylab =  lns[skip-1].split("\t")[1];
		}
		
		
		float[][] x = new float[cols-1][lns.length-skip];
		float[][] y = new float[cols-1][lns.length-skip];
		boolean[][] ok = new boolean[cols-1][lns.length-skip];
		String[] sNames = new String[cols];
		
		for(int i = 1;i<cols;i++){
			if(skip > 0){
				sNames[i-1] =  lns[skip-1].split("\t")[i];
			}else{
				sNames[i-1] =  "#"+(i);
			}
			
		}
		for(int i = skip;i<lns.length;i++){
			String[] vals = lns[i].replace(",", ".").split("\t");

			success = true;
			for(int k=1;k<vals.length;k++){
				try{
					x[k-1][i-skip] = Float.parseFloat(vals[0]);
					y[k-1][i-skip] = Float.parseFloat(vals[k]);
					ok [k-1][i-skip] = true;
				}catch (java.lang.NumberFormatException e){
					ok [k-1][i-skip] = false;
				}
			}
		}
			
		Color[] coli = {Color.black,Color.RED,Color.blue,
			Color.magenta,Color.ORANGE,Color.pink,
			Color.cyan,Color.gray, Color.DARK_GRAY};
		
		for(int i=0;i<x.length;i++){
			canvas.dats.add(new SP_PlotData(x[i],y[i], ok[i],12,coli[i%coli.length],sNames[i],false,true,false));
		}
		
		
		
		canvas.setXAxis(xlab);
		canvas.setYAxis(ylab);
		canvas.setTitle(title);
		canvas.doRedraw = true;
		canvas.getMinMax();
		canvas.refreshPlot();
		
	}
}	
	

class SP_PlotCanvas extends JPanel{
	
	
	static Image icon_lock = null;
	
	public ArrayList<SP_PlotData> dats = new ArrayList<SP_PlotData>();
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	BufferedImage canvasImage = null;
	
	public  double Ymin=0;
	public  double Ymax=0;
	public  double xmin=0;	
	public  double xmax=0;
	
	private boolean freezeX0 = false;
	private boolean freezeY0 = false;
	private boolean freezeY1 = false;
	
	
	
	private double dragXalt = -1;
	private double dragYalt = -1;
	
	int shiftLeft = 80;
	int shiftTop = 60;
	
	//private  String unit = "a.u.";
	private  String xLabel = "";
	private  String yLabel = "";
	private  String title = "";
	
	private  int simpleSelX1 =0;
	private  int simpleSelX0 =0;
	private  int simpleSelY1 =0;
	private  int simpleSelY0 =0;
	
	public  int activeData = -1;
	
	boolean doRedraw = true;
	
	private Color outerBackCol = Color.white;
	private Color innerBackCol = Color.white;
	private Color baseCol = Color.black;
	
	public boolean show0 = true;

	protected int curPosXpx;

	protected int curPosYpx;
	
	ArrayList<Double> storeValX = new ArrayList<Double>();
	ArrayList<Double> storeValY = new ArrayList<Double>();
	
	 
	SP_PlotCanvas(){
	
		//ClassLoader.getSystemClassLoader();
		//icon_lock = new ImageIcon(ClassLoader.getSystemResource("icon_lock.png")).getImage();
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		toolkit.getScreenSize();
		
		this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		this.addMouseWheelListener (new MouseWheelListener() {
			 public void mouseWheelMoved(MouseWheelEvent e){
		        	if (!e.isAltGraphDown() && !e.isAltDown() && !e.isControlDown() && !e.isShiftDown()) {
			        	Ymin += (Ymax-Ymin) * 0.01*e.getWheelRotation();
			        	Ymax -= (Ymax-Ymin)  * 0.01*e.getWheelRotation();
			        	xmin += (xmax-xmin)  * 0.01 *e.getWheelRotation();
			        	xmax -= (xmax-xmin)  * 0.01 *e.getWheelRotation();
		        	}
		        	if (!e.isAltGraphDown() && !e.isAltDown() && !e.isControlDown() && e.isShiftDown()) {
			        	Ymin += (Ymax-Ymin) * 0.01*e.getWheelRotation();
			        	Ymax -= (Ymax-Ymin)  * 0.01*e.getWheelRotation();
		        	}
		        	if (!e.isAltGraphDown() && !e.isAltDown() && e.isControlDown() && !e.isShiftDown()) {
		        		xmin += (xmax-xmin)  * 0.01 *e.getWheelRotation();
			        	xmax -= (xmax-xmin)  * 0.01 *e.getWheelRotation();
		        	}	  
		        	freezeScaling(true);
		        	repaint();
		        }
		});
		this.addMouseListener( 
	        	new MouseListener() {
	    		        public void mouseClicked(MouseEvent e){
	    		        	simpleSelX0 = e.getX();
							 simpleSelX1 = e.getX();
							 simpleSelY0 = e.getY();
							 simpleSelY1 = e.getY();
	    		        }
	    		        public void mouseEntered(MouseEvent e){        }
	    		        public void mouseExited(MouseEvent e){       }
	    		        public void mousePressed(MouseEvent e){ 
	    		        	 simpleSelX0 = e.getX();
							 simpleSelX1 = e.getX();
							 simpleSelY0 = e.getY();
							 simpleSelY1 = e.getY();

	    		        }
	    		        public void mouseReleased(MouseEvent e){
	    		        	
	    		        	doRedraw = true;
	    		        	dragXalt = -1;
	    		        	dragYalt = -1;
	    		        	
	    		        	// Click auf Position in Graph
	    		        	if ( e.isControlDown() ){
		    		        	storeValX.add(getValFromX(e.getX()));
		    		        	storeValY.add(getValFromY(e.getY()));
	    		        	}
	    		        	
	    		        	// ? Linke Maustaste
	    		        	if(e.getModifiers()!=InputEvent.BUTTON3_MASK) {
	    		        		// Abfrage, ob ein Bereich gew�hlt ist
		    		        	if(simpleSelX1 != simpleSelX0){
		    		        		if (!e.isAltGraphDown() && !e.isAltDown() && !e.isControlDown() && !e.isShiftDown()) {
				    		        	//  Speichern der gesetzten Punkte = ZOOM
		    		        			if(simpleSelX1>simpleSelX0 && simpleSelY1>simpleSelY0 ){
		    		        				double tmp1 =getValFromX(simpleSelX0);
					                 		double tmp2 = getValFromX(simpleSelX1);
					                 		xmin = tmp1;
					                 		xmax = tmp2;
					                 		tmp1 = getValFromY(simpleSelY1);
					                 		tmp2 = getValFromY(simpleSelY0);
					                 		Ymin = tmp1;
					                 		Ymax = tmp2;
					                 		
					                 		freezeScaling(true);
					                 		
					                 	}else{
					                 		freezeScaling(false);
					                 		 getMinMax();
					                 	}
		    		        		}
		    		        		
		    		        		simpleSelY0 = -1;
			    		        	simpleSelY1 = -1;
			    		        	simpleSelX0 = -1;
			    		        	simpleSelX1 = -1;
			    		        	
		    		        	}
		    		        	removeSelectionAndRepaint();
		    		        	repaint();
	    		        	}else{
	    		        		// RECHTE MAUSTASTE
	    		        		JPopupMenu inPOP = new JPopupMenu();
	    		        		JMenuItem item=null;
	    		        		
    		        			   item = new JMenuItem("Copy data to Clipboard");
    		        				inPOP.add(item);
    		        					item.addActionListener(new ActionListener(){
    		        					   public void actionPerformed(ActionEvent e) {
    		        						   //for (int d = 0; d < dats.size(); d++){
    		        							   copyToClipBoard();
    		        						   //}
    		        		                }
    		        					});
	        					 item = new JMenuItem("Copy clicks to Clipboard");
    		        				inPOP.add(item);
    		        					item.addActionListener(new ActionListener(){
    		        					   public void actionPerformed(ActionEvent e) {
    		        						   //for (int d = 0; d < dats.size(); d++){
    		        							   copyClicksToClipBoard();
    		        						   //}
    		        		                }
    		        					});
	        					
//	        					 item = new JMenuItem("Copy image to Clipboard");
//    		        				inPOP.add(item);
//    		        					item.addActionListener(new ActionListener(){
//    		        					   public void actionPerformed(ActionEvent e) {
//    		        						   copyImageToClip();
//    		        		                }
//    		        					});
	        					inPOP.show(e.getComponent(), e.getX(), e.getY());
	    		        	}
	    		        	
	    		        }
	        	}
		     );
		this.addMouseMotionListener (new MouseMotionListener() {
	        public void mouseMoved(MouseEvent e){
	        
	     	        
	 	        	curPosXpx = e.getX();
	 	        	curPosYpx = e.getY();
	 	        	repaint();
	 	

		
	        
	        }
	        public void mouseDragged(MouseEvent e){
	        	doRedraw = false;
	        	if(e.getModifiers() != InputEvent.BUTTON2_MASK) {	
	        		
		        	simpleSelX1= e.getX();
					simpleSelY1= e.getY();
					doRedraw = false;
					repaint();
	        	}else{
	        		if(dragXalt>-0){
	        			double dx = dragXalt - getValFromX(e.getX());
	        			double dy = dragYalt - getValFromY(e.getY());
	        			
	        			xmin += dx;
	        			xmax += dx;
	        			Ymin += dy;
	        			Ymax += dy;
	        			freezeScaling(true);
	        			doRedraw = true;
						repaint();
	        		}
	        		dragXalt = getValFromX(e.getX());
        			dragYalt = getValFromY(e.getY());
	        	}
	       	}
	       });  
	}
	
	
	
//	private void 	copyImageToClip(){
//		ImageTransferable it = new ImageTransferable(canvasImage);
//        Clipboard clip=Toolkit.getDefaultToolkit().getSystemClipboard();
//        clip.setContents(it,null);
//	}
	private void copyClicksToClipBoard(){
		 Object[] options = {"'.' DOT", "',' Comma"};
		 
		   int selected = JOptionPane.showOptionDialog(null,
                                      "Select the decimal separator for numeric values",
                                      "Copy data to clipboard",
                                      JOptionPane.DEFAULT_OPTION, 
                                      JOptionPane.INFORMATION_MESSAGE, 
                                      null, options, options[1]);


		   boolean replaceToComma = false;
		  if(selected != 0){
		 	 replaceToComma = true;
		  }
		  String 	wert = "MathData\n";
		  			wert += "x\ty\n";
		 String ding = "";
		  
		  for ( int i=0; i<storeValX.size(); i++ ){

			  ding = storeValX.get(i) + "\t" + storeValY.get(i);
			  if(replaceToComma)ding = ding.replace(".", ",");
			
			  wert += ding + "\n";
		  }

		  storeValX.clear();
		  storeValY.clear();
		  
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection stringSelection = new StringSelection( wert );
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents( stringSelection, null );
	}
	private void copyToClipBoard(){
		 Object[] options = {"'.' DOT", "',' Comma"};
		 
		   int selected = JOptionPane.showOptionDialog(null,
                                       "Select the decimal separator for numeric values",
                                       "Copy data to clipboard",
                                       JOptionPane.DEFAULT_OPTION, 
                                       JOptionPane.INFORMATION_MESSAGE, 
                                       null, options, options[1]);


		   boolean replaceToComma = false;
		  if(selected != 0){
		 	 replaceToComma = true;
		  }
		  String wert = "MathData";
		  for ( int i=0; i<dats.size(); i++){
			  wert += "\t"+ dats.get(i).label+"\t";
		  }
		  wert += "\n";
		  int rows = 0;

			 for ( int i=0; i<dats.size(); i++){
				 if ( rows < dats.get(i).xdat.length ) rows = dats.get(i).xdat.length;
			 }
		  
		  for ( int y=0; y<rows; y++ ){
			  for ( int i=0; i<dats.size(); i++){
				  SP_PlotData d = dats.get(i);
				  String ding = "" + d.xdat[y] + "\t" + d.ydat[y];
				  if(replaceToComma)ding = ding.replace(".", ",");
				  wert += ding;
				  wert += "\t";
			  }
			  wert += "\n";
		  }
		
			
		//String nwert = wert.replace(".", ",");
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection stringSelection = new StringSelection( wert );
		clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents( stringSelection, null );
	}
	private void freezeScaling(boolean freeze){
		freezeX0 = freeze;
		freezeY0 = freeze;
		freezeY1 = freeze;
	}
	public void setScaleX0(double nVal){
		freezeX0 = true;
		xmin = nVal;
	}
	public void setScaleY0(double nVal){
		freezeY0 = true;
		Ymin = nVal;
	}
	public void setScaleY1(double nVal){
		freezeY1 = true;
		Ymax = nVal;
	}
	
	public void doFreezeX0(){
		if(freezeX0){
			freezeX0 = false;
		}else{
			freezeX0 = true;
		}
	}
	public void doFreezeY0(){
		if(freezeY0){
			freezeY0 = false;
		}else{
			freezeY0 = true;
		}
	}
	public void doFreezeY1(){
		if(freezeY1){
			freezeY1 = false;
		}else{
			freezeY1 = true;
		}
	}
	
	
	public void refreshPlot(){
		getMinMax();
		repaint();
	}
	
	public void setXY(float[] x, float[] y, int size,Color[] cols,String name,boolean ShowDots, boolean ShowLines, boolean Dots3D){
		dats.add(new SP_PlotData(x,y, size,cols,name,ShowDots,ShowLines,Dots3D));
		//refreshPlot();
	}
	public void setXY(float[] x, float[] y, int size,Color col,String name,boolean ShowDots, boolean ShowLines, boolean Dots3D){
		dats.add(new SP_PlotData(x,y, size,col,name,ShowDots,ShowLines,Dots3D));
		//refreshPlot();
	}
	public void setXY(float[] x, float[] y, boolean[] ok, int size,Color col,String name,boolean ShowDots, boolean ShowLines, boolean Dots3D){
		dats.add(new SP_PlotData(x,y,ok, size,col,name,ShowDots,ShowLines,Dots3D));
		refreshPlot();
	}
	public void setOuterBackGroundColor(Color bk){
		outerBackCol = bk;
		repaint();
	}
	public void setInnerBackGroundColor(Color bk){
		innerBackCol = bk;
		repaint();
	}
	public void setBaseColor(Color bk){
		baseCol = bk;
		repaint();
	}
	
	public void setTitle(String Title){
		title = Title;
		repaint();
	}
	public void setXAxis(String s){
		xLabel = s;
		repaint();
	}
	public void setYAxis(String s){
		yLabel = s;
		repaint();
	}
	public void setMargin ( int left, int top){
		shiftLeft = left;
		shiftTop = top;
	}
	void getMinMax(){
		

		boolean hasxMin = false;
		boolean hasyMin = false;
		boolean hasxMax = false;
		boolean hasyMax = false;
		

		for(int i = 0;i<dats.size();i++){
			SP_PlotData pd = dats.get(i);
			for(int x = 0;x<pd.xdat.length;x++){
				if(pd.ok[x]){
					if(!freezeX0)if(xmin > pd.xdat[x] || !hasxMin) {	xmin = pd.xdat[x];hasxMin = true;}
					if(xmax < pd.xdat[x] || !hasxMax) {	xmax = pd.xdat[x];hasxMax = true;}
					if(!freezeY0)if(Ymin > pd.ydat[x] || !hasyMin) { Ymin = pd.ydat[x];hasyMin = true;}
					if(!freezeY1)if(Ymax < pd.ydat[x] || !hasyMax) {	Ymax = pd.ydat[x];hasyMax = true;}
				}
			}
		}
		
		if(!freezeY0)Ymin=(float) (Ymin - (Ymax-Ymin)*0.1);
		if(!freezeY1)Ymax=(float) (Ymax + (Ymax-Ymin)*0.1);
		if(!freezeX0)xmin=(float) (xmin - (xmax-xmin)*0.05); 
		
		if ( Ymin == Ymax) {
			Ymin = Ymin - 1;
			Ymax = Ymax + 1;
		}
		if ( xmin == xmax) {
			xmin = xmin - 1;
			xmax = xmax + 1;
		}
		
		xmax=(float) (xmax + (xmax-xmin)*0.05); 
		
		
	}
	
	
		private  void removeSelectionAndRepaint(){
		
		simpleSelY0 = -1;
    	simpleSelY1 = -1;
    	simpleSelX0 = -1;
    	simpleSelX1 = -1;
    	//repaint();
		
	}
	
	
	protected void paintComponent( Graphics g )
	{
		
		super.paintComponent( g);
		Graphics2D g2 = (Graphics2D) g;
	
		
	
		if(doRedraw) drawBasePlot(this.getWidth(),this.getHeight());
		g.drawImage(canvasImage, 0, 0,null);
		g.setFont(new Font("Consolas", Font.PLAIN, 20));
		if(freezeX0)g.drawImage(icon_lock,5,5,null);
		
		
		// Click 
		g.setColor(Color.blue);
		for ( int i=0; i<storeValX.size(); i++ ){


			  g.fillOval(getX(storeValX.get(i))-3, getY(storeValY.get(i))-3, 6, 6);

		  }
		
		// ZOOM Rechteck
		g.setColor(baseCol);
		g.drawRect(simpleSelX0,simpleSelY0,simpleSelX1-simpleSelX0,simpleSelY1-simpleSelY0);
		  Stroke stroke = new BasicStroke(1,
		    	    BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
		    	    new float[] { 4, 4 }, 0);
			g2.setStroke(stroke);
			g.setColor(Color.orange);
			g.drawRect(simpleSelX0,simpleSelY0,simpleSelX1-simpleSelX0,simpleSelY1-simpleSelY0);
			
			int mw = 2+(int)- Math.log10(xmax-xmin);
			int mh =2+ (int)- Math.log10(Ymax-Ymin);
			
			// Cursor werte
			if(curPosXpx > getX(xmin) && curPosXpx < getX(xmax) && curPosYpx < getY(Ymin) && curPosYpx > getY(Ymax)){
				g.setColor(Color.red);
				double curPosX = getValFromX(curPosXpx);
	        	double curPosY = getValFromY(curPosYpx);
	        	
	        	g.drawLine(getX(xmin), curPosYpx, getX(xmax), curPosYpx);
	        	g.drawLine(curPosXpx, getY(Ymin), curPosXpx, getY(Ymax));
	        	
//				g.drawString("" + myRound((float)curPosX,mw), curPosXpx+5, getY(Ymax) + 15); 
//				g.drawString("" + myRound((float)curPosY,mh), getX(xmin)+8, curPosYpx + 12);
				
				g.drawString("" + myRound((float)curPosX,mw), curPosXpx+5, curPosYpx -3 ); 
				g.drawString("" + myRound((float)curPosY,mh), curPosXpx+5, curPosYpx - 16);
			}
			
			//g.drawString("#clicks: " + storeValX.size(), 2, getY(Ymin) ); 
			
	}
	
	private void drawBasePlot(int wg, int hg){
		
		
		 
		
		canvasImage = new BufferedImage(wg, hg ,BufferedImage.TYPE_INT_RGB);
		Graphics g = canvasImage.getGraphics();
		Graphics2D g2 = (Graphics2D) g;
		
		g.setFont(new Font("Consolas", Font.PLAIN, 20));
		  
		// Grundfarbe
		g.setColor(innerBackCol);
		g.fillRect(0, 0,wg,hg);
		
		
//		float w =wg-2*shiftLeft;
		float w =wg-shiftLeft-5;
		float h = hg;
		
		g.setColor(baseCol);
		
		
		g2.setStroke(new BasicStroke(2));
		
// Daten plotten
		
	
	
		  // Points
		for(int i = 0;i<dats.size();i++){
			SP_PlotData pd = dats.get(i);
			if ( pd!=null)
			 if(pd.showDots){
					for(int x = 0; x < pd.xdat.length;x++){
						if(pd.ok[x]){
							
							g.setColor(pd.col);
							if ( pd.cols != null)				// NEW
								g.setColor(pd.cols[x]);	
							if(activeData != i && activeData>-1) g.setColor(Color.LIGHT_GRAY);
							g.fillOval(getX(pd.xdat[x])-pd.dotSize/2, getY(pd.ydat[x])-pd.dotSize/2, pd.dotSize, pd.dotSize);
							g.setColor(Color.DARK_GRAY);
							g.drawOval(getX(pd.xdat[x])-pd.dotSize/2, getY(pd.ydat[x])-pd.dotSize/2, pd.dotSize, pd.dotSize);
							if(pd.dots3D)g.setColor(new Color(255,255,255));
							if(pd.dots3D)g.fillOval(getX(pd.xdat[x])-pd.dotSize/4-2, getY(pd.ydat[x])-pd.dotSize/4-2, pd.dotSize/2, pd.dotSize/2);
						}
					}
			 	}
			  }
		

		
		  // Points Active
		for(int i = 0;i<dats.size();i++){
			SP_PlotData pd = dats.get(i);
			 if(pd.showDots && i == activeData){
					for(int x = 0; x < pd.xdat.length;x++){
						if(pd.ok[x]){
							g.setColor(pd.col);
							g.fillOval(getX(pd.xdat[x])-pd.dotSize/2, getY(pd.ydat[x])-pd.dotSize/2, pd.dotSize, pd.dotSize);
							if(pd.dots3D)g.setColor(new Color(255,255,255));
							if(pd.dots3D)g.fillOval(getX(pd.xdat[x])-pd.dotSize/4-2, getY(pd.ydat[x])-pd.dotSize/4-2, pd.dotSize/2, pd.dotSize/2);
						}
					}
			 	}
			  }
		
		// LINES
		  for(int i = 0;i<dats.size();i++){
			  SP_PlotData pd = dats.get(i);
			  if(pd.showLines){
				  g.setColor(pd.col);
				  if(activeData != i && activeData>-1) g.setColor(Color.LIGHT_GRAY);
				  for(int x = 1; x < pd.xdat.length;x++){
					  if(pd.ok[x] && pd.ok[x-1])
					  g.drawLine(getX(pd.xdat[x]),getY(pd.ydat[x]),getX(pd.xdat[x-1]),getY(pd.ydat[x-1]));
				  }
			  }
		  }
		
			// LINES Active
		  for(int i = 0;i<dats.size();i++){
			  SP_PlotData pd = dats.get(i);
			  if(pd.showLines && i == activeData){
				  g.setColor(pd.col);
				  for(int x = 1; x < pd.xdat.length;x++){
					  if(pd.ok[x] && pd.ok[x-1])
					  g.drawLine(getX(pd.xdat[x]),getY(pd.ydat[x]),getX(pd.xdat[x-1]),getY(pd.ydat[x-1]));
				  }
			  }
		  }
		if(show0){
			float sdash[] = { 2.0f };
			  g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
			            BasicStroke.JOIN_MITER, 10.0f, sdash, 0.0f));
			// Draw 0|0
			if(xmin < 0 && xmax > 0){
				g.setColor(Color.BLUE);
				g.drawLine(getX(0), getY(Ymin), getX(0), getY(Ymax));
			}
			if(Ymin < 0 && Ymax > 0){
				g.setColor(Color.BLUE);
				g.drawLine(getX(xmin), getY(0), getX(xmax), getY(0));
			}
		}
		
		
		
		
				
		
		g2.setStroke(new BasicStroke(2));
		
		
		// L�schen des �berzeichneten Gebiets
		g.setColor(outerBackCol);
		// links
		g.fillRect(0, 0, shiftLeft, hg);
		// rechts
		g.fillRect(this.getWidth()-5+1, 0, 5, hg);
		// unten
		g.fillRect(0,hg-shiftTop+1,this.getWidth(), shiftTop);
		// oben
		g.fillRect(0,0,this.getWidth(), shiftTop);
	
		g.setColor(baseCol);
		g.drawRect(shiftLeft, shiftTop, (int)w, (int)h-2*shiftTop+1);
		
		
		  // Labels
	  	FontMetrics fm = this.getGraphics().getFontMetrics(this.getGraphics().getFont());
	    int height = fm.getHeight();
		    int width=0;
		    
		int c = 0;    
		for(int i=0;i<dats.size();i++){
			SP_PlotData pd = dats.get(i);
			if(pd.label!=null) {					//;pd.label="";
	  		 	g.setColor(pd.col);
	  		 	//g.fill3DRect((int)w+shiftLeft+8, this.getHeight()-(int)h+(i*(height+3))+shiftTop+9, height, height, true);
	  		 	g.fill3DRect(shiftLeft+8, this.getHeight()-(int)h+(c*(height+3))+shiftTop+9, height, height, true);
				g.setColor(baseCol);
//				g.drawString(pd.label, (int)w+shiftLeft+8+height+5, this.getHeight()-(int)h+(i*(height+3))+shiftTop+height+6);
				g.drawString(pd.label, shiftLeft+8+height+5, this.getHeight()-(int)h+(c*(height+3))+shiftTop+height+6);
				c++;
			}
		}
		
	
		boolean by2 = false;
		double ya=Math.floor((Ymin)/getAxInc(Ymin,Ymax))*getAxInc(Ymin,Ymax);
		
		double axin = getAxInc(Ymin,Ymax);

		if((Ymax-Ymin)/axin<2){
			axin /=2;
			by2 = true;
		}
		
		int nrDigits = (int) (-Math.floor(Math.log10(axin)));
		if(nrDigits<0)nrDigits=0;
	
		 while(ya<Ymax){
	   
	       		String out = ""+myRound((float)ya,nrDigits);
	       		
	       		if(getY(ya)<h-shiftTop && getY(ya)>shiftTop ){
		        	g.drawLine(shiftLeft, getY(ya),shiftLeft+7, getY(ya));
		        	g.drawLine((int)w+shiftLeft, getY(ya),(int)w-7+shiftLeft, getY(ya));
		            fm = this.getGraphics().getFontMetrics(g.getFont());
		    		width = fm.stringWidth(out);
		    		g.setColor(baseCol);
		    		g.drawString(out, shiftLeft-5-width, getY(ya)-2+6);
	       		}
	        	ya += axin;
	        }
		
		 ya=Math.floor((Ymin)/getAxInc(Ymin,Ymax))*getAxInc(Ymin,Ymax);
			
			 axin = getAxInc(Ymin,Ymax);
			 if(by2){
				 axin /=20;
			 }else{
				 axin /=10;
			 }
		
			 while(ya<Ymax){
		       		if(getY(ya)<h-shiftTop && getY(ya)>shiftTop ){
			        	g.drawLine(shiftLeft, getY(ya),shiftLeft+4, getY(ya));
			        	g.drawLine((int)w+shiftLeft, getY(ya),(int)w-4+shiftLeft, getY(ya));
		       		}
		        	ya += axin;
		        }
		
		 g2.setStroke(new BasicStroke(2));
		 g.setColor(baseCol);
		by2 = false;
		//Neu X-Achse
		 double xa=Math.floor((xmin)/getAxInc(xmin,xmax))*getAxInc(xmin,xmax);
			axin = getAxInc(xmin,xmax);
			if((xmax-xmin)/axin<2){
				axin /=2;
				by2 = true;
			}
			 nrDigits = (int) (-Math.floor(Math.log10(axin)));
			if(nrDigits<0)nrDigits=0;
			while(xa<xmax){
	       		String out = ""+myRound((float)xa,nrDigits);
	       		if(xa>=xmin){
	       			//if(!ds.ColNormisLog){
		       			g.drawLine(getX(xa), (int)h-shiftTop, getX(xa), (int)h-shiftTop-7);
		       			g.drawLine(getX(xa), shiftTop, getX(xa), shiftTop+7);
			        	g.drawString(out,getX(xa)-20, (int)h-shiftTop+25);
	       		
	        	}
	       		xa += axin;
	        }
		
		xa=Math.floor((xmin)/getAxInc(xmin,xmax))*getAxInc(xmin,xmax);
			axin = getAxInc(xmin,xmax);
			 if(by2){
				 axin /=20;
			 }else{
				 axin /=10;
			 }
			while(xa<xmax){
	       		if(xa>=xmin){
		       			g.drawLine(getX(xa), (int)h-shiftTop, getX(xa), (int)h-shiftTop-4);
		       			g.drawLine(getX(xa), shiftTop, getX(xa), shiftTop+4);
	        	}
	       		xa += axin;
	        }
		
 
		// Beschriftung der Achsen
        Font oldFont = g.getFont();
		Font f = oldFont.deriveFont(AffineTransform.getRotateInstance(-Math.PI / 2.0));
		g.setFont(f);
		
		fm = this.getGraphics().getFontMetrics(f);
		int wy = fm.stringWidth(yLabel);
		
		g.drawString(yLabel , 20,(hg/2+wy/2));
		g.setFont(oldFont);
		
		fm = this.getGraphics().getFontMetrics(oldFont);
		wy = fm.stringWidth(xLabel);
		g.drawString(xLabel , (this.getWidth()/2-wy/2),hg-5);
		fm = this.getGraphics().getFontMetrics(oldFont);
		wy = fm.stringWidth(title);
		g.drawString(title , (this.getWidth()/2-wy/2),45);
		// #0 02/05/2014 07:33: 
	
		
		
		//g.drawString("Scaling Freeze: "+freezeX0, 5, 15);
		
		
	}
	
	 private double getAxInc(double min, double max){
		  
		   double erg = Math.pow(10,Math.floor(Math.log10(Math.abs((max-min)))));
		  //if(erg/Math.abs(max-min)<2)erg=erg/2;
		   return erg;
	   }

	 	private Double getValFromX(int x){
	 		Double erg=(double) x;
	 		erg -= shiftLeft;
	 		erg /= (this.getWidth()-1*shiftLeft-5);
	 		erg *= (xmax-xmin);
	 		erg += xmin;
	 		return erg;
	 	}
	 	private Double getValFromY(int y){
	 		Double erg=(double) y;
	 		erg += shiftTop;
	 		erg = this.getHeight() - erg; 
	 		erg /= this.getHeight()-2*shiftTop;
	 		erg *= (Ymax-Ymin);
	 		erg += Ymin;
	 		return erg;
	 	}
	   private int getX(double w){
	    	return (int) (((w-xmin)/(xmax-xmin))*(this.getWidth()-1*shiftLeft-5))+shiftLeft;
	    }
	   
	   private int getY(double w){
		   
	    	return (int)(this.getHeight()-shiftTop- ((w-Ymin)/(Ymax-Ymin))*(this.getHeight()-2*shiftTop));
	    }
	
	   public double myRound(float wert,int digits){
			double fak = Math.pow(10, digits);
			return Math.round(wert * fak)/fak;
		}
	  
	   
	
}  

class SP_PlotData{
	
	boolean dots3D = true;
	boolean showDots = true;
	boolean showLines = true;
	int dotSize = 8;
	float[] ydat = null;
	float[] xdat = null;
	boolean[] ok = null;
	
	
	Color col = Color.white;
	Color[] cols = null;
	String label;

	public SP_PlotData(float[] Xdat,float[] Ydat, boolean[] OK, int dS ,Color Col, String Label, boolean ShowDots, boolean ShowLines, boolean Dots3D){
		xdat = Xdat;
		ydat = Ydat;
		ok = OK;
		dotSize = dS;
		col = Col;
		label = Label;
		dots3D = Dots3D;
		showLines = ShowLines;
		showDots = ShowDots;
	}
	public SP_PlotData(float[] Xdat,float[] Ydat, int dS ,Color[] Cols, String Label, boolean ShowDots, boolean ShowLines, boolean Dots3D){
		xdat = Xdat;
		ydat = Ydat;
		ok = new boolean [Xdat.length];
		for(int i=0;i<Xdat.length;i++){
			ok[i] = true;
		}
		dotSize = dS;
		cols = Cols;
		label = Label;
		dots3D = Dots3D;
		showLines = ShowLines;
		showDots = ShowDots;
	}
	public SP_PlotData(float[] Xdat,float[] Ydat, int dS ,Color Col, String Label, boolean ShowDots, boolean ShowLines, boolean Dots3D){
		xdat = Xdat;
		ydat = Ydat;
		ok = new boolean [Xdat.length];
		for(int i=0;i<Xdat.length;i++){
			ok[i] = true;
		}
		dotSize = dS;
		col = Col;
		label = Label;
		dots3D = Dots3D;
		showLines = ShowLines;
		showDots = ShowDots;
	}
	
	public int getRows(){
		return xdat.length;
	}

}
