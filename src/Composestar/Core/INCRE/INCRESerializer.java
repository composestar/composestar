package Composestar.Core.INCRE;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Iterator;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;

public class INCRESerializer implements CTCommonModule
{
	private static final String MODULE_NAME = "INCRESerializer";
	
	public INCRESerializer()
	{
		
	}
	
	public void run(CommonResources resources) throws ModuleException
	{
		String incre_enabled = Configuration.instance().getModuleProperty(INCRE.MODULE_NAME, "enabled", "false");
			
		if("true".equalsIgnoreCase(incre_enabled))
		{
			// only serialize when incremental compilation is enabled
			INCRE incre = INCRE.instance();
			INCRETimer increhistory = incre.getReporter().openProcess("INCRESerializer","Creation of INCRE history",INCRETimer.TYPE_OVERHEAD);
			storeHistory();
			increhistory.stop();
		}
	}
	
	/**
	 * Writes the repository to disk to provide incremental compilation
	 */
	public void storeHistory() throws ModuleException
	{
		INCRE incre = INCRE.instance();
		Debug.out(Debug.MODE_DEBUG, INCRE.MODULE_NAME, "Comparator made " + incre.comparator.getCompare() + " comparisons");

		DataStore ds = DataStore.instance();
		Configuration config = Configuration.instance();

		ObjectOutputStream oos = null;
		try
		{
			FileOutputStream fos = new FileOutputStream(INCRE.instance().historyfile);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			oos = new ObjectOutputStream(bos);

			// write current date = last compilation date
			oos.writeObject(new Date());

			// write project configurations
			oos.writeObject(Configuration.instance());

			// collect the objects
			int count = ds.size(), stored = 0;
			oos.writeInt(count - 1);

			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "Up to " + count + " objects to store");

			Iterator it = ds.getIterator();
			while (it.hasNext())
			{
				Object item = it.next();
				if (item != null)
				{
					oos.writeObject(item);
					stored++;
				}
			}

			Debug.out(Debug.MODE_DEBUG, MODULE_NAME, "" + stored + " objects have been stored");
		}
		catch (Exception e)
		{
			throw new ModuleException("Error occured while creating history: " + e.toString(), MODULE_NAME);
		}
		finally
		{
			FileUtils.close(oos);
		}
	}
}
