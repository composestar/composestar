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

		setTitle(event.getLevel().toString());
		message.setMessage(event.getMessage());
		if (event.getThrowableInformation() != null)
		{
			StringBuffer sb = new StringBuffer();
			String[] traceMsg = event.getThrowableStrRep();
			for (int i = 0; i < traceMsg.length; i++)
			{
				sb.append(traceMsg[i]);
				sb.append("\n");
			}
			trace.setText(sb.toString());
		}
		else
		{
			traceScroll.setVisible(false);
		}
		setVisible(true);
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize()
	{
		this.setSize(400, 200);
		this.setContentPane(getContent());
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setModal(true);		
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
			tabs.setTabPlacement(JTabbedPane.TOP);
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
			traceScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			traceScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			traceScroll.setViewportView(getTrace());
		}
		return traceScroll;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
