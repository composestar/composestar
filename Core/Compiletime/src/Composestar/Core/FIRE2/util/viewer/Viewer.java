/*
 * Created on 17-aug-2006
 *
 */
package Composestar.Core.FIRE2.util.viewer;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import Composestar.Core.FIRE2.model.ExecutionModel;

public class Viewer extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2886400275462603530L;

	private ViewPanel viewPanel;

	private JPanel controlPanel;

	public Viewer(ExecutionModel model)
	{
		super();

		init(model);
	}

	public Viewer(JPanel controlPanel, ExecutionModel model)
	{
		super();

		this.controlPanel = controlPanel;
		init(model);
	}

	public void highlightNodes(Collection executionStates)
	{
		viewPanel.highlightNodes(executionStates);
	}

	private void init(ExecutionModel model)
	{
		viewPanel = new ViewPanel(model);
		JScrollPane scroller = new JScrollPane(viewPanel);

		if (controlPanel != null)
		{
			JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			pane.setDividerLocation(0.8d);

			pane.setTopComponent(scroller);

			JScrollPane scroller2 = new JScrollPane(controlPanel);
			pane.setBottomComponent(scroller2);

			this.getContentPane().add(pane);
		}
		else
		{
			this.getContentPane().add(scroller, BorderLayout.CENTER);
		}

		this.setSize(600, 600);
		this.setVisible(true);

		this.addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				System.exit(0);
			}
		});
	}
}
