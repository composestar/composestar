package Composestar.Core.CKRET;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import Composestar.Core.CKRET.Config.Resource;
import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.PlatformRepresentation;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Filter;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterType;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FIRE2.model.ExecutionState;
import Composestar.Core.FIRE2.model.ExecutionTransition;
import Composestar.Core.FIRE2.model.FlowNode;
import Composestar.Core.FIRE2.util.regex.LabelSequence;
import Composestar.Core.FIRE2.util.regex.Labeler;
import Composestar.Core.LAMA.Annotation;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;

public class ResourceOperationLabeler implements Labeler
{
	private static final Pattern grammer = Pattern.compile("[a-zA-Z]+\\.[a-zA-Z]+(,[a-zA-Z]+\\.[a-zA-Z]+)*");

	private Map<LabelResourcePair, LabelSequence> operationTable;

	private LabelSequence defaultSeq = new LabelSequence();

	private String currentResource;

	private Concern concern;

	// private static final String CONTINUE_ACTION = "ContinueAction";

	private static final String DISPATCH_ACTION = "DispatchAction";

	private static final String META_ACTION = "MetaAction";

	private static final String SUBSTITUTION_ACTION = "SubstitutionAction";

	private static final String ERROR_ACTION = "ErrorAction";

	public ResourceOperationLabeler()
	{
		operationTable = new HashMap<LabelResourcePair, LabelSequence>();

		// condition:
		LabelSequence seq = new LabelSequence();
		seq.addLabel("read");
		operationTable.put(new LabelResourcePair(ExecutionTransition.CONDITION_EXPRESSION_FALSE, "condition"), seq);
		operationTable.put(new LabelResourcePair(ExecutionTransition.CONDITION_EXPRESSION_TRUE, "condition"), seq);

		// matchingpart:
		seq = new LabelSequence();
		seq.addLabel("read");
		operationTable.put(new LabelResourcePair(ExecutionTransition.NAME_MATCHING_PART_ANY_FALSE, "target"), seq);
		operationTable.put(new LabelResourcePair(ExecutionTransition.NAME_MATCHING_PART_FALSE_STAR, "target"), seq);
		operationTable.put(new LabelResourcePair(ExecutionTransition.NAME_MATCHING_PART_FALSE_TRUE, "target"), seq);
		operationTable.put(new LabelResourcePair(ExecutionTransition.NAME_MATCHING_PART_STAR_STAR, "target"), seq);
		operationTable.put(new LabelResourcePair(ExecutionTransition.NAME_MATCHING_PART_STAR_TRUE, "target"), seq);
		operationTable.put(new LabelResourcePair(ExecutionTransition.NAME_MATCHING_PART_TRUE_STAR, "target"), seq);
		operationTable.put(new LabelResourcePair(ExecutionTransition.NAME_MATCHING_PART_TRUE_TRUE, "target"), seq);
		operationTable.put(new LabelResourcePair(ExecutionTransition.SIGNATURE_MATCHING_PART_FALSE, "target"), seq);
		operationTable.put(new LabelResourcePair(ExecutionTransition.SIGNATURE_MATCHING_PART_TRUE_STAR, "target"), seq);
		operationTable.put(new LabelResourcePair(ExecutionTransition.SIGNATURE_MATCHING_PART_TRUE_TRUE, "target"), seq);

		operationTable.put(new LabelResourcePair(ExecutionTransition.NAME_MATCHING_PART_ANY_FALSE, "selector"), seq);
		operationTable.put(new LabelResourcePair(ExecutionTransition.NAME_MATCHING_PART_FALSE_STAR, "selector"), seq);
		operationTable.put(new LabelResourcePair(ExecutionTransition.NAME_MATCHING_PART_FALSE_TRUE, "selector"), seq);
		operationTable.put(new LabelResourcePair(ExecutionTransition.NAME_MATCHING_PART_STAR_STAR, "selector"), seq);
		operationTable.put(new LabelResourcePair(ExecutionTransition.NAME_MATCHING_PART_STAR_TRUE, "selector"), seq);
		operationTable.put(new LabelResourcePair(ExecutionTransition.NAME_MATCHING_PART_TRUE_STAR, "selector"), seq);
		operationTable.put(new LabelResourcePair(ExecutionTransition.NAME_MATCHING_PART_TRUE_TRUE, "selector"), seq);
		operationTable.put(new LabelResourcePair(ExecutionTransition.SIGNATURE_MATCHING_PART_FALSE, "selector"), seq);
		operationTable.put(new LabelResourcePair(ExecutionTransition.SIGNATURE_MATCHING_PART_TRUE_STAR, "selector"),
				seq);
		operationTable.put(new LabelResourcePair(ExecutionTransition.SIGNATURE_MATCHING_PART_TRUE_TRUE, "selector"),
				seq);

		// error-action:
		seq = new LabelSequence();
		seq.addLabel("discard");
		operationTable.put(new LabelResourcePair(ERROR_ACTION, "args"), seq);

		seq = new LabelSequence();
		seq.addLabel("error");
		seq.addLabel("return");
		operationTable.put(new LabelResourcePair(ERROR_ACTION, "message"), seq);

		// dispatch-action:
		seq = new LabelSequence();
		seq.addLabel("dispatch");
		seq.addLabel("return");
		operationTable.put(new LabelResourcePair(DISPATCH_ACTION, "message"), seq);

		seq = new LabelSequence();
		seq.addLabel("write");
		operationTable.put(new LabelResourcePair(DISPATCH_ACTION, "target"), seq);

		seq = new LabelSequence();
		seq.addLabel("write");
		operationTable.put(new LabelResourcePair(DISPATCH_ACTION, "selector"), seq);

		seq = new LabelSequence();
		seq.addLabel("read");
		operationTable.put(new LabelResourcePair(DISPATCH_ACTION, "args"), seq);

		// substitution action:
		seq = new LabelSequence();
		seq.addLabel("write");
		operationTable.put(new LabelResourcePair(SUBSTITUTION_ACTION, "target"), seq);

		seq = new LabelSequence();
		seq.addLabel("write");
		operationTable.put(new LabelResourcePair(SUBSTITUTION_ACTION, "selector"), seq);
	}

