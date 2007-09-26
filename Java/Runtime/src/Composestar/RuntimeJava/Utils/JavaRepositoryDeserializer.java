package Composestar.RuntimeJava.Utils;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import Composestar.Core.RepositoryImplementation.DataMap;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;
import Composestar.RuntimeCore.Utils.Debug;
import Composestar.RuntimeCore.Utils.RepositoryDeserializer;

public class JavaRepositoryDeserializer extends RepositoryDeserializer
{
	protected Class<?> mainclass;

	public JavaRepositoryDeserializer(Class<?> mclass)
	{
		mainclass = mclass;
	}

	public DataStore deserialize(String file)
	{
		DataMap.setRtSerialization(true);
		DataStore ds = DataStore.instance();

		ObjectInputStream ois = null;
		try
		{
			InputStream is = mainclass.getResourceAsStream("/" + file);
			if (is == null)
			{
				is = new FileInputStream(new File(file));
			}
			BufferedInputStream bis = new BufferedInputStream(is);
			ois = new ObjectInputStream(bis);

			while (true /* fis.available() != 0 */) // available() isn't
			// reliable
			{
				Object o = ois.readObject();
				// System.err.println("Adding object '"+o+"'");
				if (o instanceof RepositoryEntity)
				{
					// System.err.println("Adding RE with key: " +
					// ((RepositoryEntity) o).repositoryKey);
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

		// fixing not needed for native serialization
		// RepositoryFixer.fixRepository(ds);
		return ds;
	}

}
