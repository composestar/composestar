/*
 * This file is part of the Compose* project.
 * http://composestar.sourceforge.net
 * Copyright (C) 2007 University of Twente.
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

package Composestar.Core.SECRET3.Report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.ProcessingInstruction;

import Composestar.Core.Config.BuildConfig;
import Composestar.Core.CpsRepository2.FilterModules.Filter;
import Composestar.Core.CpsRepository2.SIInfo.ImposedFilterModule;
import Composestar.Core.FILTH2.DefaultInnerDispatchNames;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.Master.ModuleNames;
import Composestar.Core.Resources.CommonResources;
import Composestar.Core.SECRET3.ConcernAnalysis;
import Composestar.Core.SECRET3.Conflict;
import Composestar.Core.SECRET3.FilterSetAnalysis;
import Composestar.Core.SECRET3.SECRETResources;
import Composestar.Core.SECRET3.Model.ConflictRule;
import Composestar.Core.SECRET3.Model.ExecModelOperationSequence;
import Composestar.Core.SECRET3.Model.FilterActionOperationSequence;
import Composestar.Core.SECRET3.Model.OperationSequence;
import Composestar.Core.SECRET3.Model.Resource;
import Composestar.Core.SECRET3.Model.ExecModelOperationSequence.GraphLabel;
import Composestar.Utils.FileUtils;
import Composestar.Utils.StringUtils;
import Composestar.Utils.Logging.CPSLogger;

/**
 * Produces an XML report in the projects Analysis directory
 * 
 * @author Michiel Hendriks
 */
public class XMLReport implements SECRETReport
{
	protected static final CPSLogger logger = CPSLogger.getCPSLogger(ModuleNames.SECRET + ".Report");

	/**
	 * The output filter
	 */
	protected File outputFile;

