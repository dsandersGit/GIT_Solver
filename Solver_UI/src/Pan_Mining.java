import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;

public class Pan_Mining extends JComponent {
	
	/**
	 * Raw Data Mining Canvas > plots feature on timeindex (if available) or sequence
	 * Aim is to identify and deselect features carrying gradient / step which result
	 * from measurement gradients or uncontrolled bias.
	 * - Deselected features will be omitted (vector = 0) in Runner
	 * - Deselected features will be omitted when saving data
	 * Selection by checbox or doubleclick on plot
	 */
	private static final long serialVersionUID = 1L;
	ImageIcon icon_SelAll = new ImageIcon(ClassLoader.getSystemResource("selectAll.png"));
	ImageIcon icon_SelNone = new ImageIcon(ClassLoader.getSystemResource("deselectAll.png"));
	ImageIcon icon_Up = new ImageIcon(ClassLoader.getSystemResource("up.png"));
	ImageIcon icon_Down = new ImageIcon(ClassLoader.getSystemResource("down.png"));
	
	public static 		SP_MiningCanvas scatterPlot 			= new SP_MiningCanvas();
	public static int area = 0;
	JButton jb_up 					= new JButton(icon_Up);
	JButton jb_down 				= new JButton(icon_Down);
	
	JButton jb_selAll 				= new JButton(icon_SelAll);
	JButton jb_selNone 				= new JButton(icon_SelNone);
	
	static JProgressBar pro_area	= new JProgressBar(0, 100);
	static JCheckBox chkActive 		= new JCheckBox("active area");
	static JCheckBox chkTimeIndex	= new JCheckBox("use TimeIndex",true);
	
