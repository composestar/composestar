/*
 * Created on 29-jun-2006
 *
 */
package Composestar.Core.FIRE2.preprocessing;

import groove.graph.Graph;
import groove.graph.GraphFormatException;
import groove.io.GpsGrammar;
import groove.io.UntypedGxl;
import groove.io.XmlException;
import groove.trans.GraphGrammar;
import groove.trans.PrioritizedRuleSystem;
import groove.trans.StructuredRuleName;
import groove.trans.view.RuleGraph;
import groove.trans.view.RuleViewGrammar;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;

/**
 * Subclass of GpsGrammar that supports loading from jar files
 * 
 * @author Arjan de Roo
 */
public class JarGpsGrammar extends GpsGrammar
{
	private final static String LOAD_ERROR = "Can't load graph grammar";

	private InnerLoader innerLoader = new InnerLoader();

	public GraphGrammar unmarshal(String grammarLocation) throws IOException
	{
		URL grammarUrl = this.getClass().getResource(grammarLocation);

		if (grammarUrl == null)
		{
			throw new FileNotFoundException(LOAD_ERROR + ": rule rystem location \"" + grammarLocation
					+ "\" does not exist");
		}

		RuleViewGrammar result = createGrammar(getExtensionFilter().stripExtension(grammarLocation));
		// load the rules from location
		Map ruleGraphMap = new HashMap();
		Map priorityMap = new HashMap();
		loadRules(grammarUrl, grammarLocation, null, ruleGraphMap, priorityMap);
		Iterator ruleGraphIter = ruleGraphMap.entrySet().iterator();
        for (Object o : ruleGraphMap.entrySet()) {
            Map.Entry ruleGraphEntry = (Map.Entry) o;
            StructuredRuleName ruleName = (StructuredRuleName) ruleGraphEntry.getKey();
            RuleGraph ruleGraph = (RuleGraph) ruleGraphEntry.getValue();
            result.add(ruleGraph, (Integer) priorityMap.get(ruleName));
        }
        // get the start graph
		Graph startGraph = null;

		result.setStartGraph(startGraph);

		return result;
	}

	private void loadRules(URL grammarURL, String relativeLocation, StructuredRuleName rulePath, Map ruleGraphMap,
			Map priorityMap) throws IOException
	{
		String grammarName = grammarURL.getFile();
		int pos = grammarName.indexOf('!');
		String grammarLocation = grammarName.substring(pos + 2);
		if (pos > 0)
		{
			grammarName = grammarName.substring(6, pos);
		}
		else
		{
			grammarName = grammarName.substring(6);
		}
		grammarName = grammarName.replaceAll("%20", " ");

		FileInputStream fis = new FileInputStream(grammarName);
		BufferedInputStream bis = new BufferedInputStream(fis);
		ZipInputStream zis = new ZipInputStream(bis);
		ZipEntry ze;
		try
		{
			while ((ze = zis.getNextEntry()) != null)
			{
				String name = ze.getName();
				if (name.startsWith(grammarLocation) && name.endsWith(".gpr") && !ze.isDirectory())
				{
					String fileName = name.substring(grammarLocation.length() + 1);
					String subName = fileName.substring(0, fileName.length() - 4);

					int separatorPos = subName.indexOf('.');

					Integer priority;
					if (separatorPos > 0)
					{
						priority = new Integer(fileName.substring(0, separatorPos));
						if (priority < 0)
						{
							throw new NumberFormatException("" + priority + " is not a valid priority");
						}
					}
					else
					{
						priority = PrioritizedRuleSystem.DEFAULT_PRIORITY;
					}

					String ruleName = subName.substring(separatorPos + 1);
					StructuredRuleName extendedRulePath = new StructuredRuleName(rulePath, ruleName);

					// check for overlapping rule and directory names
					if (ruleGraphMap.containsKey(extendedRulePath))
					{
						throw new IOException(LOAD_ERROR + ": duplicate rule name \"" + extendedRulePath + '\"');
					}
					try
					{
						RuleGraph ruleGraph = createRuleGraph(innerLoader.unmarshal(relativeLocation + '/' + fileName),
								extendedRulePath);
						ruleGraph.setFixed();
						ruleGraphMap.put(extendedRulePath, ruleGraph);
						priorityMap.put(extendedRulePath, priority);

					}
					catch (GraphFormatException exc)
					{
						throw new IOException(LOAD_ERROR + ": rule format error in " + grammarLocation + ": "
								+ exc.getMessage());
					}
					catch (XmlException exc)
					{
						throw new IOException(LOAD_ERROR + ": xml format error in " + grammarLocation + ": "
								+ exc.getMessage());
					}
					catch (FileNotFoundException exc)
					{
						// proceed; this should not occur but I'm not sure now
						// and
						// don't want
						// to turn it into an assert
					}
				}
			}
		}
		finally
		{
			zis.close();
		}

	}

	private class InnerLoader extends UntypedGxl
	{
		/**
		 * This implementation returns a
		 * <tt>{@link groove.gui.layout.LayedOutGraph}</tt>
		 * 
		 * @param filename
		 */
		public Graph unmarshal(String filename) throws XmlException, FileNotFoundException
		{
			groove.gxl.Graph gxlGraph = unmarshalGxlGraph(filename);
			Graph attrGraph = gxlToAttrGraph(gxlGraph, null);
			Graph result = attrToNormGraph(attrGraph);
			return result;
		}

		public groove.gxl.Graph unmarshalGxlGraph(String filename) throws XmlException, FileNotFoundException
		{
			// get a gxl object from the reader
			groove.gxl.Gxl gxl;
			try
			{
				gxl = new groove.gxl.Gxl();
				Unmarshaller unmarshaller = new Unmarshaller(gxl);
				unmarshaller.setLogWriter(new PrintWriter(System.err));
				InputStream stream = this.getClass().getResourceAsStream(filename);
				Reader reader = new InputStreamReader(stream);
				unmarshaller.unmarshal(reader);
			}
			catch (MarshalException e)
			{
				throw new XmlException(e.getMessage());
			}
			catch (ValidationException e)
			{
				throw new XmlException(e.getMessage());
			}

			// now convert the gxl to an attribute graph
			if (gxl.getGraphCount() != 1)
			{
				throw new XmlException("Only one graph allowed in document");
			}
			// Get the first and only graph element
			return gxl.getGraph(0);
		}
	}
}
