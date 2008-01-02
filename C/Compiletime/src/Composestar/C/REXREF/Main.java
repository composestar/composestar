/*
 * Created on 23-sep-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package Composestar.C.REXREF;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import Composestar.Core.LAMA.UnitRegister;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.REXREF.DoResolve;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Utils.Debug;

/**
 * @author johan
 */
public class Main extends DefaultHandler implements CTCommonModule
{
	public static boolean debug = true; // display debugging information?

	private List<String> modules = new ArrayList<String>();

	private UnitRegister register;

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
		register = (UnitRegister) resources.get(UnitRegister.RESOURCE_KEY);
		if (register == null)
		{
			register = new UnitRegister();
			resources.put(UnitRegister.RESOURCE_KEY, register);
		}

		File concernXml = new File(resources.configuration().getProject().getBase(), "CConcern.xml");
		if (concernXml.exists())
		{
			resolveConcernReferences(concernXml, resources.repository());
		}
		DoResolve dr = new DoResolve();
		dr.go(resources.repository());
	}

	private void resolveConcernReferences(File concernXml, DataStore ds) throws ModuleException
	{
		// iterate over all instances of ConcernReference
		checkCConcern(concernXml);
		for (Iterator it = ds.getAllInstancesOf(ConcernReference.class); it.hasNext();)
		{
			ConcernReference ref = (ConcernReference) it.next();

			// fetch the Concern with the same name as the reference references
			// to
			Concern concern = (Concern) ds.getObjectByID(ref.getQualifiedName());
			if (concern != null)
			{
				ref.setRef(concern);
				ref.setResolved(true);
			}
			else if (!modules.isEmpty())
			{
				boolean inCConcern = false;
				for (String module : modules)
				{
					if (ref.getName().equals(module))
					{
						/** create concern and add to repository * */
						PrimitiveConcern pconcern = new PrimitiveConcern();
						CFile type = new CFile();
						register.registerLanguageUnit(type);
						pconcern.setName(module);
						type.setName(module);
						type.setFullName(module);
						pconcern.setPlatformRepresentation(type);
						type.setParentConcern(pconcern);
						ds.addObject(pconcern.getName(), pconcern);
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

	public void checkCConcern(File concernXml) throws ModuleException
	{
		try
		{
			SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
			SAXParser saxParser = saxParserFactory.newSAXParser();
			XMLReader parser = saxParser.getXMLReader();
			parser.setContentHandler(this);
			parser.parse(new InputSource(new FileInputStream(concernXml)));
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
			modules.add(attr.getValue("name"));
		}
	}
}
