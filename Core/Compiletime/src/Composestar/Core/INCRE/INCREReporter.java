package Composestar.Core.INCRE;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Logging.CPSLogger;

public class INCREReporter
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(INCRE.MODULE_NAME);

	private BufferedWriter writer, writer2;

	private StringBuffer buffer, buffer2;

	private String cssFile;

	// private String reportFile;
	private Map<String, List<INCRETimer>> timings;

	private long timeStart, timeEnd, totalElapsed;

	public INCREReporter(CommonResources resources)
	{
		// find INCRE stylesheet and create html report file
		// DataStore ds = DataStore.instance();
		// Properties resources = (Properties)ds.getObjectByID("config");

		// this.cssFile = resources.getProperty( "ComposestarPath", "ERROR" ) +
		// "INCRE.css";
		File cssFile1 = new File(resources.configuration().getProject().getIntermediate(), "INCRE.css");
		File cssFile2 = new File(resources.getPathResolver().getCore(), "INCRE.css");
		if (cssFile1.exists())
		{
			cssFile = resources.configuration().getProject().getBase() + "/INCRE.css";
		}
		else if (cssFile2.exists())
		{
			cssFile = resources.getPathResolver().getCore() + "/INCRE.css";
		}
		else
		{
			logger.warn("Could not find stylesheet for INCRE reporter: " + cssFile);
		}

		// String reportFile = resources.getProperty("TempFolder") +
		// "INCRE.html";
		if (!resources.configuration().getProject().getIntermediate().exists())
		{
			resources.configuration().getProject().getIntermediate().mkdirs();
		}
		String reportFile = resources.configuration().getProject().getIntermediate() + "/INCRE.html";
		String reportFile2 = resources.configuration().getProject().getIntermediate() + "/INCRE.txt";
		try
		{
			writer = new BufferedWriter(new FileWriter(reportFile));
			writer2 = new BufferedWriter(new FileWriter(reportFile2));
		}
		catch (Exception e)
		{
			logger.warn("INCRE report file creation failed (" + reportFile + ')');
		}

		buffer = new StringBuffer();
		buffer2 = new StringBuffer();
		timings = new HashMap<String, List<INCRETimer>>();
	}

	public void addTimer(INCRETimer timer)
	{
		String key = timer.getModule();
		List<INCRETimer> list = new ArrayList<INCRETimer>();

		if (timings.containsKey(key))
		{
			list = timings.get(key);
		}

		list.add(timer);
		timings.put(key, list);
	}

	public long getTotalForModule(String module, int type)
	{

		long total = 0;

		if (timings.containsKey(module))
		{
			List<INCRETimer> list = timings.get(module);
			for (INCRETimer timer : list)
			{
				if (timer.getType() == type)
				{
					total += timer.getElapsed();
				}
			}
		}

		return total;
	}

	public void open()
	{
		timeStart = System.currentTimeMillis();
		// DataStore ds = DataStore.instance();
		// Properties resources = (Properties)ds.getObjectByID("config");
		// String tempPath = resources.getProperty( "TempFolder", "ERROR" );

		buffer.append("<html><head><title>INCRE Report</title></head>");
		buffer.append("<link rel=stylesheet href=");
		buffer.append("\"file://").append(cssFile).append('\"');
		buffer.append(" type=\"text/css\">");
		buffer.append("<body bgcolor=#EEEEEE><b>INCRE REPORT</b><br><b>Date: </b>").append(new Date().toString())
				.append("<br><b>Project:</b> ").append(".").append(
						"<br><br><table width=90% border=0 cellspacing=0 cellpadding=2>");
		buffer.append("<tr><td class=maincell colspan=3></td></tr>");
	}

	public INCRETimer openProcess(String name, String description, int type)
	{
		int verifiedtype = type;

		if (type == INCRETimer.TYPE_INCREMENTAL)
		{
			INCRE incre = INCRE.instance();
			if (!incre.isModuleInc(name))
			{
				verifiedtype = INCRETimer.TYPE_NORMAL;
			}
		}

		INCRETimer timer = new INCRETimer(name, description, verifiedtype);
		timer.start();
		addTimer(timer);
		return timer;
	}

	public void close()
	{
		// total time elapsed
		timeEnd = System.currentTimeMillis();
		totalElapsed = timeEnd - timeStart;

		// append all timings
		for (String modulename : timings.keySet())
		{
			List<INCRETimer> moduletimings = timings.get(modulename);

			// append header
			buffer.append("<tr class=startmodulerow><td><b>");
			buffer.append("PROCESSES OF ").append(modulename);
			buffer.append("</b></td><td align=center><b>TYPE");
			buffer.append("</b></td><td align=center><b>ELAPSED");
			buffer.append("</b></td></tr>");

			buffer2.append(modulename);
			buffer2.append(":");

			// append timings of processes
			for (INCRETimer timer : moduletimings)
			{
				if (timer.getType() != INCRETimer.TYPE_ALL)
				{
					buffer.append("<tr class=white><td>");
					buffer.append(timer.getDescription());
					buffer.append("</td><td>");
					buffer.append(timer.strType());
					buffer.append("</td><td>");
					buffer.append(timer.getElapsed());
					buffer.append(" ms</td></tr>");
				}
			}

			// append summary of module
			long incremental = getTotalForModule(modulename, INCRETimer.TYPE_INCREMENTAL);
			long overhead = getTotalForModule(modulename, INCRETimer.TYPE_OVERHEAD);
			long elapsed = getTotalForModule(modulename, INCRETimer.TYPE_ALL);
			long normal = elapsed - incremental - overhead;

			buffer.append("<tr class=endmodulerow><td align=right>Total Overhead</td><td></td><td>");
			buffer.append("").append(overhead);
			buffer.append("	ms</td></tr>");
			buffer.append("<tr class=endmodulerow><td align=right>Total Normal</td><td></td><td>");
			buffer.append("").append(normal);
			buffer.append("	ms</td></tr>");
			buffer.append("<tr class=endmodulerow><td align=right>Total Incremental</td><td></td><td>");
			buffer.append("").append(incremental);
			buffer.append("	ms</td></tr>");
			buffer.append("<tr class=endmodulerow><td align=right>Total Elapsed</td><td></td><td>");
			buffer.append("").append(elapsed);
			buffer2.append(elapsed);
			buffer2.append('\n');
			buffer.append("	ms (");
			double percentage = 0.0;
			if (totalElapsed > 0)
			{
				percentage = (elapsed * 100d / totalElapsed);
			}
			BigDecimal percDec = new BigDecimal(percentage);
			percDec = percDec.setScale(1, BigDecimal.ROUND_HALF_UP);
			buffer.append(percDec.toString());
			buffer.append("%)</td></tr>");
		}

		// append end of report
		buffer.append("</table></body></html>");
		buffer2.append(totalElapsed);

		try
		{
			// write buffer to file
			writer.write(buffer.toString());
			writer.flush();
			writer.close();
			writer2.write(buffer2.toString());
			writer2.flush();
			writer2.close();
		}
		catch (Exception e)
		{
			// ignore
		}
	}

}
