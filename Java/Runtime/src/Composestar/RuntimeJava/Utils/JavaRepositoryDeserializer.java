package Composestar.RuntimeJava.Utils;

import Composestar.Core.RepositoryImplementation.DataMap;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;
import Composestar.RuntimeCore.Utils.Debug;
import Composestar.RuntimeCore.Utils.RepositoryDeserializer;

import java.io.*;

public class JavaRepositoryDeserializer extends RepositoryDeserializer
{

	public DataStore deserialize(String file)
	{
		DataMap.setRtSerialization(true);
		DataStore ds = DataStore.instance();

		ObjectInputStream ois = null;
		try
		{
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ois = new ObjectInputStream(bis);

			while (true /*fis.available() != 0*/) //available() isn't reliable
			{
				Object o = ois.readObject();
				//System.err.println("Adding object '"+o+"'");
				if (o instanceof RepositoryEntity)
				{
					//System.err.println("Adding RE with key: " + ((RepositoryEntity) o).repositoryKey);
					ds.addObject(((RepositoryEntity) o).repositoryKey, o);
				}
				else
				{
					ds.addObject(o);
				}
			}
		}
		catch (EOFException eof)
		{
			// no need to print something, EOFException will always happen.
		}
		catch (FileNotFoundException fne)
		{
			Debug.out(Debug.MODE_ERROR, "Util", "File not found: " + fne.getMessage());
		}
		catch (Exception ex)
		{
			Debug.out(Debug.MODE_ERROR, "Util", "Exception while deserializing repository: " + ex.getMessage());
		}
		finally
		{
			DataMap.setRtSerialization(false);
			try
			{
				if (ois != null)
				{
					ois.close();
				}
			}
			catch (IOException e)
			{
				throw new RuntimeException("Unable to close stream: " + e.getMessage());
			}
		}

		//fixing not needed for native serialization
		//RepositoryFixer.fixRepository(ds);
		return ds;
	}

}
