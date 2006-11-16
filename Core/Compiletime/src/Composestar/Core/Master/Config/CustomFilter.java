package Composestar.Core.Master.Config;

import java.io.Serializable;

public class CustomFilter implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5750706940042791837L;

	private String filter = "";

	private String library = "";

	public CustomFilter()
	{}

	public String getFilter()
	{
		return this.filter;
	}

	public void setFilter(String inFilter)
	{
		filter = inFilter;
	}

	public String getLibrary()
	{
		return this.library;
	}

	public void setLibrary(String inLibrary)
	{
		library = inLibrary;
	}
}
