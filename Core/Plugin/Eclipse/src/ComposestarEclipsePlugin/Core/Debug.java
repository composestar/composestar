package ComposestarEclipsePlugin.Core;

import java.io.PrintStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 *  The Debug class is a singleton class which provides access to the debugging functions of the Composestar plugin.
 *  Usage:
 *  Debug.instance().Log(String message,int DEBUG_MODE);
 *  Debug.instance().Log(String message) -> default information mode
 *   
 *  Debug Modes:
 *  MSG_INFORMATION: to print out information
 *  MSG_WARNING: to print out a warning
 *  MSG_ERROR: to print out an error 
 */
public class Debug implements IComposestarConstants {

	private static Debug Instance = null;
	private MessageConsoleStream stream;
	private MessageConsole myConsole;
	private boolean enabled = true;
		
	public Debug()
	{
		myConsole = new MessageConsole(CONSOLE_TITLE, null);
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { myConsole });
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(myConsole);
		stream = myConsole.newMessageStream();
		stream.setActivateOnWrite(true);
		System.setOut(new PrintStream(stream));
	}
	
	public static Debug instance()
	{
		if (Instance == null) 
		{
			Instance = new Debug();
		}
		return (Instance);
	} 
	
	public void clear()
	{
		myConsole.clearConsole();
	}
	
	public void Log(String msg)
	{
		if(enabled)
		this.PrintMessage(msg, MSG_INFORMATION);   
	}
	
	public void Log(String msg, int msgKind)
	{
		if(enabled)
		this.PrintMessage(msg, msgKind);   
	}
	
	private void PrintMessage(final String msg, final int msgKind)
	{
		//Print the message in the UI Thread in async mode
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
					int swtColorId = SWT.COLOR_BLACK;
		
					switch (msgKind)
					{
					case MSG_INFORMATION:
						swtColorId = SWT.COLOR_BLACK;
						break;
					case MSG_ERROR:
						swtColorId = SWT.COLOR_RED;
						break;
					case MSG_WARNING:
						swtColorId = SWT.COLOR_YELLOW;
						break;
					default:				
					}
					stream = myConsole.newMessageStream();
					stream.setColor(Display.getCurrent().getSystemColor(swtColorId));
					stream.println(msg);
			}
		});
	}

	public void setEnabled(boolean b)
	{
		this.enabled = b;
	}
}
