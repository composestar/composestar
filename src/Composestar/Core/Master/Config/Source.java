package Composestar.Core.Master.Config;

import java.io.Serializable;

public class Source implements Serializable
{
	private static final long serialVersionUID = -5054531327936357776L;

	private boolean isExecutable = false;

	private String fileName;

	private String dummy;

	// private String compiledSource;
	private String target;

	private Project project;

	public Source()
	{}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String filename)
	{
		this.fileName = filename;
	}

	public boolean isExecutable()
	{
		return this.isExecutable;
	}

	public void setIsExecutable(boolean isexe)
	{
		this.isExecutable = isexe;
	}

	/*
	 * public void setCompiledSource(String fileName) { this.compiledSource =
	 * fileName; } public String getCompiledSource() { return compiledSource; }
	 */
	public void setDummy(String filename)
	{
		this.dummy = filename;
	}

	public String getDummy()
	{
		return dummy;
	}

	public String getTarget()
	{
		return target;
	}

	public void setTarget(String target)
	{
		this.target = target;
	}

	public void setProject(Project p)
	{
		this.project = p;
	}

	public Project getProject()
	{
		return project;
	}
}
