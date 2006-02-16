/*
 * Created on Dec 9, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package Composestar.Core.CKRET;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Resource {

	private String name;
	private StringBuffer history;

	public Resource(String name)
	{
		this.name = name;
		this.history = new StringBuffer();
	}

	public void add(String operation)
	{
		this.history.append(operation.charAt(0));
	}

	public String toString()
	{
		return this.name + " > " + history.toString();
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public String sequence()
	{
		return history.toString();
	}

}
