import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Icon_Solver extends JDialog{
	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;

	public Icon_Solver() {
		this.setTitle(SolverStart.app);
		JPanel main = new JPanel();
		main.setLayout(new GridLayout(1,1));
		main.setBorder(BorderFactory.createTitledBorder("SCHEME ALGORITHM"));
		
		JLabel labAlgo = new JLabel(new ImageIcon(ClassLoader.getSystemResource("solver_algo.png")));
		main.add(labAlgo);
		this.add(main);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}
	public void zoom() {
		
	}
}
