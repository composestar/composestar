package Composestar.Core.INLINE.CodeGen;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import Composestar.Core.CpsRepository2.PropertyNames;
import Composestar.Core.CpsRepository2.FilterElements.CanonAssignment;
import Composestar.Core.CpsRepository2.Filters.FilterActionNames;
import Composestar.Core.CpsRepository2.TypeSystem.CpsLiteral;
import Composestar.Core.CpsRepository2.TypeSystem.CpsObject;
import Composestar.Core.CpsRepository2.TypeSystem.CpsProgramElement;
import Composestar.Core.CpsRepository2.TypeSystem.CpsSelector;
import Composestar.Core.CpsRepository2.TypeSystem.CpsVariable;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsSelectorImpl;
import Composestar.Core.CpsRepository2Impl.TypeSystem.CpsSelectorMethodInfo;
import Composestar.Core.INLINE.lowlevel.InlinerResources;
import Composestar.Core.INLINE.model.FilterActionInstruction;
import Composestar.Core.LAMA.MethodInfo;
import Composestar.Core.LAMA.ProgramElement;
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
	 * @see
	 * Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#setInlinerResources
	 * (Composestar.Core.INLINE.lowlevel.InlinerResources)
	 */
	public void setInlinerResources(InlinerResources resources)
	{
		inlinerResources = resources;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#supportedTypes
	 * ()
	 */
	public String[] supportedTypes()
	{
		String[] types = { FilterActionNames.ADVICE_ACTION };
		return types;
	}

	/*
	 * (non-Javadoc)
	 * @seeComposestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#generate(
	 * Composestar.Core.INLINE.CodeGen.CodeGenerator,
	 * Composestar.Core.INLINE.model.FilterAction)
	 */
	public String generate(CodeGenerator<String> codeGen, FilterActionInstruction action)
	{
		CpsObject target = null;
		CpsSelector selector = null;

		for (CanonAssignment asgn : action.getArguments())
		{
			if (PropertyNames.TARGET.equalsIgnoreCase(asgn.getProperty().getBaseName()))
			{
				CpsVariable var = asgn.getValue();
				if (var instanceof CpsObject)
				{
					target = (CpsObject) var;
				}
				else
				{
					// TODO: error
				}
			}
			else if (PropertyNames.SELECTOR.equalsIgnoreCase(asgn.getProperty().getBaseName()))
			{
				CpsVariable var = asgn.getValue();
				if (var instanceof CpsSelector)
				{
					selector = (CpsSelector) var;
				}
				else if (var instanceof CpsLiteral)
				{
					selector = new CpsSelectorImpl(((CpsLiteral) var).getLiteralValue());
				}
				else if (var instanceof CpsProgramElement)
				{
					ProgramElement pe = ((CpsProgramElement) var).getProgramElement();
					if (pe instanceof MethodInfo)
					{
						selector = new CpsSelectorMethodInfo((MethodInfo) pe);
					}
				}
				else
				{
					// TODO: error
				}
			}
		}

		if (target == null)
		{
			target = action.getMessage().getTarget();
		}
		if (selector == null)
		{
			selector = action.getMessage().getSelector();
		}

		Type targetType = target.getTypeReference().getReference();

		if (targetType == null)
		{
			// TODO: produce error
			return null;
		}

		// TODO: this doesn't take into account of the possible MethodInfo in
		// the selector

		String[] params = new String[1];
		params[0] = codeGen.getJPCType(true);
		MethodInfo method = targetType.getMethod(selector.getName(), params);

		if (method == null)
		{
			// try to find a generic method with that name
			params[0] = codeGen.getBaseType();
			if (params[0] != null)
			{
				method = targetType.getMethod(selector.getName(), params);
			}
		}

		if (method == null)
		{
			// try to find a method that doesn't use a JPC
			params = new String[0];
			method = targetType.getMethod(selector.getName(), params);
		}

		List<String> args = new ArrayList<String>();
		args.add(codeGen.getJPCVariable(true));
		Object context = null;
		String prefix = "";

		// TODO: resolve target and context

		if (target.isInnerObject())
		{
			prefix = codeGen.emitSetInnerCall(inlinerResources.getMethodId(method));
		}
		return prefix + codeGen.emitMethodCall(method, args, context) + ";\n";
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * Composestar.Core.INLINE.CodeGen.FilterActionCodeGenerator#methodInit(
	 * Composestar.Core.INLINE.CodeGen.CodeGenerator,
	 * Composestar.Core.INLINE.model.FilterAction)
	 */
	public String generateMethodInit(CodeGenerator<String> codeGen, FilterActionInstruction action)
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
