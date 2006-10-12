package Composestar.Ant.Taskdefs;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.XSLTProcess;
import org.apache.tools.ant.taskdefs.XSLTProcess.Param;
import org.apache.tools.ant.types.FileSet;

/**
 * Generates the BuildConfiguration.xml for Compose* from a VS2003 project file.
 * 
 * @author Marcus
 */
public class CstarGen extends Task
{
	private final static String BUILD_CONFIGURATION_XML = "BuildConfiguration.xml";
	
	private String m_composestarBase;
	private String m_xslt;
	private List m_filesets;
	
	public CstarGen()
	{
		m_xslt = null;
		m_filesets = new ArrayList();
	}
	
	public void setComposestarBase(String path)
	{
		m_composestarBase = path + "/";
	}
	
	public void setAntHelperPath(String path)
	{
		if (!Composestar.Ant.XsltUtils.setAntHelperPath(path))
		{
			throw new BuildException("Invalid antHelperPath value: " + path);
		}
	}

	public void setXslt(String xslt)
	{
		m_xslt = xslt;
	}
	
	public void addFileset(FileSet fs)
	{
		m_filesets.add(fs);
	}

	public void execute() throws BuildException
	{
		getProject().log("Generating Compose* buildfiles", Project.MSG_INFO);
		
		List inputs = collectInputs();		
		Iterator it = inputs.iterator();		
		while (it.hasNext())
		{
			File input = (File)it.next();
			File output = new File(input.getParentFile(), BUILD_CONFIGURATION_XML);
			
			transform(input, output);
		}
	}
	
	private List collectInputs()
	{
		List result = new ArrayList();
		
		Iterator it = m_filesets.iterator();
		while (it.hasNext())
		{
			FileSet fs = (FileSet)it.next();
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			File basedir = ds.getBasedir();
			
			String[] files = ds.getIncludedFiles();
			for (int i = 0; i < files.length; i++)
			{
				File input = new File(basedir, files[i]);
				result.add(input);
			}
		}
		return result;
	}
	
	private void transform(File in, File out)
	{
		Composestar.Ant.XsltUtils.setCurrentDirectory(in.getParent());
		
		XSLTProcess xslt = (XSLTProcess)getProject().createTask("xslt");
		xslt.init();
		xslt.setIn(in);
		xslt.setOut(out);
		xslt.setStyle(m_xslt);
		
		Param param = xslt.createParam();
		param.setName("basepath");
		param.setExpression(in.getParent() + File.separator);
		
		param = xslt.createParam();
		param.setName("composestarpath");
		param.setExpression(m_composestarBase);
	/*
		param = xslt.createParam();
		param.setName("INCREconfig");
		param.setExpression("INCREconfig2.xml");
	*/
		xslt.execute();
	}
}
