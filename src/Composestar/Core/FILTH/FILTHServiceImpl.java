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
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.FilterModuleReference;
import Composestar.Core.FILTH.Core.Action;
import Composestar.Core.FILTH.Core.Graph;
import Composestar.Core.FILTH.Core.Node;
import Composestar.Core.FILTH.Core.OrderTraverser;
import Composestar.Core.FILTH.Core.Rule;
import Composestar.Core.FILTH.Core.SoftPreRule;
import Composestar.Core.FILTH.XMLSpecification.ConstraintFilter;
import Composestar.Core.INCRE.INCRE;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.Core.SANE.FilterModSIinfo;
import Composestar.Core.SANE.FilterModuleSuperImposition;
import Composestar.Core.SANE.SIinfo;
import Composestar.Utils.Debug;

public class FILTHServiceImpl extends FILTHService
{
	private String specfile;

	protected FILTHServiceImpl(CommonResources cr)
	{}

	public List getOrder(Concern c)
	{
		FilterModuleOrder fo = (FilterModuleOrder) c.getDynObject(FilterModuleOrder.SINGLE_ORDER_KEY);
		if (fo == null)
		{
			return new LinkedList();
		}

		// getMultipleOrder(c);
		// TODO: calling getMultipleOrder(c) generates lots of exceptions for
		// CONE-IS (filenotfound & nullpointer)

		return fo.orderAsList();
	}

