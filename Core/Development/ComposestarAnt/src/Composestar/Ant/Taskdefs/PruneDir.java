package Composestar.Ant.Taskdefs;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Delete;
import org.apache.tools.ant.types.DirSet;

/**
 * Select all but the latest X elements on a path
 * 
 * @author Michiel Hendriks
 */
public class PruneDir extends Task
{
	protected int keep = 1;

	protected DirSet dirSet;

	public void setKeep(int inval)
	{
		keep = inval;
	}

	public DirSet createDirSet() throws BuildException
	{
		if (dirSet == null) dirSet = new DirSet();
		else throw new BuildException("dirSet element already specified.");
		return dirSet;
	}

	public void execute() throws BuildException
	{
		DirectoryScanner ds = dirSet.getDirectoryScanner(getProject());
		String[] dirs = ds.getIncludedDirectories();
		CompareAge ca = new CompareAge();
		ca.basedir = ds.getBasedir();
		Arrays.sort(dirs, ca);
		for (int i = 0; i < dirs.length - keep; i++)
		{
			File pdir = new File(ds.getBasedir(), dirs[i]);
			log("Pruning dir " + pdir.getAbsolutePath(), Project.MSG_INFO);
			Delete dt = new Delete();
			// impersonate this task
			dt.setProject(getProject());
			dt.setOwningTarget(getOwningTarget());
			dt.setTaskName(getTaskName());
			dt.setDescription(getDescription());
			dt.setLocation(getLocation());
			dt.setTaskType(getTaskType());

			dt.setDir(pdir);
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
			return (int) (f1.lastModified() - f2.lastModified());
		}
	}
}
