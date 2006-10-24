/*
 * Created on 21-sep-2006
 *
 */
package Composestar.DotNET.TYM.RepositoryEmitter;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.And;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Condition;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionExpression;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.ConditionLiteral;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.External;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.False;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Internal;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Not;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Or;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.True;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ConcernReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.DeclaredObjectReference;
import Composestar.Core.CpsProgramRepository.CpsConcern.References.ExternalConcernReference;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.INLINE.lowlevel.ModelBuilder;
import Composestar.Core.INLINE.model.Block;
import Composestar.Core.INLINE.model.Branch;
import Composestar.Core.INLINE.model.Case;
import Composestar.Core.INLINE.model.ContextExpression;
import Composestar.Core.INLINE.model.ContextInstruction;
import Composestar.Core.INLINE.model.FilterAction;
import Composestar.Core.INLINE.model.Instruction;
import Composestar.Core.INLINE.model.Jump;
import Composestar.Core.INLINE.model.Label;
import Composestar.Core.INLINE.model.Switch;
import Composestar.Core.INLINE.model.Visitor;
import Composestar.Core.INLINE.model.While;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;
import Composestar.DotNET.LAMA.DotNETCallToOtherMethod;
import Composestar.DotNET.LAMA.DotNETMethodInfo;
import Composestar.DotNET.LAMA.DotNETType;
import Composestar.DotNET.MASTER.StarLightMaster;
import Composestar.Utils.Debug;
import Composestar.Utils.FileUtils;

import composestar.dotNET.tym.entities.ArrayOfAssemblyConfig;
import composestar.dotNET.tym.entities.AssemblyConfig;
import composestar.dotNET.tym.entities.ContextType;
import composestar.dotNET.tym.entities.InlineInstruction;
import composestar.dotNET.tym.entities.Reference;
import composestar.dotNET.tym.entities.WeaveCall;
import composestar.dotNET.tym.entities.WeaveMethod;
import composestar.dotNET.tym.entities.WeaveSpecification;
import composestar.dotNET.tym.entities.WeaveSpecificationDocument;
import composestar.dotNET.tym.entities.WeaveType;

public class StarLightEmitterRunner implements CTCommonModule
{
	private Vector callsToOtherMethods = new Vector();

	private Hashtable weaveSpecs = new Hashtable();

	public void run(CommonResources resources) throws ModuleException
	{

		// Emit all types to persistent repository
		try
		{
			emitTypes();
		}
		catch (NullPointerException exc)
		{
			exc.printStackTrace();
			throw new ModuleException("NullPointerException in emitter", "EMITTER");
		}
	}

