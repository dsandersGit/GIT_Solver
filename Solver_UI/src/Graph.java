import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Graph {
	
	JFrame jF = new JFrame();
	public static SP_PlotCanvas sp = new SP_PlotCanvas();
	public static flashDims fd = new flashDims();
	
	public	  Graph() {
		jF.setLayout(new BorderLayout());
		flashDims.setVals(new double [DS.numVars][Opts.numDims]);
		fd.setPreferredSize(new Dimension (DS.numVars*10,Opts.numDims*10 +40));
		jF.add(fd,BorderLayout.NORTH);
		sp.setPreferredSize(new Dimension(800,600));
		jF.add(sp,BorderLayout.CENTER);
//		JPanel controls = new JPanel();
//		controls.setLayout(new FlowLayout(FlowLayout.RIGHT));
//		JButton jB_stop = new JButton("Enough");
//		jB_stop.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e){		
//			jB_stop.setEnabled(false);
//		}});
//		controls.add(jB_stop);
//		jF.add(controls,BorderLayout.SOUTH);
		jF.pack();
		jF.setVisible(true);
	}
}
class flashDims extends JPanel{
	
	public static double[][] cols = null;
	
	private static final long serialVersionUID = 1L;
	public static void setVals (double[][] c ) {
		cols = c;
	}
    protected void paintComponent( Graphics g )
    {
         super.paintComponent(g);

         if ( cols == null ) return;
             double max = 0;
             for (int a=0;a<cols.length;a++){
    
                for (int i=0;i<cols[0].length;i++) {
                    if(max < cols [a][i] || a==0) max = cols [a][i];
                }
            }
    
             for (int a=0;a<cols.length;a++){
            	 int add=10 ;
                 for (int i=0;i<cols[0].length;i++) {
                	 if ( i>2)add=15;
                     int col = (int)( 255 * (cols [a][i])/(max));
                     if ( col > 255)col=255;
                     if ( col < -255)col=-255;
                     Color c = null;
                     if ( col<0) {
                         c = new Color(0,0,-col);
                     }else {
                         c = new Color(col,0,0);
                     }
                     g.setColor(c);
                     g.fillOval(10+a*10, 15+10*i+add, 10, 10);
                     g.setColor(Color.LIGHT_GRAY);
                     g.drawOval(10+a*10, 15+10*i+add, 10, 10);
                 }
             }
            }

}