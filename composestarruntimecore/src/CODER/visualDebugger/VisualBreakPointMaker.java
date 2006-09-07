package Composestar.RuntimeCore.CODER.VisualDebugger;

import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.CODER.BreakPoint.Parsers.*;
import Composestar.RuntimeCore.CODER.BreakPoint.Parsers.LTL.*;
import Composestar.RuntimeCore.CODER.BreakPoint.Parsers.Reg.*;

import Composestar.RuntimeCore.Utils.Debug;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

/**
 * Summary description for VisualBreakPointMaker.
 */
public class VisualBreakPointMaker extends JFrame implements ActionListener
{
	private PopUp popup = new PopUp();
	private VisualDebugger debugger;
	private BreakPoint breakpoint = null;

	private TextArea senders = new TextArea("*");
	private TextArea selectors = new TextArea("*");
	private TextArea targets = new TextArea("*");

	private TextArea regularStopExpression = new TextArea();
	private TextArea ltlValueExpression = new TextArea();

	private JButton proceed = new JButton("Proceed");
	private JButton clear = new JButton ("Clear");

	private JCheckBox onlyAccepting = new JCheckBox("Only halt when a filter accepts");

	public VisualBreakPointMaker(VisualDebugger debugger)
	{
		setTitle("Composition filter debugger");
		setSize(1000,250);
			
		Dimension dim = getToolkit().getScreenSize();
		Rectangle abounds = getBounds();
		setLocation((dim.width - abounds.width) / 2,
			(dim.height - abounds.height) / 2);

		addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent e) 
			{
				System.exit(0);
			}
		});

		this.debugger = debugger;

		JPanel senderPane = new JPanel();
		senderPane.setLayout(new BorderLayout());
		senderPane.add(new Label("Senders"),BorderLayout.NORTH);
		senderPane.add(senders,BorderLayout.SOUTH);

		JPanel selectorsPane = new JPanel();
		selectorsPane.setLayout(new BorderLayout());
		selectorsPane.add(new Label("Selectors"),BorderLayout.NORTH);
		selectorsPane.add(selectors,BorderLayout.SOUTH);

		JPanel targetPane = new JPanel();
		targetPane.setLayout(new BorderLayout());
		targetPane.add(new Label("Targets"),BorderLayout.NORTH);
		targetPane.add(targets,BorderLayout.SOUTH);

		JPanel top = new JPanel();
		top.setLayout(new BorderLayout());
		top.add(senderPane,BorderLayout.WEST);
		top.add(selectorsPane,BorderLayout.CENTER);
		top.add(targetPane,BorderLayout.EAST);

		/*		JPanel center = new JPanel();
		 panel.setLayout(bordl;
		 one = new Panel();
		 add(new Label("LTL Condition Formula",BorderLayout.WEST));
		 add(ltlValueExpression,BorderLayout.WEST);
		 add(new Label("Regular Stop Sequence"));
		 add(regularStopExpression,BorderLayout.EAST);
		 add(one,BorderLayout.CENTER,BorderLayout.EAST);		
		 add(one,BorderLayout.CENTER);*/

		JPanel bottom = new JPanel();
		proceed.addActionListener(this);
		clear.addActionListener(this);
		bottom.add(proceed);
		bottom.add(clear);
		bottom.add(onlyAccepting);

		Container content = getContentPane();
		content.setLayout(new BorderLayout());
		content.add(top,BorderLayout.NORTH);
		//content.add(middle,BorderLayout.CENTER);
		content.add(bottom,BorderLayout.SOUTH);

		show();
	}

	public void actionPerformed(ActionEvent evt) 
	{
		if(proceed.equals(evt.getSource()))
		{
			proceed();
		}
		else if(clear.equals(evt.getSource()))
		{
			clear();
		}
	}

	private void clear()
	{
		senders.setText("*");
		selectors.setText("*");
		targets.setText("*");
		onlyAccepting.setSelected(false);
	}

	private void proceed()
	{
		breakpoint = createBreakPoint();
		if(breakpoint != null)
		{
			debugger.setBreakPoint(breakpoint);
			popup.dispose();
			dispose();
			debugger.setBreakPoint(breakpoint);
			debugger.startVisualizer();
		}
	}

	public BreakPoint createBreakPoint()
	{
		String wasBussyWith = "Selector Expression";
		try
		{
			ObjectBreakPoint selector = new SenderBreakPoint(debugger.getHalter(),selectors.getText());
			Debug.out(Debug.MODE_DEBUG,"CODER","Sender list:" + selector.toString());

			wasBussyWith = "Target Expression";

			ObjectBreakPoint target = new TargetBreakPoint(debugger.getHalter(),targets.getText());
			Debug.out(Debug.MODE_DEBUG,"CODER","Target list:" + target.toString());

			breakpoint = new BreakPointAnd(debugger.getHalter(),selector,target);

			wasBussyWith = "LTL Expression";

			BreakPointParser ltlParser = new BreakPointLTLParser();
			BreakPoint ltlBreakPoint = ltlParser.parse(ltlValueExpression.getText(),debugger.getHalter());
			if(ltlBreakPoint != null)
			{
				breakpoint = new BreakPointAnd(debugger.getHalter(),breakpoint,ltlBreakPoint);
			}
			
			wasBussyWith = "Regular Expression";

			BreakPointParser regParser = new BreakPointRegParser();
			BreakPoint regBreakPoint = regParser.parse(regularStopExpression.getText(),debugger.getHalter());
			
			if(regBreakPoint != null)
			{
				breakpoint = new BreakPointAnd(debugger.getHalter(),breakpoint,regBreakPoint);
			}

			if(onlyAccepting.isSelected())
			{
				breakpoint = new BreakPointAnd(debugger.getHalter(),breakpoint,new AcceptingOnlyBreakPoint(debugger.getHalter()));
			}
		}
		catch(BreakPointParseException e)
		{
			parseException(wasBussyWith, e);
			return null;
		}
		return breakpoint;
	}

	public void parseException(String parsing, BreakPointParseException e)
	{
		popup.update("Parsing error", "Error while parsing " + parsing + '\n' + e.getMessage());
	}

	private class PopUp extends JFrame implements ActionListener
	{
		private JTextField textField = new JTextField("");
		private JButton button = new JButton("Ok");
		private void close()
		{
			setVisible(false);
			repaint();
		}

		public void actionPerformed(ActionEvent evt) 
		{
			if(button.equals(evt.getSource()))
			{
				close();
			}
		}

		public PopUp()
		{
			addWindowListener(new WindowAdapter() 
			{
				public void windowClosing(WindowEvent e) 
				{
					close();
				}
			});
			Container panel = getContentPane();
			panel.setLayout(new GridLayout(2,1));
			panel.add(textField);
			panel.add(button);
			button.addActionListener(this);

			setVisible(false);
			setSize(100,75);
		}

		public void update(String title, String text)
		{
			setTitle(title);
			textField.setText(text);
			setVisible(true);
			show();
		}
	}
}