	private void emitTypes() throws ModuleException
	{
		DataStore dataStore = DataStore.instance();

		WeaveSpecification weaveSpec;

		Iterator concernIterator = dataStore.getAllInstancesOf(Concern.class);
		while (concernIterator.hasNext())
		{
			Concern concern = (Concern) concernIterator.next();
			DotNETType type = (DotNETType) concern.getPlatformRepresentation();

			if (type == null) continue;

			if (concern.getDynObject("superImpInfo") != null)
			{
				// get weavespec:
				weaveSpec = getWeaveSpec(type.getFromDLL());

				// get filtermodules:
				FilterModuleOrder order = (FilterModuleOrder) concern.getDynObject("SingleOrder");

				WeaveType weaveType = weaveSpec.getWeaveTypes().addNewWeaveType();
				weaveType.addNewConditions();
				weaveType.addNewExternals();
				weaveType.addNewInternals();
				weaveType.addNewMethods();
				weaveType.setName(type.fullName());

				Iterator filterModules = order.orderAsList().iterator();
				while (filterModules.hasNext())
				{
					String ref = (String) filterModules.next();
					FilterModule filterModule = (FilterModule) dataStore.getObjectByID(ref);

					// internals:
					Iterator internals = filterModule.getInternalIterator();

					while (internals.hasNext())
					{
						// store internal:
						Internal internal = (Internal) internals.next();
						composestar.dotNET.tym.entities.Internal storedInternal = weaveType.getInternals()
								.addNewInternal();

						// name:
						storedInternal.setName(internal.getName());

						// namespace:
						StringBuffer namespace = new StringBuffer();
						Enumeration packages = internal.getType().getPackage().elements();
						while (packages.hasMoreElements())
						{
							namespace.append(packages.nextElement());
							if (packages.hasMoreElements())
							{
								namespace.append(".");
							}
						}
						storedInternal.setNameSpace(namespace.toString());

						// typename:
						storedInternal.setType(internal.getType().getName());

						// assembly:
						DotNETType type2 = (DotNETType) internal.getType().getRef().getPlatformRepresentation();
						storedInternal.setAssembly(type2.fromDLL);
					}

					// externals:
					Iterator externals = filterModule.getExternalIterator();

					while (externals.hasNext())
					{
						// store external
						External external = (External) externals.next();
						composestar.dotNET.tym.entities.External storedExternal = weaveType.getExternals()
								.addNewExternal();

						// name:
						storedExternal.setName(external.getName());

						// reference:
						ExternalConcernReference reference = external.getShortinit();
						DotNETType refType = (DotNETType) reference.getRef().getPlatformRepresentation();
						Reference storedReference = createReference(type, refType.getFromDLL(), reference.getPackage(),
								reference.getName(), reference.getInitSelector());
						storedExternal.setReference(storedReference);

						// type:
						StringBuffer packages = new StringBuffer();
						Enumeration enumer = external.getType().getPackage().elements();
						while (enumer.hasMoreElements())
						{
							packages.append(enumer.nextElement());
							packages.append('.');
						}
						storedExternal.setType(packages.toString() + external.getType().getName());

						// assembly:
						DotNETType type2 = (DotNETType) external.getType().getRef().getPlatformRepresentation();
						storedExternal.setAssembly(type2.getFromDLL());
					}

					// conditions:
					Iterator conditions = filterModule.getConditionIterator();
					while (conditions.hasNext())
					{

						// store condition:
						Condition condition = (Condition) conditions.next();
						composestar.dotNET.tym.entities.Condition storedCondition = weaveType.getConditions()
								.addNewCondition();

						// name:
						storedCondition.setName(condition.getName());

						// reference:
						DotNETType refType;
						Composestar.Core.CpsProgramRepository.CpsConcern.References.Reference condRef = condition
								.getShortref();
						if (condRef instanceof DeclaredObjectReference)
						{
							DeclaredObjectReference dor = (DeclaredObjectReference) condRef;
							if (dor.getName().equals("inner") || dor.getName().equals("self"))
							{
								refType = type;
							}
							else
							{
								refType = (DotNETType) dor.getRef().getType().getRef().getPlatformRepresentation();
							}
						}
						else if (condRef instanceof ConcernReference)
						{
							ConcernReference cor = (ConcernReference) condRef;
							refType = (DotNETType) cor.getRef().getPlatformRepresentation();
						}
						else
						{
							throw new RuntimeException("Unknown reference type");
						}

						Reference reference = createReference(type, refType.getFromDLL(), condition.getShortref()
								.getPackage(), condition.getShortref().getName(), (String) condition
								.getDynObject("selector"));

						storedCondition.setReference(reference);
					}
				}

				// emit methods:
				emitMethods(type, weaveType);
			}
		}

		// write specfiles:
		ArrayOfAssemblyConfig assemblies = StarLightMaster.getConfigContainer().getAssemblies();
		for (int i = 0; i < assemblies.sizeOfAssemblyConfigArray(); i++)
		{
			AssemblyConfig config = assemblies.getAssemblyConfigArray(i);
			if (weaveSpecs.containsKey(config.getName()))
			{
				weaveSpec = (WeaveSpecification) weaveSpecs.get(config.getName());
				WeaveSpecificationDocument doc = WeaveSpecificationDocument.Factory.newInstance();
				doc.setWeaveSpecification(weaveSpec);

				String filename = FileUtils.removeExtension(config.getSerializedFilename());
				filename = filename + "_weavespec.xml";
				System.out.println(filename);
				File file = new File(filename);
				try
				{
					doc.save(file);
				}
				catch (IOException e)
				{
					throw new ModuleException("IOException while writing weavespecfile " + filename, "EMITTER");
				}
				config.setWeaveSpecificationFile(filename);
			}
		}
	}

