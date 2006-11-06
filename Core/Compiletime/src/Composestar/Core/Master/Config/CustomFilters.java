package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.ArrayList;

public class CustomFilters implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3968104066872916800L;

	private ArrayList filters;

	public CustomFilters()
	{
		filters = new ArrayList();
	}

	public void addCustomFilters(CustomFilter cf)
	{
		filters.add(cf);
	}

	public ArrayList getCustomFilters()
	{
		return filters;
	}
}
