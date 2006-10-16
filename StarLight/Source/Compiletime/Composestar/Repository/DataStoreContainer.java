package Composestar.Repository;

import java.util.List;

import Composestar.DotNET.MASTER.StarLightMaster;
import Composestar.Repository.LanguageModel.*;
import Composestar.Repository.LanguageModel.Inlining.Block;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ext.StoredClass;
import com.db4o.query.Predicate;

public class DataStoreContainer {
	private static DataStoreContainer instance;
	
	private String yapFileName;
	
	private ObjectContainer dbContainer;

    private DataStoreContainer()
    {
    	this.yapFileName = StarLightMaster.getYapFileName();
		
		adjustClassNames();
		
		Db4o.configure().callConstructors(false);
        
        Db4o.configure().objectClass( MethodElement.class ).cascadeOnActivate( true );
        Db4o.configure().objectClass( MethodElement.class ).cascadeOnUpdate( true );
        Db4o.configure().objectClass( Block.class ).cascadeOnActivate( true );
        Db4o.configure().objectClass( Block.class ).cascadeOnUpdate( true );

        
		// Indexes
        Db4o.configure().objectClass(AssemblyElement.class).objectField("_fileName").indexed(true);
        
        Db4o.configure().objectClass(TypeElement.class).objectField("_id").indexed(true);
        Db4o.configure().objectClass(TypeElement.class).objectField("_fullName").indexed(true);
        Db4o.configure().objectClass(TypeElement.class).objectField("_assembly").indexed(true);
        Db4o.configure().objectClass(TypeElement.class).objectField("_fromDLL").indexed(true);

        Db4o.configure().objectClass(FieldElement.class).objectField("_parentTypeId").indexed(true);

        Db4o.configure().objectClass(MethodElement.class).objectField("_parentTypeId").indexed(true);
        Db4o.configure().objectClass(MethodElement.class).objectField("_signature").indexed(true);

        Db4o.configure().objectClass(ParameterElement.class).objectField("_parentMethodId").indexed(true);

        Db4o.configure().objectClass(AttributeElement.class).objectField("_parentId").indexed(true);
        
        Db4o.configure().objectClass(CallElement.class).objectField("_parentMethodBodyId").indexed(true);
        
        dbContainer = Db4o.openFile(yapFileName);
        
        // Debug native query optimization
        //((com.db4o.YapStream)dbContainer).getNativeQueryHandler().addListener(new com.db4o.inside.query.Db4oQueryExecutionListener() {  public void notifyQueryExecuted(com.db4o.inside.query.NQOptimizationInfo info) { System.out.println(info.message() + " - " + info.predicate().toString());  }});
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

	/**
	 * 
	 * @see com.db4o.ObjectContainer#commit()
	 */
	public void commit()
	{
		dbContainer.commit();
	}

	/**
	 * 
	 * @see com.db4o.ObjectContainer#rollback()
	 */
	public void rollback()
	{
		dbContainer.rollback();
	}

    
    
}