	public Pan_Mining() {
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		JPanel pan_Label = new JPanel();
		pan_Label.setLayout(new FlowLayout(FlowLayout.LEFT));
		pan_Label.setBorder(BorderFactory.createTitledBorder("WHAT and WHY"));
		String info = "<HTML>"
				+ "<B>Visual Mining for potential gradients / steps that might indicate underlying bias.</B>"
				+ " Bias will weaken the classification model. Untargeted classification using complex analytics or drifting sensors is prone to such bias when classes distribution is not randomized/alternating."
				+ "<BR>Move through feature's scatter plot vs. time/sample_sequence using up/down or mouse wheel, seek for gradients and steps related to time or sequence. "
				+ "(De-)Activate by checkbox or double click on plot > Deactivated Features are omitted in Training and Saving of data sets.<BR>"
				+ "</HTML>";
		pan_Label.add(new JLabel(info));
		this.add(pan_Label);
		
		scatterPlot.setPreferredSize(new Dimension(600,600));
		add(scatterPlot);
		JPanel pan_upDown = new JPanel();
		pan_upDown.setLayout(new FlowLayout(FlowLayout.LEFT));
		pan_upDown.add(pro_area);
		pan_upDown.add(jb_up);
		pan_upDown.add(jb_down);
		pan_upDown.add(jb_selAll);
		pan_upDown.add(jb_selNone);
		pan_upDown.add(chkTimeIndex);
		pan_upDown.add(chkActive);
		
		
		jb_up.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			if ( area<DS.numVars-1) {
				area ++;
			}else {
				area =0;
			}
			plot();
		}});
		
		jb_down.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			if ( area > 0) { 
				area--;
			}else {
				area=DS.numVars-1;
			}
			plot();
		}});
		jb_selAll.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			for (int i=0;i<DS.numVars;i++) {
				DS.selectedArea[i] = true;	
			}
			plot();
		}});
		jb_selNone.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			for (int i=0;i<DS.numVars;i++) {
				DS.selectedArea[i] = false;	
			}
			plot();
		}});
		chkActive.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
			DS.selectedArea[area] = chkActive.isSelected();
			plot();
		}});
		chkTimeIndex.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){	
			if (DS.timeIndex == null)chkTimeIndex.setSelected(false);
			plot();
		}});
		pro_area.addMouseListener( 
	        	new MouseListener() {
    		        public void mouseClicked(MouseEvent e){
    		        	if (e.getClickCount() > 1) {
    		        		area = 0;
    		        		plot();
    		        	}
    		        }
    		        public void mouseEntered(MouseEvent e){        }
    		        public void mouseExited(MouseEvent e){       }
    		        public void mousePressed(MouseEvent e){		        }
    		        public void mouseReleased(MouseEvent e){        }
        	}
	     );

		this.add(pan_upDown);
		
	
		this.setVisible(true);
		
		this.addMouseWheelListener (new MouseWheelListener() {
			 public void mouseWheelMoved(MouseWheelEvent e){
				 if ( e.getWheelRotation() > 0) {
					 if ( area<DS.numVars-1) {
							area ++;
						}else {
							area =0;
						}
				 }else {
					 if ( area > 0) { 
							area--;
						}else {
							area=DS.numVars-1;
						}
				 }
				 plot();
		     }
		});
	}

	public static void init() {
		DS.selectedArea = new boolean[DS.numVars];
		for (int i=0;i<DS.numVars;i++) {
			DS.selectedArea[i] = true;	
		}
		area = 0;
	
		pro_area.setStringPainted(true);
		if (DS.timeIndex == null) {
			chkTimeIndex.setSelected(false);
			chkTimeIndex.setEnabled(false);
		}else {
			chkTimeIndex.setSelected(true);
			chkTimeIndex.setEnabled(true);
		}
		plot();
	}
	public static void plot() {
		
		MultiVariate_R.pcaExport = null;
		
		pro_area.setValue(100*area/DS.numVars);
		pro_area.setString("pos: "+(100*area/DS.numVars)+"%");
		
		chkActive.setSelected( DS.selectedArea[area] );

	
		if ( area < 0 )area = 0;
		if ( area > DS.numVars-1 )area = 0;
		 scatterPlot.dats.clear();
		 Color cn;
		 scatterPlot.setYAxis(DS.AreaNames[area]);
		 scatterPlot.setXAxis("Sequence");
		 if (DS.timeIndex != null && chkTimeIndex.isSelected()) scatterPlot.setXAxis("TimeIndex"); 

		 if ( DS.numSamples < 1000) {
			 // Sort
			 ArrayList<Float> 	sortDataX 	= new ArrayList<Float>();									// SORTING
	    	 ArrayList<Integer> sortDataPosX 	= new ArrayList<Integer>();
	    	 for (int i=0;i<DS.numSamples; i++) {
	    		 float data  = (float)(i);
	    		 if (DS.timeIndex != null && chkTimeIndex.isSelected()) data = (float)DS.timeIndex[i];
	         	 boolean isIn = false;
	             for(int j=0;j<sortDataX.size();j++){									
	                 if ( sortDataX.get(j)>=data) {
	                     sortDataX.add	(j, data);
	                     sortDataPosX.add(j, i);
	                     isIn = true;
	                     break;
	                 }
	             }
	             if ( !isIn) {
	                 sortDataX.add(data);
	                 sortDataPosX.add(i);
	             }
	    	 }
	    	 double min = 0;
	    	 double max = 0;
	    	 ArrayList<Float> 	sortDataY 	= new ArrayList<Float>();									// SORTING
	    	 ArrayList<Integer> sortDataPosY 	= new ArrayList<Integer>();
	    	 for (int i=0;i<DS.numSamples; i++) {
	    		 float data  = (float)DS.rawData[i][area];
	    		 boolean isIn = false;
	             for(int j=0;j<sortDataY.size();j++){									
	                 if ( sortDataY.get(j)>=data) {
	                     sortDataY.add	(j, data);
	                     sortDataPosY.add(j, i);
	                     isIn = true;
	                     break;
	                 }
	             }
	             if ( !isIn) {
	                 sortDataY.add(data);
	                 sortDataPosY.add(i);
	             }
	             if ( i==0 | min > data) min = data;
	             if ( i==0 | max < data) max = data;
	    	 }
	    	 

			
	    	 float[] 	avgX 	= new float[DS.numSamples];								// Rolling Average
	    	 float[] 	avgY 	= new float[DS.numSamples];
	    	 for (int i=0;i<DS.numSamples; i++) {
	    		 int sortPos = sortDataPosX.get(i);
	    		 int c=0;
	    		 for (int t=i-3;t<i+4;t++) {
	    			 if ( t>-1 && t<DS.numSamples) {
			    		 if (DS.timeIndex != null && chkTimeIndex.isSelected()) {
			    			 avgX[i] += sortDataX.get(t);
			    		 }else {
			    			 avgX[i] += t;
			    		 }
			    		 c++;
	    			 }
	    		 }
	    		 if (c>0)avgX[i] /= c;
	    		 c=0;;
	    		 for (int t=i-3;t<i+4;t++) {
	    			 if ( t>-1 &&t<DS.numSamples) {
		    			 avgY[i] += DS.rawData[sortDataPosX.get(t)][area];
			    		 c++;
	    			 }
	    		 }
	    		 if (c>0)avgY[i] /= c;
	    	 }	 
	    	 scatterPlot.setXY(avgX, avgY, 12, Color.BLACK, "...",0,0, false, true, false);
	    	 scatterPlot.refreshPlot();
	    	 
		 }	// max 1000
		 
    	 

    	 // Dots
		 double max = 0;double min = 0; 
		 for (int c=0;c<DS.numClasses;c++) {
			 float[] x = new float[DS.classAllIndPop[c]];
			 float[] y = new float[DS.classAllIndPop[c]];
			 int cnt = 0;
			 
			 for (int i=0;i<DS.numSamples; i++) {
				 if ( max < DS.rawData[i][area]) max = DS.rawData[i][area];
				 if ( min > DS.rawData[i][area] || i==0) min = DS.rawData[i][area];
				 if ( DS.classIndex[i]== DS.classAllIndices[c]) {
					 if (DS.timeIndex == null || !chkTimeIndex.isSelected()) {
						 x[cnt] = (float)i;
					 }else {
						 x[cnt] = (float)DS.timeIndex[i];
					 }
					 y[cnt] = (float)DS.rawData[i][area];
					 cnt++;	 
				 }
			 }
			 cn = Tools.getClassColor(Classify.getTargetColorIndexPos(DS.classAllIndices[c]));
			 double[] avg_atd = Tools.calculateSD(y);
			 int start = (int)(100*(avg_atd[0]-avg_atd[1]-min)/(max-min));
			 int stop = (int)(100*(avg_atd[0]+avg_atd[1]-min)/(max-min));
			 scatterPlot.setXY(x, y, 12, cn, DS.classAllIndNme[c],start,stop, true, false, false);
			 
		 } 

    	 
//    	 
//    	 // HISTOGRAM
//    	 float[][] histo = new float[DS.numClasses][100];
//    	 for (int f=0;f<DS.numSamples; f++) {
//    		 int 		cls 	= Classify.getTargetColorIndexPos(DS.classIndex[sortDataPosY.get(f)]);
//    		 int 		val	 	= (int) ((100* (sortDataY.get(f)-min))/(max-min));
//             if ( val > -1 && val <100) {
//            	 histo[cls][val]+=10;
//             }
//             if ( val-1 > -1 && val <100) {
//            	 histo[cls][val-1]+=3;
//             }
//             if ( val-2 > -1 && val <100) {
//            	 histo[cls][val-2]+=1;
//             }
//             if ( val > -1 && val+1 <100) {
//            	 histo[cls][val+1]+=3;
//             }
//             if ( val > -1 && val+2 <100) {
//            	 histo[cls][val+2]+=1;
//             }
//    	 }
//    	 float[] hx = new float[100];
//    	 for (float f=0;f<100; f++) {
//    		 hx[(int)f] = (float) ((f/100.) * (max-min)+min);
//    	 }
//    	 for (int c=0;c<DS.numClasses;c++) {
//    		 cn = Tools.getClassColor(Classify.getTargetColorIndexPos(DS.classAllIndices[c]));
//    		 scatterPlot.setXY( histo[c],hx, 12, cn, DS.classAllIndNme[c], false, true, false);
//    	 }
//    	 
//  
//		
		 
		 scatterPlot.setBaseColor(Color.black);
		 scatterPlot.setOuterBackGroundColor(Color.WHITE);
		 if ( !DS.selectedArea[area] ) {
			 scatterPlot.setBaseColor(Color.RED);
			 scatterPlot.setOuterBackGroundColor(Color.LIGHT_GRAY);
		 }
		 scatterPlot.refreshPlot();
		 
	}
}

