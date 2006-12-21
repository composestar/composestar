package Composestar.Core.INCRE;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import Composestar.Core.RepositoryImplementation.DataStore;

/**
 * This class compares two files containing repository entities It stores the
 * differences and statistics in a html file
 */
public class INCRETester
{
	private FileRecord record1;

	private FileRecord record2;

	private BufferedWriter writer;

	private StringBuffer buffer;

	private static String filename1 = "Data1/history.dat";

	private static String filename2 = "Data2/history.dat";

	public INCRETester()
	{
		record1 = new FileRecord(filename1);
		record2 = new FileRecord(filename2);
		buffer = new StringBuffer("");
	}

	public void setOutputFile(String filename)
	{
		try
		{
			writer = new BufferedWriter(new FileWriter(filename));
		}
		catch (Exception e)
		{
			System.out.println("Output file creation failed!");
			System.exit(-1);
		}
	}

	public void importData()
	{
		record1.readData();
		record2.readData();
	}

	public void write()
	{
		buffer.append("<html><head><title>INCRE Report</title></head>");
		buffer.append("<body bgcolor=#EEEEEE><b>INCRETEST REPORT</b><br><b>Date: </b>").append(new Date().toString())
				.append("<br>");
		buffer
				.append("<br>Testing number of instances [per type]<hr><table cellspacing=0 cellpadding=2 width=100% border=0><tr>");
		buffer
				.append("<td width=50% align=center><b>Data1</b></td><td width=50% align=center><b>Data2</b></td></tr><tr>");
		createCell("[" + record1.instances.size() + "] Instances");
		createCell("[" + record2.instances.size() + "] Instances");

		/* Compare number of type */
		Iterator types1 = record1.instancesByType.keySet().iterator();
		while (types1.hasNext())
		{
			String type = (String) types1.next();
			int count1 = ((Integer) record1.instancesByType.get(type)).intValue();
			int count2 = 0;
			Object obj = record2.instancesByType.get(type);
			if (obj != null)
			{
				count2 = ((Integer) obj).intValue();
			}

			if (count1 != count2)
			{
				openTag("tr bgcolor=#AA8888");
			}
			else
			{
				openTag("tr");
			}

			createCell("[" + count1 + "] " + type.substring(type.lastIndexOf('.') + 1));
			createCell("[" + count2 + "] " + type.substring(type.lastIndexOf('.') + 1));
			closeTag("tr");
		}

		openTag("tr");
		createCell("<br><br>Testing objects by ID [keys with hashcodes excluded]<hr><br>", 2);
		closeTag("tr");

		/* Compare objects by ID */
		Iterator keysIt = record1.ds.keys();
		while (keysIt.hasNext())
		{
			String key = (String) keysIt.next();
			if (key.indexOf('_') == -1)
			{
				if (null == record2.ds.getObjectByID(key))
				{
					/* object with key cannot be found */
					openTag("tr align=left bgcolor=#AA8888");
					createCell(key, 2);
					closeTag("tr");
				}
				else
				{
					/* object found */
					openTag("tr align=left");
					createCell(key, 2);
					closeTag("tr");
				}
			}
		}

		openTag("tr");
		createCell("<br><br>Testing objects by fields<hr><br>", 2);
		closeTag("tr");

		/* Compare all fields */
		openTag("tr");
		createCell("TODO", 2);
		closeTag("tr");

		buffer.append("</tr></table></body></html>");
		try
		{ // write buffer to file
			writer.write(buffer.toString());
			writer.flush();
			writer.close();
		}
		catch (Exception e)
		{
			// ignore
		}

		System.exit(1);
	}

	public void createCell(String s)
	{
		buffer.append("<td width=50%>").append(s).append("</td>");
	}

	public void createCell(String s, int colspan)
	{
		buffer.append("<td width=50% colspan=").append(colspan).append('>').append(s).append("</td>");
	}

	public void openTag(String tag)
	{
		buffer.append('<').append(tag).append('>');
	}

	public void closeTag(String tag)
	{
		buffer.append("</").append(tag).append('>');
	}

	public static void main(String[] args)
	{
		INCRETester it = new INCRETester();
		if (args.length != 1)
		{
			System.out.println("Usage: java INCRETester *.html");
			System.exit(-1);
		}
		else
		{
			it.setOutputFile(args[0]);/* open output file */
			it.importData(); /* read history.dat files from /Data1 and /Data2 */
			it.write(); /* write result and close streams */
		}
	}

	/* inner class FileRecord for analyzing files */
	private static class FileRecord
	{
		private String filename;

		public DataStore ds;

		public Date compilationDate;

		/* Maps containing type information */
		public HashMap instancesByType = new HashMap();

		public HashMap instances = new HashMap();

		public FileRecord(String inFilename)
		{
			filename = inFilename;
			ds = new DataStore();
		}

		public void readData()
		{
			try
			{
				FileInputStream fis = new FileInputStream(filename);
				BufferedInputStream bis = new BufferedInputStream(fis);
				ObjectInputStream ois = new ObjectInputStream(bis);

				compilationDate = (Date) ois.readObject();
				System.out.println("Reading history created at [" + compilationDate.toString() + "]...");

				// read project configurations
				ds.addObject("config", ois.readObject());

				int numberofobjects = ois.readInt();
				for (int i = 0; i < numberofobjects; i++)
				{
					try
					{
						Object obj = ois.readObject();
						analyze(obj);
						ds.addObject(obj);
					}
					catch (EOFException ex)
					{
						System.out.println("[WARNING] End of file exception occurred");
					}
				}
				ois.close(); /* close objectinputstream */
			}
			catch (Exception e)
			{
				e.printStackTrace();
				System.exit(-1);
			}
		}

		public void analyze(Object obj)
		{

			// add object to instances
			instances.put("" + obj.hashCode(), obj);
			int count;

			// update instancesByType
			String type = obj.getClass().getName();
			if (instancesByType.containsKey(type))
			{
				count = ((Integer) instancesByType.get(type)).intValue();
				count++;
			}
			else
			{
				count = 1;
			}
			instancesByType.put(type, new Integer(count));
		}
	}
}
