/*
 * Created on 28-jul-2006
 *
 *The program make is not really easy to use,
 *It is very dependent to path-names
 *For instance \Program Files\ -> \"Program Files"\, and parhseparator is important 
 *GCC requires other things such as pathseparator is always "/" 
 * */
package ComposestarEclipsePlugin.C.Makefile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogSettings;

import ComposestarEclipsePlugin.C.IComposestarCConstants;
import ComposestarEclipsePlugin.Core.ComposestarEclipsePluginPlugin;
import ComposestarEclipsePlugin.Core.Debug;

/**
 * @author johan
 */
public class MakefileCreator
{
	private HashSet sources = null;

	private IProjectDescription projectDescription = null;

	private String projectPath = "";

	private String customFilters = "";

	private String outputPath = "";

	private String basePath = "";

	private IDialogSettings settings;

	public MakefileCreator(String projectPath, IProjectDescription projectDescription, HashSet sources,
			HashSet concerns, String customFilters)
	{
		this.projectPath = projectPath;
		this.sources = sources;
		this.projectDescription = projectDescription;
		this.customFilters = customFilters;

		settings = ComposestarEclipsePluginPlugin.getDefault().getDialogSettings(projectPath);

		outputPath = (new File(settings.get("outputPath"))).toString()+File.separator;
		basePath = (new File(projectPath)).toString()+File.separator;
	}

	private String define()
	{
		return " -D__attribute__(dllimport)=\"\" -D\"__builtin_va_list=(void)*\"";
	}

