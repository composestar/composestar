package Composestar.Ant.Taskdefs;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

/**
* @author Marcus Klimstra
*/
public abstract class BaseTask extends Task
{
	private final List m_fileSets;
	
	/**
	 * List of failed tests. Inceased with final exception.
	 */
	protected List failList = new ArrayList();	
	
	protected BaseTask()
	{
		super();
		m_fileSets = new ArrayList();
	}

	public void addFileset(FileSet fs)
	{
		m_fileSets.add(fs);
	}

	protected List collectInputs()
	{
		List result = new ArrayList();
		Iterator it = m_fileSets.iterator();
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
	
	protected void addFailure(String subject, String reason)
	{
		log("Failed: " + reason, Project.MSG_ERR);
		failList.add(subject+" : "+reason);
	}
	
	protected void reportFailures()
	{
		Iterator it = failList.iterator();
		while (it.hasNext())
		{
			String failed = (String)it.next();
			log(failed, Project.MSG_ERR);
		}
	}
}
