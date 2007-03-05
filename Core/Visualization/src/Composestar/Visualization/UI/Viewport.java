/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization.UI;

import java.awt.Dimension;
import java.awt.HeadlessException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import Composestar.Utils.Logging.CPSLogger;
import Composestar.Visualization.VisCom;

/**
 * The main viewport of the visualizer
 * 
 * @author Michiel Hendriks
 */
public class Viewport extends JFrame
{
	private static final long serialVersionUID = 7472894955484974785L;

	protected static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom.Viewport");

	protected transient VisCom controller;

	private JMenuBar mainMenu;

	private JMenu miFile;

	private JMenuItem miOpen;

	private JMenuItem miExport;

	private JMenuItem miClose;

	private JTabbedPane views;

	public Viewport(VisCom inController) throws HeadlessException
	{
		Logger.getRootLogger().addAppender(new MessageBoxAppender(this));
		logger.debug("Creating viewport");
		controller = inController;
		initialize();

		// TODO: dev only
		if (controller.getViewManager() != null)
		{
			views.add("Program view", new JScrollPane(controller.getViewManager().getProgramView().getGraph()));
		}
	}

	/**
	 * This method initializes this this.setTitle("Compose* Visualization");
	 * this.setVisible(true); this.setSize(new Dimension(588, 241));
	 * this.setJMenuBar(getMainMenu());
	 */
	private void initialize()
	{
		this.setTitle("Compose* Visualization");
		this.setContentPane(getViews());
		this.setJMenuBar(getMainMenu());
		this.setSize(new Dimension(627, 484));
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setVisible(true);
	}

	/**
	 * This method initializes mainMenu
	 * 
	 * @return javax.swing.JMenuBar
	 */
	private JMenuBar getMainMenu()
	{
		if (mainMenu == null)
		{
			mainMenu = new JMenuBar();
			mainMenu.add(getMiFile());
		}
		return mainMenu;
	}

	/**
	 * This method initializes miFile
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getMiFile()
	{
		if (miFile == null)
		{
			miFile = new JMenu();
			miFile.setText("File");
			miFile.add(getMiOpen());
			miFile.add(getMiExport());
			miFile.add(getMiClose());
		}
		return miFile;
	}

	/**
	 * This method initializes miOpen
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getMiOpen()
	{
		if (miOpen == null)
		{
			miOpen = new JMenuItem();
			miOpen.setText("Open");
			miOpen.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					views.removeAll();
					JFileChooser fc = new JFileChooser();

					fc.addChoosableFileFilter(new CompileHistoryFilter());
					if (fc.showOpenDialog(miOpen) == JFileChooser.APPROVE_OPTION)
					{
						controller.openCompileHistory(fc.getSelectedFile());
						// TODO: handle
					}

				}
			});
		}
		return miOpen;
	}

	/**
	 * This method initializes miExport
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getMiExport()
	{
		if (miExport == null)
		{
			miExport = new JMenuItem();
			miExport.setText("Export");
		}
		return miExport;
	}

	/**
	 * This method initializes miClose
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getMiClose()
	{
		if (miClose == null)
		{
			miClose = new JMenuItem();
			miClose.setText("Close");
			miClose.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					dispose();
				}
			});
		}
		return miClose;
	}

	/**
	 * This method initializes views
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getViews()
	{
		if (views == null)
		{
			views = new JTabbedPane();
		}
		return views;
	}
} // @jve:decl-index=0:visual-constraint="10,10"
