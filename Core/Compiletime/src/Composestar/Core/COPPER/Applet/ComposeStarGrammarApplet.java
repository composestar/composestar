package Composestar.Core.COPPER.Applet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import Composestar.Core.COPPER.CpsParser;
import Composestar.Core.COPPER.CpsPosLexer;
import antlr.ANTLRException;

public class ComposeStarGrammarApplet extends JApplet implements ActionListener, KeyListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5255074200151125654L;

	protected JButton button;

	protected JEditorPane textpane;

	protected JTextArea msgpane;

	protected String src = "";

	protected static int errline = -1; // michielh: why was this declared

	// static?

	protected static final double RESIZEWEIGHT = 0.75;

	public void init()
	{
		this.showStatus("Compose* grammar applet is active...");

		this.getContentPane().setLayout(new BorderLayout());

		this.initColors();

		textpane = new JEditorPane("text", "Please insert text!");
		textpane.setEditorKit(new ColorEditorKit());
		textpane.addKeyListener(this);

		msgpane = new JTextArea("Compiler messages...");
		msgpane.setEditable(false);

		JSplitPane spane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true, new JScrollPane(textpane), new JScrollPane(
				msgpane));
		spane.setContinuousLayout(true);
		spane.setOneTouchExpandable(true);
		spane.setDividerLocation((int) (this.getHeight() / 1.3));
		spane.setResizeWeight(RESIZEWEIGHT);

		this.getContentPane().add(spane, BorderLayout.CENTER);
		button = new JButton("Parse...");
		button.addActionListener(this);

		this.getContentPane().add(button, BorderLayout.SOUTH);

		this.getRootPane().setDefaultButton(button);
	}

	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource().equals(button))
		{
			src = textpane.getText();
			setErrorLine(-1);
			msgpane.setText("Invoking parser...\n");
			if (src.trim().length() == 0)
			{
				msgpane.append("Please insert text!");
			}
			else
			{
				try
				{
					StringReader sr = new StringReader(src);
					CpsPosLexer lexer = new CpsPosLexer(sr);
					CpsParser parser = new CpsParser(lexer);
					parser.concern();

					msgpane.append("Parsing successful!\n");
				}
				catch (ANTLRException exp)
				{
					String tmp = exp.toString();
					msgpane.append("Syntax error occurred: " + tmp + '\n');
					if (tmp.indexOf("line") >= 0)
					{
						int startline = tmp.indexOf("line ") + 5;
						int endline = tmp.indexOf(':');
						String line = tmp.substring(startline, endline);
						try
						{
							setErrorLine(Integer.parseInt(line));
						}
						catch (Exception ex)
						{
							setErrorLine(-1);
						}
						this.showStatus("Syntax error occurred on line: " + line);
						this.textpane.repaint();
						System.out.println("Line: " + errline + "");
					}
				}
			}
		}
	}

	public void keyTyped(KeyEvent e)
	{
		setErrorLine(-1);
	}

	public void keyPressed(KeyEvent e)
	{}

	public void keyReleased(KeyEvent e)
	{
		String txt = "" + e.getKeyChar();
		// System.out.println("Text: "+txt);
		if (txt.equals("\t"))
		{
			System.out.println("Found tab!: " + e.getKeyLocation());
			String output = "";
			StringTokenizer st = new StringTokenizer(this.textpane.getText(), "\t", false);
			while (st.hasMoreTokens())
			{
				output += st.nextToken() + "    ";
			}
			// System.out.println("Output: "+output);
			this.textpane.setText(output);
		}
	}

	protected void initColors()
	{
		Hashtable keys = new Hashtable();
		Color color = Color.blue;
		keys.put("concern", color);
		keys.put("filtermodule", color);
		keys.put("inputfilters", color);
		keys.put("outputfilters", color);
		keys.put("internals", color);
		keys.put("externals", color);
		keys.put("conditions", color);
		keys.put("superimposition", color);
		keys.put("selectors", color);
		keys.put("methods", color);
		keys.put("filtermodules", color);
		keys.put("implementation", color);
		keys.put("by", color);
		keys.put("as", color);
		keys.put("in", color);
		keys.put("true", color);
		keys.put("false", color);
		keys.put("begin", color);
		keys.put("end", color);
		keys.put("dispatch", color);
		keys.put("error", color);
		keys.put("meta", color);
		keys.put("send", color);
		keys.put("realtime", color);
		keys.put("inner", color);
		keys.put("self", color);
		keys.put("wait", color);
		ColorView.keywords = keys;
	}

	public static void setErrorLine(int inval)
	{
		errline = inval;
	}

	public static int getErrorLine()
	{
		return errline;
	}
}
