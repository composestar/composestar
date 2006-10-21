package Composestar.Ant.Taskdefs;

import java.io.File;
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

/**
* @author Marcus Klimstra
*/
public abstract class TransformTask extends BaseTask
{
	private File m_xslt;

	protected TransformTask()
	{
		super();
		m_xslt = null;
	}

	protected void setXslt(String xslt)
	{
		if (m_xslt != null)
			throw new BuildException("xslt attribute already set");

		m_xslt = new File(xslt);		

		if (! m_xslt.exists())
			throw new BuildException("xslt stylesheet '" + xslt + "' does not exist");
	}
	
	protected File getXslt()
	{
		return m_xslt;
	}

	protected final Transformer createTransformer()
	{
		if (m_xslt == null)
			throw new BuildException("xslt attribute is required");
		
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

	protected final void transform(Transformer t, List inputs)
	{
		Iterator it = inputs.iterator();
		while (it.hasNext())
		{
			File input = (File)it.next();
			File output = getOutputFile(input);

			if (needToTransform(input, output))
				transform(t, input, output);
		}
	}

	private void transform(Transformer t, File input, File output)
	{
		try
		{
			beforeTransform(t, input, output);
			
			Source xmlSource = new StreamSource(input);
			Result result = new StreamResult(output);

			t.transform(xmlSource, result);
		}
		catch (TransformerException e)
		{
			throw new BuildException(e.toString(), e);
		}
	}
	
	protected boolean needToTransform(File input, File output)
	{
		// always do the transformation by default
		return true;
	}

	protected void beforeTransform(Transformer t, File input, File output)
	{
		// do nothing by default
	}

	protected abstract File getOutputFile(File input);
}
