import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class EnsembleTree extends JComponent
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static public JTree ensTree;
	static DefaultTreeModel 		model = null;
	static DefaultMutableTreeNode 	rootNode = new DefaultMutableTreeNode("Root");
	static DefaultMutableTreeNode 	ensembleNode = new DefaultMutableTreeNode("List of Ensembles");
	
	static ArrayList<JSONObject> 	ensembles 	= new ArrayList<JSONObject> (); 
	static ArrayList<String> 		enLabels 		= new ArrayList<String> ();
	static ArrayList<String> 		enSummaries 	= new ArrayList<String> ();
    
    public static JTextArea txtEnsemble 		= new JTextArea();
	static 		JScrollPane scEnsemble		= new JScrollPane(txtEnsemble);
    
	public static JTextArea txtSummary 		= new JTextArea();
	static 		JScrollPane scSummary		= new JScrollPane(txtSummary);
	
	public 	static 		JSplitPane 		jLeftRight 			= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	
	
    public EnsembleTree()
    {

        rootNode.add(ensembleNode);
        ensTree = new JTree(rootNode);
        ensTree.setShowsRootHandles(true);
        ensTree.setRootVisible(false);
        setLayout(new GridLayout(1,1));
        JPanel inPan = new JPanel();
        inPan.setLayout(new GridLayout(1,1));
        inPan.setBorder(BorderFactory.createTitledBorder("List of Ensembles"));
        inPan.add(new JScrollPane(ensTree));
        jLeftRight.setLeftComponent(inPan);
        JPanel upDown = new JPanel();
        upDown.setLayout(new GridLayout(2,1));
        inPan = new JPanel();
        inPan.setLayout(new GridLayout(1,1));
        inPan.setBorder(BorderFactory.createTitledBorder("Confusion Matrix"));
        inPan.add(scSummary);
        upDown.add(inPan);
        inPan = new JPanel();
        inPan.setLayout(new GridLayout(1,1));
        inPan.setBorder(BorderFactory.createTitledBorder("JSON Ensemble"));
        inPan.add(scEnsemble);
        upDown.add(inPan);
        jLeftRight.setRightComponent(upDown);
        add(jLeftRight);
        jLeftRight.setDividerLocation(0.25);
        
        ensTree.addTreeSelectionListener(new TreeSelectionListener() {
			
			  public void valueChanged(TreeSelectionEvent e) {
			        DefaultMutableTreeNode node = (DefaultMutableTreeNode)     		ensTree.getLastSelectedPathComponent();
			        if (node == null) return;
			    	 if(!node.isLeaf()){
			    		 clearEnsView();
			    	 }else{
			    		 int pos = node.getParent().getIndex(node);
			    		 setEnsView(pos);
			    	 }
			       
		    }
		});
        model = (DefaultTreeModel) ensTree.getModel();
      
        ensTree.addMouseListener(ml);
    }
    
    MouseListener ml = new MouseAdapter() {
	    public void mousePressed(MouseEvent e) {
	    	DefaultMutableTreeNode node = (DefaultMutableTreeNode) ensTree.getLastSelectedPathComponent();
	    	if(node == null)return;
	    	if(e.getClickCount() == 1) {
	    		
	    	}else{ 
	    		if(e.getClickCount() == 2) {
	    			if(node.isLeaf()){
	            		 String tmp = node.getParent().toString();
	            		 String tmp2 = node.toString();
	            	 }
	            }
		   	}
	    	 
	    	// REchte Maustaste
	    	if(e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
    			
    			JPopupMenu inPOP = new JPopupMenu();
        		JMenuItem item=null;
        		 DefaultMutableTreeNode inNode = (DefaultMutableTreeNode) ensTree.getLastSelectedPathComponent();
			    	if(inNode == null)return;
			    	int pos = inNode.getParent().getIndex(inNode);

				// --------------------------------------------------------------------------	    		        		
        		JLabel jLtmp = new JLabel("<html><b>ENSEMBLE</b>"); //$NON-NLS-1$
        		inPOP.add(jLtmp);
        		item = new JMenuItem("Set Active");
        		inPOP.add(item);
        		item.setEnabled(true);
				   item.addActionListener(new ActionListener(){
					   public void actionPerformed(ActionEvent e) {
					    	DS.setEnsemble(ensembles.get(pos));
		                }
				   });

        		item = new JMenuItem("Remove");
        		inPOP.add(item);
        		item.setEnabled(true);
				   item.addActionListener(new ActionListener(){
					   public void actionPerformed(ActionEvent e) {
					    	clearEns(pos); 
		                }
				   });
			
			  
				inPOP.show(e.getComponent(), e.getX(), e.getY());
			
    		}
	    }
	    
	};
	
    private static void clearEnsView() {
    	txtEnsemble.setText("");
    	txtSummary.setText("");
    }
    private static void setEnsView(int ensNum) {
    	txtEnsemble.setText(ensembles.get(ensNum).toString(3));
    	txtSummary.setText(enSummaries.get(ensNum));
    	txtSummary.select(0, 0);
    	
    }
    public static void putEnsemble(String label,String summary,  JSONObject json) {
    	rootNode.removeAllChildren();
    	if ( json!=null) {
	    	ensembles.add(json);
	    	enLabels.add(label);
	    	enSummaries.add(summary);
    	}
    	
    	ensembleNode = new DefaultMutableTreeNode("List of Ensembles");
    	for (int i=0;i<ensembles.size();i++) {
    		ensembleNode.add(new DefaultMutableTreeNode(enLabels.get(i)));
    	}
		if(ensembleNode.getChildCount()>0)
			rootNode.add(ensembleNode);
		
		model.reload();
		ensTree.expandRow(0);
		
    }
    private static void clearEns(int i) {
    	ensembles.remove(i);
    	enLabels.remove(i);
    	enSummaries.remove(i);
    	putEnsemble(null,null,null);
    }

//	public static void clearAll() {
//		// TODO Auto-generated method stub
//		ensembles.clear();
//    	enLabels.clear();
//    	enSummaries.clear();
//    	clearEnsView() ;
//    	rootNode.removeAllChildren();
//    	model.reload();
//    	jLeftRight.setDividerLocation(0.25);
//	}
  
}