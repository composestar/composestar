package Composestar.Core.COPPER.applet;

import javax.swing.text.*;

public class ColorEditorKit extends DefaultEditorKit implements ViewFactory
{
	public ColorEditorKit()
	{
		super();
	}

	public ViewFactory getViewFactory()
	{
		return this;
	}

	/* Create Color Views */
	public View create(Element e)
	{
		return new ColorView(e);
	}
}
