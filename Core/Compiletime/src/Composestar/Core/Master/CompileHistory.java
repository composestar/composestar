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

import Composestar.Core.CpsRepository2.Repository;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Contains the intermediate results of a compilation of a Compose* program.
 * This object is used for incremental compilation but also for visualization.
 * 
 * @author Michiel Hendriks
 */
public class CompileHistory implements Serializable
{
	private static final long serialVersionUID = 758860680471199813L;

	private static final CPSLogger logger = CPSLogger.getCPSLogger("CompileHistory");

	/**
	 * Extention of uncompressed composestar compile history
	 */
	public static final String EXT_NORMAL = "cch";

	/**
	 * Compressed composestar compile history
	 */
	public static final String EXT_COMPRESSED = EXT_NORMAL + "z";

	/**
	 * Default filename for composestar compile history
	 */
	public static final String DEFAULT_FILENAME = "ComposestarHistory." + EXT_COMPRESSED;

	/**
	 * Date this history was saved on
	 */
	protected Date savedOn;

	/**
	 * The common resources. Contains data not required for runtime.
	 */
	protected CommonResources resources;

	public CompileHistory(CommonResources inResources)
	{
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
		if (FileUtils.getExtension(source.getName()).equalsIgnoreCase(EXT_COMPRESSED))
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
		try
		{
			if (FileUtils.getExtension(destination.getName()).equalsIgnoreCase(EXT_COMPRESSED))
			{
				os = new GZIPOutputStream(os);
			}
			save(os);
		}
		finally
		{
			os.close();
		}
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
		ObjectInputStream ois = getOIS(source);
		CompileHistory history;
		try
		{
			history = (CompileHistory) ois.readObject();
			ois.close();
		}
		catch (ClassNotFoundException e)
		{
			logger.warn("Unable to restore compile history. Received exception: " + e, e);
			return null;
		}
		return history;
	}

	protected static ObjectInputStream getOIS(InputStream is) throws IOException
	{
		if (is instanceof ObjectInputStream)
		{
			return (ObjectInputStream) is;
		}
		return new ObjectInputStream(is);
	}

	public Date getSavedOn()
	{
		return new Date(savedOn.getTime());
	}

	public Repository getRepository()
	{
		return resources.repository();
	}

	public CommonResources getResources()
	{
		return resources;
	}
}
