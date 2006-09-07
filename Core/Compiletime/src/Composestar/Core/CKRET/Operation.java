/*
 * Created on Dec 9, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package Composestar.Core.CKRET;

import java.io.Serializable;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Operation implements Serializable{
	
	private String name;
	private String resource;
	
	public Operation(String name, String resource)
	{
		this.name = name;
		this.resource = resource;
	}
	
	public String getResource()
	{
		return this.resource;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public boolean isFork()
	{
		return this.getName().equals("fork") && this.getResource().equals("message");
	}
	
	public boolean isProceed()
	{
		return this.getName().equals("proceed") && this.getResource().equals("message");
	}
	
	public boolean isReturn()
	{
		return this.getName().equals("return") && this.getResource().equals("message");
	}
}
