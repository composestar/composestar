package Composestar.Core.INCRE;

import Composestar.Utils.*;
import Composestar.Core.Exception.*;
import Composestar.Core.Master.CommonResources;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class Module 
{
	private String name = "";
	private String fulltype;
	private String input;	
	private boolean enabled = false;
	private boolean incremental = false;
	private String summary = "";
	   
   /**
    * A map containing dependencies for the module. 
    */
    private LinkedHashMap deps = new LinkedHashMap();
   
    /**
     * A map containing objects to be compared by MyComparator. 
     */
    private HashMap comparableObjects = new HashMap();
     
   /**
    * @roseuid 420A1C42000F
    */
	public Module(String name) 
	{
		this.name = name;
	}
   
	public void setFullType(String ftype)
	{
		this.fulltype = ftype;
	}

	public void setInput(String className)
	{
		this.input = className;
	}

	public void setEnabled(boolean b)
	{
		this.enabled = b;
	}

	public void setSummary(String s)
	{
		this.summary = s;
	}

	public void addDep(Dependency d) 
	{
		String id = d.getName();
		this.deps.put(id,d);
	}
   
	public void addComparableObject(String key, Object obj){
		ArrayList list = null;
		if(this.comparableObjects.containsKey(key))
			list = (ArrayList)comparableObjects.get(key);
		else list = new ArrayList();
		
		list.add(obj);
		this.comparableObjects.put(key,list);
	}
	
	public void removeComparableObject(String key, Object obj){
		ArrayList list = null;
		if(this.comparableObjects.containsKey(key))
			list = (ArrayList)comparableObjects.get(key);
		else list = new ArrayList();
		
		list.remove(obj);
		this.comparableObjects.put(key,list);
	}
	
	public void addComparableObjects(HashMap map){
		Iterator keys = map.keySet().iterator();
		while(keys.hasNext()){
			String key = (String)keys.next();
			HashMap tyminfo =  (HashMap)map.get(key);
			Iterator objItr = tyminfo.values().iterator();
			while(objItr.hasNext()){
				Object obj = objItr.next();
				addComparableObject(key,obj);
			}
		}
	}
	
	public void removeComparableObjects(HashMap map){
		Iterator keys = map.keySet().iterator();
		while(keys.hasNext()){
			String key = (String)keys.next();
			HashMap tyminfo =  (HashMap)map.get(key);
			Iterator objItr = tyminfo.values().iterator();
			while(objItr.hasNext()){
				Object obj = objItr.next();
				removeComparableObject(key,obj);
			}
		}
	}
	
	public boolean hasComparableObjects(String key){
		return comparableObjects.containsKey(key);	
	}
	
	public ArrayList getComparableObjects(String key){
		return (ArrayList)comparableObjects.get(key);	
	}
	
	public Iterator getDeps() 
	{
		return deps.values().iterator();
	}

	public String getName()
	{
		return name;
	}

	public String getInput()
	{
		return input;
	}

	public boolean isIncremental()
	{
		return this.incremental;
	}

	public void setIncremental(boolean b)
	{ 
		this.incremental = b;
	}

	/**
	 * Creates an instance of a coreModule of type 'fulltype' and calls it run method
	 * @param String phase of execution
	 */
	public void execute(CommonResources resources) throws ModuleException
	{
		if(this.enabled)
		{
			// module is enabled for the phase so continue
			if(!this.summary.equals(""))
				Debug.out(Debug.MODE_CRUCIAL, "Master",this.summary);
		
			try 
			{
				Class myclass = Class.forName(this.fulltype);
				Class[] methodparams = {resources.getClass()};
				Method runmethod = myclass.getMethod("run",methodparams);
				Object[] myparams = {resources};
				if(myparams == null){
					myparams = new Object[0];
				}
				INCRE incre = INCRE.instance();
				INCRETimer module = incre.getReporter().openProcess(this.name,this.name,INCRETimer.TYPE_ALL);
				runmethod.invoke(myclass.newInstance(),myparams);
				module.stop();
			}
			catch(StackOverflowError ex1){
				ex1.printStackTrace();
				throw new ModuleException("I need more stack!", "INCRE running " + this.name);
			}
			catch(OutOfMemoryError ex2){
				ex2.printStackTrace();
				throw new ModuleException("I am using too much memory!", "INCRE running " + this.name);
			}
			catch(ClassNotFoundException ex3){
				ex3.printStackTrace();
				throw new ModuleException("Cannot find class "+this.fulltype, "INCRE running " + this.name);
			}
			catch(InvocationTargetException ex4){
				ex4.printStackTrace();
				if (ex4.getCause() instanceof ModuleException)
					throw (ModuleException)ex4.getCause();
				else if(ex4.getTargetException()  instanceof ModuleException)
					throw (ModuleException)ex4.getTargetException();
				else
					throw new ModuleException(ex4.getMessage(), "INCRE running " + this.name);
			}
			catch(IllegalAccessException ex5){
				ex5.printStackTrace();
				throw new ModuleException(ex5.getMessage() + " Caused by:" + ex5.getCause().getMessage(), "INCRE running " + this.name);
			}
			catch(NoSuchMethodException ex6){
				ex6.printStackTrace();
				throw new ModuleException(ex6.getMessage(), "INCRE running " + this.name);
			}
			catch(InstantiationException ex6){
				ex6.printStackTrace();
				throw new ModuleException(ex6.getMessage(), "INCRE running " + this.name);
			}
			catch(Exception e){
				e.printStackTrace();
				Debug.out(Debug.MODE_DEBUG, "Master",e.getCause().toString());
				throw new ModuleException(e.getCause().toString(),"INCRE running " + this.name);
			}
		}
	}
}
