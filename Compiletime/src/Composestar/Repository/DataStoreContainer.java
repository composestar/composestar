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
    

	
	public ArrayList getTypes()
	{
		
		//db.set(new Composestar.Repository.LanguageModel.TypeInfo());
		ObjectSet result=dbContainer.get(Composestar.Repository.LanguageModel.TypeInfo.class);
//System.out.println("GetTypes says: " + result.size());
		//return (Composestar.Repository.LanguageModel.TypeInfo[])result.toArray();
		ArrayList types = new ArrayList();
		while (result.hasNext()) {
			types.add(result.next());
		
		}
		
		return types;
	}
	
	public int getMethods(int typeid)
	{
		if (_methods == null) {
		

			_methods = new Hashtable();
		
			Query query = dbContainer.query();
			query.constrain(Composestar.Repository.LanguageModel.MethodInfo.class);

			ObjectSet result = query.execute();
			
			while (result.hasNext())
			{
				ArrayList m = null;
				Composestar.Repository.LanguageModel.MethodInfo mi = (Composestar.Repository.LanguageModel.MethodInfo)result.next();
				if (_methods.containsKey(new Integer(mi.get_TypeId())))
				{
					m = (ArrayList)_methods.get(new Integer(mi.get_TypeId()));
				}
				else {
					m = new ArrayList();

				}
				m.add(mi);
				_methods.put(new Integer(mi.get_TypeId()), m);				
			}
		
		}
		
		if (_methods.containsKey(new Integer(typeid)))
		{
			return ((ArrayList)_methods.get(new Integer(typeid))).size();
		
		}
			
		return 0;
	}
	
	
}
