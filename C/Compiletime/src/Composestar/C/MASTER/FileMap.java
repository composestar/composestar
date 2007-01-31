package Composestar.C.MASTER;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Composestar.C.wrapper.retrieveAST;

/**
 * 
 */
public class FileMap implements Serializable
{

	public static final long serialVersionUID = -1135392544932797436L;

	private static FileMap Instance = null;

	public Map<String,retrieveAST> fileASTMap;

	public FileMap()
	{
		fileASTMap = new HashMap<String,retrieveAST>();
	}

	public static FileMap instance()
	{
		if (Instance == null)
		{
			Instance = new FileMap();
		}
		return (Instance);
	}

	public static void setInstance(FileMap fm)
	{
		Instance = fm;
	}

	public Iterator getIterator()
	{
		return (fileASTMap.values().iterator());
	}

	public void addFileASTs(Map<String,retrieveAST> fileASTs)
	{
		fileASTMap.putAll(fileASTs);
	}

	public void addFileAST(String name, retrieveAST rast)
	{
		fileASTMap.put(name, rast);
	}

	public Map<String,retrieveAST> getFileASTs()
	{
		return fileASTMap;
	}

	public String getFileASTwithName(String fileName)
	{
		for (Object o : fileASTMap.values())
		{
			String fn = ((retrieveAST) o).getFilename();
			if (fn.indexOf(fileName) != -1)
			{
				return fn;
			}
		}
		return "";
	}

}
