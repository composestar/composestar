/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2003-2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.FILTH;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import Composestar.Core.Config.ModuleInfo;
import Composestar.Core.Config.ModuleInfoManager;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleReference;
import Composestar.Core.Exception.ConfigurationException;
import Composestar.Core.FILTH.Core.Action;
import Composestar.Core.FILTH.Core.Graph;
import Composestar.Core.FILTH.Core.Node;
import Composestar.Core.FILTH.Core.OrderTraverser;
import Composestar.Core.FILTH.Core.Rule;
import Composestar.Core.FILTH.Core.SoftPreRule;
import Composestar.Core.FILTH.XMLSpecification.ConstraintFilter;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SANE.FilterModSIinfo;
import Composestar.Core.SANE.FilterModuleSuperImposition;
import Composestar.Core.SANE.SIinfo;

public class FILTHServiceImpl extends FILTHService
{
	private boolean outputEnabled;

	private String specFilename;

	protected CommonResources resources;

	protected FILTHServiceImpl(CommonResources cr) throws ConfigurationException
	{
		resources = cr;
		ModuleInfo mi = ModuleInfoManager.get(FILTH.MODULE_NAME);
		outputEnabled = mi.getBooleanSetting("outputEnabled");
		specFilename = mi.getSetting("input");
	}

	@Override
	public List<FilterModuleSuperImposition> getOrder(Concern c)
	{
		FilterModuleOrder fo = (FilterModuleOrder) c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
		if (fo == null)
		{
			return Collections.emptyList();
		}

		// getMultipleOrder(c);
		// TODO: calling getMultipleOrder(c) generates lots of exceptions for
		// CONE-IS (filenotfound & nullpointer)

		return fo.filterModuleSIList();
	}

