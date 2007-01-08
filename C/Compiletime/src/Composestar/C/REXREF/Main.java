/*
 * Created on 23-sep-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.C.REXREF;

import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import Composestar.C.LAMA.CFile;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.PrimitiveConcern;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.REXREF.DoResolve;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Utils.Debug;

/**
 * @author johan TODO To change the template for this generated type comment go
 *         to Window - Preferences - Java - Code Style - Code Templates
 */
public class Main extends DefaultHandler implements CTCommonModule
{// {
	public static boolean debug = true; // display debugging information?

	private HashMap modules = new HashMap();

	/**
	 * Constructor
	 */
	public Main()
	{}

	/**
	 * Function called by Master
	 * 
	 * @param resources Common resources supplied by Master
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		if (new java.io.File(Configuration.instance().getPathSettings().getPath("Base") + "CConcern.xml").exists())
		{
			resolveConcernReferences();
		}
		DoResolve dr = new DoResolve();
		dr.go(DataStore.instance());
	}

    private void resolveConcernReferences() throws ModuleException
	{
		// iterate over all instances of ConcernReference
		checkCConcern();
		for (Iterator it = DataStore.instance().getAllInstancesOf(ConcernReference.class); it.hasNext();)
		{
			ConcernReference ref = (ConcernReference) it.next();

			// fetch the Concern with the same name as the reference references
			// to
			Concern concern = (Concern) DataStore.instance().getObjectByID(ref.getQualifiedName());
			if (concern != null)
			{
				ref.setRef(concern);
				ref.setResolved(true);
			}
			else if (!modules.isEmpty())
			{
				boolean inCConcern = false;
				Iterator modIter = modules.keySet().iterator();
				while (modIter.hasNext())
				{
					String module = (String) modIter.next();
					if (ref.getName().equals(module))
					{
						/** create concern and add to repository * */
						PrimitiveConcern pconcern = new PrimitiveConcern();
						CFile type = new CFile();
						pconcern.setName(module);
						type.setName(module);
						type.setFullName(module);
						pconcern.setPlatformRepresentation(type);
						type.setParentConcern(pconcern);
						DataStore.instance().addObject(pconcern.getName(), pconcern);
						ref.setRef(pconcern);
						ref.setResolved(true);
						inCConcern = true;
						Debug.out(Debug.MODE_INFORMATION, "REXREF", "Found " + module
								+ " in cps concern as well as in xml file");
					}
				}
				if (!inCConcern)
				{
					throw new ModuleException(
							"ConcernReference '"
									+ ref.getQualifiedName()
									+ "' cannot be resolved (are you referencing a non-existent concern or is the startup object incorrect?)",
							"REXREF", ref);
				}
			}
			else
			{
				throw new ModuleException(
						"ConcernReference '"
								+ ref.getQualifiedName()
								+ "' cannot be resolved (are you referencing a non-existent concern or is the startup object incorrect?)",
						"REXREF", ref);
			}
		}
	}

	public void checkCConcern() throws ModuleException
	{
		try
		{
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader parser = saxParser.getXMLReader();
			parser.setContentHandler(this);
			parser.parse(new InputSource(Configuration.instance().getPathSettings().getPath("Base") + "CConcern.xml"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException
	{
		if ("module".equals(qName) && attr != null)
		{
			modules.put(attr.getValue("name"), null);
		}
	}
}
