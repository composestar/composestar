/*
 * This file is part of WeaveC project [http://weavec.sf.net].
 * Copyright (C) 2005 University of Twente.
 *
 * Licensed under the BSD License.
 * [http://www.opensource.org/licenses/bsd-license.php]
 * 
 * Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions
 are met:
 * 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the University of Twente nor the names of its 
 contributors may be used to endorse or promote products derived from
 this software without specific prior written permission.

 * THIS SOFTWARE IS PROVIDED BY AUTHOR AND CONTRIBUTORS ``AS IS'' AND 
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODSOR SERVICES; 
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * 
 * $Id$
 */
package Composestar.C.wrapper;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import Composestar.C.LAMA.CDirectory;
import Composestar.C.LAMA.CFile;
import Composestar.C.MASTER.FileMap;
import Composestar.C.wrapper.parsing.AttributeWriter;
import Composestar.C.wrapper.parsing.WrappedAST;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.LAMA.LangNamespace;
import Composestar.Core.LAMA.UnitRegister;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;

/**
 * Created by IntelliJ IDEA. User: ByelasH Date: 20-dec-2004 Time: 11:17:45 To
 * change this template use File | Settings | File Templates.
 */
public class CWrapper implements CTCommonModule
{
	public GlobalIntroductionPoint introductionPoint = null;

	public Hashtable fileASTMap = new Hashtable();

	private Vector functions = null;

	private File filename = null;

	private Hashtable absolutePaths = new Hashtable();

	private File tempFolder = null;

	public String objectname = null;

	public HashMap structASTMap = new HashMap();

	private ArrayList cfiles = new ArrayList();

	private WrappedAST wrappedAST = null;

	private String namespace = null;

	private boolean firstNameSpace = false;

	private static HashMap usedTypes = null;

	private UnitRegister register;

