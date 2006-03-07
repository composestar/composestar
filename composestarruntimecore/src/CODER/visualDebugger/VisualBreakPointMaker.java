package Composestar.RuntimeCore.CODER.VisualDebugger;

import Composestar.RuntimeCore.CODER.BreakPoint.*;
import Composestar.RuntimeCore.CODER.BreakPoint.Parsers.*;
import Composestar.RuntimeCore.CODER.BreakPoint.Parsers.LTL.*;
import Composestar.RuntimeCore.CODER.BreakPoint.Parsers.Reg.*;

import Composestar.RuntimeCore.Utils.Debug;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

/**
 * Summary description for VisualBreakPointMaker.
 */
public class VisualBreakPointMaker extends Panel implements ActionListener
{
	private class VisualBreakPointMakerFrame extends Frame 
	{
		public VisualBreakPointMakerFrame() 
		{
			setTitle("Composition filter debugger");
			setSize(800,250);
			
			Dimension dim = getToolkit().getScreenSize();
			Rectangle abounds = getBounds();
			setLocation((dim.width - abounds.width) / 2,
				(dim.height - abounds.height) / 2);

			addWindowListener(new WindowAdapter() 
			{
				public void windowClosing(WindowEvent e) 
				{
					closeMaker();
				}
			});
		}

		public void closeMaker()
		{
			popup.dispose();
			dispose();
			debugger.setBreakPoint(breakpoint);
			debugger.startVisualizer();
		}
	}

	private BreakPoint breakpoint = null;

	private TextArea regularStopExpression = new TextArea();
	private TextArea ltlValueExpression = new TextArea();
	private TextArea selectors = new TextArea("*");
	private TextArea targets = new TextArea("*");

	private VisualDebugger debugger;

	private PopUp popup = new PopUp();
	private VisualBreakPointMakerFrame frame= new VisualBreakPointMakerFrame();

	public VisualBreakPointMaker(VisualDebugger debugger)
	{
		this.debugger = debugger;
		setLayout(new BorderLayout());

		Panel top = new Panel();
		top.setLayout(new BorderLayout());
		Panel one = new Panel();
		one.setLayout(new BorderLayout());
		one.add(new Label("Selectors"),BorderLayout.WEST);
		one.add(new Label("Targets"),BorderLayout.EAST);
		top.add(one,BorderLayout.NORTH);

		one = new Panel();
		one.add(selectors,BorderLayout.WEST);
		one.add(targets,BorderLayout.EAST);
		top.add(one,BorderLayout.SOUTH);

		add(top,BorderLayout.NORTH);

/*		Panel center = new Panel();
		panel.setLayout(new BorderLayout());
 		one = new Panel();
		add(new Label("LTL Condition Formula",BorderLayout.WEST));
		add(ltlValueExpression,BorderLayout.WEST);
		add(new Label("Regular Stop Sequence"));
		add(regularStopExpression,BorderLayout.EAST);
		add(one,BorderLayout.CENTER,BorderLayout.EAST);		
		add(one,BorderLayout.CENTER);*/

		one = new Panel();
		Button button = new Button("Proceed");
		button.addActionListener(this);
		one.add(button,BorderLayout.WEST);
		button = new Button("Cancel");
		one.add(button,BorderLayout.EAST);
		add(one,BorderLayout.SOUTH);

		frame.add(this);
		frame.show();
	}

	public void actionPerformed(ActionEvent evt) 
	{
		breakpoint = createBreakPoint();
		if(breakpoint != null)
		{
			debugger.setBreakPoint(breakpoint);
			frame.closeMaker();
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
		popup.update("Parsing error", "Error while parsing " + parsing + "\n" + e.getMessage());
	}

	private class PopUp extends Frame implements ActionListener
	{
		private TextField text = new java.awt.TextField();

		private void close()
		{
			setVisible(false);
			repaint();
		}

		public void actionPerformed(ActionEvent evt) 
		{
			close();
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
			Panel panel = new Panel(new GridLayout(2,1));
			add(panel);
			Button button = new Button("Ok");
			panel.add(text);
			panel.add(button);
			button.addActionListener(this);

			setVisible(false);
			setSize(100,75);
		}

		public void update(String title, String text)
		{
			setTitle(title);
			this.text.setText(text);
			setVisible(true);
			show();
		}
	}
}
