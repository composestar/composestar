package Composestar.Core.INLINE.CodeGen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import Composestar.Core.CpsProgramRepository.Concern;
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
			Concern crn = target.getRefToConcern();
			if (crn != null)
			{
				targetType = (Type) crn.getPlatformRepresentation();
			}
		}

		String[] params = new String[1];
		params[0] = codeGen.getJPCType(true);
		MethodInfo method = targetType.getMethod(action.getSubstitutedMessage().getSelector(), params);

		if (method == null)
		{
			// try to find a generic method with that name
			params[0] = codeGen.getBaseType();
			if (params[0] != null)
			{
				method = targetType.getMethod(action.getSubstitutedMessage().getSelector(), params);
			}
		}

		if (method == null)
		{
			// try to find a method that doesn't use a JPC
			params = new String[0];
			method = targetType.getMethod(action.getSubstitutedMessage().getSelector(), params);
		}

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
	public String generateMethodInit(CodeGenerator<String> codeGen, FilterAction action)
	{
		return null;
	}

	public Set<String> getDependencies(CodeGenerator<String> codeGen, String action)
	{
		return null;
	}

	public Set<String> getImports(CodeGenerator<String> codeGen, String action)
	{
		return null;
	}
}
