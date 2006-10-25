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
		try
		{
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(bis);

			int numberofobjects = ois.readInt();

			for (int i = 0; i < numberofobjects; i++)
			{
				ds.addObject(ois.readObject());
			}
			ois.close();
		}
		catch (EOFException eof)
		{
			Debug.out(Debug.MODE_ERROR,"Util","End of file Exception: " + eof.toString());
		}
		catch (FileNotFoundException fne)
		{
			Debug.out(Debug.MODE_ERROR,"Util","File not found: " + fne.getMessage());
		}
		catch (Exception ex)
		{
			Debug.out(Debug.MODE_ERROR,"Util","Exception while deserializing repository: " + ex.getMessage());
		}

		RepositoryFixer.fixRepository(ds);
		return ds;
	}

}
