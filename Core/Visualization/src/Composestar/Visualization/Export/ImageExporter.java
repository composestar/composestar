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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;

import org.jgraph.JGraph;

import Composestar.Utils.FileUtils;

/**
 * Exports the graph to an raster image file
 * 
 * @author Michiel Hendriks
 */
public class ImageExporter extends FileGraphExporter
{
	protected Set<String> formats;

	public ImageExporter()
	{
		formats = new HashSet<String>();
		String[] formatNames = ImageIO.getWriterFormatNames();
		for (String name : formatNames)
		{
			formats.add(name);
		}
	}

	@Override
	public Set<String> getFormats()
	{
		return formats;
	}

	@Override
	public boolean tryExport(JGraph graph, File destination) throws ExportException
	{
		if (destination == null)
		{
			throw new ExportException("Destination has not be set");
		}
		String format = FileUtils.getExtension(destination);
		if (!formats.contains(format))
		{
			return false;
		}

		// clear selection because it's included in the resulting image
		graph.getSelectionModel().clearSelection();
		BufferedImage img = graph.getImage(graph.getBackground(), 5);
		try
		{
			ImageIO.write(img, format, destination);
		}
		catch (IOException e)
		{
			throw new ExportException("Failed to export graph to file " + destination + ". " + e.getMessage(), e);
		}
		return true;
	}

	/**
	 * Defaults to PNG
	 */
	@Override
	public File setDefaultExtention(File filename)
	{
		String format = FileUtils.getExtension(filename);
		if (!formats.contains(format))
		{
			return new File(filename + ".png");
		}
		return filename;
	}

	@Override
	public String getDescription()
	{
		return "Image files";
	}

}
