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

import Composestar.Utils.Logging.CPSLogger;
import Composestar.Visualization.Export.ExportException;
import Composestar.Visualization.Export.FileGraphExporter;
import Composestar.Visualization.Export.ImageExporter;
import Composestar.Visualization.Model.CpsJGraph;
import Composestar.Visualization.UI.Utils.FileGraphExporterFilter;

/**
 * Show a file selection dialog to export the graph
 * 
 * @author Michiel Hendriks
 */
public class FileExportAction extends ActiveGraphAction
{
	private static final long serialVersionUID = -3225822720564783420L;

	protected static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom.UI.Actions.FileExport");

	protected transient List<FileGraphExporter> exporters;

	protected transient JFileChooser fc;

	public FileExportAction()
	{
		super("Export");
		fc = new JFileChooser();
		exporters = new LinkedList<FileGraphExporter>();
		addExporter(new ImageExporter());
	}

	public void addExporter(FileGraphExporter newExporter)
	{
		exporters.add(newExporter);
		fc.addChoosableFileFilter(new FileGraphExporterFilter(newExporter));
	}

	@Override
	public void execute(CpsJGraph activeGraph)
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
						return;
					}
					logger.info("Exporting graph to " + dest + " using exporter " + exporter);
					exporter.tryExport(activeGraph, dest);
					return;
				}
				else
				{
					if (!confirmOverwrite(dest))
					{
						return;
					}
					for (FileGraphExporter exporter : exporters)
					{
						if (exporter.tryExport(activeGraph, dest))
						{
							logger.info("Exported graph to " + dest + " using exporter " + exporter);
							return;
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
		return;
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
