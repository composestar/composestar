/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2006 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Core.DIGGER;

import java.io.File;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import Composestar.Core.DIGGER.Graph.AbstractConcernNode;
import Composestar.Core.DIGGER.Graph.ConcernNode;
import Composestar.Core.DIGGER.Graph.CondMatchEdge;
import Composestar.Core.DIGGER.Graph.Edge;
import Composestar.Core.DIGGER.Graph.FilterChainNode;
import Composestar.Core.DIGGER.Graph.FilterElementNode;
import Composestar.Core.DIGGER.Graph.FilterNode;
import Composestar.Core.DIGGER.Graph.Graph;
import Composestar.Core.DIGGER.Graph.Node;
import Composestar.Core.DIGGER.Graph.SimpleConcernNode;
import Composestar.Core.DIGGER.Graph.SubstitutionEdge;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.Master.Config.Configuration;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Exports the generated dispatch graph to an XML file
 * 
 * @author Michiel Hendriks
 */
public class XmlExporter
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(DIGGER.MODULE_NAME);

	/**
	 * If true add comments
	 */
	protected boolean addComments;

	protected Graph graph;

	protected Document xmlDoc;

	public XmlExporter(Graph inGraph, File filename) throws ModuleException
	{
		logger.info("Exporting dispatch graph to " + filename.toString());
		addComments = Configuration.instance().getModuleProperty(DIGGER.MODULE_NAME, "xmlComments", false);
		graph = inGraph;

		// create parent directory if it doesn't exist
		if (!filename.getParentFile().exists())
		{
			filename.getParentFile().mkdirs();
		}

		exportGraphTo(filename);
	}

	public Document getXmlDocument()
	{
		return xmlDoc;
	}

	protected void exportGraphTo(File filename) throws ModuleException
	{
		try
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			xmlDoc = builder.newDocument();

			Element xmlGraph = xmlDoc.createElement("graph");
			xmlDoc.appendChild(xmlGraph);
			Iterator concerns = graph.getConcernNodes();
			while (concerns.hasNext())
			{
				AbstractConcernNode concernNode = (AbstractConcernNode) concerns.next();
				if (concernNode instanceof SimpleConcernNode)
				{
					processSimpleConcernNode((SimpleConcernNode) concernNode, xmlGraph);
				}
				else if (concernNode instanceof ConcernNode)
				{
					processConcernNode((ConcernNode) concernNode, xmlGraph);
				}
			}
			if (graph.getExceptionNode() != null)
			{
				Element xmlExcept = xmlDoc.createElement("exception");
				xmlExcept.setAttribute("id", getIdForNode(graph.getExceptionNode()));
				xmlGraph.appendChild(xmlExcept);
			}

			// Write the file
			Source source = new DOMSource(xmlDoc);
			Result result = new StreamResult(filename);

			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.METHOD, "xml");
			xformer.setOutputProperty(OutputKeys.INDENT, "yes");
			xformer.transform(source, result);
		}
		catch (ParserConfigurationException e)
		{
			throw new ModuleException("ParserConfigurationException: " + e.getMessage(), DIGGER.MODULE_NAME);
		}
		catch (TransformerConfigurationException e)
		{
			throw new ModuleException("TransformerConfigurationException: " + e.getMessage(), DIGGER.MODULE_NAME);
		}
		catch (TransformerException e)
		{
			throw new ModuleException("TransformerException: " + e.getMessage(), DIGGER.MODULE_NAME);
		}
	}

	protected String getIdForNode(Node node)
	{
		return "n" + node.hashCode();
	}

	protected void addComment(Element toElement, Node forNode)
	{
		if (!addComments)
		{
			return;
		}
		Comment cmt = xmlDoc.createComment("Node: " + forNode.toString() + " " + forNode.getLabel());
		toElement.appendChild(cmt);
	}

	protected void addComment(Element toElement, Edge forEdge)
	{
		if (!addComments)
		{
			return;
		}
		Comment cmt = xmlDoc.createComment("Destination node: " + forEdge.getDestination().toString() + " "
				+ forEdge.getDestination().getLabel());
		toElement.appendChild(cmt);
	}

	protected void processSimpleConcernNode(SimpleConcernNode concernNode, Element parentNode)
	{
		Element elm = xmlDoc.createElement("simpleconcern");
		parentNode.appendChild(elm);
		addComment(elm, concernNode);
		elm.setAttribute("id", getIdForNode(concernNode));
		elm.setAttribute("label", concernNode.getLabel());
	}

	protected void processConcernNode(ConcernNode concernNode, Element parentNode)
	{
		Element cn = xmlDoc.createElement("concern");
		parentNode.appendChild(cn);
		addComment(cn, concernNode);
		cn.setAttribute("id", getIdForNode(concernNode));
		cn.setAttribute("label", concernNode.getLabel());

		processFilterChainNode(concernNode.getInputFilters(), cn);
		processFilterChainNode(concernNode.getOutputFilters(), cn);

		Element xmlInner = xmlDoc.createElement("inner");
		xmlInner.setAttribute("id", getIdForNode(concernNode.getInnerNode()));
		cn.appendChild(xmlInner);
	}

	protected void createEdge(Element parentNode, Edge edge)
	{
		Element xmlEdge = xmlDoc.createElement("edge");
		parentNode.appendChild(xmlEdge);
		addComment(xmlEdge, edge);
		xmlEdge.setAttribute("destination", getIdForNode(edge.getDestination()));

		if (edge instanceof CondMatchEdge)
		{
			xmlEdge.setAttribute("condition", ((CondMatchEdge) edge).getConditionAsString());
			xmlEdge.setAttribute("enabler", Boolean.toString(((CondMatchEdge) edge).getEnabler()));
			xmlEdge.setAttribute("matching", ((CondMatchEdge) edge).getMatchingPartsAsString());
		}
		else if (edge instanceof SubstitutionEdge)
		{
			xmlEdge.setAttribute("substitution", ((SubstitutionEdge) edge).getSubstitutionPartAsString());
		}
	}

	protected void processEdges(Node node, Element parentNode)
	{
		Iterator edges = node.getOutgoingEdges();
		while (edges.hasNext())
		{
			Edge edge = (Edge) edges.next();
			createEdge(parentNode, edge);
			processNode(edge.getDestination(), parentNode);
		}
	}

	protected void processFilterChainNode(FilterChainNode chainNode, Element parentNode)
	{
		Element fc;
		if (chainNode.getDirection() == FilterChainNode.INPUT)
		{
			fc = xmlDoc.createElement("inputfilters");
		}
		else
		{
			fc = xmlDoc.createElement("outputfilters");
		}
		parentNode.appendChild(fc);
		addComment(fc, chainNode);
		processEdges(chainNode, fc);
	}

	/**
	 * Generic node processing. Used to process edges.
	 * 
	 * @param node
	 * @param parentNode
	 */
	protected void processNode(Node node, Element parentNode)
	{
		if (node instanceof FilterNode)
		{
			processFilterNode((FilterNode) node, parentNode);
		}
		else if (node instanceof FilterElementNode)
		{
			processFilterElementNode((FilterElementNode) node, parentNode);
		}
		/*
		 * else if (node instanceof MatchingPatternNode) {
		 * processMatchingPatternNode((MatchingPatternNode) node, parentNode); }
		 */
	}

	protected void processFilterNode(FilterNode filterNode, Element parentNode)
	{
		Element xmlFilter = xmlDoc.createElement("filter");
		parentNode.appendChild(xmlFilter);
		addComment(xmlFilter, filterNode);
		xmlFilter.setAttribute("id", getIdForNode(filterNode));
		xmlFilter.setAttribute("type", filterNode.getType());
		xmlFilter.setAttribute("label", filterNode.getLabel());

		Iterator edges = filterNode.getOutgoingEdges();
		while (edges.hasNext())
		{
			Edge edge = (Edge) edges.next();
			createEdge(xmlFilter, edge);
			if (edge.getDestination() instanceof FilterNode)
			{
				processNode(edge.getDestination(), parentNode);
			}
			else
			{
				processNode(edge.getDestination(), xmlFilter);
			}
		}
	}

	protected void processFilterElementNode(FilterElementNode feNode, Element parentNode)
	{
		Element xmlFE = xmlDoc.createElement("filterelement");
		parentNode.appendChild(xmlFE);
		addComment(xmlFE, feNode);
		xmlFE.setAttribute("id", getIdForNode(feNode));
		processEdges(feNode, xmlFE);
	}
}
