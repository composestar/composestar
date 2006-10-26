package Composestar.RuntimeDotNET.Utils;

import Composestar.RuntimeCore.FLIRT.Interpreter.Exception.*;
import Composestar.RuntimeCore.Message.*;
import Composestar.RuntimeCore.Utils.*;

import System.*;
import System.Reflection.*;
import System.Reflection.BindingFlags;

import java.util.*;

public class DotNETInvoker extends Invoker
{
	private static Assembly currentAssembly = null;

	public DotNETInvoker()
	{
		instance = this;
	}

	/**
	 *  Description of the Method
	 *
	 *@param  target    Description of Parameter
	 *@param  selector  Description of Parameter
	 *@param  args      Description of Parameter
	 *@return           Description of the Returned Value
	 */
	public Object invoke(Object target, String selector, Object[] args)
	{
		Debug.out(Debug.MODE_DEBUG,"FLIRT","Invocation on target '"+target.GetType().ToString()+"' with selector '"+selector+"'...");
		Object reply = null;
		try
		{
			BindingFlags flags = BindingFlags.Public 
			                   | BindingFlags.NonPublic
			                   | BindingFlags.Instance 
			                   | BindingFlags.InvokeMethod;

			reply = target.GetType().InvokeMember(selector, flags, null, target, args);
		}
		catch (System.Exception e)
		{
			Debug.out(Debug.MODE_ERROR,"FLIRT","Invocation on instance " + target + ":" + selector + " failed:");
			Debug.out(Debug.MODE_ERROR,"FLIRT",e.ToString());
			System.exit(1);
		}
		return reply;
	}

	public Object invoke(String target, String selector, Object[] args)
	{
		// TODO: fix this, probably MessageHandlingFacility
		// should remove these quotes
		selector = selector.Replace("'","");
		//Debug.out(Debug.MODE_DEBUG,"FLIRT","Invocation on target '"+target+"' with selector '"+selector+"'...");

		System.Type targetType = getType(target);
		Object reply = null;
		try
		{
			BindingFlags flags = BindingFlags.Public 
			                   | BindingFlags.NonPublic
			                   | BindingFlags.Static 
			                   | BindingFlags.InvokeMethod;

			reply = targetType.InvokeMember(selector, flags, null, null, args);
		}
		catch(System.Exception e) 
		{ 
			Debug.out(Debug.MODE_ERROR,"FLIRT","Invocation of static "+ target + ":" + selector + " failed.");
			String inner = "";
			Console.WriteLine(e.get_StackTrace());
			while(e != null)
			{
				Console.WriteLine(inner + "exception:" + e.getClass().getName() + ":" + e.get_Message());
				inner += "inner";
				e = e.get_InnerException();
			}
			System.exit(1);
		}
		return reply;
	}

	public Object requestInstance(String target, Object[] args)
	{
		//Debug.out(Debug.MODE_DEBUG,"FLIRT","Requesting new instance of '"+target.substring(target.lastIndexOf(".")+1)+"'...");
		//Debug.out(Debug.MODE_DEBUG,"FLIRT","\tWith "+args.length+" argument(s).");
		System.Type targetType = getType(target);
		if(targetType == null) 
		{ 
			Debug.out(Debug.MODE_ERROR,"FLIRT","Unable to locate referenced type '"+target+"'.");
		}
		return System.Activator.CreateInstance(targetType);
	}

	public java.lang.Class getClass(String type)
	{
		System.Type systemType = getType(type);
		Class result = null;
		try
		{
			result = Class.forName(systemType.get_FullName());
		}
		catch(ClassNotFoundException e)
		{
			Debug.out(Debug.MODE_ERROR, "Util", "Class not found:" + type);
			System.exit(1);
		}
		return result;
	}

	public synchronized System.Type getType(String type)
	{
		Type result = null;

		result = System.Reflection.Assembly.GetEntryAssembly().GetType(type);

		if (result == null)
		{
			System.Reflection.AssemblyName[] assemblies = Assembly.GetEntryAssembly().GetReferencedAssemblies();
			for (int i=0; i < assemblies.length; i++) 
			{
				//Debug.out(Debug.MODE_DEBUG,"FLIRT","\tChecking in assembly: "+assemblies[i].ToString());
				result = System.Reflection.Assembly.Load(assemblies[i]).GetType(type);
				//Debug.out(Debug.MODE_DEBUG,"FLIRT","\tChecking in assembly: "+result);
				if (result != null)
				{
					break;
				}
			}
		}

		return result;
	}