	public void setCurrentResource(Resource resource)
	{
		currentResource = resource.getName().toLowerCase();
	}

	public void setCurrentConcern(Concern curConcern)
	{
		concern = curConcern;
	}

	/**
	 * Return the label to use as look up for the operation table
	 * 
	 * @param transition
	 * @return
	 */
	protected String getLookupLabel(ExecutionTransition transition)
	{
		// get action name
		FlowNode fnode = transition.getStartState().getFlowNode();
		if ((fnode != null) && (fnode.containsName(FlowNode.FILTER_ACTION_NODE)))
		{
			FilterType ftype = ((Filter) fnode.getRepositoryLink()).getFilterType();
			if (fnode.containsName(FlowNode.ACCEPT_CALL_ACTION_NODE))
			{
				return ftype.getAcceptCallAction().getName();
			}
			else if (fnode.containsName(FlowNode.REJECT_CALL_ACTION_NODE))
			{
				return ftype.getRejectCallAction().getName();
			}
			else if (fnode.containsName(FlowNode.ACCEPT_RETURN_ACTION_NODE))
			{
				return ftype.getAcceptReturnAction().getName();
			}
			else if (fnode.containsName(FlowNode.REJECT_RETURN_ACTION_NODE))
			{
				return ftype.getRejectReturnAction().getName();
			}
		}
		// if no filter action, return the normal label
		return transition.getLabel();
	}

	public List<String> getResourceOperations(ExecutionTransition transition)
	{
		List<String> result = new ArrayList<String>();
		String lookupLabel = getLookupLabel(transition);
		for (Entry<LabelResourcePair, LabelSequence> entry : operationTable.entrySet())
		{
			if (entry.getKey().label.equals(lookupLabel))
			{
				for (String op : entry.getValue().getLabelsEx())
				{
					result.add(entry.getKey().resource + "." + op);
				}
			}
		}
		return result;
	}

