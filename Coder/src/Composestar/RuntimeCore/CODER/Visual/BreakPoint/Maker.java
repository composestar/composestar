package Composestar.RuntimeCore.CODER.Visual.BreakPoint;

import Composestar.RuntimeCore.CODER.BreakPoint.BreakPoint;
import Composestar.RuntimeCore.CODER.BreakPoint.Parsers.BreakPointParseException;
import Composestar.RuntimeCore.CODER.BreakPoint.Parsers.BreakPointParser;
import Composestar.RuntimeCore.CODER.BreakPoint.Parsers.Ltl.BreakPointLTLParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

public class Maker extends JFrame implements ActionListener{
    private static PopUp popup;

	private TextArea ltlValueExpression = new TextArea();

	private JButton proceed = new JButton("Proceed");
	private JButton clear = new JButton ("Clear");

	private Maker()
	{
        popup = new PopUp();
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

 		JPanel top = new JPanel();
		top.setLayout(new BorderLayout());
        top.add(new Label("LTL Condition Formula"),BorderLayout.NORTH);
        top.add(ltlValueExpression,BorderLayout.CENTER);

		JPanel bottom = new JPanel();
		proceed.addActionListener(this);
		clear.addActionListener(this);
		bottom.add(proceed);
		bottom.add(clear);

		Container content = getContentPane();
		content.setLayout(new BorderLayout());
		content.add(top,BorderLayout.NORTH);
		content.add(bottom,BorderLayout.SOUTH);

		show();
	}

    public static BreakPoint getBreakPoint(){
        Maker maker = new Maker();
        while(maker._breakpoint == null){
            try{
                Thread.sleep(1);
            }catch(InterruptedException e){
            }
        }
        popup.dispose();
		maker.dispose();
        return maker._breakpoint;
    }

    private BreakPoint _breakpoint = null;

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
		ltlValueExpression.setText("");
	}

	private void proceed()
	{
		_breakpoint = createBreakPoint();
	}

	public BreakPoint createBreakPoint()
	{
		try
		{
    		BreakPointParser ltlParser = new BreakPointLTLParser();
			return ltlParser.parse(ltlValueExpression.getText());
		}
		catch(BreakPointParseException e)
		{
			parseException(e);
			return null;
		}
	}

	public void parseException(BreakPointParseException e)
	{
		popup.update("Parsing error", "Error while parsing LTL Expression\n" + e.getMessage());
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