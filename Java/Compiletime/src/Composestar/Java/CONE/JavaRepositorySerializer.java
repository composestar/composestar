package Composestar.Java.CONE;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;

import Composestar.Core.CONE.RepositorySerializer;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;

/**
 * Serializes the repository.
 * 
 * @see Composestar.Core.CONE.RepositorySerializer.
 */
public class JavaRepositorySerializer implements RepositorySerializer
{
	private Hashtable orderedFieldInfo;

	/**
	 * run method.
	 * 
	 * @throws ModuleException : when an error occurs while serializing the
	 *             repository.
	 */
	public void run(CommonResources resources) throws ModuleException
	{

		String repositoryFilename = Configuration.instance().getPathSettings().getPath("Base") + "repository.dat";

		orderedFieldInfo = new Hashtable();

		Debug.out(Debug.MODE_DEBUG, "CONE", "writing repository to file " + repositoryFilename + " ...");

		DataStore ds = DataStore.instance();

		ds.map.excludeUnreferenced(PrimitiveConcern.class);

		try
		{
			FileOutputStream fos = new FileOutputStream(repositoryFilename);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(bos);

			// collect the objects
			Object[] objects = ds.getAllObjects();
			oos.writeInt(objects.length - 1);
			for (int k = 1; k < objects.length; k++)
			{
				// write objects
				if (objects[k] != null)
				{
					oos.writeObject(objects[k]);
					oos.flush();
				}
			}
			Debug.out(Debug.MODE_DEBUG, "CONE", "repository has been serialized");
			oos.close();
		}
		catch (StackOverflowError ex)
		{
			throw new ModuleException("Need more stack size to serialize repository: " + ex.toString(), "CONE");
		}
		catch (Exception e)
		{
			throw new ModuleException("Error occured while serializing repository: " + e.toString(), "CONE");
		}
	}
}
