/*
 * Created on Mar 16, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package Composestar.Core.FILTH;

/**
 * @author Isti
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
import java.io.PrintStream;
import java.util.List;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.Master.CommonResources;

public abstract class FILTHService {
	// static{
	// 	System.setProperty("org.xml.sax.driver","org.apache.crimson.parser.XMLReaderImpl");
	// }

	public static PrintStream log = System.out;
	public static int printMode = FILTHService.HTML;
	
	public static final int HTML = 1;
	public static final int NORMAL = 2;
	 
	public static void setLog(PrintStream stream){
		log = stream;
	}

	public static void setLog(String out) throws Exception
	{
		log = new PrintStream(new java.io.FileOutputStream(out));
	}
	
	public static FILTHService getInstance(CommonResources cr){
		 return new FILTHServiceImpl(cr);
	}
	
	public static void print(String mesg){
		if (printMode == FILTHService.HTML)
		{
			log.print(mesg+"<br>");
		}
		else 
		{
			log.print(mesg);
		}
	}

	public static void printTab(int n, String mesg){
		String s = "";
		for (int i=0; i<n; i++)
		{
			if (printMode==FILTHService.HTML)
			{
				s+="&nbsp;";
			}
			else 
			{
				s+=" ";
			}
		}
		if (printMode==FILTHService.HTML)
		{
			log.print(s+mesg+"<br>"); 
		}
		else 
		{
			log.print(s+mesg);
		}
	}

	public abstract List getOrder(Concern c);
	public abstract List getMultipleOrder(Concern c);	
	
	public abstract void copyOperation(Concern c,INCRE inc);
}
