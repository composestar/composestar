package Composestar.C.wrapper;

import java.util.ArrayList;

import Composestar.C.wrapper.parsing.GnuCTokenTypes;
import Composestar.C.wrapper.parsing.TNode;
import Composestar.C.wrapper.utils.GeneralUtils;

public class Struct extends WeaveblePoint
{
	private String name = "";
	private TNode node = null;
	private ArrayList elements = new ArrayList();
	
	public Struct(String file)
	{
		super(file);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
	public void setNode(TNode node) {
		this.node = node;
		this.populateStructInfo(node);
	}
	
	public TNode getNode() {
		return node;
	}
	
	public Parameter getElement(int index)
	{
		if(index >= 0 && index < this.elements.size())
			return (Parameter)this.elements.get(index);
		else
			return null;
	}
	
	public void addElement(Parameter param)
	{
		this.elements.add(param);
	}
	
	public int getNumberOfElements()
	{
		return this.elements.size();
	}
	
	private void populateStructInfo(TNode node)
	{
		//System.out.println("***** Processing struct: "+this.getName());
		TNode tmp = (TNode)node.getFirstChild().getNextSibling();
		Parameter param = new Parameter(null);
		ArrayList types = new ArrayList();
		FunctionFactory ff = new FunctionFactory();
		String typedefname = null;
		while(tmp != null && tmp.getType() != GnuCTokenTypes.RCURLY)
		{
			//TNode.printTree(tmp);
			//System.out.println("Evaluating node: "+tmp.getText());
			int type = tmp.getType();
			if(GeneralUtils.isType(type)) //void/int/char
			{
				if(type != GnuCTokenTypes.LITERAL_void)
					types.add(new Integer(type));
			}
			else if (type == GnuCTokenTypes.NTypedefName)
            {
				types.add(new Integer(type));
				typedefname = tmp.firstChildOfType(GnuCTokenTypes.ID).getText();
            }
			else if(tmp.getType() == GnuCTokenTypes.NStructDeclarator)
			{
				//if(param != null) param.testParameter();
				//System.out.println("NStructDeclarator: ");
				//TNode.printTree(tmp.getFirstChild());
				if(typedefname != null)
				{
					param.setAdditionalTypeValue(typedefname);
				}
				
				if(tmp.getFirstChild().getType() == GnuCTokenTypes.NDeclarator)
				{
					ff.checkForArray((TNode)tmp.getFirstChild(),param);
					ff.checkForPointer((TNode)tmp.getFirstChild(),param);
					TNode idnode = ((TNode)tmp.getFirstChild()).firstChildOfType(GnuCTokenTypes.ID);
					if(idnode != null)
					{
						//System.out.println("Found name: "+idnode.getText());
						//System.out.println("\tTypes: "+types);
						//System.out.println("\tPointers: "+param.getPointerLevel());
						//System.out.println("\tAdditonal: "+param.getAdditionalTypeValue());
						param.setValueID(idnode.getText());
					}
				}
				int[] tmptype = new int[types.size()];
				
				for(int i=0; i<tmptype.length; i++)
					tmptype[i] = ((Integer)types.get(i)).intValue();
				
				param.setType(tmptype);
				typedefname = null;
				types = new ArrayList();
				this.addElement(param);
				param = new Parameter(null);
				//return;
			}
			//System.out.println("Types: "+types);
			tmp = (TNode)tmp.getNextSibling();
		}
	}
}
