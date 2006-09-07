/*
 * Created on 5-apr-2006
 */
package Composestar.DotNET.SEMTEX;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils; 

/**
 * Imports the semtex.xml file and stores the information in this file into the repository.
 * 
 * @author Michiel van Oudheusden
 *
 */
public class DotNETSemTexCollector implements CTCommonModule {

	
	/* 
	 * Checks if the semtex.xml file is available and imports this file.
	 * 
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Master.CommonResources)
	 */
	public void run(CommonResources resources) throws ModuleException {
		// Read the generated xml file and store the data in the repository
		String filename;

		filename =Configuration.instance().getPathSettings().getPath("Base") + "obj/semtex.xml";
		
		java.io.File semTexFile = new java.io.File(filename);
		if( !semTexFile.exists() )
		{
			Debug.out(Debug.MODE_WARNING, "SEMTEX", "The SemTex file " + filename + " does not exists. Skipping SemTex Collector.");
		}
		else
		{
			// Open and parse the xml file
			try {
				SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
				SAXParser saxParser = saxParserFactory.newSAXParser();
				SemanticContainerHandler sch = new SemanticContainerHandler();
				
				// We use the getCleanInputStream because the SAX parser has some problems with UTF8
				FileInputStream in = FileUtils.getCleanInputStream(new File(filename));
				saxParser.parse(in, sch);
			} catch( Exception e ) {
				throw new ModuleException( e.getMessage() );
			}
			
		}
	}
	
}
