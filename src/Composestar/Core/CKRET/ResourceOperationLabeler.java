package Composestar.Core.CKRET;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.PlatformRepresentation;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FIRE2.model.ExecutionLabels;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.util.regex.LabelSequence;
import Composestar.Core.FIRE2.util.regex.Labeler;
import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;

public class ResourceOperationLabeler implements Labeler
{
	private Hashtable operationTable;

	private LabelSequence defaultSeq = new LabelSequence();

	private String currentResource;

	public ResourceOperationLabeler()
	{
		operationTable = new Hashtable();

		// condition:
		LabelSequence seq = new LabelSequence();
		seq.addResourceOperation("read");
		operationTable.put(new LabelResourcePair(ExecutionLabels.CONDITION_EXPRESSION_FALSE, "condition"), seq);
		operationTable.put(new LabelResourcePair(ExecutionLabels.CONDITION_EXPRESSION_TRUE, "condition"), seq);

		// matchingpart:
		seq = new LabelSequence();
		seq.addResourceOperation("read");
		operationTable.put(new LabelResourcePair(ExecutionLabels.NAME_MATCHING_PART_ANY_FALSE, "target"), seq);
		operationTable.put(new LabelResourcePair(ExecutionLabels.NAME_MATCHING_PART_FALSE_STAR, "target"), seq);
		operationTable.put(new LabelResourcePair(ExecutionLabels.NAME_MATCHING_PART_FALSE_TRUE, "target"), seq);
		operationTable.put(new LabelResourcePair(ExecutionLabels.NAME_MATCHING_PART_STAR_STAR, "target"), seq);
		operationTable.put(new LabelResourcePair(ExecutionLabels.NAME_MATCHING_PART_STAR_TRUE, "target"), seq);
		operationTable.put(new LabelResourcePair(ExecutionLabels.NAME_MATCHING_PART_TRUE_STAR, "target"), seq);
		operationTable.put(new LabelResourcePair(ExecutionLabels.NAME_MATCHING_PART_TRUE_TRUE, "target"), seq);
		operationTable.put(new LabelResourcePair(ExecutionLabels.SIGNATURE_MATCHING_PART_FALSE, "target"), seq);
		operationTable.put(new LabelResourcePair(ExecutionLabels.SIGNATURE_MATCHING_PART_TRUE_STAR, "target"), seq);
		operationTable.put(new LabelResourcePair(ExecutionLabels.SIGNATURE_MATCHING_PART_TRUE_TRUE, "target"), seq);

		operationTable.put(new LabelResourcePair(ExecutionLabels.NAME_MATCHING_PART_ANY_FALSE, "selector"), seq);
		operationTable.put(new LabelResourcePair(ExecutionLabels.NAME_MATCHING_PART_FALSE_STAR, "selector"), seq);
		operationTable.put(new LabelResourcePair(ExecutionLabels.NAME_MATCHING_PART_FALSE_TRUE, "selector"), seq);
		operationTable.put(new LabelResourcePair(ExecutionLabels.NAME_MATCHING_PART_STAR_STAR, "selector"), seq);
		operationTable.put(new LabelResourcePair(ExecutionLabels.NAME_MATCHING_PART_STAR_TRUE, "selector"), seq);
		operationTable.put(new LabelResourcePair(ExecutionLabels.NAME_MATCHING_PART_TRUE_STAR, "selector"), seq);
		operationTable.put(new LabelResourcePair(ExecutionLabels.NAME_MATCHING_PART_TRUE_TRUE, "selector"), seq);
		operationTable.put(new LabelResourcePair(ExecutionLabels.SIGNATURE_MATCHING_PART_FALSE, "selector"), seq);
		operationTable.put(new LabelResourcePair(ExecutionLabels.SIGNATURE_MATCHING_PART_TRUE_STAR, "selector"), seq);
		operationTable.put(new LabelResourcePair(ExecutionLabels.SIGNATURE_MATCHING_PART_TRUE_TRUE, "selector"), seq);

		// error-action:
		seq = new LabelSequence();
		seq.addResourceOperation("discard");
		operationTable.put(new LabelResourcePair(ExecutionLabels.ERROR_ACTION, "args"), seq);

		seq = new LabelSequence();
		seq.addResourceOperation("error");
		seq.addResourceOperation("return");
		operationTable.put(new LabelResourcePair(ExecutionLabels.ERROR_ACTION, "message"), seq);

		// dispatch-action:
		seq = new LabelSequence();
		seq.addResourceOperation("dispatch");
		seq.addResourceOperation("return");
		operationTable.put(new LabelResourcePair(ExecutionLabels.DISPATCH_ACTION, "message"), seq);

		seq = new LabelSequence();
		seq.addResourceOperation("write");
		operationTable.put(new LabelResourcePair(ExecutionLabels.DISPATCH_ACTION, "target"), seq);

		seq = new LabelSequence();
		seq.addResourceOperation("write");
		operationTable.put(new LabelResourcePair(ExecutionLabels.DISPATCH_ACTION, "selector"), seq);

		seq = new LabelSequence();
		seq.addResourceOperation("read");
		operationTable.put(new LabelResourcePair(ExecutionLabels.DISPATCH_ACTION, "args"), seq);

		// substitution action:
		seq = new LabelSequence();
		seq.addResourceOperation("write");
		operationTable.put(new LabelResourcePair(ExecutionLabels.SUBSTITUTION_ACTION, "target"), seq);

		seq = new LabelSequence();
		seq.addResourceOperation("write");
		operationTable.put(new LabelResourcePair(ExecutionLabels.SUBSTITUTION_ACTION, "selector"), seq);
	}