	/**
	 * Gets the weavespecification corresponding with a given dll
	 * 
	 * @param dllName The dll for which the weavespec needs to be returned
	 * @return The weavespec for the given dll. If not existing, a new one is
	 *         created.
	 */
	private WeaveSpecification getWeaveSpec(String dllName)
	{
		if (weaveSpecs.containsKey(dllName))
		{
			return (WeaveSpecification) weaveSpecs.get(dllName);
		}
		else
		{
			WeaveSpecification weaveSpec = WeaveSpecification.Factory.newInstance();
			weaveSpec.addNewWeaveTypes();
			weaveSpec.setAssemblyName(dllName);
			weaveSpecs.put(dllName, weaveSpec);
			return weaveSpec;
		}
	}

	/**
	 * Creates the reference used by the external and condition to retrieve it's
	 * instance/value
	 * 
	 * @param type
	 * @param pack
	 * @param target
	 * @param selector
	 * @return
	 */
	private Reference createReference(Type type, String assembly, Vector pack, String target, String selector)
	{
		Reference storedRef = Reference.Factory.newInstance();

		// namespace:
		StringBuffer namespace = new StringBuffer();
		Enumeration packages = pack.elements();
		while (packages.hasMoreElements())
		{
			namespace.append(packages.nextElement());
			if (packages.hasMoreElements())
			{
				namespace.append('.');
			}
		}
		storedRef.setNameSpace(namespace.toString());

		// selector:
		storedRef.setSelector(selector);

		// target:
		storedRef.setTarget(target);

		// innercall context:
		if (target.equals("inner"))
		{
			MethodInfo methodInfo = type.getMethod(selector, new String[0]);
			if (methodInfo != null)
			{
				storedRef.setInnerCallContext(ModelBuilder.getInnerCallContext(methodInfo));
			}
			else
			{
				storedRef.setInnerCallContext(-1);
			}
		}
		else
		{
			storedRef.setInnerCallContext(-1);
		}

		// assembly:
		storedRef.setAssembly(assembly);

		return storedRef;
	}

	private void emitMethods(Type type, WeaveType weaveType) throws ModuleException
	{
		boolean hasFilters;
		Vector methods = new Vector();

		Debug.out(Debug.MODE_DEBUG, "Emitter", "Emit type: " + type.fullName());

		Iterator methodIter = type.getMethods().iterator();

		while (methodIter.hasNext())
		{
			hasFilters = false;
			DotNETMethodInfo method = (DotNETMethodInfo) methodIter.next();
			WeaveMethod weaveMethod = WeaveMethod.Factory.newInstance();
			weaveMethod.addNewWeaveCalls();
			weaveMethod.setSignature(method.getMethodElement().getSignature());

			// get the block containing the filterinstructions:
			Block filterInstructions = ModelBuilder.getInputFilterCode(method);

			if (filterInstructions != null)
			{
				hasFilters = true;

				// add inputfilter code:
				weaveMethod.setInputFilter(translateInstruction(filterInstructions));
			}

			// emit calls:
			hasFilters = hasFilters || emitCalls(method, weaveMethod);

			// add method if it has filters inlined:
			if (hasFilters)
			{
				methods.add(weaveMethod);
			}
		}

		// add inlined methods to type:
		weaveType.getMethods().setWeaveMethodArray((WeaveMethod[]) methods.toArray(new WeaveMethod[0]));
	}

