package Composestar.Core.COPPER.applet;

import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class ColorEditorKit extends DefaultEditorKit implements ViewFactory
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7637152959901746386L;

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