	/**
	 * Lookup table for conflict rules to generated identifiers. Used for
	 * referrals.
	 */
	protected Map<ConflictRule, String> ruleIds;

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.SECRET3.Report.SECRETReport#report(Composestar.Core.
	 * Resources .CommonResources, Composestar.Core.SECRET3.SECRETResources)
	 */
	public void report(CommonResources resources, SECRETResources secretResources)
	{
		outputFile = new File(resources.configuration().getProject().getIntermediate(), "Analyses");
		if (!outputFile.exists())
		{
			if (!outputFile.mkdirs())
			{
				logger.warn(String.format("Unable to create report directory: %s", outputFile));
				return;
			}
		}
		if (!outputFile.isDirectory())
		{
			logger.warn(String.format("Report output location is not a directory: %s", outputFile));
			return;
		}
		outputFile = new File(outputFile, "SECRET.xml");

		ruleIds = new HashMap<ConflictRule, String>();

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try
		{
			builder = factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e)
		{
			logger.warn(String.format("Error creating a document builder: %s", e));
			return;
		}
		Document xmlDoc = builder.newDocument();

		// try to copy the Xslt
		InputStream is = XMLReport.class.getResourceAsStream("SECRETStyle.xslt");
		if (is != null)
		{
			try
			{
				FileOutputStream stylesheet =
						new FileOutputStream(new File(outputFile.getParentFile(), "SECRETStyle.xslt"));
				try
				{
					FileUtils.copy(is, stylesheet);
				}
				finally
				{
					stylesheet.close();
				}
				ProcessingInstruction pi =
						xmlDoc.createProcessingInstruction("xml-stylesheet",
								"type=\"text/xml\" href=\"SECRETStyle.xslt\"");
				xmlDoc.appendChild(pi);
			}
			catch (IOException e)
			{
				logger.info("Error writing stylesheet: " + e.getLocalizedMessage());
			}
			finally
			{
				try
				{
					is.close();
				}
				catch (IOException e)
				{
					logger.error(e);
				}
			}
		}

		writeReport(resources.configuration(), secretResources, xmlDoc);

		OutputFormat format = new OutputFormat();
		format.setIndenting(true);
		format.setIndent(4);
		format.setMethod("xml");
		XMLSerializer ser;
		try
		{
			ser = new XMLSerializer(new FileOutputStream(outputFile), format);
		}
		catch (FileNotFoundException e)
		{
			logger.warn(String.format("Error writing report: %s", e));
			return;
		}
		try
		{
			ser.serialize(xmlDoc);
		}
		catch (IOException e)
		{
			logger.warn(String.format("Error writing report: %s", e));
			return;
		}
		logger.info(String.format("Report generated in %s", outputFile));
	}

	/**
	 * Write the root elements of the report
	 * 
	 * @param config
	 * @param secretResources
	 * @param xmlDoc
	 */
	protected void writeReport(BuildConfig config, SECRETResources secretResources, Document xmlDoc)
	{
		Element root = xmlDoc.createElement("secretreport");
		root.setAttribute("project", config.getProject().getName());
		root.setAttribute("timestamp", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz").format(new Date()));

		writeResources(secretResources, xmlDoc, root);
		writeActions(secretResources, xmlDoc, root);
		writeRules(secretResources, xmlDoc, root);

		writeAnalyses(secretResources, xmlDoc, root);

		xmlDoc.appendChild(root);
	}

	/**
	 * Write the defined resources
	 * 
	 * @param secretResources
	 * @param xmlDoc
	 * @param root
	 */
	protected void writeResources(SECRETResources secretResources, Document xmlDoc, Element root)
	{
		for (Resource resource : secretResources.getResources())
		{
			Element resElm = xmlDoc.createElement("resource");
			resElm.setAttribute("name", resource.getName());
			for (String op : resource.getVocabulary())
			{
				Element opElm = xmlDoc.createElement("operation");
				opElm.setTextContent(op);
				resElm.appendChild(opElm);
			}
			root.appendChild(resElm);
		}
	}

	/**
	 * Write the defined actions
	 * 
	 * @param secretResources
	 * @param xmlDoc
	 * @param root
	 */
	protected void writeActions(SECRETResources secretResources, Document xmlDoc, Element root)
	{
		for (OperationSequence seq : secretResources.getOperationSequences())
		{
			Element resElm = xmlDoc.createElement("action");
			if (seq instanceof ExecModelOperationSequence)
			{
				ExecModelOperationSequence emseq = (ExecModelOperationSequence) seq;
				for (GraphLabel lbl : emseq.getLabels())
				{
					Element opElm = xmlDoc.createElement("label");
					opElm.setAttribute("type", lbl.getType().toString().toLowerCase());
					opElm.setTextContent(lbl.getLabel());
					resElm.appendChild(opElm);
				}
			}
			else if (seq instanceof FilterActionOperationSequence)
			{
				Element opElm = xmlDoc.createElement("label");
				opElm.setAttribute("type", "filteraction");
				opElm.setTextContent(((FilterActionOperationSequence) seq).getFilterAction().getName());
				resElm.appendChild(opElm);
			}
			for (Entry<Resource, List<String>> op : seq.getOperations().entrySet())
			{
				Element opElm = xmlDoc.createElement("sequence");
				opElm.setAttribute("resource", op.getKey().getName());
				StringBuffer sb = new StringBuffer();
				for (String o : op.getValue())
				{
					if (sb.length() > 0)
					{
						sb.append(";");
					}
					sb.append(o);
				}
				opElm.setTextContent(sb.toString());
				resElm.appendChild(opElm);
			}

			root.appendChild(resElm);
		}
	}

	/**
	 * Write the defined rules
	 * 
	 * @param secretResources
	 * @param xmlDoc
	 * @param root
	 */
	protected void writeRules(SECRETResources secretResources, Document xmlDoc, Element root)
	{
		for (ConflictRule rule : secretResources.getRules())
		{
			Element resElm = xmlDoc.createElement("rule");
			resElm.setAttribute("resource", rule.getResource().getName());
			resElm.setAttribute("type", rule.getType().toString().toLowerCase());

			String ruleId = "rule" + (ruleIds.keySet().size() + 1);
			resElm.setAttribute("id", ruleId);
			ruleIds.put(rule, ruleId);

			Element patternElm = xmlDoc.createElement("pattern");
			patternElm.setTextContent(rule.getPattern().toString());
			resElm.appendChild(patternElm);

			Element msgElm = xmlDoc.createElement("message");
			msgElm.setTextContent(rule.getMessage());
			resElm.appendChild(msgElm);

			root.appendChild(resElm);
		}
	}

	/**
	 * Write the created concern analysis to the report
	 * 
	 * @param secretResources
	 * @param xmlDoc
	 * @param root
	 */
	protected void writeAnalyses(SECRETResources secretResources, Document xmlDoc, Element root)
	{
		for (ConcernAnalysis ca : secretResources.getConcernAnalyses())
		{
			Element caElm = xmlDoc.createElement("analysis");
			caElm.setAttribute("concern", ca.getConcern().getFullyQualifiedName());

			for (FilterSetAnalysis fsa : ca.getAnalysis())
			{
				Element fsaElm = xmlDoc.createElement("filterset");
				fsaElm.setAttribute("selected", Boolean.toString(fsa.isSelected()));
				fsaElm.setAttribute("direction", fsa.getFilterDirection().toString());

				Element orderElm = xmlDoc.createElement("order");
				for (ImposedFilterModule fmsi : fsa.getOrder())
				{
					if (DefaultInnerDispatchNames.FQN_FILTER_MODULE.equals(fmsi.getFilterModule()
							.getFullyQualifiedName()))
					{
						continue;
					}
					Element fmsiElm = xmlDoc.createElement("filtermodule");
					if (fmsi.getCondition() != null)
					{
						fmsiElm.setAttribute("condition", fmsi.getCondition().getReferenceId());
					}
					fmsiElm.setTextContent(fmsi.getFilterModule().getFullyQualifiedName());
					orderElm.appendChild(fmsiElm);
				}
				fsaElm.appendChild(orderElm);

				for (Conflict conf : fsa.executionConflicts())
				{
					Element confElm = xmlDoc.createElement("conflict");
					confElm.setAttribute("ruleid", ruleIds.get(conf.getRule()));
					confElm.setAttribute("resource", conf.getResource().getName());
					confElm.setAttribute("selector", conf.getSelector().toString());

					Element opsElm = xmlDoc.createElement("sequence");
					opsElm.setTextContent(StringUtils.join(conf.getOperations(), " "));
					confElm.appendChild(opsElm);

					Element traceElm = null;
					for (ExecutionTransition et : conf.getTrace())
					{
						FlowNode fn = et.getStartState().getFlowNode();
						if (fn.containsName(FlowNode.FILTER_NODE))
						{
							if (!(fn.getRepositoryLink() instanceof Filter))
							{
								continue;
							}
							Filter re = (Filter) fn.getRepositoryLink();
							if (re.getSourceInformation() == null)
							{
								continue;
							}
							String fname = re.getType().getFilterName();
							traceElm = xmlDoc.createElement("trace");
							traceElm.setTextContent(re.getFullyQualifiedName() + " : " + fname);
						}
						else if (fn.containsName(FlowNode.FILTER_ACTION_NODE))
						{
							if (traceElm != null)
							{
								if (!fn.containsName("ContinueAction") && !fn.containsName("SkipAction"))
								{
									confElm.appendChild(traceElm);
								}
								traceElm = null;
							}
						}
					}

					fsaElm.appendChild(confElm);
				}

				caElm.appendChild(fsaElm);
			}

			root.appendChild(caElm);
		}
	}
}