	public LabelSequence getLabels(ExecutionTransition transition)
	{
		// get action name
		String lookupLabel = getLookupLabel(transition);

		if (lookupLabel != null && lookupLabel.equals(META_ACTION))
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
			LabelSequence seq = null;
			seq = operationTable.get(new LabelResourcePair(lookupLabel, currentResource));

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

	/**
	 * Get labels for a Meta filter action.
	 * 
	 * @param transition
	 * @return
	 * @throws ModuleException
	 */
	private LabelSequence getMetaLabels(ExecutionTransition transition) throws ModuleException
	{
		ExecutionState metaState = transition.getStartState();

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
		Target tgt = metaState.getSubstitutionMessage().getTarget();
		if (tgt.equals(Target.INNER))
		{
			target = concern;
		}
		else
		{
			DeclaredObjectReference dor = (DeclaredObjectReference) tgt.getRef();
			if (dor != null)
			{
				Object o = dor.getRef();
				if (o instanceof Internal)
				{
					target = ((Internal) o).getType().getRef();
				}
				else if (o instanceof External)
				{
					target = ((External) o).getType().getRef();
				}
			}
		}

		if (target != null)
		{
			PlatformRepresentation pr = target.getPlatformRepresentation();
			if (pr != null && pr instanceof Type)
			{
				Type dnt = (Type) pr;
				String selector = metaState.getSubstitutionMessage().getSelector();

				MethodInfo method = null;
				for (Object mo : dnt.getMethods())
				{
					method = (MethodInfo) mo;
					if (method.getName().equals(selector))
					{
						String[] params = { "Composestar.RuntimeCore.FLIRT.Message.ReifiedMessage" };
						if (method.hasParameters(params))
						{
							break;
						}
						params[0] = "System.Object"; // also valid (for .net)
						if (method.hasParameters(params))
						{
							break;
						}
						params[0] = "java.lang.Object"; // also valid (for Java)
						if (method.hasParameters(params))
						{
							break;
						}
						method = null;
						break;
					}
				}

				if (method != null)
				{
					List attributes = method.getAnnotations();
					for (Object attribute : attributes)
					{
						Annotation dna = (Annotation) attribute;
						if (dna.getType().getFullName().startsWith("Composestar.")
								&& dna.getType().getFullName().endsWith("Semantics"))
						{
							// System.err.println(dna.getValue());
							String spec = dna.getValue().replaceAll("\"", "");
							if (!grammer.matcher(spec).matches())
							{
								MethodInfo dnmi = (MethodInfo) dna.getTarget();
								String fullMethodName = dnmi.parent().fullName + '.' + dnmi.getName();
								throw new ModuleException("Error in annotation semantics of method " + fullMethodName,
										CKRET.MODULE_NAME);
							}
							StringTokenizer st = new StringTokenizer(dna.getValue().replaceAll("\"", ""), ",");
							List<Operation> metaOperations = new ArrayList<Operation>();

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
								// throw new
								// ModuleException(CKRET.MODULE_NAME,"Error
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
					for (Object o1 : method.getReifiedMessageBehavior())
					{
						String refMes = (String) (o1);

						StringTokenizer st = new StringTokenizer(refMes.replaceAll("\"", ""), ",");

						List<Operation> metaOperations = new ArrayList<Operation>();

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
							// throw new
							// ModuleException(CKRET.MODULE_NAME,"Error in
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

		@Override
		public boolean equals(Object obj)
		{
			if (!(obj instanceof LabelResourcePair))
			{
				return false;
			}

			LabelResourcePair pair = (LabelResourcePair) obj;

			return label.equals(pair.label) && resource.equals(pair.resource);
		}

		@Override
		public int hashCode()
		{
			return label.hashCode() + resource.hashCode();
		}

		@Override
		public String toString()
		{
			return label + " @ " + resource;
		}
	}
}
