package Composestar.Core.Master.Config;

import java.io.Serializable;

public class Source implements Serializable
{
	private static final long serialVersionUID = -5054531327936357776L;

	private boolean isEmbedded;

	private boolean isExecutable;

	private String filename;

	private String dummy;

	// private String compiledSource;
	private String target;

	private Project project;

	public Source()
	{}

	public String getFileName()
	{
		return filename;
	}

	public void setFileName(String inFilename)
	{
		filename = inFilename;
	}

	public boolean isEmbedded()
	{
		return isEmbedded;
	}

	public void setIsEmbedded(boolean isemb)
	{
		isEmbedded = isemb;
	}

	public boolean isExecutable()
	{
		return isExecutable;
	}

	public void setIsExecutable(boolean isexe)
	{
		isExecutable = isexe;
	}

	/*
	 * public void setCompiledSource(String fileName) { this.compiledSource =
	 * fileName; } public String getCompiledSource() { return compiledSource; }
	 */
	public void setDummy(String filename)
	{
		dummy = filename;
	}

	public String getDummy()
	{
		return dummy;
	}

	public String getTarget()
	{
		return target;
	}

	public void setTarget(String inTarget)
	{
		target = inTarget;
	}

	public void setProject(Project p)
	{
		project = p;
	}

	public Project getProject()
	{
		return project;
	}
}
