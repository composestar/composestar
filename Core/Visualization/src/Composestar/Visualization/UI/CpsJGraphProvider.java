/**
 * 
 */
package Composestar.Visualization.UI;

import java.util.List;

import Composestar.Visualization.Model.CpsJGraph;

/**
 * @author Michiel Hendriks
 */
public interface CpsJGraphProvider
{
	public CpsJGraph getCurrentGraph();

	public List<CpsJGraph> getAllGraphs();

	public void openGraph(CpsJGraph newGraph);

	public void closeGraph(CpsJGraph graph);

	public void closeAllGraphs();
}
