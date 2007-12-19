package Composestar.DotNET.TYM.TypeCollector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.CpsConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.CompiledImplementation;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.Source;
import Composestar.Core.CpsProgramRepository.CpsConcern.Implementation.SourceFile;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.LAMA.TypeMap;
import Composestar.Core.LAMA.UnitRegister;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.TYM.TypeCollector.CollectorRunner;
import Composestar.DotNET.LAMA.DotNETType;
import Composestar.Utils.Logging.CPSLogger;

public class DotNETCollectorRunner implements CollectorRunner
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(MODULE_NAME);

	private INCRE incre;

	private DataStore dataStore;

	private UnitRegister register;

	public DotNETCollectorRunner()
	{
		incre = INCRE.instance();
		dataStore = DataStore.instance();
	}

	public void run(CommonResources resources) throws ModuleException
	{
		try
		{
			register = (UnitRegister) resources.get(UnitRegister.RESOURCE_KEY);
			if (register == null)
			{
				register = new UnitRegister();
				resources.put(UnitRegister.RESOURCE_KEY, register);
			}
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader parser = saxParser.getXMLReader();

			DocumentHandler handler = new DocumentHandler(parser, register);
			parser.setContentHandler(handler);
			File typesXml = new File(resources.configuration().getProject().getIntermediate(), "types.xml");
			parser.parse(new InputSource(new FileInputStream(typesXml)));
			if (!INCRE.instance().isIncremental())
			{
				typesXml.delete(); // free up some diskspace because types.xml
				// is no longer needed
			}
		}
		catch (SAXException e)
		{
			throw new ModuleException("XML Parser exception: " + e.getMessage(), MODULE_NAME);
		}
		catch (ParserConfigurationException e)
		{
			throw new ModuleException("Parser Configuration exception: " + e.getMessage(), MODULE_NAME);
		}
		catch (IOException e)
		{
			throw new ModuleException("I/O exception while parsing types.xml: " + e.getMessage(), MODULE_NAME);
		}

		Map typeMap = TypeMap.instance().map();

		/*
		 * TODO : The only types of embedded code are the imported dll's like in
		 * the VenusFlytrap Therefore: 1) embedded code that is fully
		 * programedd, like the Sound concern of Pacman, must be ignored in this
		 * part of code; 2) embedded code from dll's do need to pass this part
		 * of code (Like VenusFlyTrap)
		 */

		// loop through all current concerns, fetch implementation and remove
		// from types map.
		Iterator concernIt = dataStore.getAllInstancesOf(CpsConcern.class);
		while (concernIt.hasNext())
		{
			Object item = concernIt.next();
			CpsConcern concern = (CpsConcern) item;
			Object impl = concern.getImplementation();

			String className;
			if (impl == null)
			{
				continue;
			}
			else if (impl instanceof Source)
			{
				Source source = (Source) impl;
				className = source.getClassName();
			}
			else if (impl instanceof SourceFile)
			{
				// TO DO: remove this?
				SourceFile source = (SourceFile) impl;
				String sourceFile = source.getSourceFile();
				className = sourceFile.replaceAll("\\.\\w+", "");
			}
			else if (impl instanceof CompiledImplementation)
			{
				className = ((CompiledImplementation) impl).getClassName();
			}
			else
			{
				throw new ModuleException(
						"CollectorRunner: Can only handle concerns with source file implementations or direct class links.",
						MODULE_NAME);
			}

			if (!concern.getQualifiedName().equals(className))
			{
				// implementation of a different class
				Object otherConcern = dataStore.getObjectByID(className);
				if (otherConcern instanceof CpsConcern)
				{
					logger.info("Implementation of " + concern + " contains type info for "
							+ ((CpsConcern) otherConcern));
					DotNETType type = (DotNETType) typeMap.get(className);
					concern.setPlatformRepresentation(type);
					type.setParentConcern((CpsConcern) otherConcern);
					typeMap.remove(className);
				}
				continue;
			}

			// transform source name into assembly name blaat.java --> blaat.dll
			if (!typeMap.containsKey(className))
			{
				throw new ModuleException("Implementation: " + className + " for concern: " + concern.getName()
						+ " not found!", MODULE_NAME);

			}
			logger.info("" + concern + " implements own type info");

			DotNETType type = (DotNETType) typeMap.get(className);
			concern.setPlatformRepresentation(type);
			type.setParentConcern(concern);
			typeMap.remove(className);
		}

		// loop through rest of the concerns and add to the repository in the
		// form of primitive concerns
		Iterator it = typeMap.values().iterator();
		while (it.hasNext())
		{
			DotNETType type = (DotNETType) it.next();
			PrimitiveConcern pc = new PrimitiveConcern();
			pc.setName(type.getFullName());
			pc.setPlatformRepresentation(type);
			type.setParentConcern(pc);
			dataStore.addObject(type.getFullName(), pc);
		}
	}
}
