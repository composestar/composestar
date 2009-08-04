package Composestar.Ant.Taskdefs;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.types.FileSet;

/**
 * Select all but the latest X elements on a path
 * 
 * @author Michiel Hendriks
 */
public class PruneFiles extends Task
{
	protected int keep = 1;

	protected FileSet fileSet;

	public void setKeep(int inval)
	{
		keep = inval;
	}

	public FileSet createFileSet() throws BuildException
	{
		if (fileSet == null) fileSet = new FileSet();
		else throw new BuildException("fileSet element already specified.");
		return fileSet;
	}

	public void execute() throws BuildException
	{
		DirectoryScanner ds = fileSet.getDirectoryScanner(getProject());
		String[] files = ds.getIncludedFiles();
		CompareAge ca = new CompareAge();
		ca.basedir = ds.getBasedir();
		Arrays.sort(files, ca);
		for (int i = 0; i < files.length - keep; i++)
		{
			File pdir = new File(ds.getBasedir(), files[i]);
			log("Pruning file " + pdir.getAbsolutePath(), Project.MSG_INFO);
			Delete dt = new Delete();
			// impersonate this task
			dt.setProject(getProject());
			dt.setOwningTarget(getOwningTarget());
			dt.setTaskName(getTaskName());
			dt.setDescription(getDescription());
			dt.setLocation(getLocation());
			dt.setTaskType(getTaskType());

			dt.setFile(pdir);
			dt.execute();
		}
	}

	class CompareAge implements Comparator
	{
		public File basedir;

		public int compare(Object o1, Object o2)
		{
			File f1 = new File(basedir, (String) o1);
			File f2 = new File(basedir, (String) o2);
			long res = f1.lastModified() - f2.lastModified();
			if (res > 0) return 1;
			if (res < 0) return -1;
			return 0;
		}
	}
}
