/*
 * This file is part of Composestar project [http://composestar.sf.net].
 * Copyright (C) 2007 University of Twente.
 *
 * Licensed under LGPL v2.1 or (at your option) any later version.
 * [http://www.fsf.org/copyleft/lgpl.html]
 *
 * $Id$
 */
package Composestar.Visualization.Export;

import org.jgraph.JGraph;

/**
 * Exports the graph to, for example, a file. Subclasses should be implemented
 * as a Strategy pattern
 * 
 * @author Michiel Hendriks
 */
public abstract class GraphExporter
{
	/**
	 * Export the given graph
	 * 
	 * @param graph
	 * @param destination
	 * @return
	 */
	public abstract void export(JGraph graph, String destination) throws ExportException;
}
