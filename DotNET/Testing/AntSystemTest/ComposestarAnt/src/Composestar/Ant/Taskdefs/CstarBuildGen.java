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
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet.NameEntry;

/**
 * Generates the BuildConfiguration.xml for Compose* from a VS2003 project file.
 * 
 * @author Marcus
 */
public final class CstarBuildGen extends TransformTask
{
	private final static String BUILD_CONFIGURATION_XML = "BuildConfiguration.xml";

	String m_composestarBase;
	
	public CstarBuildGen()
	{
		super();
	}

	/**
	 * Sets the base directory of ComposeStar.
	 * Assumes the jar files are in "{composestarBase}/Binaries".
	 */
	public void setComposestarBase(String path)
	{
		m_composestarBase = path + "/";
	}

	/**
	 * Sets the directory that contains the AntHelper executable.
	 */
	public void setAntHelperPath(String path)
	{
		if (!Composestar.Ant.XsltUtils.setAntHelperPath(path))
		{
			throw new BuildException("Invalid antHelperPath value: " + path);
		}
	}

	/**
	 * Set the location of the xslt file that is used to transform
	 * the Visual Studio 2003 project files to Compose* buildfiles.
	 */
	public void setXslt(String xslt)
	{
		m_xslt = xslt;
	}

	/**
	 * Adds a fileset of project files to process.
	 */
	public void addFileset(FileSet fs)
	{
		m_filesets.add(fs);
	}

	/**
	 * Performs the transformations on the project files 
	 * to generate Compose* buildfiles.
	 */
	public void execute() throws BuildException
	{
		registerCstarAsms();
		
		Transformer t = createTransformer();
		List inputs = collectInputs();
		
		log("Generating " + inputs.size() + " Compose* buildfiles", Project.MSG_INFO);
		transform(t, inputs);
	}

	private File getOutputFile(File input)
	{
		File baseDir = input.getParentFile();
		return new File(baseDir, BUILD_CONFIGURATION_XML);
	}

	private void transform(Transformer t, List inputs)
	{
		t.setParameter("composestarpath", m_composestarBase);

		Iterator it = inputs.iterator();
		while (it.hasNext())
		{
			File input = (File) it.next();
			File output = getOutputFile(input);

			transform(t, input, output);
		}
	}

	private void transform(Transformer t, File input, File output)
	{
		String baseDir = input.getParent();
		log(baseDir, Project.MSG_INFO);

		try
		{
			Composestar.Ant.XsltUtils.setCurrentDirectory(baseDir);
			t.setParameter("basepath", baseDir + "/");

			Source xmlSource = new StreamSource(input);
			Result result = new StreamResult(output);

			t.transform(xmlSource, result);
		}
		catch (TransformerException e)
		{
			throw new BuildException(e.toString(), e);
		}
	}

	private void registerCstarAsms()
	{
		FileSet cstarAsms = new FileSet();
		cstarAsms.setDir(new File(m_composestarBase, "Binaries"));
		NameEntry inc = cstarAsms.createInclude();
		inc.setName("*.dll");

		DirectoryScanner ds = cstarAsms.getDirectoryScanner(this.getProject());
		String[] files = ds.getIncludedFiles();
		for (int i = 0; i < files.length; i++)
		{
			File asmPath = new File(ds.getBasedir().getPath(), files[i]);
			String asm = asmPath.getName();
			asm = asm.substring(0, asm.lastIndexOf("."));
			Composestar.Ant.XsltUtils.registerAssembly(asm, asmPath.toString());
		}
	}
}