	public List getMultipleOrder(Concern c)
	{

		// Filth output enabled
		boolean filthOutput = Configuration.instance().getModuleProperty("FILTH", "outputEnabled", true);

		String filename = "";
		String cssFile = "";
		try
		{
			String basedir = Configuration.instance().getPathSettings().getPath("Base");
			File file = new File(basedir + "Analyses/");
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
				filename = file.getAbsolutePath() + "\\FILTH_" + java.net.URLEncoder.encode(filename, "UTF-8")
						+ ".html";

				if (filthOutput)
				{
					FILTHService.setLog(new PrintStream(new FileOutputStream(filename)));
				}
				else
				{
					FILTHService.setLog(new PrintStream(new DevNullOutputStream()));
				}
				cssFile = "file://" + Configuration.instance().getPathSettings().getPath("base") + "SECRET.css";
				if (!(new File(cssFile).exists()))
				{
					cssFile = "file://" + Configuration.instance().getPathSettings().getPath("Composestar")
							+ "SECRET.css";
				}
			}
		}
		catch (Exception e)
		{
			// e.getMessage();
			// e.printStackTrace();
			// throw new ModuleException("SECRET","FILTH report file creation
			// failed (" + filename + "), with reason: " + e.getMessage());
			Debug.out(Debug.MODE_INFORMATION, "FILTH", "FILTH report file creation failed (" + filename
					+ "), with reason: " + e.getMessage());
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
		buffer.append("<div id=\"headerbox\" class=\"headerbox\"><font size=6><b><i><img src=\"" + "file://").append(
				Configuration.instance().getPathSettings().getPath("Composestar")).append(
				"/logo.gif\"/>  /TRESE/Compose*/FILTH</i></b></font></div>\n");
		buffer.append("<h3>Report generated on:  ").append(new Date().toString()).append("</h3>\n");

		FILTHService.log.print(buffer.toString());

		FILTHService.log.print("<h4>Analyzing Filter Module Orders for shared join point: <u>" + c.getName()
				+ "</u></h4>\n");

		LinkedList forders = new LinkedList();

		LinkedList modulrefs;

		Graph g = new Graph();
		g.setRoot(new Node("root"));

		FILTHService.log.print("<h4>Superimposed Filter Modules: </h4>\n");
		FILTHService.log.print("<ul>\n");
		modulrefs = processModules(c, g);
		FILTHService.log.print("</ul>\n");

		this.processOrderingSpecifications(g);

		FILTHService.log.print("<h4>Ordering constraints: </h4>\n");
		FILTHService.log.print("<ul>\n");

		this.printOrderingSpecifications();
		this.processXML(c, g);

		FILTHService.log.print("</ul>\n");

		// FILTHService.print("FILTH::generating alternatives\n");
		OrderTraverser ot = new OrderTraverser();
		LinkedList orders = ot.multiTraverse(g);
		/*
		 * System.out.println("<<INNER multiple-orders>>"); for (Iterator
		 * j=orders.iterator();j.hasNext();){ for (Iterator
		 * i=((LinkedList)j.next()).iterator();i.hasNext();)
		 * System.out.println(((Node)i.next()).getElement());
		 * System.out.println("/n-----------"); }
		 */
		FilterModuleSuperImposition fmsi = null;
		for (Iterator k = orders.iterator(); k.hasNext();)
		{
			Iterator i = ((List) k.next()).iterator();
			/* skip the root */
			i.next();
			LinkedList anOrder = new LinkedList();
			while (i.hasNext())
			{
				Action a = (Action) ((Node) i.next()).getElement();

				for (Iterator j = modulrefs.iterator(); j.hasNext();)
				{
					fmsi = (FilterModuleSuperImposition) j.next();
					// System.out.println("FILTH
					// ordering>>>"+a+"::"+fr.getName() );
					if (a.getName().equals(fmsi.getFilterModule().getName()))
					{
						break;
					}

				}
				anOrder.addLast(fmsi);
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
		for (Iterator j = ((LinkedList) forders.getFirst()).iterator(); j.hasNext();)
		{
			fmsi = (FilterModuleSuperImposition) j.next();
			FilterModuleReference fmr = fmsi.getFilterModule();
			if (!(fmr.getRef().getQualifiedName()
					.equals("CpsDefaultInnerDispatchConcern.CpsDefaultInnerDispatchFilterModule")))
			{
				FILTHService.log.print("<li><i>" + fmr.getRef().getQualifiedName() + "</i></li>\n");
			}
		}
		FILTHService.log.print("</ol>\n");

		FILTHService.log.print("<h4>Alternatives: </h4>\n");
		// FILTHService.log.print("<ul>\n");

		for (Iterator i = forders.iterator(); i.hasNext();)
		{
			FILTHService.log.print("<h4>Alternative[" + alt + "]: </h4>\n");
			FILTHService.log.print("<ol>\n");
			for (Iterator j = ((List) i.next()).iterator(); j.hasNext();)
			{
				fmsi = (FilterModuleSuperImposition) j.next();
				FilterModuleReference fmr = fmsi.getFilterModule();
				if (!(fmr.getRef().getQualifiedName()
						.equals("CpsDefaultInnerDispatchConcern.CpsDefaultInnerDispatchFilterModule")))
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
		FilterModuleOrder fmorder = new FilterModuleOrder((LinkedList) forders.getFirst());
		c.addDynObject(FilterModuleOrder.SINGLE_ORDER_KEY, fmorder);

		// FILTHService.print("FILTH::order (1) added to the repository
		// {"+c.getName()+"}\n");
		// FILTHService.print("<---FILTH::END--->\n");

		FILTHService.log.print("</body></html>\n");
		FILTHService.log.close();

		if (alt > 2)
		{
			Debug.out(Debug.MODE_WARNING, "FILTH", "Multiple Filter Module orderings possible for concern "
					+ c.getQualifiedName(), filename, 0);
		}

		return forders; // arrange this according to the output required!!
	}

	private LinkedList processModules(Concern c, Graph g)
	{
		LinkedList modulerefs = new LinkedList();

		/* get the superimposition from the repository */
		SIinfo sinfo = (SIinfo) c.getDynObject(SIinfo.DATAMAP_KEY);
		/* get the firt altnernative */
		Vector msalts = sinfo.getFilterModSIAlts();

		/*
		 * System.out.print(">>>"+c.getName()); for (Iterator
		 * i=msalts.iterator();i.hasNext();)
		 * System.out.println("@@@@"+i.next());
		 */
		/* get the vector of the superimposed filtermodules */
		FilterModSIinfo fmsi = (FilterModSIinfo) msalts.get(0);

		/* add the name of each filtermodule into the graph */
		for (Iterator i = fmsi.getIter(); i.hasNext();)
		{
			FilterModuleSuperImposition fms = (FilterModuleSuperImposition) i.next();
			FilterModuleReference fr = fms.getFilterModule();
			Action a = new Action(fr.getName(), Boolean.TRUE, true);
			Action.insert(a, g);

			// FILTHService.print("FILTH::adding module> "+fr.getName()+"|");
			FILTHService.log.print("<li><i>" + fr.getQualifiedName() + "</i></li>\n");

			modulerefs.add(fms);
		}
		return modulerefs;
	}

	private void processOrderingSpecifications(Graph g)
	{
		HashMap map = (HashMap) DataStore.instance().getObjectByID(FILTH.FILTER_ORDERING_SPEC);
		Debug.out(Debug.MODE_INFORMATION, "FILTH", "FilterModule ordering constraints: " + map);

		String left, right;
		Iterator it = map.values().iterator();
		while (it.hasNext())
		{
			SyntacticOrderingConstraint soc = (SyntacticOrderingConstraint) it.next();
			left = soc.getLeft();
			Iterator socit = soc.getRightFilterModules();
			while (socit.hasNext())
			{
				right = (String) socit.next();
				this.processRule(left, right, g);
			}
		}
	}

	private void printOrderingSpecifications()
	{
		HashMap map = (HashMap) DataStore.instance().getObjectByID(FILTH.FILTER_ORDERING_SPEC);
		Iterator it = map.values().iterator();

		while (it.hasNext())
		{
			SyntacticOrderingConstraint soc = (SyntacticOrderingConstraint) it.next();
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

			Configuration config = Configuration.instance();
			specfile = config.getModuleProperty("FILTH", "input", null);

			if (specfile != null)
			{
				// System.out.println(_specfile);
				File file = new File(specfile);
				if (file != null && file.exists() && file.canRead())
				{
					FileReader fr = new FileReader(specfile);
					of.parse(new InputSource(fr));
				}
				else
				{
					Debug.out(Debug.MODE_WARNING, "FILTH", "Could not read/find Filter Module Order specification ("
							+ specfile + ").", c);
				}
			}
		}
		catch (SAXException se)
		{
			Debug.out(Debug.MODE_WARNING, "FILTH", "Problems parsing file: " + specfile + ", message: "
					+ se.getMessage());

			Debug.out(Debug.MODE_DEBUG, "FILTH", Debug.stackTrace(se));
		}
		catch (Exception ioe)
		{
			Debug.out(Debug.MODE_WARNING, "FILTH", "Could not read/find Filter Module Order specification (" + specfile
					+ ").", c);
		}
	}

	public void copyOperation(Concern c, INCRE inc)
	{
		/* Copy dynamic objects 'FilterModuleOrders' and 'SingleOrder' */
		Concern cop = (Concern) inc.findHistoryObject(c);

		LinkedList forders = (LinkedList) cop.getDynObject(FilterModuleOrder.ALL_ORDERS_KEY);

		c.addDynObject(FilterModuleOrder.ALL_ORDERS_KEY, forders);
		FilterModuleOrder fmorder = new FilterModuleOrder((LinkedList) forders.getFirst());
		c.addDynObject(FilterModuleOrder.SINGLE_ORDER_KEY, fmorder);

		Debug.out(Debug.MODE_DEBUG, "FILTH [INCRE]", "Restored " + forders.size()
				+ " Filter Module Order(s) for concern " + c.getName());
	}

	// public List getOrder(CpsConcern c)
	// {
	// LinkedList forder = new LinkedList();
	// LinkedList modulrefs;
	// Graph g = new Graph();
	// g.setRoot(new Node((Object) "root"));
	// modulrefs = processModules(c, g);
	// processXML(c, g);
	// OrderTraverser ot = new OrderTraverser();
	// LinkedList order = ot.traverse(g);
	// Action a;
	// Iterator i = order.iterator();
	// i.next();
	// while (i.hasNext())
	// {
	// a = (Action) ((Node) i.next()).getElement();
	// for (Iterator j = modulrefs.iterator(); j.hasNext();)
	// {
	// FilterModuleReference fr = ((FilterModuleReference) j.next());
	// System.out.println("FILTH ordering>>>" + a + "::" + fr.getName());
	// if (a.getName().equals(fr.getName())) forder.addLast(fr);
	// }
	// }
	// return forder;
	// }
}
