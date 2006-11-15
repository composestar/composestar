package Composestar.Core.INCRE;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.math.BigDecimal;
import java.util.*;

import Composestar.Utils.*;
import Composestar.Core.Master.Config.Configuration;

public class INCREReporter 
{
	private BufferedWriter writer, writer2;
	private StringBuffer buffer, buffer2;
	private String cssFile;
	//private String reportFile;
	private LinkedHashMap timings;
	private long timeStart, timeEnd, totalElapsed;
	 
	public INCREReporter() 
	{
		// find INCRE stylesheet and create html report file
		//DataStore ds = DataStore.instance();
		//Properties resources = (Properties)ds.getObjectByID("config");
		Configuration config = Configuration.instance();
		
		//this.cssFile = resources.getProperty( "ComposestarPath", "ERROR" ) + "INCRE.css";
		File cssFile1 = new File(config.getPathSettings().getPath("Base")+"INCRE.css");
		File cssFile2 = new File(config.getPathSettings().getPath("Composestar")+"INCRE.css");
		if (cssFile1.exists()) {
			this.cssFile = config.getPathSettings().getPath("Base")+"INCRE.css";
		}
		else if (cssFile2.exists()) {
			this.cssFile = config.getPathSettings().getPath("Composestar")+"INCRE.css";
		}
		else
		{
			Debug.out(Debug.MODE_WARNING, "INCRE", "Could not find stylesheet for INCRE reporter: "+cssFile);
		}
		
		//String reportFile = resources.getProperty("TempFolder") + "INCRE.html";
		String reportFile = config.getPathSettings().getPath("Base") + "INCRE.html";
		String reportFile2 = config.getPathSettings().getPath("Base") + "INCRE.txt";
		try 
		{
			writer = new BufferedWriter(new FileWriter(reportFile));
			writer2 = new BufferedWriter(new FileWriter(reportFile2));
		} 
		catch(Exception e)
		{
			Debug.out(Debug.MODE_WARNING, "INCRE", "INCRE report file creation failed (" + reportFile + ')');
		}

		buffer = new StringBuffer();
		buffer2 = new StringBuffer();
		timings = new LinkedHashMap();
	}
	
	public void addTimer(INCRETimer timer){
		String key = timer.getModule();
		ArrayList list = new ArrayList();
		
		if(timings.containsKey(key)){
			list = (ArrayList)timings.get(key);			
		}
		
		list.add(timer);
		timings.put(key,list);
	}

	public long getTotalForModule(String module,int type){
		
		long total = 0; 
		
		if(this.timings.containsKey(module)){
			ArrayList list = (ArrayList)timings.get(module);
			Iterator timerItr = list.iterator();
			while(timerItr.hasNext()){
				INCRETimer timer = (INCRETimer)timerItr.next();
				if(timer.getType()== type)
					total += timer.getElapsed();
			}
		}
		
		return total;
	}
	
	public void open()
	{
		timeStart = System.currentTimeMillis();
		//DataStore ds = DataStore.instance();
		//Properties resources = (Properties)ds.getObjectByID("config");
		//String tempPath = resources.getProperty( "TempFolder", "ERROR" );
		Configuration config = Configuration.instance();
		String tempPath = config.getPathSettings().getPath("Base");
		
		buffer.append("<html><head><title>INCRE Report</title></head>");
		buffer.append("<link rel=stylesheet href=");
        buffer.append("\"file://").append(this.cssFile).append('\"');
		buffer.append(" type=\"text/css\">");
        buffer.append("<body bgcolor=#EEEEEE><b>INCRE REPORT</b><br><b>Date: </b>").append(new Date().toString()).append("<br><b>Project:</b> ").append(tempPath).append("<br><br><table width=90% border=0 cellspacing=0 cellpadding=2>");
		buffer.append("<tr><td class=maincell colspan=3></td></tr>");
	}
	
	public INCRETimer openProcess(String name, String description, int type)
	{
		int verifiedtype = type;
		
		if(type == INCRETimer.TYPE_INCREMENTAL){
			INCRE incre = INCRE.instance();
			if(!incre.isModuleInc(name))
				verifiedtype = INCRETimer.TYPE_NORMAL;		
		}
			
		INCRETimer timer = new INCRETimer(name,description,verifiedtype);
		timer.start();
		this.addTimer(timer);
		return timer;
	}

	public void close()
	{
		// total time elapsed
		timeEnd = System.currentTimeMillis();
		totalElapsed = timeEnd - timeStart;
		
		// append all timings
		Iterator modules = timings.keySet().iterator();
		while(modules.hasNext()){
			String modulename = (String)modules.next();
			ArrayList moduletimings = (ArrayList)timings.get(modulename);
			
			// append header
			buffer.append("<tr class=startmodulerow><td><b>");
            buffer.append("PROCESSES OF ").append(modulename);
			buffer.append("</b></td><td align=center><b>TYPE");
			buffer.append("</b></td><td align=center><b>ELAPSED");
			buffer.append("</b></td></tr>");
			
			buffer2.append(modulename);
			buffer2.append(":");
			
			// append timings of processes
			Iterator timerItr = moduletimings.iterator();
			while(timerItr.hasNext()){
				INCRETimer timer = (INCRETimer)timerItr.next();
				
				if(timer.getType()!=INCRETimer.TYPE_ALL){
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
			long incremental = this.getTotalForModule(modulename,INCRETimer.TYPE_INCREMENTAL);
			long overhead = this.getTotalForModule(modulename,INCRETimer.TYPE_OVERHEAD);
			long elapsed = this.getTotalForModule(modulename,INCRETimer.TYPE_ALL);
			long normal = elapsed-incremental-overhead;
						
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
			double percentage = ((double)elapsed * 100d / (double) totalElapsed);
			BigDecimal percDec = new BigDecimal(percentage);
			percDec = percDec.setScale(1, BigDecimal.ROUND_HALF_UP);
			buffer.append(percDec.toString());
			buffer.append("%)</td></tr>");
		}
		
		// append end of report
		buffer.append("</table></body></html>");
		buffer2.append( totalElapsed );
		
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
		catch(Exception e)
		{
			// ignore	
		}
	}
	
}

