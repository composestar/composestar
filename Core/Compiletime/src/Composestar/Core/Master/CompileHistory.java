/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Core.Master;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Contains the intermediate results of a compilation of a Compose* program.
 * This object is used for incremental compilation but also for visualization.
 * 
 * @author Michiel Hendriks
 */
public class CompileHistory implements Serializable
{
	private static final CPSLogger logger = CPSLogger.getCPSLogger("CompileHistory");

	// cchz = Composestar Compile History gZip
	public static final String DEFAULT_FILENAME = "ComposestarHistory.cchz";

	/**
	 * Date this history was saved on
	 */
	protected Date savedOn;

	/**
	 * configuration used during compilation
	 */
	protected Configuration configuration;

	/**
	 * DataStore containing the compilation results
	 */
	protected DataStore datastore;

	/**
	 * The common resources. Contains data not required for runtime.
	 */
	protected CommonResources resources;

	public CompileHistory(Configuration inConfiguration, DataStore inDatastore, CommonResources inResources)
	{
		configuration = inConfiguration;
		datastore = inDatastore;
		resources = inResources;
	}

	/**
	 * Load the history from the given filename
	 * 
	 * @param source
	 * @throws IOException
	 */
	public static CompileHistory load(File source) throws IOException
	{
		InputStream is = new FileInputStream(source);
		if (source.getName().endsWith(".cchz"))
		{
			is = new GZIPInputStream(is);
		}
		return load(is);
	}

	/**
	 * Saves the history to the provided filename
	 * 
	 * @param destination
	 * @throws IOException
	 */
	public void save(File destination) throws IOException
	{
		OutputStream os = new FileOutputStream(destination);
		if (destination.getName().endsWith(".cchz"))
		{
			os = new GZIPOutputStream(os);
		}
		save(os);
	}

	public void save(OutputStream destination) throws IOException
	{
		savedOn = new Date();
		ObjectOutputStream oos = new ObjectOutputStream(destination);
		oos.writeObject(this);
		oos.close();
	}

	public static CompileHistory load(InputStream source) throws IOException
	{
		ObjectInputStream ois = new ObjectInputStream(source);
		CompileHistory history;
		try
		{
			DataStore.setIsDeserializing(true);
			history = (CompileHistory) ois.readObject();
			ois.close();
		}
		catch (ClassNotFoundException e)
		{
			logger.warn("Unable to restore compile history. Received exception: " + e, e);
			return null;
		}
		finally 
		{
			DataStore.setIsDeserializing(false);
		}
		return history;
	}

	public Date getSavedOn()
	{
		return savedOn;
	}

	public Configuration getConfiguration()
	{
		return configuration;
	}

	public DataStore getDataStore()
	{
		return datastore;
	}

	public CommonResources getResources()
	{
		return resources;
	}
}