	private void copyMessageFiles(BufferedWriter bw)
	{
		try
		{
			bw.write("copyMessageFromPlugin:\n");
			File path = new File(ComposestarEclipsePluginPlugin.getAbsolutePath("/", IComposestarCConstants.BUNDLE_ID));
			bw.write("\t$(fileCopy) \"" + path + File.separator + "message.*\" " + basePath + "\n");
			bw.write("\n");
		}
		catch (IOException io)
		{
			Debug.instance().Log("Error while creating MakeFile: ", Debug.MSG_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void preprocess(BufferedWriter bw)
	{
		try
		{
			bw.write("preprocess:\n");
			IPath element = null;
			Iterator i = sources.iterator();
			if (i.hasNext() == false)
			{
				return;
			}
			while (i.hasNext())
			{
				element = ((IPath) i.next());
				// FIXME: element.lastSegment() will miss subdirectories
				bw.write("\tgcc -dD -E" + define() + " " + element.lastSegment() + " -o " + element.lastSegment()
						+ "cc" + "\n"); // ((IPath)i.next()).toOSString()
				// ((IPath)i).lastSegment())
				// ||outputPath+
				// java.io.File.separatorChar +
			}
			bw.write("\tdel message.ccc");
		}
		catch (IOException io)
		{
			Debug.instance().Log("Error while writing preprocess information", Debug.MSG_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private void weave(BufferedWriter bw)
	{
		try
		{
			bw.write("weave:\n");
			String jvmOptions = "";
			String mainClass = "Composestar.C.MASTER.CMaster";
			customFilters = customFilters.replaceAll("\n", ";");

			String command = "java.exe " + jvmOptions + " -cp \"$(CLASSPATH)$(SEP)" + customFilters + "\" " + mainClass
					+ " " + "\"" + projectPath + "BuildConfiguration.xml" + "\"";

			Debug.instance().Log(command);
			bw.write("\t" + command + "\n");
		}
		catch (IOException io)
		{
			Debug.instance().Log("Error while creating MakeFile: ", Debug.MSG_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void copyFiles(BufferedWriter bw)
	{
		try
		{
			bw.write("copymessage:\n");
			bw.write("\t$(fileCopy) message.c " + outputPath + "\n");
			bw.write("\n");
			bw.write("copyhfiles:\n");
			bw.write("\t$(fileCopy) *.h " + outputPath + "\n");
			bw.write("\n");
			bw.write("copycfiles:\n");
			bw.write("\t$(fileCopy) *.c " + outputPath + "\n");
		}
		catch (IOException io)
		{
			Debug.instance().Log("Error while creating MakeFile: ", Debug.MSG_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void changeExtensions(BufferedWriter bw)
	{
		try
		{
			bw.write("changeextensions:\n");
			bw.write("\t$(fileCopy) " + outputPath + "*.cccout " + outputPath + "*.c" + "\n");
		}
		catch (IOException io)
		{
			Debug.instance().Log("Error while creating MakeFile: ", Debug.MSG_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void compile(BufferedWriter bw)
	{
		try
		{
			/*******************************************************************
			 * Copy message.c en message.h naar output dir Doe dit ook met de
			 * concerns Roep dan gcc met preprocessed files en deze files aan
			 */

			Iterator i = sources.iterator();
			if (i.hasNext() == false)
			{
				return;
			}
			bw.write("compile:\n");
			String command = "";
			while (i.hasNext())
			{
				String file = ((IPath) i.next()).lastSegment();
				if (!file.equals("message.c"))
				{
					command += " " + outputPath.replace(java.io.File.separatorChar, '/') + file;
				}
			}
			bw.write("\t gcc -o" + outputPath.replace(java.io.File.separatorChar, '/')
					+ projectDescription.getName() + ".exe" + " " + command + " "
					+ outputPath.replace(java.io.File.separatorChar, '/') + "message.c \n");
		}
		catch (IOException io)
		{
			Debug.instance().Log("Error while creating MakeFile: ", Debug.MSG_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private void run(BufferedWriter bw)
	{
		try
		{
			bw.write("run: \n");
			bw.write("\t@echo ___RUN OUTPUT BEGIN___\n");
			bw.write("\t@" + outputPath.replace(java.io.File.separatorChar, '/') + projectDescription.getName()
					+ "\n");
			bw.write("\t@echo ___RUN OUTPUT END___\n");
		}
		catch (IOException io)
		{
			Debug.instance().Log("Error while creating MakeFile: ", Debug.MSG_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void clean(BufferedWriter bw)
	{
		try
		{
			bw.write("clean:\n");
			bw.write("\t$(DEL) " + basePath + "*.ccc\n");
			bw.write("\t$(DEL) " + basePath + "message.*\n");
			bw.write("\t$(DEL) " + outputPath + "*.c\n");
			bw.write("\t$(DEL) " + outputPath + "*.cccout\n");
			bw.write("\t$(DEL) " + outputPath + "*.h\n");
		}
		catch (IOException io)
		{
			Debug.instance().Log("Error while creating MakeFile: ", Debug.MSG_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void systemTestClean(BufferedWriter bw)
	{
		try
		{
			bw.write("systemTestClean:\n");
			bw.write("\t$(DEL) " + basePath + "*.ccc\n");
			bw.write("\t$(DEL) " + basePath + "message.*\n");
			bw.write("\t$(DEL) " + outputPath + "*.c\n");
			bw.write("\t$(DEL) " + outputPath + "*.cccout\n");
			bw.write("\t$(DEL) " + outputPath + "*.h\n");
			bw.write("\t$(DEL) " + basePath + "analysis" + java.io.File.separatorChar + "*.*\n");
			bw.write("\t$(DEL) " + basePath + "INCRE.*\n");
			bw.write("\t$(RMDIR) " + basePath + "analysis" + java.io.File.separatorChar + "\n");
			bw.write("\t$(DEL) " + basePath + "message.*\n");
			bw.write("\t$(DEL) " + outputPath + "*.exe\n");
			bw.write("\t$(DEL) " + basePath + "makefile.*\n");
		}
		catch (IOException io)
		{
			Debug.instance().Log("Error while creating MakeFile: ", Debug.MSG_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void build(BufferedWriter bw)
	{
		try
		{
			bw
					.write("build: copyMessageFromPlugin preprocess weave copymessage copyhfiles changeextensions compile clean run\n");
		}
		catch (IOException io)
		{
			Debug.instance().Log("Error while creating MakeFile: ", Debug.MSG_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private void buildWithoutRun(BufferedWriter bw)
	{
		try
		{
			bw
					.write("buildWithoutRun: copyMessageFromPlugin preprocess weave copymessage copyhfiles changeextensions compile clean \n");
		}
		catch (IOException io)
		{
			Debug.instance().Log("Error while creating MakeFile: ", Debug.MSG_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private void buildWithoutConcerns(BufferedWriter bw)
	{
		try
		{
			bw
					.write("buildWithOutConcerns: copyMessageFromPlugin copymessage copycfiles copyhfiles compile clean run\n");
		}
		catch (IOException io)
		{
			Debug.instance().Log("Error while creating MakeFile: ", Debug.MSG_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	private void buildKeepIntermediateFiles(BufferedWriter bw)
	{
		try
		{
			bw
					.write("buildKeepIntermediateFiles: copyMessageFromPlugin preprocess weave copymessage copyhfiles changeextensions compile run\n");
		}
		catch (IOException io)
		{
			Debug.instance().Log("Error while creating MakeFile: ", Debug.MSG_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void saveToFile()
	{
		try
		{
			String makefile = basePath + "makefile";
			Debug.instance().Log("Creating MakeFile: " + makefile, Debug.MSG_INFORMATION);
			BufferedWriter bw = new BufferedWriter(new FileWriter(makefile));

			bw.write(" ################################################################################ \n"
					+ " # Automatically-generated file. Do not edit!\n"
					+ " ################################################################################\n");
			bw.write("\n");
			osSettings(bw);
			bw.write("\n");
			settings(bw);
			bw.write("\n");
			copyMessageFiles(bw);
			bw.write("\n");
			preprocess(bw);
			bw.write("\n");
			weave(bw);
			bw.write("\n");
			copyFiles(bw);
			bw.write("\n");
			changeExtensions(bw);
			bw.write("\n");
			compile(bw);
			bw.write("\n");
			run(bw);
			bw.write("\n");
			build(bw);
			bw.write("\n");
			buildWithoutConcerns(bw);
			bw.write("\n");
			buildWithoutRun(bw);
			bw.write("\n");
			buildKeepIntermediateFiles(bw);
			bw.write("\n");
			clean(bw);
			bw.write("\n");
			bw.close();
			Debug.instance().Log("MakeFile Created Succesfully", Debug.MSG_INFORMATION);
		}
		catch (IOException io)
		{
			Debug.instance().Log("Error while creating MakeFile: ", Debug.MSG_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void settings(BufferedWriter bw)
	{
		try
		{
			Debug.instance().Log(
					org.eclipse.core.runtime.Platform.getInstallLocation().getURL().getPath().toString() + "plugins"
							+ java.io.File.separatorChar + "ComposestarEclipsePlugin");
			String cp = settings.get("classPath");
			cp = cp.replaceAll("%composestar%", ComposestarEclipsePluginPlugin.getAbsolutePath("/"));
			bw.write("CLASSPATH=" + cp + "\n");
		}
		catch (IOException io)
		{
			Debug.instance().Log("Error while writing osSettings: ", Debug.MSG_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void osSettings(BufferedWriter bw)
	{
		try
		{
			bw.write("ifeq ($(OS), Darwin)\n");
			bw.write("\tSEP=:\n");
			bw.write("\tMAKE=make\n");
			bw.write("\tRECURSE_MAKE_RULE=if [" + '"' + "$(SUBDIRS)" + '"'
					+ "!=\"\" ]; then for a in $(SUBDIRS); do cd $$a && $(MAKE) && cd ..; done fi\n");
			bw
					.write("\tRECURSE_CLEAN_RULE=if [ \"$(SUBDIRS)\" != \"\" ]; then for a in $(SUBDIRS); do cd $$a && $(MAKE) clean && cd ..; done fi\n");
			bw.write("\tDEL=rm -f\n");
			bw.write("\tMKDIR=mkdir\n");
			bw.write("\tCOPY=cp -r\n");
			bw.write("\tfilecopy=cp\n");
			bw.write("\tRMDIR=rm -rf\n");
			bw.write("\tNULL=\n");
			bw.write("\tPATHSEP=/\n");
			bw.write("else \n");
			bw.write("ifeq ($(OS), linux)\n");
			bw.write("\tSEP=:\n");
			bw.write("\tMAKE=make\n");
			bw
					.write("\tRECURSE_MAKE_RULE=if [ \"$(SUBDIRS)\" != \"\" ]; then for a in $(SUBDIRS); do cd $$a && $(MAKE) && cd ..; done fi\n");
			bw
					.write("\tRECURSE_CLEAN_RULE=if [ \"$(SUBDIRS)\" != \"\" ]; then for a in $(SUBDIRS); do cd $$a && $(MAKE) clean && cd ..; done fi\n");
			bw.write("\tDEL=rm -f\n");
			bw.write("\tMKDIR=mkdir\n");
			bw.write("\tCOPY=cp -r\n");
			bw.write("\tfilecopy=cp\n");
			bw.write("\tRMDIR=rm -rf\n");
			bw.write("\tNULL=\n");
			bw.write("\tPATHSEP=/\n");
			bw.write("else\n");
			bw.write("\tSEP=;\n");
			bw.write("\tMAKE=make.exe\n");
			bw
					.write("\tRECURSE_MAKE_RULE=if not \"$(SUBDIRS)\" == \"\" for %%a in ( $(SUBDIRS) ) do cd %%a && $(MAKE) && cd..; done fi\n");
			bw
					.write("\tRECURSE_CLEAN_RULE=if not \"$(SUBDIRS)\" == \"\" for %%a in ( $(SUBDIRS) ) do cd %%a && $(MAKE) clean && cd..; done fi\n");
			bw.write("\tDEL=del\n");
			bw.write("\tMKDIR=md\n");
			bw.write("\tCOPY=xcopy /s /y\n");
			bw.write("\tfileCopy=copy /Y\n");
			bw.write("\tRMDIR=rmdir /s /q\n");
			bw.write("\tNULL=2>NUL\n");
			bw.write("\tPATHSEP=\\\n");
			bw.write("endif\n");
			bw.write("endif\n");
			bw.write("endif\n");
		}
		catch (IOException io)
		{
			Debug.instance().Log("Error while writing osSettings: ", Debug.MSG_ERROR);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void setOutputPath(String outputPath)
	{
		this.outputPath = outputPath;
	}

}
