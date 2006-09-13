package Composestar.Repository;

import java.util.List;
import java.util.ArrayList;
import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

public class DataStoreContainer {
	private static DataStoreContainer instance;
	
	private ObjectContainer dbContainer;

    private DataStoreContainer()
    {
		Db4o.configure().addAlias(
  				new WildcardAlias(
    				"Composestar.Repository.LanguageModel.*, Repository.LanguageModel", "Composestar.Repository.LanguageModel.*"));

		
		dbContainer = Db4o.openFile("test.yap");

    
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
		//SLLLLLLLOOOOOOOOOOWWWWWWWWWWWWWWWWWWWWWWWWWWW
		final int finaltypeid = typeid;
		
		List methods = dbContainer.query(new Predicate() {
		    public boolean match(Composestar.Repository.LanguageModel.MethodInfo mi) {
		        return mi.get_TypeId() == finaltypeid;
		    }
		});
		
		return methods.size();
	
	}
	
	
}
