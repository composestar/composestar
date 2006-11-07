package Composestar.RuntimeJava.Utils;

import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.RuntimeCore.Utils.Debug;
import Composestar.RuntimeCore.Utils.RepositoryDeserializer;

import java.io.*;

public class JavaRepositoryDeserializer extends RepositoryDeserializer
{

	public DataStore deserialize(String file)
	{

		DataStore ds = DataStore.instance();
		
		ObjectInputStream ois = null;
		try
		{
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ois = new ObjectInputStream(bis);

			while(true)
			{
				ds.addObject(ois.readObject());
			}
		}
		catch (EOFException eof)
		{
			// no need to print something, EOFException will always happen.
		}
		catch (FileNotFoundException fne)
		{
			Debug.out(Debug.MODE_ERROR,"Util","File not found: " + fne.getMessage());
		}
		catch (Exception ex)
		{
			Debug.out(Debug.MODE_ERROR,"Util","Exception while deserializing repository: " + ex.getMessage());
		}
		finally
		{
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

		RepositoryFixer.fixRepository(ds);
		return ds;
	}

}
