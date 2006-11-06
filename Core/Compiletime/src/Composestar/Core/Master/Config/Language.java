package Composestar.Core.Master.Config;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Composestar.Core.DUMMER.DummyEmitter;
import Composestar.Core.Exception.ModuleException;

public class Language implements Serializable
{
	private String name;

	private CompilerSettings compilerSettings;

	private String dummyEmitter;

	private List extensions;

	public Language()
	{
		compilerSettings = new CompilerSettings();
		extensions = new ArrayList();
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public CompilerSettings getCompilerSettings()
	{
		return compilerSettings;
	}

	public DummyEmitter getEmitter() throws ModuleException
	{
		try
		{
			Class myclass = Class.forName(this.dummyEmitter);
			return (DummyEmitter) myclass.newInstance();
		}
		catch (Exception e)
		{
			throw new ModuleException("Error while instantiating Dummy Emitter: " + this.dummyEmitter, "DUMMER");
		}
	}

	public void setEmitter(String emitter)
	{
		this.dummyEmitter = emitter;
	}

	public void addExtension(String extension)
	{
		extensions.add(extension);
	}

	public List getExtensions()
	{
		return extensions;
	}
}
