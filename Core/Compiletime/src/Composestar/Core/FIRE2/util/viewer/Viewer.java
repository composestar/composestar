/*
 * Created on 17-aug-2006
 *
 */
package Composestar.Core.FIRE2.util.viewer;

import java.awt.BorderLayout;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.FlowModel;

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

	public Viewer(FlowModel model)
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

	public void highlightNodes(Collection<ExecutionState> executionStates)
	{
		viewPanel.highlightNodes(executionStates);
	}

	private void init(FlowModel model)
	{
		viewPanel = new ViewPanel(model);

		init();
	}

	private void init(ExecutionModel model)
	{
		viewPanel = new ViewPanel(model);

		init();
	}

	private void init()
	{
		JScrollPane scroller = new JScrollPane(viewPanel);

		if (controlPanel != null)
		{
			JSplitPane pane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			pane.setDividerLocation(0.8d);

			pane.setTopComponent(scroller);

			JScrollPane scroller2 = new JScrollPane(controlPanel);
			pane.setBottomComponent(scroller2);

			getContentPane().add(pane);
		}
		else
		{
			getContentPane().add(scroller, BorderLayout.CENTER);
		}

		this.setSize(600, 600);
		setVisible(true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
}
