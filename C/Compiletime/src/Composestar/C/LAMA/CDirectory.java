package Composestar.C.LAMA;

import Composestar.Core.LAMA.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class CDirectory extends LangNamespace {

	private String dirName;
	private CDirectory parentDir = null;
	private ArrayList subDirs =new ArrayList();
	private ArrayList files = new ArrayList();
	
	public CDirectory()
    {
    	UnitRegister.instance().registerLanguageUnit(this);
    }
	
	public void setDirName(String dirName){
		this.dirName=dirName;
	}
	
	public String getDirName(){
		return dirName;
	}
	
	public void addSubDir(CDirectory dir){
		System.out.println("Dir "+dir.getDirName()+"added to"+getDirName());
		subDirs.add(dir);
		super.addChildNamespace(dir);
	}
	
	public void setParenDir(CDirectory parentDir){
		this.parentDir=parentDir;
		super.setParentNamespace(parentDir);
	}
	
	public CDirectory getParentDir(){
		return this.parentDir;
	}
	
	public ArrayList getFiles(){
		return files;
	}
	
	public ArrayList getSubDirs(){
		return subDirs;
	}
	
	public CDirectory getSubDirWithName(String dirName){
		CDirectory subDir;
		Iterator i=subDirs.iterator();
		while(i.hasNext())
		{
			subDir=(CDirectory)i.next();
			if(subDir.getDirName().equals(dirName))
			{
				return subDir;
			}
			
		}
		return null;
	}
	
	public void addFile(CFile file){
		files.add(file);
		super.addChildClass(file);
	}
	
	public CFile getFileWithName(String fileName){
		CFile file;
		Iterator i=files.iterator();
		while(i.hasNext())
		{
			file=(CFile)i.next();
			if(file.getFullName().equals(fileName))
			{
				return file;
			}
			
		}
		return null;
	}
	
	public ArrayList getAllSubDirs(){
		CDirectory subdir;
		ArrayList allSubDirs= null;
		
		if(subDirs != null)
		{
			Iterator subdirIterator = subDirs.iterator();
			while(subdirIterator.hasNext()){
				subdir =(CDirectory)subdirIterator.next();
				allSubDirs.add(subdir);
				allSubDirs.addAll(subdir.getAllSubDirs());
			}
		}
		return allSubDirs;
	}
	
	public ArrayList getAllFiles(){
		ArrayList allFilesInSubDirs =null;
		Iterator sdIterator = getAllSubDirs().iterator();
		while(sdIterator.hasNext()){
			Iterator filesOfSDIterator = ((CDirectory)sdIterator.next()).getFiles().iterator();
			while(filesOfSDIterator.hasNext()){
				allFilesInSubDirs.add(filesOfSDIterator.next());
			}
		}
		return allFilesInSubDirs;
	}
	
	public ArrayList getAllFunctions(){
		ArrayList allFunctions = new ArrayList();
		Iterator fileIterator = files.iterator();
		while(fileIterator.hasNext()){
			allFunctions.addAll(((CFile)fileIterator.next()).getMethods());
		}
		fileIterator = getAllFiles().iterator();
		while(fileIterator.hasNext()){
			allFunctions.addAll(((CFile)fileIterator.next()).getMethods());
		}
		return allFunctions;
	}
	
	 public String getUnitName(){
		 return dirName;
	 }

	  public String getUnitType(){
	    	return "Namespace";
	  }

	  public boolean hasUnitAttribute(String attribute){
		  return false;
	  }
	  
	  public Collection getUnitAttributes(){
		  return null;
	  }
	  
	  private HashSet arrayListToHashSet(Collection in)
	    {
	      HashSet out = new HashSet();
	      Iterator iter = in.iterator();
	      while (iter.hasNext())
	      {
	          Object obj = iter.next();
	          out.add(obj);
	      }
	      return out;
	    }

	  public UnitResult getUnitRelation(String argumentName){
		  	if (argumentName.equals("ChildDirectory"))
	    			return new UnitResult(arrayListToHashSet(subDirs));
	    	else if (argumentName.equals("ChildFile"))
	    			return new UnitResult(arrayListToHashSet(files));
	    	else if (argumentName.equals("ParentDirectory"))
		    		return new UnitResult(parentDir);
	    	return null; // Should never happen!
	  }
	 
	  private void readObject(ObjectInputStream in) throws IOException,ClassNotFoundException
		{
		  	dirName = (String)in.readObject();
			parentDir = (CDirectory)in.readObject();
			subDirs = (ArrayList)in.readObject();
			files = (ArrayList)in.readObject();
		}
		 
		private void writeObject(ObjectOutputStream out) throws IOException
		{
			out.writeObject(dirName);
			out.writeObject(parentDir);
			out.writeObject(subDirs);
			out.writeObject(files);
		}
	
	
}
