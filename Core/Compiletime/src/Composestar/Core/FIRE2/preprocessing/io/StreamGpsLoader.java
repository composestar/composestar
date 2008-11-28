/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2008 University of Twente.
 *
 * Compose* is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation; either version 2.1 of 
 * the License, or (at your option) any later version.
 *
 * Compose* is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this program. If not, see 
 * <http://www.gnu.org/licenses/>.
 *
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 * $Id$
 */

package Composestar.Core.FIRE2.preprocessing.io;

import groove.graph.GraphInfo;
import groove.io.AspectualViewGps;
import groove.io.ExtensionFilter;
import groove.trans.RuleNameLabel;
import groove.util.Groove;
import groove.view.AspectualRuleView;
import groove.view.DefaultGrammarView;
import groove.view.aspect.AspectGraph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Groove grammar loader that uses resource streams
 * 
 * @author Michiel Hendriks
 */
public class StreamGpsLoader extends AspectualViewGps
{
	public StreamGpsLoader()
	{}

	public DefaultGrammarView unmarshal(Class<?> rescContext, String baseLocation) throws IOException
	{
		String grammarName = baseLocation.substring(baseLocation.lastIndexOf('/') + 1);
		grammarName = getExtensionFilter().stripExtension(grammarName);
		DefaultGrammarView result = createGrammar(grammarName);

		InputStream is = rescContext.getResourceAsStream(baseLocation + '/' + grammarName + ".properties");
		Properties props = new Properties();
		if (is != null)
		{
			props.load(is);
		}
		result.setProperties(props);

		Set<String> manifest = new HashSet<String>();
		is = rescContext.getResourceAsStream(baseLocation + "/MANIFEST");
		if (is != null)
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			try
			{
				while (true)
				{
					String item = reader.readLine();
					if (item == null)
					{
						break;
					}
					manifest.add(baseLocation + '/' + item.trim());
				}
			}
			finally
			{
				reader.close();
			}
		}

		loadRules(result, rescContext, manifest);
		// loadStartGraph(result, startGraphName, location);
		// TODO loadControl(result, location);
		return result;
	}

	protected StreamGxl streamGxlLoader;

	private void loadRules(DefaultGrammarView result, Class<?> rescContext, Set<String> manifest) throws IOException
	{
		for (String item : manifest)
		{
			if (RULE_FILTER.hasExtension(item))
			{
				InputStream is = rescContext.getResourceAsStream(item);
				if (is != null)
				{
					if (streamGxlLoader == null)
					{
						streamGxlLoader = new StreamGxl();
					}
					RuleNameLabel ruleName =
							new RuleNameLabel(RULE_FILTER.stripExtension(item.substring(item.lastIndexOf('/') + 1)));
					AspectGraph unmarshalledRule =
							AspectGraph.getFactory().fromPlainGraph(
									streamGxlLoader.unmarshalGraph(ruleName.name(), new InputStreamReader(is)));
					GraphInfo.setRole(unmarshalledRule, Groove.RULE_ROLE);
					AspectualRuleView ruleView =
							new AspectualRuleView(unmarshalledRule, ruleName, result.getProperties());
					result.addRule(ruleView);
				}
				else
				{
					// TODO error
				}
			}
		}
	}

	static private final ExtensionFilter RULE_FILTER = Groove.createRuleFilter();
}
