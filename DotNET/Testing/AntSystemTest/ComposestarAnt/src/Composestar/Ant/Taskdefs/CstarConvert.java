package Composestar.Ant.Taskdefs;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

/**
 * Transforms x to y.
 * 
 * @author Marcus
 */
public final class CstarConvert extends TransformTask
{
	public CstarConvert()
	{
		super();
	}
	
	public void setXslt(String xslt)
	{
		super.setXslt(xslt);
	}
	
	public void addFileset(FileSet fs)
	{
		super.addFileset(fs);
	}

	public void execute() throws BuildException
	{
		Transformer t = createTransformer();
		List inputs = collectInputs();

		t.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");

		log("Generating " + inputs.size() + " VS2005 project files", Project.MSG_INFO);
		transform(t, inputs);
	}

	private File getOutputFile(File input)
	{
		File baseDir = input.getParentFile();
		String filename = input.getName();
		String namePart = filename.substring(0, filename.lastIndexOf('.'));
		String newName = namePart + ".cpsproj";
		
		return new File(baseDir, newName);
	}

	private void transform(Transformer t, List inputs)
	{
		Iterator it = inputs.iterator();
		while (it.hasNext())
		{
			File input = (File)it.next();
			File output = getOutputFile(input);

			transform(t, input, output);
		}
	}

	private void transform(Transformer t, File input, File output)
	{
		log(input.getParent(), Project.MSG_INFO);

		try
		{
			Source xmlSource = new StreamSource(input);
			Result result = new StreamResult(output);

			t.transform(xmlSource, result);
		}
		catch (TransformerException e)
		{
			throw new BuildException(e.toString(), e);
		}
	}	
}
