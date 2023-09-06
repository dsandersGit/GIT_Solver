import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class HeatMap extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public HeatMap() {
		
	}
	protected void paintComponent( Graphics g )
	{
		int w = this.getWidth();
		int h = this.getHeight();
		
		super.paintComponent( g);
		//Graphics2D g2 = (Graphics2D) g;
		
		int histo[][] = new int[100][DS.freezs.size()];
		int maxHisto = 0;
		int minHisto = 0;
		int clsList[] = new int[DS.freezs.size()];
//		for (int c=0;c<DS.freezs.size(); c++) {
//			for (int i=0;i<100; i++) {
//				histo[i][c]= DS.numSamples;
//			}
//		}
		
		for (int i=0;i<DS.freezs.size();i++) {
			for (int j=0;j<DS.numSamples;j++) {
				MC_Freeze mc = DS.freezs.get(i);
				int cls = Tools.getIndexOfTarget(mc.targetColorIndex);				// 0, 1, 2 ..
				clsList[i] = cls;
				int pos = (int)(mc.distances[j]*100);
				if ( pos < 0 )pos = 0;
				if ( pos > 99 )pos = 99;
				if (  mc.targetColorIndex == DS.classIndex[j] ) {
					histo[pos][i]++;
				}else {
					//histo[pos][i]--;
				}
				if ( histo[pos][i] > maxHisto) maxHisto = histo[pos][i];
				if ( histo[pos][i] < minHisto || minHisto == 0) minHisto = histo[pos][i];
			}
		}
		for (int c=0;c<DS.freezs.size(); c++) {
			for (int i=0;i<100; i++) {
				g.setColor(getGasRGB(histo[i][c],minHisto,maxHisto, false));
				g.fillRect(i*5, c*5, 5, 5);
			}
			g.setColor(Tools.getClassColor(Tools.getIndexOfTarget(DS.freezs.get(c).targetColorIndex)));
			g.fillRect(105*5, c*5, 5, 5);
		}
		
		for (int c=0;c<DS.classCols.length; c++) {
			
			g.setColor(DS.classCols[c]);
			g.fillRect(10+c*11, 400, 11, 11);
		}
		
	g.dispose();
	}
	public static Color getGasRGB(int ldat, int min, int max, boolean inv){
		int col = 0,red = 0,green = 0,blue = 0;
		
		if ( max>min)
		 col = (int)(((ldat-min)*1280)/(max-min));
		 
		 red=0;
		 green=0;
		 blue=0;
		
		red=col-512;green=col-256;blue=col;
		if(col>768){
			blue=255-(col-768);
		}
		if(col>1024){
			green=255-(col-1024);
		}
	
		if (red>255)red=255;
		if (red<0)red=0;
		if (green>255)green=255;
		if (green<0)green=0;
		if (blue>255)blue=255;
		if (blue<0)blue=0;
		
		if ( inv) {
			red = 255-red;
			green = 255-green;
			blue = 255-blue;
		}
	
		return new Color(red,green,blue);
	}
}
