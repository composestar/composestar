/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.Export;

import java.io.File;
import java.util.Set;

import org.jgraph.JGraph;

/**
 * @author Michiel Hendriks
 */
public abstract class FileGraphExporter extends GraphExporter
{
	/**
	 * Return a descriptive name for this exporter. This will be used for
	 * example by the UI in the file selection dialog.
	 * 
	 * @return
	 */
	public abstract String getDescription();

	/**
	 * Return a list of supported formats of this exporter.
	 * 
	 * @return
	 */
	public abstract Set<String> getFormats();

	/**
	 * Try to export the graph using these settings. The UI will call this for
	 * each registered FileGraphExporter until one returns true. This function
	 * should return true when it has been exported or false when this type is
	 * not supported. When an exception is thrown the UI will assume this
	 * exporter supported the exporting of this file.
	 * 
	 * @param graph
	 * @param destination
	 * @return
	 * @throws ExportException
	 */
	public abstract boolean tryExport(JGraph graph, File destination) throws ExportException;

	/**
	 * Set the default extention when needed.
	 * 
	 * @param filename
	 * @return
	 */
	public File setDefaultExtention(File filename)
	{
		return filename;
	}

	@Override
	public void export(JGraph graph, String destination) throws ExportException
	{
		tryExport(graph, new File(destination));
	}
}
