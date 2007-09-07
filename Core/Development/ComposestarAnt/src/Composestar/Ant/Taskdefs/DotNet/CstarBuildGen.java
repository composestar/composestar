package Composestar.Ant.Taskdefs.DotNet;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.DynamicConfiguratorNS;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.PatternSet.NameEntry;

import Composestar.Ant.XsltUtils;
import Composestar.Ant.Taskdefs.TransformTask;

/**
 * Generates the BuildConfiguration.xml for Compose* from a VS2003 project file.
 * 
 * @author Marcus Klimstra
 * @author Michiel Hendriks
 */
public final class CstarBuildGen extends TransformTask
{
	private final static String BUILD_CONFIGURATION_XML = "BuildConfiguration.xml";

	private File m_composestarBase;
	private Map m_moduleSettings;
	
	public CstarBuildGen()
	{
		super();
		m_moduleSettings = new HashMap();
	}

	/**
	 * Sets the base directory of ComposeStar.
	 * Assumes the jar files are in "{composestarBase}/binaries".
	 */
	public void setComposestarBase(String path)
	{
		m_composestarBase = new File(path);
	}

	/**
	 * Sets the directory that contains the AntHelper executable.
	 */
	public void setAntHelperPath(String path)
	{
		if (!XsltUtils.setAntHelperPath(path))
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
		super.setXslt(xslt);
	}

	/**
	 * Adds a fileset of project files to process.
	 */
	public void addFileset(FileSet fs)
	{
		super.addFileset(fs);
	}

	/**
	 * Creates a Modules object, which contains module settings.
	 */
	public Modules createModules()
	{
		return new Modules();
	}

	/**
	 * Adds a module setting that will be passed to the stylesheet as a parameter.
	 */
	private void addModuleSetting(String moduleName, String setting, String value)
	{
		String key = moduleName + "_" + setting;
		m_moduleSettings.put(key, value);
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
		
		setParameters(t);
		
		log("Generating " + inputs.size() + " Compose* buildfiles", Project.MSG_INFO);
		transform(t, inputs);
	}
	
	private void setParameters(Transformer t)
	{
		// try to convince the processor to indent the generated xml
		t.setOutputProperty("{http://xml.apache.org/xalan}indent-amount", "2");
		
		// set composestarpath (forward slashes required)
		String basePath = formatPathForXslt(m_composestarBase);
		t.setParameter("composestarpath", basePath);

		// set module settings
		Iterator it = m_moduleSettings.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry entry = (Map.Entry)it.next();
			String key = (String)entry.getKey();
			String value = (String)entry.getValue();
			t.setParameter(key, value);
		}
	}
	
	private String formatPathForXslt(File file)
	{
		String path = file.getAbsolutePath();
		path = path.replace('\\', '/');
		if (! path.endsWith("/")) path += '/';
		return path;
	}
	
	protected File getOutputFile(File input)
	{
		File baseDir = input.getParentFile();
		return new File(baseDir, BUILD_CONFIGURATION_XML);
	}

	protected void beforeTransform(Transformer t, File input, File output)
	{
		String baseDir = input.getParent();
		XsltUtils.setCurrentDirectory(baseDir);
		t.setParameter("basepath", baseDir + "/");

		log(baseDir, Project.MSG_INFO);
	}

	private void registerCstarAsms()
	{
		FileSet cstarAsms = new FileSet();
		cstarAsms.setDir(new File(m_composestarBase, "lib"));
		NameEntry inc = cstarAsms.createInclude();
		inc.setName("*.dll");

		DirectoryScanner ds = cstarAsms.getDirectoryScanner(this.getProject());
		String[] files = ds.getIncludedFiles();
		for (int i = 0; i < files.length; i++)
		{
			File assembly = new File(ds.getBasedir().getPath(), files[i]);
			String name = getAssemblyName(assembly);
			XsltUtils.registerAssembly(name, assembly.getAbsolutePath());
		}
	}
	
	private String getAssemblyName(File assembly)
	{
		String asm = assembly.getName();
		return asm.substring(0, asm.lastIndexOf("."));		
	}
	
	class Modules implements DynamicConfiguratorNS
	{
		public Modules() {}
		
		public void setDynamicAttribute(String uri, String localName, String qName, String value) throws BuildException
		{
			throw new BuildException(getClass() + " doesn't support any attributes.");
		}

		public Object createDynamicElement(String uri, String localName, String qName) throws BuildException
		{
			return new ModuleSettings(localName);
		}
	}
	
	class ModuleSettings implements DynamicConfiguratorNS
	{
		private String m_moduleName;
		
		public ModuleSettings(String name)
		{
			m_moduleName = name;
		}
		
		public void setDynamicAttribute(String uri, String localName, String qName, String value) throws BuildException
		{
			addModuleSetting(m_moduleName, localName, value);
		}

		public Object createDynamicElement(String uri, String localName, String qName) throws BuildException
		{
			throw new BuildException(getClass() + " doesn't support any nested elements.");
		}
	}
}
