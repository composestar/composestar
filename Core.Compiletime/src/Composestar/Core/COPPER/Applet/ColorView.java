package Composestar.Core.COPPER.Applet;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;

public class ColorView extends PlainView
{
	Segment line;

	public static Hashtable keywords;

	public ColorView(Element e)
	{
		super(e);
		line = new Segment();
	}

	/*
	 * This is the heart of the program %%% IMPORTANT %%% this is an example to
	 * illustrate the text architecture of Swing. do consider using a better
	 * algorithm for faster and more efficient program
	 */
	protected void drawLine(int lineIndex, Graphics g, int x, int y)
	{
		try
		{
			Element lineElement = getElement().getElement(lineIndex);
			int start = lineElement.getStartOffset();
			int end = lineElement.getEndOffset();

			// this puts the entire line we are typing in into the segment
			getDocument().getText(start, end - (start + 1), line);

			// break the line into words using StringTokenizer
			// the last parameter to ST is important, because we need the spaces
			// as well
			StringTokenizer str = new StringTokenizer(line.toString(), " ", true);
			int offset = 0;
			while (str.hasMoreTokens())
			{
				String t = str.nextToken();
				Color c;
				if ((lineIndex + 1) == ComposeStarGrammarApplet.getErrorLine())
				{
					// System.out.println("Drawing line: "+(lineIndex+1)+" ==
					// "+ComposeStarGrammarApplet.getErrorLine());
					c = Color.red;
				}
				else
				{
					c = (Color) keywords.get(t);
				}
				if (c == null)
				{
					c = Color.black;
				}

				g.setColor(c);
				line.count = t.length();

				// Utilities is a standard swing class
				x = Utilities.drawTabbedText(line, x, y, g, this, offset);
				line.offset += t.length();
				offset += t.length();
			}
		}
		catch (javax.swing.text.BadLocationException ble)
		{
			// should never happen
			ble.printStackTrace();
		}
	}
}