class SP_MiningCanvas extends JPanel{
	
	
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
	

	private double dragXalt = -1;
	private double dragYalt = -1;
	
	int shiftLeft = 150;
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

	
	ArrayList<Double> storeValX = new ArrayList<Double>();
	ArrayList<Double> storeValY = new ArrayList<Double>();
	
	 
	SP_MiningCanvas(){
	
		//ClassLoader.getSystemClassLoader();
		//icon_lock = new ImageIcon(ClassLoader.getSystemResource("icon_lock.png")).getImage();
		
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		toolkit.getScreenSize();
		
		this.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		
		this.addMouseListener( 
	        	new MouseListener() {
	    		        public void mouseClicked(MouseEvent e){
	    		        	simpleSelX0 = e.getX();
							 simpleSelX1 = e.getX();
							 simpleSelY0 = e.getY();
							 simpleSelY1 = e.getY();
							 if (e.getClickCount()>1) {
								 if ( DS.selectedArea[Pan_Mining.area] ) {
									 DS.selectedArea[Pan_Mining.area]=false;	 
								 }else{
									 DS.selectedArea[Pan_Mining.area]=true;
								 }
								 Pan_Mining.plot();
							 }
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
					                 	}else{
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
	        public void mouseMoved(MouseEvent e){}
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
	public void setXY(float[] x, float[] y, int size,Color col,String name, int labstart, int labstop,boolean ShowDots, boolean ShowLines, boolean Dots3D){
		dats.add(new SP_PlotData(x,y, size,col,name, labstart, labstop,ShowDots,ShowLines,Dots3D));
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
					if(xmin > pd.xdat[x] || !hasxMin) {	xmin = pd.xdat[x];hasxMin = true;}
					if(xmax < pd.xdat[x] || !hasxMax) {	xmax = pd.xdat[x];hasxMax = true;}
					if(Ymin > pd.ydat[x] || !hasyMin) { Ymin = pd.ydat[x];hasyMin = true;}
					if(Ymax < pd.ydat[x] || !hasyMax) {	Ymax = pd.ydat[x];hasyMax = true;}
				}
			}
		}
		
		Ymin=(float) (Ymin - (Ymax-Ymin)*0.1);
		Ymax=(float) (Ymax + (Ymax-Ymin)*0.1);
		xmin=(float) (xmin - (xmax-xmin)*0.15); 
		
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
		
	
//		int high
	
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
							if ( pd.dotSize > 6 ) g.drawOval(getX(pd.xdat[x])-pd.dotSize/2, getY(pd.ydat[x])-pd.dotSize/2, pd.dotSize, pd.dotSize);
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
		Stroke strokeOld = g2.getStroke();
		Stroke stroke = new BasicStroke(1,	    BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,  	    new float[] { 4, 4 }, 0);
		
		  for(int i = 0;i<dats.size();i++){
			  SP_PlotData pd = dats.get(i);
			  if(pd.showLines){
				  g.setColor(pd.col);
				  if ( pd.label.equals("...")) {
					  g2.setStroke(stroke);
				  }else {
					  g2.setStroke(strokeOld);
				  }
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
		  g2.setStroke(strokeOld);
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
	  		 	g.fill3DRect(shiftLeft+8+pd.labStart, this.getHeight()-(int)h+(c*(height+3))+shiftTop+9, pd.labStop-pd.labStart, height, true);
				g.setColor(baseCol);
//				g.drawString(pd.label, (int)w+shiftLeft+8+height+5, this.getHeight()-(int)h+(i*(height+3))+shiftTop+height+6);
				g.drawString(pd.label, shiftLeft+8+ pd.labStop+5, this.getHeight()-(int)h+(c*(height+3))+shiftTop+height+6);
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

