package Composestar.Ant.Taskdefs.DotNet;

import java.io.File;
import java.util.List;

import javax.xml.transform.Transformer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;

import Composestar.Ant.Taskdefs.TransformTask;

/**
 * Transform VS2003 project files to VS2005 ones.
 * 
 * @author Marcus Klimstra
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

		log("Generating up to " + inputs.size() + " VS2005 project files", Project.MSG_INFO);
		transform(t, inputs);
	}

	protected File getOutputFile(File input)
	{
		File baseDir = input.getParentFile();
		String filename = input.getName();
		String namePart = filename.substring(0, filename.lastIndexOf('.'));
		String newName = namePart + ".cpsproj";
		
		return new File(baseDir, newName);
	}

	protected boolean needToTransform(File input, File output)
	{
		if (! output.exists())
			return true;
		
		if (input.lastModified() > output.lastModified())
			return true;
		
		if (getXslt().lastModified() > output.lastModified())
			return true;
		
		return false;
	}

	protected void beforeTransform(Transformer t, File input, File output)
	{
		log(input.getParent(), Project.MSG_INFO);
	}
}
