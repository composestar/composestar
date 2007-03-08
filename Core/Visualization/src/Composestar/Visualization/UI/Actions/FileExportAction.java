/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.UI.Actions;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.jgraph.JGraph;

import Composestar.Utils.Logging.CPSLogger;
import Composestar.Visualization.Export.ExportException;
import Composestar.Visualization.Export.FileGraphExporter;
import Composestar.Visualization.Export.ImageExporter;
import Composestar.Visualization.UI.Utils.FileGraphExporterFilter;

/**
 * Show a file selection dialog to export the graph
 * 
 * @author Michiel Hendriks
 */
public class FileExportAction extends AbstractAction
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom.UI.Actions.FileExport");

	protected List<FileGraphExporter> exporters;

	protected JFileChooser fc;

	public FileExportAction()
	{
		fc = new JFileChooser();
		exporters = new LinkedList<FileGraphExporter>();
		addExporter(new ImageExporter());
	}

	public void addExporter(FileGraphExporter newExporter)
	{
		exporters.add(newExporter);
		fc.addChoosableFileFilter(new FileGraphExporterFilter(newExporter));
	}

	public boolean execute(JGraph activeGraph)
	{
		if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
		{
			File dest = fc.getSelectedFile();
			try
			{
				if (fc.getFileFilter() instanceof FileGraphExporterFilter)
				{
					FileGraphExporterFilter filter = (FileGraphExporterFilter) fc.getFileFilter();
					FileGraphExporter exporter = filter.getExporter();
					dest = exporter.setDefaultExtention(dest);
					if (!confirmOverwrite(dest))
					{
						return false;
					}
					logger.info("Exporting graph to " + dest + " using exporter " + exporter);
					return exporter.tryExport(activeGraph, dest);
				}
				else
				{
					if (!confirmOverwrite(dest))
					{
						return false;
					}
					for (FileGraphExporter exporter : exporters)
					{
						if (exporter.tryExport(activeGraph, dest))
						{
							logger.info("Exported graph to " + dest + " using exporter " + exporter);
							return true;
						}
					}
				}
			}
			catch (ExportException e)
			{
				logger.error("Exception while exporting the graph. " + e.getMessage(), e);
			}
			JOptionPane.showMessageDialog(null, "No exporter available that can export to the given extention: "
					+ dest.getName(), "Export", JOptionPane.ERROR_MESSAGE);
		}
		return false;
	}

	/**
	 * Returns true when the file should be overwritten
	 * 
	 * @param destination
	 * @return
	 */
	protected boolean confirmOverwrite(File destination)
	{
		if (!destination.exists())
		{
			return true;
		}
		return JOptionPane.showConfirmDialog(null, "Are you sure you want to overwrite the file " + destination,
				"Export", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}
}
