package Composestar.Ant.Taskdefs;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
* @author Marcus
*/
public abstract class TransformTask extends Task
{
	protected String m_xslt;
	protected List m_filesets;

	public TransformTask()
	{
		super();
		m_xslt = null;
		m_filesets = new ArrayList();
	}

	protected Transformer createTransformer()
	{
		try
		{
			TransformerFactory tf = TransformerFactory.newInstance();
			return tf.newTransformer(new StreamSource(m_xslt));
		}
		catch (TransformerException e)
		{
			throw new BuildException(e.toString(), e);
		}
	}

	protected List collectInputs()
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

}
