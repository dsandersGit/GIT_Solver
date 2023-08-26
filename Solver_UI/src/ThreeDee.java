import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class ThreeDee extends JPanel{
	
	/**
	* ThreeDee.java is a universal 3D Scatter Plotter  
	* that runs in a JPanel
	*
	* @author  Daniel Sanders
	* @version 1.0
	* @since   2019-12-01 
	*/
	
	private static final long serialVersionUID = 1L;
	static Color genBackColor = Color.white;
	static Color genFrontColor = Color.DARK_GRAY;

	private String title="";
	
	
	private ArrayList<Integer> DDLineX = new ArrayList<Integer>();
	private ArrayList<Integer> DDLineY = new ArrayList<Integer>();
	private ArrayList<Integer> DDLineZ = new ArrayList<Integer>();
	private ArrayList<Boolean> DDLineS = new ArrayList<Boolean>();
	private ArrayList<Integer> DDLineColR = new ArrayList<Integer>();
	private ArrayList<Integer> DDLineColG = new ArrayList<Integer>();
	private ArrayList<Integer> DDLineColB = new ArrayList<Integer>();

	private ArrayList<Integer> DDPreLineX = new ArrayList<Integer>();
	private ArrayList<Integer> DDPreLineY = new ArrayList<Integer>();
	private ArrayList<Integer> DDPreLineZ = new ArrayList<Integer>();
	private ArrayList<Boolean> DDPreLineS = new ArrayList<Boolean>();
	private ArrayList<Integer> DDPreLineColR = new ArrayList<Integer>();
	private ArrayList<Integer> DDPreLineColG = new ArrayList<Integer>();
	private ArrayList<Integer> DDPreLineColB = new ArrayList<Integer>();
	
	private ArrayList<Integer> DDCircleX = new ArrayList<Integer>();
	private ArrayList<Integer> DDCircleY = new ArrayList<Integer>();
	private ArrayList<Integer> DDCircleZ = new ArrayList<Integer>();
	private ArrayList<Integer> DDCircler = new ArrayList<Integer>();
	private ArrayList<Color> DDCircleCol = new ArrayList<Color>();
	private ArrayList<String> DDCircleLabel = new ArrayList<String>();
	
	private ArrayList<Integer> DDCubeX = new ArrayList<Integer>();
	private ArrayList<Integer> DDCubeY = new ArrayList<Integer>();
	private ArrayList<Integer> DDCubeZ = new ArrayList<Integer>();
	private ArrayList<Integer> DDCuber = new ArrayList<Integer>();
	private ArrayList<Color> DDCubeCol = new ArrayList<Color>();
	private ArrayList<String> DDCubeLabel = new ArrayList<String>();
	
	private ArrayList<Integer> DDTextX = new ArrayList<Integer>();
	private ArrayList<Integer> DDTextY = new ArrayList<Integer>();
	private ArrayList<Integer> DDTextZ = new ArrayList<Integer>();
	private ArrayList<String> DDTextText = new ArrayList<String>();
	
	private ArrayList<Integer> 	DD2DTextX = new ArrayList<Integer>();
	private ArrayList<Integer> 	DD2DTextY = new ArrayList<Integer>();
	private ArrayList<String> 	DD2DTextText = new ArrayList<String>();
	
	private ArrayList<BufferedImage> 	DDLegImg = new ArrayList<BufferedImage>();
	
	static final double pi = 3.14159265;
	
	static final double yAxisX = 0;
	static final double yAxisY0 = 100000;
	static final double yAxisY1 = 100000;
	static final double yAxisZ = 0;
	

	 double alpha=0;
	 double beta=0, gamma=0;
	
	private int xAlt, yAlt;
   	private int ScreenXAlt,ScreenYAlt;
	
  	 int zPlus=0;
  	 int xPlus=0;
  	 int yPlus=0;
  	//private static double zFak=1;
  	double cFak=1;
  	double faktor=0.25;
  	
	private  int mousePosX = 0;
	private  int mousePosY = 0;
	private  int mousePosX0 = 0;
	private  int mousePosY0 = 0;
	private  int blueBoxX0 = -1;
	private  int blueBoxX1 = -1;
	private  int blueBoxY0 = -1;
	private  int blueBoxY1 = -1;
//	
//	private int lastClickX = 0;
//	private int lastClickY = 0;
	
	private boolean isDragging=false;

	public ThreeDee() {
		
		alpha=0.325;
		beta=0.566;
		gamma=pi;
		
		this.addMouseListener ( 
	    	new MouseListener() {

	        public void mouseClicked(MouseEvent e){
	         	mousePosX = -1;
	    		mousePosY = -1;
	    		mousePosX0 = -1;
	    		mousePosY0 = -1;
	        	isDragging=false;
	        	if (e.getClickCount() == 2) {
	        		alpha=0.325;
	        		beta=0.566;
	        		gamma=pi;
	        	  	zPlus=0;
	        	  	xPlus=0;
	        	  	yPlus=0;
        		}
//	        	if ( blueBoxX0 > 0) {
//	        		
//	        		if ( blueBoxX0 < e.getX() && blueBoxX1 > e.getX() )
//	        			if ( blueBoxY0 < e.getY() && blueBoxY1 > e.getY() ) {
//	        			    String erg = "";
//	        			    for (int i=0;i<DDCubeX.size();i++){
//	        			    	
//		        			    if (ds.files.get(PerformPCA.whoAreYou[i]).minerSelected ) {
//		        			    	erg += ds.files.get(PerformPCA.whoAreYou[i]).headerLabel+"\t["+ds.files.get(PerformPCA.whoAreYou[i]).LAVClassName+"]\n";
//
//					   			}
//	        			    }
//				        	Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//				    	    StringSelection stringSelection = new StringSelection( erg );
//				    	    clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//				    	    clipboard.setContents( stringSelection, null );
//	        			}
//	        	}
	        }
	        public void mouseEntered(MouseEvent e)	{ 
	        	mousePosX = -1;
	    		mousePosY = -1;
	    		mousePosX0 = -1;
	    		mousePosY0 = -1;      }
	        public void mouseExited(MouseEvent e)	{ 
	        	mousePosX = -1;
	    		mousePosY = -1;
	    		mousePosX0 = -1;
	    		mousePosY0 = -1;
	        }
	        public void mousePressed(MouseEvent e){
			    ScreenXAlt = e.getX();
			    ScreenYAlt = e.getY();
			    mousePosX0 = e.getX();
				mousePosY0 = e.getY();
				if ( e.isShiftDown())											// Löschen Markierung
//				for(int i=0;i<DynaminerStart.rawData.length;i++){
//					ds.files.get(i).minerSelected = false;
//				}
				mousePosX = -1;
	    		mousePosY = -1;
	        }		        
	        public void mouseReleased(MouseEvent e){
	        	isDragging=false;
	   
	        	repaint();
	        }
		        		        		        		      
     }  
	    
  );   
	this.addMouseMotionListener
	       ( 
 	new MouseMotionListener() { 
  		public void mouseDragged(MouseEvent e){
				if (e.isShiftDown()) {
		  			mousePosX = e.getX();
		  			mousePosY = e.getY();
				}else{
					if (e.isAltDown()) {
//						zPlus+=(ScreenYAlt-e.getY())*0.1;
						//gamma+=(-ScreenYAlt+e.getY())*(pi/getSize().height)*.1;
					}else{
						if (e.isAltGraphDown()){
						}else{

						    if (e.isControlDown()) {
						        //gamma+=(ScreenXAlt-e.getX())*(pi/getSize().width)*.1;   
						        xPlus-=(ScreenXAlt-e.getX())*0.1;
						        yPlus-=(ScreenYAlt-e.getY())*0.1;
						        
						    }
						    if (!e.isControlDown()) {
						    	beta-=(-ScreenXAlt+e.getX())*(pi/getSize().width)*.1;
								
								alpha+=(-ScreenYAlt+e.getY())*(pi/getSize().height)*.1;
								
						    }		
						}							    
					}    
				}	
			
				isDragging=true;
				repaint();
	        }
  		public void mouseMoved(MouseEvent e){

	        }		        
     }  
  );   	     
	this.addMouseWheelListener (new MouseWheelListener() {
     public void mouseWheelMoved(MouseWheelEvent e){

     	// Zoomen Z
     	if (!e.isAltGraphDown() && !e.isAltDown() && !e.isControlDown() && e.isShiftDown()) {
     		//zFak = 1;
     		cFak=cFak+cFak*0.05*e.getWheelRotation();
     	}
     	// Zoomen Full
     	if (!e.isAltGraphDown() && !e.isAltDown() && !e.isControlDown() && !e.isShiftDown()) {
     		faktor=faktor+(faktor*0.005*(e.getWheelRotation()));
     	}
     	repaint();
     }
     	
    }   
	);
	}
	protected void paintComponent( Graphics g )
	{
		 super.paintComponent(g);
		 
		 doDraw(g);
		 
		 g.dispose();
	}
	
	 /**
	   * Returns virtual xyz coordinates for plotting 
	   * @param sin_X Sinus of first rotation angle (aka alpha)
	   * @param cos_X  Cosinus of first rotation angle (aka alpha)
	   * @param sin_Y  Sinus of second rotation angle (aka beta)
	   * @param cos_Y  Cosinus of second rotation angle (aka beta)
	   * @return int[] This returns the 0-x, 1-y, 2-z coordinates in an array
	   */
	private int[] getXYZ(double sin_X, double cos_X, double sin_Y, double cos_Y, int x, int y, int z){

		
		x += xPlus;
		y += yPlus;
		z += zPlus;
		
		int[] erg = new int[3];	
		double x0=	x;		

    	// Y
			double x1 = x0 * cos_Y - z * sin_Y;
			double z1 = z * cos_Y + x0 * sin_Y;
			// X
			double y2 = y * cos_X - z1 * sin_X;
			double z2 = z1 * cos_X + y * sin_X;

			erg[0] = (int) (faktor * x1) + getSize().width/2;
			// Invertieren
			//erg[1] = (int) (faktor * y2) + getSize().height/2;
			erg[1] = getSize().height/2 - (int) (faktor * y2) ;
			erg[2] = (int) (faktor * z2);
			
			return erg;
	}

	/**
	   * Ploting functions
	   * @param Graphics JPanel Graphics
	   */
	private void doDraw(Graphics g) {
		 //setParas();
		Graphics2D g2 = (Graphics2D) g; 		
		
		if(!isDragging)
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.setColor(genBackColor);
	    g.fillRect(0,0,this.getSize().width,this.getSize().height);
	    g.setColor(genFrontColor);
	    
	    //Legende
	    int ih = 25;
	    for (int i=0;i<DDLegImg.size();i++) {
	    	g.drawImage(DDLegImg.get(i), 5, ih , null);
	    	ih += DDLegImg.get(i).getHeight()+2;
	    }
	    
	    // Markierung
	    Stroke stroke = new BasicStroke(1,
	    	    BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
	    	    new float[] { 4, 4 }, 0);
	    if (mousePosX>0 && mousePosX-mousePosX0>0 ) {
			g.setColor(genFrontColor);
			g.drawRect(mousePosX0,mousePosY0,mousePosX-mousePosX0,mousePosY-mousePosY0);
			  
			g2.setStroke(stroke);
			g.setColor(Color.orange);
			g.drawRect(mousePosX0,mousePosY0,mousePosX-mousePosX0,mousePosY-mousePosY0);
	    }

	   
   		double sin_X = Math.sin(alpha);
	    double cos_X = Math.cos(alpha);
	    double sin_Y = Math.sin(beta);
	    double cos_Y = Math.cos(beta);
  		int xNeu, yNeu, zNeu;
  		int green, red, blue;
  		//Polygon p = new Polygon();  
				//Stroke stroke = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,       new float[] { 2, 1 }, 0);
			stroke = new BasicStroke((float) 2);
			    g2.setStroke(stroke);
			    
			for (int w=0;w<DDPreLineX.size();w++){	
			
	   			int[] xyz = getXYZ(sin_X,cos_X, sin_Y,cos_Y,  DDPreLineX.get(w),  DDPreLineY.get(w), DDPreLineZ.get(w));
		    	 xNeu=xyz[0];
	   			 yNeu=xyz[1];
	   			 zNeu=xyz[2];
	   			
	   			 red=DDPreLineColR.get(w);
				if(red>255)red=255;
				if(red<0)red=0;
				 green=DDPreLineColG.get(w);
				if(green>255)green=255;
				if(green<0)green=0;
				 blue=DDPreLineColB.get(w);
				if(blue>255)blue=255;
				if(blue<0)blue=0;
				g.setColor(new Color(red,green,blue));
 				
				if (DDPreLineS.get(w)){
		   			if (w>0){
		   				g.drawLine(xAlt,yAlt,xNeu,yNeu);
		   			}  
	   			}   		
				g.setColor(genFrontColor);
				
			xAlt=xNeu;yAlt=yNeu;
			
			}	
	
		
		stroke = new BasicStroke((float) 1);
		g2.setStroke(stroke);
  			
	
			g.setColor(genFrontColor);
//
//
//			g.fillArc( 10, 200, 20, 20, 0, (int)(alpha*180/3.1416) );
//			g.fillArc( 30, 200, 20, 20, 0, (int)(beta*180/3.1416) );
//			g.fillArc( 50, 200, 20, 20, 0, (int)(gamma*180/3.1416) );
//			
//			g.drawString("ALPHA: "+alpha,10,160);			
//			g.drawString("BETA: "+beta,10,175);
//			g.drawString("GAMMA: "+gamma,10,190);
//			g.drawString("FACTOR: "+faktor,10,105);
//			g.drawString("zFak: "+zFak,10,120);
//			g.drawString("xPlus: "+xPlus,10,135);
//			g.drawString("yPlus: "+yPlus,10,150);
//			g.drawString("zPlus: "+zPlus,10,165);

			
		
		if(isDragging){
			int l = this.getHeight()-15;
			g.drawString("DRAG: Rotate",10,l);l-=15;
			g.drawString("DoubleClick: Reset View",10,l);l-=15;
			g.drawString("<CTRL> + DRAG: Shift",10,l);l-=15;
			g.drawString("<SHIFT> + DRAG: Select",10,l);l-=15;
			g.drawString("MouseWheel: Zoom Scale",10,l);l-=15;
			g.drawString("<SHIFT> + MouseWheel: Zoom Diameter",10,l);l-=15;
			
			g.fillArc( this.getWidth()-60, this.getHeight()-25, 20, 20, 0, (int)(alpha*180/3.1416) );
			g.fillArc( this.getWidth()-40, this.getHeight()-25, 20, 20, 0, (int)(beta*180/3.1416) );
			g.fillArc( this.getWidth()-20, this.getHeight()-25, 20, 20, 0, (int)(gamma*180/3.1416) );
//			
//			g.setColor(Color.BLACK);
//			for (int i=0;i<DDCubeX.size();i++){	
//
//	   			int[] xyz = getXYZ(sin_X,cos_X, sin_Y,cos_Y,  DDCubeX.get(i),  DDCubeY.get(i), DDCubeZ.get(i));
//		    	 xNeu=xyz[0]+getSize().width/2;
//	   			 yNeu=xyz[1]+getSize().height/2;
//	   			 zNeu=xyz[2];
//	   			 if ( xNeu > 0 && xNeu<  getSize().width)
//	   				if ( yNeu > 0 && yNeu <  getSize().height) {
//	   					g.drawLine(xNeu,yNeu,xNeu,yNeu);
//	   				}
//			}	
//			return;
		}
		 
		 // CUBES
		 
		 /**/
		
		// SORT Balls
		ArrayList<Integer>sort = new ArrayList<Integer>();
		//if (plotter.menuSettingsChkSort3D.isSelected()) {
		if (true) {			
			ArrayList<Integer>sortVal = new ArrayList<Integer>();
			for (int w=0;w<DDCubeX.size();w++){
				int[] xyz = getXYZ(sin_X,cos_X, sin_Y,cos_Y,  DDCubeX.get(w),  DDCubeY.get(w), DDCubeZ.get(w));
//		    	int xNeu=xyz[0];
//	   			int yNeu=xyz[1];
	   			 zNeu=xyz[2];
	   			boolean isIn = false;
	   			for (int j=0;j<sortVal.size();j++) {
	   				if ( sortVal.get(j) > zNeu ) {
	   					sort.add(j,w);
	   					sortVal.add(j,zNeu);
	   					isIn = true;
	   					break;
	   				}
	   			}
	   			if ( !isIn ) {
	   				sort.add(w);
	   				sortVal.add(zNeu);
	   			}
	   			
			}
		}
		ArrayList<String>res = new ArrayList<String>();
				for (int i=0;i<DDCubeX.size();i++){	

					int w = i;
					if (sort.size()>0) w = sort.get(i);
					
		   			int[] xyz = getXYZ(sin_X,cos_X, sin_Y,cos_Y,  DDCubeX.get(w),  DDCubeY.get(w), DDCubeZ.get(w));
			    	 xNeu=xyz[0];
		   			 yNeu=xyz[1];
		   			 zNeu=xyz[2];
		   			g.setColor(DDCubeCol.get(w));
		   			
		   			int r = (int)(DDCuber.get(w)*faktor*cFak);//+(int)(zNeu*faktor/20.);
//		   			while ( r < 6) {
//		   				cFak *=2;
//		   				r = (int)(DDCuber.get(w)*faktor*cFak);
//		   			}
		   			
		   			
		   			//if ( r > 200) r = 200;
		   			g.fillRect(xNeu-r/2,yNeu-r/2,r,r);
		   			g.setColor(genFrontColor);
		   			g.drawRect(xNeu-r/2,yNeu-r/2,r,r);

		   			
		   			// Selektieren
		   			//ds.files.get(PerformPCA.whoAreYou[w]).minerSelected = false;
//		   			if ( xNeu > mousePosX0 &&  xNeu < mousePosX)
//		   				if ( yNeu > mousePosY0 &&  yNeu < mousePosY) {
//		   					//g.drawString(DDCubeLabel.get(w),xNeu+getSize().width/2 + 10,yNeu+getSize().height/2 + 10);
//		   					//res.add(DDCubeLabel.get(w));
//		   					res.add(ds.files.get(PerformPCA.whoAreYou[w]).LAVClassName);
//		   					ds.files.get(PerformPCA.whoAreYou[w]).minerSelected = true;
//		   				}
//		   			// Selektiert
//		   			if (plotBuilder.doHighLight)
//			   			if (ds.files.get(PerformPCA.whoAreYou[w]).minerSelected ) {
////			   				int zufiX = (int) (Math.random()*20);
////			   				if (i%2 == 0 ) {
////			   					zufiX *= -1;
////			   					zufiX -= g.getFontMetrics(getFont()).stringWidth(DDCubeLabel.get(w));
////			   				}
////			   				int zufiY = (int) (Math.random()*15);
//			   				if ( DDCubeLabel.get(w) != null ) g.drawString(DDCubeLabel.get(w), xNeu-r,yNeu-r-5);
////			   				g.drawLine( xNeu-r,yNeu-r-5,xNeu-r+zufiX,yNeu-r-5-zufiY);
//			   				g.setColor(Color.blue);
//			   				g.drawRect(xNeu-r,yNeu-r,r*2,r*2);
//			   				
//			   			}
				}	   	
		

			
			for (int w=0;w<DDCircleX.size();w++){
	   		
	   			int[] xyz = getXYZ(sin_X,cos_X, sin_Y,cos_Y,  DDCircleX.get(w),  DDCircleY.get(w), DDCircleZ.get(w));
		    	 xNeu=xyz[0];
	   			 yNeu=xyz[1];
	   			 zNeu=xyz[2];
	   			
	   			g.setColor(DDCircleCol.get(w));
	   			int r = (int)(DDCircler.get(w)*faktor*cFak);
	   			g.fillOval(xNeu-r/2,yNeu-r/2,r,r);
	   			g.setColor(genFrontColor);
	   			g.drawOval(xNeu-r/2,yNeu-r/2,r,r);
   			
	   			if (DDCircleLabel.get(w) != null) {
   					g.drawString(DDCircleLabel.get(w), xNeu + 10,yNeu + 10);
   				}
			}

			g.setColor(genFrontColor);
				Font oldFont = g.getFont();
				Font font24 = g.getFont().deriveFont(20.0f);
				g.setFont(font24);
			
				for (int w=0;w<DD2DTextText.size();w++){
					g.drawString(" "+DD2DTextText.get(w),DD2DTextX.get(w),DD2DTextY.get(w));
				}
			    for (int w=0;w<DDTextX.size();w++){
			    
		   			int[] xyz = getXYZ(sin_X,cos_X, sin_Y,cos_Y,  DDTextX.get(w),  DDTextY.get(w), DDTextZ.get(w));
			    	 xNeu=xyz[0];
		   			 yNeu=xyz[1];
		   			 zNeu=xyz[2];
		   			
		   			g.drawString(" "+DDTextText.get(w),xNeu,yNeu);
			    }
			    g.setFont(oldFont);
			
	
						
			 stroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0,
			        new float[] { 2, 1 }, 0);
			    g2.setStroke(stroke);
			    
			for (int w=0;w<DDLineX.size();w++){	
				
				int[] xyz = getXYZ(sin_X,cos_X, sin_Y,cos_Y,  DDLineX.get(w),  DDLineY.get(w), DDLineZ.get(w));
		    	 xNeu=xyz[0];
	   			 yNeu=xyz[1];
	   			 zNeu=xyz[2];

	   			if ( w == 0) {
	   				g.setColor(Color.red);
	   				g.drawLine(xNeu,0, xNeu, (int) getSize().getHeight());
	   			}
				//System.out.println(xAlt+getSize().width/2+"\t"+(int) getSize().getHeight());
	   			
	   			
	   			 red=DDLineColR.get(w);
				if(red>255)red=255;
				if(red<0)red=0;
				 green=DDLineColG.get(w);
				if(green>255)green=255;
				if(green<0)green=0;
				 blue=DDLineColB.get(w);
				if(blue>255)blue=255;
				if(blue<0)blue=0;
		
				g.setColor(new Color(red,green,blue));

				if (DDLineS.get(w)){
		   			if (w>0){
		   				g.drawLine(xAlt,yAlt,xNeu,yNeu);
		   			}  
	   			}   			
			xAlt=xNeu;yAlt=yNeu;
			}	 
		
			g.setColor(genFrontColor);
			
		g.drawString(title,5,getSize().height-20);
		
		ArrayList<String>Sres = new ArrayList<String>();
		ArrayList<Integer>SresC = new ArrayList<Integer>();
		for (int i=0;i<res.size();i++) {
			boolean isSet = false;
			for (int j=0;j<Sres.size();j++) {
				if ( i != j) {
					if ( res.get(i).contentEquals(Sres.get(j))) {
						isSet = true;
						SresC.set(j, SresC.get(j)+1);
						break;
					}
				}
			}
			if ( !isSet) {
				Sres.add(res.get(i));
				SresC.add(1);
			}
		}
		blueBoxX0 = -1;
		blueBoxX1 = -1;
		blueBoxY0 = -1;
		blueBoxY1 = -1;
		if ( Sres.size()>0) {
			FontMetrics f = g.getFontMetrics(getFont());
			int h = f.getHeight();
			int mw = 0;
			for (int i=0;i<Sres.size();i++) {
				if ( mw <  f.stringWidth(SresC.get(i)+"x "+Sres.get(i))) mw =  f.stringWidth(SresC.get(i)+"x "+Sres.get(i));
			}
			g.setColor(Color.blue);
			blueBoxX0 = mousePosX0-5-mw-4;
			blueBoxX1 = mousePosX0-5-mw-4 + mw+4;
			blueBoxY0 =  mousePosY0+5;
			blueBoxY1 =  mousePosY0+5+h*Sres.size()+2;
			g.fillRect(mousePosX0-5-mw-4, mousePosY0+5, mw+4, h*Sres.size()+2);
			g.setColor(Color.white);
			for (int i=0;i<Sres.size();i++) {
				g.drawString(SresC.get(i)+"x "+Sres.get(i), mousePosX0-5-mw-4, mousePosY0+2+(i+1)*h);
			}
			
		}
		
	}

	   /**
	   * Clears all current objects.
	   * @return Nothing.
	   */
	public void clearAll() {
		DDCircleX.clear();
		DDCircleY.clear();
		DDCircleZ.clear();
		DDCircler.clear();
		DDCircleCol.clear();
		DDCircleLabel.clear();
	
		DDCubeX.clear();
		DDCubeY.clear();
		DDCubeZ.clear();
		DDCuber.clear();
		DDCubeCol.clear();
		DDCubeLabel.clear();

		DD2DTextX.clear();	
		DD2DTextY.clear();
		DD2DTextText.clear();

		DDTextX.clear();
		DDTextY.clear();
		DDTextZ.clear();
		DDTextText.clear();

		DDLineX.clear();
		DDLineY.clear();
		DDLineZ.clear();
		DDLineS.clear();
		DDLineColR.clear();
		DDLineColG.clear();
		DDLineColB.clear();

		DDPreLineX.clear();
		DDPreLineY.clear();
		DDPreLineZ.clear();
		DDPreLineS.clear();
		DDPreLineColR.clear();
		DDPreLineColG.clear();
		DDPreLineColB.clear();
		
		DDLegImg.clear();
	}
	/**
	   * Add xzy circle point
	   * @param x,y,z coordinates as integer
	   * @param r circle diameter 
	   * @param col circle point fill color
	   * @return Nothing.
	   */
	public void setCircle(int x,int y,int z,int r, Color col, String label){
		DDCircleX.add(x);	
		DDCircleY.add(y);
		DDCircleZ.add(z);
		DDCircler.add(r);
		DDCircleCol.add(col);
		DDCircleLabel.add(label);
	}
	/**
	   * Add xzy cube point
	   * @param x,y,z coordinates as integer
	   * @param r cube width 
	   * @param col circle point fill color
	   * @return Nothing.
	   */
	public void setCube(int x,int y,int z,int r, Color col, String label){
		DDCubeX.add(x);	
		DDCubeY.add(y);
		DDCubeZ.add(z);
		DDCuber.add(r);
		DDCubeCol.add(col);
		DDCubeLabel.add(label);
	}
	/**
	   * Add 2D text to be displayed on the plot 
	   * @param x,y screen coordinates as integer
	   * @param r Text to be displayed
	   * @return Nothing.
	   */
	public void set2DText(int x,int y,String r){
		DD2DTextX.add(x);	
		DD2DTextY.add(y);
		DD2DTextText.add(r);
	}
	/**
	   * Add Legend element
	   * @param item Legend item as Image
	   * @return Nothing.
	   */
	public void setLegend(BufferedImage item){
		DDLegImg.add(item);
	}
	/**
	   * Add xzy text, e.g. axis label
	   * @param x,y,z coordinates as integer
	   * @param r text to be displayed 
	   * @return Nothing.
	   */	
	public void setText(int x,int y,int z,String r){
		DDTextX.add(x);	
		DDTextY.add(y);
		DDTextZ.add(z);
		DDTextText.add(r);
	}
	/**
	   * Add xzy line after plotting
	   * @param x,y,z coordinates as integer
	   * @param R,G,B rgb() values of line
	   * @param s boolean to define if line to last point is drawn
	   * @return Nothing.
	   */
	public void setLine(int x,int y,int z,boolean s,int R,int G,int B){
		DDLineX.add(x);
		DDLineY.add(y);
		DDLineZ.add(z);
		DDLineS.add(s);
		DDLineColR.add(R);
		DDLineColG.add(G);
		DDLineColB.add(B);
	}
	/**
	   * Add xzy line before plotting
	   * @param x,y,z coordinates as integer
	   * @param R,G,B rgb() values of line
	   * @param s boolean to define if line to last point is drawn
	   * @return Nothing.
	   */
	public void setPreLine(int x,int y,int z,boolean s,int R,int G,int B){
		DDPreLineX.add(x);
		DDPreLineY.add(y);
		DDPreLineZ.add(z);
		DDPreLineS.add(s);
		DDPreLineColR.add(R);
		DDPreLineColG.add(G);
		DDPreLineColB.add(B);
	}
}
