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
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
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

import Composestar.Utils.Logging.CPSLogger;
import Composestar.Visualization.VisCom;
import Composestar.Visualization.Model.CpsJGraph;
import Composestar.Visualization.Model.CpsView;
import Composestar.Visualization.Model.FilterView;
import Composestar.Visualization.Model.ProgramView;
import Composestar.Visualization.Model.Cells.ConcernVertex;
import Composestar.Visualization.Model.Cells.TrailEdge;
import Composestar.Visualization.UI.Actions.ActionManager;
import Composestar.Visualization.UI.Actions.FileExportAction;
import Composestar.Visualization.UI.Actions.FileOpenAction;
import Composestar.Visualization.UI.Actions.FileQuitAction;
import Composestar.Visualization.UI.Actions.LayoutAction;
import Composestar.Visualization.UI.Actions.OpenFilterActionView;
import Composestar.Visualization.UI.Actions.OpenFilterView;

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

	protected transient ActionManager actionManager;

	private JMenuBar mainMenu;

	private JMenu miFile;

	private JTabbedPane views;

	private JMenu miLayout;

	private JMenuItem miLayoutCircle;

	private JPopupMenu pmProgramView; // @jve:decl-index=0:visual-constraint="666,124"

	private JPopupMenu pmFilterView; // @jve:decl-index=0:visual-constraint="712,183"

	public Viewport()
	{
		Logger.getRootLogger().addAppender(new MessageBoxAppender(this));
		logger.debug("Creating viewport");
		actionManager = new ActionManager(this);
		initialize();
	}

	public Viewport(VisCom inController, File historyFile)
	{
		this();
		controller = inController;

		if (controller.openCompileHistory(historyFile))
		{
			openProgramView(controller.getViewManager().getProgramView().getGraph());
			for (String fv : controller.openFilterViews)
			{
				CpsView view = controller.getViewManager().getFilterView(fv);
				if (view != null)
				{
					openGraph(view.getGraph());
				}
			}
			for (String fav : controller.openFilterActionViews)
			{
				CpsView view = controller.getViewManager().getFilterActionView(fav);
				if (view != null)
				{
					openGraph(view.getGraph());
				}
			}
		}
	}

	/**
	 * This method initializes this this.setTitle("Compose* Visualization");
	 * this.setVisible(true); this.setSize(new Dimension(588, 241));
	 * this.setJMenuBar(getMainMenu());
	 */
	private void initialize()
	{
		setTitle("Compose* Visualization [alpha]");
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				getClass().getResource("/Composestar/Visualization/UI/Graphics/logo.png")));
		setContentPane(getViews());
		setJMenuBar(getMainMenu());
		setSize(new Dimension(640, 480));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setVisible(true);
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
			miFile.add(new JMenuItem(actionManager.getAction(FileOpenAction.class)));
			miFile.add(new JMenuItem(actionManager.getAction(FileExportAction.class)));
			miFile.add(new JMenuItem(new FileQuitAction(this)));
		}
		return miFile;
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
			miLayout.add(new JMenuItem(actionManager.getAction(LayoutAction.class)));
			miLayout.add(getMiLayoutCircle());
		}
		return miLayout;
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
			pmProgramView.add(new JMenuItem(actionManager.getAction(OpenFilterView.class)));
		}
		return pmProgramView;
	}

	/**
	 * This method initializes pmFilterView
	 * 
	 * @return javax.swing.JPopupMenu
	 */
	private JPopupMenu getPmFilterView()
	{
		if (pmFilterView == null)
		{
			pmFilterView = new JPopupMenu();
			pmFilterView.setVisible(true);
			pmFilterView.add(new JMenuItem(actionManager.getAction(OpenFilterActionView.class)));
		}
		return pmFilterView;
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
		logger.info("Opening new graph of type " + view.getClass().toString() + ": " + view.getName());
		if (view instanceof ProgramView)
		{
			openProgramView(newGraph);
		}
		else if (view instanceof FilterView)
		{
			openFilterView(newGraph);
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
			@Override
			public void mousePressed(MouseEvent e)
			{
				maybeShowPopup(e);
			}

			@Override
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

	public void openFilterView(CpsJGraph newGraph)
	{
		getPmFilterView();
		views.setSelectedComponent(views.add(newGraph.getName(), new JScrollPane(newGraph)));
		newGraph.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				maybeShowPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e)
			{
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					JGraph pvGraph = getCurrentGraph();
					if (pvGraph.getSelectionCell() instanceof TrailEdge)
					{
						pmFilterView.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		});
	}

} // @jve:decl-index=0:visual-constraint="10,10"
