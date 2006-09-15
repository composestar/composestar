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
	
	private static String yapFileName;
	
	private ObjectContainer dbContainer;

    private DataStoreContainer()
    {
    	//Werkt alleen de 1ste keer, daarna is de db gewijzigd en mapped ie naar niet bestaande stored types
		//Db4o.configure().addAlias(
  		//		new WildcardAlias(
    	//			"Composestar.Repository.LanguageModel.*, Repository.LanguageModel", "Composestar.Repository.LanguageModel.*"));

		if (yapFileName == null) yapFileName = "database.yap";
		
		adjustClassNames();
		
		Db4o.configure().callConstructors(false);

		// Indexes
        Db4o.configure().objectClass(Composestar.Repository.LanguageModel.FieldElement.class).objectField("_parentTypeId").indexed(true);
		Db4o.configure().objectClass(Composestar.Repository.LanguageModel.MethodElement.class).objectField("_parentTypeId").indexed(true);
        Db4o.configure().objectClass(Composestar.Repository.LanguageModel.ParameterElement.class).objectField("_parentMethodId").indexed(true);
        Db4o.configure().objectClass(Composestar.Repository.LanguageModel.CallElement.class).objectField("_parentMethodBodyId").indexed(true);
        
        
        dbContainer = Db4o.openFile(yapFileName);
    }
    
    protected void finalize () {
    	dbContainer.close();
    }
    
    public static void setYapFileName(String fileName)
    {
    	yapFileName = fileName;
    }
    
    private void adjustClassNames()
    {
    	dbContainer = Db4o.openFile(yapFileName);

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
    
    public Composestar.Repository.Configuration.CommonConfiguration GetCommonConfiguration()
    {
		Query query = dbContainer.query();
		query.constrain(Composestar.Repository.Configuration.CommonConfiguration.class);
		ObjectSet result = query.execute();   	
    	
        if (result.size() == 1)
            return (Composestar.Repository.Configuration.CommonConfiguration)result.get(0);
        else 
            return null;   	
    }
    
    public ArrayList GetConcernInformation()
    {
		ObjectSet result=dbContainer.get(Composestar.Repository.Configuration.ConcernInformation.class);

		return ObjectSetToArrayList(result);   	
    }
	
    public void addTypeElement(Composestar.Repository.LanguageModel.TypeElement typeElement)
    {
        dbContainer.set(typeElement);
    }
    
    public Composestar.Repository.LanguageModel.TypeElement GetTypeElement(String fullName)
    {
		Query query = dbContainer.query();
		query.constrain(Composestar.Repository.LanguageModel.TypeElement.class);
		query.descend("_fullName").constrain(fullName);
		ObjectSet result = query.execute();   	
    	
        if (result.size() == 1)
            return (Composestar.Repository.LanguageModel.TypeElement)result.get(0);
        else 
            return null;
    }
    
	public ObjectSet getTypeElements()
	{
		return dbContainer.get(Composestar.Repository.LanguageModel.TypeElement.class);
	}
	
	public ObjectSet getFieldElements(Composestar.Repository.LanguageModel.TypeElement type)
	{
		Query query = dbContainer.query();
		query.constrain(Composestar.Repository.LanguageModel.FieldElement.class);
		query.descend("_parentTypeId").constrain(new Integer(type.get_Id()));
		return query.execute();
	}	

	public ObjectSet getMethodElements(Composestar.Repository.LanguageModel.TypeElement type)
	{
		Query query = dbContainer.query();
		query.constrain(Composestar.Repository.LanguageModel.MethodElement.class);
		query.descend("_parentTypeId").constrain(new Integer(type.get_Id()));
		return query.execute();
	}
	
	public ObjectSet getParameterElements(Composestar.Repository.LanguageModel.MethodElement method)
	{
		Query query = dbContainer.query();
		query.constrain(Composestar.Repository.LanguageModel.ParameterElement.class);
		query.descend("_parentMethodId").constrain(new Integer(method.get_Id()));
		return query.execute();
	}
	
	public ObjectSet getCallElements(Composestar.Repository.LanguageModel.MethodBody methodBody)
	{
		Query query = dbContainer.query();
		query.constrain(Composestar.Repository.LanguageModel.MethodBody.class);
		query.descend("_parentMethodBodyId").constrain(new Integer(methodBody.get_Id()));
		return query.execute();
	}
}
