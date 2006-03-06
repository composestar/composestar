package Composestar.RuntimeCore.CODER.BreakPoint;

import Composestar.RuntimeCore.CODER.Halter;
import Composestar.RuntimeCore.CODER.Model.DebuggableFilter;
import Composestar.RuntimeCore.CODER.Model.DebuggableMessage;
import Composestar.RuntimeCore.CODER.BreakPoint.Parsers.BreakPointParseException;

import java.util.*;

/**
 * Summary description for AlwaysBreakBreakPoint.
 */
public abstract class ObjectBreakPoint extends BreakPoint {

	private Class[] classes; 
	private boolean[] accepting;

    public ObjectBreakPoint(Halter halt, String selector) throws BreakPointParseException{
		super(halt);
		setSelector(selector);
    }
   
	public String toString()
	{
		String result = "";
		if(classes.length == 0)	return result;
		for(int i=0;i < classes.length;i++)
		{
			result += (accepting[i] ? "" : "!" )+ (classes[i] == null ? "*," : classes[i].getName() + ',');
		}
		return result.substring(0,result.length() -1);
	}

	public boolean matchObject(Object object){
		if(object == null)
		{
			return true;
		}
		Class type = object.getClass();

		for(int i = 0; i < classes.length; i++)
		{
			if(classes[i] == null || classes[i].isAssignableFrom(type))
			{
				return accepting[i];
			}	
		}
		return false;
    }

    public boolean threadSpecific() {
        return false;
    }

	public void setSelector(String selector) throws BreakPointParseException
	{
		if(selector == null || selector.equals("")) selector = "*";
		
		final char[] seperator = {','};

		String[] classnames = selector.Split(seperator);
		classes = new Class[classnames.length];
		accepting = new boolean[classnames.length];

		for(int i=0; i < classnames.length;i++)
		{
			if(classnames[i].length() == 0 || (classnames[i].length() == 1 && classnames[i].charAt(0) == '!'))
			{
				throw new BreakPointParseException("Missing classname or '*' in expression or subexpression");
			}
			else if(classnames[i].startsWith("*"))
			{
				classes[i] = null;
				accepting[i] = true;
			}
			else if(classnames[i].startsWith("!*"))
			{
				classes[i] = null;
				accepting[i] = false;
			}
			else{
				if(classnames[i].charAt(0) == '!')
				{
					classnames[i] = classnames[i].substring(1,classnames[i].length());
					accepting[i] = false;
				}
				else
				{
					accepting[i] = true;
				}
				try
				{
					classes[i] = Class.forName(classnames[i]);
				}
				catch(Exception e)
				{
					throw new BreakPointParseException("Missing classname \"" + classnames[i] + "\", make sure you have included the library and used the fully quilified name");
				}
			}
		}
	}
}
