package Composestar.Ant.Taskdefs;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;

/**
 * A simple wrapper task
 * 
 * @author Michiel Hendriks
 */
public class SupJava extends Java
{
	protected Task parent;
	
	public SupJava(Task myParent)
	{
		super();
		parent = myParent;
		setProject(parent.getProject());
		setOwningTarget(parent.getOwningTarget());
		setTaskName(parent.getTaskName());
		setDescription(parent.getDescription());
		setLocation(parent.getLocation());
		setTaskType(parent.getTaskType());
	}
	
	public void log(String arg0, int arg1)
	{
		if (parent != null)
		{
			parent.log(arg0, arg1);
		}
		else
		{
			super.log(arg0, arg1);
		}
	}
}
