package Composestar.Java.CONE;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.util.Iterator;

import Composestar.Core.CONE.RepositorySerializer;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataMap;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Serializes the repository.
 * 
 * @see Composestar.Core.CONE.RepositorySerializer.
 */
public class JavaRepositorySerializer implements RepositorySerializer
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("CONE");

	/**
	 * run method.
	 * 
	 * @throws ModuleException : when an error occurs while serializing the
	 *             repository.
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		File repositoryFilename = new File(resources.configuration().getProject().getIntermediate(), "repository.dat");

		resources.add(REPOSITORY_FILE_KEY, repositoryFilename);

		logger.info("writing repository to file " + repositoryFilename + " ...");

		DataStore ds = DataStore.instance();

		ds.excludeUnreferenced(PrimitiveConcern.class);

		ObjectOutputStream oos = null;
		try
		{
			DataMap.setRtSerialization(true);

			FileOutputStream fos = new FileOutputStream(repositoryFilename);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			oos = new ObjectOutputStream(bos);

			// collect the objects
			Iterator it = ds.getIterator();

			// write the objects
			while (it.hasNext())
			{
				Object item = it.next();
				oos.writeObject(item);
			}

			logger.info("repository has been serialized");
		}
		catch (StackOverflowError ex)
		{
			throw new ModuleException("Need more stack size to serialize repository: " + ex.toString(), "CONE");
		}
		catch (NotSerializableException e)
		{
			throw new ModuleException("Unserializable class encountered: " + e.toString(), "CONE");
		}
		catch (Exception e)
		{
			throw new ModuleException("Error occured while serializing repository: " + e.toString(), "CONE");
		}
		finally
		{
			DataMap.setRtSerialization(false);
			FileUtils.close(oos);
		}
	}
}
