/**
 * 
 */
package Composestar.Visualization.UI.Actions;

import java.util.HashMap;
import java.util.Map;

import javax.swing.Action;

import Composestar.Visualization.UI.Viewport;

/**
 * @author Michiel Hendriks
 */
public class ActionManager
{
	protected Viewport viewport;

	protected Map<Class, VisComAction> actionCache;

	public ActionManager(Viewport myViewport)
	{
		viewport = myViewport;
		actionCache = new HashMap<Class, VisComAction>();
	}

	public Action getAction(Class<?> actionClass)
	{
		if (actionCache.containsKey(actionClass))
		{
			return actionCache.get(actionClass);
		}
		if (!VisComAction.class.isAssignableFrom(actionClass))
		{
			return null;
		}
		VisComAction action;
		try
		{
			action = (VisComAction) actionClass.newInstance();
			action.setGraphProvider(viewport);
			action.setController(viewport.getController());
		}
		catch (Exception e)
		{
			return null;
		}
		actionCache.put(actionClass, action);
		return action;
	}
}
