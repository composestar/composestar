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
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;
import org.jgraph.JGraph;
import org.jgraph.plugins.layouts.CircleGraphLayout;
import org.jgraph.plugins.layouts.JGraphLayoutAlgorithm;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Utils.Logging.CPSLogger;
import Composestar.Visualization.VisCom;
import Composestar.Visualization.Layout.SpringEmbeddedLayoutAlgorithm;
import Composestar.Visualization.Model.Cells.ConcernVertex;
import Composestar.Visualization.UI.Actions.FileExportAction;
import Composestar.Visualization.UI.Utils.CompileHistoryFilter;

/**
 * The main viewport of the visualizer
 * 
 * @author Michiel Hendriks
 */
public class Viewport extends JFrame
{
	private static final long serialVersionUID = 7472894955484974785L;

	protected static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom.UI.Viewport");

	protected transient VisCom controller;

	private JMenuBar mainMenu;

	private JMenu miFile;

	private JMenuItem miOpen;

	private JMenuItem miExport;

	private JMenuItem miClose;

	private JTabbedPane views;

	private JMenu miLayout = null;

	private JMenuItem miLayoutSpring = null;

	private JMenuItem miLayoutCircle = null;

	private JPopupMenu pmProgramView = null; // @jve:decl-index=0:visual-constraint="666,124"

	private JMenuItem miFilterView = null;

	public Viewport(VisCom inController)
	{
		Logger.getRootLogger().addAppender(new MessageBoxAppender(this));
		logger.debug("Creating viewport");
		controller = inController;
		initialize();

		// TODO: dev only
		if (controller.getViewManager() != null)
		{
			getPmProgramView();
			JGraph pv = controller.getViewManager().getProgramView().getGraph();
			views.add("Program view", new JScrollPane(pv));
			pv.addMouseListener(new MouseAdapter()
			{
				public void mousePressed(MouseEvent e)
				{
					maybeShowPopup(e);
				}

				public void mouseReleased(MouseEvent e)
				{
					maybeShowPopup(e);
				}

				private void maybeShowPopup(MouseEvent e)
				{
					if (e.isPopupTrigger())
					{
						JGraph pvGraph = getActiveGraph();
						if (pvGraph.getSelectionCell() instanceof ConcernVertex)
						{
							pmProgramView.show(e.getComponent(), e.getX(), e.getY());
						}
					}
				}
			});
		}
	}

	/**
	 * Returns the currently active JGraph (if any)
	 * 
	 * @return
	 */
	public JGraph getActiveGraph()
	{
		JScrollPane activeTab = (JScrollPane) getViews().getSelectedComponent();
		if (activeTab == null)
		{
			return null;
		}
		return (JGraph) activeTab.getViewport().getView();
	}

	/**
	 * This method initializes this this.setTitle("Compose* Visualization");
	 * this.setVisible(true); this.setSize(new Dimension(588, 241));
	 * this.setJMenuBar(getMainMenu());
	 */
	private void initialize()
	{
		this.setTitle("Compose* Visualization [alpha]");
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
			mainMenu.add(getMiLayout());
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
			miExport.addActionListener(new java.awt.event.ActionListener()
			{
				FileExportAction action = new FileExportAction();

				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					action.execute(getActiveGraph());
				}
			});
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

	/**
	 * This method initializes miLayout
	 * 
	 * @return javax.swing.JMenu
	 */
	private JMenu getMiLayout()
	{
		if (miLayout == null)
		{
			miLayout = new JMenu();
			miLayout.setText("Layout");
			miLayout.add(getMiLayoutSpring());
			miLayout.add(getMiLayoutCircle());
		}
		return miLayout;
	}

	/**
	 * This method initializes miLayoutSpring
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getMiLayoutSpring()
	{
		if (miLayoutSpring == null)
		{
			miLayoutSpring = new JMenuItem();
			miLayoutSpring.setText("Spring");
			miLayoutSpring.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					Object[] layoutCells = controller.getViewManager().getProgramView().getLayoutCells();
					JGraphLayoutAlgorithm.applyLayout(controller.getViewManager().getProgramView().getGraph(),
							new SpringEmbeddedLayoutAlgorithm(new Rectangle(300, 300), 30), layoutCells);
				}
			});
		}
		return miLayoutSpring;
	}

	/**
	 * This method initializes miLayoutCircle
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getMiLayoutCircle()
	{
		if (miLayoutCircle == null)
		{
			miLayoutCircle = new JMenuItem();
			miLayoutCircle.setText("Circle");
			miLayoutCircle.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					Object[] layoutCells = controller.getViewManager().getProgramView().getLayoutCells();
					JGraphLayoutAlgorithm.applyLayout(controller.getViewManager().getProgramView().getGraph(),
							new CircleGraphLayout(), layoutCells);
				}
			});
		}
		return miLayoutCircle;
	}

	/**
	 * This method initializes pmProgramView
	 * 
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getPmProgramView()
	{
		if (pmProgramView == null)
		{
			pmProgramView = new JPopupMenu();
			pmProgramView.setVisible(true);
			pmProgramView.add(getMiFilterView());
		}
		return pmProgramView;
	}

	/**
	 * This method initializes miFilterView
	 * 
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getMiFilterView()
	{
		if (miFilterView == null)
		{
			miFilterView = new JMenuItem();
			miFilterView.setText("Show filter view");
			miFilterView.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					JGraph pvGraph = getActiveGraph();
					if (pvGraph.getSelectionCell() instanceof ConcernVertex)
					{
						Concern concern = ((ConcernVertex) pvGraph.getSelectionCell()).getConcern();
						logger.info("Adding Filter View for " + concern);
						views.add(concern.getQualifiedName(), new JScrollPane(controller.getViewManager()
								.getFilterView(concern).getGraph()));
					}
				}
			});
		}
		return miFilterView;
	}
} // @jve:decl-index=0:visual-constraint="10,10"
