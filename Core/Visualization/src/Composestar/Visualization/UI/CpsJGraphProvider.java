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
	CpsJGraph getCurrentGraph();

	List<CpsJGraph> getAllGraphs();

	void openGraph(CpsJGraph newGraph);

	void closeGraph(CpsJGraph graph);

	void closeAllGraphs();
}
