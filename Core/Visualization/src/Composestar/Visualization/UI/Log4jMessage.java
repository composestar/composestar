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

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Message Dialog for a Log4J message
 * 
 * @author Michiel Hendriks
 */
public class Log4jMessage extends JDialog
{
	private static final long serialVersionUID = 108194530699529795L;

	private JPanel content;

	private JOptionPane message;

	private JTabbedPane tabs;

	private JTextPane trace;

	private JScrollPane traceScroll;

	/**
	 * @param owner
	 */
	public Log4jMessage(Frame owner, LoggingEvent event)
	{
		super(owner);
		initialize();

		if (event.getLevel().isGreaterOrEqual(Level.ERROR))
		{
			message.setMessageType(JOptionPane.ERROR_MESSAGE);
		}
		else if (event.getLevel().isGreaterOrEqual(Level.WARN))
		{
			message.setMessageType(JOptionPane.WARNING_MESSAGE);
		}
		else
		{
			message.setMessageType(JOptionPane.INFORMATION_MESSAGE);
		}

		setTitle(captialize(event.getLevel().toString()));
		message.setMessage(wordWrap(event.getMessage().toString(), 50));
		if (event.getThrowableInformation() != null)
		{
			StringBuffer sb = new StringBuffer();
			String[] traceMsg = event.getThrowableStrRep();
			for (String element : traceMsg)
			{
				sb.append(element);
				sb.append("\n");
			}
			trace.setText(sb.toString());
		}
		else
		{
			tabs.remove(1);
		}
		setVisible(true);
	}

	public static String captialize(String input)
	{
		return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
	}

	/**
	 * Crappy wordwrap implementation
	 * 
	 * @param input
	 * @param chars
	 * @return
	 */
	public static String wordWrap(String input, int chars)
	{
		StringBuffer res = new StringBuffer(input.length() + (input.length() / chars));
		int idx = 0;
		String[] data = input.split(" ");
		for (String s : data)
		{
			idx += s.length();
			if (idx >= chars)
			{
				res.append("\n");
				idx = 0;
			}
			res.append(s);
			res.append(" ");
		}
		return res.toString();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
	{
		this.setSize(400, 200);
		setContentPane(getContent());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setModal(true);
	}

	/**
	 * This method initializes content
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getContent()
	{
		if (content == null)
		{
			content = new JPanel();
			content.setLayout(new BorderLayout());
			content.add(getTabs(), BorderLayout.CENTER);
		}
		return content;
	}

	/**
	 * This method initializes message
	 * 
	 * @return javax.swing.JOptionPane
	 */
	private JOptionPane getMessage()
	{
		if (message == null)
		{
			message = new JOptionPane();
			message.setMessageType(JOptionPane.ERROR_MESSAGE);
			message.addPropertyChangeListener(new java.beans.PropertyChangeListener()
			{
				public void propertyChange(java.beans.PropertyChangeEvent e)
				{
					if (e.getPropertyName().equals("value"))
					{
						dispose();
					}
				}
			});
		}
		return message;
	}

	/**
	 * This method initializes tabs
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	private JTabbedPane getTabs()
	{
		if (tabs == null)
		{
			tabs = new JTabbedPane();
			tabs.setTabPlacement(SwingConstants.TOP);
			tabs.addTab("Message", null, getMessage(), null);
			tabs.addTab("Trace", null, getTraceScroll(), null);
		}
		return tabs;
	}

	/**
	 * This method initializes trace
	 * 
	 * @return javax.swing.JTextPane
	 */
	private JTextPane getTrace()
	{
		if (trace == null)
		{
			trace = new JTextPane();
			trace.setEditable(false);
			trace.setFont(new Font("Monospaced", Font.PLAIN, 12));
			trace.setVisible(true);
		}
		return trace;
	}

	/**
	 * This method initializes traceScroll
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getTraceScroll()
	{
		if (traceScroll == null)
		{
			traceScroll = new JScrollPane();
			traceScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			traceScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			traceScroll.setViewportView(getTrace());
		}
		return traceScroll;
	}

} // @jve:decl-index=0:visual-constraint="10,10"
