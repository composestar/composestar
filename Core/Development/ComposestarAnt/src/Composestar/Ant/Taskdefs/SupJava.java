package Composestar.Ant.Taskdefs;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Java;

/**
 * A simple wrapper task.
 * 
 * @author Michiel Hendriks
 */
public class SupJava extends Java
{
	protected Task parent;
	
	public SupJava(Task parent)
	{
		super();
		this.parent = parent;
		setProject(parent.getProject());
		setOwningTarget(parent.getOwningTarget());
		setTaskName(parent.getTaskName());
		setDescription(parent.getDescription());
		setLocation(parent.getLocation());
		setTaskType(parent.getTaskType());
	}
	
	public void log(String msg, int msgLevel)
	{
		if (parent != null)
		{
			parent.log(msg, msgLevel);
		}
		else
		{
			super.log(msg, msgLevel);
		}
	}
}
