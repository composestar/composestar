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
 * $Id: RegexPattern.java 3936 2007-11-19 12:43:44Z elmuerte $
 */

package Composestar.Utils.Regex;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.SystemColor;
import java.awt.TextArea;
import java.awt.TextField;
import java.util.regex.Pattern;

/**
 * A little GUI to help test the regular expressions as used by compose*'s fire
 * 
 * @author Michiel Hendriks
 */
public class Tool extends Frame
{

	private static final long serialVersionUID = 1L;

	private TextField txtRegularExpression = null;

	private TextArea txtTestInput = null;

	private Button btnTest = null;

	private TextArea txtResults = null;

	private Button btnQuit = null;

	private Label lblExpression = null;

	private Label lblTestInput = null;

	private Label lblResults = null;

	private Tool regexTester;

	/**
	 * This is the default constructor
	 */
	public Tool()
	{
		super();
		regexTester = this;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
	{
		GridBagConstraints gridBagConstraints7 = new GridBagConstraints();
		gridBagConstraints7.gridx = 0;
		gridBagConstraints7.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints7.gridy = 3;
		lblResults = new Label();
		lblResults.setText("Results");
		GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
		gridBagConstraints6.gridx = 0;
		gridBagConstraints6.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints6.gridy = 2;
		lblTestInput = new Label();
		lblTestInput.setText("Test input");
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		lblExpression = new Label();
		lblExpression.setText("Expression");
		GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
		gridBagConstraints5.gridx = 2;
		gridBagConstraints5.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints5.anchor = GridBagConstraints.SOUTH;
		gridBagConstraints5.gridy = 3;
		GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
		gridBagConstraints4.fill = GridBagConstraints.BOTH;
		gridBagConstraints4.gridy = 3;
		gridBagConstraints4.weightx = 1.0;
		gridBagConstraints4.weighty = 1.0;
		gridBagConstraints4.gridx = 1;
		GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
		gridBagConstraints3.gridx = 2;
		gridBagConstraints3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints3.anchor = GridBagConstraints.NORTH;
		gridBagConstraints3.gridy = 1;
		GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
		gridBagConstraints2.fill = GridBagConstraints.BOTH;
		gridBagConstraints2.gridy = 2;
		gridBagConstraints2.weightx = 1.0;
		gridBagConstraints2.weighty = 1.0;
		gridBagConstraints2.gridx = 1;
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints1.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints1.gridy = 1;
		this.setLayout(new GridBagLayout());
		this.setSize(467, 309);
		this.setBackground(SystemColor.control);
		this.setTitle("Compose* Regular Expression Tester");
		this.add(getTxtRegularExpression(), gridBagConstraints1);
		this.add(getTxtTestInput(), gridBagConstraints2);
		this.add(getBtnTest(), gridBagConstraints3);
		this.add(getTxtResults(), gridBagConstraints4);
		this.add(getBtnQuit(), gridBagConstraints5);
		this.add(lblExpression, gridBagConstraints);
		this.add(lblTestInput, gridBagConstraints6);
		this.add(lblResults, gridBagConstraints7);
		this.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				regexTester.setVisible(false);
				regexTester.dispose();
			}
		});
		this.setVisible(true);
	}

	/**
	 * This method initializes txtRegularExpression
	 * 
	 * @return java.awt.TextField
	 */
	private TextField getTxtRegularExpression()
	{
		if (txtRegularExpression == null)
		{
			txtRegularExpression = new TextField();
			txtRegularExpression.setBackground(SystemColor.window);
		}
		return txtRegularExpression;
	}

	/**
	 * This method initializes txtTestInput
	 * 
	 * @return java.awt.TextArea
	 */
	private TextArea getTxtTestInput()
	{
		if (txtTestInput == null)
		{
			txtTestInput = new TextArea();
			txtTestInput.setBackground(SystemColor.window);
		}
		return txtTestInput;
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
			btnTest.setLabel("Test");
			btnTest.setBackground(SystemColor.control);
			btnTest.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					regexTester.testRegex();
				}
			});
		}
		return btnTest;
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
			txtResults.setBackground(SystemColor.window);
			txtResults.setEditable(false);
		}
		return txtResults;
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
			btnQuit.setLabel("Exit");
			btnQuit.setBackground(SystemColor.control);
			btnQuit.addActionListener(new java.awt.event.ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					regexTester.setVisible(false);
					regexTester.dispose();
				}
			});
		}
		return btnQuit;
	}

	protected void testRegex()
	{
		txtResults.setText("");
		RegexPattern patt;
		Pattern repatt;
		try
		{
			patt = RegexPattern.compile(txtRegularExpression.getText());
			repatt = Pattern.compile(txtRegularExpression.getText());
		}
		catch (PatternParseException e)
		{
			txtResults.setText(e.getMessage());
			return;
		}
		String[] test = txtTestInput.getText().split("\\n");
		StringBuffer sb = new StringBuffer();
		for (String t : test)
		{
			t = t.trim();
			sb.append(t);
			sb.append(" -> ");
			boolean r1 = patt.matches(new StringListBuffer(t));
			if (r1)
			{
				sb.append("MATCHES");
			}
			else
			{
				sb.append("does not match");
			}
			if (r1 != repatt.matcher(t.replaceAll("\\s", "")).matches())
			{
				sb.append(" (inconsistent; bug?)");
			}
			sb.append("\n");
		}
		txtResults.setText(sb.toString());
	}

	public static void main(String[] args)
	{
		new Tool();
	}

} // @jve:decl-index=0:visual-constraint="6,0"
