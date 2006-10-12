package Composestar.Ant.Taskdefs;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
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
		log("Generating Compose* buildfiles", Project.MSG_INFO);
		
		Transformer t = createTransformer();
		List inputs = collectInputs();
		
		transform(t, inputs);
	}
	
	private Transformer createTransformer()
	{
		try
		{
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer(new StreamSource(m_xslt));
			t.setParameter("composestarpath", m_composestarBase);
			return t;
		}
		catch (TransformerException e)
		{
			throw new BuildException(e.toString(), e);
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
	
	private void transform(Transformer t, List inputs)
	{
		Iterator it = inputs.iterator();		
		while (it.hasNext())
		{
			File input = (File)it.next();
			File output = new File(input.getParentFile(), BUILD_CONFIGURATION_XML);
			
			transform(t, input, output);
		}
	}
	
	private void transform(Transformer t, File in, File out)
	{
		Composestar.Ant.XsltUtils.setCurrentDirectory(in.getParent());
		t.setParameter("basepath", in.getParent() + "/");
				
		try
		{
			Source xmlSource = new StreamSource(in);
			Result result = new StreamResult(out);
			
			t.transform(xmlSource, result);
		}
		catch (TransformerException e)
		{
			throw new BuildException(e.toString(), e);
		}
	}
}
