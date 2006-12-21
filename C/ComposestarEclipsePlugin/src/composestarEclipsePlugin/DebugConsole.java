package composestarEclipsePlugin;

import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class DebugConsole extends MessageConsole
{

	private MessageConsoleStream inMessageStream;

	public DebugConsole(String title)
	{
		super(title, null);

		// Redirect the output
		inMessageStream = newMessageStream();
		// System.setOut(new PrintStream(inMessageStream));
	}
}
