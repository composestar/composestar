package Composestar.Ant.Taskdefs;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.tools.ant.BuildException;

/**
* @author Marcus
*/
public abstract class TransformTask extends BaseTask
{
	private String m_xslt;

	protected TransformTask()
	{
		super();
		m_xslt = null;
	}

	public void setXslt(String xslt)
	{
		m_xslt = xslt;
	}

	protected Transformer createTransformer()
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
}
