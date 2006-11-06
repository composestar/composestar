package Composestar.Core.Master.Config;

import java.io.Serializable;

public class CustomFilter implements Serializable
{

	private String filter = "";

	private String library = "";

	public CustomFilter()
	{}

	public String getFilter()
	{
		return this.filter;
	}

	public void setFilter(String filter)
	{
		this.filter = filter;
	}

	public String getLibrary()
	{
		return this.library;
	}

	public void setLibrary(String library)
	{
		this.library = library;
	}
}
