package Composestar.C.LAMA;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import Composestar.Core.LAMA.LangNamespace;
import Composestar.Core.LAMA.UnitRegister;
import Composestar.Core.LAMA.UnitResult;

public class CDirectory extends LangNamespace
{
	private static final long serialVersionUID = 8508596176425673433L;

	private String dirName;

	private CDirectory parentDir = null;

	private ArrayList subDirs = new ArrayList();

	private ArrayList files = new ArrayList();

	public CDirectory()
	{
		UnitRegister.instance().registerLanguageUnit(this);
	}

	public void setDirName(String dirName)
	{
		this.dirName = dirName;
	}

	public String getDirName()
	{
		return dirName;
	}

	public void addSubDir(CDirectory dir)
	{
		System.out.println("Dir " + dir.getDirName() + "added to" + getDirName());
		subDirs.add(dir);
		super.addChildNamespace(dir);
	}

	public void setParenDir(CDirectory parentDir)
	{
		this.parentDir = parentDir;
		super.setParentNamespace(parentDir);
	}

	public CDirectory getParentDir()
	{
		return parentDir;
	}

	public ArrayList getFiles()
	{
		return files;
	}

	public ArrayList getSubDirs()
	{
		return subDirs;
	}

	public CDirectory getSubDirWithName(String dirName)
	{
		CDirectory subDir;
		Iterator i = subDirs.iterator();
		for (Object subDir1 : subDirs)
		{
			subDir = (CDirectory) subDir1;
			if (subDir.getDirName().equals(dirName))
			{
				return subDir;
			}

		}
		return null;
	}

	public void addFile(CFile file)
	{
		files.add(file);
		super.addChildClass(file);
	}

	public CFile getFileWithName(String fileName)
	{
		CFile file;
		Iterator i = files.iterator();
		for (Object file1 : files)
		{
			file = (CFile) file1;
			if (file.getFullName().equals(fileName))
			{
				return file;
			}

		}
		return null;
	}

	public ArrayList getAllSubDirs()
	{
		CDirectory subdir;
		ArrayList allSubDirs = null;

		if (subDirs != null)
		{
			Iterator subdirIterator = subDirs.iterator();
			for (Object subDir : subDirs)
			{
				subdir = (CDirectory) subDir;
				allSubDirs.add(subdir);
				allSubDirs.addAll(subdir.getAllSubDirs());
			}
		}
		return allSubDirs;
	}

	public ArrayList getAllFiles()
	{
		ArrayList allFilesInSubDirs = null;
		Iterator sdIterator = getAllSubDirs().iterator();
		for (Object o : getAllSubDirs())
		{
			Iterator filesOfSDIterator = ((CDirectory) o).getFiles().iterator();
			for (Object o1 : ((CDirectory) o).getFiles())
			{
				allFilesInSubDirs.add(o1);
			}
		}
		return allFilesInSubDirs;
	}

	public ArrayList getAllFunctions()
	{
		ArrayList allFunctions = new ArrayList();
		for (Object file : files)
		{
			allFunctions.addAll(((CFile) file).getMethods());
		}
		Iterator fileIterator = getAllFiles().iterator();
		while (fileIterator.hasNext())
		{
			allFunctions.addAll(((CFile) fileIterator.next()).getMethods());
		}
		return allFunctions;
	}

	public String getUnitName()
	{
		return dirName;
	}

	public String getUnitType()
	{
		return "Namespace";
	}

	public boolean hasUnitAttribute(String attribute)
	{
		return false;
	}

	public Collection getUnitAttributes()
	{
		return null;
	}

	private HashSet arrayListToHashSet(Collection in)
	{
		HashSet out = new HashSet();
		Iterator iter = in.iterator();
		for (Object obj : in)
		{
			out.add(obj);
		}
		return out;
	}

	public UnitResult getUnitRelation(String argumentName)
	{
		if (argumentName.equals("ChildDirectory"))
		{
			return new UnitResult(arrayListToHashSet(subDirs));
		}
		else if (argumentName.equals("ChildFile"))
		{
			return new UnitResult(arrayListToHashSet(files));
		}
		else if (argumentName.equals("ParentDirectory"))
		{
			return new UnitResult(parentDir);
		}
		return null; // Should never happen!
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		dirName = (String) in.readObject();
		parentDir = (CDirectory) in.readObject();
		subDirs = (ArrayList) in.readObject();
		files = (ArrayList) in.readObject();
	}

	private void writeObject(ObjectOutputStream out) throws IOException
	{
		out.writeObject(dirName);
		out.writeObject(parentDir);
		out.writeObject(subDirs);
		out.writeObject(files);
	}

}
