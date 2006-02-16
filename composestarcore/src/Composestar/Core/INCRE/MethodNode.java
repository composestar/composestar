package Composestar.Core.INCRE;

import Composestar.Core.Exception.ModuleException;

import java.util.ArrayList;
import java.util.Iterator;
import java.lang.reflect.Method;

public class MethodNode extends Node
{
	private ArrayList parameters;
	
	public MethodNode(String objectref)
	{
		super(objectref);
		parameters = new ArrayList();
	}

	public Object visit(Object obj) throws ModuleException
	{
		try 
		{
			Method mymethod = null;
			
            if(objectref.indexOf(".")>0)
            {
            	// objectref = FULLNAME_OF_CLASS.NAME_OF_METHOD
            	String fullclassname = objectref.substring(0,objectref.lastIndexOf("."));
            	String methodname = objectref.substring(objectref.lastIndexOf(".")+1);
            	Class myclass = Class.forName(fullclassname);
            	Class[] paramclasses = {obj.getClass()};
            	mymethod = myclass.getMethod(methodname,paramclasses);
            	Object[] paramobjects = {obj};
            	return mymethod.invoke(myclass.newInstance(),paramobjects);
            }
            else {
            	// objectref = NAME_OF_METHOD
            	//System.out.println("Visiting method: "+objectref+" for "+obj.getClass().getName());
            	mymethod = obj.getClass().getMethod(objectref,getParameterTypes());
            	return mymethod.invoke(obj,parameters.toArray());
            }
		} 
		catch(Exception excep){throw new ModuleException("Cannot visit method node "+objectref+" "+excep.toString(),"INCRE");}
	}
	
	public void setParameters(ArrayList params){
		this.parameters = params;
	}
	
	public ArrayList getParameters(){
		return this.parameters;
	}

	public Class[] getParameterTypes(){
		
		try {
			if(this.parameters.size()>0){
				Class[] classes = new Class[this.parameters.size()];
				for(int i=0;i<this.parameters.size();i++){
					Object obj = (Object)parameters.get(i);
					classes[i] = obj.getClass();
				}
				return classes;
			}
		}
		catch(NullPointerException npex){}
			
		return null;	
	}
	
	public String getUniqueID(Object obj){
		String uniqueID = obj.hashCode()+"."+this.objectref;
		if(parameters.size()>0){
			Iterator params = parameters.iterator();
			while(params.hasNext()){
				uniqueID += (String)params.next();				
			}
		}
		return uniqueID;
	}
}