	private boolean emitCalls(MethodInfo method, WeaveMethod weaveMethod)
	{
		boolean hasFilters = false;
		Iterator calls = method.getCallsToOtherMethods().iterator();

		while (calls.hasNext())
		{
			DotNETCallToOtherMethod call = (DotNETCallToOtherMethod) calls.next();

			// add outputfilter code:
			Block code = ModelBuilder.getOutputFilterCode(call);
			if (code != null)
			{
				WeaveCall weaveCall = weaveMethod.getWeaveCalls().addNewWeaveCall();

				// set methodname:
				weaveCall.setMethodName(call.getMethodName());

				// set filtercode
				weaveCall.setOutputFilter(translateInstruction(code));

				Debug.out(Debug.MODE_DEBUG, "Emitter", "Storing call" + weaveCall.toString());

				// set hasfilters to true to indicate that the method has
				// filters
				hasFilters = true;
			}
		}

		// return hasfilters
		return hasFilters;
	}

	private InlineInstruction translateInstruction(Block block)
	{
		InstructionTranslater translater = InstructionTranslater.getInstance();
		return (InlineInstruction) block.accept(translater);
	}

	private static class InstructionTranslater implements Visitor
	{
		private final static InstructionTranslater INSTANCE = new InstructionTranslater();

		private Hashtable fullNameMap = new Hashtable();

		private InstructionTranslater()
		{
			Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction filterAction;

			DataStore dataStore = DataStore.instance();
			Iterator iter = dataStore
					.getAllInstancesOf(Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction.class);
			while (iter.hasNext())
			{
				filterAction = (Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterAction) iter
						.next();
				fullNameMap.put(filterAction.getName(), filterAction.getFullName());
			}
		}

