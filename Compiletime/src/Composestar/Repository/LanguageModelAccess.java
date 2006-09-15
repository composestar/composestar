package Composestar.Repository;

import java.util.ArrayList;

import com.db4o.ObjectSet;

import Composestar.Repository.LanguageModel.*;

public class LanguageModelAccess {
	
    private ArrayList ObjectSetToArrayList(ObjectSet os)
    {
		ArrayList result = new ArrayList();
		while (os.hasNext()) {
			result.add(os.next());
		
		}
		
		return result; 
    }

	public ArrayList getTypeElements()
	{
		return ObjectSetToArrayList(DataStoreContainer.getInstance().getTypeElements());
	}
	
	public ArrayList getFieldElements(TypeElement type)
	{
		return ObjectSetToArrayList(DataStoreContainer.getInstance().getFieldElements(type));		
	}	

	public ArrayList getMethodElements(TypeElement type)
	{
		return ObjectSetToArrayList(DataStoreContainer.getInstance().getMethodElements(type));		
	}
	
	public ArrayList getParameterElements(MethodElement method)
	{
		return ObjectSetToArrayList(DataStoreContainer.getInstance().getParameterElements(method));
	}
	
	public ArrayList getCallElements(MethodBody body)
	{
		return ObjectSetToArrayList(DataStoreContainer.getInstance().getCallElements(body));
	}
}
