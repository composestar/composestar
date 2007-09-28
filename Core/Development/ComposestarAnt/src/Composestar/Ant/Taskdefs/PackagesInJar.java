/**
 * 
 */
package Composestar.Ant.Taskdefs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
 * Creates a list of packages in the selected jar files
 * 
 * @author Michiel Hendriks
 */
public class PackagesInJar extends Task
{
	protected List<FileSet> fileSets;

	protected String separator = ",";

	protected String property;

	public void addFileset(FileSet fs)
	{
		fileSets.add(fs);
	}

	public void setProperty(String name)
	{
		property = name;
	}

	public void setSeparator(String name)
	{
		separator = name;
	}

	public PackagesInJar()
	{
		super();
		fileSets = new ArrayList<FileSet>();
	}

	public void execute() throws BuildException
	{
		Set<String> packages = new HashSet<String>();
		for (FileSet fs : fileSets)
		{
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			File basedir = ds.getBasedir();

			String[] files = ds.getIncludedFiles();
			for (int i = 0; i < files.length; i++)
			{
				File input = new File(basedir, files[i]);
				packages.addAll(getPackagesFromJar(input));
			}
		}
		StringBuffer sb = new StringBuffer();
		List<String> sorted = new ArrayList<String>(packages);
		Collections.sort(sorted);
		for (String pkg : sorted)
		{
			if (sb.length() > 0)
			{
				sb.append(separator);
			}
			sb.append(pkg);
		}
		getProject().setProperty(property, sb.toString());
	}

	protected Set<String> getPackagesFromJar(File file)
	{
		Set<String> packages = new HashSet<String>();
		try
		{
			JarFile jarfile = new JarFile(file);
			Enumeration<JarEntry> entries = jarfile.entries();
			while (entries.hasMoreElements())
			{
				JarEntry entry = entries.nextElement();
				if (entry.getName().endsWith(".class"))
				{
					String pkg = entry.getName().substring(0, entry.getName().lastIndexOf("/"));
					packages.add(pkg.replace("/", "."));
				}
			}
		}
		catch (IOException e)
		{
			log("Error reading jar file: " + e.getMessage(), Project.MSG_ERR);
		}
		return packages;
	}
}