	@Override
	public List<List<FilterModuleSuperImposition>> getMultipleOrder(Concern c)
	{
		String filename = "";
		String cssFile = "";
		try
		{
			File file = new File(resources.configuration().getProject().getIntermediate(), "Analyses");
			if (!file.exists())
			{
				file.mkdir();
			}
			if (file.isDirectory())
			{
				if (c.getName().indexOf("<") >= 0)
				{
					filename = c.getName().substring(0, c.getName().indexOf("<"));

					int count = 1, fromIndex = 0, foundIndex = 0;
					do
					{
						foundIndex = c.getName().indexOf(",", fromIndex);
						if (foundIndex >= 0)
						{
							count++;
							fromIndex = foundIndex + 1;
						}
					} while (foundIndex >= 0);

					filename = filename + "`" + count;
				}
				else
				{
					filename = c.getName();
				}
				filename = file.getAbsolutePath() + "\\FILTH_" + filename + ".html";

				if (outputEnabled)
				{
					FILTHService.setLog(new PrintStream(new FileOutputStream(filename)));
				}
				else
				{
					FILTHService.setLog(new PrintStream(new DevNullOutputStream()));
				}
			}
		}
		catch (FileNotFoundException e)
		{
			// e.getMessage();
			// e.printStackTrace();
			// throw new ModuleException("SECRET","FILTH report file creation
			// failed (" + filename + "), with reason: " + e.getMessage());
			logger.info("FILTH report file creation failed (" + filename + "), with reason: " + e.getMessage(), e);
		}

		// String composestarpath = props.getProperty("ComposestarPath");
		// FILTHService.log.println("<html><head><title>SECRET
		// Report</title><link rel=\"stylesheet\" href=\"" + cssFile + "\"
		// type=\"text/css\"></head><body><H1>FILTH Report</h1><h3>");
		StringBuffer buffer = new StringBuffer("");

		buffer.append("<html>\n");
		buffer.append("\t<head>\n");
		buffer.append("\t\t<title>Filter Composition and Checking report</title>\n");
		buffer.append("<link id=\"css_color\" rel=\"stylesheet\" type=\"text/css\" href=\"").append(cssFile).append(
				"\"/>\n");
		buffer.append("</head>\n");
		buffer.append("<body>\n");
		// TODO bad path usage
		buffer.append("<div id=\"headerbox\" class=\"headerbox\"><font size=6><b><i><img src=\"");
		File logoFile = resources.getPathResolver().getResource("logo.gif");
		if (logoFile != null)
		{
			buffer.append(logoFile.toURI().toString());
		}
		buffer.append("\"/>  /TRESE/Compose*/FILTH</i></b></font></div>\n");
		buffer.append("<h3>Report generated on:  ").append(new Date().toString()).append("</h3>\n");

		FILTHService.log.print(buffer.toString());

		FILTHService.log.print("<h4>Analyzing Filter Module Orders for shared join point: <u>" + c.getName()
				+ "</u></h4>\n");

		LinkedList<List<FilterModuleSuperImposition>> forders = new LinkedList<List<FilterModuleSuperImposition>>();

		LinkedList<FilterModuleSuperImposition> modulrefs;

		Graph g = new Graph();
		g.setRoot(new Node("root"));

		FILTHService.log.print("<h4>Superimposed Filter Modules: </h4>\n");
		FILTHService.log.print("<ul>\n");
		modulrefs = processModules(c, g);
		FILTHService.log.print("</ul>\n");

		processOrderingSpecifications(g);

		FILTHService.log.print("<h4>Ordering constraints: </h4>\n");
		FILTHService.log.print("<ul>\n");

		printOrderingSpecifications();
		processXML(c, g);

		FILTHService.log.print("</ul>\n");

		// FILTHService.print("FILTH::generating alternatives\n");
		OrderTraverser ot = new OrderTraverser();
		LinkedList<List<Node>> orders = ot.multiTraverse(g);
		/*
		 * System.out.println("<<INNER multiple-orders>>"); for (Iterator
		 * j=orders.iterator();j.hasNext();){ for (Iterator
		 * i=((LinkedList)j.next()).iterator();i.hasNext();)
		 * System.out.println(((Node)i.next()).getElement());
		 * System.out.println("/n-----------"); }
		 */
		for (List<Node> ord : orders)
		{
			Iterator<Node> i = ord.iterator();
			/* skip the root */
			i.next();
			LinkedList<FilterModuleSuperImposition> anOrder = new LinkedList<FilterModuleSuperImposition>();
			while (i.hasNext())
			{
				Action a = (Action) i.next().getElement();

				for (FilterModuleSuperImposition fmsi : modulrefs)
				{
					// System.out.println("FILTH
					// ordering>>>"+a+"::"+fr.getName() );
					if (a.getName().equals(fmsi.getFilterModule().getQualifiedName()))
					{
						anOrder.addLast(fmsi);
						break;
					}

				}
				// System.out.println(count++);
			}
			anOrder.addLast(new FilterModuleSuperImposition(InnerDispatcher.getInnerDispatchReference()));
			forders.addLast(anOrder);
		}

		/* DEBUG info about the orders */
		int alt = 1;

		FILTHService.log.print("<h4>Selected Order: </h4>\n");
		FILTHService.log.print("<ol>\n");
		// FILTHService.printTab(3,"<-- Alternative ("+ alt++ +") -->\n");
		for (FilterModuleSuperImposition fmsi : forders.getFirst())
		{
			FilterModuleReference fmr = fmsi.getFilterModule();
			if (!InnerDispatcher.isDefaultDispatch(fmr))
			{
				FILTHService.log.print("<li><i>" + fmr.getRef().getQualifiedName() + "</i></li>\n");
			}
		}
		FILTHService.log.print("</ol>\n");

		FILTHService.log.print("<h4>Alternatives: </h4>\n");
		// FILTHService.log.print("<ul>\n");

		for (List<FilterModuleSuperImposition> fmsiList : forders)
		{
			FILTHService.log.print("<h4>Alternative[" + alt + "]: </h4>\n");
			FILTHService.log.print("<ol>\n");
			for (FilterModuleSuperImposition fmsi : fmsiList)
			{
				FilterModuleReference fmr = fmsi.getFilterModule();
				if (!InnerDispatcher.isDefaultDispatch(fmr))
				{
					FILTHService.log.print("<li><i>" + fmr.getRef().getQualifiedName() + "</i></li>\n");
				}
			}
			FILTHService.log.print("</ol>\n");
			alt++;
		}
		/* DEBUG info end */

		/** * attaching all orders to the concern in DataStore */
		c.addDynObject(FilterModuleOrder.ALL_ORDERS_KEY, forders);

		/*
		 * attaching the first order with different key to be dumped in the
		 * repository the list is encapsulated in FilterModuleOrder for the XML
		 * generator
		 */
		FilterModuleOrder fmorder = new FilterModuleOrder(forders.getFirst());
		c.addDynObject(FilterModuleOrder.SINGLE_ORDER_KEY, fmorder);

		// FILTHService.print("FILTH::order (1) added to the repository
		// {"+c.getName()+"}\n");
		// FILTHService.print("<---FILTH::END--->\n");

		FILTHService.log.print("</body></html>\n");
		FILTHService.log.close();

		if (alt > 2)
		{
			logger.warn("Multiple Filter Module orderings possible for concern " + c.getQualifiedName());
		}

		return forders; // arrange this according to the output required!!
	}

