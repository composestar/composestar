package Composestar.Java.CONE;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.util.Collection;

import Composestar.Core.CONE.CONE;
import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.CpsRepository2.RepositoryEntity;
import Composestar.Core.CpsRepository2.References.ReferenceManager;
import Composestar.Core.CpsRepository2.References.ReferenceUsage;
import Composestar.Core.CpsRepository2Impl.PrimitiveConcern;
import Composestar.Core.CpsRepository2Impl.RepositoryImpl;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.FileUtils;

/**
 * Serializes the repository.
 * 
 * @see Composestar.Core.CONE.RepositorySerializer.
 */
public class JavaRepositorySerializer extends CONE
{
	/**
	 * run method.
	 * 
	 * @throws ModuleException : when an error occurs while serializing the
	 *             repository.
	 */
	public ModuleReturnValue run(CommonResources resources) throws ModuleException
	{
		File repositoryFilename = new File(resources.configuration().getProject().getIntermediate(), "repository.dat");

		resources.put(REPOSITORY_FILE_KEY, repositoryFilename);

		logger.info("writing repository to file " + repositoryFilename + " ...");

		Repository repos = resources.repository();
		// create a smaller repository for the runtime
		Repository ds = new RepositoryImpl();
		ReferenceManager refman = resources.get(ReferenceManager.RESOURCE_KEY);
		for (RepositoryEntity re : repos)
		{
			if (re instanceof PrimitiveConcern)
			{
				PrimitiveConcern pc = (PrimitiveConcern) re;
				Collection<ReferenceUsage> refusers = refman.getReferenceUsage(pc.getTypeReference());
				if (refusers.isEmpty() && pc.getSuperimposed() == null)
				{
					logger.trace(String.format("PrimitiveConcern %s not used", pc.getFullyQualifiedName()));
					continue;
				}
			}
			ds.add(re);
		}
		logger.debug(String.format("Repository size shrunk to %d from %d", ds.size(), repos.size()));

		ObjectOutputStream oos = null;
		try
		{
			FileOutputStream fos = new FileOutputStream(repositoryFilename);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			oos = new ObjectOutputStream(bos);
			oos.writeObject(ds);
			logger.info("repository has been serialized");
		}
		catch (StackOverflowError ex)
		{
			throw new ModuleException("Need more stack size to serialize repository: " + ex.toString(),
					ModuleNames.CONE);
		}
		catch (NotSerializableException e)
		{
			throw new ModuleException("Unserializable class encountered: " + e.toString(), ModuleNames.CONE);
		}
		catch (Exception e)
		{
			throw new ModuleException("Error occured while serializing repository: " + e.toString(), ModuleNames.CONE);
		}
		finally
		{
			FileUtils.close(oos);
		}
		return ModuleReturnValue.OK;
	}
}