		public static InstructionTranslater getInstance()
		{
			return INSTANCE;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitBlock(Composestar.Core.INLINE.model.Block)
		 */
		public Object visitBlock(Block block)
		{
			composestar.dotNET.tym.entities.Block weaveBlock = composestar.dotNET.tym.entities.Block.Factory
					.newInstance();
			weaveBlock.addNewInstructions();

			setLabel(block, weaveBlock);

			// create contained instructions:
			Vector inlineInstructions = new Vector();
			Enumeration instructions = block.getInstructions();
			while (instructions.hasMoreElements())
			{
				Instruction instruction = (Instruction) instructions.nextElement();

				InlineInstruction inlineInstruction = (InlineInstruction) instruction.accept(this);
				inlineInstructions.add(inlineInstruction);
			}

			// add contained instructions to the weaveBlock:
			weaveBlock.getInstructions().setInstructionArray(
					(InlineInstruction[]) inlineInstructions.toArray(new InlineInstruction[0]));

			return weaveBlock;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitBranch(Composestar.Core.INLINE.model.Branch)
		 */
		public Object visitBranch(Branch branch)
		{
			composestar.dotNET.tym.entities.Branch weaveBranch = composestar.dotNET.tym.entities.Branch.Factory
					.newInstance();

			setLabel(branch, weaveBranch);

			weaveBranch.setCondition(translateConditionExpression(branch.getConditionExpression()));

			weaveBranch.setTrueBlock((composestar.dotNET.tym.entities.Block) branch.getTrueBlock().accept(this));

			weaveBranch.setFalseBlock((composestar.dotNET.tym.entities.Block) branch.getFalseBlock().accept(this));

			return weaveBranch;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitContextInstruction(Composestar.Core.INLINE.model.ContextInstruction)
		 */
		public Object visitContextInstruction(ContextInstruction contextInstruction)
		{
			composestar.dotNET.tym.entities.ContextInstruction weaveInstr = composestar.dotNET.tym.entities.ContextInstruction.Factory
					.newInstance();

			setLabel(contextInstruction, weaveInstr);

			switch (contextInstruction.getType())
			{
				case ContextInstruction.CHECK_INNER_CALL:
					weaveInstr.setType(ContextType.CHECK_INNER_CALL);
					break;
				case ContextInstruction.CREATE_ACTION_STORE:
					weaveInstr.setType(ContextType.CREATE_ACTION_STORE);
					break;
				case ContextInstruction.CREATE_JOIN_POINT_CONTEXT:
					weaveInstr.setType(ContextType.CREATE_JPC);
					break;
				case ContextInstruction.REMOVED:
					weaveInstr.setType(ContextType.REMOVED);
					break;
				case ContextInstruction.RESET_INNER_CALL:
					weaveInstr.setType(ContextType.RESET_INNER_CALL);
					break;
				case ContextInstruction.RESTORE_JOIN_POINT_CONTEXT:
					weaveInstr.setType(ContextType.RESTORE_JPC);
					break;
				case ContextInstruction.RETURN_ACTION:
					weaveInstr.setType(ContextType.RETURN_ACTION);
					break;
				case ContextInstruction.SET_INNER_CALL:
					weaveInstr.setType(ContextType.SET_INNER_CALL);
					break;
				case ContextInstruction.STORE_ACTION:
					weaveInstr.setType(ContextType.STORE_ACTION);
					break;
				default:
					throw new RuntimeException("Unknown contextinstruction");
			}

			weaveInstr.setCode(contextInstruction.getCode());

			if (contextInstruction.getInstruction() != null)
			{
				weaveInstr.setInnerBlock((composestar.dotNET.tym.entities.Block) contextInstruction.getInstruction()
						.accept(this));
			}

			return weaveInstr;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitFilterAction(Composestar.Core.INLINE.model.FilterAction)
		 */
		public Object visitFilterAction(FilterAction filterAction)
		{
			composestar.dotNET.tym.entities.FilterAction weaveAction = composestar.dotNET.tym.entities.FilterAction.Factory
					.newInstance();

			setLabel(filterAction, weaveAction);

			weaveAction.setFullName((String) fullNameMap.get(filterAction.getType()));

			weaveAction.setSelector(filterAction.getMessage().getSelector());
			weaveAction.setTarget(filterAction.getMessage().getTarget().getName());

			weaveAction.setSubstitutionSelector(filterAction.getSubstitutedMessage().getSelector());
			weaveAction.setSubstitutionTarget(filterAction.getSubstitutedMessage().getTarget().getName());

			return weaveAction;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitJump(Composestar.Core.INLINE.model.Jump)
		 */
		public Object visitJump(Jump jump)
		{
			composestar.dotNET.tym.entities.Jump weaveJump = composestar.dotNET.tym.entities.Jump.Factory.newInstance();

			setLabel(jump, weaveJump);

			weaveJump.setTarget(jump.getTarget().getId());

			return weaveJump;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitCase(Composestar.Core.INLINE.model.Case)
		 */
		public Object visitCase(Case caseInstruction)
		{
			composestar.dotNET.tym.entities.Case weaveCase = composestar.dotNET.tym.entities.Case.Factory.newInstance();

			setLabel(caseInstruction, weaveCase);

			weaveCase.setCheckConstant(caseInstruction.getCheckConstant());

			weaveCase.setInstructions((composestar.dotNET.tym.entities.Block) caseInstruction.getInstructions().accept(
					this));

			return weaveCase;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitSwitch(Composestar.Core.INLINE.model.Switch)
		 */
		public Object visitSwitch(Switch switchInstruction)
		{
			composestar.dotNET.tym.entities.Switch weaveSwitch = composestar.dotNET.tym.entities.Switch.Factory
					.newInstance();
			weaveSwitch.addNewCases();

			setLabel(switchInstruction, weaveSwitch);

			weaveSwitch.setExpression(getContextExpression(switchInstruction.getExpression()));

			Vector weaveCases = new Vector();
			Case[] cases = switchInstruction.getCases();
			for (int i = 0; i < cases.length; i++)
			{
				weaveCases.add(cases[i].accept(this));
			}
			weaveSwitch.getCases().setCaseArray(
					(composestar.dotNET.tym.entities.Case[]) weaveCases
							.toArray(new composestar.dotNET.tym.entities.Case[0]));

			return weaveSwitch;
		}

		/**
		 * @see Composestar.Core.INLINE.model.Visitor#visitWhile(Composestar.Core.INLINE.model.While)
		 */
		public Object visitWhile(While whileInstruction)
		{
			composestar.dotNET.tym.entities.While weaveWhile = composestar.dotNET.tym.entities.While.Factory
					.newInstance();

			weaveWhile.setExpression(getContextExpression(whileInstruction.getExpression()));

			weaveWhile.setInstructions((composestar.dotNET.tym.entities.Block) whileInstruction.getInstructions()
					.accept(this));

			return weaveWhile;
		}

		private composestar.dotNET.tym.entities.ConditionExpression translateConditionExpression(
				ConditionExpression expression)
		{
			if (expression instanceof And)
			{
				And and = (And) expression;

				composestar.dotNET.tym.entities.And weaveAnd = composestar.dotNET.tym.entities.And.Factory
						.newInstance();

				weaveAnd.setLeft(translateConditionExpression(and.getLeft()));
				weaveAnd.setRight(translateConditionExpression(and.getRight()));

				return weaveAnd;
			}
			else if (expression instanceof Or)
			{
				Or or = (Or) expression;

				composestar.dotNET.tym.entities.Or weaveOr = composestar.dotNET.tym.entities.Or.Factory.newInstance();

				weaveOr.setLeft(translateConditionExpression(or.getLeft()));
				weaveOr.setRight(translateConditionExpression(or.getRight()));

				return weaveOr;
			}
			else if (expression instanceof Not)
			{
				Not not = (Not) expression;

				composestar.dotNET.tym.entities.Not weaveNot = composestar.dotNET.tym.entities.Not.Factory
						.newInstance();

				weaveNot.setOperand(translateConditionExpression(not.getOperand()));

				return weaveNot;
			}
			else if (expression instanceof ConditionLiteral)
			{
				ConditionLiteral literal = (ConditionLiteral) expression;

				composestar.dotNET.tym.entities.ConditionLiteral weaveLiteral = composestar.dotNET.tym.entities.ConditionLiteral.Factory
						.newInstance();

				weaveLiteral.setName(literal.getCondition().getRef().getName());

				return weaveLiteral;
			}
			else if (expression instanceof True)
			{
				return composestar.dotNET.tym.entities.True.Factory.newInstance();
			}
			else if (expression instanceof False)
			{
				return composestar.dotNET.tym.entities.False.Factory.newInstance();
			}
			else
			{
				throw new RuntimeException("Unknown ConditionExpression");
			}
		}

		private composestar.dotNET.tym.entities.ContextExpression.Enum getContextExpression(ContextExpression expression)
		{
			composestar.dotNET.tym.entities.ContextExpression weaveExpression = composestar.dotNET.tym.entities.ContextExpression.Factory
					.newInstance();

			switch (expression.getType())
			{
				case ContextExpression.HAS_MORE_ACTIONS:
					return composestar.dotNET.tym.entities.ContextExpression.HAS_MORE_ACTIONS;
				case ContextExpression.RETRIEVE_ACTION:
					return composestar.dotNET.tym.entities.ContextExpression.RETRIEVE_ACTION;
				default:
					throw new RuntimeException("Unknown contextexpression");
			}
		}

		private void setLabel(Instruction instruction, InlineInstruction inlineInstruction)
		{
			Label label = instruction.getLabel();

			if (label == null)
			{
				return;
			}

			inlineInstruction.setLabel(label.getId());
		}
	}

}
