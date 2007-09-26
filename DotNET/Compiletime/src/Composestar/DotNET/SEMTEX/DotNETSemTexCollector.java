/*
 * Created on 5-apr-2006
 */
package Composestar.DotNET.SEMTEX;

import java.io.File;
import java.io.FileInputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Utils.FileUtils;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Imports the semtex.xml file and stores the information in this file into the
 * repository.
 * 
 * @author Michiel van Oudheusden
 */
public class DotNETSemTexCollector implements CTCommonModule
{
	public static final String MODULE_NAME = "SEMTEX";

	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	/*
	 * Checks if the semtex.xml file is available and imports this file.
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		// Read the generated xml file and store the data in the repository
		File semTexFile = new File(resources.configuration().getProject().getIntermediate(), "semtex.xml");
		if (!semTexFile.exists())
		{
			logger.info("The SemTex file " + semTexFile + " does not exists. Skipping SemTex Collector.");
		}
		else
		{
			// Open and parse the xml file
			try
			{
				SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
				SAXParser saxParser = saxParserFactory.newSAXParser();
				SemanticContainerHandler sch = new SemanticContainerHandler();

				// We use the getCleanInputStream because the SAX parser has
				// some problems with UTF-8
				FileInputStream in = FileUtils.getCleanInputStream(semTexFile);
				saxParser.parse(in, sch);
			}
			catch (Exception e)
			{
				throw new ModuleException(e.getMessage(), MODULE_NAME);
			}
		}
	}
}
