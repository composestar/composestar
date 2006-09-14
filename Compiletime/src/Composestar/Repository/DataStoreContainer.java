package Composestar.Repository;

import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.query.*;

public class DataStoreContainer {
	private static DataStoreContainer instance;
	
	private ObjectContainer dbContainer;
	
	private Hashtable _methods;

    private DataStoreContainer()
    {
    	// Werkt alleen de 1ste keer, daarna is de db gewijzigd en mapped ie naar niet bestaande stored types
		//Db4o.configure().addAlias(
  		//		new WildcardAlias(
    	//			"Composestar.Repository.LanguageModel.*, Repository.LanguageModel", "Composestar.Repository.LanguageModel.*"));

		
		
		adjustClassNames();
		
		Db4o.configure().callConstructors(false);

		// Indexes
        Db4o.configure().objectClass(Composestar.Repository.LanguageModel.MethodElement.class).objectField("_parentTypeId").indexed(true);
        Db4o.configure().objectClass(Composestar.Repository.LanguageModel.ParameterElement.class).objectField("_parentMethodId").indexed(true);
        Db4o.configure().objectClass(Composestar.Repository.LanguageModel.ParameterElement.class).objectField("parentMethodBodyId").indexed(true);
        
        
        dbContainer = Db4o.openFile("test.yap");
    }
    
    private void adjustClassNames()
    {
    	dbContainer = Db4o.openFile("test.yap");

        StoredClass[] classes = dbContainer.ext().storedClasses();
        for (int i = 0; i < classes.length; i++) {
            StoredClass storedClass = classes[i];
            String name = storedClass.getName();

            int pos = name.indexOf(",");
            if(pos > 0){
                name = name.substring(0, pos);
                storedClass.rename(name);
            }
        }
        
        dbContainer.close();
    }

    public static synchronized DataStoreContainer getInstance()
    {
      if (instance == null)
          instance = new DataStoreContainer();
      
      return instance;
    }
    
    private ArrayList ObjectSetToArrayList(ObjectSet os)
    {
		ArrayList result = new ArrayList();
		while (os.hasNext()) {
			result.add(os.next());
		
		}
		
		return result; 
    }
	
	public ArrayList getTypes()
	{
		ObjectSet result=dbContainer.get(Composestar.Repository.LanguageModel.TypeElement.class);

		return ObjectSetToArrayList(result);

	}

	public ArrayList getMethodElements(Composestar.Repository.LanguageModel.TypeElement type)
	{
		Query query = dbContainer.query();
		query.constrain(Composestar.Repository.LanguageModel.MethodElement.class);
		query.descend("_parentTypeId").constrain(new Integer(type.get_Id()));
		ObjectSet result = query.execute();
					
		return ObjectSetToArrayList(result);
	}
	
	public ArrayList getParameterElements(Composestar.Repository.LanguageModel.MethodElement method)
	{
		Query query = dbContainer.query();
		query.constrain(Composestar.Repository.LanguageModel.ParameterElement.class);
		query.descend("_parentMethodId").constrain(new Integer(method.get_Id()));
		ObjectSet result = query.execute();
					
		return ObjectSetToArrayList(result);
	}
	
	public ArrayList getCallElements(Composestar.Repository.LanguageModel.MethodBody methodBody)
	{
		Query query = dbContainer.query();
		query.constrain(Composestar.Repository.LanguageModel.MethodBody.class);
		query.descend("_parentMethodBodyId").constrain(new Integer(methodBody.get_Id()));
		ObjectSet result = query.execute();
					
		return ObjectSetToArrayList(result);
	}
}
