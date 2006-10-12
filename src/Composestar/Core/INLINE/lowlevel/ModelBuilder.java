/*
 * Created on 6-sep-2006
 *
 */
package Composestar.Core.INLINE.lowlevel;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import Composestar.Core.CpsProgramRepository.Concern;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.FilterModule;
import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.Exception.ModuleException;
import Composestar.Core.FILTH.FilterModuleOrder;
import Composestar.Core.FIRE2.model.ExecutionModel;
import Composestar.Core.FIRE2.model.FireModel;
import Composestar.Core.FIRE2.model.Message;
import Composestar.Core.INLINE.model.Block;
import Composestar.Core.INLINE.model.Branch;
import Composestar.Core.INLINE.model.ContextInstruction;
import Composestar.Core.LAMA.CallToOtherMethod;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;
import Composestar.Core.Master.CTCommonModule;
import Composestar.Core.Master.CommonResources;
import Composestar.Core.RepositoryImplementation.DataStore;

public class ModelBuilder implements CTCommonModule
{
	private LowLevelInliner inputFilterInliner;

	private ModelBuilderStrategy inputFilterBuilderStrategy;

	private LowLevelInliner outputFilterInliner;

	private ModelBuilderStrategy outputFilterBuilderStrategy;

	// private Concern currentConcern;
	private HashSet inlinedMethodSet;

	private FilterModule[] modules;

	private FireModel currentFireModelIF;

	private FireModel currentFireModelOF;

	private DataStore dataStore;

	private Vector innerCallCheckTasks = new Vector();

	private static Hashtable inputFilterCode;

	private static Hashtable outputFilterCode;

	private String currentSelector;

	public ModelBuilder()
	{
		this.inputFilterBuilderStrategy = new ModelBuilderStrategy(this, ModelBuilderStrategy.INPUT_FILTERS);
		this.inputFilterInliner = new LowLevelInliner(inputFilterBuilderStrategy);
		this.outputFilterBuilderStrategy = new ModelBuilderStrategy(this, ModelBuilderStrategy.OUTPUT_FILTERS);
		this.outputFilterInliner = new LowLevelInliner(outputFilterBuilderStrategy);
	}

	/**
	 * @see Composestar.Core.Master.CTCommonModule#run(Composestar.Core.Master.CommonResources)
	 */
	public void run(CommonResources resources) throws ModuleException
	{
		dataStore = DataStore.instance();
		startInliner();

	}

	public static Block getInputFilterCode(MethodInfo method)
	{
		return (Block) inputFilterCode.get(method);
	}

	public static Block getOutputFilterCode(CallToOtherMethod call)
	{
		return (Block) outputFilterCode.get(call);
	}

	private void startInliner()
	{
		initialize();
		processInfo();
		storeInfo();
	}

	private void initialize()
	{
		inputFilterCode = new Hashtable();
		outputFilterCode = new Hashtable();
	}

	private void processInfo()
	{
		Iterator concerns = dataStore.getAllInstancesOf(Concern.class);

		while (concerns.hasNext())
		{
			Concern concern = (Concern) concerns.next();
			processConcern(concern);
		}
	}

	private void processConcern(Concern concern)
	{
		// get inputFiltermodules:
		if (concern.getDynObject("superImpInfo") == null)
		{
			return;
		}

		// initialize:
		innerCallCheckTasks = new Vector();
		inlinedMethodSet = new HashSet();

		// get filtermodules:
		FilterModuleOrder filterModules = (FilterModuleOrder) concern.getDynObject("SingleOrder");

		currentFireModelIF = new FireModel(concern, filterModules, true);
		currentFireModelOF = new FireModel(concern, filterModules, false);

		List order = filterModules.orderAsList();
		modules = new FilterModule[order.size()];
		for (int i = 0; i < order.size(); i++)
		{
			String ref = (String) order.get(i);

			modules[i] = (FilterModule) DataStore.instance().getObjectByID(ref);
		}

		// iterate methods:
		Type type = (Type) concern.getPlatformRepresentation();
		List list = type.getMethods();
		Iterator iter = list.iterator();
		while (iter.hasNext())
		{
			MethodInfo method = (MethodInfo) iter.next();

			processMethod(method);
		}

		// do innercall checks:
		for (int i = 0; i < innerCallCheckTasks.size(); i++)
		{
			ContextInstruction instruction = (ContextInstruction) innerCallCheckTasks.elementAt(i);
			if (!inlinedMethodSet.contains(new Integer(instruction.getCode())))
			{
				instruction.setType(ContextInstruction.REMOVED);
			}
		}
	}

