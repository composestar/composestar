package Composestar.Core.INLINE.CodeGen;

import java.util.ArrayList;
import java.util.List;

import Composestar.Core.CpsProgramRepository.CpsConcern.Filtermodules.Target;
import Composestar.Core.CpsProgramRepository.Filters.FilterActionNames;
import Composestar.Core.INLINE.lowlevel.InlinerResources;
import Composestar.Core.INLINE.model.FilterAction;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.Type;

/**
 * Generic advice action code generator. This is much like the
 * DispatchActionCodeGenerator except that it looks for the target method which
 * has an argument of the JoinPointContext type.
 * 
 * @author Michiel Hendriks
 */
public class AdviceActionCodeGen implements FilterActionCodeGenerator<String>
{
	protected InlinerResources inlinerResources;

	public AdviceActionCodeGen()
	{}

	public AdviceActionCodeGen(InlinerResources resources)
	{
		setInlinerResources(resources);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#setInlinerResources(Composestar.Core.INLINE.lowlevel.InlinerResources)
	 */
	public void setInlinerResources(InlinerResources resources)
	{
		inlinerResources = resources;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#supportedTypes()
	 */
	public String[] supportedTypes()
	{
		String[] types = { FilterActionNames.ADVICE_ACTION };
		return types;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#generate(Composestar.Core.INLINE.CodeGen.CodeGenerator,
	 *      Composestar.Core.INLINE.model.FilterAction)
	 */
	public String generate(CodeGenerator<String> codeGen, FilterAction action)
	{
		MethodInfo currentMethod = codeGen.getCurrentMethod();
		Target target = action.getSubstitutedMessage().getTarget();

		Type targetType = null;
		if (Target.INNER.equals(target.getName()) || Target.SELF.equals(target.getName()))
		{
			targetType = currentMethod.parent();
		}
		else
		{
			// TODO: resolve type
		}

		String[] params = new String[1];
		params[0] = codeGen.getJPCType(true);
		MethodInfo method = targetType.getMethod(action.getSubstitutedMessage().getSelector(), params);
		// TODO: method could be null
		List<String> args = new ArrayList<String>();
		args.add(codeGen.getJPCVariable(true));
		Object context = null;
		String prefix = "";

		// TODO: resolve target and context

		if (Target.INNER.equals(target.getName()))
		{
			prefix = codeGen.emitSetInnerCall(inlinerResources.getMethodId(method));
		}
		return prefix + codeGen.emitMethodCall(method, args, context) + ";\n";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#methodInit(Composestar.Core.INLINE.CodeGen.CodeGenerator,
	 *      Composestar.Core.INLINE.model.FilterAction)
	 */
	public String methodInit(CodeGenerator<String> codeGen, FilterAction action)
	{
		return null;
	}
}
