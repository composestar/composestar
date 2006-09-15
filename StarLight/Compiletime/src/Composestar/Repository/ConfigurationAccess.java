package Composestar.Repository;

import java.util.ArrayList;

import com.db4o.ObjectSet;
import com.db4o.query.Query;

public class ConfigurationAccess {
	
	public int getCompiletimeDebugLevel() 
	{
		if ( DataStoreContainer.getInstance().GetCommonConfiguration() == null )
		{
			return 0;
		}
		
		return DataStoreContainer.getInstance().GetCommonConfiguration().get_CompiletimeDebugLevel();
	}
    
	public ArrayList getConcerns()
	{
		return DataStoreContainer.getInstance().GetConcernInformation();
	}
}
