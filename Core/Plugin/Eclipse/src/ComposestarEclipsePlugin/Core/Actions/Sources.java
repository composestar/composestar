package ComposestarEclipsePlugin.Core.Actions;

import ComposestarEclipsePlugin.Core.Debug;
import ComposestarEclipsePlugin.Core.BuildConfiguration.BuildConfigurationManager;
import ComposestarEclipsePlugin.Core.BuildConfiguration.Project;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public class Sources {
	
	private IProject[] selectedProjects;
	private IProject project;
	private IResource[] projectMembers= null;
	private Project projectConfig= null;
			
	public Sources(IProject[] selectedProjects){
		this.selectedProjects = selectedProjects;
	}
	
	public HashSet getSources(String extension, HashSet skiplist)
	{
		String languageTest = null;
		HashSet list = new HashSet();
		String folder = "";
	    
		if(selectedProjects.length == 1){
			project = selectedProjects[0];
		}
		try
		{
			projectMembers = project.members();
		}
		catch(CoreException ce){}
		
		for(int i =0; i<projectMembers.length; i++ ){
			if(projectMembers[i].getType()==IResource.FILE){
				languageTest=(((IFile)projectMembers[i]).getName()).substring(((((IFile)projectMembers[i]).getName()).indexOf('.')+1));
				
				if(languageTest.equalsIgnoreCase(extension)){
					list.add(((IFile)projectMembers[i]).getFullPath());
				}
			}
			if(projectMembers[i].getType()==IResource.FOLDER){
				folder = projectMembers[i].getLocation().toPortableString().toString()+"/";
				if(skiplist.contains(folder))
				{
					// skip folder
				}
				else
				{
				list.addAll(getAllFilesFromDirectory(projectMembers[i],list, extension));
				}
			}
		}
		return list;
	}
	
	public void setSources(HashSet list, IPath workspace){
		Iterator l=list.iterator();
		workspace=workspace.removeLastSegments(1);
		while(l.hasNext()){
			IPath test = (IPath)l.next();
			test=workspace.append(test);
			projectConfig.addSource(test.toOSString());
		}
	}
	
	public void setConcernSources(HashSet list, IPath workspace){
		Iterator l=list.iterator();
		workspace=workspace.removeLastSegments(1);
		while(l.hasNext()){
			IPath test = (IPath)l.next();
			test=workspace.append(test);
			BuildConfigurationManager.instance().setConcernSources(test.toOSString());
		}
	}
	
	public HashSet getAllFilesFromDirectory(IResource dir, HashSet list, String extension) 
    {	
	 	String languageTest= null;
    	if (dir.getType()==IResource.FOLDER)
        {
    		IResource[] children = null;
            try {
				children = ((IFolder)dir).members();
			} catch (CoreException e) {
				Debug.instance().Log("Exception:"+dir.getName());
				e.printStackTrace();
			}
			for (int i=0; i<children.length; i++)
            {
				if(children[i].getType()==IResource.FILE){
            		languageTest=(((IFile)children[i]).getName()).substring(((((IFile)children[i]).getName()).lastIndexOf('.')+1));
            		
            		if((languageTest).equalsIgnoreCase(extension)){
            			list.add(((IFile)children[i]).getFullPath());//toOSString	
            		}
            	}
                else if(children[i].getType()==IResource.FOLDER)
                {
        	  		list.addAll(getAllFilesFromDirectory(children[i],list, extension));
                }
             }
        }
    	return list;
    }
	public String getRelativePathtoWorkSpace(String file) throws CoreException{
	    	String oldfile =file;
	    	if(selectedProjects[0].getFile(file).exists()){
	    		IFile ifi=(IFile)selectedProjects[0].findMember(file);
	    		file=ifi.getFullPath().toString();
	    	}
	    	else
	    	{
	    		IResource[] memb= selectedProjects[0].members();
	    		
	    		for(int i =0; i<memb.length; i++ ){
	    			if(memb[i].getType()==IResource.FOLDER && file.equals(oldfile)){
	    				if(((IFolder)memb[i]).getFile(file).exists()){
	    					IFile ifi2=(IFile)((IFolder)memb[i]).findMember(file);
	    		    		file=ifi2.getFullPath().toString();
	    		    	}  
	    				file=checkFoldersForFile(memb[i],file);	
	    			}
	    			
	    		}
	    	}
	    	if(file.equals(oldfile)){
	    		Debug.instance().Log("Selected file is not in the Project");
	    		return "File is not in the  Project";
	    	}  
	    	file=file.substring(1,(file).indexOf('.'));
	    	file=file.replace('/','.');
	    	return file;
	    }
	    
	public String checkFoldersForFile(IResource dir, String file) 
	    {	
		  	if (dir.getType()==IResource.FOLDER)
	        {
	    		IResource[] children = null;
	            try {
					children = ((IFolder)dir).members();
				} catch (CoreException e) {
					Debug.instance().Log("Exception:"+dir.getName());
					e.printStackTrace();
				}
				for (int i=0; i<children.length; i++)
	            {
					if(children[i].getType()==IResource.FILE){
						if(((IFile)children[i]).getName().equals(file)){
	            			file=((IFile)children[i]).getFullPath().toString();
	            			return file;}
	    			}
	                else if(children[i].getType()==IResource.FOLDER)
	                {
	        	  		file=checkFoldersForFile(children[i], file);
	                }
	                else Debug.instance().Log("This is not ment to be:" + children[i].getName(),Debug.MSG_ERROR);
	            }
	        }
	    	return file;
	    }
}