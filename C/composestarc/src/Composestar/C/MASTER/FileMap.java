package Composestar.C.MASTER;

import Composestar.C.wrapper.*;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * @modelguid {A1641B28-AC6E-42C3-A196-95AEBCF46B38}
 */
public class FileMap implements Serializable, Cloneable {

   public static final long serialVersionUID = -1135392544932797436L;

  private static FileMap Instance = null;
  public Hashtable fileASTMap;
  
  public FileMap() {
	      fileASTMap = new Hashtable();
	  }
  
  public static FileMap instance() {
	   if (Instance == null) {
	      Instance = new FileMap();
	   }
	   return (Instance);
  }
  
  public static void setInstance(FileMap fm)
	{
		Instance = fm;
	}
  
  public Iterator getIterator() {
	     return (fileASTMap.values().iterator());
  }

  public void addFileASTs(Hashtable fileASTs){
	 this.fileASTMap.putAll(fileASTs);
  }
  public void addFileAST(String name , retrieveAST rast){
	  this.fileASTMap.put(name,rast);
   }
  
  public Hashtable getFileASTs(){
	 return this.fileASTMap;
  }
  public String getFileASTwithName(String fileName){
	  Iterator fileit=fileASTMap.values().iterator();
	  while(fileit.hasNext()){
		  String fn = ((retrieveAST) fileit.next()).getFilename();
		 if( fn.indexOf(fileName)!=-1){
			 return fn;
		 }
	  }
	  return "";
  }
  
}