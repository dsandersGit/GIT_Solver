import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DragUI extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static DragPan dPan = new DragPan();
	
	public DragUI() {
		setPreferredSize(new Dimension(800,600));
		add(dPan);
		pack();
		setVisible(true);
		
	}
}
class DragPan extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static ArrayList<Integer>	eml_Left 	= new ArrayList<Integer>();
	static ArrayList<Integer>	eml_Top 	= new ArrayList<Integer>();
	static ArrayList<Integer>	eml_Width 	= new ArrayList<Integer>();
	static ArrayList<Integer>	eml_Height 	= new ArrayList<Integer>();
	static ArrayList<String>	eml_Txt 	= new ArrayList<String>();
	static ArrayList<Color>		eml_Col 	= new ArrayList<Color>();
	static ArrayList<Color>		eml_hasIt 	= new ArrayList<Color>();
	
	DragPan(){
		initElements();
	}
	protected void paintComponent( Graphics g )
	{
		super.paintComponent( g);
		Graphics2D g2 = (Graphics2D) g;
		for (int i=0;i<eml_Txt.size();i++) {
			g.setColor(eml_Col.get(i));
			g.fill3DRect(eml_Left.get(i), eml_Top.get(i), eml_Width.get(i), eml_Height.get(i), true);
			g.setColor(Color.black);
			g.drawString(eml_Txt.get(i), eml_Left.get(i)+2, eml_Top.get(i)+13);
		}
		
	}
	private void initElements() {
		eml_Txt.add(  "LOAD");
		eml_Left.add(  50);
		eml_Top.add(  50);
		eml_Width.add(150);
		eml_Height.add(50);
		eml_Col.add(Color.orange);
		
		eml_Txt.add(  "Classify");
		eml_Left.add(  250);
		eml_Top.add(  50);
		eml_Width.add(150);
		eml_Height.add(50);
		eml_Col.add(Color.cyan);

		eml_Txt.add(  "Train");
		eml_Left.add(  450);
		eml_Top.add(  50);
		eml_Width.add(150);
		eml_Height.add(50);
		eml_Col.add(Color.blue);

		eml_Txt.add(  "DATA");
		eml_Left.add(  50);
		eml_Top.add(  250);
		eml_Width.add(50);
		eml_Height.add(30);
		eml_Col.add(Color.LIGHT_GRAY);
		
		eml_Txt.add(  "ENSEMBLE");
		eml_Left.add(  250);
		eml_Top.add(  250);
		eml_Width.add(50);
		eml_Height.add(30);
		eml_Col.add(Color.LIGHT_GRAY);
		
		eml_Txt.add(  "OPTIONS");
		eml_Left.add(  450);
		eml_Top.add(  250);
		eml_Width.add(50);
		eml_Height.add(30);
		eml_Col.add(Color.red);
	}

}
