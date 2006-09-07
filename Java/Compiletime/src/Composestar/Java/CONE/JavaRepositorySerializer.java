package Composestar.Java.CONE;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;

import Composestar.Core.CONE.RepositorySerializer;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.RepositoryImplementation.DataMap;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.RepositoryImplementation.RepositoryEntity;
import Composestar.Core.RepositoryImplementation.SerializableRepositoryEntity;
import Composestar.Utils.Debug;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

public class JavaRepositorySerializer implements RepositorySerializer 
{

	private PrintWriter out =null;
	private Hashtable orderedFieldInfo;
	
	public void run(CommonResources resources) throws ModuleException 
	{
		
		String repositoryFilename = Configuration.instance().getPathSettings().getPath("Base") + "repository.dat";
				
		orderedFieldInfo = new Hashtable();
		
		Debug.out(Debug.MODE_DEBUG, "CONE", "writing repository to file "+repositoryFilename+" ...");
		
		DataStore ds = DataStore.instance();
		
		ds.map.excludeUnreferenced(PrimitiveConcern.class);
		
		try
		{
			FileOutputStream fos = new FileOutputStream(repositoryFilename);
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			   		   
			// collect the objects
			Object[] objects = ds.getAllObjects();
			oos.writeInt(objects.length-1);
			for(int k=1;k<objects.length;k++)
			{
				// write objects
				if(objects[k]!=null)
				{
					oos.writeObject(objects[k]);
					oos.flush();
				}
			}
			Debug.out(Debug.MODE_DEBUG, "CONE","repository has been serialized");
			oos.close();
		}
		catch(StackOverflowError ex)
		{
			throw new ModuleException("Need more stack size to serialize repository: "+ex.toString(),"CONE");
		}
		catch(Exception e)
		{
			throw new ModuleException("Error occured while serializing repository: "+e.toString(),"CONE");
		}
	}
}
