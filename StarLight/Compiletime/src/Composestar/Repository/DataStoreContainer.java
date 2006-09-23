package Composestar.Repository;

import java.util.ArrayList;
import java.util.List;

import Composestar.DotNET.MASTER.StarLightMaster;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.ext.StoredClass;
import com.db4o.query.Predicate;
import com.db4o.query.Query;

public class DataStoreContainer {
	private static DataStoreContainer instance;
	
	private String yapFileName;
	
	private ObjectContainer dbContainer;

    private DataStoreContainer()
    {
    	this.yapFileName = StarLightMaster.getYapFileName();
		
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
    
    
    private void adjustClassNames()
    {
    	dbContainer = Db4o.openFile(yapFileName);
    	
    	// Unable to open the database
    	if (dbContainer == null) {
    		System.exit(-2);
    	}
    	
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
    
    
    
    

    /**
     * Stores the object.
     * @param o The object to store.
     */
    public void StoreObject(Object o)
    {
        
        if (o == null){
            throw new NullPointerException();
        }
        
        //TODO CheckForOpenDatabase();

        dbContainer.set(o);
    }

    
    /**
     * Gets the objects of a specified class
     * @param theClass The class of which the objects are returned
     * @return A list containing the objects.
     */
    public List getObjects( Class theClass ){
        //TODO CheckForOpenDatabase();
        
        return dbContainer.query( theClass );
    }
    
    /**
     * Gets the objects matching the given Predicate.
     * @param match The predicate for which we want the matching objects.
     * @return A list containing the objects.
     */
    public List getObjectQuery( Predicate match ){
        //TODO CheckForOpenDatabase();
        
        return dbContainer.query( match );
    }

    
    
}
