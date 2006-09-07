package Composestar.Core.FILTH.Core;


/*
 * Created on 2-sep-2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author nagyist
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Edge {
	public Node _left, _right;
	public String _label;
	
	public Edge(String label, Node left, Node right){ 
		_label=label; 
		_left=left;  
		_right=right;
		_left.addOutgoingEdge(this);
	 	_right.addIncomingEdge(this);
	}
	
	public Node getLeft(){return _left;}
	public Node getRight(){return _right;}
	public String getLabel(){return _label;}
}
