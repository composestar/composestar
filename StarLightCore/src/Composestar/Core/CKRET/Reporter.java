/*
 * Created on Jan 5, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package Composestar.Core.CKRET;

import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.CpsProgramRepository.Concern;

/**
 * @author Administrator
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface Reporter {

	public void open();
	public void close();
	public void openConcern(Concern concern);
	public void closeConcern();
	public void reportOrder(FilterModuleOrder order, FilterSetAnalysis analysis, boolean selected, boolean incremental);
	
}
