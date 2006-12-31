package composestarEclipsePlugin.C;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * The Debug class is a singleton class which provides access to the debugging
 * functions of the Composestar plugin. Usage: Debug.instance().Log(String
 * message,int DEBUG_MODE); Debug.instance().Log(String message) -> default
 * information mode Debug Modes: MSG_INFORMATION: to print out information
 * MSG_WARNING: to print out a warning MSG_ERROR: to print out an error
 */
public class Debug
{

	private static Debug Instance = null;

	private String fTitle = "Compose*";

	private DebugConsole debugConsole = null;

	private boolean DebugMode = true;

	// private BufferedWriter bw=null;
	public static final int MSG_INFORMATION = 1;

	public static final int MSG_ERROR = 2;

	public static final int MSG_WARNING = 3;

	public Debug()
	{
	// try{
	// bw = new BufferedWriter(new FileWriter("c:\\test.txt"));
	// }
	// catch(Exception e){}

	}

	public static Debug instance()
	{
		if (Instance == null)
		{
			Instance = new Debug();
		}
		return (Instance);
	}

	public void DebugModeOff()
	{
		this.DebugMode = false;
	}

	public void Log(String msg)
	{
		this.PrintMessage(msg, MSG_INFORMATION);
	}

	public void Log(String msg, int msgKind)
	{
		this.PrintMessage(msg, msgKind);
	}

	private void PrintMessage(String msg, int msgKind)
	{
		if (DebugMode)
		{
			/*
			 * if console-view in Java-perspective is not active, then show it
			 * and then display the message in the console attached to it
			 */
			if (!displayConsoleView())
			{
				/*
				 * If an exception occurs while displaying in the console, then
				 * just diplay atleast the same in a message-box
				 */
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Error", msg);
				return;
			}
			/* display message on console */
			getNewMessageConsoleStream(msgKind).println(msg);
		}
		else
		{
			System.out.println(msg);
			// try{
			// bw.write(msg + "\r\n");
			// bw.flush();
			// }
			// catch(Exception e ){}
		}
	}

	private boolean displayConsoleView()
	{
		try
		{
			IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (activeWorkbenchWindow != null)
			{
				IWorkbenchPage activePage = activeWorkbenchWindow.getActivePage();
				if (activePage != null)
				{
					activePage.showView(IConsoleConstants.ID_CONSOLE_VIEW, null,
							IWorkbenchPage.VIEW_VISIBLE);
				}
			}

		}
		catch (PartInitException partEx)
		{
			return false;
		}

		return true;
	}

	private MessageConsoleStream getNewMessageConsoleStream(int msgKind)
	{
		int swtColorId = SWT.COLOR_DARK_GREEN;

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
		MessageConsoleStream msgConsoleStream = getMessageConsole().newMessageStream();
		msgConsoleStream.setColor(Display.getCurrent().getSystemColor(swtColorId));
		return msgConsoleStream;
	}

	private MessageConsole getMessageConsole()
	{

		if (debugConsole == null)
		{
			createMessageConsoleStream(fTitle);
		}
		return debugConsole;
	}

	private void createMessageConsoleStream(String title)
	{
		debugConsole = new DebugConsole(title);
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[] { debugConsole });
	}
}