	public void setCurrentResource(String resource)
	{
		this.currentResource = resource;
	}

	public LabelSequence getLabels(ExecutionTransition transition)
	{
		if (transition.getLabel().equals(ExecutionLabels.META_ACTION))
		{
			try
			{
				return getMetaLabels(transition);
			}
			catch (ModuleException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();

				return defaultSeq;
			}
		}
		else
		{
			LabelSequence seq = (LabelSequence) operationTable.get(new LabelResourcePair(transition.getLabel(),
					currentResource));

			if (seq == null)
			{
				return defaultSeq;
			}
			else
			{
				return seq;
			}
		}
	}

	private LabelSequence getMetaLabels(ExecutionTransition transition) throws ModuleException
	{
		ExecutionState metaState = transition.getStartState();

		Pattern grammer = Pattern.compile("[a-zA-Z]+\\.[a-zA-Z]+(,[a-zA-Z]+\\.[a-zA-Z]+)*");
		// DataStore datastore = DataStore.instance();
		// for (Iterator it = datastore.getAllInstancesOf(Filter.class);
		// it.hasNext();)
		// {
		// Filter filter = (Filter) it.next();
		// if (!filter.getFilterType().getType().equals(FilterType.META))
		// continue;

		Concern target = null;
		// SubstitutionPart sp = (SubstitutionPart)
		// filter.getFilterElement(0).getMatchingPattern()
		// .getSubstitutionParts().firstElement();
		DeclaredObjectReference dor = (DeclaredObjectReference) metaState.getSubstitutionMessage().getTarget().getRef();
		Object o = dor.getRef();
		if (o instanceof Internal)
		{
			target = ((Internal) o).getType().getRef();
		}
		else if (o instanceof External)
		{
			target = ((External) o).getType().getRef();
		}

		if (target != null)
		{
			PlatformRepresentation pr = target.getPlatformRepresentation();
			if (pr != null && pr instanceof Type)
			{
				Type dnt = (Type) pr;
				String selector = metaState.getSubstitutionMessage().getSelector();

				String[] params = { "Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage" };
				MethodInfo method = dnt.getMethod(selector, params);
				if (method != null)
				{
					List attributes = method.getAnnotations();
					for (int i = 0; i < attributes.size(); i++)
					{
						Annotation dna = (Annotation) attributes.get(i);
						if (dna.getType().fullName().startsWith("Composestar.")
								&& dna.getType().fullName().endsWith("Semantics"))
						{
							// System.err.println(dna.getValue());
							String spec = dna.getValue().replaceAll("\"", "");
							if (!grammer.matcher(spec).matches())
							{
								MethodInfo dnmi = (MethodInfo) dna.getTarget();
								String fullMethodName = dnmi.parent().m_fullName + '.' + dnmi.name();
								throw new ModuleException("Error in annotation semantics of method " + fullMethodName,
										"CKRET");
							}
							StringTokenizer st = new StringTokenizer(dna.getValue().replaceAll("\"", ""), ",");
							List metaOperations = new ArrayList();

							while (st.hasMoreTokens())
							{
								String token = st.nextToken();
								StringTokenizer ost = new StringTokenizer(token, ".()");
								// try
								// {
								String resource = ost.nextToken();
								String operation = ost.nextToken();
								if (ost.hasMoreTokens())
								{
									String argument = ost.nextToken();
								}
								metaOperations.add(new Operation(operation, resource));
								// }
								// catch(Exception e)
								// {
								// throw new ModuleException("CKRET","Error
								// in annotation semantics of filter " +
								// filter.getQualifiedName());
								// }
							}

							// TODO: this overrides a previously found
							// scenario for the same filter
							// which should obviously never happen...
							// this.metaSemantics.put(filter, metaOperations);
						}
					}
					Iterator reifiedMessageBehaviour = method.getReifiedMessageBehavior().iterator();
					while (reifiedMessageBehaviour.hasNext())
					{
						String refMes = (String) (reifiedMessageBehaviour.next());

						StringTokenizer st = new StringTokenizer(refMes.replaceAll("\"", ""), ",");

						List metaOperations = new ArrayList();

						while (st.hasMoreTokens())
						{
							String token = st.nextToken();
							StringTokenizer ost = new StringTokenizer(token, ".()");
							// try
							// {
							String resource = ost.nextToken();
							String operation = ost.nextToken();
							if (ost.hasMoreTokens())
							{
								String argument = ost.nextToken();
							}
							metaOperations.add(new Operation(operation, resource));
							// }
							// catch(Exception e)
							// {
							// throw new ModuleException("CKRET","Error in
							// annotation semantics of filter " +
							// filter.getQualifiedName());
							// }
						}

						// TODO: this overrides a previously found scenario
						// for the same filter
						// which should obviously never happen...
						// this.metaSemantics.put(filter, metaOperations);
					}
				}
			}
		}

		// TODO
		return defaultSeq;
		// }
	}

	private class LabelResourcePair
	{
		private String label;

		private String resource;

		public LabelResourcePair(String inlabel, String inresource)
		{
			label = inlabel;
			resource = inresource;
		}

		public boolean equals(Object obj)
		{
			if (!(obj instanceof LabelResourcePair))
			{
				return false;
			}

			LabelResourcePair pair = (LabelResourcePair) obj;

			return this.label.equals(pair.label) && this.resource.equals(pair.resource);
		}

		public int hashCode()
		{
			return label.hashCode() + resource.hashCode();
		}
	}
}
