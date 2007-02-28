/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */

package Composestar.Visualization;

import java.awt.Dimension;
import java.awt.HeadlessException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
//import javax.swing.filechooser.FileNameExtensionFilter;

import Composestar.Core.Master.CompileHistory;
import Composestar.Utils.Logging.CPSLogger;

/**
 * The main viewport of the visualizer
 * 
 * @author Michiel Hendriks
 */
public class Viewport extends JFrame
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom.Viewport");

	protected VisCom controller;

	private JMenuBar mainMenu = null;

	private JMenu miFile = null;

	private JMenuItem miOpen = null;

	private JMenuItem miExport = null;

	private JMenuItem miClose = null;

	private JScrollPane view = null;

	public Viewport(VisCom inController) throws HeadlessException
	{
		logger.debug("Creating viewport");
		controller = inController;
		initialize();
	}

	/**
	 * This method initializes this this.setTitle("Compose* Visualization");
	 * this.setVisible(true); this.setSize(new Dimension(588, 241));
	 * this.setJMenuBar(getMainMenu());
	 */
	private void initialize()
	{
		this.setTitle("Compose* Visualization");
		this.setSize(new Dimension(627, 484));
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setJMenuBar(getMainMenu());
		this.setContentPane(getView());
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
					/*
					JFileChooser fc = new JFileChooser();
					fc.addChoosableFileFilter(new FileNameExtensionFilter("Compose* Compile History",
							CompileHistory.EXT_NORMAL, CompileHistory.EXT_COMPRESSED));
					if (fc.showOpenDialog(miOpen) == JFileChooser.APPROVE_OPTION)
					{
						controller.openCompileHistory(fc.getSelectedFile());
						// TODO: handle
					}
					*/
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
	 * This method initializes view
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getView()
	{
		if (view == null)
		{
			view = new JScrollPane();
		}
		return view;
	}
} // @jve:decl-index=0:visual-constraint="10,10"
