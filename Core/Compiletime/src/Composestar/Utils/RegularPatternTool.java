/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */
package Composestar.Utils;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Label;
import java.awt.SystemColor;
import java.awt.TextArea;
import java.awt.TextField;

import Composestar.Core.FIRE2.util.regex.RegularPattern;
import Composestar.Core.FIRE2.util.regex.SimpleMatcher;

/**
 * @author Michiel Hendriks
 */
public class RegularPatternTool extends Frame
{

	private static final long serialVersionUID = 1L;

	private Label lblPattern = null;

	private TextField edPattern = null;

	private Label lblTestInput = null;

	private TextArea txtInput = null;

	private Label lblResults = null;

	private TextArea txtResults = null;

	private Button btnTest = null;

	private Button btnQuit = null;

	private Button btnNFA = null;

	/**
	 * @throws HeadlessException
	 */
	public RegularPatternTool() throws HeadlessException
	{
		super();
		initialize();
	}

	/**
	 * @param arg0
	 */
	public RegularPatternTool(GraphicsConfiguration arg0)
	{
		super(arg0);
		initialize();
	}

	/**
	 * @param arg0
	 * @throws HeadlessException
	 */
	public RegularPatternTool(String arg0) throws HeadlessException
	{
		super(arg0);
		initialize();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public RegularPatternTool(String arg0, GraphicsConfiguration arg1)
	{
		super(arg0, arg1);
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
	{
		GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
		gridBagConstraints8.gridx = 2;
		gridBagConstraints8.anchor = GridBagConstraints.NORTH;
		gridBagConstraints8.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints8.gridy = 1;
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		gridBagConstraints7.gridx = 2;
		gridBagConstraints7.anchor = GridBagConstraints.SOUTH;
		gridBagConstraints7.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints7.gridy = 2;
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.gridx = 2;
		gridBagConstraints6.anchor = GridBagConstraints.NORTH;
		gridBagConstraints6.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints6.gridy = 0;
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.fill = GridBagConstraints.BOTH;
		gridBagConstraints5.gridy = 2;
		gridBagConstraints5.weightx = 1.0;
		gridBagConstraints5.weighty = 1.0;
		gridBagConstraints5.gridx = 1;
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.gridx = 0;
		gridBagConstraints4.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints4.gridy = 2;
		lblResults = new Label();
		lblResults.setText("Results");
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.fill = GridBagConstraints.BOTH;
		gridBagConstraints3.gridy = 1;
		gridBagConstraints3.weightx = 1.0;
		gridBagConstraints3.weighty = 1.0;
		gridBagConstraints3.gridx = 1;
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.gridx = 0;
		gridBagConstraints2.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints2.gridy = 1;
		lblTestInput = new Label();
		lblTestInput.setText("Test input");
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.fill = GridBagConstraints.BOTH;
		gridBagConstraints1.gridy = 0;
		gridBagConstraints1.weightx = 1.0;
		gridBagConstraints1.gridx = 1;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.gridy = 0;
		lblPattern = new Label();
		lblPattern.setText("Pattern");
		this.setLayout(new GridBagLayout());
		this.setSize(639, 311);
		this.setForeground(SystemColor.windowText);
		this.setBackground(SystemColor.control);
		this.setTitle("Compose* Regular Pattern Tool");

		this.add(lblPattern, gridBagConstraints);
		this.add(getEdPattern(), gridBagConstraints1);
		this.add(lblTestInput, gridBagConstraints2);
		this.add(getTxtInput(), gridBagConstraints3);
		this.add(lblResults, gridBagConstraints4);
		this.add(getTxtResults(), gridBagConstraints5);
		this.add(getBtnTest(), gridBagConstraints6);
		this.add(getBtnQuit(), gridBagConstraints7);
		this.add(getBtnNFA(), gridBagConstraints8);
		this.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				e.getWindow().dispose();
			}
		});
	}

	/**
	 * This method initializes edPattern
	 * 
	 * @return java.awt.TextField
	 */
	private TextField getEdPattern()
	{
		if (edPattern == null)
		{
			edPattern = new TextField();
			edPattern.setBackground(SystemColor.text);
		}
		return edPattern;
	}

	/**
	 * This method initializes txtInput
	 * 
	 * @return java.awt.TextArea
	 */
	private TextArea getTxtInput()
	{
		if (txtInput == null)
		{
			txtInput = new TextArea();
			txtInput.setBackground(SystemColor.text);
		}
		return txtInput;
	}

	/**
	 * This method initializes txtResults
	 * 
	 * @return java.awt.TextArea
	 */
	private TextArea getTxtResults()
	{
		if (txtResults == null)
		{
			txtResults = new TextArea();
			txtResults.setBackground(SystemColor.text);
			txtResults.setEditable(false);
		}
		return txtResults;
	}

	/**
	 * This method initializes btnTest
	 * 
	 * @return java.awt.Button
	 */
	private Button getBtnTest()
	{
		if (btnTest == null)
		{
			btnTest = new Button();
			btnTest.setLabel("test");
			btnTest.setBackground(SystemColor.control);
			btnTest.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					testPattern();
				}
			});
		}
		return btnTest;
	}

	/**
	 * This method initializes btnQuit
	 * 
	 * @return java.awt.Button
	 */
	private Button getBtnQuit()
	{
		if (btnQuit == null)
		{
			btnQuit = new Button();
			btnQuit.setLabel("exit");
			btnQuit.setBackground(SystemColor.control);
			btnQuit.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					dispose();
				}
			});
		}
		return btnQuit;
	}

	/**
	 * This method initializes btnNFA
	 * 
	 * @return java.awt.Button
	 */
	private Button getBtnNFA()
	{
		if (btnNFA == null)
		{
			btnNFA = new Button();
			btnNFA.setLabel("show NFA");
			btnNFA.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					printPattern();
				}
			});
		}
		return btnNFA;
	}

	protected void testPattern()
	{
		txtResults.setText("");
		RegularPattern patt;
		try
		{
			patt = RegularPattern.compile(edPattern.getText());
		}
		catch (Exception e)
		{
			lblPattern.setText(e.toString());
			return;
		}
		String[] test = txtInput.getText().split("\\n");
		StringBuffer sb = new StringBuffer();
		for (String t : test)
		{
			sb.append(t);
			sb.append(" -> ");
			if (SimpleMatcher.matches(patt, t))
			{
				sb.append("MATCHES");
			}
			else
			{
				sb.append("does not match");
			}
			sb.append("\n");
		}
		txtResults.setText(sb.toString());
	}

	protected void printPattern()
	{
		txtResults.setText("");
		RegularPattern patt;
		try
		{
			patt = RegularPattern.compile(edPattern.getText());
		}
		catch (Exception e)
		{
			lblPattern.setText(e.toString());
			return;
		}
		StringBuffer sb = new StringBuffer();
		sb.append("Start state: ");
		sb.append(patt.getStartState().getStateId());
		sb.append("\n\n");
		sb.append(patt.getStartState().toString());
		txtResults.setText(sb.toString());
	}

	public static void main(String[] args)
	{
		RegularPatternTool tool = new RegularPatternTool();
		if (args.length > 1)
		{
			tool.getEdPattern().setText(args[0]);
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 1; i < args.length; i++)
		{
			if (i > 1)
			{
				sb.append("\n");
			}
			sb.append(args[i]);
		}
		tool.getTxtInput().setText(sb.toString());
		tool.setVisible(true);
	}

} // @jve:decl-index=0:visual-constraint="10,10"