	private LinkedList<FilterModuleSuperImposition> processModules(Concern c, Graph g)
	{
		LinkedList<FilterModuleSuperImposition> modulerefs = new LinkedList<FilterModuleSuperImposition>();

		/* get the superimposition from the repository */
		SIinfo sinfo = (SIinfo) c.getDynObject(SIinfo.DATAMAP_KEY);
		/* get the firt altnernative */
		Vector<FilterModSIinfo> msalts = sinfo.getFilterModSIAlts();

		/*
		 * System.out.print(">>>"+c.getName()); for (Iterator
		 * i=msalts.iterator();i.hasNext();)
		 * System.out.println("@@@@"+i.next());
		 */
		/* get the vector of the superimposed filtermodules */
		FilterModSIinfo fmsi = msalts.get(0);

		/* add the name of each filtermodule into the graph */
		for (FilterModuleSuperImposition fms : (List<FilterModuleSuperImposition>) fmsi.getAll())
		{
			FilterModuleReference fr = fms.getFilterModule();
			Action a = new Action(fr.getQualifiedName(), Boolean.TRUE, true);
			Action.insert(a, g);

			// FILTHService.print("FILTH::adding module> "+fr.getName()+"|");
			FILTHService.log.print("<li><i>" + fr.getQualifiedName() + "</i></li>\n");

			modulerefs.add(fms);
		}
		return modulerefs;
	}

	private void processOrderingSpecifications(Graph g)
	{
		Map<String, SyntacticOrderingConstraint> map = (Map<String, SyntacticOrderingConstraint>) DataStore.instance()
				.getObjectByID(FILTH.FILTER_ORDERING_SPEC);
		if (map == null)
		{
			return;
		}
		logger.info("FilterModule ordering constraints: " + map);

		String left;
		for (SyntacticOrderingConstraint soc : map.values())
		{
			left = soc.getLeft();
			Iterator<String> socit = soc.getRightFilterModules();
			while (socit.hasNext())
			{
				String right = socit.next();
				processRule(left, right, g);
			}
		}
	}

	private void printOrderingSpecifications()
	{
		Map<String, SyntacticOrderingConstraint> map = (Map<String, SyntacticOrderingConstraint>) DataStore.instance()
				.getObjectByID(FILTH.FILTER_ORDERING_SPEC);
		if (map == null)
		{
			return;
		}
		for (SyntacticOrderingConstraint soc : map.values())
		{
			FILTHService.log.print("<li><i>" + soc.toString() + "</i></li>\n");
		}
	}

	private void processRule(String left, String right, Graph graph)
	{
		Action l, r;
		Node nl, nr;

		nl = Action.lookupByName(left, graph);

		if (nl != null)
		{
			l = (Action) nl.getElement();
		}
		else
		{
			/*
			 * l=new Action(_left,new Boolean(true),true);
			 * Action.insert(l,_graph); System.out.println("Action "+l+"
			 * added");
			 */
			l = null;
		}

		nr = Action.lookupByName(right, graph);
		if (nr != null)
		{
			r = (Action) nr.getElement();
		}
		else
		{
			/*
			 * r=new Action(_right,new Boolean(true),true);
			 * Action.insert(r,_graph); System.out.println("Action "+r+"
			 * added");
			 */
			r = null;
		}

		/* we add a rule only if both arguments are active */
		if ((l != null) && (r != null))
		{
			Rule rule = new SoftPreRule(l, r);
			rule.insert(graph);
		}
	}

	private void processXML(Concern c, Graph g)
	{
		/* process XML specification, build the rules into the graph */
		try
		{
			SAXParserFactory saxfactory = SAXParserFactory.newInstance();
			saxfactory.setNamespaceAware(true);
			XMLReader xr = saxfactory.newSAXParser().getXMLReader();
			ConstraintFilter of = new ConstraintFilter(g);
			of.setParent(xr);

			if (specFilename != null)
			{
				File file = new File(specFilename);
				if (file != null && file.exists() && file.canRead())
				{
					FileReader fr = new FileReader(specFilename);
					of.parse(new InputSource(fr));
				}
				else
				{
					logger.warn("Could not read/find Filter Module Order specification (" + specFilename + ").", c);
				}
			}
		}
		catch (SAXException se)
		{
			logger.warn("Problems parsing file: " + specFilename + ", message: " + se.getMessage(), se);
		}
		catch (Exception ioe)
		{
			logger.warn("Could not read/find Filter Module Order specification (" + specFilename + ").", c);
		}
	}
}