	public ArrayList getAttributesFor(Object target, String selector)
	{
		Debug.out(Debug.MODE_DEBUG,"FLIRT","\tLooking up attributes for '"+target.GetType().get_FullName()+"."+selector+"'...");
		ArrayList list = new ArrayList();
		Type t = target.GetType();
		MethodInfo[] methods = t.GetMethods();
		for(int i=0; i<methods.length; i++)
		{
			MethodInfo mi = (MethodInfo)methods[i];
			//Debug.out(Debug.MODE_DEBUG,"FLIRT","\t\tLooking up method '"+mi.get_Name()+"'.");
			if(mi.get_Name().Equals(selector))
			{
				//Debug.out(Debug.MODE_DEBUG,"FLIRT","\t\tFound method '"+mi.get_Name()+"'.");
				Object[] attributes = mi.GetCustomAttributes(true);
				for(int j=0; j<attributes.length; j++)
				{
					Attribute att = (Attribute)attributes[j];
					list.add(att);
					/*Type type = att.GetType();.GetType().ToString()
					PropertyInfo[] properties = type.GetProperties();
					for(int k=0; k<properties.length; k++)
					{
						PropertyInfo pi = (PropertyInfo)properties[k];
						Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\tLooking up atts: "+pi.get_Name()+" == "+pi.GetValue(att,null));
						attrmap.put(pi.get_Name(), pi.GetValue(att,null));
					}
					FieldInfo[] fields = type.GetFields();
					for(int k=0; k<fields.length; k++)
					{
						FieldInfo fi = (FieldInfo)fields[k];
						Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\tLooking up atts: "+fi.get_Name()+" == "+fi.GetValue(att));
						attrmap.put(fi.get_Name(), fi.GetValue(att));
					}*/
				}
				/* This would be needed for attributes on arguments */
				/*ParameterInfo[] params = mi.GetParameters();
				 for(int j=0; j<params.length; j++)
				 {
				 ParameterInfo pi = params[j];
				 Object[] param_attributes = pi.GetCustomAttributes(true);
				 for(int l=0; l< param_attributes.length; l++)
				 {
				 Attribute att = (Attribute)param_attributes[l];
				 list.add(att);
				 Type type = att.GetType();
				 PropertyInfo[] properties = type.GetProperties();
				 for(int k=0; k<properties.length; k++)
				 {
				 PropertyInfo pri = (PropertyInfo)properties[k];
				 Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\tLooking up atts: "+pri.get_Name()+" == "+pri.GetValue(att,null));
				 attrmap.put(pri.get_Name(), pri.GetValue(att,null));
				 }
				 FieldInfo[] fields = type.GetFields();
				 for(int k=0; k<fields.length; k++)
				 {
				 FieldInfo fi = (FieldInfo)fields[k];
				 Debug.out(Debug.MODE_INFORMATION,"FLIRT","\t\t\tLooking up atts: "+fi.get_Name()+" == "+fi.GetValue(att));
				 attrmap.put(fi.get_Name(), fi.GetValue(att));
				 }
				 }
				 }*/
			}
		}
		return list;
	}

	public boolean objectHasMethod (Object inner, String m_selector, Dictionary context)
	{
		// fixme Very dirty, now every filter is able to dispatch to private methods.
		// Hopefully this can only be used by self calls.
		MethodInfo[] mi = inner.GetType().GetMethods(System.Reflection.BindingFlags.Public 
			| System.Reflection.BindingFlags.NonPublic 
			| System.Reflection.BindingFlags.Instance
			| System.Reflection.BindingFlags.Static);
		for( int i=0; i< mi.length; i++)
		{
			//Debug.out(Debug.MODE_DEBUG,"FLIRT","\t\t\t\tResolving inner: "+mi[i].get_Name());
			if(mi[i].get_Name().equals(m_selector))
			{
				return true;
			}
		}
		return false;
	}
}
