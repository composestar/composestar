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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

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
import Composestar.Visualization.Model.CpsJGraph;
import Composestar.Visualization.Model.CpsView;
import Composestar.Visualization.Model.ProgramView;
import Composestar.Visualization.Model.Cells.ConcernVertex;
import Composestar.Visualization.UI.Actions.ActionManager;
import Composestar.Visualization.UI.Actions.FileExportAction;
import Composestar.Visualization.UI.Actions.FileOpenAction;
import Composestar.Visualization.UI.Actions.FileQuitAction;
import Composestar.Visualization.UI.Actions.LayoutAction;

/**
 * The main viewport of the visualizer
 * 
 * @author Michiel Hendriks
 */
public class Viewport extends JFrame implements CpsJGraphProvider
{
	private static final long serialVersionUID = 7472894955484974785L;

	protected static final CPSLogger logger = CPSLogger.getCPSLogger("VisCom.UI.Viewport");

	protected transient VisCom controller;

	protected ActionManager actionManager;

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
		actionManager = new ActionManager(this);
		controller = inController;
		initialize();

		// TODO: dev only
		if (controller.getViewManager() != null)
		{
			openProgramView(controller.getViewManager().getProgramView().getGraph());
		}
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
			miOpen = new JMenuItem(actionManager.getAction(FileOpenAction.class));
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
			miExport = new JMenuItem(actionManager.getAction(FileExportAction.class));
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
			miClose = new JMenuItem(new FileQuitAction(this));
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
			miLayoutSpring = new JMenuItem(actionManager.getAction(LayoutAction.class));
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
					JGraph pvGraph = getCurrentGraph();
					if (pvGraph.getSelectionCell() instanceof ConcernVertex)
					{
						Concern concern = ((ConcernVertex) pvGraph.getSelectionCell()).getConcern();
						logger.info("Adding Filter View for " + concern);
						openGraph(controller.getViewManager().getFilterView(concern).getGraph());
					}
				}
			});
		}
		return miFilterView;
	}

	public VisCom getController()
	{
		return controller;
	}

	public List<CpsJGraph> getAllGraphs()
	{
		List<CpsJGraph> list = new ArrayList<CpsJGraph>();
		for (int i = 0; i < views.getTabCount(); i++)
		{
			Component c = views.getComponentAt(i);
			if (c instanceof JScrollPane)
			{
				if (((JScrollPane) c).getViewport().getView() instanceof CpsJGraph)
				{
					list.add((CpsJGraph) ((JScrollPane) c).getViewport().getView());
				}
			}
		}
		return list;
	}

	public CpsJGraph getCurrentGraph()
	{
		JScrollPane activeTab = (JScrollPane) getViews().getSelectedComponent();
		if (activeTab == null)
		{
			return null;
		}
		return (CpsJGraph) activeTab.getViewport().getView();
	}

	public void closeAllGraphs()
	{
		views.removeAll();
	}

	public void closeGraph(CpsJGraph graph)
	{
		int idx = indexOfGraph(graph);
		if (idx > -1)
		{
			views.remove(idx);
		}
	}

	protected int indexOfGraph(CpsJGraph newGraph)
	{
		for (int i = 0; i < views.getTabCount(); i++)
		{
			Component c = views.getComponentAt(i);
			if (c instanceof JScrollPane)
			{
				if (((JScrollPane) c).getViewport().getView().equals(newGraph))
				{
					return i;
				}
			}
		}
		return -1;
	}

	public void openGraph(CpsJGraph newGraph)
	{
		int idx = indexOfGraph(newGraph);
		if (idx > -1)
		{
			views.setSelectedIndex(idx);
			return;
		}
		CpsView view = newGraph.getCpsView();
		if (view instanceof ProgramView)
		{
			openProgramView(newGraph);
		}
		else
		{
			views.setSelectedComponent(views.add(view.getName(), new JScrollPane(newGraph)));
		}
	}

	public void openProgramView(CpsJGraph newGraph)
	{
		getPmProgramView();
		views.setSelectedComponent(views.add(newGraph.getName(), new JScrollPane(newGraph)));
		newGraph.addMouseListener(new MouseAdapter()
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
					JGraph pvGraph = getCurrentGraph();
					if (pvGraph.getSelectionCell() instanceof ConcernVertex)
					{
						pmProgramView.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		});
	}
} // @jve:decl-index=0:visual-constraint="10,10"
