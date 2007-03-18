/**
 * 
 */
package Composestar.Visualization.UI.Actions;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import Composestar.Visualization.VisCom;
import Composestar.Visualization.UI.CpsJGraphProvider;

/**
 * @author Michiel Hendriks
 */
public abstract class VisComAction extends AbstractAction
{
	protected CpsJGraphProvider graphProvider;

	protected VisCom controller;

	public VisComAction()
	{
		super();
	}

	public VisComAction(String name, Icon icon)
	{
		super(name, icon);
	}

	public VisComAction(String name)
	{
		super(name);
	}

	public void setGraphProvider(CpsJGraphProvider inProvider)
	{
		graphProvider = inProvider;
	}

	public void setController(VisCom inController)
	{
		controller = inController;
	}
}
