/**
 * 
 */
package Composestar.Visualization.UI.Actions;

import java.awt.event.ActionEvent;

import javax.swing.JFileChooser;

import Composestar.Visualization.UI.Utils.CompileHistoryFilter;

/**
 * @author Michiel Hendriks
 */
public class FileOpenAction extends VisComAction
{
	private static final long serialVersionUID = 3340397625905635331L;

	public FileOpenAction()
	{
		super("Open");
	}

	public void actionPerformed(ActionEvent arg0)
	{
		JFileChooser fc = new JFileChooser();

		fc.addChoosableFileFilter(new CompileHistoryFilter());
		if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			graphProvider.closeAllGraphs();
			if (controller.openCompileHistory(fc.getSelectedFile()))
			{
				graphProvider.openGraph(controller.getViewManager().getProgramView().getGraph());
			}
		}
	}
}