	private void processMethod(MethodInfo methodInfo)
	{
		// set current selector:
		this.currentSelector = methodInfo.name();

		// create executionmodel:
		ExecutionModel execModel = currentFireModelIF.getExecutionModel(methodInfo, FireModel.STRICT_SIGNATURE_CHECK);

		// create inlineModel:
		inputFilterInliner.inline(execModel, modules, methodInfo);

		// store inlineModel in methodElement:
		Block inlineBlock = inputFilterBuilderStrategy.getInlineBlock();
		if (inlineBlock != null)
		{
			inputFilterCode.put(methodInfo, inlineBlock);
			inlinedMethodSet.add(new Integer(inputFilterBuilderStrategy.getMethodId(methodInfo)));
		}

		// process calls:
		HashSet calls = methodInfo.getCallsToOtherMethods();
		Iterator iter = calls.iterator();
		while (iter.hasNext())
		{
			CallToOtherMethod call = (CallToOtherMethod) iter.next();
			processCall(call);
		}

	}

	private void processCall(CallToOtherMethod call)
	{
		// set current selector:
		this.currentSelector = call.getMethodName();

		Block callBlock = new Block();
		Target target;
		Branch currentBranch = null;

		// retrieve target methodinfo:
		MethodInfo methodInfo = call.getCalledMethod();

		// create executionmodel for distinguishable targets and the
		// undistinguishable target

		// Distinguishable:
		// TODO first do only undistinguishable. The implementation for doing
		// also distinguishable
		// needs to be worked out further.
		// HashSet distTargets = currentFireModel.getDistinguishableTargets();
		// Iterator iter = distTargets.iterator();
		// while( iter.hasNext() ){
		// target = (Target) iter.next();

		// //get callblock:
		// Block block = createCallBlock( target, methodInfo );

		// //create branch to check target:
		// Branch branch = new Branch( new TargetSelectExpression(
		// target.getName() ) );
		// branch.setTrueBlock( block );

		// block = new Block();
		// block.addInstruction( branch );

		// if ( currentBranch == null ){
		// //add first branch:
		// currentBranch = branch;
		// callBlock.addInstruction( currentBranch );
		// }
		// else{
		// //add new branch to previous branch:
		// currentBranch.setFalseBlock( block );
		// currentBranch = branch;
		// }
		// }

		// Undistinguishable:
		Block block = createCallBlock(call, Message.UNDISTINGUISHABLE_TARGET, methodInfo);
		if (currentBranch == null)
		{
			// no distinguishable targets.
			// set callblock to the undistinguishable block:
			callBlock = block;
		}
		else
		{
			currentBranch.setFalseBlock(block);
		}

		// add callBlock to call
		if (callBlock != null)
		{
			outputFilterCode.put(call, callBlock);
		}
	}

	private Block createCallBlock(CallToOtherMethod call, Target target, MethodInfo methodInfo)
	{

		// create executionModel:
		ExecutionModel execModel;
		if (methodInfo != null)
		{
			execModel = currentFireModelOF.getExecutionModel(methodInfo, FireModel.STRICT_SIGNATURE_CHECK);
		}
		else
		{
			execModel = currentFireModelOF.getExecutionModel(call.getMethodName());
		}

		// create inlineModel:
		outputFilterInliner.inline(execModel, modules, methodInfo);

		// store inlineModel in callStructure:
		Block callBlock = outputFilterBuilderStrategy.getInlineBlock();

		return callBlock;
	}

	private void storeInfo()
	{
	// TODO
	}

	protected void addInnerCallCheckTask(ContextInstruction innerCallAction)
	{
		innerCallCheckTasks.add(innerCallAction);
	}

	protected String getCurrentSelector()
	{
		return this.currentSelector;
	}
}
