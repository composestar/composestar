/*
 * Contains utility methods that have to do with file handling;
 *  e.g. converting backslashes in filenames to slashes
 * 
 * Created on Nov 11, 2004 by wilke
 */
package Composestar.Utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import Composestar.Core.Exception.ModuleException;

public class FileUtils
{
  /**
   * This method converts filenames so they contain only 'slashes' instead of backslashes.
   * This works on all platforms (Java will automatically convert to backslashes on the Windows
   * platform, which is about the only platform using backslashes as a directory separator).
   * 
   * Because backslashes are often interpreted as escape characters (e.g. by the prolog engine!)
   * we can't have them in filenames - besides, it would only work in Windows.
   * 
   * @param name A filename, possibly containing backslashes
   * @return The filename, with backslashes converted to slashes.
   */
  public static String fixFilename(String name)
  {
    return name.replace('\\', '/');
  } 
  
  public static void copyFile(String dst, String src) throws ModuleException 
  {
	try  
	{
		FileInputStream fis = new FileInputStream(src);
		BufferedInputStream bis = new BufferedInputStream(fis);
		FileOutputStream fos = new FileOutputStream(dst);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
	    
	    // transfer bytes from in to out
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = bis.read(buf)) > 0) {
	        bos.write(buf, 0, len);
	    }
	    bis.close();
	    bos.close();
	}
	catch(IOException e)
	{
		throw new ModuleException( "Error while copying file!:\n" + e.getMessage());
	}
  }
  
  public static String removeExtension(String filename)
  {
	  int lastdot = filename.lastIndexOf(".");
	  if(lastdot>0){
		  return filename.substring(0,lastdot);
	  }
	  
	  return filename;
  }
}
