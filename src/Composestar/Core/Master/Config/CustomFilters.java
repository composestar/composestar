package Composestar.Core.Master.Config;

import java.util.ArrayList;

public class CustomFilters {

	private ArrayList filters;
	
	public CustomFilters() {
		filters = new ArrayList();
	}
	
	public void addCustomFilters(CustomFilter cf) {
		filters.add(cf);
	}
	
	public ArrayList getCustomFilters() {
		return filters;
	}
}