	public void run(CommonResources resources) throws ModuleException
	{
		register = (UnitRegister) resources.get(UnitRegister.RESOURCE_KEY);
		if (register == null)
		{
			register = new UnitRegister();
			resources.put(UnitRegister.RESOURCE_KEY, register);
		}

		filename = resources.configuration().getProject().getBase();
		tempFolder = filename;
		/***********************************************************************
		 * here a new file(typ): Semantics of the annotations is instantiaded
		 * just as the xml file where the annotion information will be saved
		 **********************************************************************/
		usedTypes = new HashMap();
		// CFile semantics = new CFile();
		DataStore dataStore = resources.repository();
		// PrimitiveConcern pcFile = new PrimitiveConcern();
		// semantics.setName("Semantics");
		// semantics.setFullName("Composestar.Semantics");
		// semantics.setAnnotation(true);
		// pcFile.setName( semantics.name() );
		// pcFile.setPlatformRepresentation(semantics);
		// semantics.setParentConcern(pcFile);

		// dataStore.addObject( semantics.name(), pcFile );
		// FileMap fm = FileMap.instance();

		if (filename.equals("ERROR"))
		{
			throw new ModuleException("Error in configuration file: No such property TempFolder");
		}
		/**
		 * @Todo: now the subdirs are searched for .c files and not the sources
		 *        of build.ini is used*
		 */

		// createDirectoryStructure(new File(filename),true,new
		// InputFileNameFilter(), resources);
		cfiles = getAllFilesFromDirectory(filename, true, new InputFileNameFilter(), null);

		for (Object cfile : cfiles)
		{
			fileASTMap.put(cfile, new Object());
			// this.fileASTMap.put(((String)cfiles.get(i)).substring(((String)cfiles.get(i)).lastIndexOf("\\"),((String)cfiles.get(i)).indexOf(".ccc")));
		}

		Iterator it = fileASTMap.keySet().iterator();
		for (Object o : fileASTMap.keySet())
		{
			String fn = (String) o;
			String cfName = fn.substring(fn.lastIndexOf("\\") + 1, fn.indexOf(".ccc"));
			CFile cf = (CFile) absolutePaths.get(cfName);
			// filename
			// =((File)absolutePaths.get(filename)).getAbsolutePath();//(String)cfiles.get(i);
			// //

			// filename = (String)it.next();//(String)cfiles.get(i); //
			// System.out.println("Handling file"+ filename);

			try
			{
				/***************************************************************
				 * ****************************8 object name is the filename
				 * without the extension and without the tempfolder-path,
				 * subdirs from the tempfolder path are changed into
				 * subdir.object*
				 **************************************************************/
				retrieveAST wrapper = new retrieveAST();

				setObjectName(fn, resources);
				setNameSpace(fn, resources);

				wrapper.createWrappedAST(fn, objectname, namespace, usedTypes, cf, this, register);
				fileASTMap.put(fn, wrapper);
				FileMap.instance().addFileAST(fn, wrapper);// createWrappedAST(filename,
				// objectname,
				// namespace,
				// out,
				// usedTypes,
				// this));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		/** Write annotations to attributes.xml* */
		AttributeWriter.instance().saveToXML(new File(tempFolder, "attributes.xml"));

		// Iterator i = usedTypes.values().iterator();
		// while(i.hasNext()){
		// System.out.println("HashMap usedTypes entry:" +(String)i.next());
		// }
	}

	private ArrayList getAllFilesFromDirectory(File dir, boolean recurse, FilenameFilter fnf, CDirectory cdirectory)
	{
		ArrayList list = new ArrayList();
		CDirectory cdir = null;
		if (dir.isDirectory())
		{
			if (dir.getName().startsWith(".")) // skip directories with leading
			// dot's
			{
				return list;
			}
			if (dir.getName().equals("CVS")) // skip CVS directory
			{
				return list;
			}
			String[] children = dir.list(fnf);
			if (children.length > 0)
			{
				cdir = new CDirectory();
				register.registerLanguageUnit(cdir);
				cdir.setDirName(dir.getName());
				// System.out.println("Added directory"+dir.getName());
				if (cdirectory != null)
				{
					cdirectory.addSubDir(cdir);
				}
			}
			for (String aChildren : children)
			{
				File f = new File(dir, aChildren);
				if (!recurse && f.isFile())
				{
					list.add(f.getAbsolutePath());
				}
				else if (recurse)
				{
					list.addAll(getAllFilesFromDirectory(f, recurse, fnf, cdir));
				}
			}
		}
		else
		{
			CFile cf = new CFile();
			register.registerLanguageUnit(cf);
			cf.setName(dir.getName().substring(0, dir.getName().indexOf(".ccc")));
			if (cdirectory != null)
			{
				cf.setDirectory(cdirectory);
				cdirectory.addFile(cf);
				// System.out.println("CFile "+cf.getFullName()+ " instantiated
				// and added to " + cdirectory.getDirName());
			}
			// System.out.println("CFile "+cf.getFullName()+ " added to
			// absolutepaths" );
			if (absolutePaths.put(cf.getFullName(), cf) != null)
			{
				System.out.println("Already contained object" + cf.getFullName());
			}
			list.add(dir.getAbsolutePath());
		}
		return list;
	}

	/***************************************************************************
	 * TODO:
	 * 
	 * @johan this create namespace function makes a namespace from a directory
	 *        this does not work well cause is only looks for the path and not
	 *        for the parent namespaces
	 **************************************************************************/

	private void createDirectoryStructure(File dir, boolean recurse, FilenameFilter fnf, CommonResources resources)
	{
		{
			if (dir.isDirectory())
			{
				namespace = dir.getName();

				firstNameSpace = true;
				String[] children = dir.list(fnf);
				for (String aChildren : children)
				{
					File f = new File(dir, aChildren);
					if (recurse)
					{
						createDirectoryStructure(f, recurse, fnf, resources);
					}
				}
			}
			else
			// is in directory:namespace and this directory contains a .c file
			{
				if (firstNameSpace)
				{
					LangNamespace ns = new LangNamespace(namespace);
					// dict.addLanguageUnit(ns);
					firstNameSpace = false;
				}
			}
		}

	}

	public void setFunctions(Vector functions)
	{
		this.functions = functions;
	}

	public Vector getFunctions()
	{
		return functions;
	}

	public void setFilename(File filename)
	{
		this.filename = filename;
	}

	public void setObjectname(String objectname)
	{
		this.objectname = objectname;
	}

	public void setNameSpace(String namespace)
	{
		this.namespace = namespace;
	}

	public static void setUsedType(HashMap aUsedType)
	{
		usedTypes = aUsedType;
	}

	public void setObjectName(String filename, CommonResources resources)
	{
		objectname = resources.configuration().getProject().getBase().toString();
		objectname = objectname.replace('/', '\\');
		objectname = filename.substring(objectname.length() + 1); // because
		// there
		// is no
		// trailing
		// slash
		objectname = objectname.substring(0, objectname.lastIndexOf(".ccc"));
		objectname = objectname.replace('\\', '.');
	}

	public void setNameSpace(String filename, CommonResources resources)
	{
		namespace = resources.configuration().getProject().getBase().toString();
		namespace = namespace.replace('/', '\\');
		namespace = namespace.substring(0, namespace.lastIndexOf("\\"));
		namespace = namespace.substring(0, namespace.lastIndexOf("\\"));
		namespace = filename.substring(namespace.length() + 1); // because there
		// is no
		// trailing
		// slash
		namespace = namespace.substring(0, namespace.lastIndexOf("\\"));
		namespace = namespace.substring(namespace.indexOf("\\") + 1, namespace.length());
		namespace = namespace.replace('\\', '.');
	}

	public File getFilename()
	{
		return filename;
	}

	public void pWriter(PrintWriter pw)
	{}

	public void setWrappedAST(WrappedAST wrappedAST)
	{
		this.wrappedAST = wrappedAST;
	}

	public WrappedAST getWrappedAST()
	{
		return wrappedAST;
	}

}
