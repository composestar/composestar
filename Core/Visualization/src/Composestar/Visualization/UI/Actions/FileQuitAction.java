/**
 * 
 */
package Composestar.Visualization.UI.Actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * @author Michiel Hendriks
 */
public class FileQuitAction extends AbstractAction
{
	private static final long serialVersionUID = 4850793215241141833L;

	protected JFrame frame;

	protected boolean confirmClose;

	public FileQuitAction(JFrame myFrame)
	{
		this(myFrame, false);
	}

	public FileQuitAction(JFrame myFrame, boolean confirm)
	{
		super("Close");
		frame = myFrame;
		confirmClose = confirm;
	}

	public void actionPerformed(ActionEvent arg0)
	{
		if (confirmClose)
		{
			if (JOptionPane.showConfirmDialog(frame, "Are you sure you want to close this window?") != JOptionPane.YES_OPTION)
			{
				return;
			}
		}
		frame.dispose();
	}

}
